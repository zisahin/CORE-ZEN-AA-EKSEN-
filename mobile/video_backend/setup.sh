#!/bin/bash

# AA Eksen Video Backend Setup Script

echo "🚀 AA Eksen Video Backend kurulumu başlıyor..."

# Python virtual environment oluştur
echo "📦 Python virtual environment oluşturuluyor..."
python3 -m venv venv
source venv/bin/activate

# Requirements yükle
echo "📚 Dependencies yükleniyor..."
pip install --upgrade pip
pip install -r requirements.txt

# FFmpeg kontrolü (MoviePy için gerekli)
echo "🎬 FFmpeg kontrolü..."
if ! command -v ffmpeg &> /dev/null; then
    echo "⚠️ FFmpeg bulunamadı. Lütfen FFmpeg'i yükleyin:"
    echo "macOS: brew install ffmpeg"
    echo "Ubuntu: sudo apt install ffmpeg"
    echo "Windows: https://ffmpeg.org/download.html"
else
    echo "✅ FFmpeg bulundu"
fi

# Çalışma dizinleri oluştur
echo "📁 Çalışma dizinleri oluşturuluyor..."
mkdir -p video_work/temp
mkdir -p video_work/output

# Environment dosyası oluştur
echo "🔧 Environment dosyası oluşturuluyor..."
cat > .env << EOF
# Firebase Configuration
FIREBASE_CREDENTIALS=firebase-credentials.json

# OpenAI Configuration (Optional)
OPENAI_API_KEY=your-openai-api-key-here

# Video Configuration
MAX_VIDEO_DURATION=60
VIDEO_QUALITY=high
EOF

echo "✅ Kurulum tamamlandı!"
echo ""
echo "📋 Sonraki adımlar:"
echo "1. Firebase credentials dosyanızı 'firebase-credentials.json' olarak kaydedin"
echo "2. .env dosyasındaki API key'leri güncelleyin"
echo "3. Backend'i başlatın: python main.py"
echo ""
echo "🔥 Backend başlatma:"
echo "source venv/bin/activate"
echo "python main.py"
