package com.krygodev.singlesplanet.home

import com.krygodev.singlesplanet.model.User

sealed class HomeEvent {
    data class SetUserLocation(val value: List<Double>): HomeEvent()
    data class SelectYes(val value: User): HomeEvent()
    data class SelectNo(val value: User): HomeEvent()
    data class UpdateUserData(val value: User): HomeEvent()
    object NewPair: HomeEvent()
    object GetUserData: HomeEvent()
    object GetUsers: HomeEvent()
}