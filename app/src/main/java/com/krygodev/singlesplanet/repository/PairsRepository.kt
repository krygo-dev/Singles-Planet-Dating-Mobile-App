package com.krygodev.singlesplanet.repository

import com.krygodev.singlesplanet.model.Chatroom
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.util.Resource
import kotlinx.coroutines.flow.Flow

interface PairsRepository {

    suspend fun getUsers(user: User): Flow<Resource<List<User>>>

    suspend fun getPairs(user: User): Flow<Resource<List<User>>>

    suspend fun createChatroom(chatroom: Chatroom): Flow<Resource<String>>
}