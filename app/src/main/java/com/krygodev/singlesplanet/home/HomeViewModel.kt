package com.krygodev.singlesplanet.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.repository.AuthenticationRepository
import com.krygodev.singlesplanet.repository.PairsRepository
import com.krygodev.singlesplanet.repository.ProfileRepository
import com.krygodev.singlesplanet.util.LoadingState
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
class HomeViewModel @Inject constructor(
    private val _authenticationRepository: AuthenticationRepository,
    private val _profileRepository: ProfileRepository,
    private val _pairingRepository: PairsRepository
) : ViewModel() {

    private val _state = mutableStateOf(LoadingState())
    val state: State<LoadingState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _user = mutableStateOf(User())
    val user: State<User> = _user

    private val _usersList = mutableStateOf(listOf<User>())
    val usersList: State<List<User>> = _usersList

    private var _userSelectedProfiles = mutableListOf<String>()

    init {
        onEvent(HomeEvent.GetUserData)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.GetUserData -> {
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

                                    _userSelectedProfiles =
                                        user.value.selectedProfiles.toMutableList()

                                    onEvent(HomeEvent.GetUsers)
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
            is HomeEvent.UpdateUserData -> {
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
            is HomeEvent.SetUserLocation -> {
                _user.value = user.value.copy(
                    location = event.value
                )

                if (user.value.uid != null) {
                    onEvent(HomeEvent.UpdateUserData)
                }
            }
            is HomeEvent.GetUsers -> {
                viewModelScope.launch {
                    _pairingRepository.getUsers(user = user.value).onEach { result ->
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

                                _usersList.value = result.data!!

                                Log.d("VIEWMODEL", _usersList.value.toString())
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
            is HomeEvent.SelectNo -> {
                _usersList.value = usersList.value.toMutableList().also { list ->
                    list.removeIf { it.uid == event.value }
                }
            }
            is HomeEvent.SelectYes -> {
                if (!_user.value.selectedProfiles.contains(event.value)) {
                    _userSelectedProfiles.add(event.value)

                    // Delete selected profile from list
                    onEvent(HomeEvent.SelectNo(event.value))

                    _user.value = user.value.copy(
                        selectedProfiles = _userSelectedProfiles
                    )

                    onEvent(HomeEvent.UpdateUserData)

                    Log.e("HOME_EVENT_SELECT_YES", user.value.selectedProfiles.toString())
                }
            }
        }
    }
}