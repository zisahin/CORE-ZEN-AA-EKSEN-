'use client'
import { useState, useEffect } from 'react'
import dynamic from 'next/dynamic'
import { mockCities, mockCategories } from '@/data/mockCities'
import { City, IntensityLevel } from '@/types/city'
import { getColorForIntensity, calculateIntensityLevel } from '@/utils/intensityCalculator'
import CityModal from './CityModal'
import IntensityLegend from './IntensityLegend'
import aiService from '@/services/aiService'
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
  const [mapInstance, setMapInstance] = useState<any>(null)
  const [cities, setCities] = useState<City[]>(mockCities)
  const [loading, setLoading] = useState(true)
  const [useRealData, setUseRealData] = useState(true)
  
  // Gerçek şehir verilerini yükle
  useEffect(() => {
    if (useRealData) {
      loadCityStats()
    }
  }, [useRealData])
  
  const loadCityStats = async () => {
    try {
      setLoading(true)
      console.log('🗺️ Gerçek şehir verileri yükleniyor...')
      
      const data = await aiService.getCityStats()
      
      if (data.success && data.cities && data.cities.length > 0) {
        // API'den gelen verileri City interface'ine dönüştür
        const realCities: City[] = data.cities.map((city: any) => {
          // Mock cities'den koordinatları al (eğer varsa)
          const mockCity = mockCities.find(m => 
            m.name.toLowerCase() === city.name.toLowerCase()
          )
          
          return {
            id: city.id,
            name: city.name,
            coordinates: mockCity?.coordinates || [39.0, 35.0], // Fallback koordinat
            totalNewsCount: city.totalNewsCount,
            intensityLevel: calculateIntensityLevel(city.totalNewsCount),
            newsByCategory: city.newsByCategory
          }
        })
        
        console.log(`✅ ${realCities.length} şehir yüklendi (gerçek veri)`)
        setCities(realCities)
      } else {
        console.warn('⚠️ API veri döndürmedi, mock veri kullanılıyor')
        setCities(mockCities)
      }
    } catch (error) {
      console.error('❌ Şehir verileri yüklenemedi:', error)
      console.log('🔄 Mock veri kullanılıyor')
      setCities(mockCities)
    } finally {
      setLoading(false)
    }
  }

  // Map yüklendiğinde çağrılır
  const handleMapLoad = (event: any) => {
    const map = event.target
    setMapInstance(map)
    
    console.log('🗺️ Map yüklendi!')
    
    // Style yüklendiğinde şehirleri renklendir
    const handleStyleLoad = () => {
        console.log('🎨 Style yüklendi, şehirler renklendiriliyor...')
        
        const layers = map.getStyle()?.layers || []
        console.log('📋 Mevcut layerlar:', layers.map((l: any) => l.id))
        
        // 1. Manuel Şehir İsimleri Ekle (çünkü style'da text layer yok)
        if (!map.getSource('city-labels-source')) {
          map.addSource('city-labels-source', {
            type: 'geojson',
            data: {
              type: 'FeatureCollection',
              features: cities.map(city => ({
                type: 'Feature',
                properties: {
                  name: city.name
                },
                geometry: {
                  type: 'Point',
                  coordinates: [city.coordinates[1], city.coordinates[0]]  // Ters çevir: [lon, lat]
                }
              }))
            }
          })
          console.log('✅ Şehir isimleri source eklendi')
        }
        
        if (!map.getLayer('city-labels-text')) {
          map.addLayer({
            id: 'city-labels-text',
            type: 'symbol',
            source: 'city-labels-source',
            layout: {
              'text-field': ['get', 'name'],
              'text-font': ['Open Sans Bold', 'Arial Unicode MS Bold'],
              'text-size': [
                'interpolate',
                ['linear'],
                ['zoom'],
                4, 10,   // zoom 4'te 10px
                8, 16    // zoom 8'de 16px
              ],
              'text-anchor': 'center',
              'text-offset': [0, 0.5]
            },
            paint: {
              'text-color': '#6B7280',        // Gri renk
              'text-halo-color': '#FFFFFF',   // Beyaz halo
              'text-halo-width': 2,
              'text-halo-blur': 1
            }
          })
          console.log('✅ Şehir isimleri text layer eklendi!')
        }
      // Şehir layer'ını bul ve renklendir
      layers.forEach((layer: any) => {
        // Fill tipindeki layer'ları kontrol et
        if (layer.type === 'fill' && 
            (layer.id.includes('admin') || 
             layer.id.includes('province') || 
             layer.id.includes('cities') ||
             layer.id.includes('place'))) {
          
          console.log(`🎨 Layer bulundu: ${layer.id}`)
          
          // Şehir bazlı renk expression'ı oluştur
          const colorExpression: any = ['match', ['get', 'name']]
          
          cities.forEach(city => {
            const color = getColorForIntensity(city.intensityLevel)
            colorExpression.push(city.name, color)
          })
          
          // Default renk (eşleşmeyen yerler için)
          colorExpression.push('rgba(200, 200, 200, 0.3)')
          
          // Layer'ın rengini güncelle
          try {
            map.setPaintProperty(layer.id, 'fill-color', colorExpression)
            map.setPaintProperty(layer.id, 'fill-opacity', 0.7)
            console.log(`✅ ${layer.id} renklendi! (${cities.length} şehir)`)
          } catch (error) {
            console.warn(`⚠️ ${layer.id} renklenemedi:`, error)
          }
        }
      })
    }
    
    // Style yüklendiğinde çağır
    if (map.isStyleLoaded()) {
      handleStyleLoad()
    } else {
      map.on('style.load', handleStyleLoad)
    }
  }

  // Map tıklama event'i için useEffect
  useEffect(() => {
    if (mapInstance && cities.length > 0) {
      const handleClick = (e: any) => {
        console.log('🗺️ Haritaya tıklandı:', e.lngLat)
        const nearestCity = findNearestCity(e.lngLat, cities)
        if (nearestCity) {
          console.log('✅ Şehir bulundu:', nearestCity.name)
          setSelectedCity(nearestCity)
        } else {
          console.log('❌ Yakında şehir bulunamadı')
        }
      }
      
      mapInstance.on('click', handleClick)
      
      // Mouse cursor değiştir (harita üzerinde)
      mapInstance.getCanvas().style.cursor = 'pointer'
      
      return () => {
        mapInstance.off('click', handleClick)
      }
    }
  }, [mapInstance, cities])
  
  // Şehir verileri değiştiğinde harita renklerini ve isimlerini güncelle
  useEffect(() => {
    if (mapInstance && cities.length > 0 && !loading) {
      const map = mapInstance
      const layers = map.getStyle()?.layers || []
      
      // 1. Şehir renklerini güncelle
      layers.forEach((layer: any) => {
        if (layer.type === 'fill' && 
            (layer.id.includes('admin') || layer.id.includes('province'))) {
          const colorExpression: any = ['match', ['get', 'name']]
          
          cities.forEach(city => {
            const color = getColorForIntensity(city.intensityLevel)
            colorExpression.push(city.name, color)
          })
          
          colorExpression.push('rgba(200, 200, 200, 0.3)')
          
          try {
            map.setPaintProperty(layer.id, 'fill-color', colorExpression)
            console.log(`🔄 ${layer.id} güncellendi (${cities.length} şehir)`)
          } catch (error) {
            console.warn(`⚠️ ${layer.id} güncellenemedi:`, error)
          }
        }
      })
      
      // 2. Şehir isimlerini güncelle
      const source = map.getSource('city-labels-source')
      if (source && source.type === 'geojson') {
        source.setData({
          type: 'FeatureCollection',
          features: cities.map(city => ({
            type: 'Feature',
            properties: {
              name: city.name
            },
            geometry: {
              type: 'Point',
              coordinates: [city.coordinates[1], city.coordinates[0]]
            }
          }))
        })
        console.log(`📝 Şehir isimleri güncellendi (${cities.length} şehir)`)
      }
    }
  }, [cities, loading, mapInstance])
  
  return (
    <div className="relative w-full h-screen">
      <ReactMapGL
        {...viewState}
        onMove={evt => setViewState(evt.viewState)}
        onLoad={handleMapLoad}
        mapboxAccessToken="pk.eyJ1IjoiZXJrYW50ciIsImEiOiJjbWc4MW5lenMwMmh5MmxzN3ViY2RyamhzIn0.UPXjvTosD9XqhUOZkAv1Ew"
        mapStyle="mapbox://styles/erkantr/cmg9lrv6500a301sa6vaxewfl"
        style={{ width: '100%', height: '100%' }}
      />
      
      {/* Loading Indicator */}
      {loading && (
        <div className="absolute top-4 left-1/2 transform -translate-x-1/2 bg-white/95 backdrop-blur px-6 py-3 rounded-full shadow-xl border border-blue-100 z-50">
          <div className="flex items-center gap-3">
            <div className="animate-spin w-5 h-5 border-3 border-blue-500 border-t-transparent rounded-full"></div>
            <span className="text-sm font-medium text-gray-700">
              Şehir verileri yükleniyor...
            </span>
          </div>
        </div>
      )}
      
      {/* Veri Kaynağı Göstergesi */}
      <div className="absolute top-4 left-4 bg-white/90 backdrop-blur px-4 py-2 rounded-lg shadow-lg border border-gray-200 z-40">
        <div className="flex items-center gap-2 text-sm">
          <span className={`w-2 h-2 rounded-full ${useRealData && !loading ? 'bg-green-500 animate-pulse' : 'bg-gray-400'}`}></span>
          <span className="font-medium text-gray-700">
            {useRealData && !loading ? 'Canlı Veri' : 'Demo Veri'}
          </span>
          {useRealData && !loading && (
            <span className="text-xs text-gray-500">
              ({cities.length} şehir)
            </span>
          )}
        </div>
      </div>
      
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
