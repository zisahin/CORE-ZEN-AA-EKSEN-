package com.bysoftware.aaeksen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.bysoftware.aaeksen.ui.screens.VideoPlayerScreen
import com.bysoftware.aaeksen.ui.screens.VideoGenerationScreen
import com.bysoftware.aaeksen.ui.screens.VideoListScreen

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
                    // Video'nun description'ını news content olarak kullan
                    // Şimdilik rastgele bir news ID ile navigation yap
                    val newsId = listOf("1", "2", "3", "4").random()
                    navController.navigate("news_detail/$newsId")
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
                    com.bysoftware.aaeksen.ui.screens.GamesScreen(navController = navController)
                }
        
                composable("center") {
                    HomeScreen(
                        onNewsClick = { newsItem ->
                            navController.navigate("news_detail/${newsItem.id}")
                        },
                        onAIChatClick = {
                            navController.navigate("ai_chat")
                        },
                        onVideoListClick = {
                            navController.navigate("video_list")
                        },
                        onProfileClick = {
                            navController.navigate("profile")
                        },
                        onVideoGeneratorClick = {
                            navController.navigate("video_generation")
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
        
        composable("Harita") {
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
        
        composable("Tünel") {
            TimeTunnelCategoriesScreen(navController = navController)
        }
        
        composable("time_tunnel_detail/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            TimeTunnelDetailScreen(
                navController = navController,
                categoryId = categoryId
            )
        }
                composable("profile") {
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

                // Video Ekranları
                composable("video_list") {
                    VideoListScreen(navController = navController)
                }

                composable("video_player/{videoId}") { backStackEntry ->
                    val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
                    VideoPlayerScreen(
                        videoId = videoId,
                        navController = navController
                    )
                }

                composable("video_generation") {
                    VideoGenerationScreen(navController = navController)
                }

                composable("daily_tasks") {
                    com.bysoftware.aaeksen.ui.screens.DailyTasksScreen(navController = navController)
                }

                // Standalone Oyunlar
                composable("standalone_quiz") {
                    com.bysoftware.aaeksen.ui.screens.StandaloneQuizScreen(navController = navController)
                }

                composable("standalone_map_guess") {
                    com.bysoftware.aaeksen.ui.screens.StandaloneMapGuessScreen(navController = navController)
                }

                composable("quiz_play/{category}") { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category") ?: "Genel"
                    com.bysoftware.aaeksen.ui.screens.QuizPlayScreen(
                        navController = navController,
                        category = category
                    )
                }

                composable("standalone_map_guess_menu") {
                    com.bysoftware.aaeksen.ui.screens.StandaloneMapGuessMenuScreen(navController = navController)
                }

                composable("news_detail/{newsId}?autoStartMapGuess={autoStartMapGuess}&autoStartQuiz={autoStartQuiz}") { backStackEntry ->
                    val newsId = backStackEntry.arguments?.getString("newsId") ?: ""
                    val autoStartMapGuess = backStackEntry.arguments?.getString("autoStartMapGuess")?.toBoolean() ?: false
                    val autoStartQuiz = backStackEntry.arguments?.getString("autoStartQuiz")?.toBoolean() ?: false
                    val newsDetailViewModel: NewsDetailViewModel = hiltViewModel()
                    
                    LaunchedEffect(newsId) {
                        newsDetailViewModel.loadNews(newsId)
                    }
                    
                    com.bysoftware.aaeksen.ui.screens.NewsDetailScreen(
                        newsId = newsId,
                        viewModel = newsDetailViewModel,
                        onBackClick = { navController.popBackStack() },
                        autoStartMapGuess = autoStartMapGuess,
                        autoStartQuiz = autoStartQuiz
                    )
                }
    }
    }
