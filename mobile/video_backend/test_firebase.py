#!/usr/bin/env python3
"""
Firebase bağlantısını test etmek için basit script
"""

import os
import sys
import firebase_admin
from firebase_admin import credentials, firestore, storage

def test_firebase_connection():
    """Firebase bağlantısını test et"""
    try:
        print("🔥 Firebase bağlantısı test ediliyor...")
        
        # Credentials dosyası var mı?
        cred_path = 'firebase-credentials.json'
        if not os.path.exists(cred_path):
            print(f"❌ Firebase credentials dosyası bulunamadı: {cred_path}")
            print("💡 Firebase Console'dan service account key indirin ve buraya koyun")
            return False
        
        # Firebase'i başlat
        cred = credentials.Certificate(cred_path)
        firebase_admin.initialize_app(cred, {
            'storageBucket': 'aa-eksen-71097.appspot.com'  # Gerçek proje ID'si
        })
        
        # Firestore test
        db = firestore.client()
        print("✅ Firestore bağlantısı başarılı")
        
        # Storage test
        bucket = storage.bucket()
        print("✅ Storage bağlantısı başarılı")
        
        # Basit veri testi
        test_doc = db.collection('test').document('connection_test')
        test_doc.set({'timestamp': firestore.SERVER_TIMESTAMP, 'status': 'connected'})
        print("✅ Firestore yazma testi başarılı")
        
        # Test dokümanını sil
        test_doc.delete()
        print("✅ Firestore silme testi başarılı")
        
        print("🎉 Tüm testler başarılı! Firebase hazır.")
        return True
        
    except Exception as e:
        print(f"❌ Firebase bağlantı hatası: {e}")
        print("\n🔧 Çözüm önerileri:")
        print("1. firebase-credentials.json dosyasının doğru konumda olduğunu kontrol edin")
        print("2. Firebase Console'dan doğru service account key'i indirdiğinizi kontrol edin")
        print("3. Storage bucket URL'inin doğru olduğunu kontrol edin")
        print("4. Firebase Storage'ın aktif olduğunu kontrol edin")
        return False

if __name__ == "__main__":
    success = test_firebase_connection()
    sys.exit(0 if success else 1)
