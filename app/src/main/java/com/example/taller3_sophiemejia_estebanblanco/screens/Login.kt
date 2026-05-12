package com.example.taller3_sophiemejia_estebanblanco.screens


import android.widget.Toast
import android.widget.Toast.makeText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.taller3_sophiemejia_estebanblanco.auth
import com.example.taller3_sophiemejia_estebanblanco.navigation.AppScreens
import com.example.taller3_sophiemejia_estebanblanco.shared.MyButton
import com.example.taller3_sophiemejia_estebanblanco.shared.validEmailAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthState(
    val email: String = "",
    val password: String = "",
    val emailError: String = "",
    val passwordError: String = ""

)

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState())
    val authState = _authState.asStateFlow()


    fun updateEmail(newValue: String) {
        _authState.update { it.copy(email = newValue) }
    }

    fun updatePassword(newValue: String) {
        _authState.update { it.copy(password = newValue) }
    }

    fun updateEmailError(newValue: String) {
        _authState.update { it.copy(emailError = newValue) }
    }

    fun updatePasswordError(newValue: String) {
        _authState.update { it.copy(passwordError = newValue) }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogIn(controller: NavController, model: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val state by model.authState.collectAsState()
    LaunchedEffect(Unit) {
        auth.currentUser?.let {
            controller.navigate(AppScreens.home.name)
        }

    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Icono de perfil",
            modifier = Modifier.size(160.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Inicio de Sesión",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(36.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.email,
                onValueChange = { model.updateEmail(newValue = it) },
                label = { Text("Email") },
                placeholder = { Text("Ingrese su email") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    if (state.emailError.isNotEmpty()) {
                        Text(state.emailError, color = Color.Red)
                    }
                }
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = { model.updatePassword(newValue = it) },
                label = { Text("Contraseña") },
                placeholder = { Text("Ingrese su contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                supportingText = {
                    if (state.passwordError.isNotEmpty()) {
                        Text(state.passwordError, color = Color.Red)
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(36.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MyButton("Login") {
                if (validateForm(model, state.email, state.password)) {
                    auth.signInWithEmailAndPassword(state.email, state.password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                controller.navigate(AppScreens.home.name)
                            } else {
                                makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

            MyButton("Register") {
                controller.navigate(AppScreens.register.name)
            }
        }
    }
}

fun validateForm(model: AuthViewModel, email: String, password: String): Boolean {
    if (email.isEmpty()) {
        model.updateEmailError("El email es requerido")
        return false
    } else {
        model.updateEmailError("")
    }
    if (!validEmailAddress(email)) {
        model.updateEmailError("No es un email válido")
        return false
    } else {
        model.updateEmailError("")
    }
    if (password.isEmpty()) {
        model.updatePasswordError("La contraseña es requerida")
        return false
    } else {
        model.updatePasswordError("")
    }
    if (password.length < 6) {
        model.updatePasswordError("La contraseña debe tener al menos 6 caracteres")
        return false
    } else {
        model.updatePasswordError("")
    }
    return true
}


@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val navController = rememberNavController()
    LogIn(navController)
}