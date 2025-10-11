'use client';

import { UserProfile, calculateLevel, getXpForNextLevel } from '@/services/authService';

interface ProfileHeaderProps {
  profile: UserProfile;
}

export default function ProfileHeader({ profile }: ProfileHeaderProps) {
  const currentLevel = calculateLevel(profile.totalXp);
  const xpForNextLevel = getXpForNextLevel(profile.totalXp);
  const currentLevelXp = profile.totalXp % 100;
  const progressPercentage = (currentLevelXp / 100) * 100;

  return (
    <div className="relative bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 rounded-2xl p-8 shadow-2xl overflow-hidden">
      {/* Dekoratif Background */}
      <div className="absolute inset-0 bg-gradient-to-br from-purple-900/20 to-transparent"></div>
      
      {/* Premium Badge */}
      {profile.isPremium && (
        <div className="absolute top-6 right-6 z-10">
          <div className="flex items-center gap-2 bg-gradient-to-r from-yellow-500 to-yellow-600 px-4 py-1.5 rounded-full">
            <svg className="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
              <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
            </svg>
            <span className="text-white text-sm font-bold">PREMIUM</span>
          </div>
        </div>
      )}

      <div className="relative z-10 flex items-start gap-6">
        {/* Avatar */}
        <div className="relative">
          <div className="w-24 h-24 rounded-full bg-gradient-to-br from-purple-600 to-blue-600 p-1">
            <div className="w-full h-full rounded-full bg-slate-800 flex items-center justify-center">
              {profile.photoUrl ? (
                <img 
                  src={profile.photoUrl} 
                  alt={profile.username}
                  className="w-full h-full rounded-full object-cover"
                />
              ) : (
                <span className="text-3xl font-bold text-white">
                  {profile.username[0].toUpperCase()}
                </span>
              )}
            </div>
          </div>
          
          {/* Level Badge */}
          <div className="absolute -bottom-2 -right-2 bg-gradient-to-r from-purple-600 to-purple-700 rounded-full w-12 h-12 flex items-center justify-center border-4 border-slate-900">
            <span className="text-white font-bold text-sm">{currentLevel}</span>
          </div>
        </div>

        {/* Kullanıcı Bilgileri */}
        <div className="flex-1">
          <h2 className="text-3xl font-bold text-white mb-1">{profile.username}</h2>
          <p className="text-white/60 mb-4">{profile.email}</p>

          {/* XP Progress Bar */}
          <div className="space-y-2">
            <div className="flex items-center justify-between text-sm">
              <span className="text-white/80">Level {currentLevel}</span>
              <span className="text-purple-400 font-semibold">{profile.totalXp} XP</span>
            </div>
            
            <div className="relative h-3 bg-white/10 rounded-full overflow-hidden">
              <div 
                className="absolute top-0 left-0 h-full bg-gradient-to-r from-purple-600 to-purple-400 rounded-full transition-all duration-500"
                style={{ width: `${progressPercentage}%` }}
              >
                <div className="absolute inset-0 bg-white/20 animate-pulse"></div>
              </div>
            </div>
            
            <div className="text-xs text-white/60 text-right">
              {xpForNextLevel} XP bir sonraki seviyeye
            </div>
          </div>
        </div>
      </div>

      {/* Dekoratif Alt Wave */}
      <div className="absolute bottom-0 left-0 right-0 h-24">
        <svg className="absolute bottom-0 w-full h-16" viewBox="0 0 1440 120" preserveAspectRatio="none">
          <path fill="#6b21a8" fillOpacity="0.3" d="M0,64L80,69.3C160,75,320,85,480,80C640,75,800,53,960,48C1120,43,1280,53,1360,58.7L1440,64L1440,120L1360,120C1280,120,1120,120,960,120C800,120,640,120,480,120C320,120,160,120,80,120L0,120Z"></path>
        </svg>
      </div>
    </div>
  );
}

