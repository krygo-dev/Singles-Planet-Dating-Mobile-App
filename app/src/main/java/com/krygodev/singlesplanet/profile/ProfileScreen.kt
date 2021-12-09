package com.krygodev.singlesplanet.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.krygodev.singlesplanet.composables.SetupBottomNavBar
import com.krygodev.singlesplanet.util.UIEvent
import kotlinx.coroutines.flow.collectLatest

@ExperimentalCoilApi
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val user = viewModel.user.value
    val scaffoldState = rememberScaffoldState()

    val photoUriState = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUriState.value = uri
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UIEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UIEvent.Success -> {
                    navController.navigate(event.route)
                }
                is UIEvent.PhotoUploaded -> {
                    viewModel.onEvent(ProfileEvent.GetUserData)
                }
            }
        }
    }

//    photoUriState.value?.let { uri ->
//        viewModel.onEvent(ProfileEvent.UpdatePhoto(uri))
//    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.padding(horizontal = 8.dp),
        bottomBar = { SetupBottomNavBar(navController = navController) }
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    color = MaterialTheme.colors.secondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                item {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Image(
                            painter = rememberImagePainter(
                                data = user.photoURL,
                                builder = {
                                    transformations(RoundedCornersTransformation(30.0f))
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp)
                        )
                        IconButton(
                            onClick = {
                                launcher.launch("image/*")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colors.secondary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
                item {
                    Text(text = "Bio: ${user.bio}")
                }
                item {
                    Text(text = "Birth date: ${user.birthDate}")
                }
                item {
                    Text(text = "Gender: ${user.gender}")
                }
                item {
                    Text(text = "Looking for: ${user.interestedGender}")
                }
                item {
                    Button(onClick = { viewModel.onEvent(ProfileEvent.SignOut) }) {
                        Text(text = "Sign Out!")
                    }
                }
            }
        }
    }
}