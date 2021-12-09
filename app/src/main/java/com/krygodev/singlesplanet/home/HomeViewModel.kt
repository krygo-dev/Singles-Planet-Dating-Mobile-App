package com.krygodev.singlesplanet.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krygodev.singlesplanet.repository.AuthenticationRepository
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
class HomeViewModel @Inject constructor(
    private val _authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _state = mutableStateOf(LoadingState())
    val state: State<LoadingState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    /*fun onEvent(event: HomeEvent) {
        when (event) {
        }
    }*/
}