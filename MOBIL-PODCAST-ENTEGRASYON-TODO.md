# 📱🎙️ Mobil & Podcast Entegrasyon - TODO List

## 🎯 Genel Hedef
1. Kotlin mobil uygulamayı AI servisiyle entegre etmek
2. AI destekli podcast üretim sistemi kurmak

---

## 📱 BÖLÜM 1: MOBİL UYGULAMA ENTEGRASYONU

### Seçenek Analizi: Kotlin → AI Service Bağlantısı

#### ✅ Seçenek 1: REST API (ÖNERİLEN)
**Artıları:**
- ✅ En basit ve standart yöntem
- ✅ Platform bağımsız
- ✅ Mevcut AI servisiniz zaten REST API
- ✅ Kolay test edilebilir
- ✅ Retrofit kullanımı kolay

**Eksileri:**
- ⚠️ Gerçek zamanlı olmayan (her istek-yanıt)
- ⚠️ Network bağımlı

**Kullanım:**
```kotlin
// Retrofit ile AI servise bağlanma
interface AIService {
    @POST("/api/chat")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
    
    @GET("/api/rss/{category}")
    suspend fun getNews(@Path("category") category: String): NewsResponse
}
```

#### 🔄 Seçenek 2: WebSocket
**Artıları:**
- ✅ Gerçek zamanlı iletişim
- ✅ Streaming yanıtlar
- ✅ Chat için ideal

**Eksileri:**
- ❌ Daha karmaşık implementasyon
- ❌ Sunucu tarafında WebSocket desteği gerekli
- ❌ Bağlantı yönetimi zor

#### 🌐 Seçenek 3: Firebase Cloud Functions
**Artıları:**
- ✅ Otomatik ölçekleme
- ✅ Offline destek

**Eksileri:**
- ❌ Ek maliyet
- ❌ Vendor lock-in

### ✨ ÖNERİ: REST API + Retrofit

---

## 📋 TODO 1: MOBİL APP - AI SERVİS ENTEGRASYONU

### 1.1 Backend Hazırlık (AI Service)
- [ ] **YÜKSEK ÖNCELİK** - CORS ayarlarını mobil için güncelle
  ```javascript
  // ai-service-2/src/index.js
  app.use(cors({
    origin: '*', // Veya specific mobil app domain
    credentials: true
  }));
  ```

- [ ] **YÜKSEK ÖNCELİK** - Mobil için özel endpoint'ler ekle
  - [ ] `/api/mobile/news` - Mobil optimize edilmiş haber listesi
  - [ ] `/api/mobile/chat` - Mobil chat endpoint
  - [ ] `/api/mobile/podcast` - Podcast listesi
  - [ ] `/api/mobile/profile` - Kullanıcı profili

- [ ] **ORTA ÖNCELİK** - API dokumentasyonu hazırla
  - [ ] Swagger/OpenAPI entegrasyonu
  - [ ] Endpoint örnekleri
  - [ ] Response formatları

- [ ] **ORTA ÖNCELİK** - Rate limiting ekle
  ```javascript
  import rateLimit from 'express-rate-limit';
  const limiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 dakika
    max: 100 // max 100 istek
  });
  ```

### 1.2 Mobil App - Network Layer (Kotlin)
- [ ] **YÜKSEK ÖNCELİK** - Retrofit setup
  ```kotlin
  // build.gradle dependencies ekle
  implementation 'com.squareup.retrofit2:retrofit:2.9.0'
  implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
  implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
  ```

- [ ] **YÜKSEK ÖNCELİK** - API service interface oluştur
  - [ ] `AIApiService.kt` - Chat, news endpoints
  - [ ] `PodcastApiService.kt` - Podcast endpoints
  - [ ] `AuthApiService.kt` - Auth endpoints (gelecek için)

- [ ] **YÜKSEK ÖNCELİK** - Data modelleri oluştur
  - [ ] `ChatMessage.kt`
  - [ ] `NewsItem.kt`
  - [ ] `PodcastEpisode.kt`
  - [ ] `ApiResponse.kt` (generic wrapper)

- [ ] **ORTA ÖNCELİK** - Repository pattern implementasyonu
  - [ ] `NewsRepository.kt`
  - [ ] `ChatRepository.kt`
  - [ ] `PodcastRepository.kt`

- [ ] **ORTA ÖNCELİK** - Error handling
  - [ ] Network error handling
  - [ ] Timeout handling
  - [ ] Retry mechanism

### 1.3 Mobil UI - AI Chat Ekranı
- [ ] **YÜKSEK ÖNCELİK** - Chat UI tasarımı
  - [ ] RecyclerView ile mesaj listesi
  - [ ] User/AI mesaj bubble'ları
  - [ ] Typing indicator
  - [ ] Send button

- [ ] **ORTA ÖNCELİK** - Chat özellikler
  - [ ] Mesaj gönderme
  - [ ] AI yanıtını alma
  - [ ] Conversation history
  - [ ] Quick prompts (hızlı sorular)

- [ ] **DÜŞÜK ÖNCELİK** - Ekstra özellikler
  - [ ] Voice input (sesli mesaj)
  - [ ] Copy message
  - [ ] Share conversation

### 1.4 Mobil UI - Haberler Ekranı
- [ ] **YÜKSEK ÖNCELİK** - News list UI
  - [ ] RecyclerView ile haber listesi
  - [ ] Kategori filtreleme
  - [ ] Pull-to-refresh

- [ ] **ORTA ÖNCELİK** - News detail ekranı
  - [ ] Haber detay sayfası
  - [ ] AI özet gösterme
  - [ ] Paylaşma özelliği

### 1.5 Testing
- [ ] **ORTA ÖNCELİK** - Unit testler
  - [ ] Repository testleri
  - [ ] ViewModel testleri

- [ ] **ORTA ÖNCELİK** - API testleri
  - [ ] Postman collection
  - [ ] API response validation

---

## 🎙️ BÖLÜM 2: AI PODCAST ÜRETİM SİSTEMİ

### 2.1 Podcast İçerik Üretimi (AI)
- [ ] **YÜKSEK ÖNCELİK** - Podcast script generator
  ```javascript
  // ai-service-2/src/services/podcastGenerator.js
  async generatePodcastScript(topic, duration) {
    // ChatGPT ile sohbet tarzı içerik üret
    // Bölümler: Giriş, Ana içerik, Sonuç
  }
  ```

- [ ] **YÜKSEK ÖNCELİK** - Konu seçimi stratejisi
  - [ ] Günün en popüler haberlerini analiz et
  - [ ] Kategori bazlı podcast konuları
  - [ ] Trending topics

- [ ] **ORTA ÖNCELİK** - Script yapısı
  - [ ] Giriş müziği placeholder
  - [ ] Ana konuşma metni
  - [ ] Geçiş cümleleri
  - [ ] Kapanış

### 2.2 Text-to-Speech (TTS) Entegrasyonu

#### Seçenek Analizi: TTS Servisleri

**🏆 Seçenek 1: OpenAI TTS (ÖNERİLEN)**
- ✅ Çok doğal ses
- ✅ Türkçe desteği mükemmel
- ✅ Zaten OpenAI kullanıyorsunuz
- ⚠️ Ücretli (ama uygun)
- Fiyat: ~$15/1M karakter

**Seçenek 2: Google Cloud TTS**
- ✅ İyi Türkçe desteği
- ✅ WaveNet kaliteli
- ⚠️ Ücretli
- Fiyat: ~$16/1M karakter

**Seçenek 3: Azure TTS**
- ✅ Neural voices
- ✅ Türkçe desteği var
- ⚠️ Ücretli
- Fiyat: ~$16/1M karakter

**Seçenek 4: ElevenLabs**
- ✅ EN doğal ses
- ❌ Türkçe desteği sınırlı
- ❌ Pahalı

### 2.3 Podcast TTS İmplementasyonu
- [ ] **YÜKSEK ÖNCELİK** - OpenAI TTS entegrasyonu
  ```javascript
  // ai-service-2/src/services/ttsService.js
  import OpenAI from 'openai';
  
  async function generateSpeech(text, voice = 'alloy') {
    const mp3 = await openai.audio.speech.create({
      model: "tts-1",
      voice: voice,
      input: text,
      language: 'tr'
    });
    
    return mp3.body; // Audio stream
  }
  ```

- [ ] **YÜKSEK ÖNCELİK** - Ses dosyası yönetimi
  - [ ] Audio storage (S3, local disk)
  - [ ] File naming convention
  - [ ] Metadata (duration, size, etc.)

- [ ] **ORTA ÖNCELİK** - Ses kalitesi optimizasyonu
  - [ ] Bitrate ayarları
  - [ ] Format seçimi (MP3, AAC)
  - [ ] Compression

### 2.4 Podcast Backend API
- [ ] **YÜKSEK ÖNCELİK** - Podcast endpoints
  ```javascript
  // Routes
  POST /api/podcast/generate - Yeni podcast üret
  GET /api/podcast/list - Podcast listesi
  GET /api/podcast/:id - Podcast detay
  GET /api/podcast/:id/audio - Audio dosyası stream
  DELETE /api/podcast/:id - Podcast sil
  ```

- [ ] **YÜKSEK ÖNCELİK** - Podcast data modeli
  ```javascript
  {
    id: string,
    title: string,
    description: string,
    topic: string,
    category: string,
    duration: number, // saniye
    audioUrl: string,
    transcript: string, // Script text
    createdAt: Date,
    publishedAt: Date,
    status: 'generating' | 'ready' | 'published'
  }
  ```

- [ ] **ORTA ÖNCELİK** - Podcast generation queue
  - [ ] Bull/Redis queue sistemi
  - [ ] Background job processing
  - [ ] Status tracking

### 2.5 Podcast Otomasyonu
- [ ] **ORTA ÖNCELİK** - Günlük podcast üretimi
  ```javascript
  // Cron job - Her gün sabah 7:00
  cron.schedule('0 7 * * *', async () => {
    const topics = await selectDailyTopics();
    for (const topic of topics) {
      await generatePodcast(topic);
    }
  });
  ```

- [ ] **ORTA ÖNCELİK** - Konu seçimi algoritması
  - [ ] En çok okunan haberler
  - [ ] Trending topics
  - [ ] Kategori çeşitliliği

- [ ] **DÜŞÜK ÖNCELİK** - Kalite kontrol
  - [ ] Script review
  - [ ] Audio quality check
  - [ ] Manual approval (opsiyonel)

### 2.6 Podcast UI (Mobil & Web)
- [ ] **YÜKSEK ÖNCELİK** - Podcast listesi UI
  - [ ] Podcast kartları
  - [ ] Kategori filtreleme
  - [ ] Arama

- [ ] **YÜKSEK ÖNCELİK** - Audio player
  - [ ] Play/Pause
  - [ ] Progress bar
  - [ ] Speed control (1x, 1.5x, 2x)
  - [ ] 15s forward/backward

- [ ] **ORTA ÖNCELİK** - Podcast detay sayfası
  - [ ] Transcript gösterme
  - [ ] İlgili haberler
  - [ ] Paylaşma

- [ ] **DÜŞÜK ÖNCELİK** - Ekstra özellikler
  - [ ] Download (offline dinleme)
  - [ ] Playlist oluşturma
  - [ ] Favorilere ekleme

---

## 📊 ÖNCELİK SIRASI (Önerilen Uygulama Sırası)

### FAZA 1: Mobil Temel Entegrasyon (1-2 Hafta)
1. ✅ AI service CORS ayarları
2. ✅ Mobil network layer (Retrofit)
3. ✅ Haber listesi entegrasyonu
4. ✅ Basic chat ekranı

### FAZA 2: AI Chat Geliştirme (1 Hafta)
5. ✅ Chat UI iyileştirme
6. ✅ Quick prompts
7. ✅ Conversation history

### FAZA 3: Podcast MVP (2-3 Hafta)
8. ✅ Podcast script generator (AI)
9. ✅ OpenAI TTS entegrasyonu
10. ✅ Basic podcast endpoint
11. ✅ Manuel podcast üretimi

### FAZA 4: Podcast UI (1 Hafta)
12. ✅ Podcast listesi (mobil/web)
13. ✅ Audio player
14. ✅ Podcast detay sayfası

### FAZA 5: Otomasyon & Optimizasyon (1-2 Hafta)
15. ✅ Otomatik günlük podcast
16. ✅ Queue sistemi
17. ✅ Performance optimizasyonu

---

## 💰 Maliyet Tahmini

### OpenAI API Maliyetleri
- **Chat (GPT-4o-mini):** ~$0.15/1M token
- **TTS:** ~$15/1M karakter
- **Podcast örnek:** 5 dakikalık podcast ≈ 750 kelime ≈ 4500 karakter ≈ $0.07

### Günlük 10 Podcast Üretimi
- Script üretimi: 10 x $0.02 = $0.20
- TTS: 10 x $0.07 = $0.70
- **Toplam: ~$0.90/gün = ~$27/ay**

---

## 🛠️ Teknoloji Stack Özeti

### Backend (AI Service)
- Node.js + Express
- OpenAI API (GPT-4, TTS)
- RSS Parser
- Cron Jobs
- File Storage (Local/S3)

### Mobil (Android)
- Kotlin
- Retrofit (HTTP client)
- Coroutines (Async)
- Room (Local DB - opsiyonel)
- ExoPlayer (Audio player)
- Jetpack Compose (Modern UI - opsiyonel)

### Web
- Next.js 14
- TypeScript
- Tailwind CSS

---

## 📝 Notlar

### Mobil Entegrasyon İpuçları
1. **Authentication:** İlerde JWT token sistemi ekleyin
2. **Caching:** Network isteklerini cache'leyin
3. **Offline Mode:** Local database ile offline destek
4. **Push Notifications:** Yeni podcast bildirimleri

### Podcast İpuçları
1. **Ses Kalitesi:** 128kbps MP3 yeterli
2. **Script Uzunluğu:** 3-7 dakika ideal
3. **Giriş/Çıkış:** Sabit intro/outro kullanın
4. **Kategori Çeşitliliği:** Her gün farklı kategorilerden seçin

---

## 🎯 Başarı Kriterleri

### Mobil Entegrasyon
- [ ] Haberler mobilde görüntülenebiliyor
- [ ] AI chat çalışıyor
- [ ] Kullanıcı deneyimi sorunsuz

### Podcast Sistemi
- [ ] Günlük 5-10 podcast otomatik üretiliyor
- [ ] Ses kalitesi yüksek
- [ ] Kullanıcılar dinleyip paylaşabiliyor

---

**Toplam Tahmini Süre:** 6-9 Hafta  
**Öncelikli Hedef:** Mobil entegrasyon + Podcast MVP (4-5 hafta)

**Başarılar! 🚀**
