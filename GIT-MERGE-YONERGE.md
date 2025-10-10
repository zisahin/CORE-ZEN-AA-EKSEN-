# 🔀 Git Branch Merge Yönergesi

## 📊 Mevcut Durum
- **Şu anki branch:** `feature/auth-profile-screen`
- **Durum:** Birçok dosyada değişiklik var (commit edilmemiş)
- **Hedef:** Arkadaşınızın branch'i ile merge etmek

---

## 🎯 Adım Adım Merge İşlemi

### 1️⃣ Mevcut Değişikliklerinizi Kaydedin (Commit)

```bash
# Web klasöründe olduğunuzdan emin olun
cd web

# Tüm değişiklikleri staging area'ya ekleyin
git add .

# Commit mesajı yazın
git commit -m "feat: Add gamified auth & profile system with gradient design

- Implement Firebase authentication (email/password, Google OAuth)
- Create gamified profile page with XP, levels, badges
- Add gradient theme (purple-blue) to entire site
- Update TopNav and LeftSidebar with new design
- Add AuthContext and auth services
- Create login, register, profile pages"
```

**Alternatif commit mesajı (Türkçe):**
```bash
git commit -m "feat: Oyunlaştırılmış giriş ve profil sistemi eklendi

- Firebase authentication entegrasyonu (email/şifre, Google OAuth)
- XP, seviye ve rozet sistemi ile profil sayfası
- Tüm siteye mor-mavi gradient tasarım
- TopNav ve LeftSidebar yeni tasarım
- AuthContext ve auth servisleri
- Login, kayıt ve profil sayfaları"
```

---

### 2️⃣ Remote Repository Bilgilerini Kontrol Edin

```bash
# Remote'ları listeleyin
git remote -v

# Çıktı şuna benzer olmalı:
# origin  https://github.com/kullanici/repo.git (fetch)
# origin  https://github.com/kullanici/repo.git (push)
```

**Eğer remote yoksa ekleyin:**
```bash
git remote add origin https://github.com/KULLANICI_ADI/REPO_ADI.git
```

---

### 3️⃣ Tüm Branch'leri Güncelleyin (Fetch)

```bash
# Tüm remote branch'leri getirin
git fetch origin

# Mevcut branch'leri görün
git branch -a

# Çıktı örneği:
# * feature/auth-profile-screen
#   main
#   remotes/origin/main
#   remotes/origin/arkadas-branch-adi
```

---

### 4️⃣ Arkadaşınızın Branch'ini İnceleyin

```bash
# Arkadaşınızın branch'ine geçin (yeni local branch oluşturarak)
git checkout -b arkadas-feature origin/arkadas-branch-adi

# Değişiklikleri inceleyin
git log --oneline -10

# Tekrar kendi branch'inize dönün
git checkout feature/auth-profile-screen
```

---

## 🔀 Merge Stratejileri

### **Seçenek A: Main Branch'e Sırayla Merge (ÖNERİLEN)**

```bash
# 1. Main branch'e geçin
git checkout main

# 2. Main'i güncelleyin
git pull origin main

# 3. Arkadaşınızın branch'ini main'e merge edin
git merge origin/arkadas-branch-adi

# 4. Conflict varsa çözün (aşağıda detaylı açıklama)

# 5. Push edin
git push origin main

# 6. Kendi branch'inize geçin
git checkout feature/auth-profile-screen

# 7. Main'deki güncel hali kendi branch'inize alın
git merge main

# 8. Conflict varsa çözün

# 9. Push edin
git push origin feature/auth-profile-screen
```

---

### **Seçenek B: Doğrudan Arkadaşın Branch'i ile Merge**

```bash
# 1. Kendi branch'inizde olduğunuzdan emin olun
git checkout feature/auth-profile-screen

# 2. Arkadaşınızın branch'ini kendi branch'inize merge edin
git merge origin/arkadas-branch-adi

# 3. Conflict varsa çözün

# 4. Push edin
git push origin feature/auth-profile-screen
```

---

### **Seçenek C: Rebase (Temiz Git History İçin)**

```bash
# 1. Main'i güncelleyin
git checkout main
git pull origin main

# 2. Kendi branch'inize dönün
git checkout feature/auth-profile-screen

# 3. Main üzerine rebase yapın
git rebase main

# 4. Conflict varsa çözün (her commit için)
# git rebase --continue (conflict çözümünden sonra)
# git rebase --abort (iptal etmek için)

# 5. Force push (DİKKAT: Sadece kendi branch'inizde!)
git push origin feature/auth-profile-screen --force
```

---

## ⚠️ Conflict (Çakışma) Çözümü

### Conflict Olduğunda Ne Yapmalı?

```bash
# Conflict olan dosyaları görün
git status

# Dosyaları açın ve şu işaretleri bulun:
<<<<<<< HEAD
// Sizin kodunuz
=======
// Arkadaşınızın kodu
>>>>>>> arkadas-branch-adi
```

### Conflict Çözüm Adımları:

1. **Dosyayı Açın** (VSCode, Cursor vb.)
2. **İşaretleri Silin** (`<<<<<<<`, `=======`, `>>>>>>>`)
3. **Doğru Kodu Seçin veya Birleştirin**
4. **Dosyayı Kaydedin**
5. **Stage'e Ekleyin:**
   ```bash
   git add dosya-adi
   ```
6. **Commit Tamamlayın:**
   ```bash
   git commit -m "merge: Resolve conflicts with arkadas-branch"
   ```

### VSCode/Cursor'da Otomatik Çözüm:

VSCode conflict editör gösterir:
- **Accept Current Change** - Sizin versiyonunuzu al
- **Accept Incoming Change** - Arkadaşınızın versiyonunu al
- **Accept Both Changes** - İkisini de al
- **Compare Changes** - Karşılaştır

---

## 📋 Örnek Senaryo

### Durum:
- Siz: `feature/auth-profile-screen` (profil sistemi)
- Arkadaş: `feature/news-system` (haber sistemi)
- Hedef: Her ikisini de `main`'e merge etmek

### Çözüm:

```bash
# 1. Değişikliklerinizi commit edin
git add .
git commit -m "feat: Auth and profile system complete"

# 2. Main'e geçin
git checkout main

# 3. Main'i güncelleyin
git pull origin main

# 4. Arkadaşınızın branch'ini fetch edin
git fetch origin

# 5. Arkadaşınızın branch'ini main'e merge edin
git merge origin/feature/news-system
# Conflict varsa çözün

# 6. Push edin
git push origin main

# 7. Kendi branch'inize dönün
git checkout feature/auth-profile-screen

# 8. Main'i kendi branch'inize merge edin
git merge main
# Conflict varsa çözün

# 9. Push edin
git push origin feature/auth-profile-screen

# 10. Son olarak PR (Pull Request) oluşturun
# GitHub/GitLab'da feature/auth-profile-screen -> main PR açın
```

---

## 🚀 Pull Request (PR) Oluşturma

### GitHub'da:

1. **GitHub repository'ye gidin**
2. **Pull Requests** sekmesine tıklayın
3. **New Pull Request** butonuna tıklayın
4. **Base:** `main` - **Compare:** `feature/auth-profile-screen`
5. **Başlık ve açıklama yazın:**
   ```
   Başlık: ✨ Oyunlaştırılmış Auth & Profil Sistemi
   
   Açıklama:
   ## 🎮 Özellikler
   - Firebase Authentication (Email/Password, Google OAuth)
   - Oyunlaştırılmış profil sistemi (XP, Level, Rozetler)
   - Mor-mavi gradient tasarım
   - Login, Register, Profil sayfaları
   - AuthContext ve servisler
   
   ## 📸 Ekran Görüntüleri
   (Ekran görüntüleri ekleyin)
   
   ## ✅ Test Edildi
   - [x] Login işlemi
   - [x] Register işlemi
   - [x] Profil sayfası
   - [x] Responsive tasarım
   ```
6. **Create Pull Request** butonuna tıklayın
7. **Arkadaşınızdan review isteyin**

---

## 🔍 Yararlı Git Komutları

```bash
# Branch'leri listele
git branch -a

# Son commit'leri görüntüle
git log --oneline -10

# Değişiklikleri göster (staged olmayan)
git diff

# Değişiklikleri göster (staged)
git diff --staged

# Commit geçmişini görsel olarak göster
git log --graph --oneline --all

# Belirli bir dosyanın geçmişini gör
git log -p -- dosya-adi

# Son commit'i geri al (değişiklikleri koruyarak)
git reset --soft HEAD~1

# Tüm değişiklikleri geri al (DİKKAT!)
git reset --hard HEAD

# Belirli bir commit'e geri dön
git checkout commit-hash

# Stash (geçici kaydetme)
git stash save "Work in progress"
git stash list
git stash pop
```

---

## ⚠️ Dikkat Edilmesi Gerekenler

### ❌ YAPMAYIN:
1. **Force push main branch'e:**
   ```bash
   git push origin main --force  # ASLA!
   ```

2. **Commit edilmemiş değişikliklerle merge:**
   - Önce commit edin veya stash'leyin

3. **Conflict'leri çözmeden commit:**
   - Önce conflict'leri çözün

### ✅ YAPIN:
1. **Sık sık commit edin:**
   - Küçük, anlamlı commit'ler yapın

2. **Açıklayıcı commit mesajları yazın:**
   ```bash
   feat: Yeni özellik ekle
   fix: Hata düzelt
   docs: Dokümantasyon güncelle
   style: Stil değişiklikleri
   refactor: Kod iyileştirme
   test: Test ekle
   ```

3. **Merge öncesi test edin:**
   - Kodu çalıştırın ve test edin

4. **Branch'leri güncel tutun:**
   - Düzenli olarak `git pull` yapın

---

## 📞 Sorun Yaşarsanız

### Merge İptal Etmek:
```bash
git merge --abort
```

### Rebase İptal Etmek:
```bash
git rebase --abort
```

### Son Commit'i Değiştirmek:
```bash
git commit --amend -m "Yeni commit mesajı"
```

### Yanlış Branch'e Commit Yaptıysanız:
```bash
# Commit'i başka branch'e taşı
git checkout dogru-branch
git cherry-pick commit-hash
git checkout yanlis-branch
git reset --hard HEAD~1
```

---

## 🎯 Hızlı Başlangıç Checklist

- [ ] 1. Değişiklikleri commit et (`git commit`)
- [ ] 2. Remote'u kontrol et (`git remote -v`)
- [ ] 3. Branch'leri fetch et (`git fetch origin`)
- [ ] 4. Arkadaşın branch'ini incele
- [ ] 5. Merge stratejisi seç (A, B veya C)
- [ ] 6. Merge yap
- [ ] 7. Conflict'leri çöz (varsa)
- [ ] 8. Test et
- [ ] 9. Push et
- [ ] 10. PR oluştur

---

## 📚 Ek Kaynaklar

- [Git Documentation](https://git-scm.com/doc)
- [GitHub Flow](https://docs.github.com/en/get-started/quickstart/github-flow)
- [Atlassian Git Tutorial](https://www.atlassian.com/git/tutorials)

---

**Başarılar! 🚀**

*Herhangi bir sorun yaşarsanız, git komutlarını dikkatli kullanın ve gerekirse yedek alın.*

