package com.example.crm.Screens

import android.util.Log
import androidx.compose.foundation.background
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
import com.google.firebase.firestore.FirebaseFirestore
import coil.compose.AsyncImage
import com.example.crm.viewmodel.DoctorViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Doctores(navController: NavHostController, doctorViewModel: DoctorViewModel) {
    val db = FirebaseFirestore.getInstance()
    var doctors by remember { mutableStateOf(listOf<Doctor>()) }

    // Cargar doctores al iniciar la pantalla
    LaunchedEffect(Unit) {
        db.collection("doctores").get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    Log.d("FirestoreDoctor", "Doc: ${doc.data}")
                }
                val lista = result.mapNotNull {
                    try {
                        it.toObject(Doctor::class.java)
                    } catch (e: Exception) {
                        Log.e("FirestoreDoctor", "Error al convertir doctor: ${e.message}")
                        null
                    }
                }
                doctors = lista
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener doctores", e)
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
                    IconButton(onClick = {
                        navController.navigate("AgregarDoctor") // 👈 Navega a la pantalla de agregar
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar Doctor")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF005BEA)
                )
            )
        }
    )
{ padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            items(doctors) { doctor ->
                Card(
                    shape = RoundedCornerShape(16.dp),
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
                            contentDescription = doctor.nombre,
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.Gray, shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(doctor.nombre, fontSize = 16.sp, color = Color(0xFF005BEA))
                            Text(doctor.especialidad, fontSize = 14.sp, color = Color.DarkGray)
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Button(
                                    onClick = {
                                        doctorViewModel.selectedDoctor = doctor
                                        navController.navigate("DetalleDoctor")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005BEA))
                                ) {
                                    Text("Info", color = Color.White)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Filled.Info, contentDescription = "Info")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Filled.Event, contentDescription = "Calendario")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Filled.HelpOutline, contentDescription = "Ayuda")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Filled.FavoriteBorder, contentDescription = "Favorito")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Doctor(
    val nombre: String = "",
    val especialidad: String = "",
    val imagen: String = "",
    val experiencia: Int = 0,
    val focus: String = "",
    val perfil: String = "",
    val carrera: String = "",
    val highlights: String = ""
)
