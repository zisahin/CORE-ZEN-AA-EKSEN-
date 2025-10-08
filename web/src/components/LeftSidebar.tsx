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
    <aside className="w-64 bg-white/80 backdrop-blur border-r border-cream-strong h-screen sticky top-0 overflow-y-auto">
      {/* Header */}
      <div className="p-4 border-b border-cream-strong">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-brand-blue rounded-lg flex items-center justify-center">
            <img src="/images/aa-logo.png" alt="AA" className="w-5 h-5 object-contain brightness-0 invert" />
          </div>
          <div>
            <h2 className="font-bold text-brand-blue text-sm">AA EKSEN</h2>
            <p className="text-xs text-slate-500">105 yıldır yanınızda</p>
          </div>
        </div>
      </div>

      {/* User Stats */}
      <div className="p-4 bg-gradient-to-r from-brand-blue/5 to-amber-50 border-b border-cream-strong">
        <div className="flex items-center justify-between text-xs">
          <span className="text-slate-600">Günlük XP</span>
          <span className="font-bold text-brand-blue">250/500</span>
        </div>
        <div className="mt-2 bg-white rounded-full h-2 overflow-hidden">
          <div className="bg-gradient-to-r from-brand-blue to-blue-600 h-full w-1/2 transition-all duration-500"></div>
        </div>
        <div className="mt-2 flex items-center justify-between">
          <span className="text-xs text-slate-500">Seviye 12</span>
          <span className="text-xs font-bold text-amber-600">🏆 Uzman</span>
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
                  ? 'bg-brand-blue text-white shadow-lg transform scale-[1.02]'
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
                    ? 'bg-green-100 text-green-700'
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
      <div className="p-4 mt-auto border-t border-cream-strong">
        <button className="w-full bg-gradient-to-r from-blue-600 to-black text-white px-4 py-3 rounded-xl text-sm font-bold shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200">
          ⚡ Günlük Bonus Al
        </button>
      </div>
    </aside>
  )
}




