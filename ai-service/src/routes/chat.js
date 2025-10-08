import express from 'express';
import openaiService from '../services/openai.js';

const router = express.Router();

// Chat endpoint - Ana sohbet fonksiyonu
router.post('/', async (req, res) => {
  try {
    const { message, conversationHistory = [] } = req.body;

    if (!message) {
      return res.status(400).json({
        error: 'Mesaj gerekli',
        message: 'Lütfen bir mesaj gönderin'
      });
    }

    // Konuşma geçmişini hazırla
    const messages = [
      {
        role: 'system',
        content: `Sen AA Habercilik sitesinin AI asistanısın. Adın "AA AI Asistan".

GÖREVLERİN:
- Haberyeyle ilgili sorulara yanıt vermek
- Haber özetleri hazırlamak  
- Güncel olayları analiz etmek
- Kullanıcıya yardımcı olmak

KURALLAR:
- Her zaman Türkçe yanıt ver
- Samimi ama profesyonel ol
- Güvenilir bilgi ver
- Eğer emin değilsen belirt
- Kısa ve öz yanıtlar ver
- Emojiler kullanabilirsin 😊

ÖZEL KOMUTLAR:
- "günün özeti" → Bugünkü haberleri özetle
- "haftalık ekonomi" → Ekonomi haberlerini özetle  
- "haftalık siyaset" → Siyasi gelişmeleri özetle
- Spor takımları (Fenerbahçe, Galatasaray vb.) → O takımla ilgili haberler`
      },
      ...conversationHistory.map(msg => ({
        role: msg.isUser ? 'user' : 'assistant',
        content: msg.text
      })),
      {
        role: 'user',
        content: message
      }
    ];

    // ChatGPT'den yanıt al
    const aiResponse = await openaiService.chatCompletion(messages, {
      maxTokens: 800,
      temperature: 0.7
    });

    res.json({
      success: true,
      response: aiResponse,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Chat Error:', error);
    res.status(500).json({
      error: 'Sohbet servisi hatası',
      message: 'Üzgünüm, şu anda yanıt veremiyorum. Lütfen daha sonra tekrar deneyin.'
    });
  }
});

// Hızlı öneriler endpoint'i
router.get('/suggestions', async (req, res) => {
  try {
    const suggestions = [
      {
        title: "Günün Özeti",
        prompt: "Bugünkü önemli haberleri özetle",
        category: "daily"
      },
      {
        title: "Haftalık Ekonomi", 
        prompt: "Bu haftaki ekonomi haberlerini özetle",
        category: "weekly"
      },
      {
        title: "Haftalık Siyaset",
        prompt: "Bu haftaki siyasi gelişmeleri özetle", 
        category: "weekly"
      },
      {
        title: "Genel Haberler",
        prompt: "Güncel önemli haberleri anlat",
        category: "general"
      },
      {
        title: "Spor Haberleri",
        prompt: "Spor dünyasından son dakika haberleri",
        category: "sports"  
      },
      {
        title: "Teknoloji Haberleri",
        prompt: "Teknoloji alanındaki yeni gelişmeler",
        category: "tech"
      }
    ];

    // Saat ve güne göre öneri sıralaması
    const hour = new Date().getHours();
    const dayOfWeek = new Date().getDay();
    
    let prioritizedSuggestions = [...suggestions];
    
    // Sabah saatleri için günün özeti öncelikli
    if (hour >= 6 && hour <= 10) {
      prioritizedSuggestions = suggestions.filter(s => s.category === 'daily')
        .concat(suggestions.filter(s => s.category !== 'daily'));
    }
    
    // Pazartesi için haftalık özetler öncelikli  
    if (dayOfWeek === 1) {
      prioritizedSuggestions = suggestions.filter(s => s.category === 'weekly')
        .concat(suggestions.filter(s => s.category !== 'weekly'));
    }

    res.json({
      success: true,
      suggestions: prioritizedSuggestions,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Suggestions Error:', error);
    res.status(500).json({
      error: 'Öneri servisi hatası',
      message: 'Öneriler yüklenemedi'
    });
  }
});

export default router;


