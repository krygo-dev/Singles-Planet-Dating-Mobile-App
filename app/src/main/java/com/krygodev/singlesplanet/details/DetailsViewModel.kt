package com.krygodev.singlesplanet.details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krygodev.singlesplanet.model.User
import com.krygodev.singlesplanet.repository.ProfileRepository
import com.krygodev.singlesplanet.util.Constants
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
class DetailsViewModel @Inject constructor(
    private val _profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _state = mutableStateOf(LoadingState())
    val state: State<LoadingState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _user = mutableStateOf(User())
    val user: State<User> = _user

    init {
        savedStateHandle.get<String>(Constants.PARAM_UID)?.let { uid ->
            getUserData(uid)
        }
    }

    private fun getUserData(uid: String) {
        viewModelScope.launch {
            _profileRepository.getUserData(uid = uid).onEach { result ->
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