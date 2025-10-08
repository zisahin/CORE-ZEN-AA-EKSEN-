'use client'
import { usePathname } from 'next/navigation'
import LeftSidebar from '@/components/LeftSidebar'
import TurkeyNewsMap from '@/components/TurkeyNewsMap'
import TopNav from '@/components/TopNav'

export default function NewsMapPage() {
  const pathname = usePathname()
  
  return (
    <div className="min-h-screen">
      <TopNav />
      <div className="flex">
        <LeftSidebar currentPath={pathname} />
        <div className="flex-1">
          <TurkeyNewsMap />
        </div>
      </div>
    </div>
  )
}