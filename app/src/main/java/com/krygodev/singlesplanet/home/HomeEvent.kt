package com.krygodev.singlesplanet.home

sealed class HomeEvent {
    data class SetUserLocation(val value: List<Double>): HomeEvent()
    object GetUserData: HomeEvent()
    object UpdateUserLocalization: HomeEvent()
    object GetUsers: HomeEvent()
}