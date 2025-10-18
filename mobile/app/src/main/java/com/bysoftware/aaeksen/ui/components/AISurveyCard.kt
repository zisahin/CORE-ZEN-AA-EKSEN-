package com.bysoftware.aaeksen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bysoftware.aaeksen.data.firebase.model.AIQuestion

@Composable
fun AISurveyCard(
    question: AIQuestion,
    onAnswerSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2196F3), // Mavi
                            Color(0xFF1976D2)  // Koyu mavi
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AA AI Soruyor",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Question
                Text(
                    text = question.question,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Options
                question.options.forEach { option ->
                    val isSelected = selectedOption == option
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(
                                enabled = !isAnswered,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                if (!isAnswered) {
                                    selectedOption = option
                                    isAnswered = true
                                    onAnswerSelected(option)
                                }
                            },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                Color.White.copy(alpha = 0.9f)
                            } else {
                                Color.White.copy(alpha = 0.7f)
                            }
                        )
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            color = if (isSelected) Color(0xFF1976D2) else Color.Black,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (isAnswered) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Teşekkürler! Cevabınız kaydedildi.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
