package com.example.crm.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.crm.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.crm.components.BottomNavBar
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.launch



@Composable
fun Home(navHostController: NavHostController) {
    val userName = remember { mutableStateOf("") }
    val doctors = remember { mutableStateOf(listOf<Doctor>()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val refreshData = {
        isRefreshing = true
        // Obtener nombre del usuario
        getUserName { userName.value = it }

        // Obtener lista de doctores
        FirebaseFirestore.getInstance().collection("doctores").get()
            .addOnSuccessListener { result ->
                doctors.value = result.mapNotNull {
                    try { it.toObject(Doctor::class.java) } catch (_: Exception) { null }
                }
                isRefreshing = false
            }
            .addOnFailureListener {
                isRefreshing = false
            }
    }

    // Primera carga
    LaunchedEffect(Unit) {
        refreshData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { refreshData() },
            indicator = { s, trigger ->
                SwipeRefreshIndicator(
                    state = s,
                    refreshTriggerDistance = trigger,
                    contentColor = Color(0xFF1E88E5),
                    scale = true
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp) // deja espacio para el BottomNavBar
            ) {
                TopBar(
                    userName = userName.value,
                    onSettingsClick = { navHostController.navigate("settings") },
                    onNotificationClick = { /* TODO */ }
                )

                Spacer(modifier = Modifier.height(16.dp))
                DoctorSearchBar(navHostController)
                Spacer(modifier = Modifier.height(16.dp))
                DoctorCardList(doctors = doctors.value)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 40.dp)
        ) {
            BottomNavBar(navController = navHostController)
        }
    }
}

@Composable
fun DoctorSearchBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { navController.navigate("doctores") }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_doctor),
                    contentDescription = "Doctores",
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(26.dp)
                )
                Text("Doctores", fontSize = 12.sp, color = Color(0xFF1E88E5))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { /* Acción para Favoritos */ }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite),
                    contentDescription = "Favoritos",
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(26.dp)
                )
                Text("Favoritos", fontSize = 12.sp, color = Color(0xFF1E88E5))
            }
        }

        Box(
            modifier = Modifier
                .height(45.dp)
                .weight(1f)
                .padding(start = 16.dp)
                .background(color = Color(0xFFDCE6FF), shape = CircleShape),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filtro",
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(24.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Buscar",
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(24.dp)
                )
            }
        }
    }
}

@Composable
fun TopBar(
    userName: String,
    onSettingsClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 20.dp)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "Bienvenido",
                    color = Color(0xFF1E88E5),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones",
                tint = Color(0xFF1E88E5),
                modifier = Modifier
                    .size(26.dp)
                    .clickable(onClick = onNotificationClick)
            )

            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Ajustes",
                tint = Color(0xFF1E88E5),
                modifier = Modifier
                    .size(26.dp)
                    .clickable(onClick = onSettingsClick)
            )
        }
    }
}

@Composable
fun DoctorCardList(doctors: List<Doctor>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(doctors) { doctor ->
            DoctorCard(doctor = doctor)
        }
    }
}


@Composable
fun DoctorCard(doctor: Doctor) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCE6FF)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = doctor.imagen,
                contentDescription = doctor.nombre,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doctor.nombre,
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

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconWithText2(Icons.Default.Star, "5")
                    IconWithText2(Icons.Default.ChatBubbleOutline, "60")
                    Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Ayuda", tint = Color(0xFF005BEA))
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorito", tint = Color(0xFF005BEA))
                }
            }
        }
    }
}

@Composable
fun IconWithText2(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF005BEA),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color(0xFF005BEA))
    }
}



fun getUserName(onResult: (String) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user != null) {
        val displayName = user.displayName
        if (!displayName.isNullOrBlank()) {
            onResult(displayName)
        } else {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val nombre = doc.getString("firstName") ?: ""
                    val apellidos = doc.getString("lastName") ?: ""
                    val completo = "$nombre $apellidos".trim()
                    onResult(completo.ifBlank { "Usuario desconocido" })
                }
                .addOnFailureListener {
                    onResult("Usuario desconocido")
                }
        }
    } else {
        onResult("Usuario no logueado")
    }
}



