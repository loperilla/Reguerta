package com.reguerta.user

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import com.reguerta.data.firebase.firestore.FirestoreEnvironment
import com.reguerta.data.firebase.firestore.FirestoreManager
import com.reguerta.presentation.sync.ForegroundSyncManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/*****
 * Project: Reguerta
 * From: com.reguerta.user
 * Created By Manuel Lopera on 23/1/24 at 17:03
 * All rights reserved 2024
 */

@HiltAndroidApp
class ReguertaApp : Application(), DefaultLifecycleObserver {
    override fun onCreate() {
        super<Application>.onCreate()
        FirebaseApp.initializeApp(this)

        val environment = if (BuildConfig.DEBUG) FirestoreEnvironment.DEVELOP else FirestoreEnvironment.PRODUCTION
        FirestoreManager.setEnvironment(environment)
        FirestoreManager.configureFirestore()

        if (BuildConfig.DEBUG) {
            Timber.plant(
                object : Timber.DebugTree() {
                    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                        super.log(priority, "global_tag_$tag", message, t)
                    }
                }
            )
        }
        Timber.tag("ReguertaApp").d("Entorno seleccionado: ${environment.path}")

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        Timber.tag("LifecycleDebug").d("App ha vuelto al primer plano")
        ForegroundSyncManager.requestSyncFromAppLifecycle()
    }
}