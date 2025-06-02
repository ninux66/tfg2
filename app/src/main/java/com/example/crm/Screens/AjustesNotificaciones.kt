package com.example.crm.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun AjustesNotificaciones(navHostController: NavHostController) {
    // Estados individuales por interruptor
    var generalNotification by remember { mutableStateOf(true) }
    var sound by remember { mutableStateOf(true) }
    var soundCall by remember { mutableStateOf(true) }
    var vibrate by remember { mutableStateOf(false) }
    var specialOffers by remember { mutableStateOf(false) }
    var payments by remember { mutableStateOf(true) }
    var promoAndDiscount by remember { mutableStateOf(false) }
    var cashback by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Encabezado
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF0057FF))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ajustes Notificaciones",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0057FF)
            )
        }

        NotificationToggle("General Notification", generalNotification) { generalNotification = it }
        NotificationToggle("Sound", sound) { sound = it }
        NotificationToggle("Sound Call", soundCall) { soundCall = it }
        NotificationToggle("Vibrate", vibrate) { vibrate = it }
        NotificationToggle("Special Offers", specialOffers) { specialOffers = it }
        NotificationToggle("Payments", payments) { payments = it }
        NotificationToggle("Promo And Discount", promoAndDiscount) { promoAndDiscount = it }
        NotificationToggle("Cashback", cashback) { cashback = it }
    }
}

@Composable
fun NotificationToggle(label: String, isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp)
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF296BFF),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCEDCFF)
            )
        )
    }
}
