'use client'

import { useState, useEffect } from 'react'
import { useParams } from 'next/navigation'
import { useTheme } from '@/context/ThemeContext'
import TopNav from '@/components/TopNav'
import TickerBar from '@/components/TickerBar'
import LeftSidebar from '@/components/LeftSidebar'
import RightTimeline from '@/components/RightTimeline'
import AIBubble from '@/components/AIBubble'
import { rssService, RSS_CATEGORIES, RSSNewsItem } from '@/services/rssService'

export default function CategoryPage() {
  const { isGradient } = useTheme()
  const params = useParams()
  const slug = params.slug as string
  const [news, setNews] = useState<RSSNewsItem[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const category = RSS_CATEGORIES[slug as keyof typeof RSS_CATEGORIES]

  useEffect(() => {
    async function loadNews() {
      setLoading(true)
      setError(null)
      try {
        const data = await rssService.fetchNewsByCategory(slug, 50)
        setNews(data)
      } catch (err) {
        setError('Haberler yüklenirken bir hata oluştu')
        console.error('News load error:', err)
      } finally {
        setLoading(false)
      }
    }

    if (category) {
      loadNews()
    } else {
      setError('Geçersiz kategori')
      setLoading(false)
    }
  }, [slug, category])

  return (
    <div className={`min-h-screen relative overflow-hidden transition-all duration-300 ${
      isGradient 
        ? 'bg-gradient-to-br from-slate-950 via-blue-950 to-slate-950' 
        : 'bg-cream'
    }`}>
      {/* Dekoratif Background Elements - Sadece Gradient Mode */}
      {isGradient && (
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute top-20 left-20 w-96 h-96 bg-purple-600/10 rounded-full blur-3xl animate-pulse"></div>
          <div className="absolute bottom-20 right-20 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '700ms' }}></div>
        </div>
      )}

      {/* Content */}
      <div className="relative z-10">
        <TopNav />
        <TickerBar />
        
        <div className="flex">
          <LeftSidebar currentPath={`/category/${slug}`} />
          
          <main className="flex-1 min-h-screen overflow-hidden">
            <div className="p-6 space-y-6">
              {/* Header */}
              <div className={`backdrop-blur-xl border rounded-2xl p-6 shadow-2xl transition-all duration-300 ${
                isGradient
                  ? 'bg-gradient-to-r from-purple-900/40 to-blue-900/40 border-white/10'
                  : 'bg-white/80 border-cream-strong'
              }`}>
                <div className="flex items-center gap-4">
                  <div className={`w-12 h-12 rounded-xl flex items-center justify-center shadow-lg ${
                    isGradient
                      ? 'bg-gradient-to-br from-purple-600 to-blue-600'
                      : 'bg-brand-blue'
                  }`}>
                    <span className="text-2xl">{getCategoryIcon(slug)}</span>
                  </div>
                  <div>
                    <h1 className={`text-3xl font-bold ${
                      isGradient ? 'text-white' : 'text-brand-blue'
                    }`}>
                      {category?.name || 'Kategori'}
                    </h1>
                    <p className={`text-sm mt-1 ${
                      isGradient ? 'text-white/60' : 'text-slate-600'
                    }`}>
                      Anadolu Ajansı - En güncel haberler
                    </p>
                  </div>
                </div>
              </div>

              {/* Loading State */}
              {loading && (
                <div className="flex items-center justify-center py-20">
                  <div className="text-center">
                    <div className={`w-16 h-16 border-4 border-t-transparent rounded-full animate-spin mx-auto mb-4 ${
                      isGradient ? 'border-purple-600' : 'border-brand-blue'
                    }`}></div>
                    <p className={isGradient ? 'text-white/60' : 'text-slate-600'}>
                      Haberler yükleniyor...
                    </p>
                  </div>
                </div>
              )}

              {/* Error State */}
              {error && (
                <div className={`border rounded-xl p-6 text-center ${
                  isGradient
                    ? 'bg-red-900/30 border-red-500/50'
                    : 'bg-red-50 border-red-200'
                }`}>
                  <p className={`font-medium ${
                    isGradient ? 'text-red-400' : 'text-red-700'
                  }`}>{error}</p>
                </div>
              )}

              {/* News Grid */}
              {!loading && !error && news.length > 0 && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {news.map((item) => (
                    <a
                      key={item.id}
                      href={item.link}
                      target="_blank"
                      rel="noopener noreferrer"
                      className={`group backdrop-blur-xl border rounded-xl overflow-hidden hover:shadow-2xl transition-all duration-300 hover:-translate-y-1 ${
                        isGradient
                          ? 'bg-gradient-to-br from-slate-900/50 to-slate-800/50 border-white/10 hover:border-purple-500/50 hover:shadow-purple-900/30'
                          : 'bg-white/90 border-cream-strong hover:border-brand-blue/30 hover:shadow-brand-blue/20'
                      }`}
                    >
                      {/* Image */}
                      <div className={`relative h-48 overflow-hidden ${
                        isGradient ? 'bg-slate-800' : 'bg-gray-100'
                      }`}>
                        <img
                          src={item.image}
                          alt={item.title}
                          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                          onError={(e) => {
                            const target = e.target as HTMLImageElement;
                            target.src = 'https://picsum.photos/400/300?grayscale';
                          }}
                        />
                        <div className={`absolute top-3 left-3 backdrop-blur-sm px-3 py-1 rounded-full ${
                          isGradient
                            ? 'bg-purple-600/90'
                            : 'bg-brand-blue/90'
                        }`}>
                          <span className="text-white text-xs font-bold">{item.category}</span>
                        </div>
                      </div>

                      {/* Content */}
                      <div className="p-5">
                        <h3 className={`font-bold text-lg mb-2 line-clamp-2 transition-colors ${
                          isGradient
                            ? 'text-white group-hover:text-purple-400'
                            : 'text-slate-800 group-hover:text-brand-blue'
                        }`}>
                          {item.title}
                        </h3>
                        <p className={`text-sm line-clamp-3 mb-4 ${
                          isGradient ? 'text-white/60' : 'text-slate-600'
                        }`}>
                          {item.content.replace(/<[^>]*>/g, '')}
                        </p>
                        <div className="flex items-center justify-between text-xs">
                          <span className={isGradient ? 'text-white/40' : 'text-slate-500'}>
                            {item.source}
                          </span>
                          <span className={isGradient ? 'text-white/40' : 'text-slate-500'}>
                            {new Date(item.publishedAt).toLocaleDateString('tr-TR', {
                              day: 'numeric',
                              month: 'short',
                              hour: '2-digit',
                              minute: '2-digit'
                            })}
                          </span>
                        </div>
                      </div>
                    </a>
                  ))}
                </div>
              )}

              {/* Empty State */}
              {!loading && !error && news.length === 0 && (
                <div className={`backdrop-blur-xl border rounded-xl p-12 text-center ${
                  isGradient
                    ? 'bg-slate-900/50 border-white/10'
                    : 'bg-white/80 border-cream-strong'
                }`}>
                  <div className="text-6xl mb-4">📰</div>
                  <h3 className={`text-xl font-bold mb-2 ${
                    isGradient ? 'text-white' : 'text-slate-800'
                  }`}>Henüz Haber Yok</h3>
                  <p className={isGradient ? 'text-white/60' : 'text-slate-600'}>
                    Bu kategoride şu anda haber bulunmuyor.
                  </p>
                </div>
              )}
            </div>
          </main>
          
          <RightTimeline />
        </div>

        <AIBubble />
      </div>
    </div>
  )
}

function getCategoryIcon(slug: string): string {
  const icons: Record<string, string> = {
    'son-dakika': '🔴',
    'gundem': '📰',
    'ekonomi': '💰',
    'spor': '⚽',
    'teknoloji': '💻',
    'dunya': '🌍'
  }
  return icons[slug] || '📄'
}

