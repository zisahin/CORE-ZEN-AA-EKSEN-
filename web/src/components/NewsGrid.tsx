'use client'
import { useState, useEffect } from 'react'
import NewsDetail from './NewsDetail'
import aiService from '@/services/aiService'

// Kategori mapping: Türkçe -> İngilizce (API için)
const categoryMapping: { [key: string]: string } = {
  'Tümü': 'all',
  'Güncel': 'guncel',
  'Ekonomi': 'ekonomi',
  'Spor': 'spor',
  'Teknoloji': 'teknoloji',
  'Dünya': 'dunya',
  'Sağlık': 'saglik',
  'Eğitim': 'egitim',
  'Kültür': 'kultur',
  'Siyaset': 'siyaset'
}

const categories = ['Tümü', 'Güncel', 'Ekonomi', 'Spor', 'Teknoloji', 'Dünya', 'Sağlık']

interface NewsItem {
  id: string
  title: string
  description: string
  content: string
  link: string
  pubDate: Date
  category: string
  categoryId: string
  source: string
}

export default function NewsGrid() {
  const [selectedCategory, setSelectedCategory] = useState('Tümü')
  const [newsData, setNewsData] = useState<NewsItem[]>([])
  const [loading, setLoading] = useState(true)
  const [readNews, setReadNews] = useState<string[]>([])
  const [likedNews, setLikedNews] = useState<string[]>([])
  const [selectedNewsId, setSelectedNewsId] = useState<string | null>(null)

  // Haberleri yükle
  useEffect(() => {
    loadNews(selectedCategory)
  }, [selectedCategory])

  const loadNews = async (category: string) => {
    setLoading(true)
    try {
      let news: any[] = []
      
      if (category === 'Tümü') {
        // Karışık haberler
        news = await aiService.getMixedNews(30)
      } else {
        // Kategoriye göre haberler
        const categoryId = categoryMapping[category] || 'guncel'
        news = await aiService.getNewsByCategory(categoryId, 20)
      }
      
      setNewsData(news)
    } catch (error) {
      console.error('Haberler yüklenirken hata:', error)
      setNewsData([])
    } finally {
      setLoading(false)
    }
  }

  const filteredNews = newsData
  const handleRead = (newsId: string) => {
    if (!readNews.includes(newsId)) {
      setReadNews([...readNews, newsId])
      // XP earning animation would trigger here
    }
  }
  
  const handleLike = (newsId: string) => {
    if (likedNews.includes(newsId)) {
      setLikedNews(likedNews.filter(id => id !== newsId))
    } else {
      setLikedNews([...likedNews, newsId])
    }
  }

  const calculateReadTime = (content: string): string => {
    const wordsPerMinute = 200
    const words = content.split(' ').length
    const minutes = Math.ceil(words / wordsPerMinute)
    return `${minutes} dk`
  }
  const getCategoryColor = (category: string) => {
    const colors = {
      'Teknoloji': 'bg-purple-100 text-purple-700 border-purple-200',
      'Ekonomi': 'bg-blue-100 text-blue-700 border-blue-200',
      'Spor': 'bg-green-100 text-green-700 border-green-200',
      'Ulaşım': 'bg-orange-100 text-orange-700 border-orange-200',
      'Eğitim': 'bg-indigo-100 text-indigo-700 border-indigo-200',
      'Sağlık': 'bg-pink-100 text-pink-700 border-pink-200'
    }
    return colors[category as keyof typeof colors] || 'bg-gray-100 text-gray-700 border-gray-200'
  }
  
  return (
    <section className="space-y-6">
      {/* Category Filter */}
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-slate-800"> Güncel Haberler</h2>
        <div className="flex gap-2 overflow-x-auto">
          {categories.map((category) => (
            <button
              key={category}
              onClick={() => setSelectedCategory(category)}
              className={`px-4 py-2 rounded-full text-sm font-medium transition-all duration-200 whitespace-nowrap ${
                selectedCategory === category
                  ? 'bg-brand-blue text-white shadow-lg transform scale-105'
                  : 'bg-white/80 text-slate-700 border border-cream-strong hover:bg-brand-blue/10 hover:border-brand-blue/30'
              }`}
            >
              {category}
            </button>
          ))}
        </div>
      </div>
      {/* News Grid */}
      {!loading && filteredNews.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredNews.map((news) => (
            <article
              key={news.id}
              className={`group bg-white/80 backdrop-blur rounded-2xl overflow-hidden border transition-all duration-300 hover:shadow-2xl hover:scale-[1.02] cursor-pointer ${
                readNews.includes(news.id)
                  ? 'border-green-200 bg-green-50/50'
                  : 'border-cream-strong hover:border-brand-blue/30'
              }`}
              onClick={() => { setSelectedNewsId(news.id); handleRead(news.id); }}
            >
              {/* Image */}
              <div className="relative h-48 overflow-hidden bg-gradient-to-br from-brand-blue/20 to-purple-600/20">
                <img
                  src={`https://picsum.photos/400/300?random=${news.id}`}
                  alt={news.title}
                  className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                  onError={(e) => {
                    e.currentTarget.src = '/images/istanbul.jpg'
                  }}
                />
                <div className="absolute inset-0 bg-gradient-to-t from-black/40 to-transparent" />
                {/* Category Badge */}
                <div className={`absolute top-3 left-3 px-3 py-1 rounded-full text-xs font-bold border ${getCategoryColor(news.category)}`}>
                  {news.category}
                </div>
              </div>
              {/* Content */}
              <div className="p-5">
                <h3 className={`font-bold text-lg mb-2 leading-tight group-hover:text-brand-blue transition-colors ${
                readNews.includes(news.id) ? 'text-green-800' : 'text-slate-800'
              }`}>
                {news.title}
              </h3>
              <p className="text-slate-600 text-sm mb-4 leading-relaxed line-clamp-2">
                {news.summary}
              </p>
              {/* Meta Info */}
              <div className="flex items-center justify-between text-xs text-slate-500 mb-4">
                <span className="flex items-center gap-1">
                  <img src="/images/kitap.png" alt="Okuma süresi" className="w-4 h-4" />
                  {news.readTime} okuma
                </span>
                <span>{new Date(news.publishedAt).toLocaleDateString('tr-TR')}</span>
              </div>
              {/* Actions */}
              <div className="flex items-center justify-between pt-3 border-t border-cream-strong">
                <div className="flex items-center gap-4">
                  <button
                    onClick={(e) => {
                      e.stopPropagation()
                      handleLike(news.id)
                    }}
                    className="flex items-center gap-1 transition-all duration-200"
                  >
                    <img
                      src={likedNews.includes(news.id)
                        ? "/images/like-filled.png"
                        : "/images/like-empty.png"
                      }
                      alt="Beğen"
                      className="w-5 h-5"
                    />
                    <span className={`text-xs font-medium ${
                      likedNews.includes(news.id) ? 'text-red-500' : 'text-slate-500'
                    }`}>
                      {news.likes + (likedNews.includes(news.id) ? 1 : 0)}
                    </span>
                  </button>
                  <button className="flex items-center gap-1 text-slate-500 hover:text-brand-blue transition-colors">
                    <img src="/images/share.png" alt="Paylaş" className="w-6 h-6" />
                    <span className="text-xs font-medium">Paylaş</span>
                  </button>
                </div>
                <button className={`text-xs font-bold px-3 py-1 rounded-full transition-all ${
                  readNews.includes(news.id)
                    ? 'bg-green-100 text-green-700'
                    : 'bg-brand-blue text-white hover:bg-blue-700'
                }`}>
                  {readNews.includes(news.id) ? 'Okundu' : 'Oku'}
                </button>
              </div>
            </div>
          </article>
        ))}
      </div>
      )}
      {/* Load More */}
      <div className="text-center pt-8">
        <button className="bg-white/80 backdrop-blur border border-cream-strong text-slate-700 px-8 py-3 rounded-xl font-medium hover:bg-brand-blue hover:text-white hover:border-brand-blue transition-all duration-200 shadow-lg hover:shadow-xl">
          Daha Fazla Haber Yükle
        </button>
      </div>
      
      {/* NewsDetail Modal */}
      {selectedNewsId && (
        <NewsDetail
          newsId={selectedNewsId}
          onClose={() => setSelectedNewsId(null)}
        />
      )}
    </section>
  )
}