package com.krygodev.singlesplanet.profile

import android.net.Uri

sealed class ProfileEvent {
    data class UpdateInterestedGender(val value: String): ProfileEvent()
    data class UpdateBio(val value: String): ProfileEvent()
    data class UpdatePhoto(val value: Uri): ProfileEvent()
    object UpdateUserData: ProfileEvent()
    object SignOut: ProfileEvent()
    object GetUserData: ProfileEvent()
}
