package com.bysoftware.aaeksen.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bysoftware.aaeksen.R
import com.bysoftware.aaeksen.data.NewsItem
import com.bysoftware.aaeksen.presentation.newsdetail.NewsDetailUiState
import com.bysoftware.aaeksen.presentation.newsdetail.NewsDetailViewModel
import com.bysoftware.aaeksen.ui.theme.aa_color
import com.bysoftware.aaeksen.core.constants.MapboxConfig
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.getLayerAs
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.plugin.gestures.gestures

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: String,
    viewModel: NewsDetailViewModel,
    gamificationViewModel: com.bysoftware.aaeksen.presentation.gamification.GamificationViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    autoStartMapGuess: Boolean = false,
    autoStartQuiz: Boolean = false
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val firebaseNews by viewModel.news.collectAsState()
    
    // Harita tahmin oyunu state'leri
    var showMapGuessDialog by remember { mutableStateOf(false) }
    var useMapGuessContent by remember { mutableStateOf(autoStartMapGuess) }
    var hasShownMapGuessDialog by remember { mutableStateOf(autoStartMapGuess) }
    var showMapGuessGame by remember { mutableStateOf(autoStartMapGuess) }
    var selectedProvince by remember { mutableStateOf<String?>(null) }
    var gameResult by remember { mutableStateOf<Boolean?>(null) }
    var gameCompleted by remember { mutableStateOf(false) }
    
    // Quiz oyunu state'leri
    var showQuizDialog by remember { mutableStateOf(autoStartQuiz) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var showQuizResult by remember { mutableStateOf(false) }
    var quizScore by remember { mutableStateOf(0) }

    // Load news on first composition
    LaunchedEffect(newsId) {
        viewModel.loadNews(newsId)
        viewModel.updateXP(newsId)
        gamificationViewModel.onNewsRead()
    }
    
    // Harita tahmin oyunu popup'ını göster (sadece uygun haberlerde)
    LaunchedEffect(firebaseNews) {
        if (firebaseNews != null && 
            !hasShownMapGuessDialog && 
            !firebaseNews!!.mapGuessDetail.isNullOrBlank() &&
            !firebaseNews!!.location.isNullOrBlank()) {
            showMapGuessDialog = true
            hasShownMapGuessDialog = true
        }
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

                 // Article content - mapGuessDetail kullan eğer oyun aktifse
                Text(
                     text = if (useMapGuessContent && !news.mapGuessDetail.isNullOrBlank()) {
                         news.mapGuessDetail
                     } else {
                         newsItem.description
                     },
                    fontSize = 16.sp,
                    color = Color.Black,
                    lineHeight = 24.sp
                )
                 
                 // Harita Tahmin Oyunu (eğer aktifse) - Oyunlaştırılmış tasarım
                 if (showMapGuessGame && !news.location.isNullOrBlank()) {
                     Spacer(modifier = Modifier.height(24.dp))
                     
                     Card(
                         modifier = Modifier.fillMaxWidth(),
                         colors = CardDefaults.cardColors(
                             containerColor = Color(0xFF1A237E).copy(alpha = 0.05f) // Oyunsu arka plan
                         ),
                         elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Gölge kaldırıldı
                         shape = RoundedCornerShape(20.dp)
                     ) {
                         Column(
                             modifier = Modifier.padding(20.dp)
                         ) {
                             // Oyun başlığı - daha çekici
                             Row(
                                 modifier = Modifier.fillMaxWidth(),
                                 horizontalArrangement = Arrangement.SpaceBetween,
                                 verticalAlignment = Alignment.CenterVertically
                             ) {
                                 Row(
                                     verticalAlignment = Alignment.CenterVertically
                                 ) {
                                     Box(
                                         modifier = Modifier
                                             .size(40.dp)
                                             .background(
                                                 Color(0xFF4CAF50),
                                                 CircleShape
                                             ),
                                         contentAlignment = Alignment.Center
                                     ) {
                                         Text(
                                             text = "🎯",
                                             fontSize = 20.sp
                                         )
                                     }
                                     
                                     Spacer(modifier = Modifier.width(12.dp))
                                     
                                     Column {
                                         Text(
                                             text = "Harita Tahmin Oyunu",
                                             fontSize = 18.sp,
                                             fontWeight = FontWeight.Bold,
                                             color = aa_color
                                         )
                                         Text(
                                             text = "Hangi ilden geldiğini bul!",
                                             fontSize = 12.sp,
                                             color = Color.Gray
                                         )
                                     }
                                 }
                                 
                                 // XP badge
                                 Card(
                                     colors = CardDefaults.cardColors(
                                         containerColor = Color(0xFFFFD700)
                                     ),
                                     shape = RoundedCornerShape(12.dp)
                                 ) {
                                     Text(
                                         text = "+10 XP",
                                         modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                         fontSize = 12.sp,
                                         fontWeight = FontWeight.Bold,
                                         color = Color.Black
                                     )
                                 }
                             }

                Spacer(modifier = Modifier.height(16.dp))
                             
                             // Progress bar veya durum göstergesi
                             if (!gameCompleted) {
                                 Row(
                                     modifier = Modifier.fillMaxWidth(),
                                     verticalAlignment = Alignment.CenterVertically
                                 ) {
                                     Icon(
                                         Icons.Default.TouchApp,
                                         contentDescription = null,
                                         tint = Color(0xFF2196F3),
                                         modifier = Modifier.size(20.dp)
                                     )
                                     Spacer(modifier = Modifier.width(8.dp))
                                     Text(
                                         text = if (selectedProvince == null) "Haritadan bir il seçin" else "İl seçildi: $selectedProvince",
                                         fontSize = 14.sp,
                                         color = if (selectedProvince == null) Color.Gray else Color(0xFF2196F3),
                                         fontWeight = if (selectedProvince == null) FontWeight.Normal else FontWeight.Medium
                                     )
                                 }
                                 Spacer(modifier = Modifier.height(16.dp))
                             }
                             
                             // Mapbox Harita - oyunlaştırılmış çerçeve
                             Card(
                                 modifier = Modifier.fillMaxWidth(),
                                 colors = CardDefaults.cardColors(containerColor = Color.White),
                                 elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Gölge kaldırıldı
                                 shape = RoundedCornerShape(16.dp)
                             ) {
                                 MapGuessGameMap(
                                     correctProvince = news.location,
                                     selectedProvince = selectedProvince,
                                     gameResult = gameResult,
                                     gameCompleted = gameCompleted,
                                     onProvinceSelected = { province ->
                                         if (!gameCompleted) {
                                             selectedProvince = province
                                             val isCorrect = province.equals(news.location, ignoreCase = true)
                                             gameResult = isCorrect
                                             gameCompleted = true
                                             
                                             if (isCorrect) {
                                                 gamificationViewModel.addXP(10, "map_guess", "Harita tahmin oyunu")
                                             }
                                         }
                                     }
                                 )
                             }
                             
                             // Sonuç mesajı - daha oyunsu
                             if (gameCompleted && gameResult != null) {
                                 Spacer(modifier = Modifier.height(20.dp))
                                 
                                 Card(
                                     modifier = Modifier.fillMaxWidth(),
                                     colors = CardDefaults.cardColors(
                                         containerColor = if (gameResult == true) {
                                             Color(0xFF4CAF50).copy(alpha = 0.1f)
                                         } else {
                                             Color(0xFFE53935).copy(alpha = 0.1f)
                                         }
                                     ),
                                     shape = RoundedCornerShape(16.dp),
                                     elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Gölge kaldırıldı
                                 ) {
                                     Column(
                                         modifier = Modifier.padding(16.dp),
                                         horizontalAlignment = Alignment.CenterHorizontally
                                     ) {
                                         // Büyük emoji ve sonuç
                                         Text(
                                             text = if (gameResult == true) "🎉" else "😔",
                                             fontSize = 32.sp
                                         )
                                         
                                         Spacer(modifier = Modifier.height(8.dp))
                                         
                                         Text(
                                             text = if (gameResult == true) {
                                                 "Tebrikler!"
                                             } else {
                                                 "Yanlış Tahmin!"
                                             },
                                             fontSize = 18.sp,
                                             fontWeight = FontWeight.Bold,
                                             color = if (gameResult == true) Color(0xFF4CAF50) else Color(0xFFE53935)
                                         )
                                         
                                         Spacer(modifier = Modifier.height(4.dp))

                Text(
                                             text = if (gameResult == true) {
                                                 "Doğru tahmin ettiniz! +10 XP kazandınız 🏆"
                                             } else {
                                                 "Doğru cevap: ${news.location} 📍"
                                             },
                                             fontSize = 14.sp,
                                             color = Color.Gray,
                                             textAlign = TextAlign.Center
                                         )
                                         
                                         if (gameResult == true) {
                                             Spacer(modifier = Modifier.height(12.dp))
                                             
                                             // XP kazanç animasyonu
                                             Row(
                                                 verticalAlignment = Alignment.CenterVertically
                                             ) {
                                                 Icon(
                                                     Icons.Default.Star,
                                                     contentDescription = null,
                                                     tint = Color(0xFFFFD700),
                                                     modifier = Modifier.size(20.dp)
                                                 )
                                                 Spacer(modifier = Modifier.width(4.dp))
                                                 Text(
                                                     text = "+10 XP",
                                                     fontSize = 16.sp,
                                                     fontWeight = FontWeight.Bold,
                                                     color = Color(0xFFFFD700)
                                                 )
                                                 Spacer(modifier = Modifier.width(4.dp))
                                                 Icon(
                                                     Icons.Default.Star,
                                                     contentDescription = null,
                                                     tint = Color(0xFFFFD700),
                                                     modifier = Modifier.size(20.dp)
                                                 )
                                             }
                                         }
                                     }
                                 }
                             }
                         }
                     }
                 }
                 
                /* // Çengel Bulmaca Oyunu (eğer kelimeler varsa)
                 if (showMapGuessGame && news.crosswordWords.isNotEmpty()) {
                     Spacer(modifier = Modifier.height(24.dp))
                     
                     CrosswordGameSection(
                         words = news.crosswordWords,
                         onWordFound = { word ->
                             gamificationViewModel.addXP(5, "crossword", "Kelime bulma oyunu")
                         }
                     )
                 }*/

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
                    // Favori butonu
                    FloatingActionButton(
                        onClick = { /* TODO: Favori işlemi */ },
                        containerColor = Color(0xFFf9f9f9),
                        contentColor = Color.Black,
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(28.dp),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favori",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                    }

                        Spacer(modifier = Modifier.width(10.dp))

                    // AI Asistan butonu
                        FloatingActionButton(
                        onClick = { /* TODO: AI Asistan işlemi */ },
                            containerColor = Color(0xFFf9f9f9),
                        contentColor = Color.Black,
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(28.dp),
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                        ) {
                            Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "AI Asistan",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // TTS-Seslendirme butonu
                    FloatingActionButton(
                        onClick = { /* TODO: TTS işlemi */ },
                        containerColor = Color(0xFF2d59fd),
                        contentColor = Color.White,
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(28.dp),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.play),
                            contentDescription = "TTS-Seslendirme",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Quiz butonu
                    FloatingActionButton(
                        onClick = { 
                            if (firebaseNews?.quizQuestions?.isNotEmpty() == true) {
                                showQuizDialog = true
                                currentQuestionIndex = 0
                                selectedAnswerIndex = null
                                showQuizResult = false
                                quizScore = 0
                            }
                        },
                        containerColor = Color(0xFFf9f9f9),
                        contentColor = Color.Black,
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(28.dp),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.game),
                            contentDescription = "Quiz",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
    
    // Harita Tahmin Oyunu Dialog'u
    if (showMapGuessDialog) {
        MapGuessGameDialog(
            onAccept = {
                useMapGuessContent = true
                showMapGuessGame = true
                showMapGuessDialog = false
            },
            onDecline = {
                showMapGuessDialog = false
            }
        )
    }

    // Quiz Oyunu Dialog'u
    if (showQuizDialog) {
        QuizGameDialog(
            quizQuestions = news.quizQuestions,
            onDismiss = { showQuizDialog = false },
            onQuizCompleted = { correctAnswers, totalQuestions ->
                // Quiz tamamlandığında XP ver
                val xpPerQuestion = 5
                val totalXP = correctAnswers * xpPerQuestion
                gamificationViewModel.addXP(totalXP, "quiz", "Haber quiz oyunu")
                showQuizDialog = false
            }
        )
    }
}

@Composable
fun MapGuessGameDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDecline,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Harita Tahmin Oyunu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = aa_color
                )
            }
        },
        text = {
            Text(
                text = "Bu haberi harita tahmin oyunumuzla deneyimlemek ister misiniz? Haberin hangi ilden geldiğini tahmin ederek XP kazanabilirsiniz!",
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(containerColor = aa_color)
            ) {
                Text("Evet, Oynayalım!", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDecline) {
                Text("Hayır", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun MapGuessGameMap(
    correctProvince: String,
    selectedProvince: String?,
    gameResult: Boolean?,
    gameCompleted: Boolean,
    onProvinceSelected: (String) -> Unit
) {
    val context = LocalContext.current
    
    // Türkiye'nin 5 rastgele ili (doğru cevap dahil)
    val provinces = remember {
        val allProvinces = listOf(
            "İstanbul", "Ankara", "İzmir", "Bursa", "Antalya", "Adana", "Konya", "Gaziantep",
            "Şanlıurfa", "Kocaeli", "Mersin", "Diyarbakır", "Hatay", "Manisa", "Kayseri",
            "Samsun", "Balıkesir", "Kahramanmaraş", "Van", "Aydın", "Denizli", "Muğla",
            "Tekirdağ", "Sakarya", "Trabzon", "Ordu", "Malatya", "Erzurum", "Elazığ"
        )
        
        val randomProvinces = allProvinces.filter { it != correctProvince }.shuffled().take(4).toMutableList()
        randomProvinces.add(correctProvince)
        randomProvinces.shuffled()
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp) // Biraz daha yüksek
            .clip(RoundedCornerShape(16.dp)) // Daha yumuşak köşeler
    ) {
        // Mapbox Harita - State değişikliklerini dinleyen
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    val mapView = this
                    
                    // Çalışan style URL'ini kullan
                    mapboxMap.loadStyle(MapboxConfig.STYLE_URL) { style ->
                        setupProvinceData(style, provinces, correctProvince, selectedProvince, gameResult, gameCompleted)
                    }
                    
                    // Başlangıç pozisyonunu ayarla - hafif zoom
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(MapboxConfig.TURKEY_CENTER_LNG, MapboxConfig.TURKEY_CENTER_LAT))
                            .zoom(MapboxConfig.TURKEY_ZOOM -0.2) // Hafif zoom ekle
                            .build()
                    )
                    
                    // Tıklama event handler - çalışan kodu kullan
                    gestures.addOnMapClickListener { point ->
                        if (!gameCompleted) {
                            val nearestProvince = findNearestProvince(point, provinces)
                            nearestProvince?.let { onProvinceSelected(it) }
                            return@addOnMapClickListener true
                        }
                        false
                    }
                }
            },
            update = { mapView ->
                // State değişikliklerinde haritayı güncelle
                mapView.mapboxMap.getStyle { style ->
                    setupProvinceData(style, provinces, correctProvince, selectedProvince, gameResult, gameCompleted)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Ana harita ekranındaki setupCityData'dan uyarlandı
private fun setupProvinceData(
    style: Style,
    provinces: List<String>,
    correctProvince: String,
    selectedProvince: String?,
    gameResult: Boolean?,
    gameCompleted: Boolean
) {
    try {
        val citiesLayerId = "cities" // Style Editor'daki layer ismi
        
        // Renk için expression oluştur
        val colorExpressionCases = mutableListOf<Expression>()
        
        provinces.forEach { province ->
            val color = when {
                // Oyun bitti ve bu doğru cevap - parlak yeşil
                gameCompleted && province == correctProvince -> Color(0xFF00E676) // Neon yeşil - doğru cevap
                // Oyun bitti ve bu yanlış seçim - parlak kırmızı
                gameCompleted && province == selectedProvince && gameResult == false -> Color(0xFFFF1744) // Neon kırmızı - yanlış seçim
                // Oyun devam ediyor ve bu seçili il - parlak mavi
                province == selectedProvince && !gameCompleted -> Color(0xFF00B0FF) // Neon mavi - seçili
                // Seçilebilir iller (oyun başlamadan) - oyunsu mor/pembe
                selectedProvince == null && provinces.contains(province) -> Color(0xFF7C4DFF) // Mor - seçilebilir
                // Diğer seçilebilir iller (biri seçildikten sonra) - açık mor
                provinces.contains(province) -> Color(0xFFB39DDB) // Açık mor - diğer seçenekler
                else -> Color.Gray.copy(alpha = 0.2f) // Çok açık gri - seçilemez
            }
            
            colorExpressionCases.add(Expression.eq(Expression.get("name"), Expression.literal(province)))
            colorExpressionCases.add(Expression.color(color.toArgb()))
        }
        
        val colorExpression = Expression.switchCase(
            *colorExpressionCases.toTypedArray(),
            Expression.color(Color.Gray.copy(alpha = 0.3f).toArgb()) // Default renk
        )
        
        // Layer'ı FillLayer olarak bul ve güncelle
        val citiesLayer = style.getLayerAs<FillLayer>(citiesLayerId)
        if (citiesLayer != null) {
            citiesLayer.fillColor(colorExpression)
            citiesLayer.fillOutlineColor(Color.Black.toArgb())
            citiesLayer.fillOpacity(0.8)
            println("'$citiesLayerId' (FillLayer) katmanı başarıyla güncellendi.")
        } else {
            println("'$citiesLayerId' katmanı bulunamadı. Mevcut katmanlar kontrol ediliyor...")
            style.styleLayers.forEach { layerInfo ->
                println("Mevcut katman: ${layerInfo.id} - Tipi: ${layerInfo.type}")
                if (layerInfo.type == "fill") {
                    style.getLayerAs<FillLayer>(layerInfo.id)?.let { layer ->
                        println("'${layerInfo.id}' (FillLayer) katmanı güncelleniyor...")
                        layer.fillColor(colorExpression)
                        layer.fillOutlineColor(Color.Black.toArgb())
                        layer.fillOpacity(0.8)
                    }
                }
            }
        }
    } catch (e: Exception) {
        println("Stil ayarlanırken hata oluştu: ${e.message}")
        e.printStackTrace()
    }
}

// Ana harita ekranındaki findNearestCity'den uyarlandı
private fun findNearestProvince(clickPoint: Point, provinces: List<String>): String? {
    if (provinces.isEmpty()) return null
    
    // Basit koordinat eşleştirmesi - gerçek uygulamada il koordinatları olmalı
    val provinceCoordinates = mapOf(
        "İstanbul" to Pair(41.0082, 28.9784),
        "Ankara" to Pair(39.9334, 32.8597),
        "İzmir" to Pair(38.4192, 27.1287),
        "Bursa" to Pair(40.1826, 29.0665),
        "Antalya" to Pair(36.8969, 30.7133),
        "Adana" to Pair(37.0000, 35.3213),
        "Konya" to Pair(37.8667, 32.4833),
        "Gaziantep" to Pair(37.0662, 37.3833),
        "Şanlıurfa" to Pair(37.1674, 38.7955),
        "Kocaeli" to Pair(40.8533, 29.8815),
        "Mersin" to Pair(36.8000, 34.6333),
        "Diyarbakır" to Pair(37.9144, 40.2306),
        "Hatay" to Pair(36.4018, 36.3498),
        "Manisa" to Pair(38.6191, 27.4289),
        "Kayseri" to Pair(38.7312, 35.4787),
        "Samsun" to Pair(41.2928, 36.3313),
        "Balıkesir" to Pair(39.6484, 27.8826),
        "Kahramanmaraş" to Pair(37.5858, 36.9371),
        "Van" to Pair(38.4891, 43.4089),
        "Aydın" to Pair(37.8560, 27.8416),
        "Denizli" to Pair(37.7765, 29.0864),
        "Muğla" to Pair(37.2153, 28.3636),
        "Tekirdağ" to Pair(40.9833, 27.5167),
        "Sakarya" to Pair(40.6940, 30.4358),
        "Trabzon" to Pair(41.0015, 39.7178),
        "Ordu" to Pair(40.9839, 37.8764),
        "Malatya" to Pair(38.3552, 38.3095),
        "Erzurum" to Pair(39.9000, 41.2700),
        "Elazığ" to Pair(38.6810, 39.2264)
    )
    
    return provinces.minByOrNull { province ->
        val coords = provinceCoordinates[province]
        if (coords != null) {
            calculateDistance(
                clickPoint.latitude(),
                clickPoint.longitude(),
                coords.first,
                coords.second
            )
        } else {
            Double.MAX_VALUE
        }
    }
}

private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // Earth radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return earthRadius * c
}

@Composable
fun CrosswordGameSection(
    words: List<String>,
    onWordFound: (String) -> Unit
) {
    var foundWords by remember { mutableStateOf(setOf<String>()) }
    var selectedLetters by remember { mutableStateOf(setOf<Int>()) }
    var currentWord by remember { mutableStateOf("") }
    
    // Kelimelerden harfler oluştur (basit grid)
    val allLetters = remember {
        val letters = mutableListOf<Char>()
        words.forEach { word ->
            letters.addAll(word.uppercase().toList())
        }
        // Rastgele harfler ekle
        val randomLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZÇĞIİÖŞÜ".toList().shuffled().take(20)
        letters.addAll(randomLetters)
        letters.shuffled()
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E8) // Açık yeşil arka plan
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Başlık
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFF4CAF50),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🧩",
                            fontSize = 20.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Çengel Bulmaca",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = aa_color
                        )
                        Text(
                            text = "Kelimeleri bulun!",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                // İlerleme badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${foundWords.size}/${words.size}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mevcut kelime göstergesi
            if (currentWord.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Seçilen: $currentWord",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2196F3)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Harf grid'i (6x6)
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(36) { index ->
                    val letter = if (index < allLetters.size) allLetters[index] else 'A'
                    val isSelected = selectedLetters.contains(index)
                    
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable(
                                onClick = {
                                    if (isSelected) {
                                        selectedLetters = selectedLetters - index
                                        currentWord = selectedLetters
                                            .sorted()
                                            .map { allLetters.getOrNull(it) ?: 'A' }
                                            .joinToString("")
                                    } else {
                                        selectedLetters = selectedLetters + index
                                        currentWord = selectedLetters
                                            .sorted()
                                            .map { allLetters.getOrNull(it) ?: 'A' }
                                            .joinToString("")
                                        
                                        // Kelime kontrolü
                                        if (words.any { it.uppercase() == currentWord.uppercase() } && 
                                            !foundWords.contains(currentWord.uppercase())) {
                                            foundWords = foundWords + currentWord.uppercase()
                                            onWordFound(currentWord)
                                            selectedLetters = setOf()
                                            currentWord = ""
                                        }
                                    }
                                },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isSelected -> Color(0xFF2196F3)
                                else -> Color.White
                            }
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bulunan kelimeler
            if (foundWords.isNotEmpty()) {
                Text(
                    text = "Bulunan Kelimeler:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(foundWords.toList()) { word ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = word,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            // Temizle butonu
            if (selectedLetters.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        selectedLetters = setOf()
                        currentWord = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Temizle", color = Color.White)
                }
            }
            
            // Tamamlanma mesajı
            if (foundWords.size == words.size) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉",
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Tebrikler!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "Tüm kelimeleri buldunuz! +${words.size * 5} XP",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizGameDialog(
    quizQuestions: List<com.bysoftware.aaeksen.data.firebase.model.QuizQuestion>,
    onDismiss: () -> Unit,
    onQuizCompleted: (correctAnswers: Int, totalQuestions: Int) -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var correctAnswers by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    var isAnswered by remember { mutableStateOf(false) }

    val currentQuestion = if (quizQuestions.isNotEmpty() && currentQuestionIndex < quizQuestions.size) {
        quizQuestions[currentQuestionIndex]
    } else null

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🧠 Quiz Oyunu",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = aa_color
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress
                Text(
                    text = "Soru ${currentQuestionIndex + 1}/${quizQuestions.size}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1).toFloat() / quizQuestions.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = aa_color,
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (currentQuestion != null) {
                    // Soru
                    Text(
                        text = currentQuestion.question,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Seçenekler
                    currentQuestion.options.forEachIndexed { index, option ->
                        val isSelected = selectedAnswerIndex == index
                        val isCorrect = index == currentQuestion.correctAnswerIndex
                        val backgroundColor = when {
                            !isAnswered -> if (isSelected) aa_color.copy(alpha = 0.1f) else Color.Transparent
                            isCorrect -> Color.Green.copy(alpha = 0.2f)
                            isSelected && !isCorrect -> Color.Red.copy(alpha = 0.2f)
                            else -> Color.Transparent
                        }
                        val borderColor = when {
                            !isAnswered -> if (isSelected) aa_color else Color.Gray.copy(alpha = 0.3f)
                            isCorrect -> Color.Green
                            isSelected && !isCorrect -> Color.Red
                            else -> Color.Gray.copy(alpha = 0.3f)
                        }

                         Card(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .padding(vertical = 4.dp)
                                 .clickable(
                                     enabled = !isAnswered,
                                     indication = null,
                                     interactionSource = remember { MutableInteractionSource() }
                                 ) {
                                     selectedAnswerIndex = index
                                 },
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${'A' + index})",
                                    fontWeight = FontWeight.Bold,
                                    color = borderColor,
                                    modifier = Modifier.padding(end = 12.dp)
                                )

                                Text(
                                    text = option,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                if (isAnswered) {
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Default.Check else
                                            if (isSelected) Icons.Default.Close else Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (isCorrect) Color.Green else
                                            if (isSelected) Color.Red else Color.Transparent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (!isAnswered) {
                        // Cevapla butonu
                        Button(
                            onClick = {
                                if (selectedAnswerIndex != null) {
                                    isAnswered = true
                                    if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) {
                                        correctAnswers++
                                    }
                                    showResult = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedAnswerIndex != null,
                            colors = ButtonDefaults.buttonColors(containerColor = aa_color)
                        ) {
                            Text("Cevapla", color = Color.White)
                        }
                    } else {
                        // Sonuç ve açıklama
                        if (showResult) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex)
                                        Color.Green.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex)
                                            "🎉 Doğru!" else "❌ Yanlış!",
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex)
                                            Color.Green else Color.Red
                                    )

                                    if (!currentQuestion.explanation.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = currentQuestion.explanation!!,
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "+${currentQuestion.xpPoints} XP",
                                            fontSize = 12.sp,
                                            color = Color.Green,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Sonraki soru veya bitir butonu
                        if (currentQuestionIndex < quizQuestions.size - 1) {
                            Button(
                                onClick = {
                                    currentQuestionIndex++
                                    selectedAnswerIndex = null
                                    isAnswered = false
                                    showResult = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                            ) {
                                Text("Sonraki Soru", color = Color.White)
                            }
                        } else {
                            Button(
                                onClick = {
                                    onQuizCompleted(correctAnswers, quizQuestions.size)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = aa_color)
                            ) {
                                Text("Quiz'i Bitir", color = Color.White)
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Quiz soruları bulunamadı.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

