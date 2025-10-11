'use client'

import { useState, useEffect } from 'react'
import { useTheme } from '@/context/ThemeContext'
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
  const { isGradient } = useTheme()
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
    <aside className={`w-80 backdrop-blur border-l h-screen sticky top-0 overflow-y-auto transition-all duration-300 ${
      isGradient
        ? 'bg-slate-900/60 border-white/10'
        : 'bg-white/60 border-cream-strong'
    }`}>
      {/* AI Assistant */}
      <div 
        className={`p-4 border-b cursor-pointer transition-all ${
          isGradient
            ? 'bg-gradient-to-br from-purple-900/30 to-blue-900/30 border-white/10 hover:from-purple-900/40 hover:to-blue-900/40'
            : 'bg-gradient-to-br from-brand-blue/10 to-purple-50 border-cream-strong hover:from-brand-blue/20 hover:to-purple-100'
        }`}
        onClick={() => setIsAIChatOpen(true)}
      >
        <div className="flex items-center gap-3 mb-3">
          <div className={`w-8 h-8 rounded-full overflow-hidden ring-2 ${
            isGradient ? 'ring-purple-500/30' : 'ring-brand-blue/30'
          }`}>
            <img src="/images/ai-assistant.png" alt="AI" className="w-full h-full object-cover" />
          </div>
          <div>
            <h3 className={`font-bold text-sm ${
              isGradient ? 'text-purple-400' : 'text-brand-blue'
            }`}>AA AI Asistan</h3>
            <p className={`text-xs ${
              isGradient ? 'text-white/50' : 'text-slate-500'
            }`}>Kişisel Haber Analisti</p>
          </div>
          <div className="ml-auto">
            <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
          </div>
        </div>
        <div className={`backdrop-blur rounded-xl p-3 border shadow-lg hover:shadow-xl transition-shadow ${
          isGradient
            ? 'bg-white/10 border-white/20'
            : 'bg-white/80 border-white/50'
        }`}>
          <p className={`text-sm mb-2 ${
            isGradient ? 'text-white/80' : 'text-slate-700'
          }`}>
            {aiSuggestions[currentSuggestion]}
          </p>
          <button className={`text-xs font-medium hover:underline ${
            isGradient ? 'text-purple-400' : 'text-brand-blue'
          }`}>
            Detayları Gör →
          </button>
        </div>
      </div>

      {/* Breaking News Alert */}
      <div className={`p-4 border-b ${
        isGradient
          ? 'bg-gradient-to-r from-red-900/30 to-orange-900/30 border-white/10'
          : 'bg-gradient-to-r from-red-50 to-orange-50 border-cream-strong'
      }`}>
        <div className="flex items-center gap-2 mb-2">
          <span className="w-2 h-2 bg-red-500 rounded-full animate-pulse"></span>
          <span className={`font-bold text-xs uppercase tracking-wide ${
            isGradient ? 'text-red-400' : 'text-red-700'
          }`}>Son Dakika</span>
        </div>
        <h4 className={`text-sm font-bold mb-1 ${
          isGradient ? 'text-white' : 'text-slate-800'
        }`}>
          Kritik Gelişme: Ekonomik Paket Açıklandı
        </h4>
        <div className="flex items-center gap-2">
          <div className="relative">
            <div className="w-2 h-2 bg-red-500 rounded-full"></div>
            <div className="absolute top-0 left-0 w-2 h-2 bg-red-500 rounded-full animate-ping"></div>
          </div>
          <button className={`text-xs font-medium hover:underline ${
            isGradient ? 'text-red-400' : 'text-red-600'
          }`}>
            Hemen Oku 🔔
          </button>
        </div>
      </div>

      {/* Timeline Header */}
      <div className={`p-4 border-b ${
        isGradient ? 'border-white/10' : 'border-cream-strong'
      }`}>
        <div className="flex items-center justify-between">
          <h3 className={`font-bold ${
            isGradient ? 'text-white' : 'text-slate-800'
          }`}>⚡ Canlı Haber Akışı</h3>
          <button className={`text-xs hover:underline ${
            isGradient ? 'text-purple-400' : 'text-brand-blue'
          }`}>Tümü</button>
        </div>
        <p className={`text-xs mt-1 ${
          isGradient ? 'text-white/50' : 'text-slate-500'
        }`}>Son 3 saatteki gelişmeler</p>
      </div>

      {/* Timeline Items */}
      <div className="p-3">
        {liveNews.length === 0 ? (
          <div className={`text-center py-8 ${
            isGradient ? 'text-white/40' : 'text-gray-500'
          }`}>
            <p className="text-sm">Son 3 saatte haber yok</p>
          </div>
        ) : (
          <div className="space-y-3">
            {liveNews.map((item) => (
            <div
              key={item.id}
              className={`group relative p-3 rounded-xl border transition-all duration-200 hover:shadow-lg cursor-pointer ${
                readItems.includes(item.id)
                  ? isGradient
                    ? 'bg-green-900/30 border-green-500/50'
                    : 'bg-green-50 border-green-200'
                  : isGradient
                    ? 'bg-white/5 border-white/10 hover:border-purple-500/50 hover:bg-white/10'
                    : 'bg-white/80 border-cream-strong hover:border-brand-blue/30'
              }`}
              onClick={() => handleRead(item.id, item.xp)}
            >
              {item.urgent && (
                <div className="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full animate-pulse"></div>
              )}
              
              <div className="flex items-start justify-between gap-2 mb-2">
                <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                  isGradient
                    ? item.category === 'Ekonomi' ? 'bg-blue-600/80 text-white' :
                      item.category === 'Spor' ? 'bg-green-600/80 text-white' :
                      item.category === 'Teknoloji' ? 'bg-purple-600/80 text-white' :
                      'bg-gray-600/80 text-white'
                    : item.category === 'Ekonomi' ? 'bg-blue-100 text-blue-700' :
                      item.category === 'Spor' ? 'bg-green-100 text-green-700' :
                      item.category === 'Teknoloji' ? 'bg-purple-100 text-purple-700' :
                      'bg-gray-100 text-gray-700'
                }`}>
                  {item.category}
                </span>
                <span className={`text-xs ${
                  isGradient ? 'text-white/40' : 'text-slate-500'
                }`}>{item.time}</span>
              </div>
              
              <h4 className={`text-sm font-medium mb-2 leading-snug ${
                readItems.includes(item.id)
                  ? isGradient ? 'text-green-400' : 'text-green-800'
                  : isGradient ? 'text-white' : 'text-slate-800'
              }`}>
                {item.title}
              </h4>
              
              <div className="flex items-center justify-between">
                <span className={`text-xs ${
                  readItems.includes(item.id)
                    ? isGradient ? 'text-green-400 font-bold' : 'text-green-600 font-bold'
                    : isGradient ? 'text-yellow-400' : 'text-amber-600'
                }`}>
                  {readItems.includes(item.id) ? '✅ Okundu' : `+${item.xp} XP`}
                </span>
                <button className={`opacity-0 group-hover:opacity-100 text-xs hover:underline transition-opacity ${
                  isGradient ? 'text-purple-400' : 'text-brand-blue'
                }`}>
                  Oku →
                </button>
              </div>
            </div>
          ))}
          </div>
        )}
      </div>

      {/* AI Daily Questions */}
      <div className={`p-4 border-t ${
        isGradient ? 'border-white/10' : 'border-cream-strong'
      }`}>
        <AIDailyQuestions maxShow={1} />
      </div>

      {/* Daily Challenge */}
      <div className={`p-4 border-t ${
        isGradient
          ? 'border-white/10 bg-gradient-to-br from-yellow-900/20 to-amber-900/20'
          : 'border-cream-strong bg-gradient-to-br from-amber-50 to-yellow-50'
      }`}>
        <div className="text-center">
          <div className={`w-12 h-12 rounded-full flex items-center justify-center mx-auto mb-2 ${
            isGradient
              ? 'bg-gradient-to-br from-purple-600 to-blue-600'
              : 'bg-gradient-to-br from-blue-400 to-black'
          }`}>
            <span className="text-white text-xl">🎯</span>
          </div>
          <h4 className={`font-bold text-sm mb-1 ${
            isGradient ? 'text-white' : 'text-slate-800'
          }`}>Günlük Meydan Okuma</h4>
          <p className={`text-xs mb-3 ${
            isGradient ? 'text-white/60' : 'text-slate-600'
          }`}>5 farklı kategoriden haber oku</p>
          
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




