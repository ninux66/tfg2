package com.example.crm.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Timestamp,
    val read: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var notificaciones by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            db.collection("notifications")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->
                    notificaciones = result.documents.mapNotNull { doc ->
                        try {
                            NotificationItem(
                                id = doc.id,
                                title = doc.getString("title") ?: return@mapNotNull null,
                                message = doc.getString("message") ?: "",
                                timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now(),
                                read = doc.getBoolean("read") ?: false
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (notificaciones.isEmpty()) {
            Box(modifier = Modifier
                .padding(padding)
                .fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes notificaciones.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(notificaciones) { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                db.collection("notifications").document(notif.id)
                                    .update("read", true)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (notif.read) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(notif.title, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(notif.message)
                        }
                    }
                }
            }
        }
    }
}
