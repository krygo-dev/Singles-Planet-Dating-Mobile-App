package com.krygodev.singlesplanet.startup

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.krygodev.singlesplanet.util.Gender
import com.krygodev.singlesplanet.util.Screen
import com.krygodev.singlesplanet.util.UIEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StartupScreen(
    navController: NavController,
    viewModel: StartupViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val bioState = viewModel.bio.value
    val birthDateState = viewModel.birthDate.value
    val genderState = viewModel.gender.value
    val interestedGenderState = viewModel.interestedGender.value
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    var photoUriState by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUriState = uri
    }

    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
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
                    navController.navigate(Screen.HomeScreen.route)
                }
                is UIEvent.PhotoUploaded -> {
                    viewModel.onEvent(StartupEvent.Submit)
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
                Box(
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth()
                        .clickable { launcher.launch("image/*") }
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = RoundedCornerShape(5.dp)
                        )
                ) {
                    if (photoUriState != null) {
                        photoUriState?.let {
                            if (Build.VERSION.SDK_INT < 28) {
                                bitmap.value = MediaStore.Images
                                    .Media.getBitmap(context.contentResolver, it)

                            } else {
                                val source = ImageDecoder
                                    .createSource(context.contentResolver, it)
                                bitmap.value = ImageDecoder.decodeBitmap(source)
                            }

                            bitmap.value?.let { btm ->
                                Image(
                                    bitmap = btm.asImageBitmap(),
                                    contentDescription = "Profile image",
                                    modifier = Modifier.size(400.dp)
                                )
                            }
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Filled.AddAPhoto,
                            tint = MaterialTheme.colors.primary,
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
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
                        value = bioState,
                        onValueChange = { viewModel.onEvent(StartupEvent.EnteredBio(it)) },
                        placeholder = { Text(text = "Bio") },
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
                OutlinedButton(
                    onClick = {
                        viewModel.selectDate(context = context)
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        backgroundColor = MaterialTheme.colors.primary
                    ),
                    modifier = Modifier
                        .width(280.dp)
                        .height(50.dp)
                ) {
                    when {
                        birthDateState.isBlank() -> {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Select birth date", fontSize = 20.sp)
                            }
                        }
                        else -> {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Birth date: $birthDateState", fontSize = 20.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            item {
                Text(
                    text = "Pick your gender:",
                    color = MaterialTheme.colors.secondary,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = genderState == Gender.male,
                        onClick = { viewModel.onEvent(StartupEvent.PickedGender(Gender.male)) },
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
                        selected = genderState == Gender.female,
                        onClick = { viewModel.onEvent(StartupEvent.PickedGender(Gender.female)) },
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
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            item {
                Text(
                    text = "Pick gender you looking for:",
                    color = MaterialTheme.colors.secondary,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = interestedGenderState == Gender.male,
                        onClick = { viewModel.onEvent(StartupEvent.PickedInterestedGender(Gender.male)) },
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
                        selected = interestedGenderState == Gender.female,
                        onClick = { viewModel.onEvent(StartupEvent.PickedInterestedGender(Gender.female)) },
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
                        selected = interestedGenderState == Gender.both,
                        onClick = { viewModel.onEvent(StartupEvent.PickedInterestedGender(Gender.both)) },
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
                Spacer(modifier = Modifier.height(10.dp))
            }
            item {
                OutlinedButton(
                    onClick = {
                        viewModel.onEvent(StartupEvent.UploadPhoto(photoUriState!!))
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colors.background,
                        backgroundColor = MaterialTheme.colors.secondary
                    ),
                    modifier = Modifier.width(280.dp).height(50.dp)
                ) {
                    if (state.isLoading) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = MaterialTheme.colors.background
                            )
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Submit!", fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}