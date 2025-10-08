# 🤖 AA EKSEN AI SERVICE - Kotlin/Android Entegrasyon Rehberi

## 📋 İçindekiler
1. [Genel Bakış](#genel-bakış)
2. [Kotlin Setup](#kotlin-setup)
3. [OpenAI Özellikleri](#openai-özellikleri)
4. [API Endpoint'leri](#api-endpointleri)
5. [Kotlin Kod Örnekleri](#kotlin-kod-örnekleri)
6. [Repository Pattern](#repository-pattern)
7. [ViewModel Entegrasyonu](#viewmodel-entegrasyonu)
8. [Jetpack Compose UI](#jetpack-compose-ui)
9. [Hata Yönetimi](#hata-yönetimi)

---

## 🎯 Genel Bakış

### Backend Bilgileri
**AI Service URL:** `http://10.0.2.2:3001` (Android Emulator)  
**AI Service URL:** `http://localhost:3001` (Fiziksel Cihaz - aynı ağda)  
**Production URL:** TBD (Firebase'e deploy sonrası)

**Backend Teknolojileri:**
- Node.js + Express
- OpenAI GPT-4o-mini (Chat & Text Generation)
- OpenAI TTS-1-HD (Text-to-Speech)
- AA RSS Feed Integration
- Cron Jobs (Günlük/Haftalık görevler)

### Android Gereksinimler
- **Minimum SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Kotlin:** 1.9+
- **Compose:** 1.5+
- **Ktor Client:** 2.3+

---

## 🔧 Kotlin Setup

### 1. Gradle Dependencies

**build.gradle.kts (Project):**
```kotlin
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" apply false
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
        versionName = "1.0"
        
        // AI Service URL
        buildConfigField("String", "AI_SERVICE_URL", "\"http://10.0.2.2:3001\"")
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}

dependencies {
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation("androidx.activity:activity-compose:1.7.0")
    
    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    
    // Ktor Client (HTTP)
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-android:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("io.ktor:ktor-client-logging:2.3.5")
    
    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Coil (Image Loading)
    implementation("io.coil-kt:coil-compose:2.4.0")
    
    // ExoPlayer (Podcast Audio)
    implementation("androidx.media3:media3-exoplayer:1.1.1")
    implementation("androidx.media3:media3-ui:1.1.1")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

### 2. AndroidManifest.xml

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- İnternet izni -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application
        android:name=".EksenApplication"
        android:allowBackup="true"
        android:usesCleartextTraffic="true"
        ...>
        
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
    </application>
</manifest>
```

### 3. Application Class

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
        
        // AI Service client'ı başlat
        aiServiceClient = AIServiceClient(BuildConfig.AI_SERVICE_URL)
        
        // Logging
        if (BuildConfig.DEBUG) {
            println("🤖 AI Service başlatıldı: ${BuildConfig.AI_SERVICE_URL}")
        }
    }
}
```

---

## ✅ OpenAI Özellikleri (Aktif)

### 1. **Chat Completion (GPT-4o-mini)**
- ✅ **Durum:** TAM AKTİF
- **Kullanım:** Kullanıcı sohbeti, soru-cevap
- **Model:** `gpt-4o-mini`
- **Özellikler:**
  - Gerçek haber verileriyle entegre
  - Kategori bazlı haber filtreleme
  - Context-aware yanıtlar

### 2. **Haber Özetleme**
- ✅ **Durum:** TAM AKTİF
- **Kullanım:** Uzun haberleri 3-4 cümleye indirme
- **Özellikler:**
  - Anahtar kelime çıkarma
  - Objektif özetleme
  - Markdown formatında çıktı

### 3. **Timeline Analizi**
- ✅ **Durum:** TAM AKTİF
- **Kullanım:** Haberleri kronolojik zaman tüneli olarak düzenleme
- **Özellikler:**
  - İlişkili haberleri gruplama
  - Zaman çizelgesi oluşturma
  - JSON formatında döndürme

### 4. **Podcast Üretimi (Text + Audio)**
- ✅ **Durum:** TAM AKTİF
- **Kullanım:**
  - **Text Generation:** Haberden doğal podcast metni üretme
  - **TTS (Text-to-Speech):** OpenAI TTS-1-HD ile ses oluşturma
- **Özellikler:**
  - 6 farklı ses tonu (nova, shimmer, alloy, echo, fable, onyx)
  - Duygusal ve doğal konuşma tarzı
  - MP3 formatında ses dosyası
  - 2-3 dakikalık podcast'ler

### 5. **Günlük Sorular Üretimi**
- ✅ **Durum:** TAM AKTİF
- **Kullanım:** Kullanıcı etkileşimi için günlük sorular
- **Özellikler:**
  - Cron job ile her sabah 6:00'da otomatik
  - 3 farklı kategori
  - JSON formatında

### 6. **Quiz Üretimi**
- ✅ **Durum:** TAM AKTİF
- **Kullanım:** Haberlerden quiz soruları oluşturma
- **Özellikler:**
  - 4 seçenekli sorular
  - Açıklamalı cevaplar
  - Gamification için puan sistemi

### 7. **Kategori Özetleri**
- ✅ **Durum:** TAM AKTİF
- **Kullanım:** Ekonomi, Spor, Teknoloji vb. kategorilerde haftalık özetler
- **Özellikler:**
  - Kronolojik analiz
  - Önemli veriler
  - Gelecek beklentileri

---

## 📡 API Endpoint'leri

### Base URL

**⚠️ ÖNEMLİ:** Mobil için `localhost` ÇALIŞMAZ!

```
# Android Emulator için:
http://10.0.2.2:3001/api

# Fiziksel cihaz için (aynı WiFi ağında):
http://[BILGISAYARIN_IP]:3001/api
# Örnek: http://192.168.1.100:3001/api

# Web için (referans):
http://localhost:3001/api
```

**Bilgisayarın IP Adresini Öğrenme:**
- Windows: `ipconfig` (cmd'de)
- Mac/Linux: `ifconfig` veya `ip addr`

---

### 1. 💬 Chat (Sohbet)

#### **POST /api/chat**
Kullanıcı sorusu gönderin, AI cevap alsın.

**Request:**
```json
{
  "message": "Bugünün ekonomi haberleri neler?",
  "conversationHistory": []
}
```

**Response:**
```json
{
  "reply": "Bugün ekonomi gündeminde Merkez Bankası'nın faiz kararı...",
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

**Kotlin Örneği:**
```kotlin
data class ChatRequest(
    val message: String,
    val conversationHistory: List<Message> = emptyList()
)

data class ChatResponse(
    val reply: String,
    val timestamp: String
)

suspend fun sendChatMessage(message: String): ChatResponse {
    val response = httpClient.post("http://localhost:3001/api/chat") {
        contentType(ContentType.Application.Json)
        setBody(ChatRequest(message = message))
    }
    return response.body()
}
```

---

### 2. 📰 RSS (Haber Çekme)

#### **GET /api/rss/categories**
Tüm kategorileri listele.

**Response:**
```json
{
  "categories": [
    {"id": "gundem", "name": "Gündem"},
    {"id": "ekonomi", "name": "Ekonomi"},
    {"id": "spor", "name": "Spor"}
  ]
}
```

#### **GET /api/rss/:category**
Belirli kategoriden haberleri çek.

**Örnek:** `GET /api/rss/ekonomi?limit=10`

**Response:**
```json
{
  "success": true,
  "category": "ekonomi",
  "news": [
    {
      "id": "abc123",
      "title": "Merkez Bankası faiz kararını açıkladı",
      "description": "...",
      "link": "https://...",
      "pubDate": "2024-01-15T08:00:00.000Z",
      "imageUrl": "https://..."
    }
  ],
  "total": 10
}
```

**Kotlin Örneği:**
```kotlin
data class NewsItem(
    val id: String,
    val title: String,
    val description: String,
    val link: String,
    val pubDate: String,
    val imageUrl: String?
)

data class RssResponse(
    val success: Boolean,
    val category: String,
    val news: List<NewsItem>,
    val total: Int
)

suspend fun getNews(category: String, limit: Int = 10): RssResponse {
    val response = httpClient.get("http://localhost:3001/api/rss/$category?limit=$limit")
    return response.body()
}
```

---

### 3. 📝 Summary (Özet)

#### **POST /api/summary**
Haber metnini özetle.

**Request:**
```json
{
  "content": "Uzun haber metni...",
  "maxLength": 150
}
```

**Response:**
```json
{
  "summary": "**Özet:** Merkez Bankası faiz kararı...\n**Anahtar Kelimeler:** #faiz #ekonomi",
  "originalLength": 500,
  "summaryLength": 120
}
```

**Kotlin Örneği:**
```kotlin
data class SummaryRequest(
    val content: String,
    val maxLength: Int = 150
)

suspend fun summarizeNews(content: String): String {
    val response = httpClient.post("http://localhost:3001/api/summary") {
        contentType(ContentType.Application.Json)
        setBody(SummaryRequest(content = content))
    }
    return response.body<Map<String, Any>>()["summary"] as String
}
```

---

### 4. ⏰ Timeline (Zaman Tüneli)

#### **GET /api/timeline**
Haberlerden zaman tüneli oluştur.

**Response:**
```json
{
  "timelines": [
    {
      "id": "timeline_1",
      "title": "Seçim Süreci 2024",
      "category": "Siyaset",
      "events": [
        {
          "date": "2024-01-10",
          "title": "Aday açıklamaları başladı",
          "description": "..."
        }
      ]
    }
  ]
}
```

---

### 5. 🎙️ Podcast

#### **GET /api/podcast**
Podcast listesini getir.

**Query Params:**
- `category` (optional): ekonomi, spor, teknoloji
- `limit` (default: 10)

**Response:**
```json
{
  "success": true,
  "podcasts": [
    {
      "id": "podcast_1",
      "title": "🎙️ Günün Ekonomi Haberleri",
      "description": "Merkez Bankası kararları...",
      "duration": 180,
      "category": "Ekonomi",
      "createdAt": "2024-01-15T06:00:00.000Z",
      "listenCount": 45
    }
  ],
  "total": 10
}
```

#### **POST /api/podcast/generate**
Haberden podcast üret.

**Request:**
```json
{
  "newsContent": "Haber metni...",
  "title": "Günün Ekonomi Haberleri",
  "category": "Ekonomi",
  "tone": "friendly"
}
```

**Response:**
```json
{
  "success": true,
  "podcast": {
    "id": "podcast_123",
    "title": "🎙️ Günün Ekonomi Haberleri",
    "content": "Merhabalar sevgili dinleyiciler! Ben Arda...",
    "duration": 180
  }
}
```

#### **POST /api/podcast/:podcastId/audio**
Podcast için ses dosyası oluştur (TTS).

**Request:**
```json
{
  "voice": "nova"
}
```

**Sesler:** `nova`, `shimmer`, `alloy`, `echo`, `fable`, `onyx`

**Response:** MP3 audio stream (binary)

**Kotlin Örneği:**
```kotlin
suspend fun getPodcastAudio(podcastId: String, voice: String = "nova"): ByteArray {
    val response = httpClient.post("http://localhost:3001/api/podcast/$podcastId/audio") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("voice" to voice))
    }
    return response.readBytes()
}

// Ses dosyasını kaydet
fun savePodcastAudio(audioData: ByteArray, podcastId: String) {
    val file = File(context.cacheDir, "podcast_$podcastId.mp3")
    file.writeBytes(audioData)
    
    // MediaPlayer ile çal
    val mediaPlayer = MediaPlayer()
    mediaPlayer.setDataSource(file.absolutePath)
    mediaPlayer.prepare()
    mediaPlayer.start()
}
```

---

### 6. ❓ Questions (Günlük Sorular)

#### **GET /api/questions/daily**
Bugünün sorularını al.

**Response:**
```json
{
  "success": true,
  "date": "2024-01-15",
  "questions": [
    {
      "id": "q1",
      "text": "Bugün en çok hangi haber kategorisini okudunuz?",
      "category": "gundem",
      "options": ["Ekonomi", "Spor", "Teknoloji", "Siyaset"]
    }
  ]
}
```

---

### 7. 🎯 Quiz

#### **POST /api/quiz/generate**
Haberlerden quiz oluştur.

**Request:**
```json
{
  "newsItems": [
    {
      "title": "...",
      "content": "..."
    }
  ]
}
```

**Response:**
```json
{
  "quiz": [
    {
      "question": "Merkez Bankası faiz oranını ne kadar artırdı?",
      "options": ["2.5%", "5%", "7.5%", "10%"],
      "correctAnswer": 1,
      "explanation": "Merkez Bankası faiz oranını 5% artırmıştır."
    }
  ]
}
```

---

### 8. 🗺️ Cities (Şehir Bazlı Haberler)

#### **GET /api/cities/stats**
Tüm şehirlerin haber istatistiklerini al.

**Response:**
```json
{
  "success": true,
  "cities": [
    {
      "name": "İstanbul",
      "coordinates": [41.0082, 28.9784],
      "newsCount": 45,
      "intensityLevel": "high",
      "categories": {
        "ekonomi": 15,
        "spor": 12,
        "gundem": 18
      }
    }
  ]
}
```

#### **GET /api/cities/:cityName**
Belirli şehrin haberlerini al.

**Örnek:** `GET /api/cities/istanbul?category=ekonomi`

---

---

## 📱 Kotlin Kod Örnekleri

### 1. Data Models

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
    val imageUrl: String? = null
)

@Serializable
data class NewsResponse(
    val success: Boolean,
    val category: String,
    val news: List<NewsItem>,
    val total: Int
)

@Serializable
data class NewsCategory(
    val id: String,
    val name: String
)
```

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

### 2. HTTP Client (Ktor)

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

class AIServiceClient(private val baseUrl: String) {
    
    companion object {
        private const val TAG = "AIServiceClient"
        private const val TIMEOUT = 30000L // 30 saniye
    }
    
    private val client = HttpClient(Android) {
        // JSON Serialization
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
        
        // Logging (sadece debug modda)
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d(TAG, message)
                }
            }
            level = LogLevel.BODY
        }
        
        // Timeout
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT
            connectTimeoutMillis = TIMEOUT
            socketTimeoutMillis = TIMEOUT
        }
        
        // Default Headers
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
        client.post(apiUrl("/chat")) {
            setBody(ChatRequest(message, conversationHistory))
        }.body()
    }
    
    // ============ NEWS/RSS ============
    
    suspend fun getNewsCategories(): Result<List<NewsCategory>> = runCatching {
        client.get(apiUrl("/rss/categories")).body()
    }
    
    suspend fun getNews(category: String, limit: Int = 10): Result<NewsResponse> = runCatching {
        client.get(apiUrl("/rss/$category")) {
            parameter("limit", limit)
        }.body()
    }
    
    suspend fun getMixedNews(limit: Int = 10): Result<NewsResponse> = runCatching {
        client.get(apiUrl("/rss/mixed")) {
            parameter("limit", limit)
        }.body()
    }
    
    // ============ SUMMARY ============
    
    suspend fun summarizeNews(content: String, maxLength: Int = 150): Result<String> = runCatching {
        val response: Map<String, Any> = client.post(apiUrl("/summary")) {
            setBody(mapOf("content" to content, "maxLength" to maxLength))
        }.body()
        response["summary"] as String
    }
    
    // ============ PODCAST ============
    
    suspend fun getPodcasts(category: String? = null, limit: Int = 10): Result<PodcastListResponse> = runCatching {
        client.get(apiUrl("/podcast")) {
            category?.let { parameter("category", it) }
            parameter("limit", limit)
        }.body()
    }
    
    suspend fun generatePodcast(request: GeneratePodcastRequest): Result<PodcastItem> = runCatching {
        val response: Map<String, Any> = client.post(apiUrl("/podcast/generate")) {
            setBody(request)
        }.body()
        // Parse response
        response["podcast"] as PodcastItem
    }
    
    suspend fun getPodcastAudio(podcastId: String, voice: String = "nova"): Result<ByteArray> = runCatching {
        client.post(apiUrl("/podcast/$podcastId/audio")) {
            setBody(PodcastAudioRequest(voice))
        }.body()
    }
    
    // ============ CITIES ============
    
    suspend fun getCityStats(): Result<CityStatsResponse> = runCatching {
        client.get(apiUrl("/cities/stats")).body()
    }
    
    suspend fun getCityNews(cityName: String, category: String? = null): Result<NewsResponse> = runCatching {
        client.get(apiUrl("/cities/$cityName")) {
            category?.let { parameter("category", it) }
        }.body()
    }
    
    // ============ QUESTIONS ============
    
    suspend fun getDailyQuestions(): Result<Map<String, Any>> = runCatching {
        client.get(apiUrl("/questions/daily")).body()
    }
    
    // ============ QUIZ ============
    
    suspend fun generateQuiz(newsItems: List<NewsItem>): Result<Map<String, Any>> = runCatching {
        client.post(apiUrl("/quiz/generate")) {
            setBody(mapOf("newsItems" to newsItems))
        }.body()
    }
    
    // Clean up
    fun close() {
        client.close()
    }
}
```

---

## 🏗️ Repository Pattern

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

```kotlin
// data/repository/ChatRepository.kt
package com.aa.eksen.data.repository

import com.aa.eksen.data.models.ChatMessage
import com.aa.eksen.data.models.ChatResponse
import com.aa.eksen.data.remote.AIServiceClient

class ChatRepository(private val aiService: AIServiceClient) {
    
    private val conversationHistory = mutableListOf<ChatMessage>()
    
    suspend fun sendMessage(message: String): Result<ChatResponse> {
        // Kullanıcı mesajını ekle
        conversationHistory.add(ChatMessage("user", message))
        
        // AI'ya gönder
        val result = aiService.sendChatMessage(message, conversationHistory)
        
        // AI cevabını ekle
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
            // Cache klasörüne kaydet
            val cacheDir = context.cacheDir
            val audioFile = File(cacheDir, "podcast_$podcastId.mp3")
            audioFile.writeBytes(audioData)
            audioFile
        }
    }
}
```

---

## 🎨 ViewModel Entegrasyonu

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
                // Hata mesajını göster
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
            
            // Ses dosyasını indir
            val result = podcastRepository.downloadPodcastAudio(podcast.id, "nova")
            
            result.onSuccess { audioFile ->
                // ExoPlayer ile çal
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

---

## 🎨 Jetpack Compose UI

### News Grid Screen

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
            // Resim
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
            
            // Başlık
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Açıklama
            Text(
                text = newsItem.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
        }
    }
}

@Composable
fun CategoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("gundem", "ekonomi", "spor", "teknoloji", "dunya")
    
    ScrollableTabRow(selectedTabIndex = categories.indexOf(selectedCategory)) {
        categories.forEach { category ->
            Tab(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                text = { Text(category.capitalize()) }
            )
        }
    }
}
```

### Chat Screen

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
            // Mesaj Listesi
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
            
            // Loading Indicator
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            
            // Input Alanı
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

### Podcast Player Screen

```kotlin
// ui/screens/PodcastScreen.kt
package com.aa.eksen.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aa.eksen.data.models.PodcastItem
import com.aa.eksen.ui.viewmodel.PodcastViewModel

@Composable
fun PodcastScreen(viewModel: PodcastViewModel) {
    val podcasts by viewModel.podcasts.collectAsState()
    val currentPodcast by viewModel.currentPodcast.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Aktif Podcast Player
        currentPodcast?.let { podcast ->
            PodcastPlayer(
                podcast = podcast,
                onPlayPause = {
                    // Play/Pause logic
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Divider()
        }
        
        // Podcast Listesi
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(podcasts) { podcast ->
                    PodcastCard(
                        podcast = podcast,
                        onClick = { viewModel.playPodcast(podcast) }
                    )
                }
            }
        }
    }
}

@Composable
fun PodcastCard(
    podcast: PodcastItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = podcast.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${podcast.duration / 60} dakika • ${podcast.category}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun PodcastPlayer(
    podcast: PodcastItem,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = podcast.title,
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
```

---

## ⚠️ Hata Yönetimi

### Standart Hata Formatı
```json
{
  "error": "Hata başlığı",
  "message": "Detaylı açıklama"
}
```

### HTTP Status Kodları
- `200` - Başarılı
- `400` - Kötü istek (eksik parametre)
- `404` - Endpoint/kaynak bulunamadı
- `500` - Sunucu hatası

```kotlin
// utils/ErrorHandler.kt
package com.aa.eksen.utils

import android.util.Log
import io.ktor.client.plugins.*
import io.ktor.utils.io.errors.*
import java.net.UnknownHostException

object ErrorHandler {
    
    fun handleError(error: Throwable): String {
        Log.e("ErrorHandler", "Hata oluştu", error)
        
        return when (error) {
            is UnknownHostException -> "İnternet bağlantınızı kontrol edin"
            is IOException -> "Bağlantı hatası. Lütfen tekrar deneyin."
            is ResponseException -> {
                when (error.response.status.value) {
                    400 -> "Geçersiz istek"
                    404 -> "Kaynak bulunamadı"
                    500 -> "Sunucu hatası. Lütfen daha sonra tekrar deneyin."
                    else -> "Bir hata oluştu (${error.response.status.value})"
                }
            }
            else -> error.message ?: "Bilinmeyen hata"
        }
    }
}

// Kullanım örneği
suspend fun getNewsWithErrorHandling(category: String): NewsUiState {
    return aiService.getNews(category).fold(
        onSuccess = { NewsUiState.Success(it.news) },
        onFailure = { NewsUiState.Error(ErrorHandler.handleError(it)) }
    )
}
```

---

## 🚀 Production Deploy (Firebase Functions)

### Firebase kurulumu

```bash
# Firebase CLI yükle
npm install -g firebase-tools

# Login
firebase login

# Backend klasöründe Firebase başlat
cd "ai-service -2"
firebase init functions

# Deploy
firebase deploy --only functions
```

### Firebase Functions Yapılandırması

```javascript
// functions/index.js
const functions = require('firebase-functions');
const express = require('express');
const app = require('./src/index.js');

// AI Service'i Firebase Function olarak export et
exports.api = functions
  .region('europe-west1') // En yakın region
  .runWith({
    timeoutSeconds: 60,
    memory: '1GB'
  })
  .https.onRequest(app);
```

### Android Build Config (Production)

```kotlin
android {
    buildTypes {
        debug {
            buildConfigField("String", "AI_SERVICE_URL", "\"http://10.0.2.2:3001\"")
        }
        release {
            buildConfigField("String", "AI_SERVICE_URL", "\"https://your-firebase-url.cloudfunctions.net/api\"")
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

---

## 🚀 Production Deploy (Firebase)

### Environment Variables
```env
OPENAI_API_KEY=sk-...
WEB_URL=https://your-domain.com
NODE_ENV=production
PORT=3001
```

### Firebase Functions
```javascript
const functions = require('firebase-functions');
const app = require('./src/index.js');

exports.api = functions.https.onRequest(app);
```

---

## 📊 Cron Jobs (Otomatik Görevler)

### Günlük (Her sabah 6:00)
- ✅ Günlük sorular üretimi
- ✅ Günlük quiz'ler
- ✅ Timeline analizi

### Haftalık (Her Pazartesi 8:00)
- ✅ Haftalık kategori özetleri

**Not:** Mobil uygulama bu endpoint'lere direkt erişebilir, cron job'lar arka planda çalışır.

---

## 🎯 Test Endpoint'leri

### Health Check
```bash
GET http://localhost:3001/health
```

**Response:**
```json
{
  "status": "OK",
  "service": "AI Service",
  "version": "1.0.0",
  "description": "AA Habercilik AI Entegrasyon Servisi"
}
```

---

## 📝 Notlar

1. **OpenAI API Key:** `.env` dosyasında `OPENAI_API_KEY` tanımlı olmalı
2. **CORS:** Mobil için `*` olarak ayarlandı (production'da domain kısıtlaması eklenecek)
3. **Rate Limiting:** Şu anda yok, production'da eklenecek
4. **Caching:** Redis ile implement edilebilir (opsiyonel)

---

## 🤝 Destek

Sorularınız için:
- Backend Developer: [İsim]
- AI Service Docs: Bu dosya
- API Testing: Postman koleksiyonu (isteğe bağlı hazırlanabilir)

---

---

## 📊 Özet Tablo - AI Özellikleri

| Özellik | Durum | OpenAI Model | Endpoint | Mobil Kullanım |
|---------|-------|--------------|----------|----------------|
| **Chat** | ✅ Aktif | GPT-4o-mini | `/api/chat` | Sohbet ekranı |
| **Haber Özetleme** | ✅ Aktif | GPT-4o-mini | `/api/summary` | Haber detay |
| **RSS Feed** | ✅ Aktif | - | `/api/rss/:category` | Ana sayfa |
| **Podcast (Text)** | ✅ Aktif | GPT-4o-mini | `/api/podcast/generate` | Podcast oluştur |
| **Podcast (Audio/TTS)** | ✅ Aktif | TTS-1-HD | `/api/podcast/:id/audio` | Podcast player |
| **Timeline** | ✅ Aktif | GPT-4o-mini | `/api/timeline` | Zaman tüneli |
| **Günlük Sorular** | ✅ Aktif | GPT-4o-mini | `/api/questions/daily` | Anket widget |
| **Quiz** | ✅ Aktif | GPT-4o-mini | `/api/quiz/generate` | Quiz oyunu |
| **Şehir Haberleri** | ✅ Aktif | - | `/api/cities/:city` | Harita |
| **Kategori Özetleri** | ✅ Aktif | GPT-4o-mini | Manuel | Haftalık analiz |

---

## 🎯 Hızlı Başlangıç Kontrol Listesi

### Backend Hazırlığı
- [ ] AI Service çalışıyor mu? (`npm start` in `ai-service -2/`)
- [ ] `http://localhost:3001/health` endpoint'i çalışıyor mu?
- [ ] `.env` dosyasında `OPENAI_API_KEY` tanımlı mı?
- [ ] CORS ayarları mobil için açık mı?

### Android Projesi
- [ ] Gradle dependencies eklendi mi?
- [ ] `AndroidManifest.xml`'de internet izni var mı?
- [ ] `usesCleartextTraffic="true"` eklendi mi?
- [ ] `BuildConfig.AI_SERVICE_URL` doğru mu?
- [ ] `EksenApplication` class'ı oluşturuldu mu?

### Test Adımları
```kotlin
// 1. Health check
val client = AIServiceClient("http://10.0.2.2:3001")

// 2. Chat testi
val chatResult = client.sendChatMessage("Merhaba!")
println("Chat: $chatResult")

// 3. News testi
val newsResult = client.getNews("ekonomi", 5)
println("News: $newsResult")

// 4. Podcast testi
val podcasts = client.getPodcasts()
println("Podcasts: $podcasts")
```

---

## 🆘 Sorun Giderme

### "Connection refused" hatası
**Neden:** Backend çalışmıyor veya URL yanlış  
**Çözüm:**
- Emulator: `http://10.0.2.2:3001` kullanın
- Fiziksel cihaz: Bilgisayarın local IP'sini kullanın (örn: `http://192.168.1.100:3001`)
- Backend'in çalıştığından emin olun

### "CLEARTEXT communication not permitted"
**Neden:** Android 9+ HTTP bağlantılarını engelliyor  
**Çözüm:** `AndroidManifest.xml`'e `android:usesCleartextTraffic="true"` ekleyin

### "OpenAI API Error"
**Neden:** OpenAI API key yanlış veya kotası dolmuş  
**Çözüm:** `.env` dosyasındaki `OPENAI_API_KEY`'i kontrol edin

### Podcast sesi çalınmıyor
**Neden:** Audio stream indirme veya ExoPlayer hatası  
**Çözüm:**
- Logları kontrol edin
- Cache klasörüne ses yazılabildiğinden emin olun
- ExoPlayer dependency'si doğru mu?

---

## 📞 İletişim & Destek

**Backend Developer:** [İsim]  
**AI Service Repository:** `ai-service -2/`  
**Dokümantasyon:** Bu dosya (`mobile-api-docs.md`)  
**Test API:** `http://localhost:3001/health`

**Önemli Notlar:**
1. 🔑 OpenAI API Key'i `.env` dosyasında saklanıyor
2. 🌐 CORS şu anda `*` (herkese açık) - production'da kısıtlanacak
3. ⏱️ Cron job'lar her gün 06:00'da çalışıyor
4. 🎙️ TTS ses dosyaları yaklaşık 100-200 KB (3 dakikalık podcast)
5. 📱 Mobil uygulama için `http://10.0.2.2:3001` (emulator) kullanın

---

**Hazırlayan:** AI Assistant  
**Tarih:** 7 Ekim 2025  
**Versiyon:** 1.0.0  
**Platform:** Kotlin/Android + Node.js Backend


