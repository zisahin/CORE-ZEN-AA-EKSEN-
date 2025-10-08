# 🔥 Firebase Kurulum Rehberi

Bu doküman, AA Eksen Mobile uygulaması için Firebase entegrasyonunun nasıl yapılacağını adım adım açıklar.

## 📋 İçindekiler

1. [Firebase Projesi Oluşturma](#1-firebase-projesi-oluşturma)
2. [Android Uygulamasını Ekleme](#2-android-uygulamasını-ekleme)
3. [Firestore Database Kurulumu](#3-firestore-database-kurulumu)
4. [Authentication Kurulumu](#4-authentication-kurulumu)
5. [Güvenlik Kurallarını Deploy Etme](#5-güvenlik-kurallarını-deploy-etme)
6. [Firestore Indexes Deploy Etme](#6-firestore-indexes-deploy-etme)
7. [Test Verileri Ekleme](#7-test-verileri-ekleme)

---

## 1. Firebase Projesi Oluşturma

1. [Firebase Console](https://console.firebase.google.com/)'a git
2. **"Add project"** butonuna tıkla
3. Proje adı: `AA-Eksen-Mobile` (veya istediğin bir isim)
4. Google Analytics'i etkinleştir (opsiyonel ama önerilen)
5. Proje oluşturulmasını bekle

---

## 2. Android Uygulamasını Ekleme

1. Firebase Console'da sol menüden **Project Overview** → **Add app** → **Android** seç
2. **Android package name:** `com.bysoftware.aaeksen`
3. **App nickname (opsiyonel):** `AA Eksen Mobile`
4. **Debug signing certificate SHA-1** (gerekli):
   
   Terminal'de şu komutu çalıştır:
   ```bash
   cd android
   ./gradlew signingReport
   ```
   
   Çıktıdan **debug** altındaki **SHA-1** değerini kopyala ve yapıştır.

5. **"Register app"** butonuna tıkla

6. **google-services.json** dosyasını indir
   
   İndirdiğin dosyayı şu konuma taşı:
   ```
   mobile/app/google-services.json
   ```

7. Firebase SDK kurulumunu atla (zaten build.gradle'da ekli)

---

## 3. Firestore Database Kurulumu

1. Firebase Console'da **Build** → **Firestore Database** sekmesine git
2. **"Create database"** butonuna tıkla
3. **Location** seçimi:
   - Türkiye için en yakın: `europe-west3` (Frankfurt) veya `europe-central2` (Warsaw)
4. **Security rules** için **"Start in production mode"** seç (güvenlik kurallarını sonra deploy edeceğiz)
5. **"Enable"** butonuna tıkla
6. Database oluşturulmasını bekle

---

## 4. Authentication Kurulumu

1. Firebase Console'da **Build** → **Authentication** sekmesine git
2. **"Get started"** butonuna tıkla
3. **Sign-in method** sekmesine git
4. Şu yöntemleri etkinleştir:
   - ✅ **Email/Password** (Enable)
   - ✅ **Google** (Enable - Projeyi seç ve kaydet)

---

## 5. Güvenlik Kurallarını Deploy Etme

### Firebase CLI Kurulumu

1. Node.js yüklü değilse [buradan](https://nodejs.org/) indir ve yükle

2. Firebase CLI'yi global olarak yükle:
   ```bash
   npm install -g firebase-tools
   ```

3. Firebase'e giriş yap:
   ```bash
   firebase login
   ```

### Projeyi Firebase'e Bağlama

1. Terminal'de `mobile/` klasörüne git:
   ```bash
   cd /path/to/mobile
   ```

2. Firebase projesini başlat:
   ```bash
   firebase init
   ```

3. Şu seçenekleri seç:
   - `Firestore: Configure security rules and indexes files`
   - Mevcut projeyi seç: `AA-Eksen-Mobile`
   - Firestore rules file: `firestore.rules` (Enter)
   - Firestore indexes file: `firestore.indexes.json` (Enter)

### Güvenlik Kurallarını Deploy Et

```bash
firebase deploy --only firestore:rules
```

---

## 6. Firestore Indexes Deploy Etme

Indexes dosyası zaten hazır (`firestore.indexes.json`). Deploy etmek için:

```bash
firebase deploy --only firestore:indexes
```

Index'ler oluşturulana kadar birkaç dakika bekle.

---

## 7. Test Verileri Ekleme

### Koleksiyonlar ve Örnek Veriler

Firebase Console'da **Firestore Database** → **Data** sekmesine git ve şu koleksiyonları oluştur:

#### 📰 `news` Koleksiyonu

**Örnek Haber Belgesi:**

```json
{
  "title": "İstanbul'da Yeni Metro Hattı Açıldı",
  "content": "İstanbul Büyükşehir Belediyesi, şehrin ulaşım ağını genişleten yeni metro hattını hizmete açtı. Hat, günde ortalama 100 bin yolcuya hizmet verecek.",
  "author": "Mehmet Yılmaz",
  "publishedAt": "2025-01-06T10:00:00Z",
  "imageUrl": "https://example.com/images/metro.jpg",
  "imageCaption": "Yeni metro hattı açılış töreni",
  "category": "Ulaşım",
  "location": "İstanbul",
  "xpPoints": 10,
  "likeCount": 0,
  "shortSummary": "İstanbul'da yeni metro hattı açıldı.",
  "mediumSummary": "İstanbul Büyükşehir Belediyesi, şehrin ulaşım ağını genişleten yeni metro hattını hizmete açtı.",
  "podcastVersion": "Bugün İstanbul'da heyecan verici bir gelişme yaşandı...",
  "mapGuessDetail": "Büyükşehir Belediyesi, şehrin ulaşım ağını genişleten yeni metro hattını hizmete açtı.",
  "crosswordWords": ["metro", "ulaşım", "İstanbul", "hat", "yolcu"],
  "quizQuestions": [
    {
      "question": "Yeni metro hattı hangi şehirde açıldı?",
      "options": ["İstanbul", "Ankara", "İzmir", "Bursa"],
      "correctAnswerIndex": 0,
      "explanation": "Haber İstanbul'daki yeni metro hattını anlatıyor.",
      "xpPoints": 5
    }
  ],
  "viewCount": 0,
  "shareCount": 0,
  "isBreaking": false,
  "isVerified": true,
  "createdAt": "Timestamp",
  "updatedAt": "Timestamp"
}
```

#### 👤 `users` Koleksiyonu

Test kullanıcısı oluşturmak için Authentication'dan bir kullanıcı oluştur, sonra Firestore'da:

```json
{
  "uid": "kullanıcı-uid-buraya",
  "email": "test@example.com",
  "username": "TestKullanıcı",
  "photoUrl": "",
  "newsReadCount": 0,
  "newsSharedCount": 0,
  "totalXp": 0,
  "badges": [],
  "isPremium": false,
  "notificationsEnabled": true,
  "darkModeEnabled": false,
  "createdAt": "Timestamp"
}
```

#### 🎮 `crossword_categories` Koleksiyonu

```json
{
  "name": "Siyaset",
  "description": "Siyaset ile ilgili kelimeler",
  "iconUrl": "https://example.com/politics-icon.png",
  "xpPoints": 50,
  "wordGroups": [],
  "isActive": true,
  "createdAt": "Timestamp"
}
```

#### ❓ `quiz_categories` Koleksiyonu

```json
{
  "name": "Ekonomi",
  "description": "Ekonomi haberleri quiz'i",
  "iconUrl": "https://example.com/economy-icon.png",
  "questionIds": [],
  "isActive": true,
  "createdAt": "Timestamp"
}
```

#### 🎯 `daily_tasks` Koleksiyonu

```json
{
  "title": "2 Haber Oku",
  "description": "Bugün en az 2 haber oku",
  "type": "read_news",
  "targetCount": 2,
  "xpReward": 20,
  "expiresAt": "Timestamp (bugünün sonu)",
  "isActive": true,
  "createdAt": "Timestamp"
}
```

#### 🤖 `ai_questions` Koleksiyonu

```json
{
  "question": "Yaşam şartlarından memnun musunuz?",
  "options": ["Evet, memnunum", "Kısmen memnunum", "Hayır, memnun değilim"],
  "category": "social",
  "responses": {
    "Evet, memnunum": 0,
    "Kısmen memnunum": 0,
    "Hayır, memnun değilim": 0
  },
  "totalResponses": 0,
  "isActive": true,
  "expiresAt": "Timestamp (1 hafta sonra)",
  "createdAt": "Timestamp"
}
```

---

## ✅ Kurulum Tamamlandı!

Artık Firebase altyapın hazır. Uygulaman şunları yapabilir:

- ✅ Kullanıcı kaydı ve girişi
- ✅ Haber okuma ve istatistik tutma
- ✅ Oyunlar (Quiz, Çengel Bulmaca, Harita Tahmin)
- ✅ Günlük görevler ve XP sistemi
- ✅ AI chat geçmişi
- ✅ AA AI Soruyor anketleri
- ✅ Profil ve rozet sistemi

---

## 🚨 Önemli Notlar

1. **google-services.json** dosyasını `.gitignore`'a ekle (güvenlik için)
2. Production'da güvenlik kurallarını gözden geçir
3. Firestore fiyatlandırmasını takip et (okuma/yazma limitleri)
4. Index'ler otomatik oluşturulduğunda Firebase Console'dan bildirim gelecek

---

## 📞 Yardım

Sorun yaşarsan:
- [Firebase Docs](https://firebase.google.com/docs)
- [Firestore Security Rules](https://firebase.google.com/docs/firestore/security/get-started)
- [Firebase CLI Reference](https://firebase.google.com/docs/cli)

