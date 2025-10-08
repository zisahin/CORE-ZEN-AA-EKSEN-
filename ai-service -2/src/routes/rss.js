import express from 'express';

const router = express.Router();

// AA RSS Kategorileri
const AA_CATEGORIES = {
  guncel: 'Güncel',
  ekonomi: 'Ekonomi',
  spor: 'Spor',
  dunya: 'Dünya',
  teknoloji: 'Teknoloji',
  saglik: 'Sağlık',
  egitim: 'Eğitim',
  kultur: 'Kültür',
  siyaset: 'Siyaset',
  yasam: 'Yaşam'
};

// RSS'den haberleri çek
router.get('/:category?', async (req, res) => {
  try {
    const { category = 'guncel' } = req.params;
    const { limit = 20 } = req.query;
    
    console.log(`📰 RSS isteği: ${category} kategorisi`);
    
    const rssUrl = `https://www.aa.com.tr/tr/rss/default?cat=${category}`;
    
    const response = await fetch(rssUrl, {
      headers: {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
      }
    });
    
    if (!response.ok) {
      throw new Error(`RSS yanıt hatası: ${response.status}`);
    }
    
    const xmlText = await response.text();
    
    // Basit XML parsing
    const items = extractRSSItems(xmlText);
    const news = items.slice(0, limit).map((item, index) => ({
      id: `aa_${category}_${index}_${Date.now()}`,
      title: item.title,
      link: item.link,
      description: item.description,
      content: item.description,
      pubDate: new Date(item.pubDate),
      category: AA_CATEGORIES[category] || 'Genel',
      categoryId: category,
      source: 'AA'
    }));

    res.json({
      success: true,
      category: AA_CATEGORIES[category],
      categoryId: category,
      news: news,
      total: news.length,
      timestamp: new Date().toISOString()
    });

    console.log(`✅ RSS başarılı: ${news.length} haber alındı`);

  } catch (error) {
    console.error('❌ RSS hatası:', error.message);
    res.status(500).json({
      error: 'RSS feed hatası',
      message: error.message,
      category: req.params.category
    });
  }
});

// Tüm kategorileri karışık getir (ana sayfa için)
router.get('/all/mixed', async (req, res) => {
  try {
    const { limit = 50 } = req.query;
    const categories = ['guncel', 'ekonomi', 'spor', 'teknoloji', 'dunya'];
    
    console.log('📰 Karışık RSS isteği başlatılıyor...');
    
    const newsPromises = categories.map(async (category) => {
      try {
        const rssUrl = `https://www.aa.com.tr/tr/rss/default?cat=${category}`;
        const response = await fetch(rssUrl, { 
          headers: {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
          }
        });
        
        if (!response.ok) return [];
        
        const xmlText = await response.text();
        const items = extractRSSItems(xmlText);
        
        return items.slice(0, 5).map((item, index) => ({
          id: `aa_${category}_${index}_${Date.now()}`,
          title: item.title,
          link: item.link,
          description: item.description,
          content: item.description,
          pubDate: new Date(item.pubDate),
          category: AA_CATEGORIES[category],
          categoryId: category,
          source: 'AA'
        }));
      } catch (error) {
        console.warn(`⚠️ ${category} kategorisi alınamadı:`, error.message);
        return [];
      }
    });

    const results = await Promise.all(newsPromises);
    const allNews = results.flat().sort((a, b) => b.pubDate - a.pubDate).slice(0, limit);

    res.json({
      success: true,
      news: allNews,
      total: allNews.length,
      categories: categories,
      timestamp: new Date().toISOString()
    });

    console.log(`✅ Karışık RSS başarılı: ${allNews.length} haber`);

  } catch (error) {
    console.error('❌ Karışık RSS hatası:', error);
    res.status(500).json({
      error: 'RSS karışık feed hatası',
      message: error.message
    });
  }
});

// Kategorileri listele
router.get('/categories/list', (req, res) => {
  const categories = Object.entries(AA_CATEGORIES).map(([id, name]) => ({
    id,
    name,
    rssUrl: `https://www.aa.com.tr/tr/rss/default?cat=${id}`,
    icon: getCategoryIcon(id)
  }));

  res.json({
    success: true,
    categories,
    total: categories.length,
    timestamp: new Date().toISOString()
  });
});

// XML parsing fonksiyonu
function extractRSSItems(xmlText) {
  try {
    const items = [];
    const itemRegex = /<item>(.*?)<\/item>/gs;
    let match;
    
    while ((match = itemRegex.exec(xmlText)) !== null) {
      const itemXml = match[1];
      
      const title = extractXMLTag(itemXml, 'title');
      const link = extractXMLTag(itemXml, 'link');
      const description = extractXMLTag(itemXml, 'description');
      const pubDate = extractXMLTag(itemXml, 'pubDate');
      
      if (title && link) {
        items.push({
          title: cleanText(title),
          link: cleanText(link),
          description: cleanText(description),
          pubDate: pubDate || new Date().toISOString()
        });
      }
    }
    
    return items;
  } catch (error) {
    console.error('XML parsing hatası:', error);
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

function getCategoryIcon(categoryId) {
  const icons = {
    guncel: '📰',
    ekonomi: '💰',
    spor: '⚽',
    dunya: '🌍',
    teknoloji: '💻',
    saglik: '🏥',
    egitim: '📚',
    kultur: '🎭',
    siyaset: '🏛️',
    yasam: '🏡'
  };
  return icons[categoryId] || '📄';
}

export default router;
