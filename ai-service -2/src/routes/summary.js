import express from 'express';
import openaiService from '../services/openai.js';

const router = express.Router();

// Tek haber özetleme
router.post('/news', async (req, res) => {
  try {
    const { content, title, category } = req.body;

    if (!content) {
      return res.status(400).json({
        error: 'Haber içeriği gerekli',
        message: 'Özetlenecek haber içeriği bulunamadı'
      });
    }

    // Haber içeriğini AI ile özetle
    const summary = await openaiService.summarizeNews(
      `Başlık: ${title || 'Başlık belirtilmemiş'}\nKategori: ${category || 'Genel'}\n\nİçerik: ${content}`
    );

    res.json({
      success: true,
      summary: summary,
      originalLength: content.length,
      summaryLength: summary.length,
      compressionRatio: Math.round((1 - summary.length / content.length) * 100),
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ News Summary Error:', error);
    res.status(500).json({
      error: 'Özet servisi hatası',
      message: 'Haber özetlenemedi. Lütfen daha sonra tekrar deneyin.'
    });
  }
});

// Kategori bazlı özet
router.post('/category', async (req, res) => {
  try {
    const { category, timeframe = 'günlük', newsItems = [] } = req.body;

    if (!category) {
      return res.status(400).json({
        error: 'Kategori gerekli',
        message: 'Özetlenecek kategori belirtilmedi'
      });
    }

    if (newsItems.length === 0) {
      return res.status(400).json({
        error: 'Haber listesi boş',
        message: 'Özetlenecek haber bulunamadı'
      });
    }

    // Kategori özetini oluştur
    const summary = await openaiService.generateCategorySummary(
      category,
      timeframe,
      newsItems
    );

    res.json({
      success: true,
      category: category,
      timeframe: timeframe,
      summary: summary,
      newsCount: newsItems.length,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Category Summary Error:', error);
    res.status(500).json({
      error: 'Kategori özet servisi hatası',
      message: 'Kategori özeti oluşturulamadı.'
    });
  }
});

// Günlük ve haftalık özetler
router.get('/daily', async (req, res) => {
  try {
    const { category = 'genel' } = req.query;
    
    // Burada normalde veritabanından o günün haberlerini çekeceksiniz
    // Şimdilik mock data kullanıyoruz
    const mockNewsItems = [
      {
        title: "Günün en önemli haberi",
        category: category,
        content: "Mock haber içeriği...",
        publishedAt: new Date().toISOString()
      }
    ];

    const summary = await openaiService.generateCategorySummary(
      category,
      'günlük',
      mockNewsItems
    );

    res.json({
      success: true,
      type: 'daily',
      category: category,
      summary: summary,
      date: new Date().toLocaleDateString('tr-TR'),
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Daily Summary Error:', error);
    res.status(500).json({
      error: 'Günlük özet servisi hatası',
      message: 'Günlük özet oluşturulamadı.'
    });
  }
});

router.get('/weekly', async (req, res) => {
  try {
    const { category = 'genel' } = req.query;
    
    // Haftalık haberler mock data
    const mockNewsItems = [
      {
        title: "Haftanın önemli haberi 1",
        category: category,
        content: "Mock haber içeriği 1...",
        publishedAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
      },
      {
        title: "Haftanın önemli haberi 2", 
        category: category,
        content: "Mock haber içeriği 2...",
        publishedAt: new Date(Date.now() - 48 * 60 * 60 * 1000).toISOString()
      }
    ];

    const summary = await openaiService.generateCategorySummary(
      category,
      'haftalık',
      mockNewsItems
    );

    res.json({
      success: true,
      type: 'weekly',
      category: category,
      summary: summary,
      weekStart: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toLocaleDateString('tr-TR'),
      weekEnd: new Date().toLocaleDateString('tr-TR'),
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Weekly Summary Error:', error);
    res.status(500).json({
      error: 'Haftalık özet servisi hatası',
      message: 'Haftalık özet oluşturulamadı.'
    });
  }
});

// Çoklu haber özetleme
router.post('/batch', async (req, res) => {
  try {
    const { newsItems = [] } = req.body;

    if (newsItems.length === 0) {
      return res.status(400).json({
        error: 'Haber listesi boş',
        message: 'Özetlenecek haber listesi bulunamadı'
      });
    }

    // Her haberi ayrı ayrı özetle
    const summaries = await Promise.all(
      newsItems.map(async (news) => {
        try {
          const summary = await openaiService.summarizeNews(
            `Başlık: ${news.title}\nKategori: ${news.category || 'Genel'}\n\nİçerik: ${news.content}`
          );
          return {
            id: news.id,
            title: news.title,
            summary: summary,
            originalLength: news.content?.length || 0,
            success: true
          };
        } catch (error) {
          return {
            id: news.id,
            title: news.title,
            error: 'Özet oluşturulamadı',
            success: false
          };
        }
      })
    );

    const successCount = summaries.filter(s => s.success).length;
    const failCount = summaries.length - successCount;

    res.json({
      success: true,
      summaries: summaries,
      stats: {
        total: summaries.length,
        successful: successCount,
        failed: failCount,
        successRate: Math.round((successCount / summaries.length) * 100)
      },
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('❌ Batch Summary Error:', error);
    res.status(500).json({
      error: 'Toplu özet servisi hatası',
      message: 'Haberler özetlenemedi.'
    });
  }
});

export default router;


