import { Timestamp } from "firebase/firestore";

export interface NewsItem {
  id: string;
  title: string;
  content: string;
  author: string;
  publishedAt: Timestamp;
  imageUrl: string;
  imageCaption?: string;
  category: string;
  location: string;
  xpPoints: number;
  likeCount: number;
  viewCount: number;
  shareCount: number;
  breaking: boolean;
  newsUrl: string;
  verified: boolean;
  createdAt: Timestamp;
  updatedAt: Timestamp;
  crosswordWords?: string[];
  quizQuestions?: any[];
}

export interface TimeTunnelCategory {
  id: string;
  title: string;
  description: string;
  coverImageUrl: string;
  newsIds: string[];
  active: boolean;
  createdAt: Timestamp;
  startDate?: Timestamp;
  endDate?: Timestamp;
}

export interface QuizQuestion {
  id: string;
  difficulty: string;
  question: string;
  options: string[];
  correctAnswer: number;
  explanation: string;
  points: number;
  sourceNewsTitle?: string;
}

export interface CrosswordCategory {
  id: string;
  name: string;
  xpPoints: number;
  iconUrl: string;
}

export interface CrosswordWordGroup {
  id: string;
  categoryId: string;
  words: {
    word: string;
    clue: string;
    xp: number;
  }[];
}