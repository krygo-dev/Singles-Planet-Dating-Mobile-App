package com.krygodev.singlesplanet.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.krygodev.singlesplanet.util.BottomNavItem
import com.krygodev.singlesplanet.util.Screen

@Composable
fun SetupBottomNavBar(
    navController: NavController
) {
    BottomNavBar(
        items = listOf(
            BottomNavItem(
                name = "Home",
                route = Screen.HomeScreen.route,
                icon = Icons.Filled.Home
            ),
            BottomNavItem(
                name = "Pairs",
                route = Screen.PairsScreen.route,
                icon = Icons.Filled.People
            ),
            BottomNavItem(
                name = "Profile",
                route = Screen.ProfileScreen.route,
                icon = Icons.Filled.Person
            ),
        ),
        navController = navController,
        onItemClick = {
            navController.navigate(it.route) {
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}