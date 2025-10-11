'use client';

import { useState, useEffect } from 'react';
import { newsService } from '@/services/newsService';
import { rssService, RSSNewsItem } from '@/services/rssService';
import { NewsItem as FirestoreNews } from '@/types/firestore';
import { useTheme } from '@/context/ThemeContext';
import NewsDetail from './NewsDetail';

// Kategori mapping: Türkçe -> Firebase/API kategorileri
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
  'Siyaset': 'siyaset',
  'Ulaşım': 'ulasim'
}

const categories = ['Tümü', 'Güncel', 'Ekonomi', 'Spor', 'Teknoloji', 'Dünya', 'Sağlık', 'Siyaset', 'Ulaşım']

interface NewsItem {
  id: string
  title: string
  description: string
  content?: string
  link: string
  pubDate: string
  publishedAt?: string
  category: string
  categoryId?: string
  source?: string
  imageUrl?: string
  readTime?: number
  summary?: string
  likes?: number
}

export default function NewsGrid() {
  const { isGradient } = useTheme()
  const [selectedCategory, setSelectedCategory] = useState('Tümü')
  const [newsData, setNewsData] = useState<NewsItem[]>([])
  const [loading, setLoading] = useState(true)
  const [readNews, setReadNews] = useState<string[]>([])
  const [likedNews, setLikedNews] = useState<string[]>([])
  const [selectedNewsId, setSelectedNewsId] = useState<string | null>(null)

  // Sayfa yüklendiğinde ve kategori değiştiğinde haberleri yükle
  useEffect(() => {
    loadNews();
  }, [selectedCategory]);

  // Haberleri yükle - Firebase ve RSS hybrid yaklaşımı
  const loadNews = async () => {
    setLoading(true);
    try {
      let newsWithDates: NewsItem[] = [];
      
      // Önce Firebase'den dene
      try {
        let fetchedNews;
        const apiCategory = categoryMapping[selectedCategory] || 'all';
        
        if (apiCategory === 'all') {
          fetchedNews = await newsService.getNews(50);
        } else {
          fetchedNews = await newsService.getNewsByCategory(apiCategory, 50);
        }
        
        if (fetchedNews && fetchedNews.length > 0) {
          newsWithDates = fetchedNews.map(news => ({
            id: news.id,
            title: news.title,
            description: news.content.substring(0, 200) + '...',
            link: news.newsUrl,
            pubDate: news.publishedAt.toDate().toISOString(),
            imageUrl: news.imageUrl || '/images/kitap.png',
            category: news.category,
            readTime: Math.ceil(news.content.length / 1000),
            summary: news.content.substring(0, 150) + '...',
            publishedAt: news.publishedAt.toDate().toISOString(),
            likes: news.likeCount || 0
          }));
          console.log(`✅ Firebase'den ${newsWithDates.length} haber yüklendi (Kategori: ${selectedCategory})`);
        }
      } catch (firebaseError) {
        console.warn('⚠️ Firebase veri yok veya hata, RSS\'e geçiliyor...', firebaseError);
      }
      
      // Firebase'den veri gelmezse RSS'den çek
      if (newsWithDates.length === 0) {
        const rssNews = await rssService.fetchMixedNews(50);
        
        newsWithDates = rssNews.map(news => ({
          id: news.id,
          title: news.title,
          description: news.content.substring(0, 200) + '...',
          content: news.content,
          link: news.link,
          pubDate: news.publishedAt.toISOString(),
          imageUrl: news.image,
          category: news.category,
          categoryId: news.categoryId,
          source: news.source,
          readTime: Math.ceil(news.content.length / 1000),
          summary: news.content.substring(0, 150).replace(/<[^>]*>/g, '') + '...',
          publishedAt: news.publishedAt.toISOString(),
          likes: 0
        }));
        console.log(`✅ RSS'den ${newsWithDates.length} haber yüklendi (Kategori: ${selectedCategory})`);
      }
      
      // Kategori filtresi uygula (RSS için)
      if (selectedCategory !== 'Tümü') {
        const apiCategory = categoryMapping[selectedCategory];
        newsWithDates = newsWithDates.filter(news => 
          news.category === selectedCategory || 
          news.categoryId === apiCategory
        );
      }
      
      setNewsData(newsWithDates);
    } catch (error) {
      console.error('❌ Haber yükleme hatası:', error);
      setNewsData([]);
    } finally {
      setLoading(false);
    }
  };

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
    if (isGradient) {
      const gradientColors = {
        'Teknoloji': 'bg-purple-600/90 text-white border-purple-500',
        'Ekonomi': 'bg-blue-600/90 text-white border-blue-500',
        'Spor': 'bg-green-600/90 text-white border-green-500',
        'Ulaşım': 'bg-orange-600/90 text-white border-orange-500',
        'Eğitim': 'bg-indigo-600/90 text-white border-indigo-500',
        'Sağlık': 'bg-pink-600/90 text-white border-pink-500',
        'Dünya': 'bg-purple-600/90 text-white border-purple-500',
        'Güncel': 'bg-blue-600/90 text-white border-blue-500',
      }
      return gradientColors[category as keyof typeof gradientColors] || 'bg-gray-600/90 text-white border-gray-500'
    } else {
      const classicColors = {
        'Teknoloji': 'bg-purple-100 text-purple-700 border-purple-200',
        'Ekonomi': 'bg-blue-100 text-blue-700 border-blue-200',
        'Spor': 'bg-green-100 text-green-700 border-green-200',
        'Ulaşım': 'bg-orange-100 text-orange-700 border-orange-200',
        'Eğitim': 'bg-indigo-100 text-indigo-700 border-indigo-200',
        'Sağlık': 'bg-pink-100 text-pink-700 border-pink-200',
        'Dünya': 'bg-purple-100 text-purple-700 border-purple-200',
        'Güncel': 'bg-blue-100 text-blue-700 border-blue-200',
      }
      return classicColors[category as keyof typeof classicColors] || 'bg-gray-100 text-gray-700 border-gray-200'
    }
  }
  
  return (
    <section className="space-y-6">
      {/* Category Filter */}
      <div className="flex items-center justify-between">
        <h2 className={`text-2xl font-bold transition-colors duration-300 ${
          isGradient ? 'text-white' : 'text-slate-800'
        }`}>
          📰 Güncel Haberler
        </h2>
        <div className="flex gap-2 overflow-x-auto">
          {categories.map((category) => (
            <button
              key={category}
              onClick={() => setSelectedCategory(category)}
              className={`px-4 py-2 rounded-full text-sm font-medium transition-all duration-200 whitespace-nowrap ${
                selectedCategory === category
                  ? isGradient
                    ? 'bg-gradient-to-r from-purple-600 to-blue-600 text-white shadow-lg transform scale-105'
                    : 'bg-brand-blue text-white shadow-lg transform scale-105'
                  : isGradient
                    ? 'bg-white/10 text-white/80 border border-white/20 hover:bg-white/20 hover:text-white'
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
              className={`group backdrop-blur rounded-2xl overflow-hidden border transition-all duration-300 hover:shadow-2xl hover:scale-[1.02] cursor-pointer ${
                readNews.includes(news.id)
                  ? isGradient
                    ? 'border-green-500/50 bg-green-900/30'
                    : 'border-green-200 bg-green-50/50'
                  : isGradient
                    ? 'bg-slate-900/50 border-white/10 hover:border-purple-500/50'
                    : 'bg-white/80 border-cream-strong hover:border-brand-blue/30'
              }`}
              onClick={() => { setSelectedNewsId(news.id); handleRead(news.id); }}
            >
              {/* Image */}
              <div className={`relative h-48 overflow-hidden ${
                isGradient
                  ? 'bg-gradient-to-br from-purple-900/20 to-blue-900/20'
                  : 'bg-gradient-to-br from-brand-blue/20 to-purple-600/20'
              }`}>
                <img
                  src={news.imageUrl || '/images/kitap.png'}
                  alt={news.title}
                  className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                  onError={(e) => {
                    e.currentTarget.src = '/images/kitap.png'
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
                <h3 className={`font-bold text-lg mb-2 leading-tight transition-colors ${
                readNews.includes(news.id)
                  ? isGradient ? 'text-green-400' : 'text-green-800'
                  : isGradient
                    ? 'text-white group-hover:text-purple-400'
                    : 'text-slate-800 group-hover:text-brand-blue'
              }`}>
                {news.title}
              </h3>
              <p className={`text-sm mb-4 leading-relaxed line-clamp-2 ${
                isGradient ? 'text-white/60' : 'text-slate-600'
              }`}>
                {news.summary}
              </p>
              {/* Meta Info */}
              <div className={`flex items-center justify-between text-xs mb-4 ${
                isGradient ? 'text-white/40' : 'text-slate-500'
              }`}>
                <span className="flex items-center gap-1">
                  <img src="/images/kitap.png" alt="Okuma süresi" className="w-4 h-4" />
                  {news.readTime || 5} okuma
                </span>
                <span>{new Date(news.publishedAt || news.pubDate).toLocaleDateString('tr-TR')}</span>
              </div>
              {/* Actions */}
              <div className={`flex items-center justify-between pt-3 border-t ${
                isGradient ? 'border-white/10' : 'border-cream-strong'
              }`}>
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
                      {(news.likes || 0) + (likedNews.includes(news.id) ? 1 : 0)}
                    </span>
                  </button>
                  <button className={`flex items-center gap-1 transition-colors ${
                    isGradient
                      ? 'text-white/60 hover:text-purple-400'
                      : 'text-slate-500 hover:text-brand-blue'
                  }`}>
                    <img 
                      src={isGradient ? "/images/share-white.png" : "/images/share.png"} 
                      alt="Paylaş" 
                      className="w-6 h-6" 
                    />
                    <span className="text-xs font-medium">Paylaş</span>
                  </button>
                </div>
                <button className={`text-xs font-bold px-3 py-1 rounded-full transition-all ${
                  readNews.includes(news.id)
                    ? isGradient
                      ? 'bg-green-600/30 text-green-400'
                      : 'bg-green-100 text-green-700'
                    : isGradient
                      ? 'bg-gradient-to-r from-purple-600 to-blue-600 text-white hover:from-purple-700 hover:to-blue-700'
                      : 'bg-brand-blue text-white hover:bg-blue-700'
                }`}>
                  {readNews.includes(news.id) ? 'Okundu ✓' : 'Oku'}
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