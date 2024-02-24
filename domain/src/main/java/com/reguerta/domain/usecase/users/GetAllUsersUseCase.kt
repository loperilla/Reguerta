package com.reguerta.domain.usecase.users

import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import com.reguerta.domain.model.User
import com.reguerta.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.domain.usecase.users
 * Created By Manuel Lopera on 24/2/24 at 10:53
 * All rights reserved 2024
 */
class GetAllUsersUseCase @Inject constructor(
    private val repository: UsersCollectionService
) {
    suspend operator fun invoke(): Flow<List<User>> = repository.getUserList().map {
        it.fold(
            onSuccess = { userModelList ->
                userModelList.map { userModel ->
                    userModel.toDomain()
                }
            },
            onFailure = {
                emptyList()
            }
        )
    }
}
