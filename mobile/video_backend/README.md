# 🎬 AA Eksen Video Generation Backend

Firebase entegrasyonu ile otomatik video üretimi yapan Python backend sistemi.

## 🚀 Kurulum

### 1. Gereksinimler
- Python 3.8+
- FFmpeg
- Firebase projesi
- (Opsiyonel) OpenAI API key

### 2. Hızlı Kurulum
```bash
# Repository'yi klonla
cd video_backend

# Setup script'ini çalıştır
./setup.sh
```

### 3. Manuel Kurulum
```bash
# Virtual environment oluştur
python3 -m venv venv
source venv/bin/activate

# Dependencies yükle
pip install -r requirements.txt

# FFmpeg yükle (macOS)
brew install ffmpeg

# FFmpeg yükle (Ubuntu)
sudo apt install ffmpeg
```

### 4. Firebase Konfigürasyonu

#### Firebase Console'dan:
1. Firebase Console'a git
2. Proje ayarları → Service accounts
3. "Generate new private key" → JSON dosyasını indir
4. Dosyayı `firebase-credentials.json` olarak kaydet

#### Environment Variables:
```bash
# .env dosyasını düzenle
FIREBASE_CREDENTIALS=firebase-credentials.json
OPENAI_API_KEY=your-openai-key-here
```

## 🔥 Çalıştırma

```bash
# Virtual environment'ı aktifleştir
source venv/bin/activate

# Backend'i başlat
python main.py
```

## 📊 Sistem Akışı

### 1. Firebase Listener
- `video_generation_requests` koleksiyonunu dinler
- Yeni `PENDING` istekleri yakalar
- Her istek için ayrı thread başlatır

### 2. Video Pipeline
```
PENDING → PROCESSING → TRANSCRIBING → GENERATING → UPLOADING → COMPLETED
```

### 3. Status Updates
- Her adımda Firebase'i günceller
- Mobil uygulama real-time takip eder
- Hata durumunda `FAILED` status'u

## 🎬 Video Oluşturma Süreci

### 1. Haber Verisi Alma
```python
news_data = get_news_from_firebase(news_id)
```

### 2. Transkripsiyon
- Haber metninden transkripsiyon oluştur
- Whisper AI entegrasyonu (gelişmiş sürüm için)

### 3. Video Oluşturma
- MoviePy ile video kompozisyonu
- Başlık, içerik, kategori, logo
- 1080p, 30fps, MP4 format

### 4. Firebase Storage Upload
- Video dosyasını Firebase Storage'a yükle
- Public URL oluştur
- Metadata'yı Firestore'a kaydet

## 🔧 Konfigürasyon

### Video Ayarları
```python
# main.py içinde
width, height = 1920, 1080
fps = 30
max_duration = 30  # saniye
```

### Firebase Collections
- `video_generation_requests` - İstekler
- `videos` - Tamamlanan videolar
- `news` - Haber verileri

## 📱 Mobil Entegrasyon

### İstek Gönderme
```kotlin
// Android tarafında
videoRepository.requestVideoGeneration(
    newsId = "news_123",
    userId = "user_456",
    config = VideoGenerationConfig(...)
)
```

### Real-time Tracking
```kotlin
// Firebase listener ile durum takibi
startStatusPolling(requestId)
```

## 🛠 Geliştirme

### Log Takibi
```bash
# Real-time log takibi
tail -f video_backend.log
```

### Test İsteği
```bash
# Firebase Console'dan manuel istek ekle
# Collection: video_generation_requests
{
  "newsId": "news_001",
  "userId": "test_user",
  "status": "PENDING",
  "config": {...}
}
```

## 🚨 Sorun Giderme

### FFmpeg Hatası
```bash
# macOS
brew install ffmpeg

# Ubuntu
sudo apt install ffmpeg

# Windows
# https://ffmpeg.org/download.html
```

### Firebase Bağlantı Hatası
- `firebase-credentials.json` dosyasının doğru yerde olduğundan emin ol
- Firebase proje ID'sini kontrol et
- Storage bucket'ının aktif olduğunu kontrol et

### Memory Hatası
- Video süresini kısalt
- Çözünürlüğü düşür
- Temp dosyaları temizle

## 📈 Performans

### Optimizasyonlar
- Video süresi: Maksimum 30 saniye
- Çözünürlük: 1080p (değiştirilebilir)
- Codec: H.264 (uyumluluk için)
- Temp dosya temizliği

### Ölçeklendirme
- Multiple worker threads
- Queue sistemi
- Load balancing
- Caching

## 🔐 Güvenlik

- Firebase Security Rules
- API key protection
- Input validation
- File size limits

## 📞 Destek

Sorunlar için:
1. Log dosyalarını kontrol et
2. Firebase Console'u kontrol et
3. Environment variables'ları doğrula
4. FFmpeg kurulumunu test et

---

**🎬 Artık mobil uygulamadan video oluşturma istekleri gönderebilir ve gerçek zamanlı olarak takip edebilirsiniz!**
