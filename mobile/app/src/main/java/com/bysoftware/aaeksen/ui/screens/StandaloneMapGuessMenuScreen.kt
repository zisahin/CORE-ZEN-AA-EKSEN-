package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bysoftware.aaeksen.presentation.home.HomeViewModel
import com.bysoftware.aaeksen.ui.theme.aa_color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandaloneMapGuessMenuScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf("") }
    val recommendedNews by homeViewModel.recommendedNews.collectAsState()
    
    // Firebase'den haberleri yükle
    LaunchedEffect(Unit) {
        homeViewModel.loadNews()
    }

    // Harita tahmin kategorileri - Firebase'den gelecek
    val allMapGuessCategories = remember {
        listOf(
            MapGuessCategory("Spor", "⚽", aa_color, "Spor haberleri ile konum tahmin oyunu"),
            MapGuessCategory("Siyaset", "🏛️", Color(0xFF2196F3), "Siyasi olayların konumlarını tahmin et"),
            MapGuessCategory("Ekonomi", "💰", Color(0xFFFF9800), "Ekonomik gelişmelerin yerlerini bul"),
            MapGuessCategory("Teknoloji", "💻", Color(0xFF9C27B0), "Teknoloji haberlerinin konumları"),
            MapGuessCategory("Sağlık", "🏥", Color(0xFFF44336), "Sağlık haberlerinin yerlerini tahmin et"),
            MapGuessCategory("Eğitim", "📚", Color(0xFF607D8B), "Eğitim haberlerinin konumları"),
            MapGuessCategory("Kültür", "🎭", Color(0xFF795548), "Kültürel olayların yerlerini bul"),
            MapGuessCategory("Bilim", "🔬", Color(0xFF009688), "Bilimsel gelişmelerin konumları"),
            MapGuessCategory("Çevre", "🌱", Color(0xFF8BC34A), "Çevre haberlerinin yerlerini tahmin et"),
            MapGuessCategory("Tarih", "📜", Color(0xFF5D4037), "Tarihi olayların konumları")
        )
    }

    // Filtered categories based on search
    val filteredCategories = remember(searchText) {
        if (searchText.isEmpty()) {
            allMapGuessCategories
        } else {
            allMapGuessCategories.filter { 
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
                        text = "Harita oyunu ara...",
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
                            text = "Harita & Tahmin",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Şimdi bir haber oku ve tahmin oyunumuzu deneyimle",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Button(
                            onClick = {
                                // Rastgele harita tahmin oyununa uygun haber seç
                                val mapGuessNews = recommendedNews.filter { 
                                    !it.mapGuessDetail.isNullOrBlank() 
                                }
                                if (mapGuessNews.isNotEmpty()) {
                                    val randomNews = mapGuessNews.random()
                                    navController.navigate("news_detail/${randomNews.id}?autoStartMapGuess=true")
                                }
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
                                Icons.AutoMirrored.Filled.ArrowForward,
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
                        MapGuessCategoryCard(
                            category = category,
                            onClick = {
                                // Bu kategoriden harita tahmin oyununa uygun haber seç
                                val categoryNews = recommendedNews.filter { 
                                    it.category == category.name && !it.mapGuessDetail.isNullOrBlank() 
                                }
                                if (categoryNews.isNotEmpty()) {
                                    val randomNews = categoryNews.random()
                                    navController.navigate("news_detail/${randomNews.id}?autoStartMapGuess=true")
                                }
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
fun MapGuessCategoryCard(
    category: MapGuessCategory,
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
                            text = "🗺️",
                            fontSize = 10.sp
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
                        text = "Konum tahmin oyunu",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

data class MapGuessCategory(
    val name: String,
    val emoji: String,
    val color: Color,
    val description: String
)
