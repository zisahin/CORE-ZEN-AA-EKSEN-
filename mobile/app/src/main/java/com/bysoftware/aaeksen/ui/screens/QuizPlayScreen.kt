package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bysoftware.aaeksen.presentation.gamification.GamificationViewModel
import com.bysoftware.aaeksen.ui.theme.aa_color
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPlayScreen(
    navController: NavController,
    category: String,
    gamificationViewModel: GamificationViewModel = hiltViewModel()
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var correctAnswers by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    var isAnswered by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(true) }

    // Sample questions - Firebase'den gelecek
    val questions = remember {
        listOf(
            com.bysoftware.aaeksen.data.firebase.model.QuizQuestion(
                question = "$category kategorisinde hangi gelişme son dönemde öne çıktı?",
                options = listOf("Seçenek A", "Seçenek B", "Seçenek C", "Seçenek D"),
                correctAnswerIndex = 0,
                explanation = "Bu sorunun açıklaması burada yer alacak.",
                xpPoints = 10
            ),
            com.bysoftware.aaeksen.data.firebase.model.QuizQuestion(
                question = "$category alanında en önemli gelişme nedir?",
                options = listOf("Seçenek A", "Seçenek B", "Seçenek C", "Seçenek D"),
                correctAnswerIndex = 1,
                explanation = "Bu sorunun açıklaması burada yer alacak.",
                xpPoints = 10
            ),
            com.bysoftware.aaeksen.data.firebase.model.QuizQuestion(
                question = "$category ile ilgili hangi bilgi doğrudur?",
                options = listOf("Seçenek A", "Seçenek B", "Seçenek C", "Seçenek D"),
                correctAnswerIndex = 2,
                explanation = "Bu sorunun açıklaması burada yer alacak.",
                xpPoints = 10
            )
        )
    }

    val currentQuestion = if (currentQuestionIndex < questions.size) {
        questions[currentQuestionIndex]
    } else null

    val totalQuestions = questions.size

    // Timer effect
    LaunchedEffect(isTimerRunning, currentQuestionIndex) {
        if (isTimerRunning && !isAnswered) {
            while (timeLeft > 0 && !isAnswered) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft == 0 && !isAnswered) {
                // Zaman doldu
                isAnswered = true
                showResult = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        category,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = aa_color,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (currentQuestion != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
                    .padding(16.dp)
                    .padding(bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                // Progress and Timer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Question",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "${currentQuestionIndex + 1}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = aa_color
                            )
                            Text(
                                text = "/$totalQuestions",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    // Timer
                    Box(
                        modifier = Modifier.size(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { timeLeft / 30f },
                            modifier = Modifier.size(60.dp),
                            color = if (timeLeft > 10) aa_color else Color.Red,
                            strokeWidth = 4.dp,
                        )
                        Text(
                            text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (timeLeft > 10) aa_color else Color.Red
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress Bar
                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1).toFloat() / totalQuestions },
                    modifier = Modifier.fillMaxWidth(),
                    color = aa_color,
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Question Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = currentQuestion.question,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            lineHeight = 24.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Answer Options
                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = selectedAnswerIndex == index
                    val isCorrect = index == currentQuestion.correctAnswerIndex
                    val backgroundColor = when {
                        !isAnswered -> if (isSelected) aa_color.copy(alpha = 0.1f) else Color.White
                        isCorrect -> Color.Green.copy(alpha = 0.2f)
                        isSelected && !isCorrect -> Color.Red.copy(alpha = 0.2f)
                        else -> Color.White
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
                            .padding(vertical = 6.dp)
                            .clickable(
                                enabled = !isAnswered,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                selectedAnswerIndex = index
                            },
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected || (isAnswered && isCorrect)) 2.dp else 1.dp,
                            color = borderColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSelected) 4.dp else 1.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Option letter
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        when {
                                            !isAnswered -> if (isSelected) aa_color else Color.Gray.copy(alpha = 0.2f)
                                            isCorrect -> Color.Green
                                            isSelected && !isCorrect -> Color.Red
                                            else -> Color.Gray.copy(alpha = 0.2f)
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${'a' + index}",
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        !isAnswered -> if (isSelected) Color.White else Color.Gray
                                        isCorrect || (isSelected && !isCorrect) -> Color.White
                                        else -> Color.Gray
                                    },
                                    fontSize = 14.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = option,
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            
                            // Result icon
                            if (isAnswered) {
                                when {
                                    isCorrect -> {
                                        Text(
                                            text = "✓",
                                            color = Color.Green,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    isSelected && !isCorrect -> {
                                        Text(
                                            text = "✗",
                                            color = Color.Red,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Action Button
                if (!isAnswered) {
                    Button(
                        onClick = {
                            if (selectedAnswerIndex != null) {
                                isAnswered = true
                                isTimerRunning = false
                                if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) {
                                    correctAnswers++
                                }
                                showResult = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = selectedAnswerIndex != null,
                        colors = ButtonDefaults.buttonColors(containerColor = aa_color),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Cevapla",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    // Result and explanation
                    if (showResult) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex)
                                    Color.Green.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex)
                                        "🎉 Doğru!" else "❌ Yanlış!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (selectedAnswerIndex == currentQuestion.correctAnswerIndex)
                                        Color.Green else Color.Red
                                )
                                
                                if (currentQuestion.explanation.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = currentQuestion.explanation,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                if (selectedAnswerIndex == currentQuestion.correctAnswerIndex) {
                                    Spacer(modifier = Modifier.height(8.dp))
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
                    
                    // Next/Finish Button
                    Button(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                selectedAnswerIndex = null
                                isAnswered = false
                                showResult = false
                                timeLeft = 30
                                isTimerRunning = true
                            } else {
                                // Quiz completed
                                val totalXP = correctAnswers * 10
                                gamificationViewModel.addXP(totalXP, "quiz", "$category quiz oyunu")
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = aa_color),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            if (currentQuestionIndex < questions.size - 1) "Sonraki" else "Quiz'i Bitir",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                }
            }
        } else {
            // Quiz completed screen
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Quiz Tamamlandı!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = aa_color
                )
            }
        }
    }
}
