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
  },
  {
    id: 'adana',
    name: 'Adana',
    coordinates: [37.0000, 35.3213],
    totalNewsCount: 22,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 8,
      ekonomi: 5,
      spor: 5,
      teknoloji: 2,
      dunya: 2
    }
  },
  {
    id: 'gaziantep',
    name: 'Gaziantep',
    coordinates: [37.0662, 37.3833],
    totalNewsCount: 19,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 7,
      ekonomi: 4,
      spor: 4,
      teknoloji: 2,
      dunya: 2
    }
  },
  {
    id: 'konya',
    name: 'Konya',
    coordinates: [37.8746, 32.4932],
    totalNewsCount: 15,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 6,
      ekonomi: 3,
      spor: 3,
      teknoloji: 2,
      dunya: 1
    }
  },
  {
    id: 'trabzon',
    name: 'Trabzon',
    coordinates: [41.0015, 39.7178],
    totalNewsCount: 18,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 7,
      ekonomi: 4,
      spor: 4,
      teknoloji: 2,
      dunya: 1
    }
  },
  {
    id: 'diyarbakir',
    name: 'Diyarbakır',
    coordinates: [37.9144, 40.2306],
    totalNewsCount: 24,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 9,
      ekonomi: 6,
      spor: 5,
      teknoloji: 2,
      dunya: 2
    }
  },
  {
    id: 'eskisehir',
    name: 'Eskişehir',
    coordinates: [39.7767, 30.5206],
    totalNewsCount: 21,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 8,
      ekonomi: 5,
      spor: 4,
      teknoloji: 3,
      dunya: 1
    }
  },
  {
    id: 'kayseri',
    name: 'Kayseri',
    coordinates: [38.7312, 35.4787],
    totalNewsCount: 26,
    intensityLevel: IntensityLevel.MEDIUM,
    newsByCategory: {
      guncel: 10,
      ekonomi: 7,
      spor: 5,
      teknoloji: 3,
      dunya: 1
    }
  },
  {
    id: 'samsun',
    name: 'Samsun',
    coordinates: [41.2797, 36.3361],
    totalNewsCount: 23,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 9,
      ekonomi: 6,
      spor: 5,
      teknoloji: 2,
      dunya: 1
    }
  },
  {
    id: 'mersin',
    name: 'Mersin',
    coordinates: [36.8121, 34.6415],
    totalNewsCount: 20,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 8,
      ekonomi: 5,
      spor: 4,
      teknoloji: 2,
      dunya: 1
    }
  },
  {
    id: 'van',
    name: 'Van',
    coordinates: [38.4891, 43.3897],
    totalNewsCount: 12,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 5,
      ekonomi: 3,
      spor: 2,
      teknoloji: 1,
      dunya: 1
    }
  },
  {
    id: 'sanliurfa',
    name: 'Şanlıurfa',
    coordinates: [37.1591, 38.7969],
    totalNewsCount: 17,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 7,
      ekonomi: 4,
      spor: 3,
      teknoloji: 2,
      dunya: 1
    }
  },
  {
    id: 'hatay',
    name: 'Hatay',
    coordinates: [36.4018, 36.3498],
    totalNewsCount: 19,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 8,
      ekonomi: 4,
      spor: 4,
      teknoloji: 2,
      dunya: 1
    }
  },
  {
    id: 'denizli',
    name: 'Denizli',
    coordinates: [37.7765, 29.0864],
    totalNewsCount: 14,
    intensityLevel: IntensityLevel.LOW,
    newsByCategory: {
      guncel: 6,
      ekonomi: 3,
      spor: 3,
      teknoloji: 1,
      dunya: 1
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

