package com.krygodev.singlesplanet.sign_up

import androidx.lifecycle.ViewModel
import com.krygodev.singlesplanet.repository.AuthenticationRepository
import javax.inject.Inject

class SignUpViewModel @Inject constructor(
    private val _authenticationRepository: AuthenticationRepository
): ViewModel() {
}