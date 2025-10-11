package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bysoftware.aaeksen.data.NewsItem
import com.bysoftware.aaeksen.data.firebase.model.FirebaseNews
import com.bysoftware.aaeksen.presentation.timetunnel.TimeTunnelUiState
import com.bysoftware.aaeksen.presentation.timetunnel.TimeTunnelViewModel
import com.bysoftware.aaeksen.ui.components.TimeTunnelItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeTunnelDetailScreen(
    navController: NavController,
    categoryId: String,
    viewModel: TimeTunnelViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val newsList by viewModel.newsList.collectAsState()

    LaunchedEffect(key1 = categoryId) {
        viewModel.loadTimeTunnelNews(categoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zaman Tüneli Detayı") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Arama Çubuğu
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Zaman tünelinde ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            when(uiState) {
                is TimeTunnelUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is TimeTunnelUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(newsList.filter { it.title.contains(searchQuery, ignoreCase = true) }) { newsItem ->
                            TimeTunnelItem(newsItem = newsItem.toNewsItem()) {
                                navController.navigate("news_detail/${newsItem.id}")
                            }
                        }
                    }
                }
                is TimeTunnelUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = (uiState as TimeTunnelUiState.Error).message, color = Color.Red)
                    }
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
        source = author,
        date = publishedAt?.toDate()?.toString() ?: "",
        category = category,
        author = author,
        breaking = breaking,
        likes = likeCount.toInt()
    )
}

@Preview
@Composable
fun TimeTunnelDetailScreenPreview() {
    TimeTunnelDetailScreen(navController = rememberNavController(), categoryId = "1")
}
