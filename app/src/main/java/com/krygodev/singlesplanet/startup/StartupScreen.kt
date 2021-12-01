package com.krygodev.singlesplanet.startup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.krygodev.singlesplanet.util.Screen
import com.krygodev.singlesplanet.util.UIEvent
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@Composable
fun StartupScreen(
    navController: NavController,
    viewModel: StartupViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val bioState = viewModel.bio.value
    val genderState = viewModel.gender.value
    val interestedGenderState = viewModel.interestedGender.value
    val photoURLState = viewModel.photoURL.value
    val birthDateState = viewModel.birthDate.value
    val latitudeState = viewModel.latitude.value
    val longitudeState = viewModel.longitude.value
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UIEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UIEvent.Success -> {
                    navController.navigate(Screen.HomeScreen.route)
                }
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState, modifier = Modifier.padding(8.dp)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            item {
                TextButton(
                    onClick = {
                        viewModel.selectDate(context = context)
                    }
                ) {
                    Text(text = "Select Date", color = Color.White)
                }
            }
        }
    }
}