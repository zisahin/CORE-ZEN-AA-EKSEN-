import { 
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  signOut,
  sendPasswordResetEmail,
  updateProfile,
  GoogleAuthProvider,
  signInWithPopup,
  User
} from 'firebase/auth';
import { doc, setDoc, getDoc, updateDoc, increment } from 'firebase/firestore';
import { auth, db } from '@/lib/firebase';

// Rozet tipi
export interface UserBadge {
  badgeId: string;
  name: string;
  description: string;
  iconUrl?: string;
  earnedAt: Date;
  rarity: 'common' | 'rare' | 'epic' | 'legendary';
}

// Kullanıcı profili (oyunlaştırma ile)
export interface UserProfile {
  uid: string;
  email: string;
  username: string;
  photoUrl?: string;
  
  // Oyunlaştırma İstatistikleri
  newsReadCount: number;
  newsSharedCount: number;
  totalXp: number;
  
  // Rozetler
  badges: UserBadge[];
  
  // Premium
  isPremium: boolean;
  premiumExpiryDate?: Date;
  
  // Günlük Görevler
  dailyTasks: string[];
  completedTasks: string[];
  
  // Ayarlar
  notificationsEnabled: boolean;
  darkModeEnabled: boolean;
  
  createdAt: Date;
  updatedAt: Date;
}

// Oyun istatistikleri
export interface UserGameStats {
  crosswordSolved: number;
  quizCorrect: number;
  quizWrong: number;
  mapGuessCorrect: number;
  mapGuessWrong: number;
  totalXp: number;
}

// XP sabitleri
export const XP_REWARDS = {
  NEWS_READ: 5,
  NEWS_SHARE: 10,
  QUIZ_CORRECT: 10,
  CROSSWORD_COMPLETE: 50,
  MAP_GUESS_CORRECT: 20,
  DAILY_TASK_COMPLETE: 20,
};

// Seviye hesaplama
export const calculateLevel = (totalXp: number): number => {
  return Math.floor(totalXp / 100) + 1;
};

// Bir sonraki seviye için gereken XP
export const getXpForNextLevel = (currentXp: number): number => {
  const currentLevel = calculateLevel(currentXp);
  return currentLevel * 100 - currentXp;
};

// Kayıt işlemi
export const registerUser = async (
  email: string, 
  password: string, 
  username: string
) => {
  if (!auth || !db) throw new Error('Firebase not initialized');
  const userCredential = await createUserWithEmailAndPassword(auth, email, password);
  const user = userCredential.user;

  // Profil güncelle
  await updateProfile(user, { displayName: username });

  // Firestore'da kullanıcı dokümanı oluştur
  const userProfile: UserProfile = {
    uid: user.uid,
    email: user.email!,
    username,
    newsReadCount: 0,
    newsSharedCount: 0,
    totalXp: 0,
    badges: [],
    isPremium: false,
    dailyTasks: [],
    completedTasks: [],
    notificationsEnabled: true,
    darkModeEnabled: false,
    createdAt: new Date(),
    updatedAt: new Date()
  };

  await setDoc(doc(db, 'users', user.uid), userProfile);
  
  // Oyun istatistikleri oluştur
  const gameStats: UserGameStats = {
    crosswordSolved: 0,
    quizCorrect: 0,
    quizWrong: 0,
    mapGuessCorrect: 0,
    mapGuessWrong: 0,
    totalXp: 0
  };
  
  await setDoc(doc(db, 'userGameStats', user.uid), gameStats);
  
  return userProfile;
};

// Giriş işlemi
export const loginUser = async (email: string, password: string) => {
  if (!auth) throw new Error('Firebase not initialized');
  return await signInWithEmailAndPassword(auth, email, password);
};

// Google ile giriş
export const loginWithGoogle = async () => {
  if (!auth || !db) throw new Error('Firebase not initialized');
  const provider = new GoogleAuthProvider();
  const result = await signInWithPopup(auth, provider);
  
  // Firestore'da kullanıcı kontrolü
  const userDoc = await getDoc(doc(db, 'users', result.user.uid));
  
  if (!userDoc.exists()) {
    const userProfile: UserProfile = {
      uid: result.user.uid,
      email: result.user.email!,
      username: result.user.displayName || 'Kullanıcı',
      photoUrl: result.user.photoURL || undefined,
      newsReadCount: 0,
      newsSharedCount: 0,
      totalXp: 0,
      badges: [],
      isPremium: false,
      dailyTasks: [],
      completedTasks: [],
      notificationsEnabled: true,
      darkModeEnabled: false,
      createdAt: new Date(),
      updatedAt: new Date()
    };
    await setDoc(doc(db, 'users', result.user.uid), userProfile);
    
    // Oyun istatistikleri
    const gameStats: UserGameStats = {
      crosswordSolved: 0,
      quizCorrect: 0,
      quizWrong: 0,
      mapGuessCorrect: 0,
      mapGuessWrong: 0,
      totalXp: 0
    };
    await setDoc(doc(db, 'userGameStats', result.user.uid), gameStats);
  }
  
  return result;
};

// Çıkış işlemi
export const logoutUser = async () => {
  if (!auth) {
    console.log('Demo mode - signOut simulated');
    return;
  }
  await signOut(auth);
};

// Şifre sıfırlama
export const resetPassword = async (email: string) => {
  if (!auth) throw new Error('Firebase not initialized');
  await sendPasswordResetEmail(auth, email);
};

// Profil güncelleme
export const updateUserProfile = async (
  userId: string,
  updates: Partial<UserProfile>
) => {
  if (!db) throw new Error('Firebase not initialized');
  await updateDoc(doc(db, 'users', userId), {
    ...updates,
    updatedAt: new Date()
  });
};

// Kullanıcı bilgilerini getir
export const getUserProfile = async (userId: string): Promise<UserProfile | null> => {
  if (!db) return null;
  const userDoc = await getDoc(doc(db, 'users', userId));
  return userDoc.exists() ? userDoc.data() as UserProfile : null;
};

// Oyun istatistiklerini getir
export const getUserGameStats = async (userId: string): Promise<UserGameStats | null> => {
  if (!db) return null;
  const statsDoc = await getDoc(doc(db, 'userGameStats', userId));
  return statsDoc.exists() ? statsDoc.data() as UserGameStats : null;
};

// XP ekle
export const addXP = async (userId: string, xpAmount: number) => {
  if (!db) return;
  await updateDoc(doc(db, 'users', userId), {
    totalXp: increment(xpAmount),
    updatedAt: new Date()
  });
};

// Rozet ekle
export const awardBadge = async (userId: string, badge: UserBadge) => {
  if (!db) return;
  const userDoc = await getDoc(doc(db, 'users', userId));
  const userData = userDoc.data() as UserProfile;
  
  // Rozet zaten varsa ekleme
  if (userData.badges.some(b => b.badgeId === badge.badgeId)) {
    return;
  }
  
  await updateDoc(doc(db, 'users', userId), {
    badges: [...userData.badges, badge],
    updatedAt: new Date()
  });
};

