package com.example.crm.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.example.crm.viewmodel.DoctorViewModel


@Composable
fun CalendarioScreen(navController: NavHostController, doctorViewModel: DoctorViewModel) {
    val db = FirebaseFirestore.getInstance()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Doctors", "Services")

    var allDoctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var especialidades by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("users").whereEqualTo("rol", 1).get()
            .addOnSuccessListener { result ->
                val doctores = result.mapNotNull {
                    it.toObject(Doctor::class.java).copy(uid = it.id)
                }
                allDoctors = doctores
                especialidades = doctores.map { it.especialidad }.distinct()
            }
    }

    Column {
        TabSelector(selectedTab) { selectedTab = it }

        when (selectedTab) {
            0 -> ListaDoctores(allDoctors, navController, doctorViewModel)
            1 -> ListaEspecialidades(especialidades, allDoctors, navController, doctorViewModel)
        }
    }
}

@Composable
fun TabSelector(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    val titles = listOf("Doctors", "Services")
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        titles.forEachIndexed { index, title ->
            val selected = selectedIndex == index
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .background(
                        color = if (selected) Color(0xFF2962FF) else Color(0xFFDCE6FF),
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    color = if (selected) Color.White else Color(0xFF2962FF),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ListaDoctores(doctores: List<Doctor>,navController: NavHostController,doctorViewModel: DoctorViewModel) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(doctores) { doctor ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        doctorViewModel.selectedDoctor = doctor
                        navController.navigate("DetalleDoctor")
                    },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F0FF)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${doctor.firstName} ${doctor.lastName}", fontWeight = FontWeight.Bold)
                    Text(doctor.especialidad, color = Color.DarkGray)
                }
            }

        }
    }
}

@Composable
fun ListaEspecialidades(
    especialidades: List<String>,
    doctores: List<Doctor>,
    navController: NavHostController,
    doctorViewModel: DoctorViewModel
) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        itemsIndexed(especialidades) { index, especialidad ->
            val doctoresDeEsta = doctores.filter { it.especialidad == especialidad }

            Column {
                // Tarjeta principal
                Card(
                    shape = RoundedCornerShape(50),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2962FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { expandedIndex = if (expandedIndex == index) null else index }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(especialidad, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                    }
                }

                // Contenido expandido
                if (expandedIndex == index) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6ECFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Esta especialidad se enfoca en el tratamiento específico de los síntomas relacionados.",
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                    Text(
                        "Doctors:",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(Color(0xFFDCE6FF), RoundedCornerShape(50))
                            .padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2962FF)
                    )

                    doctoresDeEsta.forEach { doctor ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F0FF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    doctorViewModel.selectedDoctor = doctor
                                    navController.navigate("DetalleDoctor")
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${doctor.firstName} ${doctor.lastName}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
