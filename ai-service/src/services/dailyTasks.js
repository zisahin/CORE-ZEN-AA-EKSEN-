import openaiService from './openai.js';

class DailyTasksService {
  constructor() {
    this.isRunning = false;
  }

  // Günlük soruları oluştur
  async generateDailyQuestions() {
    if (this.isRunning) return;
    
    try {
      this.isRunning = true;
      console.log('🤔 Günlük sorular oluşturuluyor...');

      const questions = await openaiService.generateDailyQuestions();
      
      // Burada normalde veritabanına kayıt yaparsınız
      console.log('✅ Günlük sorular hazırlandı:', JSON.stringify(questions, null, 2));
      
      return questions;
    } catch (error) {
      console.error('❌ Günlük sorular oluşturulamadı:', error);
    } finally {
      this.isRunning = false;
    }
  }

  // Günlük quizler oluştur
  async generateDailyQuizzes() {
    if (this.isRunning) return;

    try {
      this.isRunning = true;
      console.log('🧩 Günlük quiz oluşturuluyor...');

      // Mock haber verileri (normalde veritabanından çekersiniz)
      const mockNews = [
        {
          id: 1,
          title: 'Ekonomide yeni gelişmeler yaşandı',
          content: 'Merkez Bankası faiz oranlarını değiştirdi...',
          category: 'ekonomi',
          publishedAt: new Date().toISOString()
        },
        {
          id: 2,
          title: 'Fenerbahçe yeni transfer yaptı',
          content: 'Sarı-lacivertli takım kadrosunu güçlendirdi...',
          category: 'spor',
          publishedAt: new Date().toISOString()
        }
      ];

      const quiz = await openaiService.generateNewsQuiz(mockNews);
      
      console.log('✅ Günlük quiz hazırlandı');
      return quiz;
    } catch (error) {
      console.error('❌ Günlük quiz oluşturulamadı:', error);
    } finally {
      this.isRunning = false;
    }
  }

  // Timeline analizi yap
  async analyzeNewsForTimelines() {
    if (this.isRunning) return;

    try {
      this.isRunning = true;
      console.log('⏰ Timeline analizi yapılıyor...');

      // Mock haber verileri
      const mockNews = [
        {
          id: 1,
          title: 'Marmaray çalışmaları devam ediyor',
          content: 'Proje son aşamaya geldi...',
          category: 'altyapi',
          tags: ['marmaray', 'ulaşım', 'istanbul'],
          publishedAt: new Date().toISOString()
        },
        {
          id: 2,
          title: 'Marmaray testleri başarılı',
          content: 'İlk test sürüşleri tamamlandı...',
          category: 'altyapi',
          tags: ['marmaray', 'test', 'ulaşım'],
          publishedAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
        }
      ];

      const analysis = await openaiService.analyzeForTimeline(mockNews);
      
      console.log('✅ Timeline analizi tamamlandı');
      return analysis;
    } catch (error) {
      console.error('❌ Timeline analizi başarısız:', error);
    } finally {
      this.isRunning = false;
    }
  }

  // Haftalık özetler oluştur
  async generateWeeklySummaries() {
    if (this.isRunning) return;

    try {
      this.isRunning = true;
      console.log('📊 Haftalık özetler hazırlanıyor...');

      const categories = ['ekonomi', 'siyaset', 'spor', 'teknoloji'];
      const summaries = {};

      for (const category of categories) {
        console.log(`📝 ${category} kategorisi özeti hazırlanıyor...`);
        
        // Mock haber verileri
        const mockNews = [
          {
            title: `${category} kategorisinden haber 1`,
            content: 'Haber içeriği...',
            category: category,
            publishedAt: new Date().toISOString()
          }
        ];

        const summary = await openaiService.generateCategorySummary(
          category,
          'haftalık',
          mockNews
        );

        summaries[category] = summary;
        
        // Rate limiting için kısa bekleme
        await new Promise(resolve => setTimeout(resolve, 1000));
      }

      console.log('✅ Tüm haftalık özetler hazırlandı');
      return summaries;
    } catch (error) {
      console.error('❌ Haftalık özetler oluşturulamadı:', error);
    } finally {
      this.isRunning = false;
    }
  }

  // Podcast içerikleri oluştur
  async generateDailyPodcasts() {
    if (this.isRunning) return;

    try {
      this.isRunning = true;
      console.log('🎙️ Günlük podcast içerikleri hazırlanıyor...');

      // Mock haber verileri
      const mockNews = [
        {
          title: 'Günün önemli haberi',
          content: 'Detaylı haber içeriği...',
          category: 'genel'
        }
      ];

      const podcasts = [];
      for (const news of mockNews) {
        const podcastContent = await openaiService.generatePodcastContent(
          `${news.title}\n\n${news.content}`
        );
        podcasts.push({
          title: `🎙️ ${news.title}`,
          content: podcastContent,
          category: news.category,
          createdAt: new Date().toISOString()
        });
      }

      console.log('✅ Günlük podcast içerikleri hazırlandı');
      return podcasts;
    } catch (error) {
      console.error('❌ Podcast içerikleri oluşturulamadı:', error);
    } finally {
      this.isRunning = false;
    }
  }

  // Tüm günlük görevleri çalıştır
  async runAllDailyTasks() {
    console.log('🌅 Günlük AI görevleri başlatılıyor...');
    
    await Promise.all([
      this.generateDailyQuestions(),
      this.generateDailyQuizzes(),
      this.analyzeNewsForTimelines(),
      this.generateDailyPodcasts()
    ]);

    console.log('✅ Tüm günlük AI görevleri tamamlandı!');
  }

  // Sağlık kontrolü
  getStatus() {
    return {
      service: 'Daily Tasks Service',
      status: this.isRunning ? 'running' : 'idle',
      lastRun: new Date().toISOString(),
      nextRun: this.getNextRunTime()
    };
  }

  // Sonraki çalışma zamanı
  getNextRunTime() {
    const now = new Date();
    const tomorrow = new Date(now);
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(6, 0, 0, 0); // Sabah 6:00
    return tomorrow.toISOString();
  }
}

export default new DailyTasksService();


