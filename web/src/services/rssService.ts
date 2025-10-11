// RSS Servis - Anadolu Ajansı RSS Feed
export interface RSSNewsItem {
  id: string;
  title: string;
  content: string;
  link: string;
  image: string;
  publishedAt: Date;
  category: string;
  categoryId: string;
  source: string;
}

// AA RSS Kategorileri
export const RSS_CATEGORIES = {
  'son-dakika': { id: 'guncel', name: 'Son Dakika' },
  'gundem': { id: 'guncel', name: 'Gündem' },
  'ekonomi': { id: 'ekonomi', name: 'Ekonomi' },
  'spor': { id: 'spor', name: 'Spor' },
  'teknoloji': { id: 'teknoloji', name: 'Teknoloji' },
  'dunya': { id: 'dunya', name: 'Dünya' }
};

export const rssService = {
  // Kategoriye göre RSS haberlerini çek
  async fetchNewsByCategory(categorySlug: string, limit: number = 20): Promise<RSSNewsItem[]> {
    try {
      const category = RSS_CATEGORIES[categorySlug as keyof typeof RSS_CATEGORIES];
      if (!category) {
        throw new Error(`Geçersiz kategori: ${categorySlug}`);
      }

      const response = await fetch(
        `https://api.rss2json.com/v1/api.json?rss_url=https://www.aa.com.tr/tr/rss/default?cat=${category.id}`
      );
      
      if (!response.ok) {
        throw new Error(`RSS API hatası: ${response.status}`);
      }
      
      const data = await response.json();
      
      if (!data.items || data.items.length === 0) {
        return [];
      }
      
      return data.items.slice(0, limit).map((item: any, index: number) => ({
        id: item.guid || item.link || `${category.id}_${index}`,
        title: item.title || 'Başlık Yok',
        content: item.description || item.content || '',
        link: item.link || '#',
        image: item.enclosure?.link || 
               item.thumbnail || 
               `https://picsum.photos/400/300?random=${index}`,
        publishedAt: new Date(item.pubDate || Date.now()),
        category: category.name,
        categoryId: category.id,
        source: 'Anadolu Ajansı'
      }));
      
    } catch (error) {
      console.error('RSS fetch error:', error);
      return [];
    }
  },

  // Tüm kategorilerden karışık haberler çek (Ana sayfa için)
  async fetchMixedNews(limit: number = 30): Promise<RSSNewsItem[]> {
    try {
      const categories = ['guncel', 'ekonomi', 'spor', 'teknoloji', 'dunya'];
      const newsPerCategory = Math.ceil(limit / categories.length);
      
      const promises = categories.map(catId => {
        return fetch(
          `https://api.rss2json.com/v1/api.json?rss_url=https://www.aa.com.tr/tr/rss/default?cat=${catId}`
        ).then(res => res.json());
      });

      const results = await Promise.all(promises);
      const allNews: RSSNewsItem[] = [];

      results.forEach((data, idx) => {
        const catId = categories[idx];
        const catName = Object.values(RSS_CATEGORIES).find(c => c.id === catId)?.name || 'Genel';
        
        if (data.items) {
          const items = data.items.slice(0, newsPerCategory).map((item: any, index: number) => ({
            id: item.guid || item.link || `${catId}_${index}`,
            title: item.title || 'Başlık Yok',
            content: item.description || item.content || '',
            link: item.link || '#',
            image: item.enclosure?.link || 
                   item.thumbnail || 
                   `https://picsum.photos/400/300?random=${idx * 10 + index}`,
            publishedAt: new Date(item.pubDate || Date.now()),
            category: catName,
            categoryId: catId,
            source: 'Anadolu Ajansı'
          }));
          allNews.push(...items);
        }
      });

      // Tarihe göre sırala ve limit uygula
      return allNews
        .sort((a, b) => b.publishedAt.getTime() - a.publishedAt.getTime())
        .slice(0, limit);
      
    } catch (error) {
      console.error('RSS mixed fetch error:', error);
      return [];
    }
  }
};

