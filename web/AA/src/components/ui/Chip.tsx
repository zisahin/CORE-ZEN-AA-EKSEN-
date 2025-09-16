import React from 'react'
import { cn } from '@/lib/utils'

export interface ChipProps {
  variant?: 'default' | 'active' | 'outline'
  size?: 'sm' | 'md'
  icon?: React.ReactNode
  onRemove?: () => void
  children: React.ReactNode
  className?: string
}

export const Chip: React.FC<ChipProps> = ({
  variant = 'default',
  size = 'md',
  icon,
  onRemove,
  children,
  className
}) => {
  const baseClasses = 'inline-flex items-center gap-1 rounded-full font-medium transition-colors'
  
  const variants = {
    default: 'bg-aa-gray-100 text-aa-gray-700 hover:bg-aa-gray-200',
    active: 'bg-aa-accent text-white',
    outline: 'border border-aa-gray-300 text-aa-gray-700 hover:bg-aa-gray-50'
  }
  
  const sizes = {
    sm: 'px-2 py-1 text-xs',
    md: 'px-3 py-1.5 text-sm'
  }
  
  return (
    <span
      className={cn(
        baseClasses,
        variants[variant],
        sizes[size],
        className
      )}
    >
      {icon && <span className="flex-shrink-0">{icon}</span>}
      <span>{children}</span>
      {onRemove && (
        <button
          onClick={onRemove}
          className="ml-1 flex-shrink-0 hover:bg-black/10 rounded-full p-0.5"
          aria-label="Remove"
        >
          <svg className="h-3 w-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      )}
    </span>
  )
}



