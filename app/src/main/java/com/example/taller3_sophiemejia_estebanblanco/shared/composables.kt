package com.example.taller3_sophiemejia_estebanblanco.shared

import android.R.attr.enabled
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.taller3_sophiemejia_estebanblanco.R
import com.example.taller3_sophiemejia_estebanblanco.navigation.AppScreens
import com.example.taller3_sophiemejia_estebanblanco.screens.Home
import com.example.taller3_sophiemejia_estebanblanco.screens.AvailableUsers
import com.google.firebase.auth.FirebaseAuth


@Composable
fun MyButton(text: String, enabled: Boolean = true, action: () -> Unit) {
    Button(
        onClick = action,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonColors(
            contentColor = Color.White,
            containerColor = colorResource(R.color.azulBonito),
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.Gray
        )
    ) {
        Text(text)
    }

}

@Composable
fun MyBottomBar(navController: NavController, indexActual: Int) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colorResource(R.color.azulBonito),
        contentColor = Color.White,
        shadowElevation = 8.dp
    ) {
        NavigationBar(containerColor = Color.Transparent) {


            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                selected = indexActual == 0,
                onClick = {

                    if (indexActual != 0) {
                        navController.navigate(AppScreens.home.name)
                    }
                }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Usuarios Disponibles") },
                label = {
                    Text(
                        "Usuarios Disponibles", fontSize = 10.sp, fontWeight = FontWeight.Bold
                    )
                },
                selected = indexActual == 1,
                onClick = {
                    if (indexActual != 1) {
                        navController.navigate(AppScreens.availableUsers.name)
                    }
                }
            )

            NavigationBarItem(
                icon = {
                Icon(
                    Icons.Default.Close, contentDescription = "Cerrar Sesión"
                )
            },
                label = { Text("Cerrar Sesión", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                selected = indexActual == 2,
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(AppScreens.login.name)
                })
        }
    }
}