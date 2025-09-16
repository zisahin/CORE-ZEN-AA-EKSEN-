// News Types
export interface NewsItem {
  id: string
  title: string
  content: string
  excerpt: string
  imageUrl: string
  source: string
  category: string
  publishedAt: Date
  author: string
  url: string
  tags: string[]
}

// User & Account Types
export interface UserProfile {
  id: string
  name: string
  avatar?: string
  email: string
  xp: number
  level: number
  league: string
  badge?: string
  joinDate: Date
}

// Sidebar Types
export interface SidebarItem {
  id: string
  label: string
  icon: string
  href?: string
  badge?: string
  children?: SidebarItem[]
}

export interface FollowingItem {
  id: string
  name: string
  avatar?: string
  isLive?: boolean
  type: 'news' | 'person' | 'organization'
}

// Tab Types
export interface TabItem {
  id: string
  label: string
  href: string
  icon?: string
}

// Category Types
export interface Category {
  id: string
  name: string
  icon?: string
  count?: number
}

// Feedback Types
export interface FeedbackPayload {
  newsId: string
  type: 'like' | 'dislike' | 'report' | 'comment'
  comment?: string
  rating?: number
}

export interface FeedbackResponse {
  success: boolean
  message: string
}

// API Response Types
export interface ApiResponse<T> {
  data: T
  success: boolean
  message?: string
  pagination?: {
    page: number
    limit: number
    total: number
    totalPages: number
  }
}

// Component Props Types
export interface BaseComponentProps {
  className?: string
  children?: React.ReactNode
}

// Search Types
export interface SearchResult {
  id: string
  title: string
  type: 'news' | 'person' | 'category'
  url: string
  imageUrl?: string
}

// Trending Types
export interface TrendingTag {
  id: string
  name: string
  count: number
}

export interface TrendingPerson {
  id: string
  name: string
  avatar?: string
  mentionCount: number
}



