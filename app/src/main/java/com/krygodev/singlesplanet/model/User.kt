package com.krygodev.singlesplanet.model

import com.google.firebase.Timestamp

data class User(
    val uid: String? = null,
    val email: String? = null,
    val name: String? = null,
    val age: Timestamp? = null,
    val bio: String? = null,
    val gender: String? = null,
    val interestedGender: String? = null,
    val photoURL: String? = null,
    val location: List<Int>? = null,
)