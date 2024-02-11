package com.reguerta.data.firebase.firestore

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore
 * Created By Manuel Lopera on 8/2/24 at 16:19
 * All rights reserved 2024
 */
sealed class CollectionResult<T> {
    data class Success<T>(val data: T) : CollectionResult<T>()
    data class Failure<T>(val exception: Exception) : CollectionResult<T>()
}
