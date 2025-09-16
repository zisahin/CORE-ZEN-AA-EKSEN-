import { NewsItem, FeedbackPayload } from '@/types'

export interface NewsCardProps {
  news: NewsItem
  onFeedback?: (payload: FeedbackPayload) => void
  className?: string
}

export interface NewsDetailProps {
  news: NewsItem
  onFeedback?: (payload: FeedbackPayload) => void
  className?: string
}

export interface NewsListProps {
  news: NewsItem[]
  onFeedback?: (payload: FeedbackPayload) => void
  className?: string
}

export interface FeedbackModalProps {
  isOpen: boolean
  onClose: () => void
  newsId: string
  onSubmit: (payload: FeedbackPayload) => void
}

export interface TrendingNewsProps {
  className?: string
}



