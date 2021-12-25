package com.krygodev.singlesplanet.repository

import android.util.Log
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.util.Constants
import com.krygodev.singlesplanet.util.Gender
import com.krygodev.singlesplanet.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

class PairsRepositoryImpl(
    private val _firebaseFirestore: FirebaseFirestore
) : PairsRepository {

    override suspend fun getUsers(user: User): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading())

        try {

            val searchForGender = if (user.interestedGender == Gender.both) listOf(
                Gender.male,
                Gender.female
            ) else listOf(user.interestedGender)

            val result = _firebaseFirestore
                .collection(Constants.USER_COLLECTION)
                .whereIn("gender", searchForGender)
                .whereGreaterThanOrEqualTo("userAge", user.searchAge.first())
                .whereLessThanOrEqualTo("userAge", user.searchAge.last())
                .get()
                .await()

            Log.e("REPO_1", result.documents.toString())

            val x1 = user.location!!.first()
            val y1 = user.location.last()
            val profiles = result.toObjects(User::class.java)
            val profilesToRemove = mutableListOf<User>()

            profiles.forEach { profile ->
                val x2 = profile.location!!.first()
                val y2 = profile.location.last()
                val distance =
                    sqrt((x2 - x1).pow(2) + (cos((x1 * PI) / 180) * (y2 - y1)).pow(2)) * 111.3214

                if (distance > user.searchDistance) {
                    profilesToRemove.add(profile)
                }

                if (user.selectedProfiles.contains(profile.uid) || user.pairs.contains(profile.uid)) {
                    profilesToRemove.add(profile)
                }
            }

            profiles.removeAll(profilesToRemove)

            profiles.removeIf { it.uid == user.uid }

            Log.e("REPO_2", profiles.toString())

            emit(Resource.Success(profiles))

        } catch (e: HttpException) {
            emit(Resource.Error(message = "Something went wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server, check your internet connection!"))
        } catch (e: FirebaseFirestoreException) {
            emit(Resource.Error(message = e.localizedMessage!!))
        }
    }
}