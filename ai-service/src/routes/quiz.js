import express from 'express';
import openaiService from '../services/openai.js';

const router = express.Router();

// Haber tabanlı quiz oluştur
router.post('/generate', async (req, res) => {
  try {
    const { newsItems = [], difficulty = 'medium', questionCount = 4 } = req.body;

    if (newsItems.length === 0) {
      return res.status(400).json({
        error: 'Haber listesi boş',
        message: 'Quiz için haber listesi gereklidir'
      });
    }

    // AI ile quiz oluştur
    const quizResponse = await openaiService.generateNewsQuiz(newsItems, {
      maxTokens: 800,
      temperature: 0.6
    });

    let quiz;
    try {
      quiz = JSON.parse(quizResponse);
    } catch (parseError) {
      // Eğer JSON parse edilemezse, default quiz oluştur
      quiz = {
        quiz: [
          {
            question: "Bu haberlerden hangisi doğrudur?",
            options: ["A seçeneği", "B seçeneği", "C seçeneği", "D seçeneği"],
            correctAnswer: 0,
            explanation: "Quiz oluşturulamadı, lütfen tekrar deneyin."
          }
        ]
      };
    }

    // Quiz metadata'sı
    const quizMeta = {
      id: `quiz_${Date.now()}`,
      title: 'Güncel Haberler Quiz',
      description: `${newsItems.length} haberden oluşturulan quiz`,
      difficulty: difficulty,
      questionCount: quiz.quiz?.length || questionCount,
      timeLimit: 300, // 5 dakika
      category: 'mixed',
      createdAt: new Date().toISOString()
    };

    res.json({
      success: true,
      quiz: {
        ...quizMeta,
        questions: quiz.quiz || quiz,
        sourceNews: newsItems.map(news => ({
          id: news.id,
          title: news.title,
          category: news.category
        }))
      },
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Quiz Generation Error:', error);
    res.status(500).json({
      error: 'Quiz oluşturma hatası',
      message: 'Quiz oluşturulamadı.'
    });
  }
});

// Hazır quizleri getir
router.get('/', async (req, res) => {
  try {
    const { category, difficulty, limit = 10 } = req.query;

    // Mock quiz listesi
    const mockQuizzes = [
      {
        id: 'quiz_1',
        title: 'Günün Haberleri Quiz',
        description: 'Bugünkü haberlerden sorular',
        difficulty: 'easy',
        questionCount: 4,
        category: 'genel',
        timeLimit: 300,
        playCount: 127,
        averageScore: 3.2,
        createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
      },
      {
        id: 'quiz_2',
        title: 'Spor Haberleri Quiz',
        description: 'Spor dünyasından sorular',
        difficulty: 'medium',
        questionCount: 5,
        category: 'spor',
        timeLimit: 400,
        playCount: 89,
        averageScore: 2.8,
        createdAt: new Date(Date.now() - 48 * 60 * 60 * 1000).toISOString()
      },
      {
        id: 'quiz_3',
        title: 'Ekonomi Quiz',
        description: 'Ekonomi haberlerinden sorular',
        difficulty: 'hard',
        questionCount: 6,
        category: 'ekonomi',
        timeLimit: 500,
        playCount: 45,
        averageScore: 2.1,
        createdAt: new Date(Date.now() - 72 * 60 * 60 * 1000).toISOString()
      }
    ];

    let filteredQuizzes = mockQuizzes;

    if (category) {
      filteredQuizzes = filteredQuizzes.filter(q => q.category === category);
    }

    if (difficulty) {
      filteredQuizzes = filteredQuizzes.filter(q => q.difficulty === difficulty);
    }

    filteredQuizzes = filteredQuizzes.slice(0, parseInt(limit));

    res.json({
      success: true,
      quizzes: filteredQuizzes,
      total: filteredQuizzes.length,
      filters: { category, difficulty, limit },
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Quiz List Error:', error);
    res.status(500).json({
      error: 'Quiz listesi hatası',
      message: 'Quiz listesi alınamadı.'
    });
  }
});

// Belirli quiz detayları
router.get('/:quizId', async (req, res) => {
  try {
    const { quizId } = req.params;

    // Mock quiz detayı
    const mockQuiz = {
      id: quizId,
      title: 'Günün Haberleri Quiz',
      description: 'Bugünkü haberlerden oluşturulan quiz',
      difficulty: 'medium',
      questionCount: 4,
      category: 'genel',
      timeLimit: 300,
      playCount: 127,
      averageScore: 3.2,
      createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
      questions: [
        {
          id: 'q1',
          question: 'Türkiye\'nin başkenti hangi şehirdir?',
          options: ['İstanbul', 'Ankara', 'İzmir', 'Bursa'],
          correctAnswer: 1,
          explanation: 'Türkiye\'nin başkenti 1923 yılından itibaren Ankara\'dır.',
          points: 10
        },
        {
          id: 'q2',
          question: 'Marmaray projesi hangi şehirde yer almaktadır?',
          options: ['Ankara', 'İzmir', 'İstanbul', 'Antalya'],
          correctAnswer: 2,
          explanation: 'Marmaray, İstanbul Boğazı\'nı geçen metro sistemidir.',
          points: 10
        },
        {
          id: 'q3',
          question: 'Türkiye\'nin para birimi nedir?',
          options: ['Euro', 'Dolar', 'Türk Lirası', 'Pound'],
          correctAnswer: 2,
          explanation: 'Türkiye\'nin resmi para birimi Türk Lirası\'dır.',
          points: 10
        },
        {
          id: 'q4',
          question: 'Fenerbahçe hangi spor dalında faaliyet gösterir?',
          options: ['Basketbol', 'Futbol', 'Voleybol', 'Hepsi'],
          correctAnswer: 3,
          explanation: 'Fenerbahçe çok dalda faaliyet gösteren bir spor kulübüdür.',
          points: 10
        }
      ]
    };

    res.json({
      success: true,
      quiz: mockQuiz,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Quiz Detail Error:', error);
    res.status(500).json({
      error: 'Quiz detay hatası',
      message: 'Quiz detayları alınamadı.'
    });
  }
});

// Quiz yanıtlarını değerlendir
router.post('/:quizId/submit', async (req, res) => {
  try {
    const { quizId } = req.params;
    const { answers = [], userId = 'anonymous', timeSpent = 0 } = req.body;

    if (answers.length === 0) {
      return res.status(400).json({
        error: 'Yanıt bulunamadı',
        message: 'Quiz yanıtları gereklidir'
      });
    }

    // Mock quiz soruları (normalde veritabanından çekersiniz)
    const correctAnswers = [1, 2, 2, 3]; // q1: 1, q2: 2, q3: 2, q4: 3
    const pointsPerQuestion = 10;

    // Değerlendirme yap
    let correctCount = 0;
    let totalPoints = 0;
    const detailedResults = answers.map((userAnswer, index) => {
      const isCorrect = userAnswer === correctAnswers[index];
      if (isCorrect) {
        correctCount++;
        totalPoints += pointsPerQuestion;
      }

      return {
        questionId: `q${index + 1}`,
        userAnswer: userAnswer,
        correctAnswer: correctAnswers[index],
        isCorrect: isCorrect,
        points: isCorrect ? pointsPerQuestion : 0
      };
    });

    // Başarı durumu
    const successRate = (correctCount / answers.length) * 100;
    let grade = 'F';
    let message = 'Tekrar deneyebilirsiniz!';

    if (successRate >= 90) {
      grade = 'A+';
      message = 'Mükemmel! Harika bir performans! 🎉';
    } else if (successRate >= 80) {
      grade = 'A';
      message = 'Çok iyi! Tebrikler! 👏';
    } else if (successRate >= 70) {
      grade = 'B';
      message = 'İyi bir performans! 👍';
    } else if (successRate >= 60) {
      grade = 'C';
      message = 'Fena değil, biraz daha çalışın! 📚';
    } else if (successRate >= 50) {
      grade = 'D';
      message = 'Ortalama, daha iyi yapabilirsiniz! 💪';
    }

    const result = {
      quizId: quizId,
      userId: userId,
      score: correctCount,
      totalQuestions: answers.length,
      successRate: Math.round(successRate),
      totalPoints: totalPoints,
      maxPoints: answers.length * pointsPerQuestion,
      grade: grade,
      message: message,
      timeSpent: timeSpent,
      detailedResults: detailedResults,
      completedAt: new Date().toISOString()
    };

    // Burada normalde veritabanına kayıt yaparsınız

    res.json({
      success: true,
      result: result,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Quiz Submit Error:', error);
    res.status(500).json({
      error: 'Quiz değerlendirme hatası',
      message: 'Quiz değerlendirilemedi.'
    });
  }
});

// Quiz kategorilerini getir
router.get('/categories/list', async (req, res) => {
  try {
    const categories = [
      {
        id: 'genel',
        name: 'Genel Haberler',
        description: 'Güncel haberlerden genel sorular',
        icon: '📰',
        quizCount: 25,
        difficulty: 'easy'
      },
      {
        id: 'spor',
        name: 'Spor',
        description: 'Spor haberleri ve bilgileri',
        icon: '⚽',
        quizCount: 18,
        difficulty: 'medium'
      },
      {
        id: 'ekonomi',
        name: 'Ekonomi',
        description: 'Ekonomi ve finans haberleri',
        icon: '📈',
        quizCount: 15,
        difficulty: 'hard'
      },
      {
        id: 'teknoloji',
        name: 'Teknoloji',
        description: 'Teknoloji dünyasından haberler',
        icon: '💻',
        quizCount: 12,
        difficulty: 'medium'
      },
      {
        id: 'siyaset',
        name: 'Siyaset',
        description: 'Siyasi gelişmeler ve haberler',
        icon: '🏛️',
        quizCount: 20,
        difficulty: 'medium'
      }
    ];

    res.json({
      success: true,
      categories: categories,
      totalQuizzes: categories.reduce((sum, cat) => sum + cat.quizCount, 0),
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Quiz Categories Error:', error);
    res.status(500).json({
      error: 'Quiz kategorileri hatası',
      message: 'Quiz kategorileri alınamadı.'
    });
  }
});

// Kullanıcı quiz istatistikleri
router.get('/stats/:userId', async (req, res) => {
  try {
    const { userId } = req.params;

    // Mock istatistikler
    const mockStats = {
      userId: userId,
      totalQuizzes: 23,
      totalPoints: 1840,
      averageScore: 80,
      bestCategory: 'spor',
      weakestCategory: 'ekonomi',
      streak: 5,
      rank: 142,
      achievements: [
        { id: 'first_quiz', name: 'İlk Quiz', description: 'İlk quizinizi tamamladınız', earned: true },
        { id: 'perfect_score', name: 'Mükemmel Skor', description: 'Bir quizde tam puan aldınız', earned: true },
        { id: 'quiz_master', name: 'Quiz Ustası', description: '20 quiz tamamladınız', earned: true },
        { id: 'speed_demon', name: 'Hız Şeytanı', description: 'Bir quizi 2 dakikada tamamladınız', earned: false }
      ],
      recentQuizzes: [
        {
          quizId: 'quiz_1',
          title: 'Günün Haberleri Quiz',
          score: 4,
          totalQuestions: 4,
          completedAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
        },
        {
          quizId: 'quiz_2',
          title: 'Spor Quiz',
          score: 3,
          totalQuestions: 5,
          completedAt: new Date(Date.now() - 48 * 60 * 60 * 1000).toISOString()
        }
      ]
    };

    res.json({
      success: true,
      stats: mockStats,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Quiz Stats Error:', error);
    res.status(500).json({
      error: 'Quiz istatistikleri hatası',
      message: 'Quiz istatistikleri alınamadı.'
    });
  }
});

export default router;


