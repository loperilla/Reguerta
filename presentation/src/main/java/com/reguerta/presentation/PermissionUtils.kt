package com.reguerta.presentation

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation
 * Created By Manuel Lopera on 3/3/24 at 14:04
 * All rights reserved 2024
 */

fun getStoragePermissionBySdk(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(READ_MEDIA_IMAGES)
    } else {
        arrayOf(READ_EXTERNAL_STORAGE)
    }
}

fun checkStoragePermission(context: Context): Boolean {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            ContextCompat.checkSelfPermission(context, READ_MEDIA_VISUAL_USER_SELECTED) == PermissionChecker.PERMISSION_GRANTED
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            ContextCompat.checkSelfPermission(context, READ_MEDIA_IMAGES) == PermissionChecker.PERMISSION_GRANTED
        }
        else -> {
            ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED
        }
    }
}

fun checkRationalPermission(context: Context, permission: String): Boolean {
    return shouldShowRequestPermissionRationale(context as Activity, permission)
}
