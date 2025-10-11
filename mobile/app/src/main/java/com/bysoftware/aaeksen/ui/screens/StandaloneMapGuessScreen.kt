package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bysoftware.aaeksen.core.constants.MapboxConfig
import com.bysoftware.aaeksen.presentation.gamification.GamificationViewModel
import com.bysoftware.aaeksen.ui.theme.aa_color
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.gestures

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandaloneMapGuessScreen(
    navController: NavController,
    gamificationViewModel: GamificationViewModel = hiltViewModel()
) {
    var currentScore by remember { mutableStateOf(0) }
    var currentRound by remember { mutableStateOf(1) }
    var selectedProvince by remember { mutableStateOf<String?>(null) }
    var gameResult by remember { mutableStateOf<Boolean?>(null) }
    var gameCompleted by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    // Rastgele haber ve doğru il
    val currentNews = remember(currentRound) {
        val sampleNews = listOf(
            MapGuessNews("İstanbul'da büyük proje açıklandı", "İstanbul"),
            MapGuessNews("Ankara'da önemli toplantı yapıldı", "Ankara"),
            MapGuessNews("İzmir'de festival düzenlendi", "İzmir"),
            MapGuessNews("Bursa'da yeni fabrika açıldı", "Bursa"),
            MapGuessNews("Antalya'da turizm rekoru kırıldı", "Antalya")
        )
        sampleNews.random()
    }

    val totalRounds = 5

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Harita Tahmin",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Skor göstergesi
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Skor: $currentScore",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = aa_color,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Oyun bilgileri
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🗺️ Harita Tahmin",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = aa_color
                            )
                            Text(
                                text = "Round $currentRound/$totalRounds",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        
                        // XP badge
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "⭐ ${currentScore * 10} XP",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // İlerleme çubuğu
                    LinearProgressIndicator(
                        progress = { currentRound.toFloat() / totalRounds.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = aa_color,
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Haber kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📰",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bu haber hangi ilden?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = aa_color
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = currentNews.censoredContent,
                        fontSize = 16.sp,
                        color = Color.Black,
                        lineHeight = 22.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Harita
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🗺️ Haritadan il seçin:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = aa_color
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Mapbox Harita
                    StandaloneMapGuessMap(
                        correctProvince = currentNews.correctProvince,
                        selectedProvince = selectedProvince,
                        gameResult = gameResult,
                        gameCompleted = gameCompleted,
                        onProvinceSelected = { province ->
                            if (!gameCompleted) {
                                selectedProvince = province
                                gameResult = province == currentNews.correctProvince
                                gameCompleted = true
                                showResult = true
                                
                                if (gameResult == true) {
                                    currentScore++
                                }
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sonuç ve devam butonu
            if (showResult) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (gameResult == true) 
                            Color(0xFF4CAF50).copy(alpha = 0.1f) 
                        else 
                            Color(0xFFF44336).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (gameResult == true) "🎉 Doğru!" else "❌ Yanlış!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (gameResult == true) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                        
                        Text(
                            text = "Doğru cevap: ${currentNews.correctProvince}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        
                        if (gameResult == true) {
                            Text(
                                text = "+10 XP",
                                fontSize = 12.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (currentRound < totalRounds) {
                            Button(
                                onClick = {
                                    currentRound++
                                    selectedProvince = null
                                    gameResult = null
                                    gameCompleted = false
                                    showResult = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = aa_color),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Sonraki Round", color = Color.White)
                            }
                        } else {
                            Button(
                                onClick = {
                                    // Oyun tamamlandı - XP ver
                                    val totalXP = currentScore * 10
                                    gamificationViewModel.addXP(totalXP, "map_guess", "Harita tahmin oyunu")
                                    navController.popBackStack()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Oyunu Bitir", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StandaloneMapGuessMap(
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
            .height(300.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
    ) {
        // Mapbox Harita
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    mapboxMap.loadStyle(MapboxConfig.STYLE_URL) { style ->
                        // Harita stilini yükle
                    }

                    // Başlangıç pozisyonunu ayarla
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(MapboxConfig.TURKEY_CENTER_LNG, MapboxConfig.TURKEY_CENTER_LAT))
                            .zoom(MapboxConfig.TURKEY_ZOOM - 0.2)
                            .build()
                    )

                    // Tıklama event handler
                    gestures.addOnMapClickListener { point ->
                        if (!gameCompleted) {
                            val nearestProvince = findNearestProvinceForStandalone(point, provinces)
                            nearestProvince?.let { onProvinceSelected(it) }
                            return@addOnMapClickListener true
                        }
                        false
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // İl seçenekleri (harita üzerinde)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            provinces.forEach { province ->
                val isSelected = selectedProvince == province
                val isCorrect = province == correctProvince
                val backgroundColor = when {
                    !gameCompleted -> if (isSelected) aa_color else Color.White
                    isCorrect -> Color(0xFF4CAF50)
                    isSelected && !isCorrect -> Color(0xFFF44336)
                    else -> Color.White
                }
                val textColor = when {
                    !gameCompleted -> if (isSelected) Color.White else Color.Black
                    isCorrect || (isSelected && !isCorrect) -> Color.White
                    else -> Color.Black
                }
                
                Card(
                    modifier = Modifier
                        .padding(vertical = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = province,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}

// Basit il koordinatları (gerçek koordinatlar kullanılabilir)
fun findNearestProvinceForStandalone(point: Point, provinces: List<String>): String? {
    // Basit rastgele seçim - gerçek uygulamada koordinat hesaplaması yapılır
    return provinces.random()
}

data class MapGuessNews(
    val originalContent: String,
    val correctProvince: String
) {
    val censoredContent: String
        get() = originalContent.replace(correctProvince, "***")
}
