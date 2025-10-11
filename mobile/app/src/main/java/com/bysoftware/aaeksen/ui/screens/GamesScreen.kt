package com.bysoftware.aaeksen.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bysoftware.aaeksen.R
import com.bysoftware.aaeksen.ui.theme.AAEksenTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Hamburger menü ve başlık
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
            
            Text(
                text = "AA GAMES",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.size(24.dp)) // Balance için
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Ana başlık
        Text(
            text = "Kısa bir oyun molasının tam zamanı!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            lineHeight = 32.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Quiz Oyunu
        BundleGameCard(
            title = "Quiz Oyunu",
            description = "Haber bilginizi test edin ve XP kazanın.",
            gridColors = listOf(
                Color(0xFF2196F3), Color(0xFFBDBDBD), Color(0xFF4CAF50),
                Color(0xFF2196F3), Color(0xFFBDBDBD), Color(0xFF4CAF50),
                Color(0xFF4CAF50), Color(0xFF4CAF50), Color(0xFF4CAF50)
            ),
            onClick = {
                navController.navigate("standalone_quiz")
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Harita Tahmin
        BundleGameCard(
            title = "Harita Tahmin",
            description = "Haberlerin hangi ilden geldiğini tahmin edin.",
            gridColors = listOf(
                Color.White, Color(0xFF81D4FA), Color.White,
                Color(0xFF81D4FA), Color(0xFF81D4FA), Color(0xFF81D4FA),
                Color.White, Color(0xFF81D4FA), Color.White
            ),
            onClick = {
                navController.navigate("standalone_map_guess_menu")
            }
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // AA PREMIUM
        Text(
            text = "AA PREMIUM",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Premium açıklama
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier

                    .size(40.dp)
                    .background(
                        Color(0xFF01447b),
                        RoundedCornerShape(8.dp)
                    )
            ){
                Image(
                    painter = painterResource(id = R.drawable.logo_3),
                    contentDescription = null,
                    modifier = Modifier.padding( top = 7.dp, start = 5.dp, end = 5.dp, bottom = 0.dp),
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Reklamsız haber okuma ve oyun keyfi için ",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "AA Premium ",
                    fontSize = 16.sp,
                    color = Color(0xFF01447b),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "seni bekliyor.",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Abone Ol butonu
        Button(
            onClick = { /* Premium subscription */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF01447b)
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Abone Ol",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun BundleGameCard(
    title: String,
    description: String,
    gridColors: List<Color>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Grid ikonu
        Card(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // 3x3 grid
                Column(
                    modifier = Modifier.size(36.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(3) { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            repeat(3) { col ->
                                val index = row * 3 + col
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(
                                            gridColors.getOrElse(index) { Color.Gray },
                                            RoundedCornerShape(2.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Oyun bilgileri
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
        
        // Başla butonu
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .width(90.dp)
                .height(40.dp)
        ) {
            Text(
                text = "Başla",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
fun GameCard(
    title: String,
    description: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    isComingSoon: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isComingSoon) {
                    onClick()
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Oyun ikonu
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = backgroundColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Oyun bilgileri
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    if (isComingSoon) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Yakında",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(
                                    Color.Yellow,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
            
            // Ok ikonu
            if (!isComingSoon) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Oyuna Git",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GamesScreenPreview() {
    AAEksenTheme {
        GamesScreen(navController = rememberNavController())
    }
}
