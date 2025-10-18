package com.bysoftware.aaeksen.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bysoftware.aaeksen.ui.theme.AAEksenTheme
import com.bysoftware.aaeksen.R
@Composable
fun CustomBottomBar(
    selectedIndex: Int,
    isDarkTheme: Boolean = false,
    onItemSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(if (isDarkTheme) Color.Black else Color.White)
    ) {
        val items = listOf("Neews", "Oyunlar", "Center", "Harita", "Tünel")

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEachIndexed { index, item ->
                if (index == 2) {
                    // ORTA item (AA logosu)
                    Box(
                        modifier = Modifier
                            .offset(y = (-21).dp) // Yukarı çıkarıyoruz
                            .size(56.dp)
                            .shadow(
                                elevation = 5.dp,
                                shape = RoundedCornerShape(10.dp),
                                clip = false
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF01447b)) // AA mavisi
                            .clickable(
                                onClick = { onItemSelected(index) },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        contentAlignment = Alignment.Center,

                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_3), // kendi AA logonu koy
                            contentDescription = "AA Logo",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                } else {
                    // Normal itemler
                    val icon = when (item) {
                        "Neews" -> painterResource(R.drawable.discover)
                        "Oyunlar" -> painterResource(R.drawable.game)
                        "Harita" -> painterResource(R.drawable.live_2989838)
                        "Tünel" -> painterResource(R.drawable.time_tunnel)
                        else -> painterResource(R.drawable.person)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                onClick = { onItemSelected(index) },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = item,
                            tint = if (selectedIndex == index) {
                                Color(0xFF004AAD)
                            } else {
                                if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color.Gray
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = item,
                            fontSize = 12.sp,
                            color = if (selectedIndex == index) {
                                Color(0xFF004AAD)
                            } else {
                                if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color.Gray
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
@Preview
fun BottomNavPreview(){
    AAEksenTheme {
        CustomBottomBar(selectedIndex = 0, isDarkTheme = false, onItemSelected = {})
    }
}

@Composable
@Preview
fun BottomNavDarkPreview(){
    AAEksenTheme {
        CustomBottomBar(selectedIndex = 0, isDarkTheme = true, onItemSelected = {})
    }
}

