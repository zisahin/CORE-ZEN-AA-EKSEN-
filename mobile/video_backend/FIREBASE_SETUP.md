# 🔥 Firebase Service Account Kurulum Rehberi

## Adım 1: Firebase Console'a Giriş
1. [Firebase Console](https://console.firebase.google.com/) adresine git
2. AA Eksen projenizi seçin

## Adım 2: Service Account Oluştur
1. Sol menüden **⚙️ Project Settings** (Proje Ayarları) tıkla
2. Üst menüden **Service accounts** sekmesine git
3. **Generate new private key** (Yeni özel anahtar oluştur) butonuna tıkla
4. **Generate key** butonuna tıkla
5. JSON dosyası otomatik olarak indirilecek

## Adım 3: Dosyayı Yerleştir
1. İndirilen JSON dosyasını `video_backend/` klasörüne kopyala
2. Dosya adını `firebase-credentials.json` olarak değiştir

## Adım 4: Firebase Storage'ı Aktifleştir
1. Firebase Console'da sol menüden **Storage** seç
2. **Get started** butonuna tıkla
3. **Start in test mode** seç (geliştirme için)
4. **Next** → **Done**

## Adım 5: Storage Bucket URL'ini Al
1. Storage sayfasında üst kısımda bucket URL'ini göreceksin
2. Örnek: `your-project-id.appspot.com`
3. Bu URL'yi `video_backend/main.py` dosyasında güncelle:

```python
firebase_admin.initialize_app(cred, {
    'storageBucket': 'your-actual-project-id.appspot.com'  # Buraya gerçek bucket URL'ini yaz
})
```

## Adım 6: Test Et
```bash
cd video_backend
source venv/bin/activate
python main.py
```

## 🔐 Güvenlik Notları
- `firebase-credentials.json` dosyasını asla Git'e commit etme
- Bu dosya zaten `.gitignore`'da var
- Production'da environment variables kullan

## 📋 Dosya Yapısı
```
video_backend/
├── firebase-credentials.json          # ← Buraya koyacaksın
├── firebase-credentials-example.json  # ← Örnek dosya
├── main.py
├── requirements.txt
└── ...
```

## ❌ Sorun Giderme

### "File not found" hatası:
- Dosya adının tam olarak `firebase-credentials.json` olduğundan emin ol
- Dosyanın `video_backend/` klasöründe olduğundan emin ol

### "Invalid credentials" hatası:
- JSON dosyasının bozuk olmadığından emin ol
- Doğru projeden indirdiğinden emin ol

### "Storage bucket not found" hatası:
- Firebase Storage'ı aktifleştirdiğinden emin ol
- Bucket URL'ini doğru yazdığından emin ol

## 🎯 Başarı Mesajı
Backend başarıyla çalıştığında şu mesajı göreceksin:
```
✅ Firebase bağlantısı kuruldu
✅ Whisper model yüklendi
✅ Çalışma dizinleri hazırlandı
🔥 Firebase listener aktif!
```
