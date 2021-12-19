package com.krygodev.singlesplanet.repository

import android.util.Log
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.util.Constants
import com.krygodev.singlesplanet.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException

class PairsRepositoryImpl(
    private val _firebaseFirestore: FirebaseFirestore
) : PairsRepository {

    override suspend fun getUsers(user: User): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading())

        try {

            val result = _firebaseFirestore
                .collection(Constants.USER_COLLECTION)
                .whereNotEqualTo("uid", user.uid)
                .whereEqualTo("gender", user.interestedGender)
                .get()
                .await()

            Log.e("REPO", result.documents.toString())

            emit(Resource.Success(result.toObjects(User::class.java)))

        } catch (e: HttpException) {
            emit(Resource.Error(message = "Something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection!"))
        } catch (e: FirebaseFirestoreException) {
            emit(Resource.Error(message = e.localizedMessage!!))
        }
    }
}