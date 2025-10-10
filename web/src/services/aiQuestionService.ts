import { db } from '@/lib/firebase';
import { collection, getDocs, query, where, orderBy, limit } from 'firebase/firestore';
import { AIQuestion } from '@/types/firestore';

export const aiQuestionService = {
  // Bugünkü aktif soruları çek (max 3)
  async getTodayQuestions(): Promise<AIQuestion[]> {
    const questionsCol = collection(db, 'ai_questions');
    
    // Bugünün başlangıcı
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const q = query(
      questionsCol,
      where('active', '==', true),
      orderBy('createdAt', 'desc'),
      limit(3)
    );
    
    const snapshot = await getDocs(q);
    
    return snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    } as AIQuestion));
  },

  // Tüm aktif soruları çek
  async getActiveQuestions(): Promise<AIQuestion[]> {
    const questionsCol = collection(db, 'ai_questions');
    const q = query(
      questionsCol,
      where('active', '==', true),
      orderBy('createdAt', 'desc')
    );
    
    const snapshot = await getDocs(q);
    
    return snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    } as AIQuestion));
  }
};

