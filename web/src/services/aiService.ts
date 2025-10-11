/**
 * AI Service Client
 * ai-service-2 ile iletişim kuran client servisi
 */

const AI_SERVICE_URL = process.env.NEXT_PUBLIC_AI_SERVICE_URL || 'http://localhost:3001';

interface ChatMessage {
  text: string;
  isUser: boolean;
}

interface ChatResponse {
  success: boolean;
  response: string;
  timestamp: string;
}

interface SummaryResponse {
  success: boolean;
  summary: string;
  originalLength: number;
  summaryLength: number;
  compressionRatio: number;
  timestamp: string;
}

interface QuickPrompt {
  title: string;
  prompt: string;
  category: string;
}

interface SuggestionsResponse {
  success: boolean;
  suggestions: QuickPrompt[];
  timestamp: string;
}

class AIService {
  private baseURL: string;

  constructor() {
    this.baseURL = AI_SERVICE_URL;
  }

  /**
   * Chat endpoint'ine mesaj gönderir
   * @param message Kullanıcı mesajı
   * @param conversationHistory Konuşma geçmişi
   * @returns AI yanıtı
   */
  async sendChatMessage(
    message: string, 
    conversationHistory: ChatMessage[] = []
  ): Promise<string> {
    try {
      const response = await fetch(`${this.baseURL}/api/chat`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          message,
          conversationHistory: conversationHistory.map(msg => ({
            text: msg.text,
            isUser: msg.isUser
          }))
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data: ChatResponse = await response.json();
      return data.response;
    } catch (error) {
      console.error('❌ AI Chat Error:', error);
      throw new Error('AI servisi ile iletişim kurulamadı. Lütfen daha sonra tekrar deneyin.');
    }
  }

  /**
   * Hızlı önerileri getirir
   * @returns Öneri listesi
   */
  async getQuickSuggestions(): Promise<QuickPrompt[]> {
    try {
      const response = await fetch(`${this.baseURL}/api/chat/suggestions`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data: SuggestionsResponse = await response.json();
      return data.suggestions;
    } catch (error) {
      console.error('❌ Suggestions Error:', error);
      // Hata durumunda varsayılan öneriler döndür
      return [
        { title: "Günün Özeti", prompt: "Bugünkü önemli haberleri özetle", category: "daily" },
        { title: "Haftalık Ekonomi", prompt: "Bu haftaki ekonomi haberlerini özetle", category: "weekly" },
        { title: "Haftalık Siyaset", prompt: "Bu haftaki siyasi gelişmeleri özetle", category: "weekly" },
        { title: "Genel Haberler", prompt: "Güncel önemli haberleri anlat", category: "general" }
      ];
    }
  }

  /**
   * Tek bir haberi özetler
   * @param content Haber içeriği
   * @param title Haber başlığı
   * @param category Haber kategorisi
   * @returns Özet metni
   */
  async summarizeNews(
    content: string, 
    title?: string, 
    category?: string
  ): Promise<string> {
    try {
      const response = await fetch(`${this.baseURL}/api/summary/news`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          content,
          title,
          category
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data: SummaryResponse = await response.json();
      return data.summary;
    } catch (error) {
      console.error('❌ Summary Error:', error);
      throw new Error('Haber özetlenemiyor. Lütfen daha sonra tekrar deneyin.');
    }
  }

  /**
   * Günlük özet getirir
   * @param category Haber kategorisi
   * @returns Günlük özet
   */
  async getDailySummary(category: string = 'genel'): Promise<string> {
    try {
      const response = await fetch(
        `${this.baseURL}/api/summary/daily?category=${encodeURIComponent(category)}`
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return data.summary;
    } catch (error) {
      console.error('❌ Daily Summary Error:', error);
      throw new Error('Günlük özet alınamıyor. Lütfen daha sonra tekrar deneyin.');
    }
  }

  /**
   * Haftalık özet getirir
   * @param category Haber kategorisi
   * @returns Haftalık özet
   */
  async getWeeklySummary(category: string = 'genel'): Promise<string> {
    try {
      const response = await fetch(
        `${this.baseURL}/api/summary/weekly?category=${encodeURIComponent(category)}`
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return data.summary;
    } catch (error) {
      console.error('❌ Weekly Summary Error:', error);
      throw new Error('Haftalık özet alınamıyor. Lütfen daha sonra tekrar deneyin.');
    }
  }

  /**
   * Haber için quiz oluştur
   * @param newsContent Haber içeriği
   * @param newsTitle Haber başlığı
   * @param category Haber kategorisi
   * @returns Quiz soruları
   */
  async generateQuiz(
    newsContent: string,
    newsTitle: string,
    category: string
  ): Promise<any> {
    try {
      const response = await fetch(`${this.baseURL}/api/quiz/generate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          newsItems: [{
            title: newsTitle,
            content: newsContent,
            category: category
          }],
          difficulty: 'mixed',
          questionCount: 4
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return data.quiz;
    } catch (error) {
      console.error('❌ Quiz Generation Error:', error);
      throw new Error('Quiz oluşturulamadı. Lütfen daha sonra tekrar deneyin.');
    }
  }

  /**
   * Servis sağlık kontrolü
   * @returns Servis durumu
   */
  async healthCheck(): Promise<boolean> {
    try {
      const response = await fetch(`${this.baseURL}/health`);
      return response.ok;
    } catch (error) {
      console.error('❌ Health Check Error:', error);
      return false;
    }
  }

  /**
   * Kategori bazlı haberleri getirir
   * @param category Haber kategorisi (guncel, ekonomi, spor, vb.)
   * @param limit Haber sayısı
   * @returns Haber listesi
   */
  async getNewsByCategory(category: string = 'guncel', limit: number = 20): Promise<any[]> {
    try {
      const response = await fetch(
        `${this.baseURL}/api/rss/${category}?limit=${limit}`
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return data.news || [];
    } catch (error) {
      console.error('❌ News Fetch Error:', error);
      return [];
    }
  }

  /**
   * Tüm kategorilerden karışık haberler getirir
   * @param limit Toplam haber sayısı
   * @returns Karışık haber listesi
   */
  async getMixedNews(limit: number = 50): Promise<any[]> {
    try {
      const response = await fetch(
        `${this.baseURL}/api/rss/all/mixed?limit=${limit}`
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return data.news || [];
    } catch (error) {
      console.error('❌ Mixed News Fetch Error:', error);
      return [];
    }
  }

  /**
   * Mevcut kategorileri listeler
   * @returns Kategori listesi
   */
  async getCategories(): Promise<any[]> {
    try {
      const response = await fetch(`${this.baseURL}/api/rss/categories/list`);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return data.categories || [];
    } catch (error) {
      console.error('❌ Categories Fetch Error:', error);
      return [];
    }
  }

  /**
   * Tüm şehirlerin haber istatistiklerini getirir
   * @returns Şehir listesi ve haber sayıları
   */
  async getCityStats(): Promise<any> {
    try {
      console.log('📊 Şehir istatistikleri çekiliyor...');
      const response = await fetch(`${this.baseURL}/api/cities/stats`);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log(`✅ ${data.cities?.length || 0} şehir verisi alındı`);
      return data;
    } catch (error) {
      console.error('❌ City Stats Fetch Error:', error);
      return { success: false, cities: [], totalCities: 0, totalNews: 0 };
    }
  }

  /**
   * Belirli bir şehrin haberlerini getirir
   * @param cityName Şehir adı
   * @param category Kategori (opsiyonel)
   * @returns Şehre ait haberler
   */
  async getCityNews(cityName: string, category?: string): Promise<any> {
    try {
      console.log(`📰 ${cityName} haberleri getiriliyor...`);
      const url = category 
        ? `${this.baseURL}/api/cities/${encodeURIComponent(cityName)}/news?category=${category}`
        : `${this.baseURL}/api/cities/${encodeURIComponent(cityName)}/news`;
      
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log(`✅ ${data.news?.length || 0} haber alındı`);
      return data;
    } catch (error) {
      console.error(`❌ ${cityName} News Fetch Error:`, error);
      return { success: false, news: [], totalNews: 0 };
    }
  }

  /**
   * Podcast listesini getirir
   * @param category Kategori (opsiyonel)
   * @param limit Podcast sayısı
   * @returns Podcast listesi
   */
  async getPodcastList(category?: string, limit: number = 10): Promise<any> {
    try {
      console.log('🎙️ Podcast listesi getiriliyor...');
      const url = category 
        ? `${this.baseURL}/api/podcast?category=${category}&limit=${limit}`
        : `${this.baseURL}/api/podcast?limit=${limit}`;
      
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log(`✅ ${data.podcasts?.length || 0} podcast alındı`);
      return data;
    } catch (error) {
      console.error('❌ Podcast List Fetch Error:', error);
      return { success: false, podcasts: [], total: 0 };
    }
  }

  /**
   * Haber için podcast oluşturur
   * @param newsContent Haber içeriği
   * @param title Haber başlığı
   * @param category Haber kategorisi
   * @returns Oluşturulan podcast
   */
  async generatePodcast(newsContent: string, title?: string, category?: string): Promise<any> {
    try {
      console.log('🎙️ Podcast oluşturuluyor...');
      const response = await fetch(`${this.baseURL}/api/podcast/generate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          newsContent,
          title,
          category,
          tone: 'friendly'
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log(`✅ Podcast oluşturuldu: ${data.podcast?.id}`);
      return data;
    } catch (error) {
      console.error('❌ Podcast Generation Error:', error);
      return { success: false, error: 'Podcast oluşturulamadı' };
    }
  }

  /**
   * Podcast ses dosyasını getirir (TTS)
   * @param podcastId Podcast ID
   * @param voice Ses tipi (alloy, echo, fable, onyx, nova, shimmer)
   * @returns Audio URL (Blob URL)
   */
  async getPodcastAudio(podcastId: string, voice: string = 'nova'): Promise<string | null> {
    try {
      console.log(`🔊 Podcast ses dosyası getiriliyor: ${podcastId}`);
      const response = await fetch(`${this.baseURL}/api/podcast/${podcastId}/audio`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ voice }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const blob = await response.blob();
      const audioUrl = URL.createObjectURL(blob);
      console.log(`✅ Ses dosyası hazır: ${audioUrl.substring(0, 50)}...`);
      return audioUrl;
    } catch (error) {
      console.error('❌ Podcast Audio Fetch Error:', error);
      return null;
    }
  }

  /**
   * Belirli bir podcast'in detaylarını getirir
   * @param podcastId Podcast ID
   * @returns Podcast detayları
   */
  async getPodcastById(podcastId: string): Promise<any> {
    try {
      console.log(`📻 Podcast detayları getiriliyor: ${podcastId}`);
      const response = await fetch(`${this.baseURL}/api/podcast/${podcastId}`);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log(`✅ Podcast detayları alındı`);
      return data;
    } catch (error) {
      console.error('❌ Podcast Detail Fetch Error:', error);
      return { success: false, podcast: null };
    }
  }

  /**
   * Podcast kategorilerini listeler
   * @returns Kategori listesi
   */
  async getPodcastCategories(): Promise<any[]> {
    try {
      const response = await fetch(`${this.baseURL}/api/podcast/categories/list`);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return data.categories || [];
    } catch (error) {
      console.error('❌ Podcast Categories Fetch Error:', error);
      return [];
    }
  }
}

// Singleton instance
const aiService = new AIService();

export default aiService;
export type { ChatMessage, QuickPrompt };
