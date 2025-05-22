package com.example.crm.Screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarDoctor(navController: NavHostController) {
    var nombre by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf("") }
    var focus by remember { mutableStateOf("") }
    var perfil by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var highlights by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Doctor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = especialidad,
                onValueChange = { especialidad = it },
                label = { Text("Especialidad") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = imagen,
                onValueChange = { imagen = it },
                label = { Text("URL de imagen") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = experiencia,
                onValueChange = { experiencia = it },
                label = { Text("Años de experiencia") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = focus,
                onValueChange = { focus = it },
                label = { Text("Focus / Enfoque") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = perfil,
                onValueChange = { perfil = it },
                label = { Text("Perfil") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = carrera,
                onValueChange = { carrera = it },
                label = { Text("Carrera") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = highlights,
                onValueChange = { highlights = it },
                label = { Text("Highlights / Logros") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val nuevoDoctor = hashMapOf(
                        "nombre" to nombre.trim(),
                        "especialidad" to especialidad.trim(),
                        "imagen" to imagen.trim(),
                        "experiencia" to (experiencia.toIntOrNull() ?: 0),
                        "focus" to focus.trim(),
                        "perfil" to perfil.trim(),
                        "carrera" to carrera.trim(),
                        "highlights" to highlights.trim()
                    )

                    db.collection("doctores")
                        .add(nuevoDoctor)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Doctor agregado con ID: ${it.id}")
                            navController.popBackStack()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error al agregar doctor", e)
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Doctor", fontSize = 16.sp)
            }
        }
    }
}