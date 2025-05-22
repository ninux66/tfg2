package com.example.crm.Screens

import android.util.Patterns
import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navHostController: NavHostController) {
    var emailOrPhone by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    var passwordVisible by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val loginUser = {
        if (emailOrPhone.text.isNotEmpty() && password.text.isNotEmpty()) {
            val emailValid = Patterns.EMAIL_ADDRESS.matcher(emailOrPhone.text).matches()
            val phoneValid = Patterns.PHONE.matcher(emailOrPhone.text).matches()

            if (emailValid || phoneValid) {
                auth.signInWithEmailAndPassword(emailOrPhone.text, password.text)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navHostController.popBackStack()
                            navHostController.navigate("Home")
                        } else {
                            Log.e("Login", "Error al iniciar sesión: ${task.exception?.message}")
                        }
                    }
            } else {
                println("Formato de correo electrónico o teléfono inválido.")
            }
        } else {
            println("Por favor, complete todos los campos.")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // Encabezado
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = { navHostController.navigate("InicioSesion") }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Iniciar Sesion",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF005BEA)
            )
        }

        // Título y descripción
        Text(
            text = "Bienvenido",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF005BEA),
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        // Inputs
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TextField(
                value = emailOrPhone,
                onValueChange = { emailOrPhone = it },
                label = { Text("Gmail o Telefono") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF1F4FF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLabelColor = Color(0xFF005BEA)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF1F4FF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLabelColor = Color(0xFF005BEA)
                ),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = "Mostrar contraseña")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )
        }

        // Enlace "Olvidé contraseña"
        TextButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
        ) {
            Text(
                text = "He olvidado mi contraseña",
                fontSize = 13.sp,
                color = Color(0xFF005BEA)
            )
        }

        // Botón "Iniciar sesión"
        Button(
            onClick = { loginUser() },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005BEA)),
            shape = RoundedCornerShape(50)
        ) {
            Text("Iniciar Sesion", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Registro
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "No tienes cuenta? ", fontSize = 13.sp, color = Color.Gray)
            TextButton(onClick = { navHostController.navigate("Register") }) {
                Text("Registrate", fontSize = 13.sp, color = Color(0xFF005BEA), fontWeight = FontWeight.Bold)
            }
        }
    }
}
