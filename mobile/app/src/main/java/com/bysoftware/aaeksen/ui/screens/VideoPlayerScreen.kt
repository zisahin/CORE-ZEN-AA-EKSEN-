package com.bysoftware.aaeksen.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bysoftware.aaeksen.data.firebase.model.VideoComment
import com.bysoftware.aaeksen.data.firebase.model.VideoContent
import com.bysoftware.aaeksen.presentation.video.VideoPlayerViewModel
import com.bysoftware.aaeksen.presentation.video.VideoPlayerUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    videoId: String,
    navController: NavController,
    viewModel: VideoPlayerViewModel = hiltViewModel(),
    gamificationViewModel: com.bysoftware.aaeksen.presentation.gamification.GamificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val video by viewModel.currentVideo.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val context = LocalContext.current

    // ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    // Video yüklendiğinde player'ı başlat
    LaunchedEffect(video) {
        video?.let { videoContent ->
            val mediaItem = MediaItem.fromUri(videoContent.videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            
            // View count artır
            viewModel.incrementViewCount(videoId)
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(videoId) {
        viewModel.loadVideo(videoId)
        viewModel.loadComments(videoId)
        // Video izlendiğinde XP ver
        gamificationViewModel.onVideoWatched()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        video?.let { viewModel.shareVideo(it) }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Paylaş")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is VideoPlayerUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            is VideoPlayerUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (uiState as VideoPlayerUiState.Error).message,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadVideo(videoId) }) {
                            Text("Tekrar Dene")
                        }
                    }
                }
            }
            is VideoPlayerUiState.Success -> {
                video?.let { videoContent ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .padding(paddingValues)
                    ) {
                        // Video Player
                        item {
                            VideoPlayerView(
                                exoPlayer = exoPlayer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                            )
                        }

                        // Video Bilgileri
                        item {
                            VideoInfoSection(
                                video = videoContent,
                                onLikeClick = { viewModel.likeVideo(videoId) },
                                onShareClick = { 
                                    viewModel.shareVideo(videoContent)
                                    gamificationViewModel.onContentShared()
                                },
                                onDownloadClick = { viewModel.downloadVideo(videoContent) }
                            )
                        }

                        // Yorumlar Başlığı
                        item {
                            Text(
                                text = "Yorumlar (${comments.size})",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        // Yorum Ekleme
                        item {
                            CommentInputSection(
                                onCommentSubmit = { comment ->
                                    viewModel.addComment(videoId, comment)
                                }
                            )
                        }

                        // Yorumlar Listesi
                        items(comments) { comment ->
                            CommentItem(
                                comment = comment,
                                onReplyClick = { /* TODO: Yanıt özelliği */ },
                                onLikeClick = { /* TODO: Yorum beğeni */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoPlayerView(
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun VideoInfoSection(
    video: VideoContent,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Başlık
        Text(
            text = video.title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // İstatistikler
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${video.viewCount} görüntüleme",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = formatDate(video.createdAt),
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Aksiyon Butonları
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = Icons.Filled.ThumbUp,
                text = "${video.likeCount}",
                onClick = onLikeClick
            )
            ActionButton(
                icon = Icons.Filled.Share,
                text = "${video.shareCount}",
                onClick = onShareClick
            )
            ActionButton(
                icon = Icons.Filled.Download,
                text = "İndir",
                onClick = onDownloadClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Açıklama
        if (video.description.isNotEmpty()) {
            Text(
                text = video.description,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        // Transkripsiyon (varsa)
        if (video.transcription.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Transkripsiyon",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = video.transcription,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            onClick = onClick,
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun CommentInputSection(
    onCommentSubmit: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profil fotoğrafı placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Yorum input
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            placeholder = { 
                Text(
                    text = "Yorum ekle...",
                    color = Color.Gray
                ) 
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Gönder butonu
        IconButton(
            onClick = {
                if (commentText.isNotBlank()) {
                    onCommentSubmit(commentText)
                    commentText = ""
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Gönder",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CommentItem(
    comment: VideoComment,
    onReplyClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Profil fotoğrafı
        AsyncImage(
            model = comment.userPhotoUrl.ifEmpty { "https://via.placeholder.com/40" },
            contentDescription = "Profil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Kullanıcı adı ve tarih
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.username,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDate(comment.createdAt),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Yorum içeriği
            Text(
                text = comment.content,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Aksiyon butonları
            Row {
                TextButton(
                    onClick = onLikeClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ThumbUp,
                        contentDescription = "Beğen",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${comment.likeCount}",
                        fontSize = 12.sp
                    )
                }

                TextButton(
                    onClick = onReplyClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Text(
                        text = "Yanıtla",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return ""
    val date = timestamp.toDate()
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale("tr"))
    return formatter.format(date)
}
