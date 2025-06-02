package com.example.crm.Screens

import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import com.example.crm.components.DatePicker
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.filled.Event
import androidx.compose.ui.platform.LocalContext
import com.example.crm.viewmodel.DoctorViewModel
import java.util.Calendar

data class Appointment(
    val doctorId: String = "",
    val userId: String = "",
    val doctorName: String = "",
    val date: String = "",
    val time: String = "",
    val description: String = ""
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarCitaScreen(navController: NavHostController, doctorViewModel: DoctorViewModel) {
    val doctor = doctorViewModel.selectedDoctor ?: return
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    val horas = listOf(
        "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM",
        "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
        "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM"
    )

    var selectedDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var horasOcupadas by remember { mutableStateOf(setOf<String>()) }
    val horasDisponibles = horas.filter { it !in horasOcupadas }


    LaunchedEffect(selectedDate) {
        if (selectedDate.isNotBlank()) {
            db.collection("appointments")
                .whereEqualTo("doctorId", doctor.uid)
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnSuccessListener { result ->
                    horasOcupadas = result.mapNotNull { it.getString("time") }.toSet()
                }
        }
    }


    // Muestra el DatePickerDialog
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        android.app.DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                selectedDate = "%02d/%02d/%d".format(day, month + 1, year)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar con Dr. ${doctor.firstName} ${doctor.lastName}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val userId = auth.currentUser?.uid ?: return@Button
                    val cita = Appointment(
                        doctorId = doctor.uid,
                        doctorName = "${doctor.firstName} ${doctor.lastName}",
                        userId = userId,
                        date = selectedDate,
                        time = selectedTime ?: "",
                        description = description
                    )

                    db.collection("appointments").add(cita)
                        .addOnSuccessListener {
                            // Notificación para el paciente
                            db.collection("notifications").add(
                                mapOf(
                                    "userId" to userId,
                                    "title" to "Cita agendada",
                                    "message" to "Has agendado una cita con el Dr. ${doctor.firstName} ${doctor.lastName} el $selectedDate a las $selectedTime",
                                    "timestamp" to com.google.firebase.Timestamp.now(),
                                    "read" to false
                                )
                            )

                            // Notificación para el doctor
                            db.collection("notifications").add(
                                mapOf(
                                    "userId" to doctor.uid,
                                    "title" to "Nueva cita recibida",
                                    "message" to "Un paciente ha agendado una cita para el $selectedDate a las $selectedTime",
                                    "timestamp" to com.google.firebase.Timestamp.now(),
                                    "read" to false
                                )
                            )

                            navController.navigate("Home")
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = selectedDate.isNotBlank() && selectedTime != null
            ) {
                Text("Confirmar Cita")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Selecciona un día", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text("Fecha") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.Event, contentDescription = "Elegir fecha")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Selecciona una hora", fontWeight = FontWeight.Bold)
            Column {
                horasDisponibles.chunked(3).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fila.forEach { hora ->
                            FilterChip(
                                selected = selectedTime == hora,
                                onClick = { selectedTime = hora },
                                label = { Text(hora) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }



            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe tu problema") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
        }
    }
}
