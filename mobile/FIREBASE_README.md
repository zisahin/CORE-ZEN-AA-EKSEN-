# AA Eksen Firebase Altyapısı

Bu proje, AA Eksen haber uygulaması için kapsamlı Firebase altyapısını içerir.

## 🚀 Özellikler

### 📰 Ana Haberler Modülü
- **Reels&Shorts**: Kısa video haberler
- **Geri Bildirim Sistemi**: Kullanıcıların AA'ya haber geri bildirimi
- **Haber Kategorileri**: Dinamik kategori yönetimi
- **Acil Durum Hizmetleri**: İhtiyaç hattı, organ bağışı, kayıp hayvan ilanları
- **Zaman Tüneli**: Önemli olayların kronolojik listesi

### 🎮 Oyunlaştırma Modülü
- **Çok Oyunculu Kelime Bulma**: Gerçek zamanlı kelime oyunu
- **Haber Quiz**: Haberlerle ilgili soru-cevap oyunu
- **Çengel Bulmaca**: Günlük bulmaca oyunu
- **GeoGuessr**: Haberin konumunu tahmin etme oyunu
- **Günlük Görevler**: Kullanıcı etkileşimini artırma
- **Rozet ve Ödül Sistemi**: Başarı rozetleri ve puan sistemi

### 🤖 AI Modülü
- **Podcast Haber Özeti**: Sesli haber özetleri
- **Genel Haber Özeti**: Metin tabanlı özetler
- **Özel Haber Anlatma**: AI ile kişiselleştirilmiş haberler
- **Haber Doğrulama**: AA kaynaklarıyla haber doğrulama
- **Haber Okuma**: Sesli haber okuma
- **Günlük/Haftalık Özet**: AI ile oluşturulan özetler
- **Duygu Analizi**: Haberlerin duygu durumu analizi
- **AI Anketler**: Dinamik anket soruları
- **Kişiselleştirme**: İlgi alanlarına göre AI deneyimi

### 💎 Premium Modülü
- **Abonelik Yönetimi**: Aylık/yıllık premium paketler
- **Özel Özellikler**: Sınırsız AI, reklamsız deneyim
- **Gelir Modeli**: Premium özelliklerle monetizasyon

## 🏗️ Firebase Servisleri

### Firestore Veritabanı
- **Kullanıcı Yönetimi**: Profil, skor, rozet sistemi
- **Haber Sistemi**: Kategoriler, etiketler, duygu analizi
- **Oyun Sistemi**: Quiz, kelime oyunu, GeoGuessr
- **AI Sistemi**: Chat, özet, doğrulama, anketler
- **Premium Sistemi**: Abonelik yönetimi

### Cloud Functions
- **AI İşlemleri**: Özet oluşturma, doğrulama, duygu analizi
- **Oyun Mantığı**: Skor hesaplama, rozet verme
- **Bildirim Sistemi**: Push notifications
- **Günlük Görevler**: Otomatik görev oluşturma

### Storage
- **Medya Dosyaları**: Haber görselleri, videolar
- **Kullanıcı İçeriği**: Avatar, AI oluşturulan içerik
- **Oyun Varlıkları**: Oyun görselleri, ses dosyaları

### Authentication
- **Email/Şifre**: Geleneksel giriş
- **Google Sign-In**: Sosyal medya girişi
- **Premium Kontrolü**: Abonelik durumu kontrolü

## 📁 Proje Yapısı

```
firebase/
├── firebase-functions/          # Cloud Functions
│   ├── index.js                # Ana functions dosyası
│   └── package.json            # Dependencies
├── firebase.json               # Firebase konfigürasyonu
├── firestore.rules            # Firestore güvenlik kuralları
├── firestore.indexes.json     # Firestore indexleri
└── storage.rules              # Storage güvenlik kuralları

app/src/main/java/com/bysoftware/aaeksen/
├── core/constants/
│   └── FirebaseConfig.kt       # Firebase konfigürasyon sabitleri
├── data/firebase/
│   ├── model/
│   │   └── FirebaseModels.kt   # Veri modelleri
│   └── repository/
│       ├── FirebaseNewsRepository.kt
│       ├── FirebaseGameRepository.kt
│       ├── FirebaseAIRepository.kt
│       └── FirebaseAuthRepository.kt
└── di/
    └── FirebaseModule.kt       # Dependency Injection
```

## 🔧 Kurulum

### 1. Firebase Projesi Oluşturma
```bash
# Firebase CLI kurulumu
npm install -g firebase-tools

# Firebase'e giriş yap
firebase login

# Proje oluştur
firebase init
```

### 2. Android Uygulamasına Entegrasyon
```bash
# google-services.json dosyasını app/ klasörüne kopyala
# Firebase Console'dan indirilen dosyayı yerleştir
```

### 3. Cloud Functions Deploy
```bash
cd firebase-functions
npm install
cd ..
firebase deploy --only functions
```

### 4. Firestore Rules Deploy
```bash
firebase deploy --only firestore:rules
```

### 5. Storage Rules Deploy
```bash
firebase deploy --only storage
```

## 🔐 Güvenlik

### Firestore Rules
- Kullanıcılar sadece kendi verilerine erişebilir
- Adminler tüm verileri yönetebilir
- Haberler herkese açık okunabilir
- Oyunlar herkese açık okunabilir

### Storage Rules
- Medya dosyaları herkese açık okunabilir
- Kullanıcı avatarları sadece sahibi tarafından yazılabilir
- Adminler tüm dosyaları yönetebilir

## 📊 Analytics ve Monitoring

### Firebase Analytics Events
- `news_viewed`: Haber görüntüleme
- `news_shared`: Haber paylaşma
- `game_started`: Oyun başlatma
- `ai_chat_started`: AI chat başlatma
- `premium_purchased`: Premium satın alma

### Crashlytics
- Uygulama çökmelerini takip
- Performans metrikleri
- Kullanıcı deneyimi analizi

## 🚀 Deployment

### Production Deploy
```bash
# Tüm servisleri deploy et
firebase deploy

# Sadece functions deploy et
firebase deploy --only functions

# Sadece rules deploy et
firebase deploy --only firestore:rules,storage
```

### Environment Variables
```bash
# Functions için environment variables
firebase functions:config:set openai.api_key="your-api-key"
firebase functions:config:set aa.api_key="your-aa-api-key"
```

## 📈 Performans Optimizasyonu

### Firestore Indexes
- Kategori bazlı haber sorguları
- Tarih bazlı sıralama
- Kullanıcı bazlı filtreleme
- Oyun skorları için indexler

### Caching Strategy
- Haberler için 5 dakika cache
- Kullanıcı profilleri için 1 saat cache
- Oyun verileri için 10 dakika cache

## 🔄 Backup ve Recovery

### Firestore Backup
```bash
# Otomatik backup (günlük)
gcloud firestore export gs://your-backup-bucket/backup-$(date +%Y%m%d)
```

### Disaster Recovery
- Multi-region deployment
- Automated failover
- Data replication

## 📞 Destek

Firebase altyapısı ile ilgili sorularınız için:
- Firebase Console: https://console.firebase.google.com
- Firebase Documentation: https://firebase.google.com/docs
- Cloud Functions Documentation: https://firebase.google.com/docs/functions

## 🔄 Güncellemeler

### v1.0.0 (İlk Sürüm)
- Temel Firebase altyapısı
- Haber sistemi
- Oyun sistemi
- AI sistemi
- Premium sistemi

### Gelecek Sürümler
- Real-time multiplayer oyunlar
- Gelişmiş AI özellikleri
- Sosyal medya entegrasyonu
- Offline destek
