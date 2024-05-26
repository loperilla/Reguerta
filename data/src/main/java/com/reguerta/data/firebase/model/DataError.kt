package com.reguerta.data.firebase.model

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.model
 * Created By Manuel Lopera on 1/4/24 at 20:31
 * All rights reserved 2024
 */

sealed interface DataError : Error {
    enum class Firebase : DataError {
        EMPTY_LIST,
        NOT_FOUND,
        UNKNOWN
    }

    enum class Local : DataError {
        DISK_FULL
    }
}