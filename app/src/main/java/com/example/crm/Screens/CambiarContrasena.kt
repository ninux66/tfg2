package com.example.crm.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CambiarContrasena(navHostController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showOld by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF0057FF))
            }
            Text(
                text = "Cambiar Contraseña",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0057FF),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PasswordInput("Contraseña Actual", oldPassword, { oldPassword = it }, showOld) { showOld = !showOld }
        Text(
            text = "¿Has olvidado tu contraseña?",
            color = Color(0xFF0057FF),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { navHostController.navigate("reset_password") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        PasswordInput("Nueva Contraseña", newPassword, { newPassword = it }, showNew) { showNew = !showNew }

        Spacer(modifier = Modifier.height(16.dp))
        PasswordInput("Confirmar Contraseña", confirmPassword, { confirmPassword = it }, showConfirm) { showConfirm = !showConfirm }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                errorMessage = ""
                successMessage = ""

                if (newPassword != confirmPassword) {
                    errorMessage = "Las contraseñas no coinciden"
                    return@Button
                }

                val email = user?.email
                if (user != null && email != null) {
                    val credential = EmailAuthProvider.getCredential(email, oldPassword)
                    user.reauthenticate(credential)
                        .addOnSuccessListener {
                            user.updatePassword(newPassword)
                                .addOnSuccessListener {
                                    successMessage = "Contraseña actualizada correctamente"
                                    oldPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                }
                                .addOnFailureListener {
                                    errorMessage = "Error al actualizar la contraseña"
                                }
                        }
                        .addOnFailureListener {
                            errorMessage = "La contraseña actual no es válida"
                        }
                } else {
                    errorMessage = "Usuario no autenticado"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF296BFF))
        ) {
            Text("Guardar", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red)
        }
        if (successMessage.isNotEmpty()) {
            Text(successMessage, color = Color(0xFF43A047))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    showPassword: Boolean,
    onToggleVisibility: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF2F5FF), RoundedCornerShape(12.dp)),
            textStyle = TextStyle(fontSize = 16.sp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFF2F5FF),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )
    }
}
