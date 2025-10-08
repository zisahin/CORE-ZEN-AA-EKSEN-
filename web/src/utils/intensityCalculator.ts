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

