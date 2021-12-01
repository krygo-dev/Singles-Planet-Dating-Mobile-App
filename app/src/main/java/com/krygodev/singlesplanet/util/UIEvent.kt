package com.krygodev.singlesplanet.util

sealed class UIEvent {
    data class ShowSnackbar(val message: String): UIEvent()
    object Success: UIEvent()
}
