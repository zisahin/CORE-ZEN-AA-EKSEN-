# 🗺️ Türkiye Haber Haritası - Web Versiyonu

## 📱 Mobil'den Öğrendiklerimiz

### Mevcut Mobil Sistem:
- ✅ Mapbox interaktif harita
- ✅ 4 seviyeli renk sistemi (Yeşil → Kırmızı)
- ✅ Şehir bazlı haber kategorileme
- ✅ Bottom sheet ile kategori gösterimi
- ✅ Her şehirde `totalNewsCount` hesabı
- ✅ Gerçek zamanlı renklendirme

### Renk Sistemi (Mobil'den):
```kotlin
LOW       → #4FC3F7 (Açık Mavi)    - Az haber
MEDIUM    → #29B6F6 (Mavi)         - Orta haber  
HIGH      → #FFB74D (Turuncu)      - Çok haber
VERY_HIGH → #E53935 (Kırmızı)     - Çok fazla haber
```

---

## 🎯 Web Versiyonu Hedefleri

### Temel Özellikler:
1. ✅ Türkiye haritası (SVG - interaktif)
2. ✅ Her şehir tıklanabilir buton
3. ✅ Haber yoğunluğuna göre renklendirme
4. ✅ Şehir tıklayınca modal/drawer açılır
5. ✅ Kategoriler gösterilir
6. ✅ Kategori seçince o şehrin haberleri listelenir

### Ekstra Özellikler (Web için):
- 🎨 Hover efektleri
- 📊 İstatistik gösterimi
- 🔍 Arama (şehir adı)
- 📱 Responsive design
- ⚡ Smooth animasyonlar

---

## 🏗️ Teknik Mimari

### Seçenek 1: SVG Harita (ÖNERİLEN - Basit)
```
✅ Performanslı
✅ Kolay customize
✅ Dosya boyutu küçük
✅ React ile kolay entegrasyon
⚠️ Kendin çizmen veya hazır SVG bulman gerek
```

### Seçenek 2: Leaflet.js (Mapbox alternatifi)
```
✅ Açık kaynak
✅ Interaktif
⚠️ Ağır olabilir
⚠️ Extra kütüphane
```

### Seçenek 3: Canvas (Custom)
```
✅ Tam kontrol
⚠️ Karmaşık
⚠️ Accessibility zor
```

**KARAR: SVG Harita ile başlayalım!**

---

## 📊 Data Modeli

### 1. Şehir (City)
```typescript
interface City {
  id: string;              // "ankara", "istanbul"
  name: string;            // "Ankara", "İstanbul"
  coordinates: [number, number]; // [lat, lng] veya [x, y]
  totalNewsCount: number;  // Toplam haber sayısı
  newsByCategory: {
    guncel: number;
    ekonomi: number;
    spor: number;
    teknoloji: number;
    // ...
  };
  intensityLevel: IntensityLevel;
}
```

### 2. Intensity Level
```typescript
enum IntensityLevel {
  LOW = 'LOW',           // 0-10 haber
  MEDIUM = 'MEDIUM',     // 11-50 haber
  HIGH = 'HIGH',         // 51-100 haber
  VERY_HIGH = 'VERY_HIGH' // 100+ haber
}
```

### 3. News Category
```typescript
interface NewsCategory {
  id: string;        // "ekonomi"
  name: string;      // "Ekonomi"
  icon: string;      // "💰" veya icon component
  color: string;     // "#3B82F6"
  count: number;     // O kategoride kaç haber var
}
```

---

## 🎨 UI/UX Tasarımı

### Ana Sayfa Layoutu:
```
┌─────────────────────────────────────────┐
│  TopNav                                 │
├─────────────────────────────────────────┤
│                                         │
│  [Haber Yoğunluk]  TÜRKİYE HARİTASI    │
│  [Göstergesi]                           │
│  Yeşil → Kırmızı   [İnteraktif SVG]    │
│                                         │
│                    (Şehirler tıklanabilir)
│                                         │
└─────────────────────────────────────────┘

// Şehir tıklandığında:
┌─────────────────────────────────────────┐
│  Harita (blur/darken)                   │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │ 📍 ANKARA HABERLERİ             │   │
│  │ ─────────────────────────────── │   │
│  │                                 │   │
│  │ 📰 Güncel (45)          →      │   │
│  │ 💰 Ekonomi (23)         →      │   │
│  │ ⚽ Spor (18)            →      │   │
│  │ 💻 Teknoloji (12)       →      │   │
│  │                                 │   │
│  │ [Kapat X]                       │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

---

## 📁 Dosya Yapısı

```
web/src/
├── components/
│   ├── TurkeyNewsMap/
│   │   ├── TurkeyMap.tsx           # Ana harita bileşeni
│   │   ├── CityShape.tsx           # Tek şehir SVG
│   │   ├── IntensityLegend.tsx     # Renk göstergesi
│   │   ├── CityModal.tsx           # Şehir detay modal
│   │   ├── CategoryList.tsx        # Kategori listesi
│   │   └── types.ts                # TypeScript types
│   └── ...
├── services/
│   ├── cityNewsService.ts          # Şehir haberleri API
│   └── ...
├── data/
│   ├── turkeyMap.svg               # Türkiye haritası SVG
│   └── cities.ts                   # Şehir koordinatları
├── app/
│   └── haber-haritasi/
│       └── page.tsx                # Harita sayfası
└── ...
```

---

## 🚀 İMPLEMENTASYON ADIMLARI

### FAZA 1: Türkiye SVG Haritası (1-2 gün)

#### Adım 1.1: SVG Haritası Bul/Oluştur

**Seçenek A: Hazır SVG (Hızlı)**
- https://commons.wikimedia.org/wiki/File:Turkey_location_map.svg
- https://github.com/datasets/geo-boundaries-world-110m
- Her şehir ayrı `<path>` elementi olacak

**Seçenek B: Kendin Çiz (Zaman alır)**
- Adobe Illustrator / Figma
- Her şehri ayrı layer

**Seçenek C: Ben Hazır Veriyim (En Hızlı)** ✅
- Basit Türkiye haritası SVG'si
- 81 il için path'ler
- ID'ler şehir adlarıyla

#### Adım 1.2: SVG'yi React Component Yap

```typescript
// components/TurkeyNewsMap/TurkeyMap.tsx
'use client'
import { useState } from 'react'
import { cities } from '@/data/cities'
import CityShape from './CityShape'
import IntensityLegend from './IntensityLegend'
import CityModal from './CityModal'

export default function TurkeyMap() {
  const [selectedCity, setSelectedCity] = useState<City | null>(null)
  const [hoveredCity, setHoveredCity] = useState<City | null>(null)
  
  return (
    <div className="relative w-full h-screen">
      <svg 
        viewBox="0 0 1000 600" 
        className="w-full h-full"
      >
        {cities.map((city) => (
          <CityShape
            key={city.id}
            city={city}
            isSelected={selectedCity?.id === city.id}
            isHovered={hoveredCity?.id === city.id}
            onClick={() => setSelectedCity(city)}
            onMouseEnter={() => setHoveredCity(city)}
            onMouseLeave={() => setHoveredCity(null)}
          />
        ))}
      </svg>
      
      <IntensityLegend />
      
      {selectedCity && (
        <CityModal
          city={selectedCity}
          onClose={() => setSelectedCity(null)}
        />
      )}
    </div>
  )
}
```

#### Adım 1.3: Şehir Path Component

```typescript
// components/TurkeyNewsMap/CityShape.tsx
import { City, IntensityLevel } from './types'

interface CityShapeProps {
  city: City
  isSelected: boolean
  isHovered: boolean
  onClick: () => void
  onMouseEnter: () => void
  onMouseLeave: () => void
}

const intensityColors = {
  [IntensityLevel.LOW]: '#4FC3F7',
  [IntensityLevel.MEDIUM]: '#29B6F6',
  [IntensityLevel.HIGH]: '#FFB74D',
  [IntensityLevel.VERY_HIGH]: '#E53935',
}

export default function CityShape({
  city,
  isSelected,
  isHovered,
  onClick,
  onMouseEnter,
  onMouseLeave
}: CityShapeProps) {
  const fillColor = intensityColors[city.intensityLevel]
  const opacity = isHovered ? 0.9 : isSelected ? 1 : 0.7
  
  return (
    <g>
      <path
        d={city.pathData} // SVG path string
        fill={fillColor}
        stroke="#000000"
        strokeWidth={isSelected ? 3 : 1}
        opacity={opacity}
        className="cursor-pointer transition-all duration-200 hover:drop-shadow-lg"
        onClick={onClick}
        onMouseEnter={onMouseEnter}
        onMouseLeave={onMouseLeave}
      />
      
      {/* Şehir ismi (hover'da göster) */}
      {isHovered && (
        <text
          x={city.labelPosition[0]}
          y={city.labelPosition[1]}
          textAnchor="middle"
          className="text-sm font-bold fill-white pointer-events-none"
          style={{ textShadow: '0 0 3px black' }}
        >
          {city.name}
        </text>
      )}
    </g>
  )
}
```

---

### FAZA 2: Şehir Data Sistemi (1 gün)

#### Adım 2.1: Şehirler Data Dosyası

```typescript
// data/cities.ts
import { City, IntensityLevel } from '@/components/TurkeyNewsMap/types'

export const cities: City[] = [
  {
    id: 'istanbul',
    name: 'İstanbul',
    coordinates: [41.0082, 28.9784],
    pathData: 'M 100,100 L 150,120 L 140,150 Z', // Gerçek SVG path
    labelPosition: [125, 125],
    totalNewsCount: 0, // Backend'den güncellenecek
    newsByCategory: {
      guncel: 0,
      ekonomi: 0,
      spor: 0,
      teknoloji: 0,
      dunya: 0,
      saglik: 0
    },
    intensityLevel: IntensityLevel.LOW
  },
  {
    id: 'ankara',
    name: 'Ankara',
    coordinates: [39.9334, 32.8597],
    pathData: 'M 200,200 L 250,220 L 240,250 Z',
    labelPosition: [225, 225],
    totalNewsCount: 0,
    newsByCategory: {
      guncel: 0,
      ekonomi: 0,
      spor: 0,
      teknoloji: 0,
      dunya: 0,
      saglik: 0
    },
    intensityLevel: IntensityLevel.LOW
  },
  // ... 79 şehir daha
]
```

#### Adım 2.2: Intensity Calculator

```typescript
// utils/intensityCalculator.ts
import { IntensityLevel } from '@/components/TurkeyNewsMap/types'

export function calculateIntensityLevel(newsCount: number): IntensityLevel {
  if (newsCount === 0) return IntensityLevel.LOW
  if (newsCount <= 10) return IntensityLevel.LOW
  if (newsCount <= 50) return IntensityLevel.MEDIUM
  if (newsCount <= 100) return IntensityLevel.HIGH
  return IntensityLevel.VERY_HIGH
}

export function getColorForIntensity(level: IntensityLevel): string {
  const colors = {
    [IntensityLevel.LOW]: '#4FC3F7',
    [IntensityLevel.MEDIUM]: '#29B6F6',
    [IntensityLevel.HIGH]: '#FFB74D',
    [IntensityLevel.VERY_HIGH]: '#E53935',
  }
  return colors[level]
}
```

---

### FAZA 3: Backend Entegrasyonu (2 gün)

#### Adım 3.1: AI Service'e Şehir Endpoint Ekle

```javascript
// ai-service-2/src/routes/cityNews.js
import express from 'express';

const router = express.Router();

// Şehir bazlı haber istatistikleri
router.get('/stats', async (req, res) => {
  try {
    const cities = await getAllCityNewsStats();
    
    res.json({
      success: true,
      cities: cities,
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    console.error('❌ City stats error:', error);
    res.status(500).json({ error: 'Şehir istatistikleri alınamadı' });
  }
});

// Belirli şehrin haberlerini getir
router.get('/:cityId/news', async (req, res) => {
  try {
    const { cityId } = req.params;
    const { category } = req.query;
    
    const news = await getCityNews(cityId, category);
    
    res.json({
      success: true,
      city: cityId,
      category: category || 'all',
      news: news,
      total: news.length,
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    console.error('❌ City news error:', error);
    res.status(500).json({ error: 'Şehir haberleri alınamadı' });
  }
});

// Şehir kategorisi istatistikleri
router.get('/:cityId/categories', async (req, res) => {
  try {
    const { cityId } = req.params;
    const categories = await getCityCategories(cityId);
    
    res.json({
      success: true,
      city: cityId,
      categories: categories,
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    console.error('❌ City categories error:', error);
    res.status(500).json({ error: 'Kategori istatistikleri alınamadı' });
  }
});

// Haber içeriğinden şehir çıkarma (NLP ile)
async function extractCityFromNews(newsContent) {
  const cities = [
    'istanbul', 'ankara', 'izmir', 'bursa', 'antalya',
    'adana', 'konya', 'gaziantep', 'şanlıurfa', 'kocaeli',
    // ... 81 il
  ];
  
  const contentLower = newsContent.toLowerCase();
  
  for (const city of cities) {
    if (contentLower.includes(city)) {
      return city;
    }
  }
  
  return null; // Şehir bulunamadı
}

async function getAllCityNewsStats() {
  // RSS'den çekilen tüm haberleri analiz et
  // Her haberin hangi şehirle ilgili olduğunu bul
  // Şehir bazlı sayıları döndür
  
  // Örnek:
  return [
    {
      cityId: 'istanbul',
      totalNews: 125,
      byCategory: {
        guncel: 45,
        ekonomi: 30,
        spor: 25,
        teknoloji: 15,
        dunya: 5,
        saglik: 5
      }
    },
    // ...
  ];
}

async function getCityNews(cityId, category = null) {
  // O şehirle ilgili haberleri çek
  // Kategori filtresi varsa uygula
  return [];
}

async function getCityCategories(cityId) {
  // O şehrin kategori istatistiklerini döndür
  return [
    { id: 'guncel', name: 'Güncel', count: 45, icon: '📰', color: '#3B82F6' },
    { id: 'ekonomi', name: 'Ekonomi', count: 30, icon: '💰', color: '#10B981' },
    { id: 'spor', name: 'Spor', count: 25, icon: '⚽', color: '#F59E0B' },
    // ...
  ];
}

export default router;
```

#### Adım 3.2: Web Service

```typescript
// web/src/services/cityNewsService.ts
const API_URL = process.env.NEXT_PUBLIC_AI_SERVICE_URL || 'http://localhost:3001';

export interface CityStats {
  cityId: string;
  totalNews: number;
  byCategory: Record<string, number>;
}

export interface CityNews {
  id: string;
  title: string;
  description: string;
  category: string;
  city: string;
  publishedAt: Date;
}

export async function getCityStats(): Promise<CityStats[]> {
  const response = await fetch(`${API_URL}/api/city-news/stats`);
  const data = await response.json();
  return data.cities;
}

export async function getCityNews(cityId: string, category?: string): Promise<CityNews[]> {
  const url = category
    ? `${API_URL}/api/city-news/${cityId}/news?category=${category}`
    : `${API_URL}/api/city-news/${cityId}/news`;
    
  const response = await fetch(url);
  const data = await response.json();
  return data.news;
}

export async function getCityCategories(cityId: string) {
  const response = await fetch(`${API_URL}/api/city-news/${cityId}/categories`);
  const data = await response.json();
  return data.categories;
}
```

---

### FAZA 4: Modal & Kategoriler (1 gün)

#### CityModal Component

```typescript
// components/TurkeyNewsMap/CityModal.tsx
'use client'
import { useState, useEffect } from 'react'
import { City } from './types'
import { getCityCategories, getCityNews } from '@/services/cityNewsService'

interface CityModalProps {
  city: City
  onClose: () => void
}

export default function CityModal({ city, onClose }: CityModalProps) {
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  
  useEffect(() => {
    loadCategories()
  }, [city.id])
  
  const loadCategories = async () => {
    setLoading(true)
    try {
      const data = await getCityCategories(city.id)
      setCategories(data)
    } catch (error) {
      console.error('Kategoriler yüklenemedi:', error)
    } finally {
      setLoading(false)
    }
  }
  
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Backdrop */}
      <div 
        className="absolute inset-0 bg-black/50 backdrop-blur-sm"
        onClick={onClose}
      />
      
      {/* Modal */}
      <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[80vh] overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-brand-blue to-purple-600 p-6 text-white">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-2xl font-bold">📍 {city.name}</h2>
              <p className="text-sm opacity-90 mt-1">
                Toplam {city.totalNewsCount} haber
              </p>
            </div>
            <button
              onClick={onClose}
              className="w-10 h-10 rounded-full bg-white/20 hover:bg-white/30 flex items-center justify-center transition-colors"
            >
              ✕
            </button>
          </div>
        </div>
        
        {/* Categories */}
        <div className="p-6 overflow-y-auto max-h-[60vh]">
          {loading ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-brand-blue"></div>
            </div>
          ) : (
            <div className="space-y-3">
              {categories.map((category) => (
                <div
                  key={category.id}
                  className="group bg-white border-2 border-gray-100 rounded-xl p-4 hover:border-brand-blue hover:shadow-lg transition-all cursor-pointer"
                  onClick={() => {
                    // Kategori sayfasına yönlendir
                    window.location.href = `/haber-haritasi/${city.id}/${category.id}`
                  }}
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="w-12 h-12 rounded-full flex items-center justify-center text-2xl bg-gray-50 group-hover:scale-110 transition-transform">
                        {category.icon}
                      </div>
                      <div>
                        <h3 className="font-bold text-lg">{category.name}</h3>
                        <p className="text-sm text-gray-500">{category.count} haber</p>
                      </div>
                    </div>
                    <div className="text-gray-400 group-hover:text-brand-blue group-hover:translate-x-1 transition-all">
                      →
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
```

---

## 📊 Şehir-Haber Eşleştirme Algoritması

### Yöntem 1: Basit String Matching (Hızlı Başlangıç)

```javascript
// ai-service-2/src/utils/cityMatcher.js

const TURKISH_CITIES = [
  'adana', 'adıyaman', 'afyonkarahisar', 'ağrı', 'aksaray', 'amasya',
  'ankara', 'antalya', 'ardahan', 'artvin', 'aydın', 'balıkesir',
  // ... 81 il
];

export function extractCitiesFromText(text) {
  const foundCities = [];
  const textLower = text.toLowerCase();
  
  for (const city of TURKISH_CITIES) {
    if (textLower.includes(city)) {
      foundCities.push(city);
    }
  }
  
  return foundCities;
}
```

### Yöntem 2: NLP ile (Gelişmiş - İleriye Dönük)

```javascript
// OpenAI ile şehir çıkarma
async function extractCitiesWithAI(newsText) {
  const response = await openai.chat.completions.create({
    model: 'gpt-4o-mini',
    messages: [
      {
        role: 'system',
        content: 'Verilen haber metninden bahsedilen Türkiye şehirlerini çıkar. Sadece şehir isimlerini virgülle ayrılmış liste olarak döndür.'
      },
      {
        role: 'user',
        content: newsText
      }
    ],
    temperature: 0.3
  });
  
  const citiesText = response.choices[0].message.content;
  return citiesText.split(',').map(c => c.trim().toLowerCase());
}
```

---

## 🎨 Intensity Legend Component

```typescript
// components/TurkeyNewsMap/IntensityLegend.tsx
export default function IntensityLegend() {
  return (
    <div className="absolute top-4 right-4 bg-white/95 backdrop-blur rounded-xl shadow-lg p-4">
      <h3 className="text-sm font-bold text-gray-800 mb-3">
        Haber Yoğunluğu
      </h3>
      
      <div className="space-y-2">
        {/* Renk çubuğu */}
        <div className="flex h-3 rounded-full overflow-hidden">
          <div className="flex-1 bg-[#4FC3F7]"></div>
          <div className="flex-1 bg-[#29B6F6]"></div>
          <div className="flex-1 bg-[#FFB74D]"></div>
          <div className="flex-1 bg-[#E53935]"></div>
        </div>
        
        {/* Etiketler */}
        <div className="flex justify-between text-xs text-gray-600">
          <span>Az</span>
          <span>Çok</span>
        </div>
        
        {/* Detaylı açıklama */}
        <div className="text-xs text-gray-500 space-y-1 mt-3 pt-3 border-t">
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-[#4FC3F7]"></div>
            <span>0-10 haber</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-[#29B6F6]"></div>
            <span>11-50 haber</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-[#FFB74D]"></div>
            <span>51-100 haber</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-[#E53935]"></div>
            <span>100+ haber</span>
          </div>
        </div>
      </div>
    </div>
  )
}
```

---

## 📱 Sayfa Oluşturma

```typescript
// app/haber-haritasi/page.tsx
import TurkeyMap from '@/components/TurkeyNewsMap/TurkeyMap'

export const metadata = {
  title: 'Haber Haritası - AA Eksen',
  description: 'Türkiye\'nin interaktif haber haritası'
}

export default function HaberHaritasiPage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50">
      <TurkeyMap />
    </div>
  )
}
```

---

## ✅ ÖNCELİK SIRASI

### 🚀 HEMEN BAŞLAYALIM (1-2 gün)
1. ✅ Türkiye SVG haritası hazırla/bul
2. ✅ Basit harita component oluştur
3. ✅ cities.ts data dosyası
4. ✅ Renklendirme sistemi
5. ✅ Hover efektleri

### 📊 HAFTA 1 (3-4 gün)
6. ✅ AI service'e city-news endpoint ekle
7. ✅ String matching ile şehir çıkarma
8. ✅ Modal component
9. ✅ Kategori listesi

### 🎯 HAFTA 2 (3-4 gün)
10. ✅ Kategori sayfası (şehir + kategori haberleri)
11. ✅ Gerçek haber verisi entegrasyonu
12. ✅ Loading states
13. ✅ Responsive design

---

## 🎓 İlk Adım: SVG Haritası

Size 2 seçenek sunuyorum:

### Seçenek A: Ben Hazır SVG Veriyim
- Basit Türkiye haritası
- 81 il ayrı path
- Hemen kullanıma hazır

### Seçenek B: Hazır SVG Bulalım
- İnternetten uygun SVG bulup düzenleriz
- Birlikte customize ederiz

**Hangisini istersiniz?**

---

**Başlamaya hazır mısınız? 🗺️🚀**
