'use client'
import { usePathname } from 'next/navigation'
import LeftSidebar from '@/components/LeftSidebar'
import TurkeyNewsMap from '@/components/TurkeyNewsMap'
import TopNav from '@/components/TopNav'

export default function NewsMapPage() {
  const pathname = usePathname()
  
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-blue-950 to-slate-950 relative overflow-hidden">
      {/* Dekoratif Background */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-20 left-20 w-96 h-96 bg-purple-600/10 rounded-full blur-3xl animate-pulse"></div>
        <div className="absolute bottom-20 right-20 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '700ms' }}></div>
      </div>

      <div className="relative z-10">
        <TopNav />
        <div className="flex">
          <LeftSidebar currentPath={pathname} />
          <div className="flex-1">
            <TurkeyNewsMap />
          </div>
        </div>
      </div>
    </div>
  )
}