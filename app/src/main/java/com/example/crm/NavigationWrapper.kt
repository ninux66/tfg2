package com.example.crm


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.crm.Screens.AgregarDoctor
import com.example.crm.Screens.Ajustes
import com.example.crm.Screens.Ayuda
import com.example.crm.Screens.DetalleDoctor
import com.example.crm.Screens.Doctores
import com.example.crm.Screens.Favoritos
import com.example.crm.Screens.Home
import com.example.crm.Screens.InicioSesion
import com.example.crm.Screens.Login
import com.example.crm.Screens.MetodosDePago
import com.example.crm.Screens.PoliticaDePrivacidad
import com.example.crm.Screens.Register
import com.example.crm.Screens.Settings
import com.example.crm.Screens.perfil
import com.example.crm.viewmodel.DoctorViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationWrapper(navHostController: NavHostController, doctorViewModel: DoctorViewModel) {
    NavHost(navController = navHostController, startDestination = "InicioSesion") {
        composable("InicioSesion") { InicioSesion(navHostController) }
        composable("Login") { Login(navHostController) }
        composable("Register") { Register(navHostController) }
        composable("Home") { Home(navHostController) }
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

    }
}


























