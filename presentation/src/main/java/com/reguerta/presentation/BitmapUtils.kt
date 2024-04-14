package com.reguerta.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation
 * Created By Manuel Lopera on 24/2/24 at 11:40
 * All rights reserved 2024
 */

fun checkAllStringAreNotEmpty(vararg inputValues: String) = inputValues.all { it.isNotEmpty() }

/**
 * The rotationDegrees parameter is the rotation in degrees clockwise from the original orientation.
 */
fun Bitmap.rotateBitmap(rotationDegrees: Int): Bitmap {
    val matrix = Matrix().apply {
        postRotate(-rotationDegrees.toFloat())
        postScale(-1f, -1f)
    }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun uriToBitmap(context: Context, selectedFileUri: Uri): Bitmap? {
    try {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(selectedFileUri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun Bitmap.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    return baos.toByteArray()
}


suspend fun resizeAndCropImage(bitmap: Bitmap, width: Int = 300, height: Int = 300): ByteArray = withContext(Dispatchers.IO) {
    val scaleFactor = Math.min(
        bitmap.width / width.toFloat(),
        bitmap.height / height.toFloat()
    )
    val scaledWidth = (width * scaleFactor).toInt()
    val scaledHeight = (height * scaleFactor).toInt()
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
    val x = (scaledBitmap.width - width) / 2
    val y = (scaledBitmap.height - height) / 2

    val croppedBitmap = Bitmap.createBitmap(scaledBitmap, x, y, width, height)

    val baos = ByteArrayOutputStream()
    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

    baos.toByteArray()
}