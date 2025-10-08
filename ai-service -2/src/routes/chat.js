import express from 'express';
import openaiService from '../services/openai.js';

const router = express.Router();

// Güncel haberleri çeken yardımcı fonksiyon
async function fetchRecentNews(category = null, limit = 10) {
  try {
    const rssUrl = category 
      ? `https://www.aa.com.tr/tr/rss/default?cat=${category}`
      : `https://www.aa.com.tr/tr/rss/default?cat=guncel`;
    
    const response = await fetch(rssUrl, {
      headers: {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
      }
    });
    
    if (!response.ok) {
      return [];
    }
    
    const xmlText = await response.text();
    const items = extractRSSItems(xmlText);
    
    return items.slice(0, limit).map(item => ({
      title: item.title,
      description: item.description,
      pubDate: item.pubDate
    }));
  } catch (error) {
    console.error('Haber çekme hatası:', error);
    return [];
  }
}

// XML parsing fonksiyonu
function extractRSSItems(xmlText) {
  try {
    const items = [];
    const itemRegex = /<item>(.*?)<\/item>/gs;
    let match;
    
    while ((match = itemRegex.exec(xmlText)) !== null) {
      const itemXml = match[1];
      
      const title = extractXMLTag(itemXml, 'title');
      const description = extractXMLTag(itemXml, 'description');
      const pubDate = extractXMLTag(itemXml, 'pubDate');
      
      if (title) {
        items.push({
          title: cleanText(title),
          description: cleanText(description),
          pubDate: pubDate || new Date().toISOString()
        });
      }
    }
    
    return items;
  } catch (error) {
    return [];
  }
}

function extractXMLTag(xml, tagName) {
  const regex = new RegExp(`<${tagName}[^>]*>(.*?)<\/${tagName}>`, 's');
  const match = xml.match(regex);
  return match ? match[1] : '';
}

function cleanText(text) {
  if (!text) return '';
  return text
    .replace(/<!\[CDATA\[(.*?)\]\]>/gs, '$1')
    .replace(/<[^>]*>/g, '')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&amp;/g, '&')
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/&nbsp;/g, ' ')
    .trim();
}

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

    // Kullanıcı mesajını analiz et ve kategori belirle
    const categoryMap = {
      'ekonomi': ['ekonomi', 'borsa', 'dolar', 'euro', 'faiz', 'enflasyon', 'döviz'],
      'spor': ['spor', 'futbol', 'basketbol', 'fenerbahçe', 'galatasaray', 'beşiktaş', 'trabzonspor'],
      'teknoloji': ['teknoloji', 'yapay zeka', 'bilgisayar', 'internet', 'yazılım'],
      'saglik': ['sağlık', 'hastane', 'doktor', 'tedavi', 'ilaç'],
      'dunya': ['dünya', 'uluslararası', 'amerika', 'avrupa', 'rusya']
    };
    
    let detectedCategory = null;
    const lowerMessage = message.toLowerCase();
    
    for (const [category, keywords] of Object.entries(categoryMap)) {
      if (keywords.some(keyword => lowerMessage.includes(keyword))) {
        detectedCategory = category;
        break;
      }
    }

    // Güncel haberleri çek
    let newsContext = '';
    const recentNews = await fetchRecentNews(detectedCategory, 5);
    
    if (recentNews.length > 0) {
      newsContext = '\n\nGÜNCEL HABERLER (Bugün):\n';
      recentNews.forEach((news, index) => {
        newsContext += `${index + 1}. ${news.title}\n`;
        if (news.description) {
          newsContext += `   Özet: ${news.description.substring(0, 150)}...\n`;
        }
      });
    }

    // Konuşma geçmişini hazırla
    const messages = [
      {
        role: 'system',
        content: `Sen AA Habercilik sitesinin AI asistanısın. Adın "AA AI Asistan".

GÖREVLERİN:
- Anadolu Ajansı haberlerini kullanarak sorulara yanıt vermek
- Güncel haberleri özetlemek ve analiz etmek
- Kullanıcıya haber bağlamında yardımcı olmak

KURALLAR:
- Her zaman Türkçe yanıt ver
- Samimi ama profesyonel ol
- Güncel haber verilerini kullan (sana verilen GÜNCEL HABERLER listesinden)
- Eğer bir haberi bilmiyorsan, "Elimdeki güncel haberlerde bu konu hakkında detaylı bilgi bulamadım" de
- Kısa ve öz yanıtlar ver (maksimum 5-6 cümle)
- Emojiler kullanabilirsin 😊
- Haber başlıklarını ve özetlerini kullan

ÖZEL KOMUTLAR:
- "günün özeti" → Bugünkü haberleri özetle
- "haftalık ekonomi" → Ekonomi haberlerini özetle  
- "haftalık siyaset" → Siyasi gelişmeleri özetle
- Kategori soruları → O kategorideki güncel haberlerden bahset${newsContext}`
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
      newsCount: recentNews.length,
      category: detectedCategory || 'guncel',
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


