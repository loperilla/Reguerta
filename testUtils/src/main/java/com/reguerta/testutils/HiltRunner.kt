package com.reguerta.testutils

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/*****
 * Project: Reguerta
 * From: com.reguerta.testutils
 * Created By Manuel Lopera on 30/1/24 at 10:35
 * All rights reserved 2024
 */
class HiltRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}