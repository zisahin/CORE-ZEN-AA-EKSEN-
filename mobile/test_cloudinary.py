#!/usr/bin/env python3
"""
Cloudinary bağlantısını test etmek için script
"""

import os
import cloudinary
import cloudinary.uploader
from moviepy.editor import ColorClip

def test_cloudinary_connection():
    """Cloudinary bağlantısını test et"""
    try:
        print("☁️ Cloudinary bağlantısı test ediliyor...")
        
        # Environment variables'ları kontrol et
        cloud_name = os.getenv('CLOUDINARY_CLOUD_NAME')
        api_key = os.getenv('CLOUDINARY_API_KEY')
        api_secret = os.getenv('CLOUDINARY_API_SECRET')
        
        if not all([cloud_name, api_key, api_secret]):
            print("❌ Cloudinary environment variables eksik!")
            print("💡 Şu komutları çalıştırın:")
            print("export CLOUDINARY_CLOUD_NAME=your-cloud-name")
            print("export CLOUDINARY_API_KEY=your-api-key")
            print("export CLOUDINARY_API_SECRET=your-api-secret")
            return False
        
        # Cloudinary'yi yapılandır
        cloudinary.config(
            cloud_name=cloud_name,
            api_key=api_key,
            api_secret=api_secret
        )
        
        print(f"✅ Cloudinary yapılandırıldı: {cloud_name}")
        
        # Test videosu oluştur
        print("🎬 Test videosu oluşturuluyor...")
        test_video = ColorClip(size=(640, 480), color=(0, 100, 200), duration=3)
        test_path = "test_video.mp4"
        test_video.write_videofile(test_path, fps=24, verbose=False, logger=None)
        
        print("☁️ Test videosu Cloudinary'ye yükleniyor...")
        
        # Cloudinary'ye yükle
        result = cloudinary.uploader.upload(
            test_path,
            resource_type="video",
            folder="aa-eksen-test",
            public_id="test_video",
            overwrite=True
        )
        
        video_url = result['secure_url']
        print(f"✅ Test videosu başarıyla yüklendi!")
        print(f"🔗 Video URL: {video_url}")
        print(f"📊 Dosya boyutu: {result.get('bytes', 0)} bytes")
        print(f"⏱️ Süre: {result.get('duration', 0)} saniye")
        
        # Test dosyasını temizle
        os.remove(test_path)
        print("🧹 Test dosyası temizlendi")
        
        print("🎉 Cloudinary testi başarılı!")
        return True
        
    except Exception as e:
        print(f"❌ Cloudinary test hatası: {e}")
        return False

if __name__ == "__main__":
    success = test_cloudinary_connection()
    if success:
        print("\n🚀 Cloudinary hazır! Backend'i başlatabilirsiniz.")
    else:
        print("\n🔧 Lütfen Cloudinary ayarlarını kontrol edin.")
