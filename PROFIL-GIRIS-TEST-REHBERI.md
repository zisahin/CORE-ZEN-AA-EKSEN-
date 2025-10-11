# 🎮 Oyunlaştırılmış Profil ve Giriş Ekranı - Test Rehberi

## 📋 Genel Bakış

Bu rehber, yeni oluşturulan **oyunlaştırılmış profil ve giriş ekranı** sistemi için test yönergelerini içerir.

**Branch:** `feature/auth-profile-screen`  
**Tasarım Stili:** Koyu mavi-mor gradient, modern ve oyunlaştırılmış  
**Teknolojiler:** Next.js 14, Firebase Auth, TypeScript, Tailwind CSS

---

## 🚀 Kurulum ve Çalıştırma

### 1. Bağımlılıkları Yükleyin

```bash
cd web
npm install
```

**Not:** Eğer paket kurulumunda hata alırsanız, PowerShell execution policy sorunudur. Aşağıdaki komutu çalıştırın:

```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

Sonra tekrar:
```bash
npm install
```

### 2. Firebase Yapılandırmasını Kontrol Edin

`.env.local` dosyanızın web klasöründe olduğundan ve aşağıdaki değişkenleri içerdiğinden emin olun:

```env
NEXT_PUBLIC_FIREBASE_API_KEY=your-api-key
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=your-auth-domain
NEXT_PUBLIC_FIREBASE_PROJECT_ID=aa-eksen-71097
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=your-storage-bucket
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your-sender-id
NEXT_PUBLIC_FIREBASE_APP_ID=your-app-id
```

### 3. Geliştirme Sunucusunu Başlatın

```bash
npm run dev
```

Tarayıcınızda: `http://localhost:3000`

---

## 🧪 Test Senaryoları

### ✅ 1. Kayıt (Register) Ekranı Testleri

**URL:** `http://localhost:3000/auth/register`

#### Test 1.1: Normal Kayıt
- [ ] Formu açın
- [ ] Kullanıcı adı, email, şifre (min 6 karakter) ve şifre tekrar girin
- [ ] "KAYIT OL" butonuna tıklayın
- [ ] Başarılı kayıt sonrası ana sayfaya yönlendirilmeli
- [ ] Firebase'de kullanıcı oluşturulmalı

**Beklenen Sonuç:** ✓ Kullanıcı kaydedilir ve ana sayfaya yönlenir

#### Test 1.2: Şifre Eşleşme Kontrolü
- [ ] Farklı şifreler girin
- [ ] "KAYIT OL" butonuna tıklayın
- [ ] Hata mesajı: "Şifreler eşleşmiyor"

**Beklenen Sonuç:** ✓ Hata mesajı gösterilir

#### Test 1.3: Kısa Şifre Kontrolü
- [ ] 5 karakterden az şifre girin
- [ ] "KAYIT OL" butonuna tıklayın
- [ ] Hata mesajı: "Şifre en az 6 karakter olmalıdır"

**Beklenen Sonuç:** ✓ Hata mesajı gösterilir

#### Test 1.4: Mevcut Email Kontrolü
- [ ] Zaten kayıtlı bir email ile kayıt deneyin
- [ ] Hata mesajı gösterilmeli

**Beklenen Sonuç:** ✓ "Bu e-posta zaten kullanılıyor" mesajı

#### Test 1.5: UI/UX Kontrolleri
- [ ] Gradient arka plan doğru görünüyor mu? (Koyu mavi-mor)
- [ ] Wave efekti altta görünüyor mu?
- [ ] Input'lar focus olduğunda mor ring efekti var mı?
- [ ] Loading durumunda buton disabled oluyor mu?
- [ ] "Login" linki çalışıyor mu?

**Beklenen Sonuç:** ✓ Tüm UI elementleri tasarıma uygun

---

### ✅ 2. Giriş (Login) Ekranı Testleri

**URL:** `http://localhost:3000/auth/login`

#### Test 2.1: Normal Giriş
- [ ] Email ve şifre girin
- [ ] "LOGIN" butonuna tıklayın
- [ ] Başarılı giriş sonrası ana sayfaya yönlendirilmeli

**Beklenen Sonuç:** ✓ Kullanıcı giriş yapar

#### Test 2.2: Hatalı Giriş
- [ ] Yanlış email veya şifre girin
- [ ] "LOGIN" butonuna tıklayın
- [ ] Hata mesajı: "Giriş başarısız. E-posta veya şifre hatalı."

**Beklenen Sonuç:** ✓ Hata mesajı gösterilir

#### Test 2.3: Google ile Giriş
- [ ] "Google ile Giriş Yap" butonuna tıklayın
- [ ] Google popup açılmalı
- [ ] Hesap seçin ve onaylayın
- [ ] Başarılı giriş sonrası ana sayfaya yönlendirilmeli
- [ ] Firebase'de kullanıcı profili oluşturulmalı (ilk girişse)

**Beklenen Sonuç:** ✓ Google OAuth başarılı

#### Test 2.4: Şifremi Unuttum Linki
- [ ] "Forgot your password?" linkine tıklayın
- [ ] Şifre sıfırlama sayfasına yönlendirilmeli

**Beklenen Sonuç:** ✓ Yönlendirme başarılı

#### Test 2.5: UI/UX Kontrolleri
- [ ] "Here you can Login" başlığı görünüyor mu?
- [ ] "Signup" linki sağ üstte var mı?
- [ ] Gradient tasarım doğru mu?
- [ ] Glow efekti çalışıyor mu?
- [ ] Google ikonu düzgün görünüyor mu?

**Beklenen Sonuç:** ✓ Tüm UI elementleri tasarıma uygun

---

### ✅ 3. Şifre Sıfırlama Testleri

**URL:** `http://localhost:3000/auth/forgot-password`

#### Test 3.1: Şifre Sıfırlama E-postası Gönderme
- [ ] Kayıtlı bir email girin
- [ ] "Şifre Sıfırlama Bağlantısı Gönder" butonuna tıklayın
- [ ] Başarı mesajı görünmeli
- [ ] Email kutunuzu kontrol edin

**Beklenen Sonuç:** ✓ E-posta gönderilir

#### Test 3.2: Kayıtsız Email
- [ ] Kayıtsız bir email girin
- [ ] Buton tıklansın
- [ ] Hata mesajı gösterilmeli

**Beklenen Sonuç:** ✓ Hata mesajı görünür

---

### ✅ 4. Profil Sayfası Testleri

**URL:** `http://localhost:3000/profile`

#### Test 4.1: Profil Sayfası Erişimi (Giriş Yapmadan)
- [ ] Çıkış yapın (logout)
- [ ] `/profile` URL'sine gitmeyi deneyin
- [ ] Login sayfasına yönlendirilmeli

**Beklenen Sonuç:** ✓ Auth guard çalışıyor

#### Test 4.2: Profil Header Görünümü
- [ ] Giriş yapın
- [ ] Profil sayfasını açın
- [ ] Avatar (veya baş harf) görünüyor mu?
- [ ] Kullanıcı adı ve email doğru mu?
- [ ] Level badge görünüyor mu?
- [ ] XP progress bar çalışıyor mu?
- [ ] Premium badge görünüyor mu? (eğer premium ise)

**Beklenen Sonuç:** ✓ Tüm bilgiler doğru görünür

#### Test 4.3: XP ve Level Sistemi
- [ ] Toplam XP doğru mu?
- [ ] Level hesaplaması doğru mu? (XP / 100 + 1)
- [ ] Progress bar yüzdesi doğru mu?
- [ ] "X XP bir sonraki seviyeye" metni doğru mu?

**Beklenen Sonuç:** ✓ XP sistemi doğru çalışıyor

#### Test 4.4: İstatistikler
- [ ] 4 istatistik kartı görünüyor mu?
  - Okunan Haber (mavi)
  - Paylaşım (yeşil)
  - Quiz Doğru (mor)
  - Bulmaca Çözümü (turuncu)
- [ ] Sayılar doğru mu?
- [ ] İkonlar ve renkler doğru mu?
- [ ] Hover efekti çalışıyor mu?

**Beklenen Sonuç:** ✓ İstatistikler doğru görünür

#### Test 4.5: Rozetler
- [ ] Rozet bölümü görünüyor mu?
- [ ] Rozetler varsa listeleniyor mu?
- [ ] Rozet renkleri rarity'ye göre doğru mu?
  - Common: Gri
  - Rare: Mavi
  - Epic: Mor
  - Legendary: Altın-turuncu gradient
- [ ] Rozet üzerine hover yapınca tooltip görünüyor mu?
- [ ] Rozet yoksa "Henüz rozet kazanmadınız" mesajı var mı?

**Beklenen Sonuç:** ✓ Rozet sistemi çalışıyor

#### Test 4.6: Günlük Görevler
- [ ] Günlük görevler bölümü görünüyor mu?
- [ ] Tamamlanma sayısı doğru mu?
- [ ] Görevler listeleniyor mu?

**Beklenen Sonuç:** ✓ Görevler görünür

#### Test 4.7: Çıkış Yapma
- [ ] "Çıkış Yap" butonuna tıklayın
- [ ] Ana sayfaya yönlendirilmeli
- [ ] Auth state temizlenmeli

**Beklenen Sonuç:** ✓ Başarılı çıkış

---

### ✅ 5. Responsive Tasarım Testleri

#### Test 5.1: Mobil Görünüm (375px)
- [ ] Login formu düzgün görünüyor mu?
- [ ] Butonlar tıklanabilir mi?
- [ ] İstatistik kartları 2 sütun olarak dizilmiş mi?
- [ ] Rozet grid'i düzgün mü?

**Beklenen Sonuç:** ✓ Mobil uyumlu

#### Test 5.2: Tablet Görünüm (768px)
- [ ] Layout düzgün mü?
- [ ] İstatistikler 4 sütun mu?
- [ ] Rozetler 3 sütun mu?

**Beklenen Sonuç:** ✓ Tablet uyumlu

#### Test 5.3: Desktop Görünüm (1920px)
- [ ] Tüm elementler merkezi mi?
- [ ] Max-width uygulanmış mı?
- [ ] Rozetler 4 sütun mu?

**Beklenen Sonuç:** ✓ Desktop uyumlu

---

## 🎨 Tasarım Özellikleri Kontrolü

### Renk Paleti
- [ ] Koyu mavi arka plan: `from-slate-900`, `via-blue-900`
- [ ] Mor vurgular: `purple-600`, `purple-700`
- [ ] Gradient butonlar çalışıyor mu?
- [ ] Wave SVG'leri görünüyor mu?

### Animasyonlar
- [ ] Hover efektleri çalışıyor mu? (`hover:scale-105`)
- [ ] Progress bar animasyonu var mı?
- [ ] Loading spinner'lar çalışıyor mu?
- [ ] Pulse animasyonları (background circles)

### Shadow ve Glow Efektleri
- [ ] Card glow efektleri var mı?
- [ ] Buton shadow'ları çalışıyor mu?
- [ ] Rozet rarity glow'ları görünüyor mu?

---

## 🔥 Firebase Kontrolleri

### Firestore Koleksiyonları
- [ ] `users` koleksiyonu oluşturuldu mu?
- [ ] Kullanıcı dokümanı doğru alanları içeriyor mu?
  ```
  {
    uid, email, username, photoUrl,
    newsReadCount, newsSharedCount, totalXp,
    badges: [],
    isPremium, dailyTasks, completedTasks,
    notificationsEnabled, darkModeEnabled,
    createdAt, updatedAt
  }
  ```
- [ ] `userGameStats` koleksiyonu oluşturuldu mu?
  ```
  {
    crosswordSolved, quizCorrect, quizWrong,
    mapGuessCorrect, mapGuessWrong, totalXp
  }
  ```

### Authentication
- [ ] Email/Password auth aktif mi?
- [ ] Google auth aktif mi?
- [ ] Kullanıcılar listelenebiliyor mu?

---

## 🐛 Bilinen Sorunlar ve Çözümler

### Sorun 1: PowerShell npm Hatası
**Hata:** `npm: File C:\Program Files\nodejs\npm.ps1 cannot be loaded`

**Çözüm:**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Sorun 2: Firebase Auth Hatası
**Hata:** `Firebase: Error (auth/operation-not-allowed)`

**Çözüm:** Firebase Console'da Authentication > Sign-in methods > Email/Password ve Google'ı aktif edin.

### Sorun 3: Hydration Hatası
**Hata:** `Text content does not match server-rendered HTML`

**Çözüm:** Zaten çözüldü - `'use client'` direktifleri eklendi.

### Sorun 4: Environment Variables Bulunamıyor
**Hata:** `Cannot read property 'apiKey' of undefined`

**Çözüm:** 
1. `.env.local` dosyasının `web/` klasöründe olduğundan emin olun
2. Dev sunucusunu yeniden başlatın (`npm run dev`)

---

## 📊 Test Sonuçları Tablosu

Testlerinizi yaparken bu tabloyu doldurun:

| Test No | Test Adı | Durum | Notlar |
|---------|----------|-------|--------|
| 1.1 | Normal Kayıt | ⬜ Başarılı / ⬜ Başarısız | |
| 1.2 | Şifre Eşleşme | ⬜ Başarılı / ⬜ Başarısız | |
| 1.3 | Kısa Şifre | ⬜ Başarılı / ⬜ Başarısız | |
| 1.4 | Mevcut Email | ⬜ Başarılı / ⬜ Başarısız | |
| 1.5 | Register UI | ⬜ Başarılı / ⬜ Başarısız | |
| 2.1 | Normal Giriş | ⬜ Başarılı / ⬜ Başarısız | |
| 2.2 | Hatalı Giriş | ⬜ Başarılı / ⬜ Başarısız | |
| 2.3 | Google Giriş | ⬜ Başarılı / ⬜ Başarısız | |
| 2.4 | Şifre Sıfırlama Link | ⬜ Başarılı / ⬜ Başarısız | |
| 2.5 | Login UI | ⬜ Başarılı / ⬜ Başarısız | |
| 3.1 | Şifre Sıfırlama | ⬜ Başarılı / ⬜ Başarısız | |
| 4.1 | Auth Guard | ⬜ Başarılı / ⬜ Başarısız | |
| 4.2 | Profil Header | ⬜ Başarılı / ⬜ Başarısız | |
| 4.3 | XP Sistemi | ⬜ Başarılı / ⬜ Başarısız | |
| 4.4 | İstatistikler | ⬜ Başarılı / ⬜ Başarısız | |
| 4.5 | Rozetler | ⬜ Başarılı / ⬜ Başarısız | |
| 4.6 | Günlük Görevler | ⬜ Başarılı / ⬜ Başarısız | |
| 4.7 | Çıkış Yapma | ⬜ Başarılı / ⬜ Başarısız | |
| 5.1 | Mobil Responsive | ⬜ Başarılı / ⬜ Başarısız | |
| 5.2 | Tablet Responsive | ⬜ Başarılı / ⬜ Başarısız | |
| 5.3 | Desktop Responsive | ⬜ Başarılı / ⬜ Başarısız | |

---

## 🚀 Production Hazırlığı

Testler başarılıysa, production'a geçmeden önce:

### 1. Code Review
- [ ] Kod kalitesi kontrolü
- [ ] TypeScript hataları yok
- [ ] Console log'lar temizlendi
- [ ] Unused imports temizlendi

### 2. Performance
- [ ] Lighthouse skoru (>90)
- [ ] Bundle size kontrolü
- [ ] Image optimizasyonu

### 3. Security
- [ ] Environment variables güvende
- [ ] Firebase security rules güncel
- [ ] XSS koruması
- [ ] CSRF koruması

### 4. SEO
- [ ] Meta tags eklendi
- [ ] OG tags eklendi
- [ ] Sitemap güncel

---

## 📞 Destek

Sorunlarla karşılaşırsanız:

1. **Firebase Console:** [console.firebase.google.com](https://console.firebase.google.com)
2. **Next.js Docs:** [nextjs.org/docs](https://nextjs.org/docs)
3. **Tailwind CSS:** [tailwindcss.com/docs](https://tailwindcss.com/docs)

---

## ✅ Hızlı Başlangıç Checklist

```bash
# 1. Klasöre git
cd web

# 2. Bağımlılıkları yükle
npm install

# 3. .env.local dosyasını kontrol et
# (NEXT_PUBLIC_FIREBASE_* değişkenlerinin olduğundan emin ol)

# 4. Sunucuyu başlat
npm run dev

# 5. Testlere başla
# Login: http://localhost:3000/auth/login
# Register: http://localhost:3000/auth/register
# Profile: http://localhost:3000/profile
```

---

**Test Başlangıç Tarihi:** _______  
**Test Eden Kişi:** _______  
**Toplam Geçen Test:** ___ / 20  
**Notlar:**

---

*Bu rehber feature/auth-profile-screen branch'i için hazırlanmıştır.*
*Son güncelleme: Ekim 2025*

