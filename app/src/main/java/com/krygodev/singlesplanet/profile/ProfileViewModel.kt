package com.krygodev.singlesplanet.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.repository.AuthenticationRepository
import com.krygodev.singlesplanet.repository.ProfileRepository
import com.krygodev.singlesplanet.util.LoadingState
import com.krygodev.singlesplanet.util.Resource
import com.krygodev.singlesplanet.util.Screen
import com.krygodev.singlesplanet.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val _authenticationRepository: AuthenticationRepository,
    private val _profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = mutableStateOf(LoadingState())
    val state: State<LoadingState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _user = mutableStateOf(User())
    val user: State<User> = _user

    init {
        onEvent(ProfileEvent.GetUserData)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.SignOut -> {
                viewModelScope.launch {
                    _authenticationRepository.signOut().onEach { result ->
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
                                _eventFlow.emit(UIEvent.ShowSnackbar("Signed out!"))
                                delay(500)
                                _eventFlow.emit(UIEvent.Success(Screen.SignInScreen.route))
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
            is ProfileEvent.GetUserData -> {
                viewModelScope.launch {
                    val currentUser = _authenticationRepository.getCurrentUser()
                    if (currentUser == null) {
                        _eventFlow.emit(UIEvent.ShowSnackbar("Unexpected error!"))
                    } else {
                        _profileRepository.getUserData(uid = currentUser.uid).onEach { result ->
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

                                    _user.value = result.data!!
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
            is ProfileEvent.UpdatePhoto -> {
                viewModelScope.launch {
                    _profileRepository.uploadUserPhoto(
                        uid = user.value.uid!!,
                        photoURI = event.value
                    )
                        .onEach { result ->
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
                                    _user.value = user.value.copy(
                                        photoURL = result.data.toString()
                                    )
                                    _eventFlow.emit(UIEvent.PhotoUploaded)
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
            is ProfileEvent.UpdateUserData -> {
                viewModelScope.launch {
                    _profileRepository.setOrUpdateUserData(user = user.value).onEach { result ->
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
                                _eventFlow.emit(UIEvent.ShowSnackbar("Profile updated!"))
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
            is ProfileEvent.UpdateBio -> {
                _user.value = user.value.copy(
                    bio = event.value
                )
            }
            is ProfileEvent.UpdateInterestedGender -> {
                _user.value = user.value.copy(
                    interestedGender = event.value
                )
            }
            is ProfileEvent.UpdateSearchAge -> {
                _user.value = user.value.copy(
                    searchAge = event.value
                )
            }
            is ProfileEvent.UpdateSearchDistance -> {
                _user.value = user.value.copy(
                    searchDistance = event.value
                )
            }
        }
    }
}