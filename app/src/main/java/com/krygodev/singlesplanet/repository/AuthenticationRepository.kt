package com.krygodev.singlesplanet.repository

import com.google.firebase.auth.AuthResult
import com.krygodev.singlesplanet.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {

    suspend fun signIn(email: String, password: String): AuthResult

    suspend fun signUp(email: String, password: String): AuthResult

    suspend fun resetPassword(email: String): Void?

    suspend fun signOut()
}