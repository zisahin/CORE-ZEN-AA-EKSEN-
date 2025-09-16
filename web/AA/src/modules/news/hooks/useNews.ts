import { useState, useEffect } from 'react'
import { NewsItem, FeedbackPayload } from '@/types'
import { NewsApiService } from '../services/api'

export interface UseNewsOptions {
  page?: number
  limit?: number
  category?: string
  searchQuery?: string
}

export interface UseNewsReturn {
  news: NewsItem[]
  loading: boolean
  error: string | null
  hasMore: boolean
  loadMore: () => void
  refresh: () => void
  submitFeedback: (payload: FeedbackPayload) => Promise<void>
}

export const useNews = (options: UseNewsOptions = {}): UseNewsReturn => {
  const [news, setNews] = useState<NewsItem[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [hasMore, setHasMore] = useState(true)
  const [currentPage, setCurrentPage] = useState(options.page || 1)

  const fetchNews = async (page: number, append = false) => {
    try {
      setLoading(true)
      setError(null)
      
      const response = await NewsApiService.getNews(
        page,
        options.limit || 10,
        options.category
      )

      if (response.success) {
        if (append) {
          setNews(prev => [...prev, ...response.data])
        } else {
          setNews(response.data)
        }
        
        setHasMore(response.pagination ? page < response.pagination.totalPages : false)
      } else {
        setError('Failed to fetch news')
      }
    } catch (err) {
      setError('An error occurred while fetching news')
      console.error('News fetch error:', err)
    } finally {
      setLoading(false)
    }
  }

  const loadMore = () => {
    if (!loading && hasMore) {
      const nextPage = currentPage + 1
      setCurrentPage(nextPage)
      fetchNews(nextPage, true)
    }
  }

  const refresh = () => {
    setCurrentPage(1)
    fetchNews(1, false)
  }

  const submitFeedback = async (payload: FeedbackPayload) => {
    try {
      const response = await NewsApiService.submitFeedback(payload)
      if (!response.success) {
        throw new Error(response.message || 'Failed to submit feedback')
      }
    } catch (err) {
      console.error('Feedback submission error:', err)
      throw err
    }
  }

  useEffect(() => {
    fetchNews(currentPage, false)
  }, [options.category, options.searchQuery])

  return {
    news,
    loading,
    error,
    hasMore,
    loadMore,
    refresh,
    submitFeedback
  }
}



