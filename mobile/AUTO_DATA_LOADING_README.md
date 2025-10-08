# 🚀 Otomatik Veri Yükleme Sistemi

## 📋 Genel Bakış

Uygulama ilk açıldığında otomatik olarak Firebase Firestore'a örnek veriler yüklenir. Bu sistem **tek seferlik** çalışır ve bir daha veri eklenmez.

## ✅ Nasıl Çalışır?

### 1️⃣ İlk Açılış
- Uygulama açıldığında `MainActivity.onCreate()` çalışır
- `SampleDataInitializer` Hilt ile inject edilir
- `initializeSampleData()` metodu çağrılır

### 2️⃣ Kontrol Mekanizması
- `SharedPreferences` kullanılarak veri yüklenip yüklenmediği kontrol edilir
- Key: `firebase_init` → `data_initialized`
- Eğer `true` ise → Atlanır
- Eğer `false` ise → Veriler yüklenir

### 3️⃣ Veri Yükleme Sırası
```
1. ✅ Haberler (5 adet)
2. ✅ Zaman Tüneli Kategorileri (3 adet)
3. ✅ Çengel Bulmaca Kategorileri + Kelime Grupları (3 kategori, 30 kelime)
4. ✅ Quiz Kategorileri + Sorular (3 kategori, 4 soru)
5. ✅ Günlük Görevler (4 görev)
6. ✅ Rozetler (5 rozet)
7. ✅ AI Soruları (3 anket)
```

### 4️⃣ Başarı Durumu
- Tüm veriler başarıyla yüklendikten sonra
- `SharedPreferences`'e `data_initialized = true` yazılır
- Bir sonraki açılışta veriler tekrar yüklenmez

## 📊 Yüklenen Veriler

### 📰 Haberler (5 adet)
| Şehir | Kategori | Breaking | Quiz | Kelimeler |
|---|---|---|---|---|
| İstanbul | Ulaşım | ✅ | 2 soru | 8 kelime |
| Ankara | Teknoloji | ❌ | 1 soru | 7 kelime |
| İzmir | Kültür | ❌ | 1 soru | 7 kelime |
| Bursa | Tarım | ❌ | 1 soru | 7 kelime |
| Antalya | Turizm | ✅ | 1 soru | 6 kelime |

### 🕒 Zaman Tüneli (3 kategori)
- Ekrem İmamoğlu Tutuklanma Süreci
- Özgür Özel'in CHP Genel Başkanlığı
- 2024 Yerel Seçimleri

### 🎯 Çengel Bulmaca (3 kategori, 30 kelime)
- **Siyaset:** MECLIS, SEÇİM, PARTI, vb. (10 kelime)
- **Ekonomi:** ENFLASYON, BORSA, DOLAR, vb. (10 kelime)
- **Spor:** FUTBOL, BASKETBOL, YÜZME, vb. (10 kelime)

### ❓ Quiz (3 kategori, 4 soru)
- **Genel Kültür:** Başkent, il sayısı
- **Tarih:** Cumhuriyet tarihi
- **Coğrafya:** En büyük göl

### 🎯 Günlük Görevler (4 görev)
- 2 Haber Oku (+20 XP)
- 1 Haber Paylaş (+15 XP)
- 5 Quiz Çöz (+25 XP)
- 1 Bulmaca Tamamla (+30 XP)

### 🏆 Rozetler (5 rozet)
| Rozet | Gereksinim | Rarity | XP |
|---|---|---|---|
| İlk Adım | 1 haber oku | Common | 10 |
| Haber Kurdu | 10 haber oku | Rare | 50 |
| Quiz Ustası | 50 quiz çöz | Epic | 100 |
| Kelime Avcısı | 20 bulmaca | Epic | 100 |
| Coğrafya Dehası | 25 harita | Legendary | 200 |

### 🤖 AI Soruları (3 anket)
- Yaşam şartlarından memnuniyet
- Ekonomik gidişat değerlendirmesi
- Dijital vs geleneksel habercilik

## 🔧 Teknik Detaylar

### Dosya Konumları
```
/app/src/main/java/com/bysoftware/aaeksen/
├── data/firebase/
│   ├── SampleDataInitializer.kt  ← Veri yükleme sınıfı
│   └── model/                     ← Firebase modelleri
├── di/
│   └── FirebaseModule.kt          ← Hilt DI yapılandırması
└── MainActivity.kt                 ← Başlatma noktası
```

### SharedPreferences Kullanımı
```kotlin
val prefs = context.getSharedPreferences("firebase_init", Context.MODE_PRIVATE)
val isInitialized = prefs.getBoolean("data_initialized", false)

if (!isInitialized) {
    // Verileri yükle
    loadData()
    
    // Başarılı olduysa işaretle
    prefs.edit().putBoolean("data_initialized", true).apply()
}
```

### Asenkron İşlem
```kotlin
lifecycleScope.launch {
    sampleDataInitializer.initializeSampleData(this@MainActivity)
        .onSuccess { Log.d(TAG, "✅ Veriler hazır!") }
        .onFailure { Log.e(TAG, "❌ Hata: ${it.message}") }
}
```

## 🧪 Test ve Debug

### Logları Görüntüleme
```bash
adb logcat | grep SampleDataInitializer
```

**Beklenen çıktı:**
```
D/SampleDataInitializer: Örnek veriler yükleniyor...
D/SampleDataInitializer: Haberler yükleniyor...
D/SampleDataInitializer: ✅ 5 haber eklendi
D/SampleDataInitializer: Zaman tüneli kategorileri yükleniyor...
D/SampleDataInitializer: ✅ 3 zaman tüneli kategorisi eklendi
...
D/SampleDataInitializer: ✅ Tüm örnek veriler başarıyla yüklendi!
D/MainActivity: ✅ Firebase verileri hazır!
```

### Verileri Sıfırlama (Test İçin)
Eğer verileri tekrar yüklemek istersen:

**1. Manuel Yöntem (SharedPreferences sil):**
```bash
adb shell pm clear com.bysoftware.aaeksen
```

**2. Kod ile Sıfırlama:**
```kotlin
sampleDataInitializer.resetInitializationFlag(this)
```

Sonra uygulamayı yeniden başlat.

## 🚨 Önemli Notlar

### ⚠️ İlk Yüklemede
- İlk açılışta internet bağlantısı olmalı
- Firebase projesinin aktif olması gerekli
- `google-services.json` dosyası yerinde olmalı

### ⚠️ Firebase Kuralları
- Güvenlik kuralları deploy edilmiş olmalı
- Firestore write izni olmalı
- Authentication aktif değilse anonymous erişime izin ver

### ⚠️ Performans
- Tüm verilerin yüklenmesi ~5-10 saniye sürer
- Arka planda çalışır, UI'yi bloklamaz
- Hata durumunda uygulama çalışmaya devam eder

## 🎯 Avantajlar

✅ **Manuel veri girişi gerekmez**
✅ **Test için ideal**
✅ **Tek seferlik çalışır**
✅ **Hata güvenli**
✅ **Log ile takip edilebilir**
✅ **Kolayca sıfırlanabilir**

## 📱 Kullanım Senaryoları

### Senaryo 1: İlk Kurulum
1. Firebase projesi oluştur
2. `google-services.json` ekle
3. Uygulamayı aç
4. ✅ Veriler otomatik yüklenir

### Senaryo 2: Geliştirme
1. Verileri test et
2. Gerekirse `pm clear` ile sıfırla
3. Tekrar test et

### Senaryo 3: Production
1. İlk yüklemeden sonra sistem pasif kalır
2. Manuel veri yönetimi yapabilirsin
3. Örnek veriler üzerinden çalışabilirsin

## 🔒 Güvenlik

- ✅ Veriler sadece ilk açılışta yüklenir
- ✅ SharedPreferences ile kontrol
- ✅ Firebase Security Rules ile korunur
- ✅ Production'da devre dışı bırakılabilir

## 📝 Production'da Devre Dışı Bırakma

Eğer production'da bu özelliği kapatmak istersen:

```kotlin
// MainActivity.kt
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // BuildConfig kullanarak kontrol et
    if (BuildConfig.DEBUG) {
        // Sadece debug modda çalıştır
        lifecycleScope.launch {
            sampleDataInitializer.initializeSampleData(this@MainActivity)
        }
    }
    
    setContent {
        AAEksenTheme {
            MainScreen()
        }
    }
}
```

---

## ✅ Sonuç

Artık uygulamayı açtığında tüm örnek veriler otomatik olarak Firebase'e yüklenecek! 🎉

