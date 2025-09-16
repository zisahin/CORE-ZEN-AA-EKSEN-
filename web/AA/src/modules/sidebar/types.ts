import { SidebarItem, FollowingItem, UserProfile } from '@/types'

export interface SidebarProps {
  user: UserProfile
  sidebarItems: SidebarItem[]
  followingItems: FollowingItem[]
  className?: string
}

export interface AccountHeaderProps {
  user: UserProfile
  className?: string
}

export interface NavigationItemProps {
  item: SidebarItem
  isActive?: boolean
  onClick?: (item: SidebarItem) => void
}

export interface FollowingSectionProps {
  items: FollowingItem[]
  className?: string
}



