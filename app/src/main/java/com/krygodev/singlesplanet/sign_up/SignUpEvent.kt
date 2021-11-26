package com.krygodev.singlesplanet.sign_up

sealed class SignUpEvent {
    data class EnteredEmail(val value: String): SignUpEvent()
    data class EnteredPassword(val value: String): SignUpEvent()
    data class EnteredRepeatPassword(val value: String): SignUpEvent()
    object SignUp: SignUpEvent()
}
