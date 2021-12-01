package com.krygodev.singlesplanet.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.util.Constants
import com.krygodev.singlesplanet.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException

class AuthenticationRepositoryImpl(
    private val _firebaseAuth: FirebaseAuth,
    private val _firebaseFirestore: FirebaseFirestore
) : AuthenticationRepository {

    override suspend fun signIn(email: String, password: String): Flow<Resource<AuthResult>> =
        flow {
            emit(Resource.Loading())

            try {
                val result = _firebaseAuth.signInWithEmailAndPassword(email, password).await()
                emit(Resource.Success(result))
            } catch (e: HttpException) {
                emit(Resource.Error(message = "Something went wrong!"))
            } catch (e: IOException) {
                emit(Resource.Error(message = "Couldn't reach server, check your internet connection!"))
            } catch (e: FirebaseAuthException) {
                emit(Resource.Error(message = e.localizedMessage!!))
            }
        }

    override suspend fun signUp(email: String, password: String, name: String): Flow<Resource<AuthResult>> =
        flow {
            emit(Resource.Loading())

            try {
                val result = _firebaseAuth.createUserWithEmailAndPassword(email, password).await()

                result.user?.let { user ->
                    val newUser = User(uid = user.uid, email = user.email, name = name)
                    createNewUserInDB(newUser)
                }

                emit(Resource.Success(result))
            } catch (e: HttpException) {
                emit(Resource.Error(message = "Something went wrong!"))
            } catch (e: IOException) {
                emit(Resource.Error(message = "Couldn't reach server, check your internet connection!"))
            } catch (e: FirebaseAuthException) {
                emit(Resource.Error(message = e.localizedMessage!!))
            }
        }

    override suspend fun resetPassword(email: String): Flow<Resource<Void>> = flow {
        emit(Resource.Loading())

        try {
            val result = _firebaseAuth.sendPasswordResetEmail(email).await()
            emit(Resource.Success(result))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection!"))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(message = e.localizedMessage!!))
        }
    }

    override suspend fun signOut() = flow {
        emit(Resource.Loading())

        try {
            val result = _firebaseAuth.signOut()
            emit(Resource.Success(result))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection!"))
        } catch (e: FirebaseAuthException) {
            emit(Resource.Error(message = e.localizedMessage!!))
        }
    }

    private suspend fun createNewUserInDB(user: User) {
        _firebaseFirestore.collection(Constants.USER_COLLECTION).document(user.uid!!).set(user)
            .await()
    }
}