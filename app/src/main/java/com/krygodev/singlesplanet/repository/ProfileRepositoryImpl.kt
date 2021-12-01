package com.krygodev.singlesplanet.repository

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.util.Constants
import com.krygodev.singlesplanet.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class ProfileRepositoryImpl(
    private val _firebaseFirestore: FirebaseFirestore
) : ProfileRepository {

    override suspend fun getUserData(uid: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())

        try {
            val user = _firebaseFirestore.collection(Constants.USER_COLLECTION).document(uid).get()
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection!"))
        } catch (e: FirebaseFirestoreException) {
            emit(Resource.Error(message = e.localizedMessage!!))
        }
    }

    override suspend fun setOrUpdateUserData(user: User): Flow<Resource<Any>> {
        TODO("Not yet implemented")
    }
}