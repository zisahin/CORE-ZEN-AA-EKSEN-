package com.bysoftware.aaeksen.presentation.newsmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bysoftware.aaeksen.domain.model.City
import com.bysoftware.aaeksen.domain.model.CityNews
import com.bysoftware.aaeksen.domain.model.IntensityLevel
import com.bysoftware.aaeksen.domain.model.NewsCategory
import com.bysoftware.aaeksen.domain.model.NewsIntensity
import com.bysoftware.aaeksen.domain.usecase.GetCitiesUseCase
import com.bysoftware.aaeksen.domain.usecase.GetCityNewsByCategoryUseCase
import com.bysoftware.aaeksen.domain.usecase.GetNewsCategoriesUseCase
import com.bysoftware.aaeksen.domain.usecase.UpdateCityNewsIntensityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsMapViewModel @Inject constructor(
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getNewsCategoriesUseCase: GetNewsCategoriesUseCase,
    private val getCityNewsByCategoryUseCase: GetCityNewsByCategoryUseCase,
    private val updateCityNewsIntensityUseCase: UpdateCityNewsIntensityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsMapUiState())
    val uiState: StateFlow<NewsMapUiState> = _uiState.asStateFlow()

    private val _cityNews = MutableStateFlow<List<CityNews>>(emptyList())
    val cityNews: StateFlow<List<CityNews>> = _cityNews.asStateFlow()

    private val newsIntensity = NewsIntensity()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        loadCities()
        loadNewsCategories()
    }

    private fun loadCities() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            getCitiesUseCase()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { cities ->
                            _uiState.value = _uiState.value.copy(
                                cities = cities,
                                isLoading = false,
                                error = null
                            )
                            updateMapIntensity(cities)
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = error.message
                            )
                        }
                    )
                }
        }
    }

    private fun loadNewsCategories() {
        viewModelScope.launch {
            getNewsCategoriesUseCase()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { categories ->
                            _uiState.value = _uiState.value.copy(newsCategories = categories)
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(error = error.message)
                        }
                    )
                }
        }
    }

    fun onCityClick(city: City) {
        _uiState.value = _uiState.value.copy(selectedCity = city)
    }

    fun onCategoryClick(city: City, category: NewsCategory) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCityNews = true)
            
            getCityNewsByCategoryUseCase(city.id, category.id)
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingCityNews = false,
                        error = error.message
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { news ->
                            _cityNews.value = news
                            _uiState.value = _uiState.value.copy(
                                isLoadingCityNews = false,
                                selectedCity = null
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoadingCityNews = false,
                                error = error.message
                            )
                        }
                    )
                }
        }
    }

    fun dismissBottomSheet() {
        _uiState.value = _uiState.value.copy(selectedCity = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun getCityIntensityLevel(city: City): IntensityLevel {
        return newsIntensity.getIntensityLevel(city.totalNewsCount)
    }

    private fun updateMapIntensity(cities: List<City>) {
        viewModelScope.launch {
            updateCityNewsIntensityUseCase(cities)
        }
    }
}

data class NewsMapUiState(
    val cities: List<City> = emptyList(),
    val newsCategories: List<NewsCategory> = emptyList(),
    val selectedCity: City? = null,
    val isLoading: Boolean = false,
    val isLoadingCityNews: Boolean = false,
    val error: String? = null
)
