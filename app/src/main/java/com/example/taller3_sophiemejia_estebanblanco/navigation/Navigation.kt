package com.example.taller3_sophiemejia_estebanblanco.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taller3_sophiemejia_estebanblanco.screens.LogIn

enum class AppScreens{
    login,
    register,
    home
}

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.login.name){
        composable(route = AppScreens.login.name){

            LogIn(navController)
        }
        composable(route = AppScreens.register.name){
            //Register(navController)
        }
        composable(route = AppScreens.home.name){
            //Home(navController)
        }
    }
}
