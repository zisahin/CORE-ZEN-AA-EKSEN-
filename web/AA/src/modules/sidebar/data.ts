import { SidebarItem, FollowingItem } from '@/types'

export const SIDEBAR_ITEMS: SidebarItem[] = [
  {
    id: 'home',
    label: 'Home',
    icon: 'Home',
    href: '/'
  },
  {
    id: 'breaking',
    label: 'Breaking News',
    icon: 'Zap',
    href: '/breaking'
  },
  {
    id: 'trending',
    label: 'Trending News',
    icon: 'TrendingUp',
    href: '/trending'
  },
  {
    id: 'forum',
    label: 'Forum',
    icon: 'Users',
    href: '/forum'
  },
  {
    id: 'categories',
    label: 'Categories',
    icon: 'Filter',
    href: '/categories'
  },
  {
    id: 'history',
    label: 'History',
    icon: 'Clock',
    href: '/history'
  },
  {
    id: 'saved',
    label: 'Saved',
    icon: 'Bookmark',
    href: '/saved'
  },
  {
    id: 'collections',
    label: 'Collections',
    icon: 'Layers',
    href: '/collections'
  },
  {
    id: 'settings',
    label: 'Setting',
    icon: 'Settings',
    href: '/settings'
  },
  {
    id: 'account',
    label: 'Your Account',
    icon: 'User',
    href: '/account'
  }
]

export const FOLLOWING_ITEMS: FollowingItem[] = [
  {
    id: 'cnn',
    name: 'CNN Indonesia',
    avatar: 'https://via.placeholder.com/32x32/3b82f6/ffffff?text=CNN',
    type: 'news'
  },
  {
    id: 'vice',
    name: 'Vice',
    avatar: 'https://via.placeholder.com/32x32/ef4444/ffffff?text=V',
    type: 'news'
  },
  {
    id: 'volix',
    name: 'Volix Media',
    avatar: 'https://via.placeholder.com/32x32/10b981/ffffff?text=VM',
    isLive: true,
    type: 'news'
  }
]



