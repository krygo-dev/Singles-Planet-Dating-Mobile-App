package com.krygodev.singlesplanet.composables

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.krygodev.singlesplanet.home.HomeScreen
import com.krygodev.singlesplanet.pairs.PairsScreen
import com.krygodev.singlesplanet.profile.ProfileScreen
import com.krygodev.singlesplanet.sign_in.SignInScreen
import com.krygodev.singlesplanet.sign_up.SignUpScreen
import com.krygodev.singlesplanet.startup.StartupScreen
import com.krygodev.singlesplanet.util.Constants
import com.krygodev.singlesplanet.util.Screen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Constants.HOME_GRAPH_ROUTE,
        route = Constants.ROOT_GRAPH_ROUTE
    ) {
        authenticationNavGraph(navController = navController)
        homeNavGraph(navController = navController)
    }
}

fun NavGraphBuilder.authenticationNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.SignInScreen.route,
        route = Constants.AUTH_GRAPH_ROUTE
    ) {
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(navController = navController)
        }
        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }
        composable(route = Screen.StartupScreen.route) {
            StartupScreen(navController = navController)
        }
    }
}

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.HomeScreen.route,
        route = Constants.HOME_GRAPH_ROUTE
    ) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screen.PairsScreen.route) {
            PairsScreen(navController = navController)
        }
        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen(navController = navController)
        }
    }
}