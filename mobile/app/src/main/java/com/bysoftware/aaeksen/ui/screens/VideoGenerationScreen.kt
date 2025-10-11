package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bysoftware.aaeksen.data.firebase.model.*
import com.bysoftware.aaeksen.presentation.video.VideoGenerationViewModel
import com.bysoftware.aaeksen.presentation.video.VideoGenerationUiState
import com.bysoftware.aaeksen.ui.theme.AAEksenTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoGenerationScreen(
    navController: NavController,
    viewModel: VideoGenerationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val requests by viewModel.generationRequests.collectAsState()
    val availableNews by viewModel.availableNews.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserRequests()
        viewModel.loadAvailableNews()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Oluştur") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF01447b),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showNewsSelectionDialog() },
                containerColor = Color(0xFF01447b)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Yeni Video Oluştur",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            when (uiState) {
                is VideoGenerationUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF01447b))
                    }
                }
                is VideoGenerationUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (uiState as VideoGenerationUiState.Error).message,
                                color = Color.Red,
                                textAlign = TextAlign.Center
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
                is VideoGenerationUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Başlık
                        item {
                            Text(
                                text = "Video Oluşturma İstekleri",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        // İstekler listesi
                        if (requests.isEmpty()) {
                            item {
                                EmptyStateCard()
                            }
                        } else {
                            items(requests) { request ->
                                VideoGenerationRequestCard(
                                    request = request,
                                    onCancelClick = { 
                                        viewModel.cancelRequest(request.id) 
                                    },
                                    onRetryClick = { 
                                        viewModel.retryRequest(request.id) 
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Haber seçimi dialog'u
        if (viewModel.showNewsDialog.collectAsState().value) {
            NewsSelectionDialog(
                newsList = availableNews,
                onNewsSelected = { news ->
                    viewModel.createVideoRequest(news.id)
                },
                onDismiss = { viewModel.hideNewsSelectionDialog() }
            )
        }

        // Video yapılandırma dialog'u
        if (viewModel.showConfigDialog.collectAsState().value) {
            VideoConfigDialog(
                config = viewModel.currentConfig.collectAsState().value,
                onConfigChanged = { config ->
                    viewModel.updateConfig(config)
                },
                onConfirm = { 
                    viewModel.confirmVideoCreation() 
                },
                onDismiss = { viewModel.hideConfigDialog() }
            )
        }
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
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
                text = "Henüz video oluşturma isteğiniz yok",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sağ alttaki + butonuna tıklayarak yeni bir video oluşturma isteği başlatabilirsiniz",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun VideoGenerationRequestCard(
    request: VideoGenerationRequest,
    onCancelClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Başlık ve durum
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Video İsteği #${request.id.take(8)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                StatusChip(status = request.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Haber ID
            Text(
                text = "Haber ID: ${request.newsId}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            // Mevcut adım (eğer işlem devam ediyorsa)
            if (request.currentStep.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mevcut Adım: ${request.currentStep}",
                    fontSize = 14.sp,
                    color = Color(0xFF01447b)
                )
            }

            // İlerleme çubuğu
            if (request.status == VideoGenerationStatus.PROCESSING ||
                request.status == VideoGenerationStatus.TRANSCRIBING ||
                request.status == VideoGenerationStatus.GENERATING ||
                request.status == VideoGenerationStatus.UPLOADING) {
                
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = request.progress / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF01447b)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "%${request.progress}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Hata mesajı (varsa)
            if (request.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hata: ${request.errorMessage}",
                    fontSize = 14.sp,
                    color = Color.Red
                )
            }

            // Aksiyon butonları
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (request.status) {
                    VideoGenerationStatus.PENDING,
                    VideoGenerationStatus.PROCESSING,
                    VideoGenerationStatus.TRANSCRIBING,
                    VideoGenerationStatus.GENERATING,
                    VideoGenerationStatus.UPLOADING -> {
                        OutlinedButton(
                            onClick = onCancelClick,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Text("İptal Et")
                        }
                    }
                    VideoGenerationStatus.FAILED -> {
                        Button(
                            onClick = onRetryClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF01447b)
                            )
                        ) {
                            Text("Tekrar Dene", color = Color.White)
                        }
                    }
                    VideoGenerationStatus.COMPLETED -> {
                        Button(
                            onClick = { /* Navigate to video */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF01447b)
                            )
                        ) {
                            Text("Videoyu İzle", color = Color.White)
                        }
                    }
                    VideoGenerationStatus.CANCELLED -> {
                        // Hiçbir aksiyon butonu gösterme
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: VideoGenerationStatus) {
    val (text, color) = when (status) {
        VideoGenerationStatus.PENDING -> "Beklemede" to Color(0xFFFFA726)
        VideoGenerationStatus.PROCESSING -> "İşleniyor" to Color(0xFF42A5F5)
        VideoGenerationStatus.TRANSCRIBING -> "Transkripsiyon" to Color(0xFF42A5F5)
        VideoGenerationStatus.GENERATING -> "Oluşturuluyor" to Color(0xFF42A5F5)
        VideoGenerationStatus.UPLOADING -> "Yükleniyor" to Color(0xFF42A5F5)
        VideoGenerationStatus.COMPLETED -> "Tamamlandı" to Color(0xFF66BB6A)
        VideoGenerationStatus.FAILED -> "Başarısız" to Color(0xFFEF5350)
        VideoGenerationStatus.CANCELLED -> "İptal Edildi" to Color(0xFF9E9E9E)
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun NewsSelectionDialog(
    newsList: List<com.bysoftware.aaeksen.data.firebase.model.FirebaseNews>,
    onNewsSelected: (com.bysoftware.aaeksen.data.firebase.model.FirebaseNews) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Haber Seçin") },
        text = {
            LazyColumn(
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(newsList) { news ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = { onNewsSelected(news) },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = news.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = news.category,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun VideoConfigDialog(
    config: VideoGenerationConfig,
    onConfigChanged: (VideoGenerationConfig) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Video Ayarları") },
        text = {
            Column {
                // Kalite seçimi
                Text("Video Kalitesi:", fontWeight = FontWeight.Medium)
                Row {
                    VideoQuality.values().forEach { quality ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                onClick = { onConfigChanged(config.copy(quality = quality)) },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                        ) {
                            RadioButton(
                                selected = config.quality == quality,
                                onClick = { onConfigChanged(config.copy(quality = quality)) }
                            )
                            Text(quality.name)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Arka plan müziği
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = config.includeBackgroundMusic,
                        onCheckedChange = { 
                            onConfigChanged(config.copy(includeBackgroundMusic = it))
                        }
                    )
                    Text("Arka plan müziği ekle")
                }

                // Altyazı
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = config.includeSubtitles,
                        onCheckedChange = { 
                            onConfigChanged(config.copy(includeSubtitles = it))
                        }
                    )
                    Text("Altyazı ekle")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF01447b)
                )
            ) {
                Text("Oluştur", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun VideoGenerationScreenPreview() {
    AAEksenTheme {
        VideoGenerationScreen(navController = rememberNavController())
    }
}
