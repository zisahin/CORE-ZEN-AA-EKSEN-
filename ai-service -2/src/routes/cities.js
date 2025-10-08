import express from 'express';

const router = express.Router();

// Türkiye'deki şehirler listesi
const TURKISH_CITIES = [
  'İstanbul', 'Ankara', 'İzmir', 'Bursa', 'Antalya', 'Adana', 'Konya', 
  'Gaziantep', 'Şanlıurfa', 'Kocaeli', 'Mersin', 'Diyarbakır', 'Hatay', 
  'Manisa', 'Kayseri', 'Samsun', 'Balıkesir', 'Kahramanmaraş', 'Van', 
  'Aydın', 'Denizli', 'Sakarya', 'Tekirdağ', 'Muğla', 'Eskişehir', 
  'Mardin', 'Malatya', 'Erzurum', 'Trabzon', 'Elazığ', 'Ordu', 'Afyon', 
  'Sivas', 'Tokat', 'Çorum', 'Kütahya', 'İskenderun', 'Antakya'
];

// Kategori ID'leri
const CATEGORIES = ['guncel', 'ekonomi', 'spor', 'teknoloji', 'dunya'];

/**
 * RSS feed'den haberleri çek ve parse et
 */
async function fetchRSSNews(category) {
  try {
    const rssUrl = `https://www.aa.com.tr/tr/rss/default?cat=${category}`;
    
    const response = await fetch(rssUrl, {
      headers: {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
      }
    });
    
    if (!response.ok) {
      console.warn(`⚠️ RSS hatası (${category}):`, response.status);
      return [];
    }
    
    const xmlText = await response.text();
    const items = extractRSSItems(xmlText);
    
    return items.map(item => ({
      title: item.title,
      description: item.description,
      link: item.link,
      pubDate: new Date(item.pubDate),
      category
    }));
  } catch (error) {
    console.error(`❌ RSS çekme hatası (${category}):`, error.message);
    return [];
  }
}

/**
 * Basit XML parsing (RSS items)
 */
function extractRSSItems(xmlText) {
  const items = [];
  const itemRegex = /<item>([\s\S]*?)<\/item>/g;
  
  let match;
  while ((match = itemRegex.exec(xmlText)) !== null) {
    const itemXml = match[1];
    
    items.push({
      title: extractXMLTag(itemXml, 'title'),
      link: extractXMLTag(itemXml, 'link'),
      description: extractXMLTag(itemXml, 'description'),
      pubDate: extractXMLTag(itemXml, 'pubDate')
    });
  }
  
  return items;
}

/**
 * XML tag'inden içeriği çıkar
 */
function extractXMLTag(xml, tag) {
  const regex = new RegExp(`<${tag}>(.*?)</${tag}>`, 's');
  const match = xml.match(regex);
  return match ? cleanText(match[1]) : '';
}

/**
 * Metni temizle
 */
function cleanText(text) {
  return text
    .replace(/<!\[CDATA\[(.*?)\]\]>/g, '$1')
    .replace(/<[^>]+>/g, '')
    .replace(/&quot;/g, '"')
    .replace(/&amp;/g, '&')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&#39;/g, "'")
    .trim();
}

/**
 * Haber başlığında veya açıklamasında şehir adı var mı kontrol et
 */
function findCityInNews(news) {
  const text = `${news.title} ${news.description}`.toLowerCase();
  
  for (const city of TURKISH_CITIES) {
    const cityLower = city.toLowerCase()
      .replace(/ı/g, 'i')
      .replace(/ğ/g, 'g')
      .replace(/ü/g, 'u')
      .replace(/ş/g, 's')
      .replace(/ö/g, 'o')
      .replace(/ç/g, 'c');
    
    if (text.includes(cityLower) || text.includes(city.toLowerCase())) {
      return city;
    }
  }
  
  return null;
}

/**
 * Tüm şehirlerin haber istatistiklerini getir
 * GET /api/cities/stats
 */
router.get('/stats', async (req, res) => {
  try {
    console.log('📊 Şehir istatistikleri hesaplanıyor...');
    
    // Tüm kategorilerden haberleri çek
    const allNewsPromises = CATEGORIES.map(cat => fetchRSSNews(cat));
    const allNewsArrays = await Promise.all(allNewsPromises);
    const allNews = allNewsArrays.flat();
    
    console.log(`📰 Toplam ${allNews.length} haber çekildi`);
    
    // Her şehir için haber sayılarını hesapla
    const cityStats = {};
    
    TURKISH_CITIES.forEach(city => {
      cityStats[city] = {
        id: city.toLowerCase().replace(/ı/g, 'i').replace(/ğ/g, 'g').replace(/ü/g, 'u').replace(/ş/g, 's').replace(/ö/g, 'o').replace(/ç/g, 'c'),
        name: city,
        totalNewsCount: 0,
        newsByCategory: {}
      };
    });
    
    // Haberleri şehirlere ata
    allNews.forEach(news => {
      const city = findCityInNews(news);
      if (city && cityStats[city]) {
        cityStats[city].totalNewsCount++;
        cityStats[city].newsByCategory[news.category] = (cityStats[city].newsByCategory[news.category] || 0) + 1;
      }
    });
    
    // Array'e çevir ve sadece haber olanları döndür
    const citiesWithNews = Object.values(cityStats)
      .filter(city => city.totalNewsCount > 0)
      .sort((a, b) => b.totalNewsCount - a.totalNewsCount);
    
    console.log(`✅ ${citiesWithNews.length} şehirde haber bulundu`);
    
    res.json({
      success: true,
      totalCities: citiesWithNews.length,
      totalNews: allNews.length,
      cities: citiesWithNews,
      timestamp: new Date().toISOString()
    });
    
  } catch (error) {
    console.error('❌ Şehir istatistikleri hatası:', error);
    res.status(500).json({
      success: false,
      error: 'Şehir istatistikleri alınamadı',
      message: error.message
    });
  }
});

/**
 * Belirli bir şehrin haberlerini getir
 * GET /api/cities/:cityName/news
 */
router.get('/:cityName/news', async (req, res) => {
  try {
    const { cityName } = req.params;
    const { category } = req.query;
    
    console.log(`📰 ${cityName} şehri için haberler getiriliyor...`);
    
    // Kategori filtreleme
    const categoriesToFetch = category ? [category] : CATEGORIES;
    
    const allNewsPromises = categoriesToFetch.map(cat => fetchRSSNews(cat));
    const allNewsArrays = await Promise.all(allNewsPromises);
    const allNews = allNewsArrays.flat();
    
    // Şehre ait haberleri filtrele
    const cityNews = allNews.filter(news => {
      const foundCity = findCityInNews(news);
      return foundCity && foundCity.toLowerCase().includes(cityName.toLowerCase());
    });
    
    console.log(`✅ ${cityName} için ${cityNews.length} haber bulundu`);
    
    res.json({
      success: true,
      city: cityName,
      totalNews: cityNews.length,
      news: cityNews,
      timestamp: new Date().toISOString()
    });
    
  } catch (error) {
    console.error(`❌ ${req.params.cityName} haberleri hatası:`, error);
    res.status(500).json({
      success: false,
      error: 'Şehir haberleri alınamadı',
      message: error.message
    });
  }
});

export default router;

