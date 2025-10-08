package com.bysoftware.aaeksen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bysoftware.aaeksen.data.NewsItem
import com.bysoftware.aaeksen.ui.screens.AiChatScreen
import com.bysoftware.aaeksen.ui.screens.CityNewsListScreen
import com.bysoftware.aaeksen.ui.screens.HomeScreen
import com.bysoftware.aaeksen.ui.screens.NewsDetailScreen
import com.bysoftware.aaeksen.ui.screens.NewsMapScreen
import com.bysoftware.aaeksen.ui.screens.ShortsScreen
import com.bysoftware.aaeksen.ui.screens.TimeTunnelCategoriesScreen
import com.bysoftware.aaeksen.presentation.newsdetail.NewsDetailViewModel
import com.bysoftware.aaeksen.ui.screens.TimeTunnelDetailScreen
import com.bysoftware.aaeksen.ui.screens.ProfileScreen
import com.bysoftware.aaeksen.ui.screens.LoginScreen
import com.bysoftware.aaeksen.ui.screens.RegisterScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "center"
    ) {
        composable("Neews") {
            ShortsScreen(
                onNavigateToNews = { videoId ->
                    // Navigate to news detail based on video
                    navController.navigate("news_detail/1") // Mock news ID for now
                },
                onShareVideo = { videoId ->
                    // Handle video sharing (could open share dialog)
                    // For now, just a placeholder
                }
            )
        }
        
        composable("news_detail/{newsId}") { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId") ?: ""
            val viewModel: NewsDetailViewModel = hiltViewModel()
            
            NewsDetailScreen(
                newsId = newsId,
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("Oyunlar") {
            // Placeholder for Discover screen
            HomeScreen(
                onNewsClick = { newsItem ->
                    navController.navigate("news_detail/${newsItem.id}")
                }
            )
        }
        
        composable("center") {
            HomeScreen(
                onNewsClick = { newsItem ->
                    navController.navigate("news_detail/${newsItem.id}")
                },
                onAIChatClick = {
                    navController.navigate("ai_chat")
                }
            )
        }
        
        composable("ai_chat") {
            AiChatScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("Canlı") {
            NewsMapScreen(
                onCategoryNewsClick = { cityName, categoryName ->
                    navController.navigate("city_news/$cityName/$categoryName")
                }
            )
        }
        
        composable("city_news/{cityName}/{categoryName}") { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            
            CityNewsListScreen(
                cityName = cityName,
                categoryName = categoryName,
                onNewsClick = { cityNews ->
                    // CityNews'i NewsItem'a dönüştürüp navigation yap
                    navController.navigate("news_detail/${cityNews.id}")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("time_tunnel") {
            TimeTunnelCategoriesScreen(navController = navController)
        }
        
        composable("time_tunnel_detail/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            TimeTunnelDetailScreen(
                navController = navController,
                categoryId = categoryId
            )
        }
                composable("Profil") {
                    ProfileScreen(navController = navController)
                }

                composable("login") {
                    LoginScreen(
                        navController = navController,
                        onLoginSuccess = {
                            navController.navigate("center") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable("register") {
                    RegisterScreen(
                        navController = navController,
                        onRegisterSuccess = {
                            navController.navigate("center") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }
    }
    }
