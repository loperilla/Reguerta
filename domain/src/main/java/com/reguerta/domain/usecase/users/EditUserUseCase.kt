package com.reguerta.domain.usecase.users

import com.reguerta.data.firebase.firestore.users.UserModel
import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.users
 * Created By Manuel Lopera on 24/2/24 at 10:55
 * All rights reserved 2024
 */

class EditUserUseCase @Inject constructor(
    private val repository: UsersCollectionService
) {
    suspend operator fun invoke(
        id: String,
        name: String,
        surname: String,
        phoneNumber: String,
        email: String,
        isAdmin: Boolean,
        isProducer: Boolean,
        companyName: String,
        numResignations: Int = 0,
        typeConsumer: String? = null,
        typeProducer: String? = null,
        available: Boolean = true
    ): Result<Boolean> {
        return try {
            val userModel = UserModel(
                name = name,
                surname = surname,
                email = email,
                phone = phoneNumber,
                isAdmin = isAdmin,
                isProducer = isProducer,
                companyName = companyName,
                numResignations = numResignations,
                typeConsumer = typeConsumer,
                typeProducer = typeProducer,
                available = available
            )
            repository.updateUser(id, userModel)
            Result.success(true)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}