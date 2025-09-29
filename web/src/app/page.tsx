import TopNav from '@/components/TopNav'
import TickerBar from '@/components/TickerBar'
import LeftSidebar from '@/components/LeftSidebar'
import MainHero from '@/components/MainHero'
import RightTimeline from '@/components/RightTimeline'
import NewsGrid from '@/components/NewsGrid'

export default function Home() {
  return (
    <div className="min-h-screen">
      <TopNav />
      <TickerBar />
      
      <div className="flex">
        {/* Left Sidebar */}
        <LeftSidebar />
        
        {/* Main Content */}
        <main className="flex-1 min-h-screen overflow-hidden">
          <div className="p-6 space-y-8">
            <MainHero />
            <NewsGrid />
          </div>
        </main>
        
        {/* Right Timeline */}
        <RightTimeline />
      </div>
    </div>
  )
}