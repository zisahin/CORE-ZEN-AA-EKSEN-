# AI Service - AA Habercilik Entegrasyon Sistemi

Bu servis, AA Habercilik web sitesi ve mobil uygulaması için ChatGPT tabanlı AI özelliklerini sağlar.

## 🚀 Özellikler

### 1. 💬 AI Chat Sistemi
- Kullanıcılarla doğal dilde sohbet
- Haber özetleri ve analizler
- Kategoriye özel haber sorguları
- Çoklu dil desteği (Türkçe odaklı)

### 2. 📄 Haber Özetleme
- Tek haber özetleme
- Kategori bazlı özetler (günlük/haftalık)
- Toplu haber özetleme
- Anahtar kelime çıkarma

### 3. ⏰ Timeline (Zaman Tüneli) Sistemi
- Haberlerin kronolojik analizi
- Timeline önerileri
- İlgili haber gruplama
- Sürekli güncellenen konular

### 4. 🎙️ Podcast Generator
- Haberleri podcast formatında yeniden yazma
- Sohbet tarzı içerik üretimi
- Kategori bazlı podcast'ler
- Text-to-Speech uyumlu format

### 5. 🤔 AI Soruyor Sistemi
- Günlük interaktif sorular
- Kullanıcı etkileşim analizi
- Kategori bazlı sorular
- İstatistik ve analiz

### 6. 🧩 Quiz Generator
- Haberlerden otomatik quiz oluşturma
- Farklı zorluk seviyeleri
- Kategori bazlı quizler
- Detaylı sonuç analizi

## 🛠️ Kurulum

### Gereksinimler
- Node.js 18+ 
- npm veya yarn
- OpenAI API anahtarı

### Adımlar

1. **Bağımlılıkları yükleyin:**
```bash
npm install
```

2. **Çevre değişkenlerini ayarlayın:**
```bash
cp .env.example .env
```
`.env` dosyasındaki `OPENAI_API_KEY` değerini kendi anahtarınızla değiştirin.

3. **Servisi başlatın:**
```bash
# Development
npm run dev

# Production
npm start
```

## 📡 API Endpoints

### Chat Sistemi
- `POST /api/chat` - AI ile sohbet
- `GET /api/chat/suggestions` - Hızlı öneriler

### Haber Özetleme
- `POST /api/summary/news` - Tek haber özeti
- `POST /api/summary/category` - Kategori özeti
- `GET /api/summary/daily` - Günlük özetler
- `GET /api/summary/weekly` - Haftalık özetler

### Timeline Sistemi
- `POST /api/timeline/analyze` - Timeline analizi
- `GET /api/timeline` - Timeline listesi
- `GET /api/timeline/:id` - Timeline detayı
- `POST /api/timeline/create` - Yeni timeline

### Podcast Generator
- `POST /api/podcast/generate` - Podcast oluştur
- `GET /api/podcast` - Podcast listesi
- `GET /api/podcast/:id` - Podcast detayı

### AI Soruyor
- `GET /api/questions/daily` - Günlük sorular
- `POST /api/questions/answer` - Soru yanıtla
- `GET /api/questions/stats/:userId` - Kullanıcı istatistikleri

### Quiz Sistemi
- `POST /api/quiz/generate` - Quiz oluştur
- `GET /api/quiz` - Quiz listesi
- `GET /api/quiz/:id` - Quiz detayı
- `POST /api/quiz/:id/submit` - Quiz yanıtla

## 🔧 Yapılandırma

### Ortam Değişkenleri
```env
OPENAI_API_KEY=your_openai_api_key
PORT=3001
WEB_URL=http://localhost:3000
NODE_ENV=development
```

### Cron Jobs
Sistem otomatik olarak şu görevleri çalıştırır:
- **06:00** - Günlük sorular ve quizler
- **Pazartesi 08:00** - Haftalık özetler
- **Sürekli** - Timeline analizi

## 🎯 Kullanım Örnekleri

### Chat API Kullanımı
```javascript
const response = await fetch('/api/chat', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    message: 'Fenerbahçe son bir haftada ne oldu?',
    conversationHistory: []
  })
});
```

### Haber Özetleme
```javascript
const summary = await fetch('/api/summary/news', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    title: 'Haber Başlığı',
    content: 'Haber içeriği...',
    category: 'spor'
  })
});
```

## 🔄 Web Sitesi Entegrasyonu

### Frontend Bileşenleri
Mevcut web sitesinde şu komponentler hazır:
- `AIChat.tsx` - Ana chat bileşeni
- `AIBubble.tsx` - Chat açma butonu
- `Timeline.tsx` - Timeline görüntüleme

### API Entegrasyonu
```javascript
// Mevcut web sitesindeki chat route'ları güncelleme
// ../web/src/app/api/ai/chat/route.ts
// Bu servisin /api/chat endpoint'ini kullanacak şekilde güncelleyin
```

## 📱 Mobil Uygulama Entegrasyonu

### Kotlin Integration
```kotlin
// API client configuration
const val AI_SERVICE_BASE_URL = "http://your-server:3001/api"

// Chat request example
data class ChatRequest(
    val message: String,
    val conversationHistory: List<Message> = emptyList()
)
```

## 🧪 Test

```bash
# Tüm testleri çalıştır
npm test

# Specific endpoint test
curl -X POST http://localhost:3001/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Merhaba!"}'
```

## 📊 Monitoring ve Logs

Sistem logları:
- `🤖` AI Service başlatma
- `💬` Chat istekleri
- `📄` Özetleme işlemleri
- `⏰` Timeline analizleri
- `🎙️` Podcast oluşturma
- `❌` Hata logları

## 🛡️ Güvenlik

- API rate limiting
- Input validation
- OpenAI API key güvenliği
- CORS yapılandırması
- Error handling

## 🔮 Gelecek Özellikler

- [ ] Sesli asistan entegrasyonu
- [ ] Görsel içerik analizi
- [ ] Çoklu dil desteği genişletme
- [ ] Offline AI modelleri
- [ ] Kullanıcı tercihleri öğrenme
- [ ] A/B testing sistemi

## 📞 Destek

Sorularınız için:
- GitHub Issues
- Development Team
- [Dokumentasyon](./docs)

---

**Core Zen AA Eksen Team** - 2024 Hackathon Projesi


