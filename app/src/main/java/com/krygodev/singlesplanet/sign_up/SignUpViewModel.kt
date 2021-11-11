package com.krygodev.singlesplanet.sign_up

import androidx.lifecycle.ViewModel
import com.krygodev.singlesplanet.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val _authenticationRepository: AuthenticationRepository
): ViewModel() {
}