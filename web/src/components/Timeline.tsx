'use client'

import { useState } from 'react'

interface TimelineEvent {
  id: string
  title: string
  summary: string
  image: string
  category: string
  date: Date
  isTimelineMaterial: boolean
  relatedEvents?: string[]
}

// Ana timeline verisi
const mainTimelineData: TimelineEvent[] = [
  {
    id: '1',
    title: 'Marmaray Projesi İhalesi Açıklandı',
    summary: 'İstanbul Boğazı altından geçecek dev projenin ihale süreci başladı.',
    image: '/images/istanbul.jpg',
    category: 'Ulaşım',
    date: new Date('2024-01-10'),
    isTimelineMaterial: true,
  },
  {
    id: '2', 
    title: 'Marmaray İnşaat Çalışmaları Başladı',
    summary: 'Boğaz altında kazı işlemlerine başlandı. İlk etapta pilot tünel açılacak.',
    image: '/images/istanbul.jpg',
    category: 'Ulaşım',
    date: new Date('2024-01-15'),
    isTimelineMaterial: true,
  },
  {
    id: '3',
    title: 'Marmaray Test Sürüşleri Gerçekleştirildi',
    summary: 'Proje kapsamında ilk test sürüşleri başarıyla tamamlandı.',
    image: '/images/istanbul.jpg', 
    category: 'Ulaşım',
    date: new Date('2024-01-20'),
    isTimelineMaterial: true,
  },
  {
    id: '4',
    title: 'Marmaray Halk İçin Açıldı',
    summary: 'Uzun yıllar süren çalışmaların ardından Marmaray hizmet vermeye başladı.',
    image: '/images/istanbul.jpg',
    category: 'Ulaşım', 
    date: new Date('2024-01-25'),
    isTimelineMaterial: true,
  }
]

// Sol taraf mini timeline'ları
const sideTimelineData = [
  {
    id: 's1',
    title: 'Kanal İstanbul Projesi',
    summary: 'Dev projenin son gelişmeleri',
    category: 'Ulaşım',
    date: new Date('2024-01-28'),
    items: 4
  },
  {
    id: 's2', 
    title: 'İstanbul Havalimanı',
    summary: 'Yeni terminal açılışı',
    category: 'Ulaşım',
    date: new Date('2024-01-25'),
    items: 6
  },
  {
    id: 's3',
    title: 'Cumhurbaşkanlığı Seçimleri',
    summary: 'Seçim süreci gelişmeleri',
    category: 'Siyaset',
    date: new Date('2024-01-20'),
    items: 12
  },
  {
    id: 's4',
    title: 'Ekonomik Reform Paketi',
    summary: 'Yeni ekonomi politikaları',
    category: 'Ekonomi', 
    date: new Date('2024-01-15'),
    items: 8
  }
]

export default function Timeline() {
  const [selectedEvent, setSelectedEvent] = useState<TimelineEvent | null>(null)
  const [selectedSideTimeline, setSelectedSideTimeline] = useState<string | null>(null)

  return (
    <div className="min-h-screen bg-cream">
      {/* Header */}
      <div className="bg-brand-blue border-b border-cream-strong">
        <div className="max-w-7xl mx-auto px-6 py-6">
            <div className="text-center">
            <div className="flex items-center justify-center gap-3 mb-2">
                <img src="/images/timeline-logo-laci.png" alt="Timeline" className="w-8 h-8" />
                <h1 className="text-2xl font-bold text-white">Zaman Tüneli</h1>
                <img src="/images/timeline-logo-laci.png" alt="Timeline" className="w-8 h-8" />
            </div>
            <p className="text-white/90">Haberlerin kronolojik gelişim hikayesi</p>
         </div>
        </div>
    </div>

      <div className="max-w-7xl mx-auto flex gap-6 p-6">
        {/* Sol Taraf - Mini Timeline'lar */}
        <aside className="w-80 space-y-4">
          <div className="bg-white rounded-xl border border-cream-strong p-4">
          <h2 className="font-bold text-slate-800 mb-3 flex items-center gap-2">
            <img src="/images/timeline-logo.png" alt="Timeline" className="w-5 h-5" />
            Diğer Zaman Tünelleri
              <span className="bg-brand-blue text-white px-2 py-0.5 rounded-full text-xs">
                {sideTimelineData.length}
              </span>
            </h2>
            
            <div className="space-y-3">
              {sideTimelineData.map((timeline) => (
                <div
                  key={timeline.id}
                  onClick={() => setSelectedSideTimeline(timeline.id)}
                  className="group p-3 border border-gray-200 rounded-xl hover:border-brand-blue/30 hover:shadow-md transition-all cursor-pointer"
                >
                  <div className="flex items-start justify-between mb-2">
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 bg-brand-blue rounded-full"></div>
                      <span className="text-xs font-medium text-slate-500">
                        {timeline.category}
                      </span>
                    </div>
                    <span className="bg-amber-100 text-amber-700 px-2 py-0.5 rounded-full text-xs font-medium">
                      {timeline.items} haber
                    </span>
                  </div>
                  
                  <h3 className="font-semibold text-sm text-slate-800 mb-1 group-hover:text-brand-blue transition-colors">
                    {timeline.title}
                  </h3>
                  
                  <p className="text-xs text-slate-600 mb-2">
                    {timeline.summary}
                  </p>
                  
                  <div className="text-xs text-slate-500">
                    {timeline.date.toLocaleDateString('tr-TR')}
                  </div>
                </div>
              ))}
            </div>

            {/* Tüm Timeline'ları Gör */}
            <button className="w-full mt-4 py-2 bg-brand-blue/10 text-brand-blue rounded-xl text-sm font-medium hover:bg-brand-blue/20 transition-colors">
              Tüm Zaman Tünellerini Gör →
            </button>
          </div>

          {/* Popüler Etiketler */}
          <div className="bg-white rounded-xl border border-cream-strong p-4">
          <h3 className="font-semibold text-slate-800 mb-3 flex items-center gap-2">
            <img src="/images/etiket.png" alt="Etiket" className="w-5 h-5" />
            Popüler Etiketler
          </h3>
            <div className="flex flex-wrap gap-2">
              {['Ulaşım', 'Siyaset', 'Ekonomi', 'Teknoloji', 'Spor'].map(tag => (
                <span key={tag} className="px-2 py-1 bg-gray-100 text-gray-700 rounded-full text-xs hover:bg-brand-blue hover:text-white transition-colors cursor-pointer">
                  {tag}
                </span>
              ))}
            </div>
          </div>
        </aside>

        {/* Sağ Taraf - Ana Timeline */}
        <main className="flex-1">
          <div className="bg-white rounded-xl border border-cream-strong p-6">
            {/* Timeline Header */}
            <div className="flex items-center justify-between mb-6">
              <div>
              <h2 className="text-xl font-bold text-slate-800 mb-1 flex items-center gap-2">
                <img src="/images/timeline-logo.png" alt="Timeline" className="w-6 h-6" />
                Marmaray Projesi
             </h2>
                <p className="text-slate-600 text-sm">İstanbul'un ulaşım tarihindeki dönüm noktası</p>
              </div>
              <div className="flex items-center gap-2 bg-brand-blue/10 px-3 py-1.5 rounded-full">
                <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
                <span className="text-sm font-medium text-brand-blue">Canlı Takip</span>
              </div>
            </div>

            {/* Timeline Container */}
            <div className="relative">
              {/* Timeline Track (Sağda) */}
              <div className="absolute left-8 top-0 w-1 bg-gradient-to-b from-brand-blue via-purple-500 to-brand-blue h-full rounded-full"></div>
              
              {/* Timeline Events */}
              <div className="space-y-6">
                {mainTimelineData.map((event, index) => (
                  <div key={event.id} className="relative flex items-start gap-6">
                    {/* Metro Station */}
                    <div className="relative z-10 flex-shrink-0">
                      <div className="w-6 h-6 bg-brand-blue rounded-full border-4 border-white shadow-lg hover:scale-125 transition-transform cursor-pointer">
                        <div className="w-full h-full bg-gradient-to-br from-blue-400 to-brand-blue rounded-full"></div>
                      </div>
                      
                      {/* Station Number */}
                      <div className="absolute -left-8 top-1/2 transform -translate-y-1/2 bg-brand-blue text-white px-2 py-1 rounded-full text-xs font-bold">
                        {index + 1}
                      </div>
                    </div>

                    {/* Event Card - Yatay Dikdörtgen */}
                    <div 
                      className="flex-1 group cursor-pointer"
                      onClick={() => setSelectedEvent(event)}
                    >
                      <div className="bg-white border border-gray-200 rounded-xl overflow-hidden hover:border-brand-blue/30 hover:shadow-lg transition-all duration-300">
                        <div className="flex">
                          {/* Image */}
                          <div className="w-48 h-32 flex-shrink-0 relative overflow-hidden">
                            <img
                              src={event.image}
                              alt={event.title}
                              className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                            />
                            <div className="absolute inset-0 bg-gradient-to-r from-transparent to-black/20"></div>
                            
                            {/* Timeline Badge */}
                                                        <div className="absolute top-2 left-2 bg-brand-blue text-white px-2 py-1 rounded-full text-xs font-bold flex items-center gap-1">
                             <img src="/images/timeline-white.png" alt="Timeline" className="w-3 h-3" />
                            </div>
                          </div>

                          {/* Content */}
                          <div className="flex-1 p-4">
                            <div className="flex items-center justify-between mb-2">
                              <div className="flex items-center gap-2">
                                <span className="bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-xs font-medium">
                                  {event.category}
                                </span>
                                <span className="text-xs text-slate-500">
                                  {event.date.toLocaleDateString('tr-TR', { 
                                    day: 'numeric', 
                                    month: 'long', 
                                    year: 'numeric' 
                                  })}
                                </span>
                              </div>
                              
                              <div className="flex items-center gap-2">
                                <button className="w-8 h-8 bg-gray-100 hover:bg-brand-blue hover:text-white rounded-full flex items-center justify-center text-sm transition-colors">
                                  <img src="/images/voice.png" alt="Seslendir" className="w-4 h-4" />
                                </button>
                              </div>
                            </div>
                            
                            <h3 className="font-bold text-lg text-slate-800 mb-2 group-hover:text-brand-blue transition-colors line-clamp-2">
                              {event.title}
                            </h3>
                            
                            <p className="text-slate-600 text-sm leading-relaxed mb-3 line-clamp-2">
                              {event.summary}
                            </p>
                            
                            <button className="text-brand-blue hover:text-blue-700 text-sm font-medium transition-colors">
                              Devamını Oku →
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
              
              {/* End Station */}
              <div className="relative flex items-center gap-6 mt-6">
                <div className="relative z-10 flex-shrink-0">
                <div className="w-8 h-8 bg-gradient-to-br from-amber-400 to-orange-500 rounded-full border-4 border-white shadow-lg flex items-center justify-center">
                 <img src="/images/finish.png" alt="Finish" className="w-4 h-4" />
                </div>
                  <div className="absolute -left-10 top-1/2 transform -translate-y-1/2 bg-amber-500 text-white px-2 py-1 rounded-full text-xs font-bold">
                    SON
                  </div>
                </div>
                <div className="text-sm text-slate-600 bg-amber-50 px-4 py-2 rounded-xl border border-amber-200">
                  Zaman Tüneli'ne haber eklendikçe yeni haberler eklendikçe burada görünecek...
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>

      {/* Event Detail Modal - Aynı kalabilir */}
      {selectedEvent && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
          <div className="bg-white rounded-2xl p-6 max-w-2xl w-full mx-4 max-h-[80vh] overflow-y-auto">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-bold text-slate-800">Olay Detayı</h2>
              <button
                onClick={() => setSelectedEvent(null)}
                className="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center"
              >
                ✕
              </button>
            </div>
            
            <div className="space-y-4">
              <img
                src={selectedEvent.image}
                alt={selectedEvent.title}
                className="w-full h-48 object-cover rounded-xl"
              />
              
              <div className="flex items-center gap-2">
              <span className="bg-brand-blue text-white px-2 py-1 rounded-full text-xs font-bold flex items-center gap-1">
                <img src="/images/timeline-logo.png" alt="Timeline" className="w-3 h-3 brightness-0 invert" />
                Zaman Tüneli
              </span>
                <span className="bg-gray-100 text-gray-700 px-2 py-1 rounded-full text-xs font-medium">
                  {selectedEvent.category}
                </span>
                <span className="text-xs text-slate-500">
                  {selectedEvent.date.toLocaleDateString('tr-TR')}
                </span>
              </div>
              
              <h3 className="text-2xl font-bold text-slate-800">
                {selectedEvent.title}
              </h3>
              
              <p className="text-slate-600 leading-relaxed">
                {selectedEvent.summary}
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}