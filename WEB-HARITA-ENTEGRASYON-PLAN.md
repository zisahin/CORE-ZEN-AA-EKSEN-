# 🗺️ Web Harita Entegrasyonu - Adım Adım

## 🎯 Hedef
Mobil'deki Mapbox haber haritası sistemini **birebir** web'e aktarmak.

---

## 📋 ADIM 1: Şehir Data Modeli (5 dakika)

**DOSYA:** `web/src/types/city.ts` (yeni)

```typescript
export enum IntensityLevel {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  VERY_HIGH = 'VERY_HIGH'
}

export interface City {
  id: string
  name: string
  coordinates: [number, number] // [lat, lng]
  totalNewsCount: number
  intensityLevel: IntensityLevel
  newsByCategory?: {
    [key: string]: number
  }
}

export interface NewsCategory {
  id: string
  name: string
  icon: string
  color: string
  count: number
}
```

---

## 📋 ADIM 2: Mock Şehir Verisi (5 dakika)

**DOSYA:** `web/src/data/mockCities.ts` (yeni)

```typescript
import { City, IntensityLevel } from '@/types/city'

export const mockCities: City[] = [
  {
    id: 'istanbul',
    name: 'İstanbul',
    coordinates: [41.0082, 28.9784],
    totalNewsCount: 156,
    intensityLevel: IntensityLevel.VERY_HIGH,
    newsByCategory: {
      guncel: 45,
      ekonomi: 38,
      spor: 32,
      teknoloji: 25,
      dunya: 16
    }
  },
  {
    id: 'ankara',
    name: 'Ankara',
    coordinates: [39.9334, 32.8597],
    totalNewsCount: 89,
    intensityLevel: IntensityLevel.HIGH,
    newsByCategory: {
      guncel: 28,
      ekonomi: 22,
      spor: 18,
      teknoloji: 12,
      dunya: 9
    }
  },
  {
    id: 'izmir',
    name: 'İzmir',
    coordinates: [38.4237, 27.1428],
    totalNewsCount: 67,
    intensityLevel: IntensityLevel.HIGH,
    newsByCategory: {
      guncel: 22,
      ekonomi: 18,
      spor: 15,
      teknoloji: 8,
      dunya: 4
    }
  },
  {
    id: 'antalya',
    name: 'Antalya',
    coordinates: [36.8969, 30.7133],
    totalNewsCount: 34,
    intensityLevel: IntensityLevel.MEDIUM,
    newsByCategory: {
      guncel: 12,
      ekonomi: 8,
      spor: 7,
      teknoloji: 4,
      dunya: 3
    }
  },
  {
    id: 'bursa',
    name: 'Bursa',
    coordinates: [40.1826, 29.0665],
    totalNewsCount: 28,
    intensityLevel: IntensityLevel.MEDIUM,
    newsByCategory: {
      guncel: 10,
      ekonomi: 7,
      spor: 6,
      teknoloji: 3,
      dunya: 2
    }
  }
]

export const mockCategories = [
  { id: 'guncel', name: 'Güncel', icon: '📰', color: '#3B82F6', count: 0 },
  { id: 'ekonomi', name: 'Ekonomi', icon: '💰', color: '#10B981', count: 0 },
  { id: 'spor', name: 'Spor', icon: '⚽', color: '#F59E0B', count: 0 },
  { id: 'teknoloji', name: 'Teknoloji', icon: '💻', color: '#8B5CF6', count: 0 },
  { id: 'dunya', name: 'Dünya', icon: '🌍', color: '#EF4444', count: 0 }
]
```

---

## 📋 ADIM 3: Intensity Calculator (3 dakika)

**DOSYA:** `web/src/utils/intensityCalculator.ts` (yeni)

```typescript
import { IntensityLevel } from '@/types/city'

export function calculateIntensityLevel(newsCount: number): IntensityLevel {
  if (newsCount === 0) return IntensityLevel.LOW
  if (newsCount <= 25) return IntensityLevel.LOW
  if (newsCount <= 60) return IntensityLevel.MEDIUM
  if (newsCount <= 100) return IntensityLevel.HIGH
  return IntensityLevel.VERY_HIGH
}

export function getColorForIntensity(level: IntensityLevel): string {
  const colors = {
    [IntensityLevel.LOW]: '#4FC3F7',      // Açık mavi
    [IntensityLevel.MEDIUM]: '#29B6F6',   // Mavi
    [IntensityLevel.HIGH]: '#FFB74D',     // Turuncu
    [IntensityLevel.VERY_HIGH]: '#E53935' // Kırmızı
  }
  return colors[level]
}
```

---

## 📋 ADIM 4: TurkeyNewsMap Güncelleme (10 dakika)

**DOSYA:** `web/src/components/TurkeyNewsMap.tsx`

```typescript
'use client'
import { useState, useRef, useEffect } from 'react'
import dynamic from 'next/dynamic'
import { mockCities, mockCategories } from '@/data/mockCities'
import { City, NewsCategory } from '@/types/city'
import { getColorForIntensity } from '@/utils/intensityCalculator'
import CityModal from './CityModal'
import IntensityLegend from './IntensityLegend'
import 'mapbox-gl/dist/mapbox-gl.css'

const ReactMapGL = dynamic(() => import('react-map-gl'), {
  ssr: false
})

export default function TurkeyNewsMap() {
  const [viewState, setViewState] = useState({
    latitude: 39.0,
    longitude: 35.3,
    zoom: 5.5
  })
  
  const [selectedCity, setSelectedCity] = useState<City | null>(null)
  const mapRef = useRef(null)

  // Map yüklendiğinde şehir renklendirmesini uygula
  useEffect(() => {
    if (mapRef.current) {
      const map = mapRef.current.getMap()
      
      map.on('load', () => {
        // Şehir layer'ını kontrol et
        const layers = map.getStyle().layers
        console.log('Mevcut layerlar:', layers?.map(l => l.id))
        
        // Şehir renklendirmesi için expression oluştur
        // (Mobil'deki gibi)
        // TODO: Mapbox style'da şehir layer'ı varsa renklendir
      })
      
      // Map tıklama event'i
      map.on('click', (e) => {
        // Tıklanan yere en yakın şehri bul
        const nearestCity = findNearestCity(e.lngLat, mockCities)
        if (nearestCity) {
          setSelectedCity(nearestCity)
        }
      })
    }
  }, [])

  return (
    <div className="relative w-full h-screen">
      <ReactMapGL
        ref={mapRef}
        {...viewState}
        onMove={evt => setViewState(evt.viewState)}
        mapboxAccessToken="pk.eyJ1IjoiZXJrYW50ciIsImEiOiJjbWc5bGt0MDMwNjEyMmtzOG45OGd2aHR3In0.tnSZJxECyU7ItBYCvYQh2g"
        mapStyle="mapbox://styles/erkantr/cmg9lrv6500a301sa6vaxewfl"
        style={{ width: '100%', height: '100%' }}
      />
      
      {/* Intensity Legend - Sağ üstte */}
      <IntensityLegend />
      
      {/* City Modal */}
      {selectedCity && (
        <CityModal
          city={selectedCity}
          categories={mockCategories}
          onClose={() => setSelectedCity(null)}
        />
      )}
    </div>
  )
}

// Tıklanan noktaya en yakın şehri bul
function findNearestCity(clickPoint: {lng: number, lat: number}, cities: City[]): City | null {
  if (cities.length === 0) return null
  
  let nearestCity = cities[0]
  let minDistance = calculateDistance(
    clickPoint.lat,
    clickPoint.lng,
    cities[0].coordinates[0],
    cities[0].coordinates[1]
  )
  
  for (let i = 1; i < cities.length; i++) {
    const distance = calculateDistance(
      clickPoint.lat,
      clickPoint.lng,
      cities[i].coordinates[0],
      cities[i].coordinates[1]
    )
    
    if (distance < minDistance) {
      minDistance = distance
      nearestCity = cities[i]
    }
  }
  
  // Eğer 100km'den uzaktaysa null döndür
  return minDistance < 100 ? nearestCity : null
}

// Haversine formülü ile mesafe hesapla
function calculateDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
  const R = 6371 // Earth radius in km
  const dLat = toRad(lat2 - lat1)
  const dLon = toRad(lon2 - lon1)
  
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
    Math.sin(dLon / 2) * Math.sin(dLon / 2)
  
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return R * c
}

function toRad(degrees: number): number {
  return degrees * (Math.PI / 180)
}
```

---

## 📋 ADIM 5: IntensityLegend Bileşeni (5 dakika)

**DOSYA:** `web/src/components/IntensityLegend.tsx` (yeni)

```typescript
export default function IntensityLegend() {
  return (
    <div className="absolute top-4 right-4 bg-white/95 backdrop-blur rounded-xl shadow-lg p-4 z-10">
      <h3 className="text-sm font-bold text-gray-800 mb-3">
        Haber Yoğunluğu
      </h3>
      
      {/* Renk çubuğu */}
      <div className="flex h-3 rounded-full overflow-hidden mb-2">
        <div className="flex-1" style={{ backgroundColor: '#4FC3F7' }}></div>
        <div className="flex-1" style={{ backgroundColor: '#29B6F6' }}></div>
        <div className="flex-1" style={{ backgroundColor: '#FFB74D' }}></div>
        <div className="flex-1" style={{ backgroundColor: '#E53935' }}></div>
      </div>
      
      {/* Etiketler */}
      <div className="flex justify-between text-xs text-gray-600 mb-3">
        <span>Az</span>
        <span>Çok</span>
      </div>
      
      {/* Detaylı açıklama */}
      <div className="text-xs text-gray-500 space-y-1 pt-3 border-t">
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-full" style={{ backgroundColor: '#4FC3F7' }}></div>
          <span>0-25 haber</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-full" style={{ backgroundColor: '#29B6F6' }}></div>
          <span>26-60 haber</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-full" style={{ backgroundColor: '#FFB74D' }}></div>
          <span>61-100 haber</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-3 h-3 rounded-full" style={{ backgroundColor: '#E53935' }}></div>
          <span>100+ haber</span>
        </div>
      </div>
    </div>
  )
}
```

---

## 📋 ADIM 6: CityModal Bileşeni (10 dakika)

**DOSYA:** `web/src/components/CityModal.tsx` (yeni)

```typescript
import { City, NewsCategory } from '@/types/city'
import { getColorForIntensity } from '@/utils/intensityCalculator'

interface CityModalProps {
  city: City
  categories: NewsCategory[]
  onClose: () => void
}

export default function CityModal({ city, categories, onClose }: CityModalProps) {
  // Şehrin kategorilerini güncelle
  const cityCategories = categories.map(cat => ({
    ...cat,
    count: city.newsByCategory?.[cat.id] || 0
  })).filter(cat => cat.count > 0)
  
  const cityColor = getColorForIntensity(city.intensityLevel)
  
  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center">
      {/* Backdrop */}
      <div 
        className="absolute inset-0 bg-black/50 backdrop-blur-sm"
        onClick={onClose}
      />
      
      {/* Modal */}
      <div className="relative bg-white rounded-t-3xl sm:rounded-2xl shadow-2xl w-full sm:max-w-2xl max-h-[80vh] overflow-hidden">
        {/* Header */}
        <div 
          className="p-6 text-white relative"
          style={{ 
            background: `linear-gradient(135deg, ${cityColor} 0%, ${cityColor}dd 100%)`
          }}
        >
          <button
            onClick={onClose}
            className="absolute top-4 right-4 w-10 h-10 rounded-full bg-white/20 hover:bg-white/30 flex items-center justify-center transition-colors"
          >
            ✕
          </button>
          
          <div>
            <h2 className="text-2xl font-bold mb-1">📍 {city.name}</h2>
            <p className="text-sm opacity-90">
              Toplam {city.totalNewsCount} haber
            </p>
          </div>
        </div>
        
        {/* Kategoriler */}
        <div className="p-6 overflow-y-auto max-h-[50vh]">
          <h3 className="text-lg font-bold text-gray-800 mb-4">
            Haber Kategorileri
          </h3>
          
          <div className="space-y-3">
            {cityCategories.map((category) => (
              <div
                key={category.id}
                className="group bg-white border-2 border-gray-100 rounded-xl p-4 hover:border-blue-500 hover:shadow-lg transition-all cursor-pointer"
                onClick={() => {
                  // Kategori sayfasına yönlendir
                  window.location.href = `/news-map/${city.id}/${category.id}`
                }}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div 
                      className="w-12 h-12 rounded-full flex items-center justify-center text-2xl group-hover:scale-110 transition-transform"
                      style={{ 
                        backgroundColor: `${category.color}20`,
                        color: category.color
                      }}
                    >
                      {category.icon}
                    </div>
                    <div>
                      <h4 className="font-bold text-lg">{category.name}</h4>
                      <p className="text-sm text-gray-500">{category.count} haber</p>
                    </div>
                  </div>
                  <div className="text-gray-400 group-hover:text-blue-500 group-hover:translate-x-1 transition-all">
                    →
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
```

---

## ✅ ŞUAN YAPILACAKLAR:

**Sırayla oluştur:**
1. `web/src/types/city.ts`
2. `web/src/data/mockCities.ts`
3. `web/src/utils/intensityCalculator.ts`
4. `web/src/components/IntensityLegend.tsx`
5. `web/src/components/CityModal.tsx`
6. `web/src/components/TurkeyNewsMap.tsx` - güncelle

**Sonra test et:**
- Harita açılacak ✅
- Legend görünecek ✅
- Şehir konumuna tıklayınca modal açılacak ✅

---

**Hazır mısın? İlk dosyayı oluştur ve bana "1. dosya tamam" de!** 🚀
