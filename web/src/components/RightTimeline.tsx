'use client'

import { useState } from 'react'

const timelineNews = [
  {
    id: 1,
    title: "Borsa İstanbul'da Rekor Artış",
    time: "2 dk önce",
    category: "Ekonomi",
    urgent: true,
    xp: 15
  },
  {
    id: 2,
    title: "Milli Takım Kadrosu Açıklandı",
    time: "15 dk önce", 
    category: "Spor",
    xp: 12
  },
  {
    id: 3,
    title: "Yeni Teknoloji Merkezi Açılıyor",
    time: "32 dk önce",
    category: "Teknoloji", 
    xp: 18
  },
  {
    id: 4,
    title: "İklim Zirvesi Sonuçları",
    time: "1 saat önce",
    category: "Dünya",
    xp: 20
  },
  {
    id: 5,
    title: "Eğitimde Dijital Dönüşüm",
    time: "2 saat önce",
    category: "Eğitim",
    xp: 16
  }
]

const aiSuggestions = [
  "Size özel ekonomi analizi hazırladık",
  "Spor haberlerinde yeni gelişmeler var",
  "Bu haberi arkadaşlarınız da okudu"
]

export default function RightTimeline() {
  const [readItems, setReadItems] = useState<number[]>([])
  const [currentSuggestion, setCurrentSuggestion] = useState(0)

  const handleRead = (id: number, xp: number) => {
    if (!readItems.includes(id)) {
      setReadItems([...readItems, id])
      // XP animation trigger here
    }
  }

  return (
    <aside className="w-80 bg-white/60 backdrop-blur border-l border-cream-strong h-screen sticky top-0 overflow-y-auto">
      {/* AI Assistant */}
      <div className="p-4 bg-gradient-to-br from-brand-blue/10 to-purple-50 border-b border-cream-strong">
        <div className="flex items-center gap-3 mb-3">
          <div className="w-8 h-8 bg-gradient-to-br from-brand-blue to-purple-600 rounded-full flex items-center justify-center">
            <span className="text-white text-sm">🤖</span>
          </div>
          <div>
            <h3 className="font-bold text-brand-blue text-sm">AA AI Asistan</h3>
            <p className="text-xs text-slate-500">Kişisel haber analisti</p>
          </div>
        </div>
        
        <div className="bg-white/80 backdrop-blur rounded-xl p-3 border border-white/50 shadow-lg">
          <p className="text-sm text-slate-700 mb-2">
            {aiSuggestions[currentSuggestion]}
          </p>
          <button className="text-xs text-brand-blue font-medium hover:underline">
            Detayları Gör →
          </button>
        </div>
      </div>

      {/* Breaking News Alert */}
      <div className="p-4 bg-gradient-to-r from-red-50 to-orange-50 border-b border-cream-strong">
        <div className="flex items-center gap-2 mb-2">
          <span className="w-2 h-2 bg-red-500 rounded-full animate-pulse"></span>
          <span className="text-red-700 font-bold text-xs uppercase tracking-wide">Son Dakika</span>
        </div>
        <h4 className="text-sm font-bold text-slate-800 mb-1">
          Kritik Gelişme: Ekonomik Paket Açıklandı
        </h4>
        <button className="text-xs text-red-600 font-medium hover:underline">
          🔥 Hemen Oku (+30 XP)
        </button>
      </div>

      {/* Timeline Header */}
      <div className="p-4 border-b border-cream-strong">
        <div className="flex items-center justify-between">
          <h3 className="font-bold text-slate-800">📈 Canlı Haber Akışı</h3>
          <button className="text-xs text-brand-blue hover:underline">Tümü</button>
        </div>
        <p className="text-xs text-slate-500 mt-1">Son 3 saatteki gelişmeler</p>
      </div>

      {/* Timeline Items */}
      <div className="p-3">
        <div className="space-y-3">
          {timelineNews.map((item) => (
            <div
              key={item.id}
              className={`group relative p-3 rounded-xl border transition-all duration-200 hover:shadow-lg cursor-pointer ${
                readItems.includes(item.id)
                  ? 'bg-green-50 border-green-200'
                  : 'bg-white/80 border-cream-strong hover:border-brand-blue/30'
              }`}
              onClick={() => handleRead(item.id, item.xp)}
            >
              {item.urgent && (
                <div className="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full animate-pulse"></div>
              )}
              
              <div className="flex items-start justify-between gap-2 mb-2">
                <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                  item.category === 'Ekonomi' ? 'bg-blue-100 text-blue-700' :
                  item.category === 'Spor' ? 'bg-green-100 text-green-700' :
                  item.category === 'Teknoloji' ? 'bg-purple-100 text-purple-700' :
                  'bg-gray-100 text-gray-700'
                }`}>
                  {item.category}
                </span>
                <span className="text-xs text-slate-500">{item.time}</span>
              </div>
              
              <h4 className={`text-sm font-medium mb-2 leading-snug ${
                readItems.includes(item.id) ? 'text-green-800' : 'text-slate-800'
              }`}>
                {item.title}
              </h4>
              
              <div className="flex items-center justify-between">
                <span className={`text-xs ${
                  readItems.includes(item.id) ? 'text-green-600 font-bold' : 'text-amber-600'
                }`}>
                  {readItems.includes(item.id) ? '✅ Okundu' : `+${item.xp} XP`}
                </span>
                <button className="opacity-0 group-hover:opacity-100 text-xs text-brand-blue hover:underline transition-opacity">
                  Oku →
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Daily Challenge */}
      <div className="p-4 border-t border-cream-strong bg-gradient-to-br from-amber-50 to-yellow-50">
        <div className="text-center">
          <div className="w-12 h-12 bg-gradient-to-br from-amber-400 to-orange-500 rounded-full flex items-center justify-center mx-auto mb-2">
            <span className="text-white text-xl">🎯</span>
          </div>
          <h4 className="font-bold text-slate-800 text-sm mb-1">Günlük Meydan Okuma</h4>
          <p className="text-xs text-slate-600 mb-3">5 farklı kategoriden haber oku</p>
          
          <div className="bg-white/80 rounded-full h-2 mb-2 overflow-hidden">
            <div className="bg-gradient-to-r from-amber-400 to-orange-500 h-full w-3/5 transition-all duration-500"></div>
          </div>
          
          <div className="flex justify-between text-xs">
            <span className="text-slate-500">3/5 tamamlandı</span>
            <span className="font-bold text-amber-600">+50 XP</span>
          </div>
        </div>
      </div>
    </aside>
  )
}
