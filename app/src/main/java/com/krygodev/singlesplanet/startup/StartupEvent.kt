package com.krygodev.singlesplanet.startup

import android.net.Uri

sealed class StartupEvent {
    data class EnteredBio(val value: String): StartupEvent()
    data class PickedGender(val value: String): StartupEvent()
    data class PickedInterestedGender(val value: String): StartupEvent()
    data class PickedBirthDate(val value: String): StartupEvent()
    data class PickedPhoto(val value: String): StartupEvent()
    data class UploadPhoto(val value: Uri): StartupEvent()
    object Submit: StartupEvent()
    object GetUserData: StartupEvent()
}
