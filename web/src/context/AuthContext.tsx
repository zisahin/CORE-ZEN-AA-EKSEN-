'use client';

import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { User, onAuthStateChanged } from 'firebase/auth';
import { auth } from '@/lib/firebase';
import { getUserProfile, UserProfile, getUserGameStats, UserGameStats } from '@/services/authService';

interface AuthContextType {
  user: User | null;
  userProfile: UserProfile | null;
  gameStats: UserGameStats | null;
  loading: boolean;
  refreshProfile: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  userProfile: null,
  gameStats: null,
  loading: true,
  refreshProfile: async () => {}
});

export const useAuth = () => useContext(AuthContext);

// Demo kullanıcı verisi (Firebase olmadan tasarımı görmek için)
const createDemoUser = (): UserProfile => ({
  uid: 'demo-user-123',
  email: 'demo@aaeksen.com',
  username: 'Demo Kullanıcı',
  photoUrl: undefined,
  newsReadCount: 45,
  newsSharedCount: 12,
  totalXp: 350,
  badges: [
    {
      badgeId: 'first_reader',
      name: 'İlk Okuyucu',
      description: 'İlk haberini okudun!',
      rarity: 'common',
      earnedAt: new Date()
    },
    {
      badgeId: 'news_master',
      name: 'Haber Ustası',
      description: '50 haber okudun!',
      rarity: 'rare',
      earnedAt: new Date()
    },
    {
      badgeId: 'quiz_expert',
      name: 'Quiz Uzmanı',
      description: '10 quiz tamamladın!',
      rarity: 'epic',
      earnedAt: new Date()
    }
  ],
  isPremium: true,
  dailyTasks: ['task1', 'task2', 'task3'],
  completedTasks: ['task1'],
  notificationsEnabled: true,
  darkModeEnabled: false,
  createdAt: new Date(),
  updatedAt: new Date()
});

const createDemoStats = (): UserGameStats => ({
  crosswordSolved: 8,
  quizCorrect: 24,
  quizWrong: 6,
  mapGuessCorrect: 15,
  mapGuessWrong: 5,
  totalXp: 350
});

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [userProfile, setUserProfile] = useState<UserProfile | null>(null);
  const [gameStats, setGameStats] = useState<UserGameStats | null>(null);
  const [loading, setLoading] = useState(true);

  const loadUserData = async (currentUser: User) => {
    try {
      const [profile, stats] = await Promise.all([
        getUserProfile(currentUser.uid),
        getUserGameStats(currentUser.uid)
      ]);
      setUserProfile(profile);
      setGameStats(stats);
    } catch (error) {
      console.error('Kullanıcı verileri yüklenirken hata:', error);
    }
  };

  const refreshProfile = async () => {
    if (user) {
      await loadUserData(user);
    }
  };

  useEffect(() => {
    // Firebase yoksa demo mode
    if (!auth) {
      console.log('🎨 DEMO MODE: Firebase olmadan çalışıyor');
      console.log('👤 Demo kullanıcı yüklendi - Profil sayfasını görebilirsiniz!');
      console.log('💡 Gerçek Firebase için .env.local dosyasını düzenleyin');
      
      // Demo kullanıcıyı yükle
      setUserProfile(createDemoUser());
      setGameStats(createDemoStats());
      setLoading(false);
      return;
    }

    // Firebase varsa normal auth flow
    const unsubscribe = onAuthStateChanged(auth, async (currentUser) => {
      setUser(currentUser);
      
      if (currentUser) {
        await loadUserData(currentUser);
      } else {
        setUserProfile(null);
        setGameStats(null);
      }
      
      setLoading(false);
    });

    return unsubscribe;
  }, []);

  return (
    <AuthContext.Provider value={{ user, userProfile, gameStats, loading, refreshProfile }}>
      {children}
    </AuthContext.Provider>
  );
}

