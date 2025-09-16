import React from 'react'
import { NavigationItemProps } from '../types'
import { cn } from '@/lib/utils'
import * as LucideIcons from 'lucide-react'

export const NavigationItem: React.FC<NavigationItemProps> = ({
  item,
  isActive = false,
  onClick
}) => {
  const IconComponent = LucideIcons[item.icon as keyof typeof LucideIcons] as React.ComponentType<{
    className?: string
    size?: number
  }>

  const handleClick = () => {
    onClick?.(item)
  }

  return (
    <button
      onClick={handleClick}
      className={cn(
        'w-full flex items-center gap-3 px-4 py-3 text-left transition-colors group',
        isActive
          ? 'bg-aa-accent/10 text-aa-accent border-r-2 border-aa-accent'
          : 'text-aa-gray-700 hover:bg-aa-gray-50 hover:text-aa-gray-900'
      )}
    >
      <div className="flex-shrink-0">
        {IconComponent && (
          <IconComponent
            className={cn(
              'h-5 w-5 transition-colors',
              isActive ? 'text-aa-accent' : 'text-aa-gray-500 group-hover:text-aa-gray-700'
            )}
          />
        )}
      </div>
      <span className="font-medium truncate">{item.label}</span>
      {item.badge && (
        <span className="ml-auto bg-red-500 text-white text-xs px-2 py-0.5 rounded-full">
          {item.badge}
        </span>
      )}
    </button>
  )
}



