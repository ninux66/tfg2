package com.example.crm

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.crm.viewmodel.DoctorViewModel


class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private val doctorViewModel = DoctorViewModel()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navHostController = rememberNavController()
            NavigationWrapper(navHostController, doctorViewModel)
        }
    }
}
