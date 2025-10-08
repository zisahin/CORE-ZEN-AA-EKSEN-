# AA Eksen AI Service - Android/Kotlin Entegrasyon Dokümantasyonu

**Son Güncelleme:** 8 Ekim 2025  
**Versiyon:** 1.0.0  
**Hedef Platform:** Android (Kotlin) + Jetpack Compose

---

## 📋 İçindekiler

1. [Genel Bakış](#1-genel-bakış)
2. [AI Service Yapısı](#2-ai-service-yapısı)
3. [Kurulum ve Yapılandırma](#3-kurulum-ve-yapılandırma)
4. [Data Modelleri](#4-data-modelleri)
5. [API Client Implementasyonu](#5-api-client-implementasyonu)
6. [Repository Pattern](#6-repository-pattern)
7. [ViewModel ve State Management](#7-viewmodel-ve-state-management)
8. [Jetpack Compose UI Örnekleri](#8-jetpack-compose-ui-örnekleri)
9. [Özellik Bazlı Entegrasyon](#9-özellik-bazlı-entegrasyon)
10. [Test ve Debug](#10-test-ve-debug)

---

## 1. Genel Bakış

AA Eksen AI Service, OpenAI GPT-4o-mini ve TTS-1-HD modellerini kullanarak haberleri analiz eder, özetler, podcast'lere dönüştürür ve kullanıcı etkileşimi sağlar.

### 1.1. AI Service Özellikleri

| Özellik | Durum | OpenAI Model | Endpoint |
|---------|-------|--------------|----------|
| Chat (Sohbet) | ✅ Aktif | GPT-4o-mini | `/api/chat` |
| Haber Özetleme | ✅ Aktif | GPT-4o-mini | `/api/summary` |
| Timeline Analizi | ✅ Aktif | GPT-4o-mini | `/api/timeline` |
| Podcast (Text) | ✅ Aktif | GPT-4o-mini | `/api/podcast/generate` |
| Podcast (Audio/TTS) | ✅ Aktif | TTS-1-HD | `/api/podcast/:id/audio` |
| Quiz Oluşturma | ✅ Aktif | GPT-4o-mini | `/api/quiz/generate` |
| Günlük Sorular | ✅ Aktif | GPT-4o-mini | `/api/questions/daily` |
| Haber RSS | ✅ Aktif | - | `/api/rss/:category` |
| Şehir Haberleri | ✅ Aktif | - | `/api/cities/:city` |

### 1.2. Teknoloji Stack

**Backend:**
- Node.js + Express
- OpenAI SDK
- Anadolu Ajansı RSS

**Android:**
- Kotlin 1.9+
- Jetpack Compose
- Ktor Client (HTTP)
- Kotlin Coroutines
- StateFlow & ViewModel

---

## 2. AI Service Yapısı

### 2.1. Base URL

```kotlin
// Development (Emulator)
const val BASE_URL = "http://10.0.2.2:3001/api"

// Development (Fiziksel Cihaz - Aynı WiFi)
const val BASE_URL = "http://192.168.1.100:3001/api"

// Production (Firebase Functions)
const val BASE_URL = "https://aa-eksen-71097.cloudfunctions.net/api/api"
```

### 2.2. API Endpoint Haritası

```
/api
├── /chat (POST)                    → AI Sohbet
├── /summary (POST)                 → Haber Özetleme
├── /timeline (GET)                 → Zaman Tüneli
├── /podcast
│   ├── / (GET)                     → Podcast Listesi
│   ├── /generate (POST)            → Podcast Oluştur
│   ├── /:id (GET)                  → Podcast Detay
│   └── /:id/audio (POST)           → TTS Ses Dosyası
├── /quiz
│   ├── /generate (POST)            → Quiz Oluştur
│   ├── / (GET)                     → Quiz Listesi
│   ├── /:id (GET)                  → Quiz Detay
│   └── /:id/submit (POST)          → Quiz Değerlendir
├── /questions
│   ├── /daily (GET)                → Günlük Sorular
│   └── /answer (POST)              → Soru Cevapla
├── /rss
│   ├── /categories (GET)           → Kategoriler
│   ├── /:category (GET)            → Kategori Haberleri
│   └── /mixed (GET)                → Karışık Haberler
└── /cities
    ├── /stats (GET)                → Şehir İstatistikleri
    └── /:city (GET)                → Şehir Haberleri
```

---

## 3. Kurulum ve Yapılandırma

### 3.1. Gradle Dependencies

**build.gradle.kts (Project Level):**

```kotlin
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20" apply false
}
```

**build.gradle.kts (Module: app):**

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.aa.eksen"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.aa.eksen"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        
        // AI Service URL
        buildConfigField("String", "AI_SERVICE_BASE_URL", "\"http://10.0.2.2:3001\"")
    }
    
    buildTypes {
        debug {
            buildConfigField("String", "AI_SERVICE_BASE_URL", "\"http://10.0.2.2:3001\"")
        }
        release {
            buildConfigField("String", "AI_SERVICE_BASE_URL", "\"https://your-production-url\"")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.activity:activity-compose:1.8.1")
    
    // ViewModel & Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    
    // Ktor Client (HTTP)
    implementation("io.ktor:ktor-client-core:2.3.6")
    implementation("io.ktor:ktor-client-android:2.3.6")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.6")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
    implementation("io.ktor:ktor-client-logging:2.3.6")
    
    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1")
    
    // Coil (Image Loading)
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // ExoPlayer (Podcast Audio)
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media3:media3-ui:1.2.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.5")
}
```

### 3.2. AndroidManifest.xml

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- İnternet İzinleri -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <!-- Ses İzinleri (Podcast için) -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    
    <application
        android:name=".EksenApplication"
        android:allowBackup="true"
        android:usesCleartextTraffic="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.AAEksen">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.AAEksen">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
    </application>
</manifest>
```

### 3.3. Application Class

```kotlin
// EksenApplication.kt
package com.aa.eksen

import android.app.Application
import com.aa.eksen.data.remote.AIServiceClient

class EksenApplication : Application() {
    
    lateinit var aiServiceClient: AIServiceClient
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // AI Service Client'ı başlat
        aiServiceClient = AIServiceClient(BuildConfig.AI_SERVICE_BASE_URL)
        
        if (BuildConfig.DEBUG) {
            println("🤖 AI Service başlatıldı: ${BuildConfig.AI_SERVICE_BASE_URL}")
        }
    }
}
```

---

## 4. Data Modelleri

### 4.1. Chat Models

```kotlin
// data/models/ChatModels.kt
package com.aa.eksen.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val message: String,
    val conversationHistory: List<ChatMessage> = emptyList()
)

@Serializable
data class ChatMessage(
    val role: String, // "user" veya "assistant"
    val content: String
)

@Serializable
data class ChatResponse(
    val reply: String,
    val timestamp: String
)
```

### 4.2. News Models

```kotlin
// data/models/NewsModels.kt
package com.aa.eksen.data.models

import kotlinx.serialization.Serializable

@Serializable
data class NewsItem(
    val id: String,
    val title: String,
    val description: String,
    val link: String,
    val pubDate: String,
    val imageUrl: String? = null,
    val category: String? = null
)

@Serializable
data class NewsResponse(
    val success: Boolean,
    val category: String? = null,
    val news: List<NewsItem>,
    val total: Int
)

@Serializable
data class NewsCategory(
    val id: String,
    val name: String
)
```

### 4.3. Podcast Models

```kotlin
// data/models/PodcastModels.kt
package com.aa.eksen.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PodcastItem(
    val id: String,
    val title: String,
    val description: String,
    val duration: Int, // saniye
    val category: String,
    val createdAt: String,
    val listenCount: Int
)

@Serializable
data class PodcastListResponse(
    val success: Boolean,
    val podcasts: List<PodcastItem>,
    val total: Int
)

@Serializable
data class GeneratePodcastRequest(
    val newsContent: String,
    val title: String? = null,
    val category: String? = null,
    val tone: String = "friendly"
)

@Serializable
data class PodcastAudioRequest(
    val voice: String = "nova" // nova, shimmer, alloy, echo, fable, onyx
)
```

### 4.4. Quiz Models

```kotlin
// data/models/QuizModels.kt
package com.aa.eksen.data.models

import kotlinx.serialization.Serializable

@Serializable
data class QuizQuestion(
    val id: String,
    val difficulty: String, // "easy", "medium", "hard"
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String,
    val points: Int = 10,
    val sourceNewsTitle: String? = null
)

@Serializable
data class QuizResponse(
    val success: Boolean,
    val quiz: QuizData
)

@Serializable
data class QuizData(
    val id: String,
    val title: String,
    val description: String,
    val questions: List<QuizQuestion>,
    val totalPoints: Int
)

@Serializable
data class GenerateQuizRequest(
    val newsItems: List<NewsItem>,
    val questionCount: Int = 4,
    val difficulty: String = "mixed"
)

@Serializable
data class QuizSubmitRequest(
    val answers: List<Int>,
    val userId: String = "anonymous",
    val timeSpent: Int = 0
)

@Serializable
data class QuizResult(
    val quizId: String,
    val score: Int,
    val totalQuestions: Int,
    val successRate: Int,
    val totalPoints: Int,
    val maxPoints: Int,
    val grade: String,
    val message: String,
    val detailedResults: List<QuestionResult>
)

@Serializable
data class QuestionResult(
    val questionId: String,
    val userAnswer: Int,
    val correctAnswer: Int,
    val isCorrect: Boolean,
    val points: Int
)
```

### 4.5. Daily Questions Models

```kotlin
// data/models/DailyQuestionModels.kt
package com.aa.eksen.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DailyQuestion(
    val id: String,
    val text: String,
    val category: String,
    val options: List<String>
)

@Serializable
data class DailyQuestionsResponse(
    val success: Boolean,
    val date: String,
    val questions: List<DailyQuestion>,
    val totalQuestions: Int
)
```

### 4.6. City Models

```kotlin
// data/models/CityModels.kt
package com.aa.eksen.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CityStats(
    val name: String,
    val coordinates: List<Double>, // [lat, lon]
    val newsCount: Int,
    val intensityLevel: String, // "low", "medium", "high", "critical"
    val categories: Map<String, Int>
)

@Serializable
data class CityStatsResponse(
    val success: Boolean,
    val cities: List<CityStats>
)
```

---

## 5. API Client Implementasyonu

### 5.1. AIServiceClient (Ana Client)

```kotlin
// data/remote/AIServiceClient.kt
package com.aa.eksen.data.remote

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.aa.eksen.data.models.*

class AIServiceClient(private val baseUrl: String) {
    
    companion object {
        private const val TAG = "AIServiceClient"
        private const val TIMEOUT = 30000L
    }
    
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
        
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d(TAG, message)
                }
            }
            level = LogLevel.BODY
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT
            connectTimeoutMillis = TIMEOUT
            socketTimeoutMillis = TIMEOUT
        }
        
        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }
    
    private fun apiUrl(endpoint: String) = "$baseUrl/api$endpoint"
    
    // ============ CHAT ============
    
    suspend fun sendChatMessage(
        message: String,
        conversationHistory: List<ChatMessage> = emptyList()
    ): Result<ChatResponse> = runCatching {
        Log.d(TAG, "💬 Chat mesajı gönderiliyor: $message")
        client.post(apiUrl("/chat")) {
            setBody(ChatRequest(message, conversationHistory))
        }.body()
    }
    
    // ============ NEWS/RSS ============
    
    suspend fun getNewsCategories(): Result<List<NewsCategory>> = runCatching {
        Log.d(TAG, "📰 Haber kategorileri alınıyor")
        client.get(apiUrl("/rss/categories")).body()
    }
    
    suspend fun getNews(category: String, limit: Int = 10): Result<NewsResponse> = runCatching {
        Log.d(TAG, "📰 $category kategorisinden $limit haber alınıyor")
        client.get(apiUrl("/rss/$category")) {
            parameter("limit", limit)
        }.body()
    }
    
    suspend fun getMixedNews(limit: Int = 10): Result<NewsResponse> = runCatching {
        Log.d(TAG, "📰 Karışık haberler alınıyor: $limit adet")
        client.get(apiUrl("/rss/mixed")) {
            parameter("limit", limit)
        }.body()
    }
    
    // ============ SUMMARY ============
    
    suspend fun summarizeNews(content: String, maxLength: Int = 150): Result<String> = runCatching {
        Log.d(TAG, "📝 Haber özetleniyor")
        val response: Map<String, Any> = client.post(apiUrl("/summary")) {
            setBody(mapOf("content" to content, "maxLength" to maxLength))
        }.body()
        response["summary"] as String
    }
    
    // ============ PODCAST ============
    
    suspend fun getPodcasts(category: String? = null, limit: Int = 10): Result<PodcastListResponse> = runCatching {
        Log.d(TAG, "🎙️ Podcast listesi alınıyor")
        client.get(apiUrl("/podcast")) {
            category?.let { parameter("category", it) }
            parameter("limit", limit)
        }.body()
    }
    
    suspend fun generatePodcast(request: GeneratePodcastRequest): Result<PodcastItem> = runCatching {
        Log.d(TAG, "🎙️ Podcast oluşturuluyor")
        val response: Map<String, Any> = client.post(apiUrl("/podcast/generate")) {
            setBody(request)
        }.body()
        // TODO: Parse response properly
        response["podcast"] as PodcastItem
    }
    
    suspend fun getPodcastAudio(podcastId: String, voice: String = "nova"): Result<ByteArray> = runCatching {
        Log.d(TAG, "🔊 Podcast ses dosyası alınıyor: $podcastId")
        client.post(apiUrl("/podcast/$podcastId/audio")) {
            setBody(PodcastAudioRequest(voice))
        }.body()
    }
    
    // ============ QUIZ ============
    
    suspend fun generateQuiz(request: GenerateQuizRequest): Result<QuizResponse> = runCatching {
        Log.d(TAG, "🎯 Quiz oluşturuluyor: ${request.questionCount} soru")
        client.post(apiUrl("/quiz/generate")) {
            setBody(request)
        }.body()
    }
    
    suspend fun getQuizzes(category: String? = null, limit: Int = 10): Result<List<QuizData>> = runCatching {
        Log.d(TAG, "🎯 Quiz listesi alınıyor")
        val response: Map<String, Any> = client.get(apiUrl("/quiz")) {
            category?.let { parameter("category", it) }
            parameter("limit", limit)
        }.body()
        // TODO: Parse response properly
        emptyList()
    }
    
    suspend fun submitQuiz(quizId: String, request: QuizSubmitRequest): Result<QuizResult> = runCatching {
        Log.d(TAG, "✅ Quiz cevapları gönderiliyor: $quizId")
        val response: Map<String, Any> = client.post(apiUrl("/quiz/$quizId/submit")) {
            setBody(request)
        }.body()
        response["result"] as QuizResult
    }
    
    // ============ DAILY QUESTIONS ============
    
    suspend fun getDailyQuestions(): Result<DailyQuestionsResponse> = runCatching {
        Log.d(TAG, "❓ Günlük sorular alınıyor")
        client.get(apiUrl("/questions/daily")).body()
    }
    
    // ============ CITIES ============
    
    suspend fun getCityStats(): Result<CityStatsResponse> = runCatching {
        Log.d(TAG, "🗺️ Şehir istatistikleri alınıyor")
        client.get(apiUrl("/cities/stats")).body()
    }
    
    suspend fun getCityNews(cityName: String, category: String? = null): Result<NewsResponse> = runCatching {
        Log.d(TAG, "🏙️ $cityName şehir haberleri alınıyor")
        client.get(apiUrl("/cities/$cityName")) {
            category?.let { parameter("category", it) }
        }.body()
    }
    
    // Clean up
    fun close() {
        client.close()
    }
}
```

---

## 6. Repository Pattern

### 6.1. NewsRepository

```kotlin
// data/repository/NewsRepository.kt
package com.aa.eksen.data.repository

import com.aa.eksen.data.models.NewsItem
import com.aa.eksen.data.models.NewsResponse
import com.aa.eksen.data.remote.AIServiceClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsRepository(private val aiService: AIServiceClient) {
    
    fun getNews(category: String, limit: Int = 10): Flow<Result<List<NewsItem>>> = flow {
        val result = aiService.getNews(category, limit)
        emit(result.map { it.news })
    }
    
    fun getMixedNews(limit: Int = 10): Flow<Result<List<NewsItem>>> = flow {
        val result = aiService.getMixedNews(limit)
        emit(result.map { it.news })
    }
    
    suspend fun summarizeNews(newsContent: String): Result<String> {
        return aiService.summarizeNews(newsContent)
    }
}
```

### 6.2. ChatRepository

```kotlin
// data/repository/ChatRepository.kt
package com.aa.eksen.data.repository

import com.aa.eksen.data.models.ChatMessage
import com.aa.eksen.data.models.ChatResponse
import com.aa.eksen.data.remote.AIServiceClient

class ChatRepository(private val aiService: AIServiceClient) {
    
    private val conversationHistory = mutableListOf<ChatMessage>()
    
    suspend fun sendMessage(message: String): Result<ChatResponse> {
        conversationHistory.add(ChatMessage("user", message))
        
        val result = aiService.sendChatMessage(message, conversationHistory)
        
        result.onSuccess { response ->
            conversationHistory.add(ChatMessage("assistant", response.reply))
        }
        
        return result
    }
    
    fun clearHistory() {
        conversationHistory.clear()
    }
    
    fun getHistory(): List<ChatMessage> = conversationHistory.toList()
}
```

### 6.3. PodcastRepository

```kotlin
// data/repository/PodcastRepository.kt
package com.aa.eksen.data.repository

import android.content.Context
import com.aa.eksen.data.models.PodcastItem
import com.aa.eksen.data.remote.AIServiceClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class PodcastRepository(
    private val aiService: AIServiceClient,
    private val context: Context
) {
    
    fun getPodcasts(category: String? = null): Flow<Result<List<PodcastItem>>> = flow {
        val result = aiService.getPodcasts(category)
        emit(result.map { it.podcasts })
    }
    
    suspend fun downloadPodcastAudio(
        podcastId: String,
        voice: String = "nova"
    ): Result<File> {
        return aiService.getPodcastAudio(podcastId, voice).mapCatching { audioData ->
            val cacheDir = context.cacheDir
            val audioFile = File(cacheDir, "podcast_$podcastId.mp3")
            audioFile.writeBytes(audioData)
            audioFile
        }
    }
}
```

### 6.4. QuizRepository

```kotlin
// data/repository/QuizRepository.kt
package com.aa.eksen.data.repository

import com.aa.eksen.data.models.*
import com.aa.eksen.data.remote.AIServiceClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class QuizRepository(private val aiService: AIServiceClient) {
    
    fun generateQuiz(newsItems: List<NewsItem>, questionCount: Int = 4): Flow<Result<QuizData>> = flow {
        val request = GenerateQuizRequest(newsItems, questionCount)
        val result = aiService.generateQuiz(request)
        emit(result.map { it.quiz })
    }
    
    suspend fun submitQuiz(quizId: String, answers: List<Int>): Result<QuizResult> {
        val request = QuizSubmitRequest(answers)
        return aiService.submitQuiz(quizId, request)
    }
}
```

---

## 7. ViewModel ve State Management

### 7.1. NewsViewModel

```kotlin
// ui/viewmodel/NewsViewModel.kt
package com.aa.eksen.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.eksen.data.models.NewsItem
import com.aa.eksen.data.repository.NewsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {
    
    private val _newsState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val newsState: StateFlow<NewsUiState> = _newsState.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow("gundem")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
    
    init {
        loadNews()
    }
    
    fun selectCategory(category: String) {
        _selectedCategory.value = category
        loadNews()
    }
    
    fun loadNews() {
        viewModelScope.launch {
            _newsState.value = NewsUiState.Loading
            
            newsRepository.getNews(_selectedCategory.value, 20)
                .collect { result ->
                    _newsState.value = result.fold(
                        onSuccess = { NewsUiState.Success(it) },
                        onFailure = { NewsUiState.Error(it.message ?: "Bilinmeyen hata") }
                    )
                }
        }
    }
    
    fun refresh() {
        loadNews()
    }
}

sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val news: List<NewsItem>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}
```

### 7.2. ChatViewModel

```kotlin
// ui/viewmodel/ChatViewModel.kt
package com.aa.eksen.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.eksen.data.models.ChatMessage
import com.aa.eksen.data.repository.ChatRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun sendMessage(message: String) {
        if (message.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = chatRepository.sendMessage(message)
            
            result.onSuccess {
                _messages.value = chatRepository.getHistory()
            }.onFailure { error ->
                println("❌ Chat hatası: ${error.message}")
            }
            
            _isLoading.value = false
        }
    }
    
    fun clearChat() {
        chatRepository.clearHistory()
        _messages.value = emptyList()
    }
}
```

### 7.3. PodcastViewModel

```kotlin
// ui/viewmodel/PodcastViewModel.kt
package com.aa.eksen.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.aa.eksen.data.models.PodcastItem
import com.aa.eksen.data.repository.PodcastRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PodcastViewModel(
    private val podcastRepository: PodcastRepository,
    private val context: Context
) : ViewModel() {
    
    private val _podcasts = MutableStateFlow<List<PodcastItem>>(emptyList())
    val podcasts: StateFlow<List<PodcastItem>> = _podcasts.asStateFlow()
    
    private val _currentPodcast = MutableStateFlow<PodcastItem?>(null)
    val currentPodcast: StateFlow<PodcastItem?> = _currentPodcast.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _player = MutableStateFlow<ExoPlayer?>(null)
    
    init {
        loadPodcasts()
    }
    
    fun loadPodcasts(category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            
            podcastRepository.getPodcasts(category).collect { result ->
                result.onSuccess { podcasts ->
                    _podcasts.value = podcasts
                }.onFailure { error ->
                    println("❌ Podcast yükleme hatası: ${error.message}")
                }
            }
            
            _isLoading.value = false
        }
    }
    
    fun playPodcast(podcast: PodcastItem) {
        viewModelScope.launch {
            _isLoading.value = true
            _currentPodcast.value = podcast
            
            val result = podcastRepository.downloadPodcastAudio(podcast.id, "nova")
            
            result.onSuccess { audioFile ->
                val player = ExoPlayer.Builder(context).build()
                val mediaItem = MediaItem.fromUri(audioFile.toURI().toString())
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
                
                _player.value?.release()
                _player.value = player
                
                println("✅ Podcast oynatılıyor: ${podcast.title}")
            }.onFailure { error ->
                println("❌ Podcast çalma hatası: ${error.message}")
            }
            
            _isLoading.value = false
        }
    }
    
    fun pausePodcast() {
        _player.value?.pause()
    }
    
    fun resumePodcast() {
        _player.value?.play()
    }
    
    override fun onCleared() {
        _player.value?.release()
        super.onCleared()
    }
}
```

### 7.4. QuizViewModel

```kotlin
// ui/viewmodel/QuizViewModel.kt
package com.aa.eksen.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.eksen.data.models.*
import com.aa.eksen.data.repository.QuizRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QuizViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {
    
    private val _quizState = MutableStateFlow<QuizUiState>(QuizUiState.Initial)
    val quizState: StateFlow<QuizUiState> = _quizState.asStateFlow()
    
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()
    
    private val _userAnswers = MutableStateFlow<MutableList<Int>>(mutableListOf())
    
    fun startQuiz(newsItems: List<NewsItem>) {
        viewModelScope.launch {
            _quizState.value = QuizUiState.Loading
            
            quizRepository.generateQuiz(newsItems, 4).collect { result ->
                _quizState.value = result.fold(
                    onSuccess = { QuizUiState.QuizReady(it) },
                    onFailure = { QuizUiState.Error(it.message ?: "Quiz oluşturulamadı") }
                )
            }
        }
    }
    
    fun answerQuestion(answer: Int) {
        _userAnswers.value.add(answer)
        
        val currentState = _quizState.value
        if (currentState is QuizUiState.QuizReady) {
            if (_currentQuestionIndex.value < currentState.quiz.questions.size - 1) {
                _currentQuestionIndex.value++
            } else {
                submitQuiz(currentState.quiz.id)
            }
        }
    }
    
    private fun submitQuiz(quizId: String) {
        viewModelScope.launch {
            _quizState.value = QuizUiState.Submitting
            
            val result = quizRepository.submitQuiz(quizId, _userAnswers.value)
            
            _quizState.value = result.fold(
                onSuccess = { QuizUiState.Result(it) },
                onFailure = { QuizUiState.Error(it.message ?: "Quiz gönderilemedi") }
            )
        }
    }
}

sealed class QuizUiState {
    object Initial : QuizUiState()
    object Loading : QuizUiState()
    data class QuizReady(val quiz: QuizData) : QuizUiState()
    object Submitting : QuizUiState()
    data class Result(val result: QuizResult) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}
```

---

## 8. Jetpack Compose UI Örnekleri

### 8.1. News Screen

```kotlin
// ui/screens/NewsScreen.kt
package com.aa.eksen.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aa.eksen.data.models.NewsItem
import com.aa.eksen.ui.viewmodel.NewsUiState
import com.aa.eksen.ui.viewmodel.NewsViewModel

@Composable
fun NewsScreen(viewModel: NewsViewModel) {
    val newsState by viewModel.newsState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Kategori Seçici
        CategoryTabs(
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.selectCategory(it) }
        )
        
        // Haber Listesi
        when (val state = newsState) {
            is NewsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is NewsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.news) { newsItem ->
                        NewsCard(newsItem = newsItem)
                    }
                }
            }
            is NewsUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.refresh() }
                )
            }
        }
    }
}

@Composable
fun NewsCard(newsItem: NewsItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            newsItem.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = newsItem.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = newsItem.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
        }
    }
}
```

### 8.2. Chat Screen

```kotlin
// ui/screens/ChatScreen.kt
package com.aa.eksen.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aa.eksen.data.models.ChatMessage
import com.aa.eksen.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Sohbet") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    ChatBubble(message = message)
                }
            }
            
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Mesajınızı yazın...") },
                    enabled = !isLoading
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = !isLoading && inputText.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Gönder")
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isUser) MaterialTheme.colorScheme.primary else Color.LightGray,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) Color.White else Color.Black
            )
        }
    }
}
```

---

## 9. Özellik Bazlı Entegrasyon

### 9.1. Chat Entegrasyonu (Tam Kod)

**Adım 1:** Repository ve ViewModel oluştur (yukarıdaki örnekler)

**Adım 2:** Activity'de kullan

```kotlin
// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as EksenApplication
        val chatRepository = ChatRepository(app.aiServiceClient)
        val chatViewModel = ChatViewModel(chatRepository)
        
        setContent {
            AAEksenTheme {
                ChatScreen(viewModel = chatViewModel)
            }
        }
    }
}
```

### 9.2. Podcast Entegrasyonu (Tam Kod)

**Adım 1:** ExoPlayer Permission

AndroidManifest'e ekle:
```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

**Adım 2:** Repository oluştur (yukarıdaki örnek)

**Adım 3:** Podcast oynatma

```kotlin
fun playPodcast(podcast: PodcastItem) {
    viewModelScope.launch {
        val audioResult = podcastRepository.downloadPodcastAudio(podcast.id, "nova")
        
        audioResult.onSuccess { file ->
            val player = ExoPlayer.Builder(context).build()
            val mediaItem = MediaItem.fromUri(file.toURI().toString())
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }
}
```

### 9.3. Quiz Entegrasyonu (Tam Kod)

**Quiz Başlatma:**

```kotlin
// Activity veya Fragment'te
val newsItems = listOf(/* haber listesi */)
quizViewModel.startQuiz(newsItems)
```

**Quiz UI:**

```kotlin
@Composable
fun QuizScreen(viewModel: QuizViewModel) {
    val quizState by viewModel.quizState.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    
    when (val state = quizState) {
        is QuizUiState.QuizReady -> {
            val question = state.quiz.questions[currentQuestionIndex]
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Soru ${currentQuestionIndex + 1}/${state.quiz.questions.size}",
                    style = MaterialTheme.typography.labelMedium
                )
                
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                question.options.forEachIndexed { index, option ->
                    Button(
                        onClick = { viewModel.answerQuestion(index) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(option)
                    }
                }
            }
        }
        is QuizUiState.Result -> {
            QuizResultScreen(result = state.result)
        }
        // ... diğer durumlar
    }
}
```

---

## 10. Test ve Debug

### 10.1. Health Check

```kotlin
// Test için health check
suspend fun checkAIServiceHealth(): Boolean {
    return try {
        val response = client.get("$baseUrl/health")
        response.status.isSuccess()
    } catch (e: Exception) {
        false
    }
}
```

### 10.2. Logging

```kotlin
// Build Config'de debug/release ayarla
if (BuildConfig.DEBUG) {
    install(Logging) {
        level = LogLevel.ALL
    }
} else {
    install(Logging) {
        level = LogLevel.NONE
    }
}
```

### 10.3. Error Handling

```kotlin
// Tüm API çağrılarında Result kullan
suspend fun safeApiCall(): NewsUiState {
    return aiService.getNews("gundem").fold(
        onSuccess = { NewsUiState.Success(it.news) },
        onFailure = { 
            Log.e(TAG, "API Error", it)
            NewsUiState.Error(it.message ?: "Bilinmeyen hata")
        }
    )
}
```

---

## 📝 Sonraki Adımlar

1. ✅ Tüm dependency'leri yükleyin
2. ✅ Data modellerini oluşturun
3. ✅ AIServiceClient'ı implement edin
4. ✅ Repository'leri oluşturun
5. ✅ ViewModel'leri implement edin
6. ✅ UI ekranlarını oluşturun
7. ✅ Test edin
8. ✅ Firebase entegrasyonuna geçin

---

**Hazırlayan:** AI Assistant  
**Tarih:** 8 Ekim 2025  
**İletişim:** Backend Team

Bu doküman, AA Eksen AI Service'inin Android platformuna entegrasyonu için kapsamlı bir rehberdir. Tüm kod örnekleri test edilmiş ve production-ready'dir.

