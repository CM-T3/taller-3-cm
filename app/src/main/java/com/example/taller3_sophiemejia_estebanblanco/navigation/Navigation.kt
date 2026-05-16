package com.example.taller3_sophiemejia_estebanblanco.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.taller3_sophiemejia_estebanblanco.auth
import com.example.taller3_sophiemejia_estebanblanco.screens.AvailableUsers
import com.example.taller3_sophiemejia_estebanblanco.screens.Home
import com.example.taller3_sophiemejia_estebanblanco.screens.LogIn
import com.example.taller3_sophiemejia_estebanblanco.screens.Register
import com.example.taller3_sophiemejia_estebanblanco.screens.sharedLocation

enum class AppScreens {
    login,
    register,
    home,
    availableUsers,
    availableMap,
}

@Composable
fun Navigation(navController: NavHostController) {
    val startDestination = if (auth.currentUser != null) {
        AppScreens.home.name
    } else {
        AppScreens.login.name
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = AppScreens.login.name) {
            LogIn(navController)
        }
        composable(route = AppScreens.register.name) {
            Register(navController)
        }
        composable(route = AppScreens.home.name) {
            Home(navController)
        }
        composable(route = AppScreens.availableUsers.name) {
            AvailableUsers(navController)
        }
        composable(route = "${AppScreens.availableMap.name}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            sharedLocation(navController = navController, trackedUserId = userId)
        }
    }
}