# 🔥 Firebase Örnek Veri Ekleme Rehberi

Bu rehber, `firebase_sample_data.json` dosyasındaki örnek verileri Firebase Firestore'a nasıl ekleyeceğinizi adım adım açıklar.

## 📋 Genel Bakış

Toplamda **8 koleksiyon** için örnek veriler hazırlandı:

1. ✅ **news** (5 haber)
2. ✅ **time_tunnel_categories** (3 kategori)
3. ✅ **crossword_categories** (4 kategori)
4. ✅ **crossword_word_groups** (3 grup)
5. ✅ **quiz_categories** (3 kategori)
6. ✅ **quiz_questions** (4 soru)
7. ✅ **daily_tasks** (4 görev)
8. ✅ **badges** (5 rozet)
9. ✅ **ai_questions** (3 anket sorusu)

---

## 🚀 Adım Adım Ekleme

### 1️⃣ Haberler Koleksiyonu (news)

1. Firebase Console → Firestore Database → **"Start collection"**
2. Collection ID: **`news`**
3. İlk dokümanı ekle:

**Document ID:** Otomatik

**Fields (İlk haber için):**
```
title (string): "İstanbul'da Yeni Metro Hattı Hizmete Açıldı"
content (string): "İstanbul Büyükşehir Belediyesi, şehrin ulaşım ağını genişleten yeni metro hattını bugün düzenlenen törenle hizmete açtı. Mecidiyeköy-Mahmutbey arasında uzanan 15 kilometrelik hat, günde ortalama 300 bin yolcuya hizmet verecek. Belediye Başkanı, 'Bu proje İstanbul'un ulaşım sorununa önemli bir çözüm getiriyor' dedi."
author (string): "Mehmet Yılmaz"
category (string): "Ulaşım"
location (string): "İstanbul"
imageUrl (string): "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=800"
imageCaption (string): "Yeni metro hattının açılış töreni"
xpPoints (number): 10
likeCount (number): 0
viewCount (number): 0
shareCount (number): 0
isBreaking (boolean): true
isVerified (boolean): true
shortSummary (string): "İstanbul'da yeni metro hattı açıldı. Günde 300 bin yolcuya hizmet verecek."
mediumSummary (string): "İstanbul Büyükşehir Belediyesi, Mecidiyeköy-Mahmutbey arasında uzanan 15 kilometrelik yeni metro hattını hizmete açtı. Hat günde 300 bin yolcuya hizmet verecek."
podcastVersion (string): "Bugün İstanbul'da heyecan verici bir gelişme yaşandı. Şehrin ulaşım ağını genişleten yeni metro hattı törenle hizmete açıldı..."
mapGuessDetail (string): "Büyükşehir Belediyesi, şehrin ulaşım ağını genişleten yeni metro hattını bugün düzenlenen törenle hizmete açtı. Mecidiyeköy-Mahmutbey arasında uzanan 15 kilometrelik hat, günde ortalama 300 bin yolcuya hizmet verecek."
crosswordWords (array): ["metro", "ulaşım", "istanbul", "hat", "yolcu", "belediye", "tören", "proje"]
publishedAt (timestamp): Şu anki tarih/saat
createdAt (timestamp): Şu anki tarih/saat
updatedAt (timestamp): Şu anki tarih/saat
```

**quizQuestions (array):** Map ekle
```
[0] (map):
  - question (string): "Yeni metro hattı hangi şehirde açıldı?"
  - options (array): ["İstanbul", "Ankara", "İzmir", "Bursa"]
  - correctAnswerIndex (number): 0
  - explanation (string): "Haber İstanbul'daki yeni metro hattını anlatıyor."
  - xpPoints (number): 5

[1] (map):
  - question (string): "Metro hattı günde kaç yolcuya hizmet verecek?"
  - options (array): ["100 bin", "200 bin", "300 bin", "400 bin"]
  - correctAnswerIndex (number): 2
  - explanation (string): "Haberde günde ortalama 300 bin yolcuya hizmet vereceği belirtiliyor."
  - xpPoints (number): 5
```

4. **"Save"** butonuna tıkla
5. Diğer 4 haberi de aynı şekilde ekle (Ankara Teknoloji, İzmir Kültür, Bursa Tarım, Antalya Turizm)

---

### 2️⃣ Zaman Tüneli Kategorileri (time_tunnel_categories)

1. **"Start collection"** → Collection ID: **`time_tunnel_categories`**

**İlk kategori:**
```
title (string): "Ekrem İmamoğlu Tutuklanma Süreci"
description (string): "İstanbul Büyükşehir Belediye Başkanı Ekrem İmamoğlu'nun yargı süreciyle ilgili gelişmeler"
coverImageUrl (string): "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=800"
newsIds (array): [] (boş array)
isActive (boolean): true
createdAt (timestamp): Şu anki tarih/saat
```

2. Diğer 2 kategoriyi de ekle

---

### 3️⃣ Çengel Bulmaca Kategorileri (crossword_categories)

1. **"Start collection"** → Collection ID: **`crossword_categories`**

**İlk kategori:**
```
name (string): "Siyaset"
description (string): "Siyasetle ilgili kelimeler"
iconUrl (string): "https://img.icons8.com/color/96/politics.png"
xpPoints (number): 50
wordGroups (array): [] (boş array)
isActive (boolean): true
createdAt (timestamp): Şu anki tarih/saat
```

2. **ÖNEMLİ:** Bu kategoriyi ekledikten sonra, **Document ID'sini kopyala** (örn: `xyz123abc`)
3. Diğer kategorileri de ekle (Ekonomi, Spor, Teknoloji)

---

### 4️⃣ Çengel Bulmaca Kelime Grupları (crossword_word_groups)

1. **"Start collection"** → Collection ID: **`crossword_word_groups`**

**İlk grup (Siyaset):**
```
categoryId (string): "xyz123abc" (Yukarıda kopyaladığın Siyaset kategorisinin ID'si)
difficulty (string): "medium"
isActive (boolean): true
createdAt (timestamp): Şu anki tarih/saat
```

**words (array):** Map array ekle
```
[0] (map):
  - word (string): "MECLIS"
  - hint (string): "Yasama organı"
  - xpPoints (number): 5

[1] (map):
  - word (string): "SEÇİM"
  - hint (string): "Demokratik süreç"
  - xpPoints (number): 5

... (10 kelime için devam et)
```

2. Ekonomi ve Spor için de kelime grupları ekle

---

### 5️⃣ Quiz Kategorileri (quiz_categories)

1. **"Start collection"** → Collection ID: **`quiz_categories`**

**İlk kategori:**
```
name (string): "Genel Kültür"
description (string): "Genel kültür soruları"
iconUrl (string): "https://img.icons8.com/color/96/brain.png"
questionIds (array): [] (boş array)
isActive (boolean): true
createdAt (timestamp): Şu anki tarih/saat
```

2. **Document ID'sini kopyala**
3. Tarih ve Coğrafya kategorilerini ekle

---

### 6️⃣ Quiz Soruları (quiz_questions)

1. **"Start collection"** → Collection ID: **`quiz_questions`**

**İlk soru:**
```
categoryId (string): "genel_kultur_category_id" (Yukarıdaki ID)
question (string): "Türkiye'nin başkenti neresidir?"
options (array): ["İstanbul", "Ankara", "İzmir", "Bursa"]
correctAnswerIndex (number): 1
explanation (string): "Türkiye'nin başkenti 1923 yılından beri Ankara'dır."
xpPoints (number): 10
difficulty (string): "easy"
isActive (boolean): true
createdAt (timestamp): Şu anki tarih/saat
```

2. Diğer 3 soruyu da ekle

---

### 7️⃣ Günlük Görevler (daily_tasks)

1. **"Start collection"** → Collection ID: **`daily_tasks`**

**İlk görev:**
```
title (string): "2 Haber Oku"
description (string): "Bugün en az 2 haber oku ve bilgilen"
type (string): "read_news"
targetCount (number): 2
xpReward (number): 20
isActive (boolean): true
createdAt (timestamp): Şu anki tarih/saat
```

**expiresAt (timestamp):** Bugünün sonu (23:59:59)

2. Diğer 3 görevi de ekle

---

### 8️⃣ Rozetler (badges)

1. **"Start collection"** → Collection ID: **`badges`**

**İlk rozet:**
```
name (string): "İlk Adım"
description (string): "İlk haberini okudun"
iconUrl (string): "https://img.icons8.com/color/96/first-place.png"
rarity (string): "common"
xpReward (number): 10
isActive (boolean): true
createdAt (timestamp): Şu anki tarih/saat
```

**requirement (map):**
```
type (string): "news_read"
count (number): 1
```

2. Diğer 4 rozeti de ekle

---

### 9️⃣ AI Soruları (ai_questions)

1. **"Start collection"** → Collection ID: **`ai_questions`**

**İlk soru:**
```
question (string): "Yaşam şartlarından memnun musunuz?"
options (array): ["Evet, memnunum", "Kısmen memnunum", "Hayır, memnun değilim"]
category (string): "social"
totalResponses (number): 0
isActive (boolean): true
createdAt (timestamp): Şu anki tarih/saat
```

**responses (map):**
```
Evet, memnunum (number): 0
Kısmen memnunum (number): 0
Hayır, memnun değilim (number): 0
```

**expiresAt (timestamp):** 1 hafta sonra

2. Diğer 2 soruyu da ekle

---

## ✅ Test Kullanıcısı Oluşturma

1. Firebase Console → **Authentication** → **Users** → **Add user**

```
Email: test@aaeksen.com
Password: Test123456
```

2. **Firestore** → **users** koleksiyonu oluştur

**Document ID:** Authentication'dan aldığın User UID

**Fields:**
```
uid (string): [User UID]
email (string): "test@aaeksen.com"
username (string): "Test Kullanıcı"
photoUrl (string): ""
newsReadCount (number): 0
newsSharedCount (number): 0
totalXp (number): 0
badges (array): [] (boş array)
isPremium (boolean): false
dailyTasks (array): [] (boş array)
completedTasks (array): [] (boş array)
notificationsEnabled (boolean): true
darkModeEnabled (boolean): false
createdAt (timestamp): Şu anki tarih/saat
updatedAt (timestamp): Şu anki tarih/saat
```

---

## 🎯 Hızlı İpuçları

### Timestamp Ekleme
- Field type: **timestamp**
- Value: Klavyede **Ctrl+;** (veya **Cmd+;**) ile şu anki zaman

### Array Ekleme
1. Field type: **array**
2. Her eleman için **Add item**
3. String arrayler için: `["item1", "item2"]`

### Map Ekleme
1. Field type: **map**
2. Her alan için **Add field**
3. İç içe map için: Map içinde yeni map ekle

### Toplu Ekleme (Firebase CLI ile)
Daha hızlı veri eklemek için Firebase CLI kullanabilirsin:

```bash
# Firebase CLI'yi yükle
npm install -g firebase-tools

# Giriş yap
firebase login

# Firestore'a import et (örnek script gerekir)
firebase firestore:import data.json
```

---

## 📊 Veri Kontrolü

Tüm verileri ekledikten sonra:

1. ✅ **news** koleksiyonunda 5 doküman olmalı
2. ✅ **time_tunnel_categories** koleksiyonunda 3 doküman olmalı
3. ✅ **crossword_categories** koleksiyonunda 4 doküman olmalı
4. ✅ **crossword_word_groups** koleksiyonunda 3 doküman olmalı
5. ✅ **quiz_categories** koleksiyonunda 3 doküman olmalı
6. ✅ **quiz_questions** koleksiyonunda 4 doküman olmalı
7. ✅ **daily_tasks** koleksiyonunda 4 doküman olmalı
8. ✅ **badges** koleksiyonunda 5 doküman olmalı
9. ✅ **ai_questions** koleksiyonunda 3 doküman olmalı
10. ✅ **users** koleksiyonunda 1 doküman olmalı

---

## 🔒 Güvenlik Kurallarını Deploy Et

Verileri ekledikten sonra güvenlik kurallarını deploy et:

```bash
cd mobile/
firebase deploy --only firestore:rules
firebase deploy --only firestore:indexes
```

---

## 🎉 Tamamlandı!

Artık Firebase altyapın tamamen hazır ve test edilebilir durumda! Uygulamayı çalıştırıp verileri çekebilirsin.

