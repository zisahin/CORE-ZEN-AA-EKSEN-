'use client'

import { useState, useEffect } from 'react'
import AIDailyQuestions from './AIDailyQuestions'
import AIChat from './AIChat'
import { newsService } from '@/services/newsService'

interface LiveNewsItem {
  id: string
  title: string
  time: string
  category: string
  urgent: boolean
  xp: number
}

const aiSuggestions = [
  "Size özel ekonomi analizi hazırladık",
  "Spor haberlerinde yeni gelişmeler var"
]

export default function RightTimeline() {
  const [readItems, setReadItems] = useState<string[]>([])
  const [currentSuggestion, setCurrentSuggestion] = useState(0)
  const [isAIChatOpen, setIsAIChatOpen] = useState(false)
  const [liveNews, setLiveNews] = useState<LiveNewsItem[]>([])

  // Son 3 saatteki haberleri yükle
  useEffect(() => {
    const loadLiveNews = async () => {
      try {
        const allNews = await newsService.getNews(100)
        const now = new Date()
        const threeHoursAgo = new Date(now.getTime() - 3 * 60 * 60 * 1000)

        const recentNews = allNews
          .filter(news => {
            const newsDate = news.publishedAt.toDate()
            return newsDate >= threeHoursAgo && newsDate <= now
          })
          .sort((a, b) => b.publishedAt.toDate().getTime() - a.publishedAt.toDate().getTime())
          .slice(0, 10)
          .map(news => {
            const newsDate = news.publishedAt.toDate()
            const diffMs = now.getTime() - newsDate.getTime()
            const diffMins = Math.floor(diffMs / 60000)
            const diffHours = Math.floor(diffMins / 60)
            
            let timeText = ''
            if (diffMins < 1) timeText = 'Az önce'
            else if (diffMins < 60) timeText = `${diffMins} dk önce`
            else timeText = `${diffHours} saat önce`

            return {
              id: news.id,
              title: news.title,
              time: timeText,
              category: news.category,
              urgent: news.breaking || false,
              xp: news.xpPoints
            }
          })

        setLiveNews(recentNews)
        console.log(`✅ ${recentNews.length} canlı haber yüklendi`)
      } catch (error) {
        console.error('❌ Canlı haberler yüklenemedi:', error)
      }
    }

    loadLiveNews()
    // Her 2 dakikada bir güncelle
    const interval = setInterval(loadLiveNews, 2 * 60 * 1000)
    return () => clearInterval(interval)
  }, [])

  const handleRead = (id: string, xp: number) => {
    if (!readItems.includes(id)) {
      setReadItems([...readItems, id])
      // XP animation trigger here
    }
  }

  return (
    <aside className="w-80 bg-white/60 backdrop-blur border-l border-cream-strong h-screen sticky top-0 overflow-y-auto">
      {/* AI Assistant */}
      <div 
        className="p-4 bg-gradient-to-br from-brand-blue/10 to-purple-50 border-b border-cream-strong cursor-pointer hover:from-brand-blue/20 hover:to-purple-100 transition-all"
        onClick={() => setIsAIChatOpen(true)}
      >
        <div className="flex items-center gap-3 mb-3">
          <div className="w-8 h-8 rounded-full overflow-hidden ring-2 ring-brand-blue/30">
            <img src="/images/ai-assistant.png" alt="AI" className="w-full h-full object-cover" />
          </div>
          <div>
            <h3 className="font-bold text-brand-blue text-sm">AA AI Asistan</h3>
            <p className="text-xs text-slate-500">Kişisel Haber Analisti</p>
          </div>
          <div className="ml-auto">
            <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
          </div>
        </div>
        <div className="bg-white/80 backdrop-blur rounded-xl p-3 border border-white/50 shadow-lg hover:shadow-xl transition-shadow">
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
        <div className="flex items-center gap-2">
          <div className="relative">
            <div className="w-2 h-2 bg-red-500 rounded-full"></div>
            <div className="absolute top-0 left-0 w-2 h-2 bg-red-500 rounded-full animate-ping"></div>
          </div>
          <button className="text-xs text-red-600 font-medium hover:underline">
            Hemen Oku 
          </button>
        </div>
      </div>

      {/* Timeline Header */}
      <div className="p-4 border-b border-cream-strong">
        <div className="flex items-center justify-between">
          <h3 className="font-bold text-slate-800"> Canlı Haber Akışı</h3>
          <button className="text-xs text-brand-blue hover:underline">Tümü</button>
        </div>
        <p className="text-xs text-slate-500 mt-1">Son 3 saatteki gelişmeler</p>
      </div>

      {/* Timeline Items */}
      <div className="p-3">
        {liveNews.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            <p className="text-sm">Son 3 saatte haber yok</p>
          </div>
        ) : (
          <div className="space-y-3">
            {liveNews.map((item) => (
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
        )}
      </div>

      {/* AI Daily Questions */}
      <div className="p-4 border-t border-cream-strong">
        <AIDailyQuestions maxShow={1} />
      </div>

      {/* Daily Challenge */}
      <div className="p-4 border-t border-cream-strong bg-gradient-to-br from-amber-50 to-yellow-50">
        <div className="text-center">
          <div className="w-12 h-12 bg-gradient-to-br from-blue-400 to-black rounded-full flex items-center justify-center mx-auto mb-2">
            <span className="text-white text-xl">🎯</span>
          </div>
          <h4 className="font-bold text-slate-800 text-sm mb-1">Günlük Meydan Okuma</h4>
          <p className="text-xs text-slate-600 mb-3">5 farklı kategoriden haber oku</p>
          
          <div className="bg-white/80 rounded-full h-2 mb-2 overflow-hidden">
            <div className="bg-gradient-to-r from-blue-400 to-black h-full w-3/5 transition-all duration-500"></div>
          </div>
          
          <div className="flex justify-between text-xs">
            <span className="text-slate-500">3/5 tamamlandı</span>
            <span className="font-bold text-blue-900">+50 XP</span>
          </div>
        </div>
      </div>

      {/* AI Chat Modal */}
      {isAIChatOpen && (
        <AIChat 
          isOpen={isAIChatOpen}
          onClose={() => setIsAIChatOpen(false)} 
        />
      )}
    </aside>
  )
}




