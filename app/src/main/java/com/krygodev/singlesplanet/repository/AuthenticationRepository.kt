package com.krygodev.singlesplanet.repository

import com.google.firebase.auth.AuthResult
import com.krygodev.singlesplanet.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {

    suspend fun signIn(email: String, password: String): Flow<Resource<AuthResult>>

    suspend fun signUp(email: String, password: String): Flow<Resource<AuthResult>>

    suspend fun resetPassword(email: String): Flow<Resource<Void>>

    suspend fun signOut(): Flow<Resource<Unit>>
}