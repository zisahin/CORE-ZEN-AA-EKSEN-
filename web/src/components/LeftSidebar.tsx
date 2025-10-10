'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation' 

interface LeftSidebarProps {
  onOpenMap?: () => void
  currentPath?: string
}
const menuItems = [
  { label: 'Anasayfa', path: '/', active: true },
  { label: 'Son Dakika', badge: '3' },
  { label: 'Gündem' },
  { label: 'Ekonomi' },
  { label: 'Spor'  },
  { label: 'Teknoloji' },
  { label: 'Dünya'  },
  { label: 'Haber Haritası', path: '/news-map', badge: 'YENİ'},
  { label: 'Zaman Tüneli', path: '/timeline', badge: 'YENİ' },
  { label: 'Podcast', path: '/podcast', badge: 'YENİ'  },
  { label: 'İhtiyaç Hattı'  },
  { label: 'Liderlik',  badge: 'YENİ' }
]

export default function LeftSidebar({ onOpenMap, currentPath = '/' }: LeftSidebarProps) {
  const [activeItem, setActiveItem] = useState('Anasayfa')
  const router = useRouter()

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
    <aside className="w-64 bg-gradient-to-b from-slate-900 via-slate-900 to-blue-900 backdrop-blur border-r border-white/10 h-screen sticky top-0 overflow-y-auto shadow-2xl">
      {/* Header */}
      <div className="p-4 border-b border-white/10">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-gradient-to-br from-purple-600 to-blue-600 rounded-lg flex items-center justify-center shadow-lg">
            <img src="/images/aa-logo.png" alt="AA" className="w-5 h-5 object-contain brightness-0 invert" />
          </div>
          <div>
            <h2 className="font-bold text-white text-sm">AA EKSEN</h2>
            <p className="text-xs text-white/60">105 yıldır yanınızda</p>
          </div>
        </div>
      </div>

      {/* User Stats */}
      <div className="p-4 bg-gradient-to-r from-purple-900/30 to-blue-900/30 border-b border-white/10">
        <div className="flex items-center justify-between text-xs">
          <span className="text-white/80">Günlük XP</span>
          <span className="font-bold text-purple-400">250/500</span>
        </div>
        <div className="mt-2 bg-white/10 rounded-full h-2 overflow-hidden">
          <div className="bg-gradient-to-r from-purple-600 to-blue-600 h-full w-1/2 transition-all duration-500"></div>
        </div>
        <div className="mt-2 flex items-center justify-between">
          <span className="text-xs text-white/60">Seviye 12</span>
          <span className="text-xs font-bold text-yellow-400">🏆 Uzman</span>
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
                  ? 'bg-gradient-to-r from-purple-600 to-purple-700 text-white shadow-lg shadow-purple-900/50 transform scale-[1.02]'
                  : 'text-white/80 hover:bg-white/10 hover:text-white hover:shadow-md'
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
                    ? 'bg-green-500/20 text-green-400 border border-green-500/50'
                    : 'bg-red-500/20 text-red-400 border border-red-500/50'
                }`}>
                  {item.badge}
                </span>
              )}
            </button>
          ))}
        </div>
      </nav>

      {/* Bottom Action */}
      <div className="p-4 mt-auto border-t border-white/10">
        <button className="w-full bg-gradient-to-r from-purple-600 to-purple-700 text-white px-4 py-3 rounded-xl text-sm font-bold shadow-lg shadow-purple-900/50 hover:shadow-xl hover:from-purple-700 hover:to-purple-800 transform hover:scale-105 transition-all duration-200">
          ⚡ Günlük Bonus Al
        </button>
      </div>
    </aside>
  )
}




