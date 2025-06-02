package com.example.crm.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.net.URLEncoder

data class ChatItem(
    val uid: String,
    val fullName: String,
    val image: String,
    val lastMessage: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavHostController,
    currentUserId: String,
    currentUserRole: Int // 0 = paciente, 1 = doctor
) {
    val db = FirebaseFirestore.getInstance()
    var chatUsers by remember { mutableStateOf<List<ChatItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("appointments")
            .whereEqualTo(if (currentUserRole == 0) "userId" else "doctorId", currentUserId)
            .get()
            .addOnSuccessListener { result ->
                val otherIds = result.mapNotNull {
                    val doctorId = it.getString("doctorId") ?: return@mapNotNull null
                    val userId = it.getString("userId") ?: return@mapNotNull null
                    if (currentUserRole == 0) doctorId else userId
                }.distinct()

                otherIds.forEach { otherId ->
                    db.collection("users").document(otherId).get()
                        .addOnSuccessListener { userDoc ->
                            val name = userDoc.getString("firstName") ?: ""
                            val surname = userDoc.getString("lastName") ?: ""
                            val image = userDoc.getString("imagen")
                                ?: "https://previews.123rf.com/images/jemastock/jemastock1709/jemastock170901928/85132270-pictograma-dentro-del-marco-de-la-persona-de-la-gente-y-el-tema-humano-ilustraci%C3%B3n-de-vector-de-dise.jpg"

                            val chatId = if (currentUserId < otherId)
                                "${currentUserId}_$otherId"
                            else
                                "${otherId}_$currentUserId"

                            db.collection("chats").document(chatId).collection("messages")
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnSuccessListener { msgs ->
                                    val lastMsg = msgs.firstOrNull()?.getString("text") ?: ""
                                    val item = ChatItem(otherId, "$name $surname", image, lastMsg)
                                    chatUsers = (chatUsers + item).distinctBy { it.uid }
                                }
                        }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mensajes") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            items(chatUsers) { chatUser ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            val encodedImage = URLEncoder.encode(chatUser.image, "UTF-8")
                            navController.navigate("chat/${chatUser.uid}/${chatUser.fullName}/$encodedImage")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2EDF7)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(chatUser.image),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.LightGray, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(chatUser.fullName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text(chatUser.lastMessage, fontSize = 14.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}
