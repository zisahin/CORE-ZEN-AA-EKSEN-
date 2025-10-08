import express from 'express';
import openaiService from '../services/openai.js';

const router = express.Router();

// Günlük sorular getir
router.get('/daily', async (req, res) => {
  try {
    const today = new Date().toLocaleDateString('tr-TR');
    
    // Cache kontrolü için bugünün tarihini kullan
    const cacheKey = `daily_questions_${today}`;
    
    // Burada normalde cache'den kontrol edersiniz
    // Şimdilik her seferinde yeni sorular oluşturalım
    
    const questionsResponse = await openaiService.generateDailyQuestions();
    
    let questions;
    try {
      questions = JSON.parse(questionsResponse);
    } catch (parseError) {
      // Eğer JSON parse edilemezse, default sorular kullan
      questions = {
        questions: [
          {
            text: "Bugün nasıl hissediyorsunuz?",
            category: "duygu",
            options: ["Harika", "İyi", "Normal", "Biraz yorgun"]
          },
          {
            text: "En sevdiğiniz mevsim hangisi?",
            category: "kişisel",
            options: ["İlkbahar", "Yaz", "Sonbahar", "Kış"]
          },
          {
            text: "Hangi spor dalını izlemeyi tercih edersiniz?",
            category: "spor",
            options: ["Futbol", "Basketbol", "Tenis", "Diğer"]
          }
        ]
      };
    }

    res.json({
      success: true,
      date: today,
      questions: questions.questions || questions,
      totalQuestions: questions.questions?.length || 3,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Daily Questions Error:', error);
    res.status(500).json({
      error: 'Günlük sorular hatası',
      message: 'Günlük sorular oluşturulamadı.'
    });
  }
});

// Soru yanıtla ve analiz al
router.post('/answer', async (req, res) => {
  try {
    const { questionId, answer, userId = 'anonymous' } = req.body;

    if (!questionId || !answer) {
      return res.status(400).json({
        error: 'Eksik bilgi',
        message: 'Soru ID ve yanıt gereklidir'
      });
    }

    // Burada normalde veritabanına kayıt yaparsınız
    const responseData = {
      id: `response_${Date.now()}`,
      questionId: questionId,
      answer: answer,
      userId: userId,
      timestamp: new Date().toISOString(),
      analyzed: false
    };

    // AI ile yanıt analizi (opsiyonel)
    try {
      const analysisPrompt = `Kullanıcı şu soruya: "${questionId}" şu yanıtı verdi: "${answer}". Bu yanıt hakkında kısa ve pozitif bir değerlendirme yap.`;
      
      const analysis = await openaiService.chatCompletion([
        {
          role: 'system',
          content: 'Sen pozitif ve destekleyici bir psikolog asistanısın. Kullanıcı yanıtlarını analiz edip kısa, yapıcı geribildirimler veriyorsun.'
        },
        {
          role: 'user',
          content: analysisPrompt
        }
      ], { maxTokens: 150, temperature: 0.7 });

      responseData.analysis = analysis;
      responseData.analyzed = true;
    } catch (analysisError) {
      console.warn('⚠️ Analysis failed:', analysisError);
      responseData.analysis = 'Yanıtınız kaydedildi, teşekkürler!';
    }

    res.json({
      success: true,
      response: responseData,
      message: 'Yanıtınız başarıyla kaydedildi',
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Answer Question Error:', error);
    res.status(500).json({
      error: 'Yanıt kaydetme hatası',
      message: 'Yanıtınız kaydedilemedi.'
    });
  }
});

// Kullanıcı istatistikleri
router.get('/stats/:userId', async (req, res) => {
  try {
    const { userId } = req.params;
    const { timeframe = '30d' } = req.query;

    // Mock istatistik verileri
    const mockStats = {
      userId: userId,
      timeframe: timeframe,
      totalAnswers: 45,
      streakDays: 7,
      favoriteCategory: 'kişisel',
      answeredToday: true,
      categories: {
        'kişisel': 18,
        'duygu': 15,
        'spor': 8,
        'güncel': 4
      },
      recentAnswers: [
        {
          date: new Date().toLocaleDateString('tr-TR'),
          question: 'Bugün nasıl hissediyorsunuz?',
          answer: 'Harika',
          category: 'duygu'
        },
        {
          date: new Date(Date.now() - 24 * 60 * 60 * 1000).toLocaleDateString('tr-TR'),
          question: 'En sevdiğiniz mevsim?',
          answer: 'Sonbahar',
          category: 'kişisel'
        }
      ]
    };

    res.json({
      success: true,
      stats: mockStats,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ User Stats Error:', error);
    res.status(500).json({
      error: 'İstatistik hatası',
      message: 'Kullanıcı istatistikleri alınamadı.'
    });
  }
});

// Soru kategorilerini getir
router.get('/categories', async (req, res) => {
  try {
    const categories = [
      {
        id: 'kişisel',
        name: 'Kişisel Tercihler',
        description: 'Hobiler, ilgi alanları, tercihler',
        icon: '👤',
        color: '#3b82f6'
      },
      {
        id: 'duygu',
        name: 'Duygusal Durum',
        description: 'Ruh hali, hisler, motivasyon',
        icon: '😊',
        color: '#10b981'
      },
      {
        id: 'spor',
        name: 'Spor & Aktivite',
        description: 'Spor tercihleri, fiziksel aktiviteler',
        icon: '⚽',
        color: '#f59e0b'
      },
      {
        id: 'güncel',
        name: 'Güncel Olaylar',
        description: 'Haberler, gündem, görüşler',
        icon: '📰',
        color: '#ef4444'
      },
      {
        id: 'yaşam',
        name: 'Yaşam Tarzı',
        description: 'Günlük alışkanlıklar, yaşam tarzı',
        icon: '🏡',
        color: '#8b5cf6'
      }
    ];

    res.json({
      success: true,
      categories: categories,
      totalCategories: categories.length,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Question Categories Error:', error);
    res.status(500).json({
      error: 'Kategori hatası',
      message: 'Soru kategorileri alınamadı.'
    });
  }
});

// Belirli kategori için sorular
router.get('/category/:categoryId', async (req, res) => {
  try {
    const { categoryId } = req.params;
    const { limit = 5 } = req.query;

    // Kategoriye özel sorular oluştur
    const categoryPrompt = `${categoryId} kategorisinde ${limit} adet interaktif soru oluştur. JSON formatında ver.`;
    
    const questionsResponse = await openaiService.chatCompletion([
      {
        role: 'system',
        content: `Sen ${categoryId} kategorisinde sorular üreten uzmanısın. Her soru 4 seçenekli olmalı ve kullanıcı etkileşimini artırmalı.`
      },
      {
        role: 'user',
        content: categoryPrompt
      }
    ], { maxTokens: 600, temperature: 0.8 });

    let questions;
    try {
      questions = JSON.parse(questionsResponse);
    } catch (parseError) {
      // Default sorular
      questions = {
        questions: [
          {
            text: `${categoryId} kategorisinde bir soru`,
            category: categoryId,
            options: ["Seçenek 1", "Seçenek 2", "Seçenek 3", "Seçenek 4"]
          }
        ]
      };
    }

    res.json({
      success: true,
      category: categoryId,
      questions: questions.questions || questions,
      count: questions.questions?.length || 1,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Category Questions Error:', error);
    res.status(500).json({
      error: 'Kategori soruları hatası',
      message: 'Kategori soruları oluşturulamadı.'
    });
  }
});

export default router;


