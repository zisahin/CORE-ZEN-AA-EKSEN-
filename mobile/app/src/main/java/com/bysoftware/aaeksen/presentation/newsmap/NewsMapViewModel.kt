package com.bysoftware.aaeksen.presentation.newsmap

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bysoftware.aaeksen.data.firebase.model.FirebaseNews
import com.bysoftware.aaeksen.data.firebase.repository.NewsRepository
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
    private val updateCityNewsIntensityUseCase: UpdateCityNewsIntensityUseCase,
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsMapUiState())
    val uiState: StateFlow<NewsMapUiState> = _uiState.asStateFlow()

    private val _cityNews = MutableStateFlow<List<CityNews>>(emptyList())
    val cityNews: StateFlow<List<CityNews>> = _cityNews.asStateFlow()

    private val _firebaseNews = MutableStateFlow<List<FirebaseNews>>(emptyList())
    val firebaseNews: StateFlow<List<FirebaseNews>> = _firebaseNews.asStateFlow()

    private val newsIntensity = NewsIntensity()

    // Türkiye şehirleri listesi
    private val turkishCities = listOf(
        "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin",
        "Aydın", "Balıkesir", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale",
        "Çankırı", "Çorum", "Denizli", "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum",
        "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Isparta", "Mersin",
        "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir", "Kocaeli",
        "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş",
        "Nevşehir", "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas",
        "Tekirdağ", "Tokat", "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak",
        "Aksaray", "Bayburt", "Karaman", "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan",
        "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye", "Düzce"
    )

    init {
        loadInitialData()
        loadFirebaseNews()
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

    private fun loadFirebaseNews() {
        viewModelScope.launch {
            try {
                Log.d("NewsMapViewModel", "🔥 Firebase haberler yükleniyor...")
                
                newsRepository.getNews(limit = 1000).fold(
                    onSuccess = { newsList ->
                        _firebaseNews.value = newsList
                        Log.d("NewsMapViewModel", "✅ ${newsList.size} haber yüklendi")
                        
                        // Şehir yoğunluklarını güncelle
                        updateCityIntensitiesFromFirebase(newsList)
                    },
                    onFailure = { error ->
                        Log.e("NewsMapViewModel", "❌ Firebase haber yükleme hatası: ${error.message}")
                        _uiState.value = _uiState.value.copy(error = error.message)
                    }
                )
            } catch (e: Exception) {
                Log.e("NewsMapViewModel", "❌ Beklenmeyen hata: ${e.message}")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    private fun updateCityIntensitiesFromFirebase(newsList: List<FirebaseNews>) {
        viewModelScope.launch {
            try {
                // Her şehir için haber sayısını hesapla
                val cityNewsCount = mutableMapOf<String, Int>()
                
                newsList.forEach { news ->
                    // Haber lokasyonunu analiz et
                    val detectedCity = detectCityFromNews(news)
                    if (detectedCity != null) {
                        cityNewsCount[detectedCity] = cityNewsCount.getOrDefault(detectedCity, 0) + 1
                    }
                }
                
                // Mevcut şehirleri güncelle
                val currentCities = _uiState.value.cities.toMutableList()
                currentCities.forEach { city ->
                    val newsCount = cityNewsCount[city.name] ?: 0
                    val updatedCity = city.copy(totalNewsCount = newsCount)
                    val index = currentCities.indexOf(city)
                    if (index != -1) {
                        currentCities[index] = updatedCity
                    }
                }
                
                _uiState.value = _uiState.value.copy(cities = currentCities)
                
                Log.d("NewsMapViewModel", "🗺️ Şehir yoğunlukları güncellendi:")
                cityNewsCount.forEach { (city, count) ->
                    Log.d("NewsMapViewModel", "  $city: $count haber")
                }
                
            } catch (e: Exception) {
                Log.e("NewsMapViewModel", "❌ Şehir yoğunluğu güncelleme hatası: ${e.message}")
            }
        }
    }

    private fun detectCityFromNews(news: FirebaseNews): String? {
        // Haber başlığı ve içeriğinde şehir ismi ara
        val searchText = "${news.title} ${news.content} ${news.location}".lowercase()
        
        return turkishCities.find { city ->
            searchText.contains(city.lowercase())
        }
    }

    fun getNewsByCity(cityName: String): List<FirebaseNews> {
        return _firebaseNews.value.filter { news ->
            detectCityFromNews(news) == cityName
        }
    }

    fun getNewsByCityAndCategory(cityName: String, categoryName: String): List<FirebaseNews> {
        return _firebaseNews.value.filter { news ->
            detectCityFromNews(news) == cityName && 
            news.category.equals(categoryName, ignoreCase = true)
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
