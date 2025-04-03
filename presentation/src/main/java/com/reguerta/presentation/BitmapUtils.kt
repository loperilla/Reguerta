package com.reguerta.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.IOException
import kotlin.math.max
import kotlin.math.min
import androidx.core.graphics.scale

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation
 * Created By Manuel Lopera on 24/2/24 at 11:40
 * All rights reserved 2024
 */

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

suspend fun resizeAndCropImage(bitmap: Bitmap, size: Int = 300): ByteArray = withContext(Dispatchers.IO) {
    val aspectRatio = max(bitmap.width, bitmap.height).toFloat() / min(bitmap.width, bitmap.height)
    val resizedWidth: Int
    val resizedHeight: Int
    if (bitmap.width < bitmap.height) {
        resizedWidth = size
        resizedHeight = (size * aspectRatio).toInt()
    } else {
        resizedHeight = size
        resizedWidth = (size * aspectRatio).toInt()
    }
    val resizedBitmap = bitmap.scale(resizedWidth, resizedHeight)
    val cropStartX = (resizedWidth - size) / 2
    val cropStartY = (resizedHeight - size) / 2

    val croppedBitmap = Bitmap.createBitmap(resizedBitmap, cropStartX, cropStartY, size, size)
    croppedBitmap.toByteArray()
}
