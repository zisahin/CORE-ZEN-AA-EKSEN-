package com.bysoftware.aaeksen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bysoftware.aaeksen.data.firebase.SampleDataInitializer
import com.bysoftware.aaeksen.navigation.AppNavigation
import com.bysoftware.aaeksen.ui.components.CustomBottomBar
import com.bysoftware.aaeksen.ui.theme.AAEksenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var sampleDataInitializer: SampleDataInitializer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Firebase örnek verilerini yükle (tek seferlik)
        lifecycleScope.launch {
            try {
                sampleDataInitializer.initializeSampleData(this@MainActivity)
                    .onSuccess {
                        Log.d("MainActivity", "✅ Firebase verileri hazır!")
                    }
                    .onFailure { error ->
                        Log.e("MainActivity", "❌ Firebase verileri yüklenemedi: ${error.message}")
                    }
            } catch (e: Exception) {
                Log.e("MainActivity", "❌ Beklenmeyen hata: ${e.message}")
            }
        }
        
        setContent {
            AAEksenTheme {
                MainScreen()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "home"
    
    val showBottomBar = !currentRoute.startsWith("news_detail") && 
                        !currentRoute.startsWith("city_news") &&
                        !currentRoute.startsWith("ai_chat") &&
                        !currentRoute.startsWith("chat_conversation")
    
    // Check if we're on Shorts screen for dark theme
    val isDarkTheme = currentRoute == "Neews"
    
    Scaffold(
        modifier = Modifier.fillMaxSize().background(
            if (isDarkTheme) Color.Black else Color.White
        ),
        bottomBar = {
            if (showBottomBar) {
                CustomBottomBar(
                    selectedIndex = when (currentRoute) {
                        "Neews" -> 0
                        "Oyunlar" -> 1
                        "center" -> 2
                        "Canlı" -> 3  // Harita
                        "Profil" -> 4  // Profil
                        else -> 2
                    },
                    isDarkTheme = isDarkTheme,
                    onItemSelected = { index ->
                        val route = when (index) {
                            0 -> "Neews"
                            1 -> "Oyunlar"
                            2 -> "center"
                            3 -> "Canlı"  // Harita butonu → NewsMapScreen
                            4 -> "Profil" // Profil ekranı
                            else -> "center"
                        }
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        AppNavigation(navController = navController)
    }
}
