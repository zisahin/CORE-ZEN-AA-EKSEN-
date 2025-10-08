# AI Service Entegrasyon Rehberi

## 🎯 Genel Bakış

Bu proje `ai-service-2` ile web sitesini başarıyla entegre eder. AIBubble bileşeni gerçek OpenAI API'si kullanarak çalışır.

## 📋 Yapılan Değişiklikler

### 1. AI Service Client Oluşturuldu
- **Dosya:** `web/src/services/aiService.ts`
- **Özellikler:**
  - Chat mesajları gönderme
  - Hızlı öneriler alma
  - Haber özetleme
  - Günlük/haftalık özetler
  - Servis sağlık kontrolü

### 2. AIChat Bileşeni Güncellendi
- **Dosya:** `web/src/components/AIChat.tsx`
- **Değişiklikler:**
  - Mock yanıtlar kaldırıldı
  - Gerçek AI servisi entegre edildi
  - Servis durumu göstergesi eklendi
  - Hata yönetimi iyileştirildi
  - Dinamik hızlı öneriler desteği

### 3. AIBubble Zaten Entegre
- **Dosya:** `web/src/app/page.tsx`
- AIBubble bileşeni ana sayfada aktif ✅

## 🚀 Kurulum Adımları

### 1. AI Service Kurulumu

```bash
cd "ai-service -2"

# Bağımlılıkları yükle
npm install

# .env dosyası oluştur
cp .env.example .env
```

**.env dosyasını düzenle:**
```env
OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
PORT=3001
NODE_ENV=development
WEB_URL=http://localhost:3000
```

**OpenAI API Key almak için:**
1. https://platform.openai.com/api-keys adresine git
2. Yeni API key oluştur
3. Key'i `.env` dosyasına yapıştır

**AI Service'i başlat:**
```bash
npm run dev
```

Servis şu adreste çalışacak: `http://localhost:3001`

### 2. Web Sitesi Kurulumu

```bash
cd web

# Bağımlılıkları yükle (gerekirse)
npm install

# .env.local dosyası oluştur
cp .env.example .env.local
```

**.env.local dosyasını düzenle:**
```env
NEXT_PUBLIC_AI_SERVICE_URL=http://localhost:3001
```

**Web sitesini başlat:**
```bash
npm run dev
```

Web sitesi şu adreste çalışacak: `http://localhost:3000`

## 🧪 Test Etme

### 1. Servis Sağlık Kontrolü
```bash
curl http://localhost:3001/health
```

Beklenen yanıt:
```json
{
  "status": "OK",
  "service": "AI Service",
  "version": "1.0.0",
  "timestamp": "...",
  "description": "AA Habercilik AI Entegrasyon Servisi"
}
```

### 2. Chat Test
```bash
curl -X POST http://localhost:3001/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Merhaba!"}'
```

### 3. Web Sitesinde Test
1. `http://localhost:3000` adresine git
2. Sağ alt köşedeki AI bubble'a tıkla
3. Bir mesaj gönder
4. AI'ın gerçek yanıtını gör

## 📡 API Endpoint'leri

### Chat
- **POST** `/api/chat`
  - Mesaj gönder
  - Body: `{ message: string, conversationHistory?: array }`

- **GET** `/api/chat/suggestions`
  - Hızlı öneriler al

### Summary (Özet)
- **POST** `/api/summary/news`
  - Tek haber özetle
  - Body: `{ content: string, title?: string, category?: string }`

- **GET** `/api/summary/daily?category=genel`
  - Günlük özet

- **GET** `/api/summary/weekly?category=genel`
  - Haftalık özet

### Diğer Servisler
- **POST** `/api/timeline` - Timeline analizi
- **POST** `/api/podcast` - Podcast içeriği
- **POST** `/api/questions` - Günlük sorular
- **POST** `/api/quiz` - Quiz oluşturma
- **GET** `/api/rss` - RSS feed

## 🔧 Özellikler

### AI Chat Özellikleri
- ✅ Gerçek zamanlı sohbet
- ✅ Konuşma geçmişi
- ✅ Hızlı öneriler
- ✅ Servis durumu göstergesi
- ✅ Sesli okuma (TTS)
- ✅ Hata yönetimi
- ✅ Otomatik scroll
- ✅ ESC tuşu ile kapatma
- ✅ **GÜNCEL AA HABERLERİ İLE YANIT!** 🆕

### AI Service Özellikleri
- ✅ GPT-4 entegrasyonu
- ✅ **AA RSS ile gerçek haber entegrasyonu** 🆕
- ✅ **Akıllı kategori algılama** 🆕
- ✅ **Haber kontekstli AI yanıtları** 🆕
- ✅ Haber özetleme
- ✅ Chat desteği
- ✅ Timeline analizi
- ✅ Podcast içeriği
- ✅ Quiz oluşturma
- ✅ Günlük görevler (cron)
- ✅ CORS desteği
- ✅ Güvenlik (helmet)

## 🐛 Sorun Giderme

### AI Servisi Yanıt Vermiyor
1. AI service'in çalıştığından emin ol: `http://localhost:3001/health`
2. .env dosyasında `OPENAI_API_KEY` kontrolü yap
3. OpenAI hesabında kredi olduğundan emin ol
4. Console loglarını kontrol et

### Web Sitesi AI'a Bağlanamıyor
1. `.env.local` dosyasında `NEXT_PUBLIC_AI_SERVICE_URL` kontrolü yap
2. CORS ayarlarını kontrol et
3. Her iki serviste port충돌이 olup olmadığını kontrol et
4. Browser console'da network hatalarını incele

### OpenAI API Hataları
- **401 Unauthorized:** API key yanlış veya geçersiz
- **429 Too Many Requests:** Rate limit aşıldı, biraz bekle
- **500 Internal Error:** OpenAI servisi sorunlu olabilir

## 📊 Servis Durumu

Web sitesinde AI bubble'daki yeşil/kırmızı nokta servis durumunu gösterir:
- 🟢 **Yeşil:** AI servisi aktif ve çalışıyor
- 🔴 **Kırmızı:** AI servisi çevrimdışı veya hata var

## 💡 İpuçları

### Development Ortamı
- Her iki servisi ayrı terminal'de çalıştır
- Logları takip et
- Browser console'ı açık tut

### Production Ortamı
```env
# ai-service-2/.env
NODE_ENV=production
WEB_URL=https://your-domain.com

# web/.env.local
NEXT_PUBLIC_AI_SERVICE_URL=https://your-ai-service-url.com
```

### Performans
- AI yanıtları genellikle 2-5 saniye sürer
- Rate limiting için retry mekanizması ekleyebilirsiniz
- Uzun konuşmalarda conversation history'yi sınırlayabilirsiniz

## 📚 Daha Fazla Bilgi

- [OpenAI API Docs](https://platform.openai.com/docs)
- [Next.js Environment Variables](https://nextjs.org/docs/basic-features/environment-variables)
- [Express.js CORS](https://expressjs.com/en/resources/middleware/cors.html)

## ✅ Başarıyla Tamamlandı!

AI Service entegrasyonu tamamlandı. Artık kullanıcılar web sitesinde gerçek AI asistanı ile sohbet edebilir! 🎉

---

**Geliştirici:** Core Zen AA Team  
**Tarih:** 2025  
**Versiyon:** 1.0.0
