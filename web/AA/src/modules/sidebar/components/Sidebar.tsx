import React from 'react'
import { AccountHeader } from './AccountHeader'
import { NavigationItem } from './NavigationItem'
import { FollowingSection } from './FollowingSection'
import { SidebarProps } from '../types'
import { cn } from '@/lib/utils'
import * as LucideIcons from 'lucide-react'

export const Sidebar: React.FC<SidebarProps> = ({
  user,
  sidebarItems,
  followingItems,
  className
}) => {
  const [activeItem, setActiveItem] = React.useState('home')

  const handleNavigationClick = (item: any) => {
    setActiveItem(item.id)
    // In a real app, you would handle navigation here
    console.log('Navigate to:', item.href)
  }

  return (
    <div className={cn('w-64 bg-white border-r border-aa-gray-200 h-screen flex flex-col', className)}>
      {/* Logo */}
      <div className="p-4 border-b border-aa-gray-200">
        <h1 className="text-xl font-bold text-aa-navy">Yoks News</h1>
      </div>

      {/* Account Header */}
      <AccountHeader user={user} />

      {/* Navigation */}
      <nav className="flex-1 overflow-y-auto">
        <div className="py-2">
          {sidebarItems.map((item) => (
            <NavigationItem
              key={item.id}
              item={item}
              isActive={activeItem === item.id}
              onClick={handleNavigationClick}
            />
          ))}
        </div>

        {/* Following Section */}
        <FollowingSection items={followingItems} />

        {/* Help & Logout */}
        <div className="px-4 py-4 border-t border-aa-gray-200">
          <div className="space-y-1">
            <button className="w-full flex items-center gap-3 px-4 py-2 text-left text-aa-gray-700 hover:bg-aa-gray-50 rounded-lg transition-colors">
              <LucideIcons.HelpCircle className="h-5 w-5 text-aa-gray-500" />
              <span className="font-medium">Help & Information</span>
            </button>
            <button className="w-full flex items-center gap-3 px-4 py-2 text-left text-red-600 hover:bg-red-50 rounded-lg transition-colors">
              <LucideIcons.LogOut className="h-5 w-5" />
              <span className="font-medium">Log Out</span>
            </button>
          </div>
        </div>
      </nav>

      {/* Footer */}
      <div className="p-4 border-t border-aa-gray-200">
        <p className="text-xs text-aa-gray-500 text-center">@2023 Yoks News</p>
      </div>
    </div>
  )
}



