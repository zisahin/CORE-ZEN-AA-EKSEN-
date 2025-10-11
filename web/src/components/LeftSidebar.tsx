'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { useTheme } from '@/context/ThemeContext'

interface LeftSidebarProps {
  onOpenMap?: () => void
  currentPath?: string
}
const menuItems = [
  { label: 'Anasayfa', path: '/', active: true },
  { label: 'Son Dakika', path: '/category/son-dakika', badge: '🔴' },
  { label: 'Gündem', path: '/category/gundem' },
  { label: 'Ekonomi', path: '/category/ekonomi' },
  { label: 'Spor', path: '/category/spor'  },
  { label: 'Teknoloji', path: '/category/teknoloji' },
  { label: 'Dünya', path: '/category/dunya'  },
  { label: 'Haber Haritası', path: '/news-map', badge: 'YENİ'},
  { label: 'Zaman Tüneli', path: '/timeline', badge: 'YENİ' },
  { label: 'Podcast', path: '/podcast', badge: 'YENİ'  },
  { label: 'İhtiyaç Hattı'  },
  { label: 'Liderlik',  badge: 'YENİ' }
]

export default function LeftSidebar({ onOpenMap, currentPath = '/' }: LeftSidebarProps) {
  const [activeItem, setActiveItem] = useState('Anasayfa')
  const router = useRouter()
  const { isGradient } = useTheme()

  const handleNavigation = (item: any) => { 
    setActiveItem(item.label)
    
    // Haber Haritası modalı aç (ana sayfadayken)
    if (item.label === 'Haber Haritası' && onOpenMap && currentPath === '/') {
      onOpenMap()
    } 
    // Eğer path varsa sayfaya git
    else if (item.path) {
      router.push(item.path)
    }
  }
  return (
    <aside className={`w-64 backdrop-blur border-r h-screen sticky top-0 overflow-y-auto transition-all duration-300 ${
      isGradient
        ? 'bg-gradient-to-b from-slate-900 via-slate-900 to-blue-900 border-white/10 shadow-2xl'
        : 'bg-white/80 border-cream-strong'
    }`}>
      {/* Header */}
      <div className={`p-4 border-b transition-colors duration-300 ${
        isGradient ? 'border-white/10' : 'border-cream-strong'
      }`}>
        <div className="flex items-center gap-3">
          <div className={`w-8 h-8 rounded-lg flex items-center justify-center shadow-lg ${
            isGradient 
              ? 'bg-gradient-to-br from-purple-600 to-blue-600'
              : 'bg-brand-blue'
          }`}>
            <img src="/images/aa-logo.png" alt="AA" className="w-5 h-5 object-contain brightness-0 invert" />
          </div>
          <div>
            <h2 className={`font-bold text-sm ${isGradient ? 'text-white' : 'text-brand-blue'}`}>AA EKSEN</h2>
            <p className={`text-xs ${isGradient ? 'text-white/60' : 'text-slate-500'}`}>105 yıldır yanınızda</p>
          </div>
        </div>
      </div>

      {/* User Stats */}
      <div className={`p-4 border-b transition-colors duration-300 ${
        isGradient
          ? 'bg-gradient-to-r from-purple-900/30 to-blue-900/30 border-white/10'
          : 'bg-gradient-to-r from-brand-blue/5 to-amber-50 border-cream-strong'
      }`}>
        <div className="flex items-center justify-between text-xs">
          <span className={isGradient ? 'text-white/80' : 'text-slate-600'}>Günlük XP</span>
          <span className={`font-bold ${isGradient ? 'text-purple-400' : 'text-brand-blue'}`}>250/500</span>
        </div>
        <div className={`mt-2 rounded-full h-2 overflow-hidden ${isGradient ? 'bg-white/10' : 'bg-white'}`}>
          <div className={`h-full w-1/2 transition-all duration-500 ${
            isGradient 
              ? 'bg-gradient-to-r from-purple-600 to-blue-600'
              : 'bg-gradient-to-r from-brand-blue to-blue-600'
          }`}></div>
        </div>
        <div className="mt-2 flex items-center justify-between">
          <span className={`text-xs ${isGradient ? 'text-white/60' : 'text-slate-500'}`}>Seviye 12</span>
          <span className={`text-xs font-bold ${isGradient ? 'text-yellow-400' : 'text-amber-600'}`}>🏆 Uzman</span>
        </div>
      </div>

      {/* Navigation Menu */}
      <nav className="p-3">
        <div className="space-y-1">
          {menuItems.map((item) => (
            <button
              key={item.label}
              onClick={() => handleNavigation(item)}
              className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 ${
                (currentPath && item.path && currentPath === item.path) || activeItem === item.label
                  ? isGradient
                    ? 'bg-gradient-to-r from-purple-600 to-purple-700 text-white shadow-lg shadow-purple-900/50 transform scale-[1.02]'
                    : 'bg-brand-blue text-white shadow-lg transform scale-[1.02]'
                  : isGradient
                    ? 'text-white/80 hover:bg-white/10 hover:text-white hover:shadow-md'
                    : 'text-slate-700 hover:bg-cream hover:text-brand-blue hover:shadow-md'
              }`}
            >
              <div className="flex items-center gap-3">
                <span>{item.label}</span>
              </div>
              {item.badge && (
                <span className={`px-2 py-0.5 rounded-full text-xs font-bold ${
                  activeItem === item.label
                    ? 'bg-white/20 text-white'
                    : item.badge === 'YENİ'
                    ? isGradient
                      ? 'bg-green-500/20 text-green-400 border border-green-500/50'
                      : 'bg-green-100 text-green-700'
                    : isGradient
                      ? 'bg-red-500/20 text-red-400 border border-red-500/50'
                      : 'bg-red-100 text-red-700'
                }`}>
                  {item.badge}
                </span>
              )}
            </button>
          ))}
        </div>
      </nav>

      {/* Bottom Action */}
      <div className={`p-4 mt-auto border-t transition-colors duration-300 ${
        isGradient ? 'border-white/10' : 'border-cream-strong'
      }`}>
        <button className={`w-full text-white px-4 py-3 rounded-xl text-sm font-bold shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200 ${
          isGradient
            ? 'bg-gradient-to-r from-purple-600 to-purple-700 shadow-purple-900/50 hover:from-purple-700 hover:to-purple-800'
            : 'bg-gradient-to-r from-blue-600 to-black'
        }`}>
          ⚡ Günlük Bonus Al
        </button>
      </div>
    </aside>
  )
}




