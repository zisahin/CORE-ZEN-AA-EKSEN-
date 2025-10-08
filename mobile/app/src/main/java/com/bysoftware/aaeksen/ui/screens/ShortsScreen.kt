package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.bysoftware.aaeksen.core.utils.YouTubeExtractor
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.bysoftware.aaeksen.domain.model.VideoInteractionType
import com.bysoftware.aaeksen.domain.model.VideoPlaybackState
import com.bysoftware.aaeksen.domain.model.VideoShort
import com.bysoftware.aaeksen.presentation.shorts.ShortsUiState
import com.bysoftware.aaeksen.presentation.shorts.ShortsViewModel
import com.bysoftware.aaeksen.ui.theme.aa_color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@Composable
fun ShortsScreen(
    onNavigateToNews: (String) -> Unit = {},
    onShareVideo: (String) -> Unit = {},
    viewModel: ShortsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val videos = viewModel.videosFlow.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val hapticFeedback = LocalHapticFeedback.current
    
    val systemUiController = rememberSystemUiController()
    
    // Set system bars to transparent for full screen experience
    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = false
        )
    }
    
    // Handle side effects
    LaunchedEffect(uiState.shareVideoId) {
        uiState.shareVideoId?.let { videoId ->
            onShareVideo(videoId)
            viewModel.clearShareAction()
        }
    }
    
    LaunchedEffect(uiState.navigateToNewsId) {
        uiState.navigateToNewsId?.let { videoId ->
            onNavigateToNews(videoId)
            viewModel.clearNavigationAction()
        }
    }
    
    // Auto-play current video based on scroll position
    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (videos.itemCount > 0) {
            val currentVideo = videos[listState.firstVisibleItemIndex]
            currentVideo?.let { video ->
                viewModel.onVideoChanged(video)
            }
        }
    }
    
    // Handle programmatic scrolling
    LaunchedEffect(uiState.scrollToIndex) {
        uiState.scrollToIndex?.let { targetIndex ->
            if (targetIndex >= 0 && targetIndex < videos.itemCount) {
                listState.animateScrollToItem(targetIndex)
            }
            viewModel.clearScrollAction()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            videos.loadState.refresh is LoadState.Loading -> {
                // Initial loading
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = aa_color,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Videolar yükleniyor...",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            
            videos.loadState.refresh is LoadState.Error -> {
                // Error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorScreen(
                        error = (videos.loadState.refresh as LoadState.Error).error.message ?: "Bilinmeyen hata",
                        onRetry = { videos.retry() }
                    )
                }
            }
            
            videos.itemCount > 0 -> {
                // Video list with paging
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(
                        count = videos.itemCount,
                        key = videos.itemKey { it.id }
                    ) { index ->
                        val video = videos[index]
                        if (video != null) {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxSize() // Bu her video'nun tam ekranı kaplamasını sağlar
                            ) {
                                VideoPlayerScreen(
                                    video = video,
                                    playbackState = playbackState,
                                    isCurrentVideo = index == listState.firstVisibleItemIndex,
                                    onVideoInteraction = { interactionType ->
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.onVideoInteraction(video.id, interactionType)
                                    },
                                    onTogglePlayPause = viewModel::togglePlayPause,
                                    onSwipeUp = {
                                        if (index < videos.itemCount - 1) {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            // Trigger scroll to next video (will be handled by LaunchedEffect)
                                            viewModel.onScrollToVideo(index + 1)
                                        }
                                    },
                                    onSwipeDown = {
                                        if (index > 0) {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            // Trigger scroll to previous video (will be handled by LaunchedEffect)
                                            viewModel.onScrollToVideo(index - 1)
                                        }
                                    }
                                )
                            }
                        } else {
                            // Placeholder for loading item
                            VideoLoadingPlaceholder()
                        }
                    }
                    
                    // Loading indicator for pagination
                    if (videos.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = aa_color,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            else -> {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = "No videos",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Henüz video yok",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Daha sonra tekrar deneyin",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        
        // Global error handling
        uiState.error?.let { error ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorScreen(
                    error = error,
                    onRetry = { viewModel.clearError() }
                )
            }
        }
    }
}

@Composable
private fun VideoPlayerScreen(
    video: VideoShort,
    playbackState: VideoPlaybackState,
    isCurrentVideo: Boolean = true,
    onVideoInteraction: (VideoInteractionType) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit
) {
    var swipeThreshold by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { swipeThreshold = 0f },
                    onDragEnd = {
                        when {
                            swipeThreshold < -150 -> onSwipeUp() // Swipe up threshold
                            swipeThreshold > 150 -> onSwipeDown() // Swipe down threshold
                        }
                        swipeThreshold = 0f
                    }
                ) { _, dragAmount ->
                    swipeThreshold += dragAmount.y
                }
            }
    ) {
        // YouTube Player API Integration
        var isPlaying by remember { mutableStateOf(false) }
        var showPlayer by remember { mutableStateOf(false) }
        var showControls by remember { mutableStateOf(true) }
        var youTubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
        
        // Auto-hide controls after 3 seconds
        LaunchedEffect(showControls) {
            if (showControls && isPlaying) {
                delay(3000)
                showControls = false
            }
        }
        
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (showPlayer && isCurrentVideo) {
                // YouTube Player API - Büyük Boyut
                AndroidView(
                    factory = { context ->
                        YouTubePlayerView(context).apply {
                            enableAutomaticInitialization = false
                            
                            // Player boyutunu büyüt
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            
                            // Player'ı daha büyük yapmak için scale ayarları
                            scaleX = 1.2f  // %20 daha geniş
                            scaleY = 1.2f  // %20 daha yüksek
                            
                            initialize(object : AbstractYouTubePlayerListener() {
                                override fun onReady(player: YouTubePlayer) {
                                    val videoId = YouTubeExtractor.extractVideoId(video.videoUrl) ?: return
                                    player.loadVideo(videoId, 0f)
                                    player.play()
                                    isPlaying = true
                                    youTubePlayer = player
                                }
                                
                                override fun onStateChange(player: YouTubePlayer, state: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState) {
                                    when (state) {
                                        com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.PLAYING -> {
                                            isPlaying = true
                                            showControls = false
                                        }
                                        com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.PAUSED -> {
                                            isPlaying = false
                                            showControls = true
                                        }
                                        com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.ENDED -> {
                                            isPlaying = false
                                            // Video bitti, sonrakine geç
                                            onSwipeUp()
                                        }
                                        else -> {}
                                    }
                                }
                                
                                override fun onError(player: YouTubePlayer, error: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError) {
                                    // Hata durumunda thumbnail'e geri dön
                                    showPlayer = false
                                    isPlaying = false
                                }
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.1f) // Compose scale modifier ile %10 daha büyük
                )
                
                // Video overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f))
                )
            } else {
                // Video thumbnail with play overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            onClick = { showControls = !showControls },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                ) {
                    // Video thumbnail background
                    AsyncImage(
                        model = video.thumbnailUrl.ifEmpty { "https://via.placeholder.com/400x600/1a1a1a/ffffff?text=Video" },
                        contentDescription = video.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Dark overlay for better contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                    )
                    
                    // Center play button
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                onClick = {
                                    showPlayer = true
                                    onTogglePlayPause()
                                },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Original play button design
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.6f),
                                    CircleShape
                                )
                                .shadow(12.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Video",
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                    /*
                    // YouTube branding
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .background(
                                Color.Red,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "YouTube",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }*/
                }
            }
            
            // Overlay controls when video is playing
            if (showPlayer && showControls) {
                // Semi-transparent overlay for controls
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(
                            onClick = {
                                if (isPlaying) {
                                    youTubePlayer?.pause()
                                } else {
                                    youTubePlayer?.play()
                                }
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isPlaying) {
                        // Pause overlay
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.6f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }
            /*
            // YouTube branding and external link
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clickable {
                        // Open in YouTube app with proper intent
                        try {
                            val youtubeIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(video.videoUrl))
                            youtubeIntent.setPackage("com.google.android.youtube")
                            context.startActivity(youtubeIntent)
                        } catch (e: Exception) {
                            // Fallback to browser
                            val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(video.videoUrl))
                            context.startActivity(browserIntent)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.Red, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "YouTube",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "YouTube'da İzle",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }*/
            
            // Video quality indicator
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "HD",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // Right side interaction buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp).padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Like button
            VideoInteractionButton(
                icon = if (video.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                text = formatCount(video.likeCount),
                isActive = video.isLiked,
                onClick = { onVideoInteraction(VideoInteractionType.LIKE) }
            )
            
            // Dislike button
            VideoInteractionButton(
                icon = if (video.isDisliked) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                text = "",
                isActive = video.isDisliked,
                onClick = { onVideoInteraction(VideoInteractionType.DISLIKE) }
            )
            
            // Go to news button
            VideoInteractionButton(
                icon = Icons.Default.Article,
                text = "Haber",
                isActive = false,
                onClick = { onVideoInteraction(VideoInteractionType.GO_TO_NEWS) }
            )
            
            // Share button
            VideoInteractionButton(
                icon = Icons.Default.Share,
                text = "Paylaş",
                isActive = false,
                onClick = { onVideoInteraction(VideoInteractionType.SHARE) }
            )
            
            // Save button
            VideoInteractionButton(
                icon = if (video.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                text = "",
                isActive = video.isSaved,
                onClick = { onVideoInteraction(VideoInteractionType.SAVE) }
            )
        }
        
        // Bottom video info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .padding(bottom = 60.dp)
                .fillMaxWidth(0.7f) // Leave space for interaction buttons
        ) {
            // Channel info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // Channel avatar placeholder
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(aa_color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AA",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = video.channelTitle,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Video title
            Text(
                text = video.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
            
            // Video stats
            if (video.viewCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${formatCount(video.viewCount)} görüntüleme",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun VideoInteractionButton(
    icon: ImageVector,
    text: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
                onClick = { onClick()  },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) aa_color.copy(alpha = 0.3f) else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isActive) aa_color else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        
        if (text.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}

private fun formatCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}

/**
 * Helper function to format time in MM:SS format
 */
private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}

/**
 * YouTube URL'den video ID'sini çıkarır
 * Örnek: https://www.youtube.com/watch?v=dQw4w9WgXcQ -> dQw4w9WgXcQ
 */
private fun extractYouTubeVideoId(url: String): String {
    return try {
        when {
            url.contains("youtube.com/watch?v=") -> {
                url.substringAfter("watch?v=").substringBefore("&")
            }
            url.contains("youtu.be/") -> {
                url.substringAfter("youtu.be/").substringBefore("?")
            }
            url.contains("youtube.com/embed/") -> {
                url.substringAfter("embed/").substringBefore("?")
            }
            else -> {
                // Fallback: assume the URL already contains just the video ID
                url.substringAfterLast("/").substringBefore("?")
            }
        }
    } catch (e: Exception) {
        // Fallback video ID if parsing fails
        "dQw4w9WgXcQ"
    }
}

@Composable
private fun ErrorScreen(
    error: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Bir hata oluştu",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = aa_color)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tekrar Dene", color = Color.White)
            }
        }
    }
}

@Composable
private fun VideoLoadingPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = aa_color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Video yükleniyor...",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShortsScreenPreview() {
    // Sahte UI state
    val mockVideo = VideoShort(
        id = "1",
        title = "Preview video başlığı",
        thumbnailUrl = "",
        likeCount = 1234,
        viewCount = 9876,
        channelTitle = "AA Haber",
        isLiked = false,
        isDisliked = false,
        isSaved = false,
        description = "",
        videoUrl = "",
        duration = "",
        publishedAt = LocalDateTime.now(),
        tags = emptyList(),
        channelId = "1",
    )

    // Dummy state oluştur
    val mockUiState = ShortsUiState()
    val mockPlaybackState = VideoPlaybackState(isPlaying = false)

    // Kısaltılmış basit sürüm: doğrudan Box göster
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        VideoPlayerScreen(
            video = mockVideo,
            playbackState = mockPlaybackState,
            onVideoInteraction = {},
            onTogglePlayPause = {},
            onSwipeUp = {},
            onSwipeDown = {}
        )
    }
}


