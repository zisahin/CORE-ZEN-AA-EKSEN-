package com.bysoftware.aaeksen.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bysoftware.aaeksen.data.SampleData
import com.bysoftware.aaeksen.ui.screens.HomeScreen
import com.bysoftware.aaeksen.ui.screens.NewsDetailScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNewsClick = { newsItem ->
                    navController.navigate("news_detail/${newsItem.id}")
                }
            )
        }
        
        composable("news_detail/{newsId}") { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId")
            val newsItem = if (newsId == "1") {
                SampleData.breakingNews.first()
            } else {
                SampleData.recommendedNews.find { it.id == newsId }
                    ?: SampleData.recommendedNews.first()
            }
            
            NewsDetailScreen(
                newsItem = newsItem,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("discover") {
            // Placeholder for Discover screen
            HomeScreen(
                onNewsClick = { newsItem ->
                    navController.navigate("news_detail/${newsItem.id}")
                }
            )
        }
        
        composable("saved") {
            // Placeholder for Saved screen
            HomeScreen(
                onNewsClick = { newsItem ->
                    navController.navigate("news_detail/${newsItem.id}")
                }
            )
        }
        
        composable("profile") {
            // Placeholder for Profile screen
            HomeScreen(
                onNewsClick = { newsItem ->
                    navController.navigate("news_detail/${newsItem.id}")
                }
            )
        }
    }
}

