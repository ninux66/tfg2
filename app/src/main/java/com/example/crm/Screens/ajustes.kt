package com.example.crm.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Ajustes(navHostController: NavHostController) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ajustes",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0057FF),
                textAlign = TextAlign.Center
            )
            IconButton(
                onClick = { navHostController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF0057FF))
            }
        }

        AjustesItem(
            icon = Icons.Default.Notifications,
            text = "Ajustes Notificaciones"
        ) {
            navHostController.navigate("ajustesNotificaciones")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AjustesItem(
            icon = Icons.Default.VpnKey,
            text = "Cambiar Contraseña"
        ) {
            navHostController.navigate("CambiarContrasena")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AjustesItem(
            icon = Icons.Default.Person,
            text = "Borrar Cuenta"
        ) {
            showDeleteDialog.value = true
        }
    }

    // Diálogo de confirmación
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = {
                Text("Borrar Cuenta", fontWeight = FontWeight.Bold, color = Color(0xFF0057FF))
            },
            text = {
                Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        val user = auth.currentUser
                        val uid = user?.uid

                        if (user != null && uid != null) {
                            // Paso 1: Eliminar de Firestore
                            FirebaseFirestore.getInstance().collection("users").document(uid)
                                .delete()
                                .addOnSuccessListener {
                                    // Paso 2: Eliminar de Firebase Auth
                                    user.delete()
                                        .addOnSuccessListener {
                                            // Paso 3: Desloguear y navegar
                                            auth.signOut()
                                            navHostController.navigate("login") {
                                                popUpTo(0)
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            println("❌ Error al borrar de Auth: ${e.localizedMessage}")
                                        }
                                }
                                .addOnFailureListener { e ->
                                    println("❌ Error al borrar de Firestore: ${e.localizedMessage}")
                                }
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun AjustesItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color(0xFF0057FF),
            modifier = Modifier
                .size(40.dp)
                .padding(end = 16.dp)
        )
        Text(
            text = text,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Siguiente",
            tint = Color(0xFF0057FF),
            modifier = Modifier.size(24.dp)
        )
    }
}
