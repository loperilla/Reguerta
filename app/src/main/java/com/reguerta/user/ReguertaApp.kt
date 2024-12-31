package com.reguerta.user

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.reguerta.data.BuildConfig
import com.reguerta.data.firebase.firestore.FirestoreEnvironment
import com.reguerta.data.firebase.firestore.FirestoreManager
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
        val memoryCacheSettings = MemoryCacheSettings.newBuilder().build()
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(memoryCacheSettings)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = firestoreSettings

        if (BuildConfig.DEBUG) {
            Timber.plant(
                object : Timber.DebugTree() {
                    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                        super.log(priority, "global_tag_$tag", message, t)
                    }
                }
            )
        }
        FirestoreManager.setEnvironment(
            if (BuildConfig.DEBUG) FirestoreEnvironment.DEVELOP else FirestoreEnvironment.PRODUCTION
        )
    }
}

