package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bysoftware.aaeksen.data.firebase.model.VideoContent
import com.bysoftware.aaeksen.data.firebase.model.VideoPlaylist
import com.bysoftware.aaeksen.presentation.video.VideoListViewModel
import com.bysoftware.aaeksen.presentation.video.VideoListUiState
import com.bysoftware.aaeksen.ui.theme.AAEksenTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    navController: NavController,
    viewModel: VideoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val featuredVideos by viewModel.featuredVideos.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadContent()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Videolar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        navController.navigate("video_generation")
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Video Oluştur")
                    }
                    IconButton(onClick = { 
                        navController.navigate("video_search")
                    }) {
                        Icon(Icons.Filled.Search, contentDescription = "Ara")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF01447b),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is VideoListUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF01447b))
                }
            }
                is VideoListUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (uiState as VideoListUiState.Error).message,
                                color = Color.Red
                            )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.refresh() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF01447b)
                            )
                        ) {
                            Text("Tekrar Dene", color = Color.White)
                        }
                    }
                }
            }
            is VideoListUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Öne Çıkan Videolar
                    if (featuredVideos.isNotEmpty()) {
                        item {
                            FeaturedVideosSection(
                                videos = featuredVideos,
                                onVideoClick = { video ->
                                    navController.navigate("video_player/${video.id}")
                                }
                            )
                        }
                    }

                    // Oynatma Listeleri
                    if (playlists.isNotEmpty()) {
                        item {
                            PlaylistsSection(
                                playlists = playlists,
                                onPlaylistClick = { playlist ->
                                    navController.navigate("playlist/${playlist.id}")
                                }
                            )
                        }
                    }

                    // Kategori Filtreleri
                    item {
                        CategoryFilters(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { category ->
                                viewModel.selectCategory(category)
                            }
                        )
                    }

                    // Video Listesi
                    item {
                        Text(
                            text = if (selectedCategory.isEmpty()) "Tüm Videolar" else selectedCategory,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    if (videos.isEmpty()) {
                        item {
                            EmptyVideoState()
                        }
                    } else {
                        items(videos) { video ->
                            VideoListItem(
                                video = video,
                                onClick = { 
                                    navController.navigate("video_player/${video.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedVideosSection(
    videos: List<VideoContent>,
    onVideoClick: (VideoContent) -> Unit
) {
    Column {
        Text(
            text = "Öne Çıkan Videolar",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(videos) { video ->
                FeaturedVideoCard(
                    video = video,
                    onClick = { onVideoClick(video) }
                )
            }
        }
    }
}

@Composable
fun FeaturedVideoCard(
    video: VideoContent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = video.thumbnailUrl.ifEmpty { "https://via.placeholder.com/280x160" },
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Duration overlay
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = formatDuration(video.duration),
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            // Video bilgileri
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = video.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${video.viewCount} görüntüleme • ${formatDate(video.createdAt)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun PlaylistsSection(
    playlists: List<VideoPlaylist>,
    onPlaylistClick: (VideoPlaylist) -> Unit
) {
    Column {
        Text(
            text = "Oynatma Listeleri",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(playlists) { playlist ->
                PlaylistCard(
                    playlist = playlist,
                    onClick = { onPlaylistClick(playlist) }
                )
            }
        }
    }
}

@Composable
fun PlaylistCard(
    playlist: VideoPlaylist,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = playlist.thumbnailUrl.ifEmpty { "https://via.placeholder.com/200x120" },
                    contentDescription = playlist.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Video count overlay
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlaylistPlay,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${playlist.videoCount}",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            // Playlist bilgileri
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = playlist.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = playlist.creatorName,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CategoryFilters(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        "Tümü" to "",
        "Siyaset" to "Siyaset",
        "Ekonomi" to "Ekonomi", 
        "Spor" to "Spor",
        "Gündem" to "Gündem",
        "Teknoloji" to "Teknoloji"
    )
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (displayName, categoryValue) ->
            FilterChip(
                selected = selectedCategory == categoryValue,
                onClick = { onCategorySelected(categoryValue) },
                label = { Text(displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF01447b),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun VideoListItem(
    video: VideoContent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = video.thumbnailUrl.ifEmpty { "https://via.placeholder.com/120x68" },
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Duration
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    Text(
                        text = formatDuration(video.duration),
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Video bilgileri
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = video.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = video.category,
                    fontSize = 12.sp,
                    color = Color(0xFF01447b)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${video.viewCount} görüntüleme • ${formatDate(video.createdAt)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // More options
            IconButton(
                onClick = { /* TODO: Show options menu */ }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Seçenekler",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun EmptyVideoState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.VideoLibrary,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Henüz video yok",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Video oluşturma özelliğini kullanarak ilk videonuzu oluşturun",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}

private fun formatDate(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return ""
    val date = timestamp.toDate()
    val formatter = SimpleDateFormat("dd MMM", Locale("tr"))
    return formatter.format(date)
}

@Preview(showBackground = true)
@Composable
fun VideoListScreenPreview() {
    AAEksenTheme {
        VideoListScreen(navController = rememberNavController())
    }
}
