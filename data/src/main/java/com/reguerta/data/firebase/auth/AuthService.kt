package com.reguerta.data.firebase.auth

import com.reguerta.data.AuthState

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase
 * Created By Manuel Lopera on 24/1/24 at 12:20
 * All rights reserved 2024
 */
interface AuthService {
    val isAuthenticated: Boolean
    suspend fun signOut()
    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthState
    suspend fun refreshUser(): AuthState
    suspend fun logInWithUserPassword(email: String, password: String): AuthState
}