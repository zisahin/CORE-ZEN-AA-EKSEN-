package com.bysoftware.aaeksen.domain.model

data class City(
    val id: String,
    val name: String,
    val totalNewsCount: Int,
    val coordinates: Pair<Double, Double>, // lat, lng
    val categoryNews: Map<String, Int> = emptyMap()
)

data class NewsCategory(
    val id: String,
    val name: String,
    val icon: String,
    val color: String
)

data class CityNews(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val source: String,
    val publishDate: String,
    val timeAgo: String,
    val categoryId: String,
    val cityId: String,
    val url: String
)

data class NewsIntensity(
    val low: Int = 0,
    val medium: Int = 20,
    val high: Int = 50,
    val veryHigh: Int = 100
) {
    fun getIntensityLevel(newsCount: Int): IntensityLevel {
        return when {
            newsCount <= low -> IntensityLevel.LOW
            newsCount <= medium -> IntensityLevel.MEDIUM
            newsCount <= high -> IntensityLevel.HIGH
            else -> IntensityLevel.VERY_HIGH
        }
    }
}

enum class IntensityLevel(val colorHex: String) {
    LOW("#4FC3F7"),       // Açık mavi
    MEDIUM("#29B6F6"),    // Mavi
    HIGH("#FFB74D"),      // Turuncu
    VERY_HIGH("#E53935")  // Kırmızı
}
