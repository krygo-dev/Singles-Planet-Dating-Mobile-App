package com.krygodev.singlesplanet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krygodev.singlesplanet.sign_in.SignInScreen
import com.krygodev.singlesplanet.sign_up.SignUpScreen
import com.krygodev.singlesplanet.ui.theme.SinglesPlanetTheme
import com.krygodev.singlesplanet.util.Screen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SinglesPlanetTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
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
                    }
                }
            }
        }
    }
}