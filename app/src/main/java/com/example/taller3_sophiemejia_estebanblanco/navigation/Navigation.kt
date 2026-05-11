package com.example.taller3_sophiemejia_estebanblanco.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taller3_sophiemejia_estebanblanco.screens.Home
import com.example.taller3_sophiemejia_estebanblanco.screens.LogIn
import com.example.taller3_sophiemejia_estebanblanco.screens.Register

//import com.example.taller3_sophiemejia_estebanblanco.screens.register

enum class AppScreens{
    login,
    register,
    home,
    avaliableMap
}

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.register.name){
        composable(route = AppScreens.login.name){

            LogIn(navController)
        }
        composable(route = AppScreens.register.name){
            Register(navController)
        }
        composable(route = AppScreens.home.name){
            Home()
        }

    }
}
