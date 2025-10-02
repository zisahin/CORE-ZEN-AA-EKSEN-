package com.bysoftware.aaeksen.data.repository

import com.bysoftware.aaeksen.data.local.LocalNewsDataSource
import com.bysoftware.aaeksen.data.mapper.toCityNews
import com.bysoftware.aaeksen.data.mapper.toNewsCategory
import com.bysoftware.aaeksen.data.remote.NewsMapApi
import com.bysoftware.aaeksen.domain.model.City
import com.bysoftware.aaeksen.domain.model.CityNews
import com.bysoftware.aaeksen.domain.model.NewsCategory
import com.bysoftware.aaeksen.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val api: NewsMapApi,
    private val localDataSource: LocalNewsDataSource
) : NewsRepository {
    
    override suspend fun getCities(): Flow<Result<List<City>>> = flow {
        try {
            // Önce local sample data kullan, sonra API'ye geçebilirsin
            val cities = localDataSource.getSampleCities()
            emit(Result.success(cities))
            
            // API kullanmak için aşağıdaki kod aktif edilir:
            // val citiesDto = api.getCities()
            // val cities = citiesDto.map { it.toCity() }
            // emit(Result.success(cities))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override suspend fun getNewsCategories(): Flow<Result<List<NewsCategory>>> = flow {
        try {
            // Önce local sample data kullan
            val categories = localDataSource.getSampleNewsCategories()
            emit(Result.success(categories))
            
            // API kullanmak için:
            // val categoriesDto = api.getNewsCategories()
            // val categories = categoriesDto.map { it.toNewsCategory() }
            // emit(Result.success(categories))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override suspend fun getCityNewsByCategory(
        cityId: String, 
        categoryId: String
    ): Flow<Result<List<CityNews>>> = flow {
        try {
            // Önce local sample data kullan
            val news = localDataSource.getSampleCityNews(cityId, categoryId)
            emit(Result.success(news))
            
            // API kullanmak için:
            // val newsDto = api.getCityNewsByCategory(cityId, categoryId)
            // val news = newsDto.map { it.toCityNews() }
            // emit(Result.success(news))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override suspend fun updateCityNewsIntensity(cities: List<City>): Result<Unit> {
        return try {
            // Burada Mapbox haritasında şehir renklerini güncelleme işlemi yapılacak
            // Şimdilik başarılı döndürüyoruz
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
