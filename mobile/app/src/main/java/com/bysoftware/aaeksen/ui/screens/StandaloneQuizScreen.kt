package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bysoftware.aaeksen.presentation.gamification.GamificationViewModel
import com.bysoftware.aaeksen.ui.theme.aa_color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandaloneQuizScreen(
    navController: NavController,
    gamificationViewModel: GamificationViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf("") }

    // Quiz kategorileri - Firebase'den gelecek
    val allQuizCategories = remember {
        listOf(
            QuizCategory("Spor", "⚽", aa_color, "Spor haberleri ve bilgileri"),
            QuizCategory("Siyaset", "🏛️", Color(0xFF2196F3), "Siyasi gelişmeler ve haberler"),
            QuizCategory("Ekonomi", "💰", Color(0xFFFF9800), "Ekonomik haberler ve analizler"),
            QuizCategory("Teknoloji", "💻", Color(0xFF9C27B0), "Teknoloji haberleri ve yenilikler"),
            QuizCategory("Sağlık", "🏥", Color(0xFFF44336), "Sağlık haberleri ve bilgileri"),
            QuizCategory("Eğitim", "📚", Color(0xFF607D8B), "Eğitim haberleri ve gelişmeler"),
            QuizCategory("Kültür", "🎭", Color(0xFF795548), "Kültür ve sanat haberleri"),
            QuizCategory("Bilim", "🔬", Color(0xFF009688), "Bilim ve araştırma haberleri"),
            QuizCategory("Çevre", "🌱", Color(0xFF8BC34A), "Çevre ve doğa haberleri"),
            QuizCategory("Tarih", "📜", Color(0xFF5D4037), "Tarih ve arkeoloji haberleri")
        )
    }

    // Filtered categories based on search
    val filteredCategories = remember(searchText) {
        if (searchText.isEmpty()) {
            allQuizCategories
        } else {
            allQuizCategories.filter { 
                it.name.contains(searchText, ignoreCase = true) ||
                it.description.contains(searchText, ignoreCase = true)
            }
        }
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .padding(bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Search Bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = {
                    Text(
                        text = "Quiz ara...",
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }
        
        item {
        
            // Play and Win Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    aa_color,
                                    aa_color.copy(alpha = 0.8f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Oyna & Kazan",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Şimdi bir quiz başlat ve eğlen",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Button(
                            onClick = {
                                navController.navigate("quiz_play/Genel")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text(
                                text = "Başla",
                                color = aa_color,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = aa_color,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
        
        item {
            // Categories Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kategoriler",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = "${filteredCategories.size} kategori",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        // Categories Grid - Show all categories in a grid
        val chunkedCategories = filteredCategories.chunked(2)
        items(chunkedCategories) { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowCategories.forEach { category ->
                    Box(modifier = Modifier.weight(1f)) {
                        EnhancedCategoryCard(
                            category = category,
                            onClick = {
                                navController.navigate("quiz_play/${category.name}")
                            }
                        )
                    }
                }
                // Fill remaining space if odd number of items
                if (rowCategories.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Composable
fun EnhancedCategoryCard(
    category: QuizCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                category.color.copy(alpha = 0.1f),
                                category.color.copy(alpha = 0.05f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = category.emoji,
                        fontSize = 24.sp
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(category.color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "5",
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Column {
                    Text(
                        text = category.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "5 soru",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun QuizCategoryCard(
    category: QuizCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kategori ikonu
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        category.color.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.emoji,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Kategori bilgileri
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = category.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
            
            // Ok ikonu
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Quiz'e Git",
                tint = category.color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ModernCategoryCard(
    category: QuizCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(140.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = category.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Category icon with background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        category.color.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.emoji,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = category.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}


data class QuizCategory(
    val name: String,
    val emoji: String,
    val color: Color,
    val description: String
)

