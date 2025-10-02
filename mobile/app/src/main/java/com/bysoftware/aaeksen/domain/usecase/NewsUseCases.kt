package com.bysoftware.aaeksen.domain.usecase

import com.bysoftware.aaeksen.domain.model.City
import com.bysoftware.aaeksen.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCitiesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(): Flow<Result<List<City>>> {
        return repository.getCities()
    }
}

class GetNewsCategoriesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke() = repository.getNewsCategories()
}

class GetCityNewsByCategoryUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(cityId: String, categoryId: String) =
        repository.getCityNewsByCategory(cityId, categoryId)
}

class UpdateCityNewsIntensityUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(cities: List<City>) =
        repository.updateCityNewsIntensity(cities)
}
