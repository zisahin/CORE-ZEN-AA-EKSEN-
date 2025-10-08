import OpenAI from 'openai';
import dotenv from 'dotenv';

dotenv.config();

// OpenAI client'ı başlat
const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY,
});

class OpenAIService {
  constructor() {
    if (!process.env.OPENAI_API_KEY) {
      console.warn('⚠️  OPENAI_API_KEY environment variable bulunamadı!');
    }
  }

  // Genel chat için GPT-4 kullanımı
  async chatCompletion(messages, options = {}) {
    try {
      const response = await openai.chat.completions.create({
        model: options.model || 'gpt-4',
        messages: messages,
        max_tokens: options.maxTokens || 1000,
        temperature: options.temperature || 0.7,
        presence_penalty: options.presencePenalty || 0,
        frequency_penalty: options.frequencyPenalty || 0,
        ...options
      });

      return response.choices[0]?.message?.content || 'Üzgünüm, bir yanıt oluşturamadım.';
    } catch (error) {
      console.error('❌ OpenAI API Error:', error);
      throw new Error('AI servisi şu anda kullanılamıyor. Lütfen daha sonra tekrar deneyin.');
    }
  }

  // Haber özetleme için özel prompt
  async summarizeNews(newsContent, options = {}) {
    const messages = [
      {
        role: 'system',
        content: `Sen Türkiye'nin en deneyimli haber editörüsün. Haberleri kısa, net ve anlaşılır şekilde özetlemen gerekiyor.

KURALLAR:
- Maksimum 3-4 cümle kullan
- Ana noktaları vurgula
- Objektif kalıp kişisel yorum yapma
- Türkçe dilbilgisi kurallarına dikkat et
- Anahtar kelimeleri kalın yaparak vurgula

FORMAT:
**Özet:** [Ana nokta]
**Anahtar Kelimeler:** #kelime1 #kelime2 #kelime3`
      },
      {
        role: 'user',
        content: `Bu haberi özetle:\n\n${newsContent}`
      }
    ];

    return await this.chatCompletion(messages, {
      maxTokens: 300,
      temperature: 0.3,
      ...options
    });
  }

  // Timeline analizi için özel AI
  async analyzeForTimeline(newsItems, options = {}) {
    const messages = [
      {
        role: 'system',
        content: `Sen bir haber analisti olarak çalışıyorsun. Görevin haberleri analiz ederek hangi haberlerin bir "zaman tüneli" oluşturabileceğini belirlemek.

ZAMAN TÜNELİ KRİTERLERİ:
- Aynı konu/olay hakkında birden fazla haber olmalı
- Kronolojik gelişim göstermeli
- Sürekli güncellenen durumlar olmalı
- Uzun vadeli takip gerektiren konular olmalı

ÖRNEKLER:
✅ İyi: Seçim süreci, Büyük projeler, Davalar, Spor transferleri
❌ Kötü: Tek seferlik haberler, Rutin günlük haberler

JSON formatında yanıt ver:`
      },
      {
        role: 'user',
        content: `Bu haberleri analiz et ve timeline potansiyeli olanları belirle:\n\n${JSON.stringify(newsItems, null, 2)}`
      }
    ];

    return await this.chatCompletion(messages, {
      maxTokens: 800,
      temperature: 0.4,
      ...options
    });
  }

  // Podcast içeriği oluşturma
  async generatePodcastContent(newsContent, options = {}) {
    const messages = [
      {
        role: 'system',
        content: `Sen sıcak ve samimi bir radyo programcısısın. Haberleri podcast formatında, sohbet havasında anlatıyorsun.

PODCAST KURALLARI:
- Samimi ve sohbet dili kullan
- "Arkadaşlar", "Biliyorsunuz", "İşte şimdi" gibi ifadeler kullan  
- Konuya kişisel dokunuşlar ekle
- Merak uyandıracak sorular sor
- 2-3 dakikalık konuşma uzunluğunda yaz
- Dinleyiciyle direkt konuş

FORMAT:
[Giriş - ilgi çekici]
[Ana içerik - sohbet tarzı]
[Sonuç - düşündürücü]`
      },
      {
        role: 'user',
        content: `Bu haberi podcast formatında anlat:\n\n${newsContent}`
      }
    ];

    return await this.chatCompletion(messages, {
      maxTokens: 600,
      temperature: 0.8,
      ...options
    });
  }

  // Günlük sorular oluşturma
  async generateDailyQuestions(options = {}) {
    const messages = [
      {
        role: 'system',
        content: `Sen kullanıcı etkileşimini artıran soru uzmanısın. Günlük hayattan, güncel olaylardan, kişisel tercihlerden sorular üretiyorsun.

SORU KATEGORİLERİ:
- Kişisel tercihler (yemek, müzik, film vb.)
- Güncel olaylar hakkında görüşler
- Mevsimsel konular
- Sosyal yaşam
- Hobiler ve ilgi alanları

KURALLAR:
- Basit ve anlaşılır sorular sor
- Kişisel veri isteme
- Siyasi görüş toplama
- Her gün 3 farklı kategori soru
- JSON formatında ver

FORMAT:
{
  "questions": [
    {"text": "soru metni", "category": "kategori", "options": ["A", "B", "C"]},
    ...
  ]
}`
      },
      {
        role: 'user',
        content: `Bugün için 3 adet interaktif soru oluştur. Tarih: ${new Date().toLocaleDateString('tr-TR')}`
      }
    ];

    return await this.chatCompletion(messages, {
      maxTokens: 400,
      temperature: 0.9,
      ...options
    });
  }

  // Quiz oluşturma
  async generateNewsQuiz(newsItems, options = {}) {
    const messages = [
      {
        role: 'system',
        content: `Sen eğlenceli quiz uzmanısın. Güncel haberlerden ilginç ve öğretici sorular hazırlıyorsun.

QUIZ KURALLARI:
- 4 seçenekli sorular
- 1 doğru, 3 yanlış cevap
- Kolay-orta zorluk seviyesi
- Eğlenceli ama bilgilendirici
- Sadece doğrulanabilir bilgiler

JSON FORMAT:
{
  "quiz": [
    {
      "question": "soru metni",
      "options": ["A", "B", "C", "D"],
      "correctAnswer": 0,
      "explanation": "açıklama"
    }
  ]
}`
      },
      {
        role: 'user',
        content: `Bu haberlerden 3-4 soruluk bir quiz hazırla:\n\n${JSON.stringify(newsItems, null, 2)}`
      }
    ];

    return await this.chatCompletion(messages, {
      maxTokens: 600,
      temperature: 0.6,
      ...options
    });
  }

  // Haber kategorilerine göre özet
  async generateCategorySummary(category, timeframe, newsItems, options = {}) {
    const messages = [
      {
        role: 'system',
        content: `Sen ${category} alanında uzman bir gazeteci ve analistsin. Bu kategorideki haberleri analiz edip kapsamlı özetler hazırlıyorsun.

ÖZET KURALLARI:
- Ana gelişmeleri kronolojik sırala
- Önemli rakamları ve tarihleri belirt
- Gelecekteki etkileri değerlendir
- Objektif ve profesyonel dil kullan
- Kaynakları güvenilir tut

FORMAT:
📊 **${category.toUpperCase()} ÖZETİ** (${timeframe})

🔍 **Ana Gelişmeler:**
• [Gelişme 1]
• [Gelişme 2]

📈 **Önemli Veriler:**
• [Veri 1]
• [Veri 2]  

🔮 **Gelecek Beklentileri:**
[Analiz]`
      },
      {
        role: 'user',
        content: `${category} kategorisindeki ${timeframe} haberlerini özetle:\n\n${JSON.stringify(newsItems, null, 2)}`
      }
    ];

    return await this.chatCompletion(messages, {
      maxTokens: 800,
      temperature: 0.5,
      ...options
    });
  }
}

export default new OpenAIService();


