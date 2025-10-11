package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bysoftware.aaeksen.R
import com.bysoftware.aaeksen.data.firebase.model.FirebaseUser
import com.bysoftware.aaeksen.data.firebase.model.UserBadge
import com.bysoftware.aaeksen.data.firebase.model.UserProfile
import com.bysoftware.aaeksen.presentation.profile.ProfileUiState
import com.bysoftware.aaeksen.presentation.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    gamificationViewModel: com.bysoftware.aaeksen.presentation.gamification.GamificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user by viewModel.user.collectAsState()
    val userProfile by gamificationViewModel.userProfile.collectAsState()
    val userBadges by gamificationViewModel.userBadges.collectAsState()
    val userStats by gamificationViewModel.userStats.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                actions = {
                    IconButton(onClick = { /* Ayarlar */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (uiState as ProfileUiState.Error).message,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Tekrar Dene")
                        }
                    }
                }
            }
            is ProfileUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .verticalScroll(scrollState)
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Profil Bilgileri
                    ProfileHeader(user = user, viewModel = viewModel, navController = navController)

                    Spacer(modifier = Modifier.height(16.dp))

                    // XP ve Seviye Kartı
                    userProfile?.let { profile ->
                        XPLevelCard(userProfile = profile, gamificationViewModel = gamificationViewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // İstatistikler Kartı
                    userProfile?.let { profile ->
                        StatsCard(userProfile = profile)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Rozetler Bölümü (Gamification)
                    GamificationBadgesSection(userBadges = userBadges)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Günlük Görevler Kartı
                    DailyTasksCard(navController = navController)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Orijinal Rozetler Bölümü (Firebase'den)
                    BadgesSection(user = user, viewModel = viewModel)

                    Spacer(modifier = Modifier.height(24.dp))

                    // İlerleme İstatistikleri
                    ProgressSection(user = user, viewModel = viewModel)

                    Spacer(modifier = Modifier.height(100.dp)) // Alt navigasyon için boşluk
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    user: FirebaseUser?,
    viewModel: ProfileViewModel,
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profil Fotoğrafı
        Box(
            modifier = Modifier.size(80.dp)
        ) {
            AsyncImage(
                model = user?.photoUrl?.ifBlank { "https://images.unsplash.com/photo-1494790108755-2616b332c913?w=150&h=150&fit=crop&crop=face" },
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Kullanıcı Adı ve Premium Badge
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user?.username ?: "Kullanıcı",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (user?.isPremium == true) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "👑",
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Üyelik Süresi ve Email
        Text(
            text = user?.email ?: "user@example.com",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Kullanıcı İstatistikleri
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "${user?.newsReadCount ?: 0} Haber Okundu",
                fontSize = 14.sp,
                color = Color(0xFF2563EB)
            )
            Text(
                text = "•",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "${user?.newsSharedCount ?: 0} Paylaşım",
                fontSize = 14.sp,
                color = Color(0xFF2563EB)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Giriş Yap ve Premium Butonları
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF01447b)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Giriş Yap",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = { /* Premium */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (user?.isPremium == true) Color(0xFFFFD700) else Color(0xFFE3F2FD)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (user?.isPremium == true) "Premium" else "Premium Ol",
                    color = if (user?.isPremium == true) Color.Black else Color(0xFF2563EB),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun BadgesSection(
    user: FirebaseUser?,
    viewModel: ProfileViewModel
) {
    val earnedCount = viewModel.getEarnedBadgesCount()
    val totalCount = viewModel.getTotalBadgesCount()

    Column {
        // Başlık
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
        Text(
            text = "Rozetler $earnedCount/$totalCount",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
            Text(
                text = "Tümünü Gör >",
                fontSize = 14.sp,
                color = Color(0xFF2563EB),
                modifier = Modifier.clickable(
                    onClick = { /* Tüm rozetler */ },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rozet Listesi
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(user?.badges?.take(3) ?: emptyList()) { badge ->
                BadgeItem(badge = badge)
            }
            
            // Eğer rozet yoksa örnek rozetler göster
            if (user?.badges?.isEmpty() != false) {
                items(getSampleBadges()) { badge ->
                    BadgeItem(badge = badge)
                }
            }
        }
    }
}

@Composable
fun BadgeItem(badge: UserBadge) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        // Rozet İkonu
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    when (badge.rarity) {
                        "rare" -> Color(0xFFFFD700)
                        "epic" -> Color(0xFF9C27B0)
                        "legendary" -> Color(0xFFFF5722)
                        else -> Color(0xFF2196F3)
                    },
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = badge.iconUrl.ifBlank { "https://images.unsplash.com/photo-1614680376573-df3480f0c6ff?w=100&h=100&fit=crop" },
                contentDescription = badge.name,
                modifier = Modifier.size(40.dp)
            )
            
            // "New" badge
            if (badge.name.contains("Confident")) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp)
                        .background(
                            Color(0xFFFFD700),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "New",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Rozet Adı
        Text(
            text = badge.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
fun ProgressSection(
    user: FirebaseUser?,
    viewModel: ProfileViewModel
) {
    Column {
        Text(
            text = "Genel İlerleme",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // İstatistik Kartları
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // İlk satır
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Okunan\nHaber",
                    value = (user?.newsReadCount ?: 0).toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Paylaşılan\nHaber",
                    value = (user?.newsSharedCount ?: 0).toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            // İkinci satır
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Kazanılan\nRozet",
                    value = (user?.badges?.size ?: 0).toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Günlük\nGörevler",
                    value = "${user?.completedTasks?.size ?: 0}/${user?.dailyTasks?.size ?: 0}",
                    modifier = Modifier.weight(1f)
                )
            }

            // XP Puanları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Toplam XP Puanı",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = (user?.totalXp ?: 0).toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🏆",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

// ==================== GAMİFİCATİON BİLEŞENLERİ ====================

@Composable
fun XPLevelCard(
    userProfile: FirebaseUser,
    gamificationViewModel: com.bysoftware.aaeksen.presentation.gamification.GamificationViewModel
) {
    val currentLevel = gamificationViewModel.calculateLevelFromXP(userProfile.totalXp)
    val nextLevelXP = gamificationViewModel.getXPForNextLevel(currentLevel)
    val progress = gamificationViewModel.getLevelProgress(userProfile.totalXp, currentLevel)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF01447b))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Deneyim Puanı",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${userProfile.totalXp} XP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Yellow
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // XP Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Seviye $currentLevel",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Seviye ${currentLevel + 1}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.Yellow,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Sonraki seviye için ${nextLevelXP - userProfile.totalXp} XP gerekli",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun StatsCard(userProfile: FirebaseUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "İstatistikler",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Filled.Add, // Article yerine Add kullanıyoruz
                    label = "Haber",
                    value = userProfile.newsReadCount.toString(),
                    color = Color(0xFF4CAF50)
                )
                StatItem(
                    icon = Icons.Filled.Add, // PlayArrow yerine Add kullanıyoruz
                    label = "Video",
                    value = "0", // Firebase'de video field'ı yok, 0 göster
                    color = Color(0xFF2196F3)
                )
                StatItem(
                    icon = Icons.Filled.Add, // Games yerine Add kullanıyoruz
                    label = "Oyun",
                    value = "0", // Firebase'de game field'ı yok, 0 göster
                    color = Color(0xFF9C27B0)
                )
                StatItem(
                    icon = Icons.Filled.Share,
                    label = "Paylaşım",
                    value = userProfile.newsSharedCount.toString(),
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun GamificationBadgesSection(userBadges: List<com.bysoftware.aaeksen.data.firebase.model.UserBadge>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kazanılan Rozetler",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${userBadges.size} rozet",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (userBadges.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(userBadges) { badge ->
                        GamificationBadgeItem(badge = badge)
                    }
                }
            } else {
                Text(
                    text = "Henüz rozet kazanmadınız. Görevleri tamamlayarak rozet kazanabilirsiniz!",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun GamificationBadgeItem(badge: com.bysoftware.aaeksen.data.firebase.model.UserBadge) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD700)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Add, // EmojiEvents yerine Add kullanıyoruz
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = badge.name,
            fontSize = 10.sp,
            maxLines = 2,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DailyTasksCard(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Günlük Görevler",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Filled.Add, // Assignment yerine Add kullanıyoruz
                    contentDescription = null,
                    tint = Color(0xFF01447b)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bugünkü görevlerini tamamla ve XP kazan!",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navController.navigate("daily_tasks") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF01447b)
                )
            ) {
                Text("Görevleri Görüntüle")
            }
        }
    }
}

// Örnek rozet verileri (Firebase'den veri gelmediyse)
fun getSampleBadges(): List<UserBadge> {
    return listOf(
        UserBadge(
            badgeId = "1",
            name = "Confident\nReader",
            description = "Read 10 news articles",
            iconUrl = "",
            rarity = "common"
        ),
        UserBadge(
            badgeId = "2",
            name = "Responsible\nReader",
            description = "Share 5 news articles",
            iconUrl = "",
            rarity = "rare"
        ),
        UserBadge(
            badgeId = "3",
            name = "Serious\nLearner",
            description = "Complete 3 quizzes",
            iconUrl = "",
            rarity = "epic"
        )
    )
}
