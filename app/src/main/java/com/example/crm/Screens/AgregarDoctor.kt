package com.example.crm.Screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarDoctor(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var selectedUserId by remember { mutableStateOf<String?>(null) }
    var userList by remember { mutableStateOf(listOf<Triple<String, String, String>>()) } // (uid, nombre completo, email)
    var searchQuery by remember { mutableStateOf("") }
    var userRol by remember { mutableStateOf<Int?>(null) }

    // Campos médicos
    var especialidad by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf("") }
    var focus by remember { mutableStateOf("") }
    var perfil by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var highlights by remember { mutableStateOf("") }

    // Validar acceso por rol
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    userRol = doc.getLong("rol")?.toInt()
                }
        }
    }

    if (userRol != 2) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes permiso para acceder a esta pantalla.", color = Color.Red)
        }
        return
    }

    // Cargar usuarios
    LaunchedEffect(Unit) {
        db.collection("users").get().addOnSuccessListener { result ->
            userList = result.map {
                val uid = it.id
                val nombre = it.getString("firstName") ?: ""
                val apellidos = it.getString("lastName") ?: ""
                val email = it.getString("email") ?: ""
                Triple(uid, "$nombre $apellidos", email)
            }
        }
    }

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
            // Buscador de usuario
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar usuario por nombre, apellidos o UID") },
                modifier = Modifier.fillMaxWidth()
            )

            val filteredUsers = userList.filter {
                it.first.contains(searchQuery, ignoreCase = true) ||
                        it.second.contains(searchQuery, ignoreCase = true)
            }

            DropdownMenu(
                expanded = filteredUsers.isNotEmpty() && searchQuery.isNotBlank(),
                onDismissRequest = { }
            ) {
                filteredUsers.forEach { (uid, nombreCompleto, _) ->
                    DropdownMenuItem(
                        text = { Text("$nombreCompleto ($uid)") },
                        onClick = {
                            selectedUserId = uid
                            searchQuery = "$nombreCompleto ($uid)"
                        }
                    )
                }
            }

            // Campos médicos
            OutlinedTextField(value = especialidad, onValueChange = { especialidad = it }, label = { Text("Especialidad") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imagen, onValueChange = { imagen = it }, label = { Text("URL de imagen") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = experiencia, onValueChange = { experiencia = it }, label = { Text("Años de experiencia") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = focus, onValueChange = { focus = it }, label = { Text("Focus / Enfoque") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = perfil, onValueChange = { perfil = it }, label = { Text("Perfil") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = carrera, onValueChange = { carrera = it }, label = { Text("Carrera") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = highlights, onValueChange = { highlights = it }, label = { Text("Highlights / Logros") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    selectedUserId?.let { uid ->
                        val doctorData = mapOf(
                            "especialidad" to especialidad.trim(),
                            "imagen" to imagen.trim(),
                            "experiencia" to (experiencia.toIntOrNull() ?: 0),
                            "focus" to focus.trim(),
                            "perfil" to perfil.trim(),
                            "carrera" to carrera.trim(),
                            "highlights" to highlights.trim(),
                            "rol" to 1 // Asignar rol de doctor
                        )
                        db.collection("users").document(uid).update(doctorData)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Doctor actualizado correctamente")
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error al actualizar doctor", e)
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Doctor", fontSize = 16.sp)
            }
        }
    }
}
