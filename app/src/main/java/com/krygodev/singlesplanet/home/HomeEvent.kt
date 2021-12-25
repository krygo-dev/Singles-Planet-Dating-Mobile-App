package com.krygodev.singlesplanet.home

import com.krygodev.singlesplanet.model.User

sealed class HomeEvent {
    data class SetUserLocation(val value: List<Double>): HomeEvent()
    data class SelectYes(val value: String): HomeEvent()
    data class SelectNo(val value: String): HomeEvent()
    data class NewPair(val value: User): HomeEvent()
    object GetUserData: HomeEvent()
    object UpdateUserData: HomeEvent()
    object GetUsers: HomeEvent()
}