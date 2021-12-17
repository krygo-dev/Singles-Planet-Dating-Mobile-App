package com.krygodev.singlesplanet.repository

import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.util.Resource
import kotlinx.coroutines.flow.Flow

interface PairsRepository {

    suspend fun getUsers(): Flow<Resource<List<User>>>
}