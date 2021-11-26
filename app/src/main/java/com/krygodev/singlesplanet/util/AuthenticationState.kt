package com.krygodev.singlesplanet.util

data class AuthenticationState(
    val isLoading: Boolean = false,
    val result: Any? = null,
    val error: String = ""
)
