package com.krygodev.singlesplanet.sign_in

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.krygodev.singlesplanet.repository.AuthenticationRepository
import com.krygodev.singlesplanet.util.AuthenticationState
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val _authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _state = mutableStateOf(AuthenticationState())
    val state: State<AuthenticationState> = _state

    private val _email = mutableStateOf(SignInFieldState(hint = "Email address"))
    val email: State<SignInFieldState> = _email

    private val _password = mutableStateOf(SignInFieldState(hint = "Password"))
    val password: State<SignInFieldState> = _password

    fun signIn(email: String, password: String) {

    }
}