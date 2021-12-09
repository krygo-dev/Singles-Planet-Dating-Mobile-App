package com.krygodev.singlesplanet.startup

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.repository.AuthenticationRepository
import com.krygodev.singlesplanet.repository.ProfileRepository
import com.krygodev.singlesplanet.util.AuthenticationState
import com.krygodev.singlesplanet.util.Resource
import com.krygodev.singlesplanet.util.Screen
import com.krygodev.singlesplanet.util.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
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

    private val _birthDate = mutableStateOf("")
    val birthDate: State<String> = _birthDate

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
            is StartupEvent.PickedBirthDate -> {
                _birthDate.value = event.value
            }
            is StartupEvent.PickedPhoto -> {
                _photoURL.value = event.value
            }
            is StartupEvent.Submit -> {
                viewModelScope.launch {
                    if (gender.value.isBlank() ||
                        interestedGender.value.isBlank() ||
                        birthDate.value.isBlank() ||
                        photoURL.value.isBlank()
                    ) {
                        _eventFlow.emit(UIEvent.ShowSnackbar("Fill up all fields marked with '*'!"))
                    } else {
                        user = User(
                            uid = user.uid,
                            email = user.email,
                            name = user.name,
                            birthDate = birthDate.value,
                            bio = bio.value,
                            gender = gender.value,
                            interestedGender = interestedGender.value,
                            photoURL = photoURL.value
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

                                    _eventFlow.emit(UIEvent.Success(Screen.HomeScreen.route))
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

                                    _eventFlow.emit(UIEvent.ShowSnackbar("User data loaded!"))
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
            is StartupEvent.UploadPhoto -> {
                viewModelScope.launch {
                    _profileRepository.uploadUserPhoto(uid = user.uid!!, photoURI = event.value)
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
                                    _photoURL.value = result.data.toString()

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
        }
    }

    fun selectDate(context: Context) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, year, month, day ->
            val pickedDate = Calendar.getInstance()
            pickedDate.set(year, month, day)
            _birthDate.value = "$day-$month-$year"
            Log.d("VIEWMODEL", "Chosen date: ${birthDate.value}")
        }, startYear, startMonth, startDay).show()
    }
}