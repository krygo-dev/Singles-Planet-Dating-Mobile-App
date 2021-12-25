package com.krygodev.singlesplanet.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.krygodev.singlesplanet.composables.SetupBottomNavBar
import com.krygodev.singlesplanet.util.Gender
import com.krygodev.singlesplanet.util.UIEvent
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@ExperimentalMaterialApi
@ExperimentalCoilApi
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val user = viewModel.user.value
    val scaffoldState = rememberScaffoldState()

    var userAge: Int? = null

    val photoUriState = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUriState.value = uri
    }

    user.birthDate?.let {
        userAge =
            Calendar.getInstance().get(Calendar.YEAR) - user.birthDate.substring(0, 4).toInt()
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
                    viewModel.onEvent(ProfileEvent.UpdateUserData)
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.padding(horizontal = 8.dp),
        bottomBar = {
            SetupBottomNavBar(navController = navController)
        },
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
                    .padding(16.dp, 16.dp, 16.dp, 58.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                item {
                    Box(
                        modifier = Modifier.height(550.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        if (photoUriState.value != null) {
                            Image(
                                painter = rememberImagePainter(
                                    data = photoUriState.value,
                                    builder = {
                                        transformations(RoundedCornersTransformation(30.0f))
                                    }
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds
                            )
                        } else {
                            Image(
                                painter = rememberImagePainter(
                                    data = user.photoURL,
                                    builder = {
                                        transformations(RoundedCornersTransformation(30.0f))
                                    }
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds
                            )
                        }

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
                    Spacer(modifier = Modifier.height(15.dp))
                }
                item {
                    Text(
                        text = "${user.name}, $userAge",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.secondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Box {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    color = MaterialTheme.colors.background,
                                    shape = RoundedCornerShape(5.dp)
                                )
                        )
                        OutlinedTextField(
                            value = user.bio!!,
                            onValueChange = { viewModel.onEvent(ProfileEvent.UpdateBio(it)) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colors.primary,
                                cursorColor = MaterialTheme.colors.primaryVariant,
                                unfocusedBorderColor = MaterialTheme.colors.primary,
                                textColor = MaterialTheme.colors.primaryVariant,
                                placeholderColor = MaterialTheme.colors.primaryVariant,
                            ),
                            shape = RoundedCornerShape(5.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Text(
                        text = "Currently you are looking for ${user.interestedGender}.",
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(Select another option to change that)",
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.secondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = user.interestedGender == Gender.male,
                            onClick = { viewModel.onEvent(ProfileEvent.UpdateInterestedGender(Gender.male)) },
                            enabled = true,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colors.secondary,
                                unselectedColor = MaterialTheme.colors.secondary
                            )
                        )
                        Text(
                            text = Gender.male,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colors.secondary,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                        RadioButton(
                            selected = user.interestedGender == Gender.female,
                            onClick = { viewModel.onEvent(ProfileEvent.UpdateInterestedGender(Gender.female)) },
                            enabled = true,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colors.secondary,
                                unselectedColor = MaterialTheme.colors.secondary
                            )
                        )
                        Text(
                            text = Gender.female,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colors.secondary,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                        RadioButton(
                            selected = user.interestedGender == Gender.both,
                            onClick = { viewModel.onEvent(ProfileEvent.UpdateInterestedGender(Gender.both)) },
                            enabled = true,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colors.secondary,
                                unselectedColor = MaterialTheme.colors.secondary
                            )
                        )
                        Text(
                            text = Gender.both,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colors.secondary,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
                item {
                    Text(
                        text = "Your search distance is: ${user.searchDistance.toInt()} km",
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = user.searchDistance,
                        onValueChange = {
                            viewModel.onEvent(ProfileEvent.UpdateSearchDistance(it))
                        },
                        valueRange = 5f..150f,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Text(
                        text = "Your search age is: ${user.searchAge.first().toInt()} - ${user.searchAge.last().toInt()}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    RangeSlider(
                        values = user.searchAge.first()..user.searchAge.last(),
                        onValueChange = {
                            viewModel.onEvent(
                                ProfileEvent.UpdateSearchAge(
                                    listOf(
                                        it.start.toInt().toFloat(),
                                        it.endInclusive.toInt().toFloat()
                                    )
                                )
                            )
                        },
                        valueRange = 18f..99f,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    OutlinedButton(
                        onClick = {
                            if (photoUriState.value != null) {
                                viewModel.onEvent(ProfileEvent.UpdatePhoto(photoUriState.value!!))
                            } else {
                                viewModel.onEvent(ProfileEvent.UpdateUserData)
                            }
                        },
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colors.background,
                            backgroundColor = MaterialTheme.colors.secondary
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Update profile", fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Text(
                        text = "Or",
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    OutlinedButton(
                        onClick = {
                            viewModel.onEvent(ProfileEvent.SignOut)
                        },
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colors.background,
                            backgroundColor = MaterialTheme.colors.secondary
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Sign out!", fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}