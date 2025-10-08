package com.bysoftware.aaeksen.ui.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bysoftware.aaeksen.core.constants.MapboxConfig
import com.bysoftware.aaeksen.domain.model.City
import com.bysoftware.aaeksen.domain.model.IntensityLevel
import com.bysoftware.aaeksen.domain.model.NewsCategory
import com.bysoftware.aaeksen.presentation.newsmap.NewsMapViewModel
import com.bysoftware.aaeksen.ui.theme.aa_color
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
import androidx.compose.ui.graphics.toArgb

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsMapScreen(
    onCategoryNewsClick: (String, String) -> Unit = { _, _ -> },
    viewModel: NewsMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Haber Haritası") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = aa_color,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            
            // Mapbox haritası
            MapboxNewsMap(
                cities = uiState.cities,
                onCityClick = viewModel::onCityClick,
                getCityIntensityLevel = viewModel::getCityIntensityLevel,
                modifier = Modifier.fillMaxSize()
            )
            
            // Renk yoğunluk göstergesi
            NewsIntensityLegend(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
            
            // Loading indicator
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = aa_color
                )
            }
            
            // Bottom Sheet
            AnimatedVisibility(
                visible = uiState.selectedCity != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                uiState.selectedCity?.let { city ->
                    NewsCategoryBottomSheet(
                        cityName = city.name,
                        categories = uiState.newsCategories,
                        onCategoryClick = { category ->
                            viewModel.onCategoryClick(city, category)
                            onCategoryNewsClick(city.id, category.id)
                        },
                        onDismiss = viewModel::dismissBottomSheet
                    )
                }
            }
        }
    }
}

@Composable
fun MapboxNewsMap(
    cities: List<City>,
    onCityClick: (City) -> Unit,
    getCityIntensityLevel: (City) -> IntensityLevel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                val mapView = this
                
                // Stili yükle
                mapboxMap.loadStyle(MapboxConfig.STYLE_URL) { style ->
                    setupCityData(style, cities, getCityIntensityLevel)
                    
                    // Stil yüklendikten sonra, haritanın tamamen render edilmesini bekle
                    mapView.post {
                        try {
                            // O anki kamera durumundan visible region'ı al
                            val cameraState = mapboxMap.cameraState
                            val currentBounds = mapboxMap.coordinateBoundsForCamera(
                                CameraOptions.Builder()
                                    .center(cameraState.center)
                                    .zoom(cameraState.zoom)
                                    .bearing(cameraState.bearing)
                                    .pitch(cameraState.pitch)
                                    .build()
                            )
                            
                            // Bu sınırları haritanın yeni gezinme alanı olarak ata
                           /* val cameraBoundsOptions = CameraBoundsOptions.Builder()
                                .bounds(currentBounds)
                                .build()
                            mapboxMap.setBounds(cameraBoundsOptions)*/
                        } catch (e: Exception) {
                            println("Harita sınırları ayarlanamadı: ${e.message}")
                        }
                    }
                }
                
                // Başlangıç pozisyonunu ayarla
                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(MapboxConfig.TURKEY_CENTER_LNG, MapboxConfig.TURKEY_CENTER_LAT))
                        .zoom(MapboxConfig.TURKEY_ZOOM)
                        .build()
                )
                
                // Tıklama event handler
                gestures.addOnMapClickListener { point ->
                    val nearestCity = findNearestCity(point, cities)
                    nearestCity?.let { onCityClick(it) }
                    true
                }
            }
        },
        modifier = modifier
    )
}

private fun findNearestCity(clickPoint: Point, cities: List<City>): City? {
    if (cities.isEmpty()) return null
    
    return cities.minByOrNull { city ->
        val distance = calculateDistance(
            clickPoint.latitude(),
            clickPoint.longitude(),
            city.coordinates.first,
            city.coordinates.second
        )
        distance
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

private fun setupCityData(
    style: Style,
    cities: List<City>,
    getCityIntensityLevel: (City) -> IntensityLevel
) {
    try {
        val citiesLayerId = "cities" // Style Editor'daki layer ismi

        // Renk için expression oluştur
        val colorExpressionCases = mutableListOf<Expression>()
        cities.forEach { city ->
            val intensity = getCityIntensityLevel(city)
            val color = when (intensity) {
                IntensityLevel.LOW -> Color(0xFF4FC3F7)
                IntensityLevel.MEDIUM -> Color(0xFF29B6F6)
                IntensityLevel.HIGH -> Color(0xFFFFB74D)
                IntensityLevel.VERY_HIGH -> Color(0xFFE53935)
            }
            colorExpressionCases.add(Expression.eq(Expression.get("name"), Expression.literal(city.name)))
            colorExpressionCases.add(Expression.color(color.toArgb()))
        }
        val colorExpression = Expression.switchCase(
            *colorExpressionCases.toTypedArray(),
            Expression.color(Color.Gray.copy(alpha = 0.5f).toArgb()) // Default renk
        )

        // Layer'ı FillLayer olarak bul ve güncelle
        val citiesLayer = style.getLayerAs<FillLayer>(citiesLayerId)
        if (citiesLayer != null) {
            citiesLayer.fillColor(colorExpression)
            citiesLayer.fillOutlineColor(Color.Black.toArgb())
            citiesLayer.fillOpacity(0.7)
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
                        layer.fillOpacity(0.7)
                    }
                }
            }
        }
    } catch (e: Exception) {
        println("Stil ayarlanırken hata oluştu: ${e.message}")
        e.printStackTrace()
    }
}

// Bottom Sheet Component
@Composable
fun NewsCategoryBottomSheet(
    cityName: String,
    categories: List<NewsCategory>,
    onCategoryClick: (NewsCategory) -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Başlık
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$cityName Haber Kategorileri",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kapat",
                        tint = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Kategori listesi
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    NewsCategoryItem(
                        category = category,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }
    }
}

@Composable
fun NewsCategoryItem(
    category: NewsCategory,
    onClick: () -> Unit
) {
    val icon = getIconFromString(category.icon)
    val color = try {
        Color(android.graphics.Color.parseColor(category.color))
    } catch (e: Exception) {
        aa_color
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onClick()  },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Kategori ikonu
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = color.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = category.name,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Kategori adı
            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            // İleri ok
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Git",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Renk göstergesi
@Composable
fun NewsIntensityLegend(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Haber Yoğunluğu",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Renk çubuğu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val colors = listOf(
                    Color(0xFF4FC3F7), // Açık mavi
                    Color(0xFF29B6F6), // Mavi  
                    Color(0xFFFFB74D), // Turuncu
                    Color(0xFFE53935)  // Kırmızı
                )
                
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(16.dp, 4.dp)
                            .background(color)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.width(80.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Az",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Çok",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// Icon helper function
private fun getIconFromString(iconName: String): ImageVector {
    return when (iconName) {
        "article" -> Icons.Default.Article
        "account_balance" -> Icons.Default.AccountBalance
        "sports" -> Icons.Default.Sports
        "gavel" -> Icons.Default.Gavel
        "trending_up" -> Icons.Default.TrendingUp
        "computer" -> Icons.Default.Computer
        else -> Icons.Default.Article
    }
}
