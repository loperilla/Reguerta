package com.reguerta.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.reguerta.data.AuthState
import com.reguerta.localdata.datastore.ReguertaDataStore
import kotlinx.coroutines.tasks.await

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase
 * Created By Manuel Lopera on 24/1/24 at 12:04
 * All rights reserved 2024
 */
class AuthServiceImpl(
    private val firebaseAuth: FirebaseAuth,
    private val dataStore: ReguertaDataStore
) : AuthService {
    private val currentUser
        get() = firebaseAuth.currentUser

    override val isAuthenticated
        get() = currentUser != null

    override suspend fun signOut() {
        firebaseAuth.signOut()
        dataStore.saveUID("")
    }

    override suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthState {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            dataStore.saveUID(currentUser?.uid.orEmpty())
            AuthState.LoggedIn
        } catch (ex: Exception) {
            Log.e("AuthService", ex.message.orEmpty())
            AuthState.Error(ex.message ?: "Error")
        }
    }

    override suspend fun refreshUser(): AuthState {
        return try {
            currentUser?.reload()?.await()
            if (isAuthenticated) {
                AuthState.LoggedIn
            } else {
                AuthState.Error("Error")
            }
        } catch (ex: Exception) {
            AuthState.Error(ex.message ?: "Error")
        }
    }

    override suspend fun logInWithUserPassword(email: String, password: String): AuthState {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            dataStore.saveUID(currentUser?.uid.orEmpty())
            AuthState.LoggedIn
        } catch (ex: Exception) {
            AuthState.Error(ex.message ?: "Error")
        }
    }
}
