import React from 'react'
import { Avatar } from '@/components/ui/Avatar'
import { Badge } from '@/components/ui/Badge'
import { AccountHeaderProps } from '../types'
import { cn } from '@/lib/utils'

export const AccountHeader: React.FC<AccountHeaderProps> = ({ user, className }) => {
  const getLeagueColor = (league: string) => {
    switch (league.toLowerCase()) {
      case 'bronze': return 'bg-yellow-100 text-yellow-800'
      case 'silver': return 'bg-gray-100 text-gray-800'
      case 'gold': return 'bg-yellow-100 text-yellow-800'
      case 'platinum': return 'bg-purple-100 text-purple-800'
      case 'diamond': return 'bg-blue-100 text-blue-800'
      default: return 'bg-gray-100 text-gray-800'
    }
  }

  const getLevelFromXP = (xp: number) => {
    return Math.floor(xp / 100) + 1
  }

  const getXPProgress = (xp: number) => {
    const currentLevelXP = (Math.floor(xp / 100)) * 100
    const nextLevelXP = (Math.floor(xp / 100) + 1) * 100
    const progress = ((xp - currentLevelXP) / (nextLevelXP - currentLevelXP)) * 100
    return Math.min(progress, 100)
  }

  return (
    <div className={cn('p-4 border-b border-aa-gray-200', className)}>
      {/* User Info */}
      <div className="flex items-center gap-3 mb-4">
        <Avatar
          src={user.avatar}
          fallback={user.name}
          size="lg"
        />
        <div className="flex-1 min-w-0">
          <h3 className="font-semibold text-aa-gray-900 truncate">
            {user.name}
          </h3>
          <p className="text-sm text-aa-gray-600">
            Level {getLevelFromXP(user.xp)}
          </p>
        </div>
      </div>

      {/* Badge */}
      {user.badge && (
        <div className="mb-3">
          <Badge variant="info" size="sm">
            {user.badge}
          </Badge>
        </div>
      )}

      {/* XP Progress */}
      <div className="mb-3">
        <div className="flex justify-between items-center mb-1">
          <span className="text-xs font-medium text-aa-gray-600">XP</span>
          <span className="text-xs text-aa-gray-500">{user.xp}</span>
        </div>
        <div className="w-full bg-aa-gray-200 rounded-full h-2">
          <div
            className="bg-aa-accent h-2 rounded-full transition-all duration-300"
            style={{ width: `${getXPProgress(user.xp)}%` }}
          />
        </div>
      </div>

      {/* League */}
      <div className="flex items-center justify-between">
        <span className="text-xs font-medium text-aa-gray-600">League</span>
        <Badge
          variant="default"
          size="sm"
          className={cn('text-xs', getLeagueColor(user.league))}
        >
          {user.league}
        </Badge>
      </div>
    </div>
  )
}



