package com.example.crm.Screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.firestore.FirebaseFirestore
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.example.crm.viewmodel.DoctorViewModel


data class Doctor(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val especialidad: String = "",
    val imagen: String = "",
    val experiencia: Int = 0,
    val focus: String = "",
    val perfil: String = "",
    val carrera: String = "",
    val highlights: String = ""
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Doctores(navController: NavHostController, doctorViewModel: DoctorViewModel) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var doctors by remember { mutableStateOf(listOf<Doctor>()) }
    var allDoctors by remember { mutableStateOf(listOf<Doctor>()) }
    var isDeveloper by remember { mutableStateOf(false) }
    var favoritos by remember { mutableStateOf(setOf<String>()) } // set de UID de favoritos
    var filterMode by remember { mutableStateOf("A-Z") } // "A-Z" | "Favoritos"

    // Obtener rol y favoritos
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    isDeveloper = (doc.getLong("rol")?.toInt() == 2)
                    favoritos = (doc.get("favoritos") as? List<String>)?.toSet() ?: emptySet()
                }
        }
    }

    // Cargar doctores
    LaunchedEffect(Unit) {
        db.collection("users").whereEqualTo("rol", 1).get()
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull {
                    try {
                        it.toObject(Doctor::class.java).copy(uid = it.id)
                    } catch (e: Exception) {
                        Log.e("FirestoreDoctor", "Error al convertir doctor: ${e.message}")
                        null
                    }
                }
                allDoctors = lista
                doctors = lista.sortedBy { it.firstName }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener doctores", e)
            }
    }

    fun updateFilter() {
        doctors = when (filterMode) {
            "Favoritos" -> allDoctors.filter { favoritos.contains(it.uid) }
            else -> allDoctors.sortedBy { it.firstName }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctores", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (isDeveloper) {
                        IconButton(onClick = {
                            navController.navigate("AgregarDoctor")
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Agregar Doctor")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF005BEA)
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Filtro visual
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Ordenar", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))

                FilterChip("A-Z", filterMode == "A-Z") {
                    filterMode = "A-Z"
                    updateFilter()
                }
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip("★", filterMode == "Favoritos") {
                    filterMode = "Favoritos"
                    updateFilter()
                }
            }

            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                items(doctors) { doctor ->
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F0FF))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = doctor.imagen,
                                contentDescription = "${doctor.firstName} ${doctor.lastName}",
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color.Gray, shape = CircleShape)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${doctor.firstName} ${doctor.lastName}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF005BEA)
                                )
                                Text(
                                    text = doctor.especialidad,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(
                                        onClick = {
                                            doctorViewModel.selectedDoctor = doctor
                                            navController.navigate("DetalleDoctor")
                                        },
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005BEA))
                                    ) {
                                        Text("Info", color = Color.White)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(onClick = {
                                        doctorViewModel.selectedDoctor = doctor
                                        navController.navigate("AgendarCita")
                                    }) {
                                        Icon(Icons.Default.CalendarToday, contentDescription = "Agendar cita", tint = Color(0xFF005BEA))
                                    }
                                    IconButton(onClick = { /* TODO preguntas */ }) {
                                        Icon(Icons.Filled.HelpOutline, contentDescription = "FAQ", tint = Color(0xFF005BEA))
                                    }

                                    IconButton(onClick = {
                                        val uid = currentUser?.uid ?: return@IconButton
                                        val updated = favoritos.toMutableSet()
                                        if (updated.contains(doctor.uid)) {
                                            updated.remove(doctor.uid)
                                        } else {
                                            updated.add(doctor.uid)
                                        }
                                        db.collection("users").document(uid)
                                            .update("favoritos", updated.toList())
                                            .addOnSuccessListener {
                                                favoritos = updated
                                                if (filterMode == "Favoritos") updateFilter()
                                            }
                                    }) {
                                        Icon(
                                            imageVector = if (favoritos.contains(doctor.uid)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                            contentDescription = "Favorito",
                                            tint = Color(0xFF005BEA)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF005BEA) else Color(0xFFE0E8FF),
        modifier = Modifier
            .height(36.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(label, color = if (selected) Color.White else Color(0xFF005BEA))
        }
    }
}
