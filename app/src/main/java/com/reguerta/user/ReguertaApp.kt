package com.reguerta.user

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/*****
 * Project: Reguerta
 * From: com.reguerta.user
 * Created By Manuel Lopera on 23/1/24 at 17:03
 * All rights reserved 2024
 */

@HiltAndroidApp
class ReguertaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(
                object : Timber.DebugTree() {
                    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                        super.log(priority, "global_tag_$tag", message, t)
                    }
                }
            )
        }
    }
}