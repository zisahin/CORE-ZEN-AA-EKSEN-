import express from 'express';
import openaiService from '../services/openai.js';

const router = express.Router();

// Timeline analizi ve önerisi
router.post('/analyze', async (req, res) => {
  try {
    const { newsItems = [] } = req.body;

    if (newsItems.length === 0) {
      return res.status(400).json({
        error: 'Haber listesi boş',
        message: 'Analiz edilecek haber bulunamadı'
      });
    }

    // AI ile timeline analizi yap
    const analysisResult = await openaiService.analyzeForTimeline(newsItems);
    
    // JSON parse etmeye çalış
    let analysis;
    try {
      analysis = JSON.parse(analysisResult);
    } catch (parseError) {
      // Eğer JSON parse edilemezse, düz metin olarak döndür
      analysis = {
        message: analysisResult,
        timelineRecommendations: [],
        categories: []
      };
    }

    res.json({
      success: true,
      analysis: analysis,
      analyzedCount: newsItems.length,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Timeline Analysis Error:', error);
    res.status(500).json({
      error: 'Timeline analiz servisi hatası',
      message: 'Haberler analiz edilemedi.'
    });
  }
});

// Timeline kategorilerini getir
router.get('/categories', async (req, res) => {
  try {
    // Önceden tanımlanmış timeline kategorileri
    const categories = [
      {
        id: 'politics',
        name: 'Siyaset',
        description: 'Seçimler, politika değişiklikleri, önemli kararlar',
        color: '#dc2626',
        icon: '🏛️'
      },
      {
        id: 'economy',
        name: 'Ekonomi', 
        description: 'Piyasa gelişmeleri, ekonomi politikaları, büyük projeler',
        color: '#059669',
        icon: '📈'
      },
      {
        id: 'sports',
        name: 'Spor',
        description: 'Transfer dönemleri, turnuvalar, büyük maçlar',
        color: '#2563eb',
        icon: '⚽'
      },
      {
        id: 'technology',
        name: 'Teknoloji',
        description: 'Yeni ürünler, şirket haberleri, teknolojik gelişmeler',
        color: '#7c3aed',
        icon: '💻'
      },
      {
        id: 'legal',
        name: 'Hukuk',
        description: 'Davalar, yasal süreçler, mahkeme kararları',
        color: '#b45309',
        icon: '⚖️'
      },
      {
        id: 'infrastructure',
        name: 'Altyapı',
        description: 'Büyük projeler, inşaatlar, ulaşım',
        color: '#0891b2',
        icon: '🏗️'
      }
    ];

    res.json({
      success: true,
      categories: categories,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Timeline Categories Error:', error);
    res.status(500).json({
      error: 'Kategori servisi hatası',
      message: 'Timeline kategorileri yüklenemedi.'
    });
  }
});

// Belirli bir timeline'ı getir
router.get('/:timelineId', async (req, res) => {
  try {
    const { timelineId } = req.params;
    
    // Burada normalde veritabanından timeline verilerini çekeceksiniz
    // Şimdilik mock data
    const mockTimeline = {
      id: timelineId,
      title: 'Marmaray Projesi',
      description: 'İstanbul Marmaray projesinin tüm aşamaları',
      category: 'infrastructure',
      status: 'active',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: new Date().toISOString(),
      events: [
        {
          id: '1',
          title: 'Proje İhalesi Açıklandı',
          content: 'Marmaray projesi için ihale süreci başladı...',
          date: '2024-01-10T10:00:00Z',
          category: 'infrastructure',
          isTimelineMaterial: true
        },
        {
          id: '2', 
          title: 'İnşaat Çalışmaları Başladı',
          content: 'Boğaz altında kazı işlemleri başladı...',
          date: '2024-01-15T14:30:00Z',
          category: 'infrastructure',
          isTimelineMaterial: true
        }
      ]
    };

    res.json({
      success: true,
      timeline: mockTimeline,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Timeline Get Error:', error);
    res.status(500).json({
      error: 'Timeline servisi hatası',
      message: 'Timeline verisi alınamadı.'
    });
  }
});

// Tüm aktif timeline'ları listele
router.get('/', async (req, res) => {
  try {
    const { category, status = 'active', limit = 10 } = req.query;
    
    // Mock timeline listesi
    const mockTimelines = [
      {
        id: 'marmaray',
        title: 'Marmaray Projesi',
        description: 'İstanbul ulaşım projesi',
        category: 'infrastructure',
        status: 'active',
        eventCount: 4,
        lastUpdate: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
        createdAt: '2024-01-01T00:00:00Z'
      },
      {
        id: 'secim2024',
        title: 'Yerel Seçimler 2024',
        description: 'Türkiye yerel seçim süreci',
        category: 'politics',
        status: 'completed',
        eventCount: 12,
        lastUpdate: new Date(Date.now() - 48 * 60 * 60 * 1000).toISOString(),
        createdAt: '2023-12-01T00:00:00Z'
      },
      {
        id: 'fenerbahce-transfer',
        title: 'Fenerbahçe Transfer Dönemi',
        description: '2024 yaz transfer dönemindeki gelişmeler',
        category: 'sports',
        status: 'active',
        eventCount: 8,
        lastUpdate: new Date(Date.now() - 12 * 60 * 60 * 1000).toISOString(),
        createdAt: '2024-06-01T00:00:00Z'
      }
    ];

    // Filtreleme
    let filteredTimelines = mockTimelines;
    
    if (category) {
      filteredTimelines = filteredTimelines.filter(t => t.category === category);
    }
    
    if (status) {
      filteredTimelines = filteredTimelines.filter(t => t.status === status);
    }

    // Sayfa sınırı
    filteredTimelines = filteredTimelines.slice(0, parseInt(limit));

    res.json({
      success: true,
      timelines: filteredTimelines,
      total: filteredTimelines.length,
      filters: { category, status, limit },
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Timeline List Error:', error);
    res.status(500).json({
      error: 'Timeline liste servisi hatası',
      message: 'Timeline listesi alınamadı.'
    });
  }
});

// Yeni timeline oluştur
router.post('/create', async (req, res) => {
  try {
    const { title, description, category, initialNews } = req.body;

    if (!title || !category) {
      return res.status(400).json({
        error: 'Eksik bilgi',
        message: 'Timeline başlığı ve kategorisi gereklidir'
      });
    }

    // Yeni timeline ID'si oluştur
    const timelineId = title.toLowerCase()
      .replace(/[çÇ]/g, 'c')
      .replace(/[ğĞ]/g, 'g') 
      .replace(/[ıİ]/g, 'i')
      .replace(/[öÖ]/g, 'o')
      .replace(/[şŞ]/g, 's')
      .replace(/[üÜ]/g, 'u')
      .replace(/[^a-z0-9]/g, '-')
      .replace(/-+/g, '-')
      .replace(/^-|-$/g, '');

    // Yeni timeline objesi
    const newTimeline = {
      id: timelineId,
      title: title,
      description: description || '',
      category: category,
      status: 'active',
      eventCount: initialNews ? 1 : 0,
      createdAt: new Date().toISOString(),
      lastUpdate: new Date().toISOString(),
      events: initialNews ? [
        {
          id: '1',
          title: initialNews.title,
          content: initialNews.content,
          date: new Date().toISOString(),
          category: category,
          isTimelineMaterial: true
        }
      ] : []
    };

    // Burada normalde veritabanına kayıt yapacaksınız

    res.json({
      success: true,
      timeline: newTimeline,
      message: 'Timeline başarıyla oluşturuldu',
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Timeline Create Error:', error);
    res.status(500).json({
      error: 'Timeline oluşturma hatası',
      message: 'Timeline oluşturulamadı.'
    });
  }
});

export default router;


