package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bysoftware.aaeksen.data.firebase.model.FirebaseNews
import com.bysoftware.aaeksen.domain.model.CityNews
import com.bysoftware.aaeksen.presentation.newsmap.NewsMapViewModel
import com.bysoftware.aaeksen.ui.theme.aa_color
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityNewsListScreen(
    cityName: String,
    categoryName: String,
    onNewsClick: (CityNews) -> Unit,
    onBackClick: () -> Unit,
    viewModel: NewsMapViewModel = hiltViewModel()
) {
    val cityNews by viewModel.cityNews.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Firebase haberlerini al
    val firebaseNews = remember(cityName, categoryName) {
        viewModel.getNewsByCityAndCategory(cityName, categoryName)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(
            left = 0.dp,
            top = 0.dp,
            right = 0.dp,
            bottom = 72.dp
        ),
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = cityName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$categoryName Haberleri",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = aa_color,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            if (uiState.isLoadingCityNews) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = aa_color)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Firebase haberlerini göster
                    items(firebaseNews) { news ->
                        FirebaseNewsCard(
                            news = news,
                            onClick = { 
                                // FirebaseNews'i CityNews'e dönüştür
                                val cityNews = CityNews(
                                    id = news.id,
                                    title = news.title,
                                    description = news.content.take(150) + "...",
                                    imageUrl = news.imageUrl,
                                    source = "AA Eksen",
                                    publishDate = news.createdAt?.toDate()?.toString() ?: "",
                                    timeAgo = calculateTimeAgo(news.createdAt),
                                    categoryId = news.category,
                                    cityId = cityName,
                                    url = news.newsUrl
                                )
                                onNewsClick(cityNews)
                            }
                        )
                    }
                    
                    // Eğer Firebase haberi yoksa bilgi mesajı göster
                    if (firebaseNews.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Bu kategori için henüz haber bulunmuyor",
                                        fontSize = 16.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "$cityName - $categoryName",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CityNewsCard(
    news: CityNews,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onClick()  },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // News image
            AsyncImage(
                model = news.imageUrl,
                contentDescription = news.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Category tag
                Text(
                    text = news.categoryId,
                    fontSize = 12.sp,
                    color = aa_color,
                    fontWeight = FontWeight.Medium
                )
                
                // Title
                Text(
                    text = news.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Time and source
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = news.source,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = news.timeAgo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun FirebaseNewsCard(
    news: FirebaseNews,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Haber görseli
            AsyncImage(
                model = news.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=400&h=200&fit=crop" },
                contentDescription = news.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Haber içeriği
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Kategori etiketi
                Surface(
                    color = aa_color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = news.category,
                        fontSize = 12.sp,
                        color = aa_color,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Haber başlığı
                Text(
                    text = news.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Haber özeti
                Text(
                    text = news.content.take(120) + if (news.content.length > 120) "..." else "",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Alt bilgiler
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AA Eksen",
                        fontSize = 12.sp,
                        color = aa_color,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = calculateTimeAgo(news.createdAt),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// Zaman hesaplama fonksiyonu
fun calculateTimeAgo(timestamp: Timestamp?): String {
    if (timestamp == null) return "Bilinmiyor"
    
    val now = System.currentTimeMillis()
    val newsTime = timestamp.toDate().time
    val diff = now - newsTime
    
    return when {
        diff < 60 * 1000 -> "Az önce"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} dakika önce"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} saat önce"
        diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)} gün önce"
        else -> {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            sdf.format(timestamp.toDate())
        }
    }
}
