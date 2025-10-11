'use client'

import { createContext, useContext, useState, useEffect, ReactNode } from 'react'

type Theme = 'gradient' | 'classic'

interface ThemeContextType {
  theme: Theme
  toggleTheme: () => void
  isGradient: boolean
  isClassic: boolean
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined)

export function ThemeProvider({ children }: { children: ReactNode }) {
  const [theme, setTheme] = useState<Theme>('gradient')
  const [mounted, setMounted] = useState(false)

  // İlk yüklemede localStorage'dan tema al
  useEffect(() => {
    const savedTheme = localStorage.getItem('aa-theme') as Theme
    if (savedTheme === 'classic' || savedTheme === 'gradient') {
      setTheme(savedTheme)
    }
    setMounted(true)
  }, [])

  // Tema değiştiğinde localStorage'a kaydet ve body class'ını güncelle
  useEffect(() => {
    if (mounted) {
      localStorage.setItem('aa-theme', theme)
      document.documentElement.classList.remove('theme-gradient', 'theme-classic')
      document.documentElement.classList.add(`theme-${theme}`)
    }
  }, [theme, mounted])

  const toggleTheme = () => {
    setTheme(prev => prev === 'gradient' ? 'classic' : 'gradient')
  }

  const value: ThemeContextType = {
    theme,
    toggleTheme,
    isGradient: theme === 'gradient',
    isClassic: theme === 'classic'
  }

  // Prevent flash of unstyled content
  if (!mounted) {
    return null
  }

  return (
    <ThemeContext.Provider value={value}>
      {children}
    </ThemeContext.Provider>
  )
}

export function useTheme() {
  const context = useContext(ThemeContext)
  if (context === undefined) {
    throw new Error('useTheme must be used within a ThemeProvider')
  }
  return context
}

