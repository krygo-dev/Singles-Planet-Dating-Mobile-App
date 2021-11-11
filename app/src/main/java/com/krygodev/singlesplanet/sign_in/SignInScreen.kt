package com.krygodev.singlesplanet.sign_in

import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val emailState = viewModel.email.value
    val passwordState = viewModel.password.value

    val scaffoldState = rememberScaffoldState()

    Scaffold() {
        
    }
}