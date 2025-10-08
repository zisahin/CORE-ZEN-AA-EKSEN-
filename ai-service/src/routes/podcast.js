import express from 'express';
import openaiService from '../services/openai.js';

const router = express.Router();

// Podcast içeriği oluştur
router.post('/generate', async (req, res) => {
  try {
    const { newsContent, title, category, tone = 'friendly' } = req.body;

    if (!newsContent) {
      return res.status(400).json({
        error: 'Haber içeriği gerekli',
        message: 'Podcast için haber içeriği bulunamadı'
      });
    }

    // Haberi podcast formatında yeniden yaz
    const podcastContent = await openaiService.generatePodcastContent(
      `Başlık: ${title || 'Başlık belirtilmemiş'}\nKategori: ${category || 'Genel'}\n\nİçerik: ${newsContent}`,
      { 
        temperature: tone === 'professional' ? 0.6 : 0.8,
        maxTokens: 800 
      }
    );

    // Podcast meta bilgileri
    const podcastMeta = {
      id: `podcast_${Date.now()}`,
      title: `🎙️ ${title || 'Günün Haberi'}`,
      description: `${category || 'Genel'} kategorisinden podcast`,
      duration: Math.ceil(podcastContent.length / 10), // Yaklaşık okuma süresi (saniye)
      category: category || 'Genel',
      tone: tone,
      createdAt: new Date().toISOString()
    };

    res.json({
      success: true,
      podcast: {
        ...podcastMeta,
        content: podcastContent,
        originalLength: newsContent.length,
        podcastLength: podcastContent.length
      },
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Podcast Generation Error:', error);
    res.status(500).json({
      error: 'Podcast oluşturma hatası',
      message: 'Podcast içeriği oluşturulamadı.'
    });
  }
});

// Çoklu haber için podcast oluştur
router.post('/batch', async (req, res) => {
  try {
    const { newsItems = [], theme = 'Günün Özeti' } = req.body;

    if (newsItems.length === 0) {
      return res.status(400).json({
        error: 'Haber listesi boş',
        message: 'Podcast için haber listesi bulunamadı'
      });
    }

    // Tüm haberleri birleştirip tek podcast yap
    const combinedContent = newsItems.map(news => 
      `• ${news.title}\n${news.content}\n`
    ).join('\n');

    const podcastContent = await openaiService.generatePodcastContent(
      `Tema: ${theme}\n\nHaberler:\n${combinedContent}`,
      { 
        temperature: 0.8,
        maxTokens: 1200 
      }
    );

    const podcastMeta = {
      id: `podcast_batch_${Date.now()}`,
      title: `🎙️ ${theme}`,
      description: `${newsItems.length} haberin podcast özeti`,
      duration: Math.ceil(podcastContent.length / 8), // Biraz daha uzun okuma süresi
      newsCount: newsItems.length,
      categories: [...new Set(newsItems.map(n => n.category))],
      createdAt: new Date().toISOString()
    };

    res.json({
      success: true,
      podcast: {
        ...podcastMeta,
        content: podcastContent
      },
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Batch Podcast Error:', error);
    res.status(500).json({
      error: 'Toplu podcast hatası',
      message: 'Podcast içeriği oluşturulamadı.'
    });
  }
});

// Podcast listesi
router.get('/', async (req, res) => {
  try {
    const { category, limit = 10 } = req.query;
    
    // Mock podcast listesi
    const mockPodcasts = [
      {
        id: 'podcast_1',
        title: '🎙️ Günün Ekonomi Haberleri',
        description: 'Ekonomi kategorisinden podcast',
        duration: 180,
        category: 'Ekonomi',
        createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
        listenCount: 45
      },
      {
        id: 'podcast_2',
        title: '🎙️ Spor Dünyasından Haberler', 
        description: 'Spor kategorisinden podcast',
        duration: 240,
        category: 'Spor',
        createdAt: new Date(Date.now() - 48 * 60 * 60 * 1000).toISOString(),
        listenCount: 72
      },
      {
        id: 'podcast_3',
        title: '🎙️ Teknoloji Gelişmeleri',
        description: 'Teknoloji kategorisinden podcast', 
        duration: 200,
        category: 'Teknoloji',
        createdAt: new Date(Date.now() - 72 * 60 * 60 * 1000).toISOString(),
        listenCount: 38
      }
    ];

    let filteredPodcasts = mockPodcasts;
    
    if (category) {
      filteredPodcasts = filteredPodcasts.filter(p => 
        p.category.toLowerCase() === category.toLowerCase()
      );
    }

    filteredPodcasts = filteredPodcasts.slice(0, parseInt(limit));

    res.json({
      success: true,
      podcasts: filteredPodcasts,
      total: filteredPodcasts.length,
      filters: { category, limit },
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Podcast List Error:', error);
    res.status(500).json({
      error: 'Podcast liste hatası',
      message: 'Podcast listesi alınamadı.'
    });
  }
});

// Belirli podcast içeriğini getir
router.get('/:podcastId', async (req, res) => {
  try {
    const { podcastId } = req.params;
    
    // Mock podcast detayı
    const mockPodcast = {
      id: podcastId,
      title: '🎙️ Günün Ekonomi Haberleri',
      description: 'Ekonomi kategorisinden podcast',
      content: `Arkadaşlar, merhaba! Ben bugün sizlere ekonomi dünyasından çok önemli gelişmeleri aktaracağım. 

Biliyorsunuz, ekonomi konuları bazen biraz karmaşık gelebiliyor ama hiç merak etmeyin, ben bunları sizin için sadeleştireceğim.

İşte bugünkü gündemimizde şunlar var: Merkez Bankası'nın aldığı yeni kararlar, döviz kurlarındaki son durum ve tabii ki borsadaki hareketlilik...

Şimdi, gelin birlikte bu konuları inceleyelim. İlk olarak, Merkez Bankası'nın bugün açıkladığı faiz kararından başlayalım...`,
      duration: 180,
      category: 'Ekonomi',
      tone: 'friendly',
      createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
      listenCount: 45,
      transcript: true
    };

    res.json({
      success: true,
      podcast: mockPodcast,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Podcast Get Error:', error);
    res.status(500).json({
      error: 'Podcast detay hatası',
      message: 'Podcast detayları alınamadı.'
    });
  }
});

// Podcast kategorilerini getir
router.get('/categories/list', async (req, res) => {
  try {
    const categories = [
      {
        id: 'ekonomi',
        name: 'Ekonomi',
        description: 'Ekonomi ve finans haberleri',
        icon: '📈',
        podcastCount: 15
      },
      {
        id: 'siyaset',
        name: 'Siyaset',
        description: 'Siyasi gelişmeler ve analizler',
        icon: '🏛️',
        podcastCount: 23
      },
      {
        id: 'spor',
        name: 'Spor',
        description: 'Spor haberleri ve yorumları',
        icon: '⚽',
        podcastCount: 18
      },
      {
        id: 'teknoloji',
        name: 'Teknoloji',
        description: 'Teknoloji dünyasından haberler',
        icon: '💻',
        podcastCount: 12
      },
      {
        id: 'kultur',
        name: 'Kültür-Sanat',
        description: 'Kültür ve sanat etkinlikleri',
        icon: '🎭',
        podcastCount: 8
      }
    ];

    res.json({
      success: true,
      categories: categories,
      totalPodcasts: categories.reduce((sum, cat) => sum + cat.podcastCount, 0),
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Podcast Categories Error:', error);
    res.status(500).json({
      error: 'Kategori listesi hatası',
      message: 'Kategori listesi alınamadı.'
    });
  }
});

export default router;


