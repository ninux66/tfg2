package com.example.crm.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.crm.R

@Composable
fun InicioSesion(navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(350.dp)
                    .padding(top = 24.dp),
                contentScale = ContentScale.Fit
            )

            // Título
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Vital",
                    fontSize = 36.sp,
                    color = Color(0xFF005BEA),
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "Connect",
                    fontSize = 36.sp,
                    color = Color(0xFF005BEA),
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "Tu Medico De Confianza",
                    fontSize = 14.sp,
                    color = Color(0xFF005BEA),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Texto informativo
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 24.dp),
                lineHeight = 12.sp
            )

            // Botones
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navHostController.navigate("Login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005BEA)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Iniciar Sesion", color = Color.White, fontSize = 16.sp)
                }

                Button(
                    onClick = { navHostController.navigate("Register") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80C1FF)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Registrarse", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
