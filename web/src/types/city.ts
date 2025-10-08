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

