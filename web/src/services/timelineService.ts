import { db } from '@/lib/firebase';
import { collection, getDocs, query, where, doc, getDoc, documentId } from 'firebase/firestore';
import { TimeTunnelCategory, NewsItem } from '@/types/firestore';

export const timelineService = {
  // Aktif timeline kategorilerini çek
  async getActiveCategories(): Promise<TimeTunnelCategory[]> {
    const categoriesCol = collection(db, 'time_tunnel_categories');
    const q = query(categoriesCol, where('active', '==', true));
    const snapshot = await getDocs(q);
    
    return snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    } as TimeTunnelCategory));
  },

  // Kategori detayını çek
  async getCategoryById(categoryId: string): Promise<TimeTunnelCategory | null> {
    const categoryDoc = doc(db, 'time_tunnel_categories', categoryId);
    const snapshot = await getDoc(categoryDoc);
    
    if (!snapshot.exists()) return null;
    
    return {
      id: snapshot.id,
      ...snapshot.data()
    } as TimeTunnelCategory;
  },

  // Kategorideki haberleri kronolojik çek
  async getCategoryNews(newsIds: string[]): Promise<NewsItem[]> {
    if (newsIds.length === 0) return [];
    
    // Firestore 'in' sorgusu max 10 ID alır, grupla
    const newsCol = collection(db, 'news');
    const chunks = [];
    
    for (let i = 0; i < newsIds.length; i += 10) {
      const chunk = newsIds.slice(i, i + 10);
      const q = query(newsCol, where(documentId(), 'in', chunk));
      const snapshot = await getDocs(q);
      chunks.push(...snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      } as NewsItem)));
    }
    
    // newsIds sırasına göre sırala
    return newsIds
      .map(id => chunks.find(news => news.id === id))
      .filter(news => news !== undefined) as NewsItem[];
  }
};