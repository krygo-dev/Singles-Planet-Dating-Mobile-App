package com.krygodev.singlesplanet.sign_up

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krygodev.singlesplanet.repository.AuthenticationRepository
import com.krygodev.singlesplanet.util.AuthenticationState
import com.krygodev.singlesplanet.util.Resource
import com.krygodev.singlesplanet.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val _authenticationRepository: AuthenticationRepository
) : ViewModel() {
    private val _state = mutableStateOf(AuthenticationState())
    val state: State<AuthenticationState> = _state

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _repeatPassword = mutableStateOf("")
    val repeatPassword: State<String> = _repeatPassword

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.EnteredEmail -> {
                _email.value = event.value
            }
            is SignUpEvent.EnteredPassword -> {
                _password.value = event.value
            }
            is SignUpEvent.EnteredRepeatPassword -> {
                _repeatPassword.value = event.value
            }
            is SignUpEvent.SignUp -> {
                viewModelScope.launch {
                    if (email.value.isBlank() || password.value.isBlank() || repeatPassword.value.isBlank()) {
                        _eventFlow.emit(UIEvent.ShowSnackbar("Enter email address, password and repeated password!"))
                    } else if (password.value != repeatPassword.value) {
                        _eventFlow.emit(UIEvent.ShowSnackbar("Passwords are different!"))
                    } else {
                        _authenticationRepository.signUp(
                            email = email.value,
                            password = password.value
                        ).onEach { result ->
                            when (result) {
                                is Resource.Loading -> {
                                    _state.value = state.value.copy(
                                        isLoading = true,
                                        error = "",
                                        result = result.data
                                    )
                                }
                                is Resource.Success -> {
                                    _state.value = state.value.copy(
                                        isLoading = false,
                                        error = "",
                                        result = result.data
                                    )
                                    _eventFlow.emit(UIEvent.ShowSnackbar("Account created!"))
                                }
                                is Resource.Error -> {
                                    _state.value = state.value.copy(
                                        isLoading = false,
                                        error = result.message!!,
                                        result = result.data
                                    )
                                    _eventFlow.emit(UIEvent.ShowSnackbar(result.message))
                                }
                            }
                        }.launchIn(this)
                    }
                }
            }
        }
    }
}