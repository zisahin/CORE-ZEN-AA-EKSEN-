package com.bysoftware.aaeksen.data.local

import com.bysoftware.aaeksen.domain.model.City
import com.bysoftware.aaeksen.domain.model.CityNews
import com.bysoftware.aaeksen.domain.model.NewsCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalNewsDataSource @Inject constructor() {
    
    // Sample data for development/testing
    fun getSampleCities(): List<City> {
        return listOf(
            City("1", "İstanbul", 95, Pair(41.0082, 28.9784), mapOf("1" to 25, "2" to 20, "3" to 15, "4" to 35)),
            City("2", "Ankara", 78, Pair(39.9334, 32.8597), mapOf("1" to 20, "2" to 25, "3" to 13, "4" to 20)),
            City("3", "İzmir", 65, Pair(38.4237, 27.1428), mapOf("1" to 18, "2" to 15, "3" to 17, "4" to 15)),
            City("4", "Bursa", 50, Pair(40.1885, 29.0634), mapOf("1" to 12, "2" to 13, "3" to 12, "4" to 13)),
            City("5", "Antalya", 42, Pair(36.8969, 30.7133), mapOf("1" to 10, "2" to 12, "3" to 10, "4" to 10)),
            City("6", "Adana", 38, Pair(37.0000, 35.3213), mapOf("1" to 9, "2" to 11, "3" to 9, "4" to 9)),
            City("7", "Konya", 33, Pair(37.8746, 32.4932), mapOf("1" to 8, "2" to 10, "3" to 8, "4" to 7)),
            City("8", "Gaziantep", 30, Pair(37.0662, 37.3833), mapOf("1" to 7, "2" to 8, "3" to 7, "4" to 8)),
            City("9", "Şanlıurfa", 28, Pair(37.1591, 38.7969), mapOf("1" to 7, "2" to 8, "3" to 6, "4" to 7)),
            City("10", "Kocaeli", 26, Pair(40.8533, 29.8815), mapOf("1" to 6, "2" to 7, "3" to 6, "4" to 7)),
            City("11", "Mersin", 25, Pair(36.8121, 34.6415), mapOf("1" to 6, "2" to 7, "3" to 6, "4" to 6)),
            City("12", "Diyarbakır", 23, Pair(37.9142, 40.2351), mapOf("1" to 5, "2" to 6, "3" to 6, "4" to 6)),
            City("13", "Hatay", 22, Pair(36.4018, 36.3498), mapOf("1" to 5, "2" to 6, "3" to 5, "4" to 6)),
            City("14", "Manisa", 21, Pair(38.6191, 27.4289), mapOf("1" to 5, "2" to 6, "3" to 5, "4" to 5)),
            City("15", "Kayseri", 20, Pair(38.7312, 35.4787), mapOf("1" to 5, "2" to 5, "3" to 5, "4" to 5)),
            City("16", "Samsun", 19, Pair(41.2928, 36.3313), mapOf("1" to 4, "2" to 5, "3" to 5, "4" to 5)),
            City("17", "Balıkesir", 18, Pair(39.6484, 27.8826), mapOf("1" to 4, "2" to 5, "3" to 4, "4" to 5)),
            City("18", "Kahramanmaraş", 17, Pair(37.5858, 36.9371), mapOf("1" to 4, "2" to 4, "3" to 4, "4" to 5)),
            City("19", "Van", 16, Pair(38.4891, 43.4089), mapOf("1" to 4, "2" to 4, "3" to 4, "4" to 4)),
            City("20", "Aydın", 15, Pair(37.8560, 27.8416), mapOf("1" to 3, "2" to 4, "3" to 4, "4" to 4)),
            // Daha fazla il eklenebilir...
            City("21", "Denizli", 14, Pair(37.7765, 29.0864), mapOf("1" to 3, "2" to 4, "3" to 3, "4" to 4)),
            City("22", "Sakarya", 13, Pair(40.6940, 30.4358), mapOf("1" to 3, "2" to 3, "3" to 3, "4" to 4)),
            City("23", "Eskişehir", 12, Pair(39.7767, 30.5206), mapOf("1" to 3, "2" to 3, "3" to 3, "4" to 3)),
            City("24", "Şırnak", 11, Pair(37.5158, 42.4611), mapOf("1" to 2, "2" to 3, "3" to 3, "4" to 3)),
            City("25", "Erzurum", 10, Pair(39.9334, 41.2767), mapOf("1" to 2, "2" to 3, "3" to 2, "4" to 3))
        )
    }
    
    fun getSampleNewsCategories(): List<NewsCategory> {
        return listOf(
            NewsCategory("1", "Gündem", "article", "#2196F3"),
            NewsCategory("2", "Siyaset", "account_balance", "#9C27B0"),
            NewsCategory("3", "Spor", "sports", "#4CAF50"),
            NewsCategory("4", "Ekonomi", "trending_up", "#FF9800"),
            NewsCategory("5", "Teknoloji", "computer", "#607D8B"),
            NewsCategory("6", "Sağlık", "local_hospital", "#4CAF50")
        )
    }
    
    fun getSampleCityNews(cityId: String, categoryId: String): List<CityNews> {
        val cityName = getSampleCities().find { it.id == cityId }?.name ?: "Şehir"
        val categoryName = getSampleNewsCategories().find { it.id == categoryId }?.name ?: "Kategori"
        
        return (1..8).map { index ->
            CityNews(
                id = "${cityId}_${categoryId}_$index",
                title = "$cityName - $categoryName Haberi $index: Lorem ipsum dolor sit amet consectetur",
                description = "Bu haberin detaylı açıklaması burada yer alacak. Önemli gelişmeler ve detaylar...",
                imageUrl = "https://picsum.photos/400/250?random=$index",
                source = "AA Eksen",
                publishDate = "2024-${(1..12).random()}-${(1..28).random()}",
                timeAgo = "${(1..24).random()} saat önce",
                categoryId = categoryId,
                cityId = cityId,
                url = "https://example.com/news/${cityId}_${categoryId}_$index"
            )
        }
    }
}
