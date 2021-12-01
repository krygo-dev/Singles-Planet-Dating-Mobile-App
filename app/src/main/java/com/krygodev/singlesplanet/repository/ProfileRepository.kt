package com.krygodev.singlesplanet.repository

import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getUserData(uid: String): Flow<Resource<User>>

    suspend fun setOrUpdateUserData(user: User): Flow<Resource<Boolean>>
}