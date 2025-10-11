# 🎮 Oyunlaştırılmış Profil & Giriş Sistemi - Kullanım Kılavuzu

## 📌 Özellikler

### ✨ Giriş/Kayıt Sistemi
- 🎨 **Modern Gradient Tasarım** - Koyu mavi-mor tema
- 🔐 **Email/Şifre ile Giriş** - Firebase Authentication
- 🌐 **Google OAuth** - Tek tıkla giriş
- 🔑 **Şifre Sıfırlama** - E-posta ile şifre kurtarma
- 🎯 **Form Validasyonu** - Gerçek zamanlı doğrulama

### 🏆 Oyunlaştırma Sistemi
- 📊 **XP & Seviye Sistemi** - Her aktivitede XP kazan
- 🏅 **Rozet Sistemi** - 4 nadir seviyesi (Common, Rare, Epic, Legendary)
- 📈 **İstatistikler** - Detaylı kullanıcı aktivite takibi
- 🎯 **Günlük Görevler** - Görev tamamla, XP kazan
- 👑 **Premium Üyelik** - Özel rozetler ve özellikler

### 🎨 UI/UX
- 📱 **Tam Responsive** - Mobil, tablet, desktop
- 🌊 **Wave Efektleri** - Dekoratif SVG dalga animasyonları
- ✨ **Glow & Shadow** - Premium görünüm
- 🎭 **Smooth Animations** - Akıcı geçişler
- 🎯 **Hover Efektleri** - İnteraktif elementler

---

## 🚀 Hızlı Başlangıç

### 1. Kurulum
```bash
cd web
npm install
```

### 2. Firebase Yapılandırması
`.env.local` dosyasını oluşturun ve Firebase bilgilerinizi ekleyin:
```env
NEXT_PUBLIC_FIREBASE_API_KEY=your-api-key
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=your-auth-domain
NEXT_PUBLIC_FIREBASE_PROJECT_ID=aa-eksen-71097
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=your-storage-bucket
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your-sender-id
NEXT_PUBLIC_FIREBASE_APP_ID=your-app-id
```

### 3. Geliştirme Sunucusu
```bash
npm run dev
```

Tarayıcı: `http://localhost:3000`

---

## 📂 Dosya Yapısı

```
web/src/
├── app/
│   ├── auth/
│   │   ├── login/page.tsx              # Giriş sayfası
│   │   ├── register/page.tsx           # Kayıt sayfası
│   │   └── forgot-password/page.tsx    # Şifre sıfırlama
│   └── profile/
│       └── page.tsx                    # Profil sayfası
│
├── components/
│   ├── auth/
│   │   ├── LoginForm.tsx               # Giriş formu
│   │   └── RegisterForm.tsx            # Kayıt formu
│   ├── profile/
│   │   ├── ProfileHeader.tsx           # Profil header (avatar, level, XP)
│   │   ├── ProfileStats.tsx            # İstatistik kartları
│   │   └── ProfileBadges.tsx           # Rozet grid'i
│   └── common/
│       └── UserMenu.tsx                # Kullanıcı dropdown menüsü
│
├── context/
│   └── AuthContext.tsx                 # Auth state yönetimi
│
└── services/
    └── authService.ts                  # Firebase auth işlemleri
```

---

## 🎯 Kullanım Örnekleri

### UserMenu Komponenti Kullanımı

Herhangi bir sayfada kullanıcı menüsünü eklemek için:

```tsx
import UserMenu from '@/components/common/UserMenu';

export default function Navbar() {
  return (
    <nav className="flex items-center justify-between p-4">
      <div>Logo</div>
      <UserMenu />
    </nav>
  );
}
```

### Auth Durumunu Kontrol Etme

```tsx
'use client';

import { useAuth } from '@/context/AuthContext';

export default function MyComponent() {
  const { user, userProfile, loading } = useAuth();

  if (loading) return <div>Yükleniyor...</div>;

  if (!user) {
    return <div>Giriş yapmanız gerekiyor</div>;
  }

  return (
    <div>
      <h1>Hoş geldin, {userProfile?.username}!</h1>
      <p>Toplam XP: {userProfile?.totalXp}</p>
    </div>
  );
}
```

### XP Ekleme

```tsx
import { addXP, XP_REWARDS } from '@/services/authService';

// Haber okuma XP'si ekle
await addXP(userId, XP_REWARDS.NEWS_READ); // +5 XP

// Quiz doğru cevap XP'si
await addXP(userId, XP_REWARDS.QUIZ_CORRECT); // +10 XP
```

### Rozet Verme

```tsx
import { awardBadge } from '@/services/authService';

const badge = {
  badgeId: 'first_news',
  name: 'İlk Haber',
  description: 'İlk haberini okudun!',
  rarity: 'common' as const,
  earnedAt: new Date()
};

await awardBadge(userId, badge);
```

---

## 🎨 Tasarım Rehberi

### Renk Paleti

```css
/* Ana Gradient */
bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900

/* Mor Vurgular */
from-purple-600 to-purple-700

/* Rozet Renkleri */
Common:    from-gray-500 to-gray-600
Rare:      from-blue-500 to-blue-600
Epic:      from-purple-500 to-purple-600
Legendary: from-yellow-500 to-orange-600
```

### Animasyonlar

```tsx
// Hover Scale
className="hover:scale-105 transition-transform"

// Glow Efekti
<div className="absolute -inset-1 bg-gradient-to-r from-purple-600 to-blue-600 rounded-2xl blur-2xl opacity-20 -z-10" />

// Progress Bar
<div className="h-3 bg-white/10 rounded-full">
  <div className="h-full bg-gradient-to-r from-purple-600 to-purple-400 transition-all" />
</div>
```

---

## 🔥 Firebase Koleksiyonları

### `users` Koleksiyonu
```typescript
{
  uid: string;
  email: string;
  username: string;
  photoUrl?: string;
  
  // Oyunlaştırma
  newsReadCount: number;
  newsSharedCount: number;
  totalXp: number;
  badges: UserBadge[];
  
  // Premium
  isPremium: boolean;
  premiumExpiryDate?: Date;
  
  // Görevler
  dailyTasks: string[];
  completedTasks: string[];
  
  // Ayarlar
  notificationsEnabled: boolean;
  darkModeEnabled: boolean;
  
  createdAt: Date;
  updatedAt: Date;
}
```

### `userGameStats` Koleksiyonu
```typescript
{
  crosswordSolved: number;
  quizCorrect: number;
  quizWrong: number;
  mapGuessCorrect: number;
  mapGuessWrong: number;
  totalXp: number;
}
```

---

## 🏅 XP Ödül Sistemi

| Aktivite | XP Değeri |
|----------|-----------|
| Haber Okuma | +5 XP |
| Haber Paylaşma | +10 XP |
| Quiz Doğru Cevap | +10 XP |
| Çengel Bulmaca Tamamlama | +50 XP |
| Harita Tahmini Doğru | +20 XP |
| Günlük Görev Tamamlama | +20 XP |

**Seviye Hesaplama:** `Level = Math.floor(totalXP / 100) + 1`

---

## 🎯 Rozet Nadir Seviyeleri

### Common (Gri)
- En basit rozetler
- Kolayca kazanılır
- Örnek: "İlk Haber", "Günlük Okuyucu"

### Rare (Mavi)
- Orta zorluk
- Biraz çaba gerektirir
- Örnek: "10 Haber Okudun", "5 Paylaşım"

### Epic (Mor)
- Zor rozetler
- Özel başarılar
- Örnek: "50 Haber Okudun", "10 Quiz Tamamladın"

### Legendary (Altın)
- En nadir rozetler
- Özel kullanıcılara verilir
- Örnek: "100 Günlük Seri", "Premium Üye"

---

## 🔐 Güvenlik

### Firebase Security Rules (Örnek)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    match /userGameStats/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

## 📱 Responsive Breakpoints

```tsx
// Mobil (varsayılan)
<div className="grid grid-cols-2">

// Tablet
<div className="md:grid-cols-3">

// Desktop
<div className="lg:grid-cols-4">
```

**İstatistik Kartları:**
- Mobil: 2 sütun
- Tablet: 4 sütun
- Desktop: 4 sütun

**Rozetler:**
- Mobil: 2 sütun
- Tablet: 3 sütun
- Desktop: 4 sütun

---

## 🐛 Troubleshooting

### Sorun: Firebase bağlantı hatası
**Çözüm:** `.env.local` dosyasının doğru olduğundan ve dev sunucusunu yeniden başlattığınızdan emin olun.

### Sorun: Hydration hatası
**Çözüm:** `'use client'` direktifinin komponent dosyasının en üstünde olduğundan emin olun.

### Sorun: Rozetler görünmüyor
**Çözüm:** `userProfile.badges` dizisini kontrol edin. Boş ise "Henüz rozet yok" mesajı görünmelidir.

### Sorun: XP güncellenmiyor
**Çözüm:** `refreshProfile()` fonksiyonunu çağırarak profili yenileyin:
```tsx
const { refreshProfile } = useAuth();
await addXP(userId, 10);
await refreshProfile();
```

---

## 🚀 Production Deployment

1. **Build Oluştur:**
```bash
npm run build
```

2. **Environment Variables Ayarla:**
   - Vercel/Netlify dashboard'dan `.env` değişkenlerini ekleyin

3. **Firebase Security Rules Güncelle:**
   - Production kurallarını Firebase Console'dan deploy edin

4. **Domain Ayarları:**
   - Firebase Console > Authentication > Authorized domains
   - Domain'inizi ekleyin

---

## 📚 Ek Kaynaklar

- [Firebase Auth Docs](https://firebase.google.com/docs/auth)
- [Next.js App Router](https://nextjs.org/docs/app)
- [Tailwind CSS](https://tailwindcss.com/docs)
- [React Hooks](https://react.dev/reference/react)

---

## 🎉 Özellik İstekleri

Gelecek güncellemeler için planlanan:

- [ ] Email doğrulama
- [ ] İki faktörlü kimlik doğrulama (2FA)
- [ ] Facebook/Twitter/Apple login
- [ ] Avatar yükleme
- [ ] Profil tema seçimi
- [ ] Rozet koleksiyonu sayfası
- [ ] Lider tablosu (leaderboard)
- [ ] Arkadaş sistemi

---

## 📞 Destek

Sorularınız için:
- GitHub Issues
- Discord: [Sunucu linki]
- E-posta: support@aaeksen.com

---

**Branch:** `feature/auth-profile-screen`  
**Version:** 1.0.0  
**Son Güncelleme:** Ekim 2025

---

*Mutlu kodlamalar! 🚀*

