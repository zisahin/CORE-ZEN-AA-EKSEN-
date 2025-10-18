package com.bysoftware.aaeksen.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bysoftware.aaeksen.data.CategoryTab
import com.bysoftware.aaeksen.data.NewsItem
import com.bysoftware.aaeksen.data.firebase.model.FirebaseNews
import com.bysoftware.aaeksen.presentation.home.HomeUiState
import com.bysoftware.aaeksen.presentation.home.HomeViewModel
import com.bysoftware.aaeksen.ui.components.*
import com.bysoftware.aaeksen.ui.components.CategoryTabRow
import com.bysoftware.aaeksen.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onNewsClick: (NewsItem) -> Unit,
    onAIChatClick: () -> Unit = {},
    onVideoListClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onVideoGeneratorClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    var selectedCategory by remember { mutableStateOf("All") }
    
    val uiState by viewModel.uiState.collectAsState()
    val breakingNews by viewModel.breakingNews.collectAsState()
    val recommendedNews by viewModel.recommendedNews.collectAsState()
    val dailyAIQuestion by viewModel.dailyAIQuestion.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAIChatClick,
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White,
                modifier = Modifier
                    .padding(
                        bottom = 72.dp // alt bar boşluğu bırak
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.ai_assistant),
                    contentDescription = "AI Assistant",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End, // sağ alt
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

            TopBar(
                onProfileClick = onProfileClick,
                onVideoGeneratorClick = onVideoGeneratorClick
            )

            when (uiState) {
                is HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp) // FAB için extra padding
                    ) {
                        // Breaking News Section
                        if (breakingNews.isNotEmpty()) {
                            item {
                                Column {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Breaking News",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        
                                        Text(
                                            text = "Show More",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 13.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(breakingNews) { news ->
                                            BreakingNewsCard(
                                                newsItem = news.toNewsItem(),
                                                onClick = { onNewsClick(news.toNewsItem()) },
                                                modifier = Modifier.width(360.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Category Tabs
                        item {
                            val categories = listOf(
                                CategoryTab("All", "Tümü"),
                                CategoryTab("Gündem", "Gündem"),
                                CategoryTab("Teknoloji", "Teknoloji"),
                                CategoryTab("Kültür", "Kültür"),
                                CategoryTab("Tarım", "Tarım"),
                                CategoryTab("Turizm", "Turizm"),
                                CategoryTab("Spor", "Spor"),
                                CategoryTab("Ekonomi", "Ekonomi"),
                                CategoryTab("Sağlık", "Sağlık")
                            )

                            CategoryTabRow(
                                categories = categories.map { 
                                    it.copy(isSelected = it.name == selectedCategory) 
                                },
                                onCategorySelected = { categoryName ->
                                    selectedCategory = categoryName
                                    if (categoryName == "All") {
                                        viewModel.loadNews()
                                    } else {
                                        viewModel.loadNewsByCategory(categoryName)
                                    }
                                }
                            )
                        }
                        
                        // Recommended Section
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Recommended for you",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                
                                Text(
                                    text = "Show More",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        
                        // Recommended News List with AI Survey Card
                        itemsIndexed(recommendedNews) { index, news ->
                            // AI Survey Card'ını 3. haberin sonrasına ekle
                            if (index == 3 && dailyAIQuestion != null) {
                                AISurveyCard(
                                    question = dailyAIQuestion!!,
                                    onAnswerSelected = { selectedOption ->
                                        viewModel.submitAIAnswer(dailyAIQuestion!!.id, selectedOption)
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                RecommendedNewsCard(
                                    newsItem = news.toNewsItem(),
                                    onClick = { onNewsClick(news.toNewsItem()) }
                                )
                            }
                        }
                    }
                }
                 is HomeUiState.Error -> {
                    // Hata durumunda boş bir ekran göstererek kullanıcıya hata yansıtmıyoruz.
                    // Hatalar log'lara yazılıyor.
                     Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

private fun FirebaseNews.toNewsItem(): NewsItem {
    return NewsItem(
        id = id,
        title = title,
        description = content,
        imageUrl = imageUrl,
        source = author ?: "Anadolu Ajansı",
        date = "Az önce",
        category = category,
        breaking = breaking,
        readTime = null,
        author = author,
        likes = likeCount.toInt(),
        comments = 0
    )
}
