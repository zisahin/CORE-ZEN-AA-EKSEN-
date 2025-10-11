package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bysoftware.aaeksen.data.firebase.model.DailyTask
import com.bysoftware.aaeksen.presentation.gamification.GamificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTasksScreen(
    navController: NavController,
    gamificationViewModel: GamificationViewModel = hiltViewModel()
) {
    val dailyTasks by gamificationViewModel.dailyTasks.collectAsState()
    val taskProgress by gamificationViewModel.taskProgress.collectAsState()
    val userProfile by gamificationViewModel.userProfile.collectAsState()
    val isLoading by gamificationViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Günlük Görevler") },
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
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF01447b))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Günlük İlerleme Özeti
                    DailyProgressCard(
                        completedTasks = gamificationViewModel.getCompletedTasksCount(),
                        totalTasks = dailyTasks.size,
                        todayXP = gamificationViewModel.getTodayXP(),
                        userLevel = userProfile?.let { gamificationViewModel.calculateLevelFromXP(it.totalXp) } ?: 1
                    )
                }

                item {
                    Text(
                        text = "Bugünkü Görevler",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF01447b)
                    )
                }

                items(dailyTasks) { task ->
                    val (currentProgress, isCompleted) = gamificationViewModel.getTaskCompletionStatus(task.id)
                    DailyTaskCard(
                        task = task,
                        currentProgress = currentProgress,
                        isCompleted = isCompleted
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Bottom navigation space
                }
            }
        }
    }
}

@Composable
fun DailyProgressCard(
    completedTasks: Int,
    totalTasks: Int,
    todayXP: Long,
    userLevel: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF01447b),
                            Color(0xFF0066cc)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Günlük İlerleme",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Seviye $userLevel",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$completedTasks/$totalTasks",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress Bar
                val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0f
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.Yellow,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bugün: $todayXP XP",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                    
                    Text(
                        text = "${((progress * 100).toInt())}% tamamlandı",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyTaskCard(
    task: DailyTask,
    currentProgress: Int,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFF0F8FF) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Task Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) Color(0xFF4CAF50) else Color(0xFF01447b)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getTaskIcon(task.type),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Task Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isCompleted) Color(0xFF4CAF50) else Color.Black
                )
                Text(
                    text = task.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$currentProgress/${task.targetCount}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = (currentProgress.toFloat() / task.targetCount.toFloat()).coerceAtMost(1f),
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (isCompleted) Color(0xFF4CAF50) else Color(0xFF01447b),
                        trackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Reward
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isCompleted) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Tamamlandı",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Text(
                        text = "+${task.xpReward}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                    Text(
                        text = "XP",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

fun getTaskIcon(taskType: String): ImageVector {
    return when (taskType) {
        "read_news" -> Icons.Filled.Add // Article yerine
        "watch_videos" -> Icons.Filled.Add // PlayArrow yerine
        "share_content" -> Icons.Filled.Share
        "play_games" -> Icons.Filled.Add // Games yerine
        "make_comments" -> Icons.Filled.Add // Comment yerine
        "daily_login" -> Icons.Filled.Add // Login yerine
        else -> Icons.Filled.Add // Assignment yerine
    }
}
