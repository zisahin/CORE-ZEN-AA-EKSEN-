// utils/rss.js
export async function fetchAANews(category = 'guncel') {
  try {
    const response = await fetch(
      `https://api.rss2json.com/v1/api.json?rss_url=https://www.aa.com.tr/tr/rss/default?cat=${category}`
    );
    
    const data = await response.json();
    
    return data.items.map((item, index) => ({
      id: item.guid || item.link,
      title: item.title,
      content: item.description,
      link: item.link,
      // Çalışan fotoğraf URL'leri
      image: item.enclosure?.link || 
             item.thumbnail || 
             `https://picsum.photos/400/200?random=${index}`, // ✅ Bu çalışır
      publishedAt: new Date(item.pubDate),
      category: category,
      source: 'Anadolu Ajansı'
    }));
    
  } catch (error) {
    console.error('RSS fetch error:', error);
    return [];
  }
}