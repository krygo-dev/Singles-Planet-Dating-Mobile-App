package com.krygodev.singlesplanet.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.krygodev.singlesplanet.composables.ProfileCard
import com.krygodev.singlesplanet.composables.SetupBottomNavBar
import com.krygodev.singlesplanet.util.Constants
import com.krygodev.singlesplanet.util.UIEvent
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("MissingPermission")
@ExperimentalPermissionsApi
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val usersList = viewModel.usersList.value
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val checkInterval = 30000L

    val locationRequest =
        LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = checkInterval
            fastestInterval = checkInterval

        }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            result.locations.forEach { location ->
                try {
                    viewModel.onEvent(
                        HomeEvent.SetUserLocation(
                            listOf(
                                location.latitude,
                                location.longitude
                            )
                        )
                    )
                } catch (e: Exception) {
                    Log.e(Constants.LOG_TAG, e.message!!)
                }
            }
        }
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    permissionState.launchMultiplePermissionRequest()
                }

                if (event == Lifecycle.Event.ON_CREATE && permissionState.allPermissionsGranted) {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

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
                else -> {
                    Log.e(Constants.LOG_TAG, "Something went wrong!")
                }
            }
        }
    }

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
            if (!permissionState.allPermissionsGranted) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Location permissions denied. \n" + "You can change that in app settings.",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    if (usersList.isEmpty()) {
                        Box(
                            modifier = Modifier.height(550.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = "No more profiles to show! \n" + "Change settings to search for more."
                            )
                        }
                    } else {
                        Box {
                            usersList.forEach { userInList ->
                                ProfileCard(user = userInList, navController = navController)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(HomeEvent.SelectNo(usersList[usersList.lastIndex]))
                            },
                            modifier = Modifier
                                .size(60.dp)
                                .border(2.dp, MaterialTheme.colors.secondary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Hardware,
                                contentDescription = null,
                                tint = MaterialTheme.colors.secondary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.onEvent(HomeEvent.SelectYes(usersList[usersList.lastIndex]))
                            },
                            modifier = Modifier
                                .size(60.dp)
                                .border(2.dp, MaterialTheme.colors.primaryVariant, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primaryVariant,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}