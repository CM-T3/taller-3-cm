package com.example.taller3_sophiemejia_estebanblanco.shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.taller3_sophiemejia_estebanblanco.R

@Composable
fun MyButton(text: String, action : ()->Unit ){
    Button(onClick = action, modifier = Modifier.fillMaxWidth(),
        colors = ButtonColors(
            contentColor = Color.White,
            containerColor = colorResource(R.color.azulBonito),
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.Gray
        )
    ){
        Text(text)
    }

}
