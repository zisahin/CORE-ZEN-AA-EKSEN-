package com.bysoftware.aaeksen.domain.repository

import com.bysoftware.aaeksen.domain.model.City
import com.bysoftware.aaeksen.domain.model.CityNews
import com.bysoftware.aaeksen.domain.model.NewsCategory
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getCities(): Flow<Result<List<City>>>
    suspend fun getNewsCategories(): Flow<Result<List<NewsCategory>>>
    suspend fun getCityNewsByCategory(cityId: String, categoryId: String): Flow<Result<List<CityNews>>>
    suspend fun updateCityNewsIntensity(cities: List<City>): Result<Unit>
}
