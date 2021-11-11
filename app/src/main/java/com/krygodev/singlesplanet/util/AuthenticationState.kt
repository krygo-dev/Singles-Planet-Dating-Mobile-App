package com.krygodev.singlesplanet.util

import com.google.firebase.auth.AuthResult

data class AuthenticationState(
    val isLoading: Boolean = false,
    val result: AuthResult? = null,
    val error: String = ""
)
