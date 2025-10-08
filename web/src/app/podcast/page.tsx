'use client'

import { useState, useEffect } from 'react'
import { usePathname } from 'next/navigation'
import TopNav from '@/components/TopNav'
import LeftSidebar from '@/components/LeftSidebar'
import PodcastPlayer from '@/components/PodcastPlayer'
import PodcastSidebar from '@/components/PodcastSidebar'

export default function PodcastPage() {
  const pathname = usePathname()
  const [selectedPodcast, setSelectedPodcast] = useState<any>(null)
  const [audioUrl, setAudioUrl] = useState<string | null>(null)
  const [isLoadingAudio, setIsLoadingAudio] = useState(false)

  // İlk podcast'i otomatik yükle
  useEffect(() => {
    // Varsayılan podcast
    setSelectedPodcast({
      id: 'podcast_1',
      title: '🎙️ Günün Ekonomi Haberleri',
      description: 'Merkez Bankası kararları ve piyasa analizleri',
      duration: 180,
      category: 'Ekonomi',
      coverImage: '/images/aa-logo.png',
      listenCount: 45,
      createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
    })
  }, [])

  // Podcast seçildiğinde ses dosyasını yükle
  const handlePodcastSelect = async (podcast: any) => {
    setSelectedPodcast(podcast)
    setAudioUrl(null)
    setIsLoadingAudio(true)
    
    try {
      // AI Service'den ses dosyasını al
      const response = await fetch(`http://localhost:3001/api/podcast/${podcast.id}/audio`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ voice: 'nova' }),
      })

      if (response.ok) {
        const blob = await response.blob()
        const url = URL.createObjectURL(blob)
        setAudioUrl(url)
        console.log('✅ Podcast ses dosyası yüklendi!')
      } else {
        console.error('❌ Ses dosyası yüklenemedi')
      }
    } catch (error) {
      console.error('❌ Podcast yükleme hatası:', error)
    } finally {
      setIsLoadingAudio(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
      <TopNav />
      
      <div className="flex">
        <LeftSidebar currentPath={pathname} />
        
        {/* Ana İçerik - Podcast Player */}
        <main className="flex-1 min-h-screen p-8">
          <div className="max-w-4xl mx-auto">
            {/* Sayfa Başlığı */}
            <div className="mb-8">
              <h1 className="text-4xl font-bold text-gray-800 mb-3">
                🎙️ AA Eksen Podcast
              </h1>
              <p className="text-gray-600 text-lg">
                Güncel haberler, analizler ve özel içerikler podcast formatında
              </p>
            </div>

            {/* Podcast Player */}
            {selectedPodcast && (
              <div className="bg-white rounded-2xl shadow-xl p-8 border border-gray-100">
                {isLoadingAudio ? (
                  <div className="flex flex-col items-center justify-center py-20">
                    <div className="animate-spin w-16 h-16 border-4 border-blue-500 border-t-transparent rounded-full mb-6"></div>
                    <p className="text-gray-600 text-lg font-medium">Podcast yükleniyor...</p>
                    <p className="text-gray-400 text-sm mt-2">OpenAI TTS ile ses oluşturuluyor</p>
                  </div>
                ) : audioUrl ? (
                  <PodcastPlayer
                    podcastId={selectedPodcast.id}
                    title={selectedPodcast.title}
                    coverImage={selectedPodcast.coverImage}
                    audioUrl={audioUrl}
                    duration={selectedPodcast.duration}
                  />
                ) : (
                  <div className="text-center py-20">
                    <div className="text-6xl mb-4">🎧</div>
                    <p className="text-gray-600 text-lg">Podcast yükleniyor...</p>
                  </div>
                )}

                {/* Podcast Bilgileri */}
                <div className="mt-8 pt-8 border-t border-gray-100">
                  <div className="flex items-start gap-6">
                    <div className="flex-shrink-0">
                      <div className="w-20 h-20 rounded-xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-3xl shadow-lg">
                        🎙️
                      </div>
                    </div>
                    <div className="flex-1">
                      <h3 className="text-xl font-bold text-gray-800 mb-2">
                        {selectedPodcast.title}
                      </h3>
                      <p className="text-gray-600 mb-4">
                        {selectedPodcast.description}
                      </p>
                      <div className="flex flex-wrap gap-4 text-sm text-gray-500">
                        <span className="flex items-center gap-2">
                          <span className="w-2 h-2 bg-blue-500 rounded-full"></span>
                          {selectedPodcast.category}
                        </span>
                        <span className="flex items-center gap-2">
                          <span className="w-2 h-2 bg-green-500 rounded-full"></span>
                          {Math.floor(selectedPodcast.duration / 60)} dakika
                        </span>
                        <span className="flex items-center gap-2">
                          <span className="w-2 h-2 bg-purple-500 rounded-full"></span>
                          {selectedPodcast.listenCount} dinlenme
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        </main>

        {/* Sağ Sidebar - Podcast Kütüphanesi */}
        <PodcastSidebar 
          selectedPodcastId={selectedPodcast?.id}
          onPodcastSelect={handlePodcastSelect}
        />
      </div>
    </div>
  )
}

