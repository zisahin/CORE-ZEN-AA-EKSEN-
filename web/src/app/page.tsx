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
    <div className="min-h-screen">
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
  )
}