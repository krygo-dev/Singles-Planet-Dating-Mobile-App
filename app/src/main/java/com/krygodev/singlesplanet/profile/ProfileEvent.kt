package com.krygodev.singlesplanet.profile

import android.net.Uri

sealed class ProfileEvent {
    data class UpdateInterestedGender(val value: String): ProfileEvent()
    data class UpdateBio(val value: String): ProfileEvent()
    data class UpdatePhoto(val value: Uri): ProfileEvent()
    data class UpdateSearchDistance(val value: Float): ProfileEvent()
    data class UpdateSearchAge(val value: List<Float>): ProfileEvent()
    object UpdateUserData: ProfileEvent()
    object SignOut: ProfileEvent()
    object GetUserData: ProfileEvent()
}
