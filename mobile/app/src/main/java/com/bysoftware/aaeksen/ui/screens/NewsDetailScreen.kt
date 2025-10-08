package com.bysoftware.aaeksen.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bysoftware.aaeksen.R
import com.bysoftware.aaeksen.data.NewsItem
import com.bysoftware.aaeksen.presentation.newsdetail.NewsDetailUiState
import com.bysoftware.aaeksen.presentation.newsdetail.NewsDetailViewModel
import com.bysoftware.aaeksen.ui.theme.aa_color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: String,
    viewModel: NewsDetailViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val firebaseNews by viewModel.news.collectAsState()
    
    // Load news on first composition
    LaunchedEffect(newsId) {
        viewModel.loadNews(newsId)
        viewModel.updateXP(newsId)
    }
    
    when (uiState) {
        is NewsDetailUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }
        is NewsDetailUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = (uiState as NewsDetailUiState.Error).message,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadNews(newsId) }) {
                        Text("Tekrar Dene")
                    }
                }
            }
            return
        }
        else -> {
            // Continue with success state
        }
    }
    
    val news = firebaseNews ?: return
    
    // Convert FirebaseNews to NewsItem for UI
    val newsItem = NewsItem(
        id = news.id,
        title = news.title,
        description = news.content,
        imageUrl = news.imageUrl,
        source = news.author ?: "Anadolu Ajansı",
        date = "Az önce",
        category = news.category,
        breaking = news.breaking,
        readTime = null,
        author = news.author,
        likes = news.likeCount.toInt(),
        comments = 0
    )

    Scaffold(
        topBar = {
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
                    IconButton(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, news.newsUrl.ifBlank { "https://www.aa.com.tr" })
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box( // <-- Burası önemli
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // İçerik
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Source and category info
                Row(
                    modifier = Modifier
                        .background(
                            Color.Red,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CNN",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = newsItem.source,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = newsItem.date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = newsItem.category,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = newsItem.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Main Image
                AsyncImage(
                    model = newsItem.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Damage of tornadoes",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Article content
                Text(
                    text = newsItem.description,
                    fontSize = 16.sp,
                    color = Color.Black,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.padding(16.dp))

                /* Spacer(modifier = Modifier.height(32.dp))

             // Action buttons
             Row(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalArrangement = Arrangement.SpaceBetween,
                 verticalAlignment = Alignment.CenterVertically
             ) {
                 Row(
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     IconButton(onClick = { }) {
                         Icon(
                             imageVector = Icons.Default.FavoriteBorder,
                             contentDescription = "Like",
                             tint = Color.Gray
                         )
                     }
                     Text(
                         text = "6.7k",
                         fontSize = 14.sp,
                         color = Color.Gray
                     )

                     Spacer(modifier = Modifier.width(16.dp))

                     IconButton(onClick = { }) {
                         Icon(
                             imageVector = Icons.Default.ChatBubble,
                             contentDescription = "Comments",
                             tint = Color.Gray
                         )
                     }
                     Text(
                         text = "12k",
                         fontSize = 14.sp,
                         color = Color.Gray
                     )
                 }

                 FloatingActionButton(
                     onClick = { },
                     containerColor = Color(0xFF2563EB),
                     contentColor = Color.White,
                     modifier = Modifier.size(48.dp)
                 ) {
                     Icon(
                         imageVector = Icons.Default.MoreVert,
                         contentDescription = "More",
                         modifier = Modifier.size(24.dp)
                     )
                 }
             }*/
                Spacer(modifier = Modifier.padding(20.dp))
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter) // ✅ Bu sadece Box içinde çalışır
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Like section
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { /* TODO: Like işlemi */ }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "6.7k",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Comments section
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        /*  IconButton(onClick = { /* TODO: Comment işlemi */ }) {
                          Icon(
                              imageVector = Icons.Default.ChatBubble,
                              contentDescription = "Comments",
                              tint = Color.Gray,
                              modifier = Modifier.size(24.dp)
                          )
                      }*/


                        Spacer(modifier = Modifier.width(10.dp))

                        FloatingActionButton(
                            onClick = { /* TODO: Audio işlemi */ },
                            containerColor = Color(0xFFf9f9f9),
                            contentColor = Color.White,
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(28.dp),
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.game),
                                tint = Color.Black,
                                contentDescription = "Comments",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "12k",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Audio/Headset button
                    FloatingActionButton(
                        onClick = { /* TODO: Audio işlemi */ },
                        containerColor = Color(0xFF2d59fd),
                        contentColor = Color.White,
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(28.dp),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.play),
                            contentDescription = "Audio",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
}