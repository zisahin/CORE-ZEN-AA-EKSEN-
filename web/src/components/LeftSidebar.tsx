'use client'

import { useState } from 'react'

const menuItems = [
  { label: 'Anasayfa', icon: '🏠', active: true },
  { label: 'Son Dakika', icon: '⚡', badge: '3' },
  { label: 'Gündem', icon: '📰' },
  { label: 'Ekonomi', icon: '💰' },
  { label: 'Spor', icon: '⚽' },
  { label: 'Teknoloji', icon: '💻' },
  { label: 'Dünya', icon: '🌍' },
  { label: 'Haber Haritası', icon: '🗺️' },
  { label: 'İhtiyaç Hattı', icon: '🤝' },
  { label: 'Oyunlar', icon: '🎮', badge: 'YENİ' },
  { label: 'Podcast', icon: '🎧' },
  { label: 'Liderlik', icon: '👔' }
]

export default function LeftSidebar() {
  const [activeItem, setActiveItem] = useState('Anasayfa')

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
              onClick={() => setActiveItem(item.label)}
              className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 ${
                activeItem === item.label
                  ? 'bg-brand-blue text-white shadow-lg transform scale-[1.02]'
                  : 'text-slate-700 hover:bg-cream hover:text-brand-blue hover:shadow-md'
              }`}
            >
              <div className="flex items-center gap-3">
                <span className="text-base">{item.icon}</span>
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
        <button className="w-full bg-gradient-to-r from-amber-400 to-orange-500 text-white px-4 py-3 rounded-xl text-sm font-bold shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200">
          ⚡ Günlük Bonus Al
        </button>
      </div>
    </aside>
  )
}
