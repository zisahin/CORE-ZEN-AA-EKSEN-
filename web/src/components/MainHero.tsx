'use client'

import { useState, useEffect } from 'react'

const heroNews = [
  {
    id: 1,
    title: "Türkiye'nin İlk Yapay Zeka Destekli Haber Platformu AA Eksen Yayında",
    summary: "Anadolu Ajansı'nın yeni nesil haber platformu AA Eksen, yapay zeka teknolojileri ile okuyuculara kişiselleştirilmiş haber deneyimi sunuyor.",
    image: "/images/istanbul.jpg",
    category: "Teknoloji",
    readTime: "3 dk",
    xp: 25
  },
  {
    id: 2,
    title: "Cumhurbaşkanı Erdoğan: 'Teknolojide Milli Hamle Başlatıyoruz'",
    summary: "Cumhurbaşkanı Erdoğan, teknoloji alanında yeni yatırım programını açıkladı.",
    image: "/images/istanbul.jpg",
    category: "Gündem",
    readTime: "5 dk", 
    xp: 30
  }
]

export default function MainHero() {
  const [currentNews, setCurrentNews] = useState(0)
  const [isReading, setIsReading] = useState(false)

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentNews((prev) => (prev + 1) % heroNews.length)
    }, 8000)
    return () => clearInterval(timer)
  }, [])

  const handleReadClick = () => {
    setIsReading(true)
    // Simulate reading time
    setTimeout(() => setIsReading(false), 2000)
  }

  const currentItem = heroNews[currentNews]
  const [savedNews, setSavedNews] = useState<number[]>([])
  return (
    <section className="relative">
      {/* Main Hero */}
      <div className="relative h-96 rounded-2xl overflow-hidden bg-gradient-to-br from-brand-blue via-blue-800 to-blue-900 shadow-2xl">
        <div 
          className="absolute inset-0 bg-cover bg-center opacity-20"
          style={{ backgroundImage: `url("${currentItem.image}")` }}
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent" />
        
        {/* Content */}
        <div className="relative z-10 p-8 h-full flex flex-col justify-end">
          <div className="max-w-2xl">
            <div className="flex items-center gap-4 mb-4">
              <span className="bg-white/20 backdrop-blur text-white px-3 py-1 rounded-full text-sm font-medium">
                {currentItem.category}
              </span>
              <span className="text-white/80 text-sm">
                 {currentItem.readTime} okuma
              </span>
              <span className="bg-amber-500 text-white px-2 py-1 rounded-full text-xs font-bold">
                +{currentItem.xp} XP
              </span>
            </div>
            
            <h1 className="text-3xl md:text-4xl font-bold text-white mb-4 leading-tight">
              {currentItem.title}
            </h1>
            
            <p className="text-white/90 text-lg mb-6 leading-relaxed">
              {currentItem.summary}
            </p>
            
            <div className="flex items-center gap-4">
              <button 
                onClick={handleReadClick}
                disabled={isReading}
                className="bg-white text-brand-blue px-6 py-3 rounded-xl font-bold shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200 disabled:opacity-50"
              >
                {isReading ? ' Okunuyor...' : ' Haberi Oku'}
              </button>
              
              <button 
                onClick={() => {
                  if (savedNews.includes(currentItem.id)) {
                    setSavedNews(savedNews.filter(id => id !== currentItem.id))
                  } else {
                    setSavedNews([...savedNews, currentItem.id])
                  }
                }}
                className="bg-white/20 backdrop-blur text-white px-4 py-3 rounded-xl font-medium hover:bg-white/30 transition-all duration-200 flex items-center gap-2"
              >
                <img 
                  src={savedNews.includes(currentItem.id)
                    ? "/images/save-filled.png"
                    : "/images/save-empty.png"
                  }
                  alt="Kaydet" 
                  className="w-5 h-5"
                />
                {savedNews.includes(currentItem.id) ? 'Kaydedildi' : 'Kaydet'}
              </button>
              
              <button className="bg-white/20 backdrop-blur text-white pl-3 pr-4 py-2 rounded-xl font-medium hover:bg-white/30 transition-all duration-200 flex items-center gap-1">
                <img src="/images/share-white.png" alt="Paylaş" className="w-5 h-5" />
                Paylaş
              </button>
            </div>
          </div>
        </div>
        
        {/* Progress Indicators */}
        <div className="absolute bottom-4 right-4 flex gap-2">
          {heroNews.map((_, index) => (
            <button
              key={index}
              onClick={() => setCurrentNews(index)}
              className={`w-3 h-3 rounded-full transition-all duration-300 ${
                index === currentNews ? 'bg-white' : 'bg-white/40'
              }`}
            />
          ))}
        </div>
      </div>

      {/* Quick Stats */}
     {/* <div className="grid grid-cols-3 gap-4 mt-6">
        <div className="bg-white/80 backdrop-blur rounded-xl p-4 text-center border border-cream-strong shadow-lg">
          <div className="text-2xl font-bold text-brand-blue">1,250</div>
          <div className="text-sm text-slate-600">Toplam XP</div>
        </div>
        <div className="bg-white/80 backdrop-blur rounded-xl p-4 text-center border border-cream-strong shadow-lg">
          <div className="text-2xl font-bold text-green-600">47</div>
          <div className="text-sm text-slate-600">Okunan Haber</div>
        </div>
        <div className="bg-white/80 backdrop-blur rounded-xl p-4 text-center border border-cream-strong shadow-lg">
          <div className="text-2xl font-bold text-amber-600">12</div>
          <div className="text-sm text-slate-600">Seviye</div>
        </div>
      </div>"*/}
    </section>
  )
}




