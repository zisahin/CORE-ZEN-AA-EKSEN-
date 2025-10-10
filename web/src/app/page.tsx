'use client'
import { useState } from 'react'
import TopNav from '@/components/TopNav'
import TickerBar from '@/components/TickerBar'
import LeftSidebar from '@/components/LeftSidebar'
import MainHero from '@/components/MainHero'
import RightTimeline from '@/components/RightTimeline'
import NewsGrid from '@/components/NewsGrid'
import AIBubble from '@/components/AIBubble'  
import MapModal from '@/components/MapModal'

export default function Home() {
  const [isMapModalOpen, setIsMapModalOpen] = useState(false)
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-blue-950 to-slate-950 relative overflow-hidden">
      {/* Dekoratif Background Elements */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-20 left-20 w-96 h-96 bg-purple-600/10 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute bottom-20 right-20 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '700ms' }}></div>
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-purple-500/5 rounded-full blur-3xl"></div>
      </div>

      {/* Content */}
      <div className="relative z-10">
        <TopNav />
        <TickerBar />
        
        <div className="flex">
          <LeftSidebar 
            onOpenMap={() => setIsMapModalOpen(true)}
          />
          <main className="flex-1 min-h-screen overflow-hidden">
            <div className="p-6 space-y-8">
              <MainHero />
              <NewsGrid />
            </div>
          </main>
          <RightTimeline />
        </div>

        {/* AI Bubble - Sağ Alt Köşe */}
        <AIBubble />
        
        {/* Haber Haritası Modal */}
        {isMapModalOpen && (
          <MapModal onClose={() => setIsMapModalOpen(false)} />
        )}
      </div>
    </div>
  )
}