package com.krygodev.singlesplanet.util

sealed class Screen(val route: String) {
    object SignInScreen: Screen("sign_in_screen")
    object SignUpScreen: Screen("sign_up_screen")
}
