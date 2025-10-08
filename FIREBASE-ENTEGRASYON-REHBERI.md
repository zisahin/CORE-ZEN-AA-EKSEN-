# 🔥 Firebase Entegrasyon Rehberi - AA Eksen

## 🎯 Neden Firebase?

### ✅ Avantajları
- **Gerçek Zamanlı Database:** Haberler anında güncellenir
- **Authentication:** Kullanıcı yönetimi hazır
- **Cloud Functions:** AI servisi entegrasyonu kolay
- **Storage:** Podcast MP3'leri için
- **Hosting:** Web sitesi deploy
- **Free Tier:** Başlangıç için ücretsiz
- **Offline Support:** Mobilde offline çalışır
- **Multi-platform:** Web, Android, iOS tek yerden

### ⚠️ Dikkat Edilecekler
- Read/Write limitleri var (free tier)
- Vendor lock-in riski
- Karmaşık sorgular zor olabilir

---

## 📋 FIREBASE ENTEGRASYON PLANI

### FAZA 1️⃣: Firebase Projesi Kurulumu (30 dakika)

#### Adım 1: Firebase Konsolu
1. **Firebase Console'a git:** https://console.firebase.google.com/
2. **"Add project"** tıkla
3. **Proje adı:** `aa-eksen` veya `core-zen-news`
4. **Google Analytics:** Etkinleştir (önerilen)
5. **Bölge seç:** Europe (eur3) - En yakın
6. **Projeyi oluştur** ✅

#### Adım 2: Firebase Servisleri Aktifleştir

**2.1 Authentication**
```
Firebase Console → Authentication → Get Started
→ Email/Password: Enable
→ Google: Enable (opsiyonel)
→ Anonymous: Enable (misafir kullanıcılar için)
```

**2.2 Firestore Database**
```
Firebase Console → Firestore Database → Create Database
→ Start in Production Mode (güvenlik kurallarıyla)
→ Location: europe-west3 (Frankfurt)
```

**2.3 Storage**
```
Firebase Console → Storage → Get Started
→ Start in Production Mode
→ Location: europe-west3
```

**2.4 Cloud Functions**
```
Firebase Console → Functions → Get Started
→ Upgrade to Blaze Plan (pay-as-you-go) - AI API çağrıları için gerekli
```

---

### FAZA 2️⃣: Firebase CLI Kurulumu (15 dakika)

#### Adım 1: Firebase CLI Yükle
```bash
npm install -g firebase-tools
```

#### Adım 2: Login
```bash
firebase login
```

#### Adım 3: Proje Initialize
```bash
cd "C:\AA HACKHATON\CORE-ZEN-AA-EKSEN-"

firebase init

# Seçenekler:
# ✅ Firestore
# ✅ Functions
# ✅ Hosting
# ✅ Storage

# Existing project seç: aa-eksen
# Functions: JavaScript VEYA TypeScript (TypeScript önerilen)
# Hosting: web klasörünü seç
```

---

### FAZA 3️⃣: Firestore Database Yapısı

#### 📊 Collections (Koleksiyonlar)

```javascript
// 1. HABERLER KOLEKSIYONU
news/
  {newsId}/
    - id: string
    - title: string
    - description: string
    - content: string
    - category: string (guncel, ekonomi, spor, vb.)
    - source: string (AA)
    - link: string
    - imageUrl: string
    - publishedAt: timestamp
    - createdAt: timestamp
    - updatedAt: timestamp
    - views: number
    - likes: number
    - featured: boolean
    - tags: array
    
// 2. KULLANICILAR
users/
  {userId}/
    - email: string
    - displayName: string
    - photoURL: string
    - createdAt: timestamp
    - xp: number
    - level: number
    - readNews: array (newsId'ler)
    - likedNews: array
    - savedNews: array

// 3. PODCAST'LER
podcasts/
  {podcastId}/
    - id: string
    - title: string
    - description: string
    - topic: string
    - category: string
    - duration: number (saniye)
    - audioUrl: string (Storage'dan)
    - transcript: string
    - createdAt: timestamp
    - publishedAt: timestamp
    - plays: number
    - likes: number

// 4. CHAT GEÇMİŞİ
chats/
  {userId}/
    conversations/
      {conversationId}/
        - messages: array
          - text: string
          - isUser: boolean
          - timestamp: timestamp

// 5. ANKET/SURVEY
surveys/
  {surveyId}/
    - question: string
    - options: array
    - category: string
    - responses: map {optionId: count}
    - createdAt: timestamp
```

---

### FAZA 4️⃣: Web Entegrasyonu (Next.js)

#### Adım 1: Firebase SDK Yükle
```bash
cd web
npm install firebase
```

#### Adım 2: Firebase Config Dosyası
```typescript
// web/src/lib/firebase.ts
import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';
import { getStorage } from 'firebase/storage';

const firebaseConfig = {
  apiKey: process.env.NEXT_PUBLIC_FIREBASE_API_KEY,
  authDomain: process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN,
  projectId: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID,
  storageBucket: process.env.NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: process.env.NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID,
  appId: process.env.NEXT_PUBLIC_FIREBASE_APP_ID
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Services
export const auth = getAuth(app);
export const db = getFirestore(app);
export const storage = getStorage(app);

export default app;
```

#### Adım 3: Environment Variables
```bash
# web/.env.local
NEXT_PUBLIC_FIREBASE_API_KEY=your_api_key
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=aa-eksen.firebaseapp.com
NEXT_PUBLIC_FIREBASE_PROJECT_ID=aa-eksen
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=aa-eksen.appspot.com
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
NEXT_PUBLIC_FIREBASE_APP_ID=your_app_id
```

#### Adım 4: Haberler Servisi
```typescript
// web/src/services/newsService.ts
import { db } from '@/lib/firebase';
import { 
  collection, 
  query, 
  where, 
  orderBy, 
  limit, 
  getDocs,
  doc,
  getDoc,
  addDoc,
  updateDoc,
  increment
} from 'firebase/firestore';

export class NewsService {
  // Haberleri çek
  async getNews(category?: string, limitCount = 20) {
    const newsRef = collection(db, 'news');
    
    let q = query(
      newsRef,
      orderBy('publishedAt', 'desc'),
      limit(limitCount)
    );
    
    if (category && category !== 'all') {
      q = query(newsRef, where('category', '==', category), orderBy('publishedAt', 'desc'), limit(limitCount));
    }
    
    const snapshot = await getDocs(q);
    return snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
  }
  
  // Tek haber detayı
  async getNewsById(newsId: string) {
    const docRef = doc(db, 'news', newsId);
    const docSnap = await getDoc(docRef);
    
    if (docSnap.exists()) {
      // View sayısını artır
      await updateDoc(docRef, {
        views: increment(1)
      });
      return { id: docSnap.id, ...docSnap.data() };
    }
    return null;
  }
  
  // Haber ekle (admin/cron job için)
  async addNews(newsData: any) {
    const newsRef = collection(db, 'news');
    const docRef = await addDoc(newsRef, {
      ...newsData,
      createdAt: new Date(),
      updatedAt: new Date(),
      views: 0,
      likes: 0
    });
    return docRef.id;
  }
}

export const newsService = new NewsService();
```

---

### FAZA 5️⃣: Mobil Entegrasyonu (Kotlin/Android)

#### Adım 1: Firebase SDK Ekle
```gradle
// mobile/build.gradle (Project level)
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.4.0'
    }
}

// mobile/build.gradle (App level)
plugins {
    id 'com.google.gms.google-services'
}

dependencies {
    // Firebase BoM
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    
    // Firebase services
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.firebase:firebase-storage-ktx'
    implementation 'com.google.firebase:firebase-analytics-ktx'
}
```

#### Adım 2: google-services.json
```
1. Firebase Console → Project Settings
2. "Add app" → Android
3. Package name: com.corezenaeksen
4. google-services.json indir
5. mobile/app/ klasörüne kopyala
```

#### Adım 3: Firebase Initialize (Kotlin)
```kotlin
// MainActivity.kt veya Application class
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

// Firestore kullanımı
val db = Firebase.firestore

// Haberleri çek
db.collection("news")
    .orderBy("publishedAt", Query.Direction.DESCENDING)
    .limit(20)
    .get()
    .addOnSuccessListener { documents ->
        for (document in documents) {
            val news = document.toObject<News>()
            // UI'da göster
        }
    }
```

#### Adım 4: Data Class
```kotlin
// News.kt
data class News(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val content: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val publishedAt: com.google.firebase.Timestamp? = null,
    val views: Int = 0,
    val likes: Int = 0
)
```

---

### FAZA 6️⃣: Cloud Functions (AI Entegrasyonu)

#### Adım 1: Functions Klasörü
```bash
cd functions
npm install
npm install openai
npm install node-fetch
```

#### Adım 2: RSS → Firestore Cloud Function
```javascript
// functions/index.js
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const fetch = require('node-fetch');

admin.initializeApp();
const db = admin.firestore();

// Her 30 dakikada bir haberleri çek
exports.fetchNewsFromRSS = functions.pubsub
  .schedule('*/30 * * * *') // Her 30 dakika
  .timeZone('Europe/Istanbul')
  .onRun(async (context) => {
    console.log('📰 Haberler çekiliyor...');
    
    const categories = ['guncel', 'ekonomi', 'spor', 'teknoloji', 'dunya'];
    
    for (const category of categories) {
      try {
        const rssUrl = `https://www.aa.com.tr/tr/rss/default?cat=${category}`;
        const response = await fetch(rssUrl);
        const xmlText = await response.text();
        
        // XML parse et (basit regex veya xml2js kullan)
        const items = parseRSS(xmlText);
        
        // Firestore'a ekle
        for (const item of items) {
          const newsRef = db.collection('news').doc();
          await newsRef.set({
            title: item.title,
            description: item.description,
            content: item.description,
            category: category,
            source: 'AA',
            link: item.link,
            imageUrl: item.image || null,
            publishedAt: admin.firestore.Timestamp.fromDate(new Date(item.pubDate)),
            createdAt: admin.firestore.FieldValue.serverTimestamp(),
            views: 0,
            likes: 0,
            featured: false
          });
        }
        
        console.log(`✅ ${category}: ${items.length} haber eklendi`);
      } catch (error) {
        console.error(`❌ ${category} hatası:`, error);
      }
    }
    
    return null;
  });

// AI Chat Function
exports.chatWithAI = functions.https.onCall(async (data, context) => {
  const { message, conversationHistory } = data;
  
  // OpenAI API çağrısı
  const OpenAI = require('openai');
  const openai = new OpenAI({
    apiKey: functions.config().openai.key
  });
  
  // Son haberleri Firestore'dan çek
  const newsSnapshot = await db.collection('news')
    .orderBy('publishedAt', 'desc')
    .limit(5)
    .get();
  
  const recentNews = newsSnapshot.docs.map(doc => ({
    title: doc.data().title,
    description: doc.data().description
  }));
  
  const newsContext = recentNews.map((n, i) => 
    `${i+1}. ${n.title}: ${n.description}`
  ).join('\n');
  
  const response = await openai.chat.completions.create({
    model: 'gpt-4o-mini',
    messages: [
      {
        role: 'system',
        content: `Sen AA Habercilik AI asistanısın. Güncel haberler:\n${newsContext}`
      },
      ...conversationHistory,
      { role: 'user', content: message }
    ]
  });
  
  return {
    response: response.choices[0].message.content
  };
});

// Podcast üretimi
exports.generatePodcast = functions.https.onCall(async (data, context) => {
  // ... Podcast üretim kodu
});

function parseRSS(xml) {
  // Basit RSS parser
  // Gerçek projeye xml2js kullanın
  return [];
}
```

#### Adım 3: Environment Config
```bash
firebase functions:config:set openai.key="YOUR_OPENAI_API_KEY"
```

#### Adım 4: Deploy
```bash
firebase deploy --only functions
```

---

### FAZA 7️⃣: Firestore Security Rules

```javascript
// firestore.rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Haberler - Herkes okuyabilir, sadece admin yazabilir
    match /news/{newsId} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.token.admin == true;
    }
    
    // Kullanıcılar - Sadece kendi profilini okuyabilir/yazabilir
    match /users/{userId} {
      allow read: if request.auth != null && request.auth.uid == userId;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Podcast'ler - Herkes okuyabilir
    match /podcasts/{podcastId} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.token.admin == true;
    }
    
    // Chat - Sadece kendi chat'lerini okuyabilir
    match /chats/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

## 📊 ÖNCELİK SIRASI

### 🚀 HEMEN BAŞLAYIN (Bugün)
1. ✅ Firebase Console'da proje oluştur (10 dk)
2. ✅ Authentication + Firestore + Storage aktifleştir (15 dk)
3. ✅ Firebase CLI yükle ve init (15 dk)
4. ✅ Web'e Firebase SDK ekle (30 dk)

### 📱 İLK HAFTA
5. ✅ Firestore'a manuel test haberi ekle
6. ✅ Web'de Firestore'dan haberleri çek ve göster
7. ✅ Mobil Firebase SDK ekle
8. ✅ Mobil'de Firestore'dan haberleri çek

### 🤖 İKİNCİ HAFTA
9. ✅ Cloud Function: RSS → Firestore (otomatik haber çekme)
10. ✅ Cloud Function: AI Chat
11. ✅ Authentication ekle (opsiyonel)

### 🎙️ ÜÇÜNCÜ HAFTA
12. ✅ Podcast üretimi Cloud Function
13. ✅ Storage'a MP3 upload
14. ✅ Podcast player UI

---

## 💰 Firebase Fiyatlandırma

### Spark Plan (ÜCRETSIZ)
- ✅ Firestore: 50K read/day, 20K write/day
- ✅ Storage: 1 GB
- ✅ Cloud Functions: 125K invocations/month
- ✅ Hosting: 10 GB/month
- ⚠️ Cloud Functions OpenAI çağrıları için yetersiz

### Blaze Plan (Pay-as-you-go)
- ✅ OpenAI API çağrıları yapabilirsiniz
- ✅ İlk 125K çağrı ücretsiz
- ✅ Sonrası: $0.40/million invocations
- **Tahmini:** $5-10/ay (düşük trafik için)

---

## 🎓 ÖĞRENME KAYNAKLARI

### Firebase Resmi Döküman
- https://firebase.google.com/docs
- https://firebase.google.com/codelabs

### YouTube Tutorials
- Fireship: "Firebase in 100 Seconds"
- Net Ninja: "Firebase Tutorial"

### Örnek Projeler
- https://github.com/firebase/quickstart-js
- https://github.com/firebase/quickstart-android

---

## ✅ İLK ADIMLAR CHECKLIST

- [ ] Firebase Console'da proje oluştur
- [ ] Auth, Firestore, Storage aktifleştir
- [ ] Firebase CLI yükle (`npm install -g firebase-tools`)
- [ ] Firebase login (`firebase login`)
- [ ] Proje klasöründe `firebase init`
- [ ] Web'e Firebase SDK ekle (`npm install firebase`)
- [ ] Firebase config dosyası oluştur
- [ ] İlk test haberi Firestore'a manuel ekle
- [ ] Web'de Firestore'dan oku ve göster

---

**Hazır mısınız? İlk adımdan başlayalım! 🚀**

Hangi adımdan başlamak istersiniz?
