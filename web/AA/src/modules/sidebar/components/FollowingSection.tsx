import React from 'react'
import { Avatar } from '@/components/ui/Avatar'
import { FollowingSectionProps } from '../types'
import { cn } from '@/lib/utils'

export const FollowingSection: React.FC<FollowingSectionProps> = ({ items, className }) => {
  const [showAll, setShowAll] = React.useState(false)
  const displayItems = showAll ? items : items.slice(0, 3)

  return (
    <div className={cn('px-4 py-4', className)}>
      <h3 className="text-sm font-semibold text-aa-gray-900 mb-3">Following</h3>
      
      <div className="space-y-2">
        {displayItems.map((item) => (
          <div
            key={item.id}
            className="flex items-center gap-3 p-2 rounded-lg hover:bg-aa-gray-50 transition-colors cursor-pointer"
          >
            <div className="relative">
              <Avatar
                src={item.avatar}
                fallback={item.name}
                size="sm"
              />
              {item.isLive && (
                <div className="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full animate-pulse" />
              )}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-aa-gray-900 truncate">
                {item.name}
              </p>
              {item.isLive && (
                <p className="text-xs text-red-600">Live</p>
              )}
            </div>
          </div>
        ))}
      </div>

      {items.length > 3 && (
        <button
          onClick={() => setShowAll(!showAll)}
          className="mt-3 text-sm text-aa-accent hover:text-blue-700 transition-colors"
        >
          {showAll ? 'Show Less' : 'Show More'}
        </button>
      )}
    </div>
  )
}



