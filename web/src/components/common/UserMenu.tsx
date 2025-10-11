'use client';

import { useState, useRef, useEffect } from 'react';
import { useAuth } from '@/context/AuthContext';
import { useRouter } from 'next/navigation';
import { logoutUser, calculateLevel } from '@/services/authService';
import Link from 'next/link';

export default function UserMenu() {
  const { user, userProfile } = useAuth();
  const [isOpen, setIsOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);
  const router = useRouter();

  // Dışarı tıklandığında menüyü kapat
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLogout = async () => {
    try {
      await logoutUser();
    } catch (error) {
      console.log('Demo mode - logout simülasyonu');
    }
    router.push('/');
    setIsOpen(false);
  };

  if (!userProfile) {
    return (
      <div className="flex items-center gap-3">
        <Link
          href="/auth/login"
          className="px-4 py-2 text-white hover:text-purple-400 transition-colors font-medium"
        >
          Giriş Yap
        </Link>
        <Link
          href="/auth/register"
          className="px-4 py-2 bg-gradient-to-r from-purple-600 to-purple-700 hover:from-purple-700 hover:to-purple-800 text-white rounded-lg transition-all font-medium"
        >
          Kayıt Ol
        </Link>
      </div>
    );
  }

  const currentLevel = calculateLevel(userProfile.totalXp);

  return (
    <div className="relative" ref={menuRef}>
      {/* User Avatar Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-white/10 transition-colors group"
      >
        {/* Avatar */}
        <div className="relative">
          <div className="w-10 h-10 rounded-full bg-gradient-to-br from-purple-600 to-blue-600 p-0.5">
            <div className="w-full h-full rounded-full bg-slate-800 flex items-center justify-center">
              {userProfile.photoUrl ? (
                <img
                  src={userProfile.photoUrl}
                  alt={userProfile.username}
                  className="w-full h-full rounded-full object-cover"
                />
              ) : (
                <span className="text-sm font-bold text-white">
                  {userProfile.username[0].toUpperCase()}
                </span>
              )}
            </div>
          </div>
          
          {/* Level Badge */}
          <div className="absolute -bottom-1 -right-1 bg-purple-600 rounded-full w-5 h-5 flex items-center justify-center border-2 border-slate-900">
            <span className="text-white font-bold text-[10px]">{currentLevel}</span>
          </div>
        </div>

        {/* Username & XP */}
        <div className="hidden md:block text-left">
          <div className="text-white font-medium text-sm">{userProfile.username}</div>
          <div className="text-purple-400 text-xs font-semibold">{userProfile.totalXp} XP</div>
        </div>

        {/* Chevron */}
        <svg
          className={`w-4 h-4 text-white/60 transition-transform ${isOpen ? 'rotate-180' : ''}`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
        </svg>
      </button>

      {/* Dropdown Menu */}
      {isOpen && (
        <div className="absolute right-0 mt-2 w-64 bg-slate-900 border border-white/10 rounded-xl shadow-2xl overflow-hidden z-50">
          {/* User Info Header */}
          <div className="bg-gradient-to-br from-purple-900/40 to-blue-900/40 p-4 border-b border-white/10">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-full bg-gradient-to-br from-purple-600 to-blue-600 p-0.5">
                <div className="w-full h-full rounded-full bg-slate-800 flex items-center justify-center">
                  {userProfile.photoUrl ? (
                    <img
                      src={userProfile.photoUrl}
                      alt={userProfile.username}
                      className="w-full h-full rounded-full object-cover"
                    />
                  ) : (
                    <span className="text-lg font-bold text-white">
                      {userProfile.username[0].toUpperCase()}
                    </span>
                  )}
                </div>
              </div>
              <div>
                <div className="text-white font-semibold">{userProfile.username}</div>
                <div className="text-white/60 text-xs">{userProfile.email}</div>
              </div>
            </div>

            {/* XP Bar */}
            <div className="mt-3">
              <div className="flex items-center justify-between text-xs text-white/80 mb-1">
                <span>Level {currentLevel}</span>
                <span className="text-purple-400 font-semibold">{userProfile.totalXp} XP</span>
              </div>
              <div className="h-2 bg-white/10 rounded-full overflow-hidden">
                <div
                  className="h-full bg-gradient-to-r from-purple-600 to-purple-400"
                  style={{ width: `${(userProfile.totalXp % 100)}%` }}
                ></div>
              </div>
            </div>
          </div>

          {/* Menu Items */}
          <div className="p-2">
            <Link
              href="/profile"
              onClick={() => setIsOpen(false)}
              className="flex items-center gap-3 px-3 py-2.5 text-white/80 hover:text-white hover:bg-white/10 rounded-lg transition-colors group"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <span>Profilim</span>
            </Link>

            <Link
              href="/profile/badges"
              onClick={() => setIsOpen(false)}
              className="flex items-center gap-3 px-3 py-2.5 text-white/80 hover:text-white hover:bg-white/10 rounded-lg transition-colors group"
            >
              <svg className="w-5 h-5 text-purple-400" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
              <span>Rozetlerim</span>
              <span className="ml-auto bg-purple-600/20 text-purple-400 text-xs font-bold px-2 py-0.5 rounded-full">
                {userProfile.badges.length}
              </span>
            </Link>

            {userProfile.isPremium && (
              <div className="px-3 py-2.5 bg-gradient-to-r from-yellow-600/20 to-orange-600/20 rounded-lg border border-yellow-600/30 mt-2">
                <div className="flex items-center gap-2">
                  <svg className="w-5 h-5 text-yellow-500" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                  </svg>
                  <span className="text-yellow-500 font-semibold text-sm">Premium Üye</span>
                </div>
              </div>
            )}
          </div>

          {/* Logout */}
          <div className="p-2 border-t border-white/10">
            <button
              onClick={handleLogout}
              className="w-full flex items-center gap-3 px-3 py-2.5 text-red-400 hover:text-red-300 hover:bg-red-500/10 rounded-lg transition-colors"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
              <span>Çıkış Yap</span>
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

