'use client'

import { useState, useEffect } from 'react'
import aiService from '@/services/aiService'

interface PodcastSidebarProps {
  selectedPodcastId?: string
  onPodcastSelect: (podcast: any) => void
}

export default function PodcastSidebar({ selectedPodcastId, onPodcastSelect }: PodcastSidebarProps) {
  const [activeCategory, setActiveCategory] = useState<string>('all')
  const [podcasts, setPodcasts] = useState<any[]>([])
  const [categories, setCategories] = useState<any[]>([])
  const [loading, setLoading] = useState(true)

  // Kategorileri ve podcastleri yükle
  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      setLoading(true)
      
      // Kategorileri al
      const categoriesData = await aiService.getPodcastCategories()
      setCategories([
        { id: 'all', name: 'Tümü', icon: '🎙️', podcastCount: 0 },
        ...categoriesData
      ])

      // Podcastleri al
      const podcastsData = await aiService.getPodcastList(undefined, 20)
      setPodcasts(podcastsData.podcasts || [])
      
      console.log('✅ Podcast kütüphanesi yüklendi')
    } catch (error) {
      console.error('❌ Podcast yükleme hatası:', error)
    } finally {
      setLoading(false)
    }
  }

  // Kategori değiştiğinde podcastleri filtrele
  const handleCategoryChange = async (categoryId: string) => {
    setActiveCategory(categoryId)
    setLoading(true)

    try {
      const podcastsData = categoryId === 'all' 
        ? await aiService.getPodcastList(undefined, 20)
        : await aiService.getPodcastList(categoryId, 20)
      
      setPodcasts(podcastsData.podcasts || [])
    } catch (error) {
      console.error('❌ Kategori yükleme hatası:', error)
    } finally {
      setLoading(false)
    }
  }

  // Süreyi formatlama
  const formatDuration = (seconds: number) => {
    const minutes = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${minutes}:${secs.toString().padStart(2, '0')}`
  }

  // Tarih formatlama
  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const hours = Math.floor(diff / (1000 * 60 * 60))
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))

    if (hours < 24) return `${hours} saat önce`
    if (days < 7) return `${days} gün önce`
    return date.toLocaleDateString('tr-TR', { day: 'numeric', month: 'short' })
  }

  return (
    <aside className="w-96 bg-white border-l border-gray-200 h-screen sticky top-0 overflow-y-auto">
      {/* Başlık */}
      <div className="p-6 border-b border-gray-200 bg-gradient-to-r from-blue-50 to-purple-50">
        <h2 className="text-2xl font-bold text-gray-800 mb-2">
          📚 Podcast Kütüphanesi
        </h2>
        <p className="text-sm text-gray-600">
          {podcasts.length} podcast mevcut
        </p>
      </div>

      {/* Kategoriler */}
      <div className="p-4 border-b border-gray-100">
        <h3 className="text-xs font-semibold text-gray-500 uppercase mb-3 px-2">
          Kategoriler
        </h3>
        <div className="space-y-1">
          {categories.map((category) => (
            <button
              key={category.id}
              onClick={() => handleCategoryChange(category.id)}
              className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 ${
                activeCategory === category.id
                  ? 'bg-gradient-to-r from-blue-500 to-purple-600 text-white shadow-lg transform scale-[1.02]'
                  : 'text-gray-700 hover:bg-gray-100 hover:shadow-md'
              }`}
            >
              <div className="flex items-center gap-3">
                <span className="text-lg">{category.icon}</span>
                <span>{category.name}</span>
              </div>
              {category.podcastCount > 0 && (
                <span className={`px-2 py-0.5 rounded-full text-xs font-bold ${
                  activeCategory === category.id
                    ? 'bg-white/20 text-white'
                    : 'bg-blue-100 text-blue-700'
                }`}>
                  {category.podcastCount}
                </span>
              )}
            </button>
          ))}
        </div>
      </div>

      {/* Podcast Listesi */}
      <div className="p-4">
        <h3 className="text-xs font-semibold text-gray-500 uppercase mb-3 px-2">
          Son Podcastler
        </h3>
        
        {loading ? (
          <div className="flex flex-col items-center justify-center py-12">
            <div className="animate-spin w-10 h-10 border-3 border-blue-500 border-t-transparent rounded-full mb-4"></div>
            <p className="text-sm text-gray-500">Yükleniyor...</p>
          </div>
        ) : (
          <div className="space-y-3">
            {podcasts.map((podcast) => (
              <button
                key={podcast.id}
                onClick={() => onPodcastSelect(podcast)}
                className={`w-full text-left p-4 rounded-xl transition-all duration-200 border-2 ${
                  selectedPodcastId === podcast.id
                    ? 'bg-gradient-to-r from-blue-50 to-purple-50 border-blue-300 shadow-lg transform scale-[1.02]'
                    : 'bg-white border-gray-200 hover:border-blue-200 hover:shadow-md hover:bg-gray-50'
                }`}
              >
                {/* Podcast Header */}
                <div className="flex items-start gap-3 mb-3">
                  <div className={`flex-shrink-0 w-12 h-12 rounded-lg flex items-center justify-center text-xl ${
                    selectedPodcastId === podcast.id
                      ? 'bg-gradient-to-br from-blue-500 to-purple-600 text-white'
                      : 'bg-gray-100 text-gray-600'
                  } shadow-md`}>
                    🎙️
                  </div>
                  <div className="flex-1 min-w-0">
                    <h4 className="font-bold text-gray-800 text-sm mb-1 line-clamp-2">
                      {podcast.title}
                    </h4>
                    <p className="text-xs text-gray-500 line-clamp-1">
                      {podcast.description}
                    </p>
                  </div>
                </div>

                {/* Podcast Metadata */}
                <div className="flex items-center justify-between text-xs text-gray-500">
                  <div className="flex items-center gap-3">
                    <span className="flex items-center gap-1">
                      <span className="w-1.5 h-1.5 bg-blue-500 rounded-full"></span>
                      {formatDuration(podcast.duration)}
                    </span>
                    <span className="flex items-center gap-1">
                      <span className="w-1.5 h-1.5 bg-green-500 rounded-full"></span>
                      {podcast.category}
                    </span>
                  </div>
                  <span className="text-gray-400">
                    {formatDate(podcast.createdAt)}
                  </span>
                </div>

                {/* Dinlenme Sayısı */}
                <div className="mt-2 flex items-center gap-2 text-xs">
                  <span className="text-gray-400">👂</span>
                  <span className="text-gray-600 font-medium">
                    {podcast.listenCount} dinlenme
                  </span>
                </div>

                {/* Seçili İşareti */}
                {selectedPodcastId === podcast.id && (
                  <div className="mt-3 flex items-center gap-2 text-xs font-bold text-blue-600">
                    <span className="animate-pulse">▶</span>
                    Şu an oynatılıyor
                  </div>
                )}
              </button>
            ))}
          </div>
        )}

        {/* Boş Durum */}
        {!loading && podcasts.length === 0 && (
          <div className="text-center py-12">
            <div className="text-5xl mb-4">🎧</div>
            <p className="text-gray-500 text-sm">
              Bu kategoride podcast bulunamadı
            </p>
          </div>
        )}
      </div>

      {/* Alt Bilgi */}
      <div className="p-4 border-t border-gray-200 bg-gradient-to-r from-gray-50 to-blue-50">
        <div className="text-center">
          <p className="text-xs text-gray-500 mb-2">
            🤖 AI destekli podcast üretimi
          </p>
          <p className="text-xs text-gray-400">
            Powered by OpenAI TTS
          </p>
        </div>
      </div>
    </aside>
  )
}

