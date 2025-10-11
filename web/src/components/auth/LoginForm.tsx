'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { loginUser, loginWithGoogle } from '@/services/authService';
import Link from 'next/link';

export default function LoginForm() {
  const router = useRouter();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await loginUser(email, password);
      router.push('/');
    } catch (err: any) {
      setError('Giriş başarısız. E-posta veya şifre hatalı.');
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = async () => {
    try {
      await loginWithGoogle();
      router.push('/');
    } catch (err: any) {
      setError('Google ile giriş başarısız oldu.');
    }
  };

  return (
    <div className="relative w-full max-w-md">
      {/* Ana Form Container - Gradient Arka Plan */}
      <div className="relative bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 rounded-2xl shadow-2xl overflow-hidden">
        
        {/* Dekoratif Gradient Bottom Wave */}
        <div className="absolute bottom-0 left-0 right-0 h-32 bg-gradient-to-t from-purple-900/40 to-transparent">
          <svg className="absolute bottom-0 w-full h-24" viewBox="0 0 1440 120" preserveAspectRatio="none">
            <path fill="#6b21a8" fillOpacity="0.5" d="M0,64L80,69.3C160,75,320,85,480,80C640,75,800,53,960,48C1120,43,1280,53,1360,58.7L1440,64L1440,120L1360,120C1280,120,1120,120,960,120C800,120,640,120,480,120C320,120,160,120,80,120L0,120Z"></path>
          </svg>
        </div>

        {/* Signup Link - Sağ Üst */}
        <div className="absolute top-6 right-6 z-10">
          <Link 
            href="/auth/register"
            className="flex items-center gap-2 text-white/80 hover:text-white transition-colors text-sm font-medium"
          >
            <span>Signup</span>
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </Link>
        </div>

        {/* Form İçeriği */}
        <div className="relative z-10 p-10 pt-16">
          {/* Başlık */}
          <div className="mb-8">
            <h2 className="text-3xl font-bold text-white mb-2">Here you can Login</h2>
            <p className="text-white/60 text-sm">Let's join us :)</p>
          </div>

          {/* Hata Mesajı */}
          {error && (
            <div className="mb-6 p-3 bg-red-500/20 border border-red-500/50 rounded-lg text-red-200 text-sm">
              {error}
            </div>
          )}

          {/* Form */}
          <form onSubmit={handleLogin} className="space-y-6">
            {/* Email Input */}
            <div>
              <label className="block text-white/80 text-sm mb-2">Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-lg text-white placeholder-white/40 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                placeholder="ornek@email.com"
                required
              />
            </div>

            {/* Password Input */}
            <div>
              <label className="block text-white/80 text-sm mb-2">Password</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-lg text-white placeholder-white/40 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                placeholder="••••••••"
                required
              />
            </div>

            {/* Login Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-gradient-to-r from-purple-600 to-purple-700 hover:from-purple-700 hover:to-purple-800 text-white font-semibold py-3 rounded-lg transition-all transform hover:scale-[1.02] disabled:opacity-50 disabled:cursor-not-allowed shadow-lg shadow-purple-900/50"
            >
              {loading ? 'Giriş yapılıyor...' : 'LOGIN'}
            </button>

            {/* Forgot Password */}
            <div className="text-center">
              <Link 
                href="/auth/forgot-password" 
                className="text-white/60 hover:text-white text-sm transition-colors"
              >
                Forgot your password?
              </Link>
            </div>
          </form>

          {/* Divider */}
          <div className="my-8">
            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-white/20"></div>
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-transparent text-white/60">veya</span>
              </div>
            </div>
          </div>

          {/* Google Login */}
          <button
            onClick={handleGoogleLogin}
            className="w-full flex items-center justify-center gap-3 bg-white/10 hover:bg-white/20 border border-white/20 text-white py-3 rounded-lg font-medium transition-all"
          >
            <svg className="w-5 h-5" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
            </svg>
            Google ile Giriş Yap
          </button>
        </div>
      </div>

      {/* Glow Effect */}
      <div className="absolute -inset-1 bg-gradient-to-r from-purple-600 to-blue-600 rounded-2xl blur-2xl opacity-20 -z-10"></div>
    </div>
  );
}

