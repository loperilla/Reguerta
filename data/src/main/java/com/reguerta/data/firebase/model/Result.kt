package com.reguerta.data.firebase.model

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.model
 * Created By Manuel Lopera on 1/4/24 at 20:30
 * All rights reserved 2024
 */
typealias RootError = Error

sealed interface DataResult<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data: D) : DataResult<D, E>
    data class Error<out D, out E : RootError>(val error: E) : DataResult<D, E>
}