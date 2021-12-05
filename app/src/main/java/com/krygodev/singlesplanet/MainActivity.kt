package com.krygodev.singlesplanet

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.krygodev.singlesplanet.home.HomeScreen
import com.krygodev.singlesplanet.sign_in.SignInScreen
import com.krygodev.singlesplanet.sign_up.SignUpScreen
import com.krygodev.singlesplanet.startup.StartupScreen
import com.krygodev.singlesplanet.ui.theme.SinglesPlanetTheme
import com.krygodev.singlesplanet.util.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SignInScreen.route
                    ) {
                        composable(route = Screen.SignInScreen.route) {
                            SignInScreen(navController = navController)
                        }
                        composable(route = Screen.SignUpScreen.route) {
                            SignUpScreen(navController = navController)
                        }
                        composable(route = Screen.HomeScreen.route) {
                            HomeScreen(navController = navController)
                        }
                        composable(route = Screen.StartupScreen.route) {
                            StartupScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}