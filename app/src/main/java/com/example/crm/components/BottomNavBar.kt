package com.example.crm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BottomNavBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(color = Color(0xFF006AFF), shape = CircleShape),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Inicio",
            tint = Color.Black,
            modifier = Modifier
                .size(26.dp)
                .clickable { navController.navigate("home") }
        )

        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = "Mensajes",
            tint = Color.White,
            modifier = Modifier
                .size(26.dp)
                .clickable { navController.navigate("mensajes") }
        )

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Perfil",
            tint = Color.White,
            modifier = Modifier
                .size(26.dp)
                .clickable { navController.navigate("perfil") }
        )

        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "calendario",
            tint = Color.White,
            modifier = Modifier
                .size(26.dp)
                .clickable { navController.navigate("calendario") }
        )
    }
}
