package com.example.crm


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.lint.Names.Runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.crm.Screens.AgendarCitaScreen
import com.example.crm.Screens.AgregarDoctor
import com.example.crm.Screens.Ajustes
import com.example.crm.Screens.AjustesNotificaciones
import com.example.crm.Screens.Ayuda
import com.example.crm.Screens.CalendarioScreen
import com.example.crm.Screens.CambiarContrasena
import com.example.crm.Screens.ChatListScreen
import com.example.crm.Screens.ChatScreen
import com.example.crm.Screens.DetalleDoctor
import com.example.crm.Screens.Doctores
import com.example.crm.Screens.Favoritos
import com.example.crm.Screens.Home
import com.example.crm.Screens.InicioSesion
import com.example.crm.Screens.Login
import com.example.crm.Screens.MetodosDePago
import com.example.crm.Screens.NotificacionesScreen
import com.example.crm.Screens.PoliticaDePrivacidad
import com.example.crm.Screens.Register
import com.example.crm.Screens.Settings
import com.example.crm.Screens.perfil
import com.example.crm.viewmodel.DoctorViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

suspend fun getUserRole(uid: String): Int {
    val doc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
    return doc.getLong("rol")?.toInt() ?: 0
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationWrapper(navHostController: NavHostController, doctorViewModel: DoctorViewModel) {
    NavHost(navController = navHostController, startDestination = "InicioSesion") {
        composable("InicioSesion") { InicioSesion(navHostController) }
        composable("Login") { Login(navHostController) }
        composable("Register") { Register(navHostController) }
        composable("Home") { Home(navHostController, doctorViewModel) }
        composable("Doctores") { Doctores(navHostController, doctorViewModel) }
        composable("DetalleDoctor") { DetalleDoctor(navHostController, doctorViewModel) }
        composable("AgregarDoctor") { AgregarDoctor(navHostController) }
        composable("settings") { Settings(navHostController) }
        composable("perfil") { perfil(navHostController) }
        composable("Favoritos") { Favoritos(navHostController) }
        composable("Metodos-De-Pago") { MetodosDePago(navHostController) }
        composable("PoliticaDePrivacidad") { PoliticaDePrivacidad(navHostController) }
        composable("Ajustes") { Ajustes(navHostController) }
        composable("Ayuda") { Ayuda(navHostController) }
        composable("AjustesNotificaciones") { AjustesNotificaciones(navHostController)}
        composable("CambiarContrasena") { CambiarContrasena(navHostController)}
        composable("calendario") { CalendarioScreen(navHostController, doctorViewModel) }
        composable("AgendarCita") { AgendarCitaScreen(navHostController, doctorViewModel) }
        composable("Notification") { NotificacionesScreen(navHostController) }
        composable("mensajes") {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            val currentUserId = currentUser?.uid.orEmpty()
            var rol by remember { mutableStateOf<Int?>(null) }

            LaunchedEffect(currentUserId) {
                rol = getUserRole(currentUserId)
            }

            rol?.let {
                ChatListScreen(navHostController, currentUserId, it)
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable("chat/{otherId}/{name}/{image}") { backStackEntry ->
            val otherId = backStackEntry.arguments?.getString("otherId") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val encodedImage = backStackEntry.arguments?.getString("image") ?: ""
            val image = URLDecoder.decode(encodedImage, StandardCharsets.UTF_8.toString())
            val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

            ChatScreen(
                currentUserId = currentUserId,
                otherUserId = otherId,
                otherUserName = name,
                otherUserImage = image,
                navController = navHostController
            )
        }




    }
}


























