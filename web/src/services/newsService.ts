import { db } from '@/lib/firebase';
import { collection, getDocs, query, orderBy, limit, where, doc, getDoc } from 'firebase/firestore';
import { NewsItem } from '@/types/firestore';

export const newsService = {
  // Tüm haberleri çek
  async getNews(limitCount: number = 20): Promise<NewsItem[]> {
    const newsCol = collection(db, 'news');
    const q = query(newsCol, orderBy('publishedAt', 'desc'), limit(limitCount));
    const snapshot = await getDocs(q);
    
    return snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    } as NewsItem));
  },

  // Kategoriye göre haberleri çek
  async getNewsByCategory(category: string, limitCount: number = 20): Promise<NewsItem[]> {
    const newsCol = collection(db, 'news');
    const q = query(
      newsCol, 
      where('category', '==', category),
      orderBy('publishedAt', 'desc'), 
      limit(limitCount)
    );
    const snapshot = await getDocs(q);
    
    return snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    } as NewsItem));
  },

  // Şehre göre haberleri çek
  async getNewsByCity(cityName: string, limitCount: number = 20): Promise<NewsItem[]> {
    const newsCol = collection(db, 'news');
    const q = query(
      newsCol,
      where('location', '==', cityName),
      orderBy('publishedAt', 'desc'),
      limit(limitCount)
    );
    const snapshot = await getDocs(q);
    
    return snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    } as NewsItem));
  },

  // Tek haber çek
  async getNewsById(newsId: string): Promise<NewsItem | null> {
    const newsDoc = doc(db, 'news', newsId);
    const snapshot = await getDoc(newsDoc);
    
    if (!snapshot.exists()) return null;
    
    return {
      id: snapshot.id,
      ...snapshot.data()
    } as NewsItem;
  },

  // Breaking news çek
  async getBreakingNews(): Promise<NewsItem[]> {
    const newsCol = collection(db, 'news');
    const q = query(
      newsCol,
      where('breaking', '==', true),
      orderBy('publishedAt', 'desc'),
      limit(5)
    );
    const snapshot = await getDocs(q);
    
    return snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    } as NewsItem));
  }
};