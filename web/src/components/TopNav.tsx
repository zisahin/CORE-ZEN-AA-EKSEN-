'use client'

import { useTheme } from '@/context/ThemeContext'
import { useAuth } from '@/context/AuthContext'
import { useRouter } from 'next/navigation'
import ThemeToggle from './ThemeToggle'

export default function TopNav() {
  const { isGradient } = useTheme()
  const { user } = useAuth()
  const router = useRouter()

  // İsmin baş harflerini al (örn: "Ahmet Yılmaz" -> "AY")
  const getInitials = (name: string | null) => {
    if (!name) return 'K'; // Kullanıcı
    const names = name.trim().split(' ');
    if (names.length === 1) return names[0].charAt(0).toUpperCase();
    return (names[0].charAt(0) + names[names.length - 1].charAt(0)).toUpperCase();
  };

  return (
    <header className={`sticky top-0 z-50 backdrop-blur border-b shadow-lg transition-all duration-300 ${
      isGradient
        ? 'bg-gradient-to-r from-slate-900 via-blue-900 to-slate-900 border-white/10'
        : 'bg-cream/95 border-cream-strong'
    }`}>
      <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <img 
            src="/images/aa-logo.png" 
            alt="AA" 
            className={`h-8 w-auto transition-all duration-300 ${
              isGradient ? 'brightness-0 invert' : ''
            }`} 
          />
          <span className={`text-lg font-extrabold tracking-tight transition-colors duration-300 ${
            isGradient ? 'text-white' : 'text-brand-blue'
          }`}>
            AA HABER
          </span>
        </div>
        
        <nav className="hidden md:flex items-center gap-6">
          <a className={`text-sm transition-colors ${
            isGradient 
              ? 'text-white/80 hover:text-purple-400' 
              : 'text-slate-700 hover:text-brand-blue'
          }`} href="#">
            Anasayfa
          </a>
          <a className={`text-sm transition-colors ${
            isGradient 
              ? 'text-white/80 hover:text-purple-400' 
              : 'text-slate-700 hover:text-brand-blue'
          }`} href="#">
            Kategoriler
          </a>
          <a className={`text-sm transition-colors ${
            isGradient 
              ? 'text-white/80 hover:text-purple-400' 
              : 'text-slate-700 hover:text-brand-blue'
          }`} href="#">
            Oyunlar
          </a>
        </nav>
        
        <div className="flex items-center gap-3">
          {/* Theme Toggle - Sağ Üst */}
          <ThemeToggle />
          
          {/* Search */}
          <div className={`hidden sm:flex items-center rounded-full shadow-lg px-4 h-9 backdrop-blur border ${
            isGradient
              ? 'bg-white/10 border-white/20 text-white/60'
              : 'bg-white border-cream-strong text-slate-400'
          }`}>
            <svg className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-4.35-4.35M11 18a7 7 0 110-14 7 7 0 010 14z" />
            </svg>
            <input 
              className={`ml-2 text-sm outline-none bg-transparent ${
                isGradient 
                  ? 'placeholder-white/40 text-white' 
                  : 'placeholder-slate-400 text-slate-700'
              }`} 
              placeholder="Haber ara..." 
            />
          </div>
          
          {/* User Profile / Login Button */}
          {user ? (
            // Kullanıcı giriş yapmışsa - İsim ve Avatar
            <button
              onClick={() => router.push('/profile')}
              className={`flex items-center gap-3 px-4 py-2 rounded-xl font-medium shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200 ${
                isGradient
                  ? 'bg-gradient-to-r from-purple-600 to-blue-600 text-white hover:from-purple-700 hover:to-blue-700'
                  : 'bg-brand-blue text-white hover:bg-blue-700'
              }`}
            >
              {/* İsim Soyisim */}
              <span className="text-sm font-bold">
                {user.displayName || 'Kullanıcı'}
              </span>

              {/* Avatar */}
              <div className={`w-9 h-9 rounded-full overflow-hidden ring-2 flex items-center justify-center ${
                isGradient ? 'ring-white/30' : 'ring-white'
              }`}>
                {user.photoURL ? (
                  <img
                    src={user.photoURL}
                    alt={user.displayName || 'Profil'}
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center bg-white/20 backdrop-blur text-white font-bold text-sm">
                    {getInitials(user.displayName)}
                  </div>
                )}
              </div>
            </button>
          ) : (
            // Kullanıcı giriş yapmamışsa - Giriş/Kayıt Butonu
            <button
              onClick={() => router.push('/auth/login')}
              className={`px-6 py-2.5 rounded-xl text-sm font-bold shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-200 ${
                isGradient
                  ? 'bg-gradient-to-r from-purple-600 to-purple-700 text-white hover:from-purple-700 hover:to-purple-800'
                  : 'bg-brand-blue text-white hover:bg-blue-700'
              }`}
            >
              🔐 Giriş / Kayıt
            </button>
          )}
        </div>
      </div>
    </header>
  );
}