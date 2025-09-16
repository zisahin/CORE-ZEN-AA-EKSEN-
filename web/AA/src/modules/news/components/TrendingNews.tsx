import React from 'react'
import { NewsList } from './NewsList'
import { TrendingNewsProps } from '../types'
import { MOCK_NEWS } from '../data'
import { cn } from '@/lib/utils'

export const TrendingNews: React.FC<TrendingNewsProps> = ({ className }) => {
  const handleFeedback = (payload: any) => {
    console.log('Feedback received:', payload)
    // In a real app, this would send the feedback to your API
  }

  return (
    <div className={cn('space-y-6', className)}>
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-aa-gray-900">Trending News</h2>
        <button className="text-sm text-aa-accent hover:text-blue-700 transition-colors">
          View All
        </button>
      </div>
      
      <NewsList
        news={MOCK_NEWS}
        onFeedback={handleFeedback}
      />
    </div>
  )
}



