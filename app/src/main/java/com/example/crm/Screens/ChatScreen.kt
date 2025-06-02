package com.example.crm.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.example.crm.model.Message
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUserId: String,
    otherUserId: String,
    otherUserName: String,
    otherUserImage: String?,
    navController: NavHostController
) {
    require(currentUserId.isNotBlank()) { "currentUserId vacío" }
    require(otherUserId.isNotBlank()) { "otherUserId vacío" }

    val db = FirebaseFirestore.getInstance()
    val chatId = if (currentUserId < otherUserId) "${currentUserId}_$otherUserId" else "${otherUserId}_$currentUserId"
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var input by remember { mutableStateOf("") }
    val decodedImage = URLDecoder.decode(
        otherUserImage
            ?: "https://previews.123rf.com/images/jemastock/jemastock1709/jemastock170901928/85132270-pictograma-dentro-del-marco-de-la-persona-de-la-gente-y-el-tema-humano-ilustraci%C3%B3n-de-vector-de-dise.jpg",
        StandardCharsets.UTF_8.toString()
    )

    LaunchedEffect(chatId) {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                messages = snapshot?.documents?.mapNotNull {
                    it.toObject(Message::class.java)
                } ?: emptyList()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(decodedImage),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = otherUserName, fontSize = 18.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    val isMe = msg.senderId == currentUserId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isMe) Color(0xFF006AFF) else Color(0xFFE6ECF0),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .widthIn(60.dp, 280.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = if (isMe) Color.White else Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp),
                    placeholder = { Text("Escribe un mensaje...") },
                    maxLines = 4
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    if (input.isNotBlank()) {
                        val msg = Message(currentUserId, input, Timestamp.now())
                        db.collection("chats").document(chatId)
                            .set(mapOf("participants" to listOf(currentUserId, otherUserId)), SetOptions.merge())
                        db.collection("chats").document(chatId).collection("messages").add(msg)
                        db.collection("notifications").add(
                            mapOf(
                                "userId" to otherUserId,
                                "title" to "Nuevo mensaje",
                                "message" to "Tienes un nuevo mensaje de chat",
                                "timestamp" to Timestamp.now(),
                                "read" to false
                            )
                        )
                        input = ""
                    }
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color(0xFF006AFF))
                }
            }
        }
    }
}
