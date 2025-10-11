package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val imageUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatConversationScreen(
    onBackClick: () -> Unit = {}
) {
    val messages = listOf(
        ChatMessage(
            id = "1",
            text = "Can these foods relieve stress, and where can I find them?",
            isUser = true
        ),
        ChatMessage(
            id = "2", 
            text = "Yes, certain foods can help relieve stress! Bali definitely has some of the best food! From the iconic Babi Guling (suckling pig) to spicy Sambal Matah, every dish is packed with flavor. And don't forget Nasi Campur—such a unique mix of rice, meats, vegetables, and spices. The freshness of ingredients and the blend of traditional Balinese spices make every bite unforgettable!",
            isUser = false,
            imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400&h=300&fit=crop"
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0F2FE), // Light blue at top
                        Color(0xFFF0F9FF), // Lighter blue in middle
                        Color(0xFFFAFAFA)  // Light gray at bottom
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        text = "Get Chatia +",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3B82F6) // Blue color
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
            
            // Chat Messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    if (message.isUser) {
                        UserMessageBubble(message = message)
                    } else {
                        BotMessageBubble(message = message)
                    }
                }
            }
            
            // Bottom Chat Input
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Text(
                        text = "Type a message..",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEF4444) // Red color like in image
            )
        ) {
            Text(
                text = message.text,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(16.dp),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun BotMessageBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Image if exists
        message.imageUrl?.let { imageUrl ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 200.dp, height = 120.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Bot message bubble
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Card(
                modifier = Modifier.widthIn(max = 320.dp),
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = message.text,
                        fontSize = 16.sp,
                        color = Color.Black,
                        lineHeight = 22.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CopyAll,
                    contentDescription = "Copy",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            IconButton(
                onClick = { },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Like",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            IconButton(
                onClick = { },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeOff,
                    contentDescription = "Volume",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            IconButton(
                onClick = { },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ChatConversationScreenPreview() {
    ChatConversationScreen(onBackClick = {})
}
