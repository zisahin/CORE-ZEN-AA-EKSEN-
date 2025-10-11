'use client';

import { useAuth } from '@/context/AuthContext';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { logoutUser } from '@/services/authService';
import ProfileHeader from '@/components/profile/ProfileHeader';
import ProfileStats from '@/components/profile/ProfileStats';
import ProfileBadges from '@/components/profile/ProfileBadges';

export default function ProfilePage() {
  const { user, userProfile, gameStats, loading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    // Demo mode'da login'e yönlendirme - sadece profil varsa kontrol et
    if (!loading && !userProfile) {
      router.push('/auth/login');
    }
  }, [userProfile, loading, router]);

  const handleLogout = async () => {
    try {
      await logoutUser();
    } catch (error) {
      console.log('Demo mode - logout simülasyonu');
    }
    router.push('/');
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-950 via-blue-950 to-slate-950 flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-purple-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-white/60">Yükleniyor...</p>
        </div>
      </div>
    );
  }

  if (!userProfile) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-blue-950 to-slate-950 py-12 px-4">
      {/* Dekoratif Background */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-40 left-10 w-96 h-96 bg-purple-600/5 rounded-full blur-3xl"></div>
        <div className="absolute bottom-40 right-10 w-96 h-96 bg-blue-600/5 rounded-full blur-3xl"></div>
      </div>

      <div className="max-w-7xl mx-auto relative z-10">
        {/* Top Bar */}
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-4xl font-bold text-white">Profilim</h1>
          <button
            onClick={handleLogout}
            className="flex items-center gap-2 px-6 py-2.5 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors font-medium"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            Çıkış Yap
          </button>
        </div>

        {/* Profile Header */}
        <div className="mb-8">
          <ProfileHeader profile={userProfile} />
        </div>

        {/* Stats */}
        <div className="mb-8">
          <ProfileStats profile={userProfile} gameStats={gameStats} />
        </div>

        {/* Badges */}
        <div className="mb-8">
          <ProfileBadges badges={userProfile.badges} />
        </div>

        {/* Günlük Görevler Preview */}
        <div className="bg-gradient-to-br from-slate-800 to-slate-900 rounded-xl p-6 border border-white/10">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-xl font-bold text-white flex items-center gap-2">
              <svg className="w-6 h-6 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
              </svg>
              Günlük Görevler
            </h3>
            <span className="text-sm text-white/60">
              {userProfile.completedTasks.length} / {userProfile.dailyTasks.length} tamamlandı
            </span>
          </div>

          {userProfile.dailyTasks.length === 0 ? (
            <div className="text-center py-8 text-white/60">
              <p>Henüz günlük görev yok</p>
            </div>
          ) : (
            <div className="space-y-3">
              {userProfile.dailyTasks.slice(0, 3).map((taskId, index) => (
                <div
                  key={index}
                  className="flex items-center gap-3 p-3 bg-white/5 rounded-lg border border-white/10"
                >
                  <div className="w-5 h-5 rounded border-2 border-purple-500"></div>
                  <span className="text-white/80 flex-1">Görev {index + 1}</span>
                  <span className="text-purple-400 text-sm font-medium">+20 XP</span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

