'use client'

import React, { useState } from 'react'
import { Sidebar, SIDEBAR_ITEMS, FOLLOWING_ITEMS } from '@/modules/sidebar'
import { TrendingNews } from '@/modules/news'
import { TabsSection, TabItem } from '@/components/TabsSection'
import { CategoriesChips, Category } from '@/components/CategoriesChips'
import { Input } from '@/components/ui/Input'
import { UserProfile } from '@/types'
import { cn } from '@/lib/utils'
import * as LucideIcons from 'lucide-react'

// Mock user data
const MOCK_USER: UserProfile = {
  id: '1',
  name: 'John Doe',
  avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=64&h=64&fit=crop&crop=face',
  email: 'john@example.com',
  xp: 1250,
  level: 13,
  league: 'Gold',
  badge: 'News Enthusiast',
  joinDate: new Date('2023-01-15')
}

// Categories data
const CATEGORIES: Category[] = [
  { id: 'all', name: 'All', count: 1247 },
  { id: 'politics', name: 'Politics', icon: 'Building', count: 234 },
  { id: 'global', name: 'Global', icon: 'Globe', count: 189 },
  { id: 'business', name: 'Business', icon: 'Briefcase', count: 156 },
  { id: 'entertainment', name: 'Entertainment', icon: 'Film', count: 298 },
  { id: 'sport', name: 'Sport', icon: 'Zap', count: 167 },
  { id: 'technology', name: 'Technology', icon: 'Cpu', count: 203 }
]

export default function HomePage() {
  const [selectedCategory, setSelectedCategory] = useState('all')
  const [searchQuery, setSearchQuery] = useState('')

  // Tab content components
  const FollowingContent = () => (
    <div className="space-y-6">
      <div className="text-center py-12 text-aa-gray-500">
        <LucideIcons.Users className="mx-auto h-12 w-12 mb-4" />
        <p className="text-lg font-medium">Following Feed</p>
        <p className="text-sm">News from sources you follow will appear here.</p>
      </div>
    </div>
  )

  const ForumContent = () => (
    <div className="space-y-6">
      <div className="text-center py-12 text-aa-gray-500">
        <LucideIcons.MessageSquare className="mx-auto h-12 w-12 mb-4" />
        <p className="text-lg font-medium">Community Forum</p>
        <p className="text-sm">Join discussions and share your thoughts.</p>
      </div>
    </div>
  )

  const ForYouContent = () => (
    <div className="space-y-6">
      <TrendingNews />
    </div>
  )

  const tabs: TabItem[] = [
    {
      id: 'following',
      label: 'Following',
      icon: 'Users',
      content: <FollowingContent />
    },
    {
      id: 'forum',
      label: 'Forum',
      icon: 'MessageSquare',
      content: <ForumContent />
    },
    {
      id: 'foryou',
      label: 'For You',
      icon: 'Heart',
      content: <ForYouContent />
    }
  ]

  return (
    <div className="min-h-screen bg-aa-gray-50">
      <div className="flex">
        {/* Sidebar */}
        <Sidebar
          user={MOCK_USER}
          sidebarItems={SIDEBAR_ITEMS}
          followingItems={FOLLOWING_ITEMS}
        />

        {/* Main Content */}
        <main className="flex-1 min-h-screen">
          <div className="max-w-4xl mx-auto p-6">
            {/* Header */}
            <div className="mb-8">
              {/* Search Bar */}
              <div className="mb-6">
                <Input
                  placeholder="Search..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  icon={<LucideIcons.Search className="h-5 w-5" />}
                  className="max-w-md mx-auto"
                />
              </div>

              {/* Trending Hashtags */}
              <div className="flex flex-wrap gap-2 justify-center mb-6">
                {['#Trending News', '#Maling', '#mobilterbang', '#manusiasapi'].map((tag) => (
                  <span
                    key={tag}
                    className="px-3 py-1 bg-aa-gray-100 text-aa-gray-700 rounded-full text-sm hover:bg-aa-gray-200 transition-colors cursor-pointer"
                  >
                    {tag}
                  </span>
                ))}
              </div>
            </div>

            {/* Tabs */}
            <TabsSection tabs={tabs} defaultTab="foryou" />

            {/* Categories */}
            <div className="mb-8">
              <CategoriesChips
                categories={CATEGORIES}
                selectedCategory={selectedCategory}
                onCategorySelect={setSelectedCategory}
              />
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}



