package com.krygodev.singlesplanet.startup

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.repository.AuthenticationRepository
import com.krygodev.singlesplanet.repository.ProfileRepository
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
class StartupViewModel @Inject constructor(
    private val _profileRepository: ProfileRepository,
    private val _authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _state = mutableStateOf(AuthenticationState())
    val state: State<AuthenticationState> = _state

    private val _bio = mutableStateOf("")
    val bio: State<String> = _bio

    private val _gender = mutableStateOf("")
    val gender: State<String> = _gender

    private val _interestedGender = mutableStateOf("")
    val interestedGender: State<String> = _interestedGender

    private val _photoURL = mutableStateOf("")
    val photoURL: State<String> = _photoURL

    private val _age = mutableStateOf("")
    val age: State<String> = _age

    private val _latitude = mutableStateOf(0.0)
    val latitude: State<Double> = _latitude

    private val _longitude = mutableStateOf(0.0)
    val longitude: State<Double> = _longitude

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var user = User()

    init {
        onEvent(StartupEvent.GetUserData)
    }

    fun onEvent(event: StartupEvent) {
        when (event) {
            is StartupEvent.EnteredBio -> {
                _bio.value = event.value
            }
            is StartupEvent.PickedGender -> {
                _gender.value = event.value
            }
            is StartupEvent.PickedInterestedGender -> {
                _interestedGender.value = event.value
            }
            is StartupEvent.PickedAge -> {
                _age.value = event.value
            }
            is StartupEvent.PickedPhoto -> {
                _photoURL.value = event.value
            }
            is StartupEvent.CheckedLatitude -> {
                _latitude.value = event.value
            }
            is StartupEvent.CheckedLongitude -> {
                _longitude.value = event.value
            }
            is StartupEvent.Submit -> {
                viewModelScope.launch {
                    if (gender.value.isBlank() ||
                        interestedGender.value.isBlank() ||
                        age.value.isBlank() ||
                        photoURL.value.isBlank() ||
                        latitude.value.isNaN() ||
                        longitude.value.isNaN()
                    ) {
                        _eventFlow.emit(UIEvent.ShowSnackbar("Fill up all fields marked with '*'!"))
                    } else {
                        user = User(
                            uid = user.uid,
                            email = user.email,
                            name = user.name,
                            age = age.value,
                            bio = bio.value,
                            gender = gender.value,
                            interestedGender = interestedGender.value,
                            photoURL = photoURL.value,
                            location = listOf(latitude.value, longitude.value)
                        )
                        _profileRepository.setOrUpdateUserData(user = user).onEach { result ->
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
                                    _eventFlow.emit(UIEvent.ShowSnackbar("User updated!"))
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
            is StartupEvent.GetUserData -> {
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

                                    user = result.data!!

                                    _eventFlow.emit(UIEvent.ShowSnackbar("Data loaded"))
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