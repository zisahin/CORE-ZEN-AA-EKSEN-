package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class SuggestionCard(
    val title: String
)

data class PopularTopic(
    val title: String,
    val description: String,
    val iconColor: Color,
    val backgroundColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    onBackClick: () -> Unit = {},
    onSuggestionClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    
    val suggestions = listOf(
        SuggestionCard("Günün Özeti"),
        SuggestionCard("Haftanın Özeti"),
        SuggestionCard("Zaman Tüneli"),
        SuggestionCard("AA Haber Doğrulama"),
        SuggestionCard("AI Haber Araştırma")
    )
    
    val popularTopics = listOf(
        PopularTopic(
            title = "Ekrem İmamoğlu",
            description = "Transforming diagnostics and treatments for improved patient outcomes.",
            iconColor = Color.White,
            backgroundColor = Color(0xFF4F46E5)
        ),
        PopularTopic(
            title = "Borsa ve Altın\n",


            description = "Empowering artists with innovative tools and endless creative possibilities.",
            iconColor = Color.White,
            backgroundColor = Color(0xFFF59E0B)
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
                title = { },
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
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Greeting Section
                Text(
                    text = "Merhaba, Misafir",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "AA AI'a sor",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Last Update: 10/10/2024",
                    fontSize = 14.sp,
                    color = Color(0xFF3B82F6), // Blue color
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Suggestion Cards Grid - Scrollable rows
                val topRowSuggestions = suggestions.take(3)
                val bottomRowSuggestions = suggestions.drop(3)
                
                // First row - scrollable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    topRowSuggestions.forEach { suggestion ->
                        Card(
                            modifier = Modifier
                                .clickable(
                onClick = { onSuggestionClick()  },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.8f)
                            )
                        ) {
                            Text(
                                text = suggestion.title,
                                fontSize = 14.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Second row - scrollable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    bottomRowSuggestions.forEach { suggestion ->
                        Card(
                            modifier = Modifier
                                .clickable(
                onClick = { onSuggestionClick()  },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.8f)
                            )
                        ) {
                            Text(
                                text = suggestion.title,
                                fontSize = 14.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Popular Topics Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pöpüler Haberler",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "See all",
                        fontSize = 16.sp,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.clickable(
                onClick = {   },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Popular Topics Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    popularTopics.forEach { topic ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                onClick = {   },
                                    indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
                                    shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.8f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = -1.dp),

                            ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                // Icon circle
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(topic.backgroundColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Simple icon representation
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                topic.iconColor,
                                                CircleShape
                                            )
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = topic.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    lineHeight = 22.sp
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = topic.description,
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp))
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
                        text = "Type a message...",
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

@Preview
@Composable
fun AIChatScreenPreview() {
    AiChatScreen(onBackClick = {})
}

