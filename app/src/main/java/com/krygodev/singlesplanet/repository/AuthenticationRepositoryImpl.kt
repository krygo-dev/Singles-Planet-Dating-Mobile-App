package com.krygodev.singlesplanet.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.util.Constants
import com.krygodev.singlesplanet.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class AuthenticationRepositoryImpl(
    private val _firebaseAuth: FirebaseAuth,
    private val _firebaseFirestore: FirebaseFirestore
) : AuthenticationRepository {

    override suspend fun signIn(email: String, password: String): AuthResult {
        return _firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signUp(email: String, password: String): AuthResult {
        return _firebaseAuth.createUserWithEmailAndPassword(email, password).await().also { result ->
            val user = User(uid = result.user?.uid, email = result.user?.email)
            createNewUserInDB(user)
            _firebaseAuth.currentUser?.sendEmailVerification()?.await()
        }
    }

    override suspend fun resetPassword(email: String): Void? {
        return _firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override suspend fun signOut() {
        return _firebaseAuth.signOut()
    }

    private suspend fun createNewUserInDB(user: User) {
        _firebaseFirestore.collection(Constants.USER_COLLECTION).document(user.uid!!).set(user)
            .await()
    }
}