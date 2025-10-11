#!/usr/bin/env python3
"""
AA Eksen - Video Generation Backend
Firebase entegrasyonu ile otomatik video üretimi
"""

import os
import sys
import json
import time
import logging
import threading
from datetime import datetime
from pathlib import Path

import firebase_admin
from firebase_admin import credentials, firestore, storage
import openai
from moviepy.editor import *
import whisper
import requests
from PIL import Image, ImageDraw, ImageFont
import numpy as np
import cloudinary
import cloudinary.uploader
from dotenv import load_dotenv
from unsplash.api import Api
from unsplash.auth import Auth
import urllib.request
import io

# Logging setup
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('video_backend.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class VideoGenerationBackend:
    def __init__(self):
        """Initialize Firebase and AI services"""
        # .env dosyasını yükle
        load_dotenv()
        logger.info("📄 .env dosyası yüklendi")
        
        # Konfigürasyonları yükle
        self.load_config()
        
        self.setup_firebase()
        self.setup_ai_services()
        self.setup_directories()
    
    def load_config(self):
        """Konfigürasyonları .env dosyasından yükle"""
        try:
            # Video ayarları
            self.max_video_duration = int(os.getenv('MAX_VIDEO_DURATION', 60))
            self.video_quality = os.getenv('VIDEO_QUALITY', 'high')
            
            # Firebase ayarları
            self.firebase_credentials = os.getenv('FIREBASE_CREDENTIALS', 'firebase-credentials.json')
            
            # OpenAI ayarları
            self.openai_api_key = os.getenv('OPENAI_API_KEY')
            
            # Cloudinary ayarları
            self.cloudinary_cloud_name = os.getenv('CLOUDINARY_CLOUD_NAME')
            self.cloudinary_api_key = os.getenv('CLOUDINARY_API_KEY')
            self.cloudinary_api_secret = os.getenv('CLOUDINARY_API_SECRET')
            
            # Unsplash ayarları
            self.unsplash_access_key = os.getenv('UNSPLASH_ACCESS_KEY')
            
            logger.info(f"⚙️ Konfigürasyon yüklendi:")
            logger.info(f"  📹 Max video süresi: {self.max_video_duration}s")
            logger.info(f"  🎨 Video kalitesi: {self.video_quality}")
            logger.info(f"  🔥 Firebase credentials: {self.firebase_credentials}")
            logger.info(f"  🤖 OpenAI API: {'✅ Var' if self.openai_api_key else '❌ Yok'}")
            logger.info(f"  ☁️ Cloudinary: {'✅ Var' if all([self.cloudinary_cloud_name, self.cloudinary_api_key, self.cloudinary_api_secret]) else '❌ Eksik'}")
            logger.info(f"  🖼️ Unsplash: {'✅ Var' if self.unsplash_access_key else '❌ Yok'}")
            
        except Exception as e:
            logger.error(f"❌ Konfigürasyon yükleme hatası: {e}")
            # Varsayılan değerler
            self.max_video_duration = 60
            self.video_quality = 'high'
            self.firebase_credentials = 'firebase-credentials.json'
        
    def setup_firebase(self):
        """Firebase bağlantısını kur"""
        try:
            # Firebase credentials (.env'den)
            cred_path = self.firebase_credentials
            if not os.path.exists(cred_path):
                logger.error(f"❌ Firebase credentials bulunamadı: {cred_path}")
                sys.exit(1)
                
            cred = credentials.Certificate(cred_path)
            firebase_admin.initialize_app(cred, {
                'storageBucket': 'aa-eksen-71097.firebasestorage.app'  # Firebase Storage bucket URL'si
            })
            
            self.db = firestore.client()
            self.bucket = storage.bucket()
            logger.info("✅ Firebase bağlantısı kuruldu")
            
        except Exception as e:
            logger.error(f"❌ Firebase bağlantı hatası: {e}")
            sys.exit(1)
    
    def setup_ai_services(self):
        """AI servislerini kur"""
        try:
            # OpenAI API Key (.env'den)
            if self.openai_api_key:
                openai.api_key = self.openai_api_key
                logger.info("✅ OpenAI API key yüklendi")
            else:
                logger.warning("⚠️ OpenAI API key bulunamadı")
            
            # Whisper model
            self.whisper_model = whisper.load_model("base")
            logger.info("✅ Whisper model yüklendi")
            
            # Cloudinary setup
            self.setup_cloudinary()
            
            # Unsplash setup
            self.setup_unsplash()
            
        except Exception as e:
            logger.error(f"❌ AI servisleri kurulum hatası: {e}")
    
    def setup_cloudinary(self):
        """Cloudinary yapılandırması (.env'den)"""
        try:
            if all([self.cloudinary_cloud_name, self.cloudinary_api_key, self.cloudinary_api_secret]):
                cloudinary.config(
                    cloud_name=self.cloudinary_cloud_name,
                    api_key=self.cloudinary_api_key,
                    api_secret=self.cloudinary_api_secret
                )
                logger.info(f"✅ Cloudinary yapılandırıldı: {self.cloudinary_cloud_name}")
            else:
                logger.warning("⚠️ Cloudinary bilgileri eksik - Firebase Storage kullanılacak")
        except Exception as e:
            logger.warning(f"⚠️ Cloudinary yapılandırma hatası: {e}")
    
    def setup_unsplash(self):
        """Unsplash API yapılandırması"""
        try:
            if self.unsplash_access_key:
                auth = Auth(client_id=self.unsplash_access_key, client_secret="", redirect_uri="", code="")
                self.unsplash_api = Api(auth)
                logger.info("✅ Unsplash API yapılandırıldı")
            else:
                self.unsplash_api = None
                logger.warning("⚠️ Unsplash API key yok - görsel ekleme devre dışı")
        except Exception as e:
            logger.warning(f"⚠️ Unsplash yapılandırma hatası: {e}")
            self.unsplash_api = None
    
    def setup_directories(self):
        """Çalışma dizinlerini oluştur"""
        self.work_dir = Path("video_work")
        self.work_dir.mkdir(exist_ok=True)
        
        self.temp_dir = self.work_dir / "temp"
        self.temp_dir.mkdir(exist_ok=True)
        
        self.output_dir = self.work_dir / "output"
        self.output_dir.mkdir(exist_ok=True)
        
        logger.info("✅ Çalışma dizinleri hazırlandı")
    
    def start_listening(self):
        """Firebase'deki video isteklerini dinlemeye başla"""
        logger.info("🔥 Firebase listener başlatılıyor...")
        
        def on_snapshot(doc_snapshot, changes, read_time):
            for change in changes:
                if change.type.name == 'ADDED':
                    request_data = change.document.to_dict()
                    request_id = change.document.id
                    
                    if request_data.get('status') == 'PENDING':
                        logger.info(f"🎬 Yeni video isteği: {request_id}")
                        # Ayrı thread'de işle
                        threading.Thread(
                            target=self.process_video_request,
                            args=(request_id, request_data),
                            daemon=True
                        ).start()
        
        # video_generation_requests koleksiyonunu dinle
        self.db.collection('video_generation_requests').on_snapshot(on_snapshot)
        logger.info("🔥 Firebase listener aktif!")
        
        # Ana thread'i canlı tut
        try:
            while True:
                time.sleep(1)
        except KeyboardInterrupt:
            logger.info("👋 Backend kapatılıyor...")
    
    def update_request_status(self, request_id: str, status: str, progress: int, step: str):
        """İstek durumunu güncelle"""
        try:
            self.db.collection('video_generation_requests').document(request_id).update({
                'status': status,
                'progress': progress,
                'currentStep': step,
                'updatedAt': firestore.SERVER_TIMESTAMP
            })
            logger.info(f"📊 Status güncellendi: {request_id} -> {status} ({progress}%)")
        except Exception as e:
            logger.error(f"❌ Status güncellenirken hata: {e}")
    
    def process_video_request(self, request_id: str, request_data: dict):
        """Video oluşturma pipeline'ı"""
        try:
            logger.info(f"🎬 Video işleme başladı: {request_id}")
            
            # 1. Status güncelle: PROCESSING
            self.update_request_status(request_id, 'PROCESSING', 10, "İşlem başlatıldı")
            
            # 2. Haber verisini al
            news_id = request_data['newsId']
            news_data = self.get_news_from_firebase(news_id)
            if not news_data:
                raise Exception(f"Haber bulunamadı: {news_id}")
            
            # 3. Whisper transkripsiyon (eğer ses varsa)
            self.update_request_status(request_id, 'TRANSCRIBING', 30, "Ses-metin dönüşümü")
            transcription = self.generate_transcription(news_data)
            
            # 4. Video oluştur
            self.update_request_status(request_id, 'GENERATING', 70, "Video oluşturuluyor")
            video_path = self.generate_video(news_data, transcription, request_data.get('config', {}))
            
            # 5. Cloudinary'ye yükle (Sadece Cloudinary)
            self.update_request_status(request_id, 'UPLOADING', 90, "Video Cloudinary'ye yükleniyor")
            video_url = self.upload_video_to_cloudinary(video_path, request_id)
            
            # 6. Video kaydını oluştur
            video_record = self.create_video_record(news_data, video_url, transcription, request_data)
            video_id = self.save_video_to_firebase(video_record)
            
            # 7. Tamamlandı
            self.complete_request(request_id, video_id)
            
            # 8. Temp dosyaları temizle
            self.cleanup_temp_files(video_path)
            
            logger.info(f"✅ Video başarıyla oluşturuldu: {request_id} -> {video_id}")
            
        except Exception as e:
            logger.error(f"❌ Video oluşturma hatası ({request_id}): {e}")
            self.fail_request(request_id, str(e))
    
    def get_news_from_firebase(self, news_id: str) -> dict:
        """Firebase'den haber verisini al"""
        try:
            doc = self.db.collection('news').document(news_id).get()
            if doc.exists:
                return doc.to_dict()
            return None
        except Exception as e:
            logger.error(f"❌ Haber alınırken hata: {e}")
            return None
    
    def generate_transcription(self, news_data: dict) -> str:
        """Haber içeriğinden transkripsiyon oluştur"""
        try:
            # Haber metnini kullan (gerçek uygulamada ses dosyası olabilir)
            content = news_data.get('content', '')
            title = news_data.get('title', '')
            
            # Basit transkripsiyon (gerçek uygulamada Whisper kullanılabilir)
            transcription = f"{title}. {content}"
            
            # Maksimum uzunluk sınırla (video süresi için)
            max_length = 500
            if len(transcription) > max_length:
                transcription = transcription[:max_length] + "..."
            
            logger.info(f"📝 Transkripsiyon oluşturuldu: {len(transcription)} karakter")
            return transcription
            
        except Exception as e:
            logger.error(f"❌ Transkripsiyon hatası: {e}")
            return news_data.get('title', 'Haber')
    
    def generate_video(self, news_data: dict, transcription: str, config: dict) -> str:
        """Gerçek haber videosu oluştur - AI ses narasyonu + animasyonlar"""
        try:
            logger.info("🎬 Profesyonel haber videosu oluşturuluyor...")

            # Video ayarları
            width, height = 1920, 1080
            fps = 30
            
            # Haber metnini kısalt ve ses için hazırla
            news_text = self.prepare_news_text(news_data, transcription)
            
            # 1. AI Ses Narasyonu Oluştur
            logger.info("🎙️ AI ses narasyonu oluşturuluyor...")
            audio_path = self.generate_ai_voice(news_text)
            
            # Ses dosyasının süresine göre video süresi
            if audio_path and os.path.exists(audio_path):
                audio_clip = AudioFileClip(audio_path)
                duration = min(audio_clip.duration, self.max_video_duration)
                audio_clip = audio_clip.subclip(0, duration)
            else:
                duration = min(len(news_text) * 0.08, self.max_video_duration)  # Fallback
                audio_clip = None
            
            if duration < 5:
                duration = 5  # Minimum 5 saniye

            logger.info(f"📏 Video süresi: {duration:.1f} saniye")

            # 2. Haber konusuna göre görsel bul
            background_image = self.get_news_related_image(news_data, news_text)
            
            # 3. Video Sahnelerini Oluştur
            clips = self.create_news_scenes(news_data, news_text, width, height, duration, background_image)
            
            # 3. Video birleştir
            final_video = CompositeVideoClip(clips)
            
            # 4. Ses ekle
            if audio_clip:
                final_video = final_video.set_audio(audio_clip)
                logger.info("🔊 AI ses narasyonu eklendi")
            
            # 5. Video dosyasını kaydet
            output_path = self.temp_dir / f"news_video_{int(time.time())}.mp4"
            final_video.write_videofile(
                str(output_path),
                fps=fps,
                codec='libx264',
                audio_codec='aac' if audio_clip else None,
                temp_audiofile=None,
                remove_temp=True,
                verbose=False,
                logger=None
            )

            # Temizlik
            if audio_clip:
                audio_clip.close()
            final_video.close()
            
            # Geçici dosyaları temizle
            if audio_path and os.path.exists(audio_path):
                os.remove(audio_path)
            if background_image and os.path.exists(background_image):
                os.remove(background_image)

            logger.info(f"✅ Profesyonel haber videosu oluşturuldu: {output_path}")
            logger.info(f"📊 Video: {duration:.1f}s, {width}x{height}, {fps}fps")
            logger.info(f"🎙️ Ses narasyonu: {'✅ Var' if audio_clip else '❌ Yok'}")

            return str(output_path)

        except Exception as e:
            logger.error(f"❌ Video oluşturma hatası: {e}")
            return self.create_fallback_video()
    
    def prepare_news_text(self, news_data: dict, transcription: str) -> str:
        """Haber metnini ses narasyonu için hazırla"""
        try:
            title = news_data.get('title', '')
            content = transcription or news_data.get('content', '')
            
            # Metni temizle ve kısalt
            text = f"{title}. {content}"
            
            # Maksimum 500 karakter (yaklaşık 30-40 saniye konuşma)
            if len(text) > 500:
                text = text[:497] + "..."
            
            # Noktalama işaretlerini düzelt
            text = text.replace('..', '.').replace('  ', ' ').strip()
            
            logger.info(f"📝 Ses metni hazırlandı: {len(text)} karakter")
            return text
            
        except Exception as e:
            logger.error(f"❌ Metin hazırlama hatası: {e}")
            return news_data.get('title', 'Haber')
    
    def generate_ai_voice(self, text: str) -> str:
        """OpenAI TTS ile ses narasyonu oluştur"""
        try:
            if not self.openai_api_key:
                logger.warning("⚠️ OpenAI API key yok, ses narasyonu atlanıyor")
                return None
            
            logger.info("🎙️ OpenAI TTS ile ses oluşturuluyor...")
            
            # OpenAI TTS API çağrısı
            from openai import OpenAI
            client = OpenAI(api_key=self.openai_api_key)
            
            response = client.audio.speech.create(
                model="tts-1",
                voice="nova",  # Türkçe için uygun ses
                input=text,
                speed=1.0
            )
            
            # Ses dosyasını kaydet
            audio_path = self.temp_dir / f"narration_{int(time.time())}.mp3"
            response.stream_to_file(audio_path)
            
            logger.info(f"✅ AI ses narasyonu oluşturuldu: {audio_path}")
            return str(audio_path)
            
        except Exception as e:
            logger.error(f"❌ AI ses oluşturma hatası: {e}")
            return None
    
    def get_news_related_image(self, news_data: dict, text: str) -> str:
        """Haber konusuna göre ilgili görsel bul"""
        try:
            if not self.unsplash_api:
                logger.warning("⚠️ Unsplash API yok, görsel atlanıyor")
                return None
            
            # Haber kategorisi ve içeriğinden anahtar kelimeler çıkar
            category = news_data.get('category', '').lower()
            title = news_data.get('title', '').lower()
            
            # Kategori bazlı anahtar kelimeler
            search_terms = self.get_search_terms_for_category(category, title, text)
            
            logger.info(f"🔍 Görsel aranıyor: {search_terms}")
            
            # Unsplash'dan görsel ara
            for term in search_terms:
                try:
                    photos = self.unsplash_api.photo.search(query=term, per_page=5, orientation="landscape")
                    if photos and photos['results']:
                        photo = photos['results'][0]  # İlk sonucu al
                        image_url = photo['urls']['regular']  # 1080px genişlik
                        
                        # Görseli indir
                        image_path = self.download_image(image_url, term)
                        if image_path:
                            logger.info(f"✅ Görsel bulundu: {term} -> {image_path}")
                            return image_path
                except Exception as e:
                    logger.warning(f"⚠️ '{term}' için görsel arama hatası: {e}")
                    continue
            
            logger.warning("⚠️ Uygun görsel bulunamadı")
            return None
            
        except Exception as e:
            logger.error(f"❌ Görsel arama hatası: {e}")
            return None
    
    def get_search_terms_for_category(self, category: str, title: str, text: str) -> list:
        """Kategori ve içeriğe göre arama terimleri oluştur"""
        terms = []
        
        # Kategori bazlı terimler
        category_terms = {
            'spor': ['sports', 'football', 'basketball', 'athlete', 'stadium'],
            'teknoloji': ['technology', 'computer', 'innovation', 'digital', 'tech'],
            'ekonomi': ['business', 'finance', 'economy', 'money', 'market'],
            'sağlık': ['health', 'medical', 'hospital', 'doctor', 'healthcare'],
            'eğitim': ['education', 'school', 'university', 'student', 'learning'],
            'tarım': ['agriculture', 'farming', 'crops', 'field', 'harvest'],
            'turizm': ['tourism', 'travel', 'vacation', 'destination', 'hotel'],
            'kültür': ['culture', 'art', 'museum', 'festival', 'tradition'],
            'çevre': ['environment', 'nature', 'green', 'sustainability', 'ecology'],
            'ulaştırma': ['transportation', 'traffic', 'road', 'vehicle', 'infrastructure'],
            'güvenlik': ['security', 'police', 'safety', 'protection', 'law'],
            'siyaset': ['politics', 'government', 'parliament', 'election', 'democracy']
        }
        
        # Kategori terimlerini ekle
        if category in category_terms:
            terms.extend(category_terms[category])
        
        # Başlık ve metinden anahtar kelimeler çıkar
        keywords = self.extract_keywords_from_text(title + " " + text)
        terms.extend(keywords)
        
        # Genel terimler (fallback)
        if not terms:
            terms = ['news', 'breaking news', 'journalism', 'media', 'information']
        
        return terms[:3]  # İlk 3 terimi kullan
    
    def extract_keywords_from_text(self, text: str) -> list:
        """Metinden anahtar kelimeleri çıkar"""
        # Basit anahtar kelime çıkarma
        keywords = []
        
        # Önemli kelimeler
        important_words = [
            'istanbul', 'ankara', 'izmir', 'antalya', 'bursa',
            'festival', 'konser', 'yarışma', 'turnuva', 'şampiyonluk',
            'teknoloji', 'yapay zeka', 'robot', 'bilgisayar', 'internet',
            'hastane', 'doktor', 'tedavi', 'aşı', 'sağlık',
            'okul', 'üniversite', 'öğrenci', 'eğitim', 'ders'
        ]
        
        text_lower = text.lower()
        for word in important_words:
            if word in text_lower:
                # Türkçe kelimeleri İngilizce karşılıklarına çevir
                english_equivalent = self.translate_to_english(word)
                if english_equivalent:
                    keywords.append(english_equivalent)
        
        return keywords[:2]  # En fazla 2 anahtar kelime
    
    def translate_to_english(self, turkish_word: str) -> str:
        """Basit Türkçe-İngilizce çeviri"""
        translations = {
            'istanbul': 'istanbul city',
            'ankara': 'ankara capital',
            'izmir': 'izmir city',
            'festival': 'festival',
            'konser': 'concert',
            'teknoloji': 'technology',
            'yapay zeka': 'artificial intelligence',
            'hastane': 'hospital',
            'doktor': 'doctor',
            'okul': 'school',
            'üniversite': 'university'
        }
        return translations.get(turkish_word.lower(), turkish_word)
    
    def download_image(self, image_url: str, term: str) -> str:
        """Görseli indir ve kaydet"""
        try:
            # Görsel dosya yolu
            image_path = self.temp_dir / f"bg_image_{term}_{int(time.time())}.jpg"
            
            # Görseli indir
            urllib.request.urlretrieve(image_url, str(image_path))
            
            # Görsel boyutunu kontrol et ve yeniden boyutlandır
            with Image.open(image_path) as img:
                # 1920x1080'e uygun hale getir
                img = img.resize((1920, 1080), Image.Resampling.LANCZOS)
                img.save(image_path, 'JPEG', quality=85)
            
            return str(image_path)
            
        except Exception as e:
            logger.error(f"❌ Görsel indirme hatası: {e}")
            return None
    
    def create_news_scenes(self, news_data: dict, text: str, width: int, height: int, duration: float, background_image: str = None) -> list:
        """Haber videosu sahnelerini oluştur"""
        try:
            clips = []
            
            # 1. Arka Plan - Gerçek görsel veya gradyan
            if background_image and os.path.exists(background_image):
                background = self.create_image_background(background_image, width, height, duration)
                logger.info(f"🖼️ Gerçek görsel arka plan kullanılıyor: {background_image}")
            else:
                background = self.create_animated_background(width, height, duration)
                logger.info("🎨 Animasyonlu gradyan arka plan kullanılıyor")
            clips.append(background)
            
            # 2. Ana Başlık Sahnesi (0-3 saniye)
            title_scene = self.create_title_scene(news_data, width, height, min(3, duration))
            clips.append(title_scene)
            
            # 3. İçerik Sahnesi (3 saniye sonrası)
            if duration > 3:
                content_scene = self.create_content_scene(news_data, text, width, height, duration - 3)
                content_scene = content_scene.set_start(3)
                clips.append(content_scene)
            
            # 4. Alt Bilgi Çubuğu (sürekli)
            info_bar = self.create_info_bar(news_data, width, height, duration)
            clips.append(info_bar)
            
            # 5. Logo ve Branding (sürekli)
            branding = self.create_branding(width, height, duration)
            clips.append(branding)
            
            return clips
            
        except Exception as e:
            logger.error(f"❌ Sahne oluşturma hatası: {e}")
            # Basit fallback
            bg = ColorClip(size=(width, height), color=(20, 30, 60), duration=duration)
            return [bg]
    
    def create_animated_background(self, width: int, height: int, duration: float) -> VideoClip:
        """Animasyonlu arka plan oluştur"""
        try:
            # Gradyan arka plan
            def make_frame(t):
                # Zamanla değişen gradyan
                progress = (t / duration) % 1.0
                
                # Renk geçişi
                r = int(20 + 40 * progress)
                g = int(30 + 50 * progress)  
                b = int(60 + 80 * progress)
                
                # Gradyan oluştur
                frame = np.zeros((height, width, 3), dtype=np.uint8)
                for y in range(height):
                    intensity = y / height
                    frame[y, :] = [
                        max(0, min(255, int(r * (1 - intensity * 0.3)))),
                        max(0, min(255, int(g * (1 - intensity * 0.3)))),
                        max(0, min(255, int(b * (1 - intensity * 0.3))))
                    ]
                
                return frame
            
            return VideoClip(make_frame, duration=duration)
            
        except Exception as e:
            logger.error(f"❌ Arka plan hatası: {e}")
            return ColorClip(size=(width, height), color=(20, 30, 60), duration=duration)
    
    def create_image_background(self, image_path: str, width: int, height: int, duration: float) -> VideoClip:
        """Gerçek görsel arka plan oluştur"""
        try:
            # Görseli yükle
            image_clip = ImageClip(image_path, duration=duration)
            
            # Boyutlandır
            image_clip = image_clip.resize(newsize=(width, height))
            
            # Hafif karartma efekti (metinlerin okunabilir olması için)
            def darken_frame(get_frame, t):
                frame = get_frame(t)
                # %30 karartma
                return (frame * 0.7).astype(np.uint8)
            
            image_clip = image_clip.fl(darken_frame)
            
            # Hafif zoom efekti
            def zoom_effect(t):
                zoom_factor = 1.0 + (t / duration) * 0.1  # %10 zoom
                return zoom_factor
            
            image_clip = image_clip.resize(lambda t: zoom_effect(t))
            
            return image_clip
            
        except Exception as e:
            logger.error(f"❌ Görsel arka plan hatası: {e}")
            # Fallback: Gradyan arka plan
            return ColorClip(size=(width, height), color=(20, 30, 60), duration=duration)
    
    def create_title_scene(self, news_data: dict, width: int, height: int, duration: float) -> VideoClip:
        """Başlık sahnesi oluştur"""
        try:
            title = news_data.get('title', 'Haber')[:100]
            
            # Başlık metni - büyük ve animasyonlu
            title_clip = TextClip(
                title,
                fontsize=72,
                color='white',
                font='Arial-Bold',
                method='caption',
                size=(width - 200, None),
                align='center'
            ).set_position(('center', 'center')).set_duration(duration)
            
            # Fade in animasyonu
            title_clip = title_clip.crossfadein(0.5)
            
            return title_clip
            
        except Exception as e:
            logger.error(f"❌ Başlık sahnesi hatası: {e}")
            return ColorClip(size=(1, 1), color=(0, 0, 0, 0), duration=duration)
    
    def create_content_scene(self, news_data: dict, text: str, width: int, height: int, duration: float) -> VideoClip:
        """İçerik sahnesi oluştur"""
        try:
            # İçerik metnini satırlara böl
            lines = self.split_text_to_lines(text, 60)
            clips = []
            
            # Her satır için ayrı clip
            for i, line in enumerate(lines[:6]):  # Maksimum 6 satır
                if line.strip():
                    line_clip = TextClip(
                        line,
                        fontsize=48,
                        color='white',
                        font='Arial',
                        method='caption',
                        size=(width - 300, None)
                    ).set_position(('center', height * 0.3 + i * 70)).set_duration(duration)
                    
                    # Satır satır fade in
                    line_clip = line_clip.set_start(i * 0.3).crossfadein(0.3)
                    clips.append(line_clip)
            
            return CompositeVideoClip(clips) if clips else ColorClip(size=(1, 1), color=(0, 0, 0, 0), duration=duration)
            
        except Exception as e:
            logger.error(f"❌ İçerik sahnesi hatası: {e}")
            return ColorClip(size=(1, 1), color=(0, 0, 0, 0), duration=duration)
    
    def create_info_bar(self, news_data: dict, width: int, height: int, duration: float) -> VideoClip:
        """Alt bilgi çubuğu oluştur"""
        try:
            # Alt çubuk arka planı
            bar_height = 100
            bar_bg = ColorClip(
                size=(width, bar_height), 
                color=(0, 0, 0, 180),  # Yarı şeffaf siyah
                duration=duration
            ).set_position((0, height - bar_height))
            
            # Kategori
            category = news_data.get('category', 'Genel')
            category_clip = TextClip(
                f"📂 {category}",
                fontsize=32,
                color='#FFD700',
                font='Arial-Bold'
            ).set_position((50, height - 70)).set_duration(duration)
            
            # Tarih
            timestamp = datetime.now().strftime("%d.%m.%Y %H:%M")
            time_clip = TextClip(
                timestamp,
                fontsize=28,
                color='#CCCCCC',
                font='Arial'
            ).set_position((width - 200, height - 70)).set_duration(duration)
            
            return CompositeVideoClip([bar_bg, category_clip, time_clip])
            
        except Exception as e:
            logger.error(f"❌ Bilgi çubuğu hatası: {e}")
            return ColorClip(size=(1, 1), color=(0, 0, 0, 0), duration=duration)
    
    def create_branding(self, width: int, height: int, duration: float) -> VideoClip:
        """Logo ve marka oluştur"""
        try:
            # AA logosu
            logo_clip = TextClip(
                "ANADOLU AJANSI",
                fontsize=36,
                color='#FF4444',
                font='Arial-Bold'
            ).set_position((width - 400, 50)).set_duration(duration)
            
            # CANLI etiketi
            live_clip = TextClip(
                "🔴 CANLI",
                fontsize=28,
                color='#FF0000',
                font='Arial-Bold'
            ).set_position((50, 50)).set_duration(duration)
            
            return CompositeVideoClip([logo_clip, live_clip])
            
        except Exception as e:
            logger.error(f"❌ Branding hatası: {e}")
            return ColorClip(size=(1, 1), color=(0, 0, 0, 0), duration=duration)
    
    def create_fallback_video(self) -> str:
        """Hata durumunda basit video oluştur"""
        try:
            logger.info("🔄 Fallback video oluşturuluyor...")
            
            background = ColorClip(size=(1920, 1080), color=(30, 30, 50), duration=10)
            text_clip = TextClip(
                "Haber Videosu\nYükleniyor...",
                fontsize=64,
                color='white',
                font='Arial-Bold'
            ).set_position('center').set_duration(10)
            
            video = CompositeVideoClip([background, text_clip])
            
            fallback_path = self.temp_dir / f"fallback_{int(time.time())}.mp4"
            video.write_videofile(str(fallback_path), fps=30, verbose=False, logger=None)
            
            video.close()
            return str(fallback_path)
            
        except Exception as e:
            logger.error(f"❌ Fallback video hatası: {e}")
            raise
    
    def split_text_to_lines(self, text: str, max_chars: int) -> list:
        """Metni satırlara böl"""
        words = text.split()
        lines = []
        current_line = ""
        
        for word in words:
            if len(current_line + " " + word) <= max_chars:
                current_line += " " + word if current_line else word
            else:
                if current_line:
                    lines.append(current_line)
                current_line = word
        
        if current_line:
            lines.append(current_line)
        
        return lines
    
    def upload_video_to_cloudinary(self, video_path: str, request_id: str) -> str:
        """Videoyu Cloudinary'ye yükle"""
        try:
            logger.info(f"☁️ Cloudinary'ye video yükleniyor: {video_path}")
            
            result = cloudinary.uploader.upload(
                video_path,
                resource_type="video",
                folder="aa-eksen-videos",
                public_id=f"video_{request_id}",
                overwrite=True,
                quality="auto",
                format="mp4"
            )
            
            video_url = result['secure_url']
            logger.info(f"✅ Video Cloudinary'ye yüklendi: {video_url}")
            return video_url
            
        except Exception as e:
            logger.error(f"❌ Cloudinary yükleme hatası: {e}")
            raise
    
    def upload_video_to_firebase(self, video_path: str, request_id: str) -> str:
        """Firebase Storage devre dışı - sadece Cloudinary kullanıyoruz"""
        logger.warning("🚫 Firebase Storage devre dışı - Sadece Cloudinary kullanılıyor")
        raise Exception("Firebase Storage kullanılmıyor, sadece Cloudinary aktif")
    
    def create_video_record(self, news_data: dict, video_url: str, transcription: str, request_data: dict) -> dict:
        """Video kaydı oluştur"""
        return {
            'title': news_data.get('title', 'Video'),
            'description': news_data.get('content', '')[:200] + "...",
            'newsId': news_data.get('id', ''),
            'videoUrl': video_url,
            'thumbnailUrl': self.generate_thumbnail_url(video_url),
            'duration': 30,  # Sabit 30 saniye
            'fileSize': 0,  # Hesaplanabilir
            'resolution': '1080p',
            'format': 'mp4',
            'bitrate': 2500,
            'category': news_data.get('category', 'Genel'),
            'tags': news_data.get('tags', []),
            'language': 'tr',
            'generationStatus': 'COMPLETED',
            'transcription': transcription,
            'audioFiles': [],
            'imageFiles': [],
            'backgroundMusic': '',
            'backgroundImage': '',
            'viewCount': 0,
            'likeCount': 0,
            'shareCount': 0,
            'downloadCount': 0,
            'isPublic': True,
            'isActive': True,
            'isFeatured': False,
            'createdAt': firestore.SERVER_TIMESTAMP,
            'updatedAt': firestore.SERVER_TIMESTAMP
        }
    
    def generate_thumbnail_url(self, video_url: str) -> str:
        """Thumbnail URL oluştur (placeholder)"""
        return "https://images.unsplash.com/photo-1611162617474-5b21e879e113?w=400&h=225&fit=crop"
    
    def save_video_to_firebase(self, video_record: dict) -> str:
        """Video kaydını Firebase'e kaydet"""
        try:
            doc_ref = self.db.collection('videos').add(video_record)
            video_id = doc_ref[1].id
            logger.info(f"✅ Video kaydı oluşturuldu: {video_id}")
            return video_id
        except Exception as e:
            logger.error(f"❌ Video kaydı hatası: {e}")
            raise
    
    def complete_request(self, request_id: str, video_id: str):
        """İsteği tamamlandı olarak işaretle"""
        try:
            self.db.collection('video_generation_requests').document(request_id).update({
                'status': 'COMPLETED',
                'progress': 100,
                'currentStep': 'Video oluşturma tamamlandı',
                'resultVideoId': video_id,
                'updatedAt': firestore.SERVER_TIMESTAMP
            })
            logger.info(f"✅ İstek tamamlandı: {request_id} -> {video_id}")
        except Exception as e:
            logger.error(f"❌ İstek tamamlama hatası: {e}")
    
    def fail_request(self, request_id: str, error_message: str):
        """İsteği başarısız olarak işaretle"""
        try:
            self.db.collection('video_generation_requests').document(request_id).update({
                'status': 'FAILED',
                'progress': 0,
                'currentStep': 'Hata oluştu',
                'errorMessage': error_message,
                'updatedAt': firestore.SERVER_TIMESTAMP
            })
            logger.error(f"❌ İstek başarısız: {request_id} -> {error_message}")
        except Exception as e:
            logger.error(f"❌ İstek başarısızlık güncelleme hatası: {e}")
    
    def cleanup_temp_files(self, video_path: str):
        """Geçici dosyaları temizle"""
        try:
            if os.path.exists(video_path):
                os.remove(video_path)
            logger.info("🧹 Geçici dosyalar temizlendi")
        except Exception as e:
            logger.warning(f"⚠️ Dosya temizleme hatası: {e}")

def main():
    """Ana fonksiyon"""
    logger.info("🚀 AA Eksen Video Backend başlatılıyor...")
    
    # .env dosyası kontrolü
    if not os.path.exists('.env'):
        logger.error("❌ .env dosyası bulunamadı!")
        logger.info("💡 .env dosyası oluşturun ve gerekli ayarları yapın")
        logger.info("📄 Örnek .env içeriği:")
        logger.info("FIREBASE_CREDENTIALS=firebase-credentials.json")
        logger.info("CLOUDINARY_CLOUD_NAME=your-cloud-name")
        logger.info("CLOUDINARY_API_KEY=your-api-key")
        logger.info("CLOUDINARY_API_SECRET=your-api-secret")
        logger.info("MAX_VIDEO_DURATION=60")
        logger.info("VIDEO_QUALITY=high")
        sys.exit(1)
    
    # Backend'i başlat
    backend = VideoGenerationBackend()
    backend.start_listening()

if __name__ == "__main__":
    main()
