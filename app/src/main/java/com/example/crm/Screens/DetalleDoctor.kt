package com.example.crm.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.crm.viewmodel.DoctorViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleDoctor(navController: NavHostController, viewModel: DoctorViewModel) {
    val doctor = viewModel.selectedDoctor ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Información", fontSize = 20.sp) },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Tarjeta principal con imagen, experiencia y focus
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Imagen circular
                    AsyncImage(
                        model = doctor.imagen,
                        contentDescription = "${doctor.firstName} ${doctor.lastName}",
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color.LightGray, CircleShape)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Experiencia
                    Surface(
                        color = Color(0xFF267CFC),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "${doctor.experiencia} años de experiencia",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Focus
                    Surface(
                        color = Color(0xFF4D92FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Focus: ${doctor.focus}",
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nombre y especialidad
                    Text("${doctor.firstName} ${doctor.lastName}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF005BEA))
                    Text(doctor.especialidad, fontSize = 14.sp, color = Color.DarkGray)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Iconos de puntuación y horario (puedes personalizar)
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconWithText(Icons.Default.Star, "5")
                        IconWithText(Icons.Default.Favorite, "40")
                        IconWithText(Icons.Default.AccessTime, "Mon–Sat / 9:00AM–5:00PM")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón de agendar
                    Button(
                        onClick = {navController.navigate("AgendarCita")},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005BEA)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Schedule", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Secciones inferiores
            Section(title = "Profile", content = doctor.perfil)
            Section(title = "Career Path", content = doctor.carrera)
            Section(title = "Highlights", content = doctor.highlights)
        }
    }
}

@Composable
fun Section(title: String, content: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF005BEA))
        Text(text = content, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun IconWithText(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF005BEA))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 14.sp, color = Color(0xFF005BEA))
    }
}
