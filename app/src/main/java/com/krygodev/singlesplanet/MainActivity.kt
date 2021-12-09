package com.krygodev.singlesplanet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.krygodev.singlesplanet.composables.SetupNavGraph
import com.krygodev.singlesplanet.ui.theme.SinglesPlanetTheme
import com.krygodev.singlesplanet.util.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseAuthentication = FirebaseAuth.getInstance()

        setContent {
            SinglesPlanetTheme {

                val focusManager = LocalFocusManager.current
                val systemUIController = rememberSystemUiController()
                systemUIController.setSystemBarsColor(MaterialTheme.colors.background)

                Surface(color = MaterialTheme.colors.background,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    }
                ) {
                    val navController = rememberNavController()
                    SetupNavGraph(navController = navController)

                    firebaseAuthentication.currentUser?.let {
                        navController.navigate(Screen.HomeScreen.route)
                    }
                }
            }
        }
    }
}