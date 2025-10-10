import { db } from '@/lib/firebase';
import { collection, getDocs, query, where, doc, getDoc, limit } from 'firebase/firestore';
import { QuizQuestion } from '@/types/firestore';

export const quizService = {
  // Belirli bir kategorideki quiz sorularını çek
  async getQuizzesByCategory(category: string): Promise<QuizQuestion[]> {
    const quizzesCol = collection(db, 'quiz_questions');
    const q = query(
      quizzesCol,
      where('category', '==', category),
      limit(10)
    );
    
    const snapshot = await getDocs(q);
    
    return snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    } as QuizQuestion));
  },

  // Belirli bir haber için quiz çek
  async getQuizByNewsId(newsId: string): Promise<QuizQuestion[]> {
    const quizzesCol = collection(db, 'quiz_questions');
    const q = query(
      quizzesCol,
      where('newsId', '==', newsId),
      limit(4)
    );
    
    const snapshot = await getDocs(q);
    
    return snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    } as QuizQuestion));
  },

  // Random 4 quiz sorusu çek (kolay -> zor)
  async getRandomQuiz(): Promise<QuizQuestion[]> {
    const quizzesCol = collection(db, 'quiz_questions');
    
    // Kolay, Orta, Zor, Çok Zor - birer soru
    const difficulties = ['easy', 'medium', 'hard', 'very_hard'];
    const quizQuestions: QuizQuestion[] = [];
    
    for (const difficulty of difficulties) {
      const q = query(
        quizzesCol,
        where('difficulty', '==', difficulty),
        limit(1)
      );
      
      const snapshot = await getDocs(q);
      snapshot.docs.forEach(doc => {
        quizQuestions.push({
          id: doc.id,
          ...doc.data()
        } as QuizQuestion);
      });
    }
    
    return quizQuestions;
  },

  // Quiz detayını çek
  async getQuizById(quizId: string): Promise<QuizQuestion | null> {
    const quizDoc = doc(db, 'quiz_questions', quizId);
    const snapshot = await getDoc(quizDoc);
    
    if (!snapshot.exists()) return null;
    
    return {
      id: snapshot.id,
      ...snapshot.data()
    } as QuizQuestion;
  }
};

