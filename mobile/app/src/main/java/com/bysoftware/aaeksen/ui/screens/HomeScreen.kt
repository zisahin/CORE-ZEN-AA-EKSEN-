package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bysoftware.aaeksen.data.CategoryTab
import com.bysoftware.aaeksen.data.NewsItem
import com.bysoftware.aaeksen.data.SampleData
import com.bysoftware.aaeksen.ui.components.*

@Composable
fun HomeScreen(
    onNewsClick: (NewsItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar()
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Breaking News Section
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
                    
                    // Horizontal scrollable breaking news list
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 13.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(SampleData.breakingNews) { newsItem ->
                            BreakingNewsCard(
                                newsItem = newsItem,
                                onClick = { onNewsClick(newsItem) },
                                modifier = Modifier.width(360.dp) // Kart genişliği - bir sonrakinin bir kısmı görünsün
                            )
                        }
                    }
                }
            }
            
            // Category Tabs
            item {
                CategoryTabRow(
                    categories = SampleData.categories.map { 
                        it.copy(isSelected = it.name == selectedCategory) 
                    },
                    onCategorySelected = { selectedCategory = it }
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
            
            // Recommended News List
            items(SampleData.recommendedNews) { newsItem ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    RecommendedNewsCard(
                        newsItem = newsItem,
                        onClick = { onNewsClick(newsItem) }
                    )
                }
            }
        }
    }
}

