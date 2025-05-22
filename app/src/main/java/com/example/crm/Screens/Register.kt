package com.example.crm.Screens

import android.app.DatePickerDialog
import android.util.Patterns
import android.widget.DatePicker
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(navHostController: NavHostController) {
    var firstName by remember { mutableStateOf(TextFieldValue()) }
    var lastName by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }
    var phone by remember { mutableStateOf(TextFieldValue()) }
    var birthDate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf(TextFieldValue()) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue()) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var registrationError by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.height(24.dp)) // Baja la flecha

            // Flecha
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Atrás"
                )
            }

            // Título
            Text(
                text = "Crear Cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF005BEA),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            val fieldModifier = Modifier
                .fillMaxWidth()
                .height(56.dp)

            val fieldColors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFEFF3FF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = { Text("Nombre", color = Color(0xFF9BB5FF)) },
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = fieldModifier
                )

                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = { Text("Apellidos", color = Color(0xFF9BB5FF)) },
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = fieldModifier
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email", color = Color(0xFF9BB5FF)) },
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = fieldModifier
                )

                TextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text("Teléfono", color = Color(0xFF9BB5FF)) },
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = fieldModifier
                )

                TextField(
                    value = birthDate,
                    onValueChange = {},
                    placeholder = { Text("DD / MM / YYYY", color = Color(0xFF9BB5FF)) },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            val datePicker = DatePickerDialog(
                                context,
                                { _: DatePicker, year: Int, month: Int, day: Int ->
                                    birthDate = "%02d/%02d/%d".format(day, month + 1, year)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            datePicker.show()
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Fecha")
                        }
                    },
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = fieldModifier
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Contraseña", color = Color(0xFF9BB5FF)) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Mostrar/Ocultar"
                            )
                        }
                    },
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = fieldModifier
                )

                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Confirmar Contraseña", color = Color(0xFF9BB5FF)) },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Mostrar/Ocultar"
                            )
                        }
                    },
                    colors = fieldColors,
                    shape = RoundedCornerShape(12.dp),
                    modifier = fieldModifier
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // Baja el botón

            Button(
                onClick = {
                    if (email.text.isNotEmpty() && isValidEmail(email.text)) {
                        if (password.text == confirmPassword.text && password.text.length >= 6) {
                            auth.createUserWithEmailAndPassword(email.text, password.text)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser

                                        // 1. Actualizar el displayName con nombre + apellidos
                                        val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                            .setDisplayName("${firstName.text} ${lastName.text}")
                                            .build()

                                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                                            if (profileTask.isSuccessful) {
                                                // 2. Guardar en Firestore si lo deseas
                                                val data = hashMapOf(
                                                    "firstName" to firstName.text,
                                                    "lastName" to lastName.text,
                                                    "email" to email.text,
                                                    "phone" to phone.text,
                                                    "birthDate" to birthDate
                                                )
                                                db.collection("users").document(user.uid).set(data)
                                                    .addOnSuccessListener {
                                                        navHostController.navigate("Home")
                                                    }
                                            } else {
                                                registrationError = "No se pudo guardar el nombre: ${profileTask.exception?.message}"
                                            }
                                        }
                                    }
                                    else {
                                        registrationError = "Error: ${task.exception?.message}"
                                    }
                                }
                        } else {
                            registrationError = "Contraseñas no coinciden o son muy cortas."
                        }
                    } else {
                        registrationError = "Email inválido."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005BEA))
            ) {
                Text("Registrarse", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (registrationError.isNotEmpty()) {
                Text(
                    text = registrationError,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Sección inferior visible y centrada
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Ya tienes cuenta?",
                fontSize = 14.sp,
                color = Color.Gray
            )
            TextButton(onClick = { navHostController.navigate("Login") }) {
                Text("Iniciar Sesión", fontSize = 14.sp, color = Color(0xFF005BEA))
            }
        }
    }
}
