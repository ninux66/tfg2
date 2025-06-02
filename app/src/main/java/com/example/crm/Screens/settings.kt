package com.example.crm.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.crm.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavHostController) {

    val showLogoutDialog = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val userFullName = remember { mutableStateOf("") }
    val profileImageUrl = remember {
        mutableStateOf(
            "https://previews.123rf.com/images/jemastock/jemastock1709/jemastock170901928/85132270-pictograma-dentro-del-marco-de-la-persona-de-la-gente-y-el-tema-humano-ilustraci%C3%B3n-de-vector-de-dise.jpg"
        )
    }
    val showImageDialog = remember { mutableStateOf(false) }
    val newImageUrl = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val nombre = doc.getString("firstName") ?: ""
                    val apellido = doc.getString("lastName") ?: ""
                    userFullName.value = "$nombre $apellido".trim()

                    profileImageUrl.value = doc.getString("imagen")
                        ?: profileImageUrl.value
                }
                .addOnFailureListener {
                    userFullName.value = "Usuario desconocido"
                }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
                .padding(WindowInsets.statusBars.asPaddingValues()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Text(
                    text = "Mi Perfil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = profileImageUrl.value,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
                Box(
                    modifier = Modifier
                        .offset(x = (-4).dp, y = (-4).dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E88E5))
                        .clickable { showImageDialog.value = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar foto",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = userFullName.value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PerfilItem(Icons.Default.Person, "Perfil") { navController.navigate("perfil") }
                PerfilItem(Icons.Default.FavoriteBorder, "Favoritos") { navController.navigate("Favoritos") }
                PerfilItem(Icons.Default.CreditCard, "Metodos De Pago") { navController.navigate("MetodosDePago") }
                PerfilItem(Icons.Default.Lock, "Politica De Privacidad") { navController.navigate("PoliticaDePrivacidad") }
                PerfilItem(Icons.Default.Settings, "Ajustes") { navController.navigate("Ajustes") }
                PerfilItem(Icons.Default.HelpOutline, "Ayuda") { navController.navigate("Ayuda") }
                PerfilItem(Icons.Default.ExitToApp, "Cerrar Sesion") {
                    showLogoutDialog.value = true
                }
            }
        }

        if (showLogoutDialog.value) {
            ModalBottomSheet(
                onDismissRequest = { showLogoutDialog.value = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Cerrar Sesion",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E88E5)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¿Quieres cerrar sesion?",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = { showLogoutDialog.value = false },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFFE8EDFF)
                            )
                        ) {
                            Text("Cancelar", color = Color(0xFF1E88E5))
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login") {
                                    popUpTo("settings") { inclusive = true }
                                }
                                showLogoutDialog.value = false
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                        ) {
                            Text("Cerrar Sesion", color = Color.White)
                        }
                    }
                }
            }
        }

        if (showImageDialog.value) {
            Dialog(onDismissRequest = { showImageDialog.value = false }) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text("Introduce la URL de la imagen", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newImageUrl.value,
                        onValueChange = { newImageUrl.value = it },
                        label = { Text("URL") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Cancelar",
                            color = Color.Gray,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable { showImageDialog.value = false }
                        )
                        Text(
                            "Guardar",
                            color = Color(0xFF1E88E5),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@clickable
                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(uid)
                                    .update("imagen", newImageUrl.value)
                                    .addOnSuccessListener {
                                        profileImageUrl.value = newImageUrl.value
                                        showImageDialog.value = false
                                    }
                            }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 40.dp)
        ) {
            BottomNavBar(navController = navController)
        }
    }
}

@Composable
fun PerfilItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() }
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCE6FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1E88E5))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}
