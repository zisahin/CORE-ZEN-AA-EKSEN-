package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bysoftware.aaeksen.data.NewsItem
import com.bysoftware.aaeksen.data.firebase.model.TimeTunnelCategory
import com.bysoftware.aaeksen.presentation.timetunnel.TimeTunnelUiState
import com.bysoftware.aaeksen.presentation.timetunnel.TimeTunnelViewModel
import com.bysoftware.aaeksen.ui.components.RecommendedNewsCard
import com.bysoftware.aaeksen.ui.components.TopBar

@Composable
fun TimeTunnelCategoriesScreen(
    navController: NavController,
    viewModel: TimeTunnelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar()

        when (uiState) {
            is TimeTunnelUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TimeTunnelUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, bottom = 70.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Tüm Zaman Tünelleri",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    items(categories) { category ->
                        RecommendedNewsCard(
                            newsItem = category.toNewsItem(),
                            onClick = {
                                navController.navigate("time_tunnel_detail/${category.id}")
                            }
                        )
                    }
                }
            }
            is TimeTunnelUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState as TimeTunnelUiState.Error).message,
                        color = Color.Red
                    )
                }
            }
        }
    }
}

private fun TimeTunnelCategory.toNewsItem(): NewsItem {
    return NewsItem(
        id = id,
        title = title,
        description = description,
        imageUrl = coverImageUrl,
        source = "Zaman Tüneli",
        date = "Kronoloji",
        category = "Tarih"
    )
}
