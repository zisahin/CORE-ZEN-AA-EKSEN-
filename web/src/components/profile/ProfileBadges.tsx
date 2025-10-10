'use client';

import { UserBadge } from '@/services/authService';

interface ProfileBadgesProps {
  badges: UserBadge[];
}

const rarityColors = {
  common: {
    bg: 'from-gray-500 to-gray-600',
    border: 'border-gray-500/50',
    glow: 'shadow-gray-500/20'
  },
  rare: {
    bg: 'from-blue-500 to-blue-600',
    border: 'border-blue-500/50',
    glow: 'shadow-blue-500/20'
  },
  epic: {
    bg: 'from-purple-500 to-purple-600',
    border: 'border-purple-500/50',
    glow: 'shadow-purple-500/20'
  },
  legendary: {
    bg: 'from-yellow-500 to-orange-600',
    border: 'border-yellow-500/50',
    glow: 'shadow-yellow-500/30'
  }
};

export default function ProfileBadges({ badges }: ProfileBadgesProps) {
  if (badges.length === 0) {
    return (
      <div className="bg-gradient-to-br from-slate-800 to-slate-900 rounded-xl p-12 border border-white/10 text-center">
        <div className="w-20 h-20 mx-auto mb-4 rounded-full bg-white/5 flex items-center justify-center">
          <svg className="w-10 h-10 text-white/30" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
          </svg>
        </div>
        <p className="text-white/60">Henüz rozet kazanmadınız</p>
        <p className="text-white/40 text-sm mt-2">Görevleri tamamlayarak rozetler kazanın!</p>
      </div>
    );
  }

  return (
    <div className="bg-gradient-to-br from-slate-800 to-slate-900 rounded-xl p-6 border border-white/10">
      <h3 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
        <svg className="w-6 h-6 text-purple-400" fill="currentColor" viewBox="0 0 20 20">
          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
        </svg>
        Rozetler
        <span className="text-sm font-normal text-white/60">({badges.length})</span>
      </h3>

      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {badges.map((badge, index) => {
          const colors = rarityColors[badge.rarity as keyof typeof rarityColors];
          
          return (
            <div
              key={index}
              className={`relative group cursor-pointer transition-all hover:scale-105`}
            >
              {/* Badge Card */}
              <div className={`bg-slate-900 rounded-xl p-4 border ${colors.border} shadow-lg ${colors.glow} transition-all`}>
                {/* Badge Icon/Avatar */}
                <div className={`w-16 h-16 mx-auto mb-3 rounded-full bg-gradient-to-br ${colors.bg} flex items-center justify-center relative`}>
                  {badge.iconUrl ? (
                    <img src={badge.iconUrl} alt={badge.name} className="w-10 h-10" />
                  ) : (
                    <svg className="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                  )}
                  
                  {/* Rarity Indicator */}
                  <div className={`absolute -top-1 -right-1 w-4 h-4 rounded-full bg-gradient-to-br ${colors.bg} border-2 border-slate-900`}></div>
                </div>

                {/* Badge Name */}
                <h4 className="text-white text-sm font-semibold text-center mb-1 whitespace-pre-line leading-tight">
                  {badge.name}
                </h4>

                {/* Rarity */}
                <div className="text-center">
                  <span className={`inline-block px-2 py-0.5 rounded-full text-xs font-medium bg-gradient-to-r ${colors.bg} text-white capitalize`}>
                    {badge.rarity}
                  </span>
                </div>
              </div>

              {/* Tooltip */}
              <div className="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-3 py-2 bg-slate-800 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none whitespace-nowrap z-10 border border-white/10">
                {badge.description}
                <div className="absolute top-full left-1/2 transform -translate-x-1/2 -mt-1 border-4 border-transparent border-t-slate-800"></div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

