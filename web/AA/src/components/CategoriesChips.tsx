import React from 'react'
import { Chip } from '@/components/ui/Chip'
import { cn } from '@/lib/utils'
import * as LucideIcons from 'lucide-react'

export interface Category {
  id: string
  name: string
  icon?: string
  count?: number
}

export interface CategoriesChipsProps {
  categories: Category[]
  selectedCategory?: string
  onCategorySelect?: (categoryId: string) => void
  className?: string
}

export const CategoriesChips: React.FC<CategoriesChipsProps> = ({
  categories,
  selectedCategory,
  onCategorySelect,
  className
}) => {
  const handleCategoryClick = (categoryId: string) => {
    onCategorySelect?.(categoryId)
  }

  return (
    <div className={cn('flex flex-wrap gap-2', className)}>
      {/* Add Category Button */}
      <Chip
        variant="outline"
        size="sm"
        icon={<LucideIcons.Plus className="h-3 w-3" />}
        onClick={() => console.log('Add category')}
        className="cursor-pointer hover:bg-aa-accent hover:text-white hover:border-aa-accent"
      >
        Add
      </Chip>

      {/* Category Chips */}
      {categories.map((category) => {
        const IconComponent = category.icon 
          ? LucideIcons[category.icon as keyof typeof LucideIcons] as React.ComponentType<{
              className?: string
              size?: number
            }>
          : null

        const isSelected = selectedCategory === category.id

        return (
          <Chip
            key={category.id}
            variant={isSelected ? 'active' : 'default'}
            size="sm"
            icon={IconComponent ? <IconComponent className="h-3 w-3" /> : undefined}
            onClick={() => handleCategoryClick(category.id)}
            className="cursor-pointer transition-colors"
          >
            {category.name}
            {category.count && (
              <span className="ml-1 text-xs opacity-75">
                ({category.count})
              </span>
            )}
          </Chip>
        )
      })}
    </div>
  )
}



