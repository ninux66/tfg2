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
import androidx.compose.material.icons.filled.Favorite
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
import com.example.crm.viewmodel.DoctorViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun Home(navHostController: NavHostController, doctorViewModel: DoctorViewModel) {
    val userName = remember { mutableStateOf("") }
    val doctors = remember { mutableStateOf(listOf<Doctor>()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    var favoritos by remember { mutableStateOf(setOf<String>()) }
    var showOnlyFavoritos by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val refreshData = refreshData@ fun() {
        isRefreshing = true
        getUserName { userName.value = it }

        val uid = currentUser?.uid
        if (uid == null) {
            isRefreshing = false
            return@refreshData
        }

        FirebaseFirestore.getInstance().collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                favoritos = (doc.get("favoritos") as? List<String>)?.toSet() ?: emptySet()

                FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("rol", 1)
                    .get()
                    .addOnSuccessListener { result ->
                        val allDoctors = result.mapNotNull {
                            try {
                                it.toObject(Doctor::class.java).copy(uid = it.id)
                            } catch (_: Exception) { null }
                        }

                        doctors.value = allDoctors.filter {
                            (if (showOnlyFavoritos) favoritos.contains(it.uid) else true) &&
                                    (it.firstName.contains(searchQuery, true) || it.lastName.contains(searchQuery, true) || it.especialidad.contains(searchQuery, true))
                        }

                        isRefreshing = false
                    }
            }
            .addOnFailureListener {
                isRefreshing = false
            }
    }

    LaunchedEffect(Unit) { refreshData() }

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
            Column(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
                TopBar(
                    userName = userName.value,
                    onSettingsClick = { navHostController.navigate("settings") },
                    onNotificationClick = { navHostController.navigate("Notification") }
                )

                Spacer(modifier = Modifier.height(16.dp))
                DoctorSearchBar(
                    navController = navHostController,
                    onFavoritesClick = {
                        showOnlyFavoritos = !showOnlyFavoritos
                        refreshData()
                    },
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        refreshData()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                DoctorCardList(
                    doctors = doctors.value,
                    favoritos = favoritos,
                    onToggleFavorite = { uid ->
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@DoctorCardList
                        val updated = favoritos.toMutableSet()
                        if (updated.contains(uid)) updated.remove(uid) else updated.add(uid)
                        FirebaseFirestore.getInstance().collection("users").document(userId)
                            .update("favoritos", updated.toList())
                            .addOnSuccessListener {
                                favoritos = updated
                                if (showOnlyFavoritos) refreshData()
                            }
                    },
                    navController = navHostController,
                    doctorViewModel = doctorViewModel
                )
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
fun DoctorSearchBar(
    navController: NavHostController,
    onFavoritesClick: () -> Unit,
    query: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
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
                modifier = Modifier.clickable { onFavoritesClick() }
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

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .height(45.dp)
                .weight(1f)
                .padding(start = 16.dp),
            placeholder = { Text("Buscar...") },
            singleLine = true,
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFDCE6FF),
                focusedContainerColor = Color(0xFFDCE6FF),
                cursorColor = Color(0xFF1E88E5)
            )
        )
    }
}

@Composable
fun DoctorCardList(
    doctors: List<Doctor>,
    favoritos: Set<String>,
    onToggleFavorite: (String) -> Unit,
    navController: NavHostController,
    doctorViewModel: DoctorViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(doctors) { doctor ->
            DoctorCard(
                doctor = doctor,
                isFavorite = favoritos.contains(doctor.uid),
                onFavoriteClick = { onToggleFavorite(doctor.uid) },
                onHelpClick = {
                    doctorViewModel.selectedDoctor = doctor
                    navController.navigate("DetalleDoctor")
                }
            )
        }
    }
}

@Composable
fun DoctorCard(
    doctor: Doctor,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onHelpClick: () -> Unit
) {
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
                contentDescription = "${doctor.firstName} ${doctor.lastName}",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.width(12.dp))

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

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconWithText2(Icons.Default.Star, "5")
                    IconWithText2(Icons.Default.ChatBubbleOutline, "60")
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "Ayuda",
                        tint = Color(0xFF005BEA),
                        modifier = Modifier.clickable { onHelpClick() }
                    )
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = Color(0xFF005BEA),
                        modifier = Modifier.clickable { onFavoriteClick() }
                    )
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
    } else {
        onResult("Usuario no logueado")
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
