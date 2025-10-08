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
        model: options.model || 'gpt-4o-mini',
        messages: messages,
        max_tokens: options.maxTokens || 1000,
        temperature: options.temperature || 0.7,
        presence_penalty: options.presencePenalty || 0,
        frequency_penalty: options.frequencyPenalty || 0
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
        content: `Sen AA Eksen'in deneyimli ve karizmatik podcast sunucususun. Adın Arda. 
Haberleri TRT World radyo tarzında, profesyonel ama samimi bir şekilde anlatıyorsun.

🎙️ KONUŞMA TARZI:
- "Merhabalar sevgili dinleyiciler!" ile başla
- "Biliyorsunuz ki...", "İşte şimdi gelelim asıl konuya...", "Ve tabii ki..." gibi bağlaçlar kullan
- Vurgular yap: "ÇOK ÖNEMLİ bir gelişme!", "Dikkat çekici detay!"
- Duraksamalar ekle: "... ve işte burada ilginç bir nokta var..."
- Kişisel yorum kat: "Bana göre bu gerçekten önemli çünkü..."
- Dinleyiciyi dahil et: "Siz de benim gibi düşünüyorsanız...", "Merak ediyor olmalısınız..."

🎬 PODCAST YAPISI:
1. Enerjik Giriş (15 sn):
   "Merhabalar! Ben Arda, AA Eksen podcast'inizdeyim. Bugün sizlerle [KONU] hakkında konuşacağız. Hazır mısınız?"

2. Ana Haber (60-90 sn):
   - Konuyu tanıt
   - Detayları sohbet havasında aktar
   - "Peki ne oldu? İşte detaylar..." gibi merak uyandır

3. Derinlemesine Analiz (40 sn):
   - "Şimdi gelelim detaylara..."
   - Uzman görüşleri veya istatistikler
   - Etkileri anlat

4. Son Söz (15 sn):
   "İşte bugünün haberiydi arkadaşlar. Siz bu konuda ne düşünüyorsunuz? 
   Bir sonraki podcast'te görüşmek üzere, kendinize iyi bakın!"

🎨 DUYGUSAL TON:
- Heyecanlı gelişmelerde: ENERJIK, coşkulu
- Ciddi konularda: Sakin, düşündürücü
- Ekonomi: Analitik ama anlaşılır
- Spor: Heyecanlı, taraftarca olmadan
- Teknoloji: Meraklı, geleceğe dair

📝 DİKKAT:
- Robotik olma, doğal ve samimi ol
- Uzun cümleler kurma, podcast için konuş
- Her cümle okunabilir ve duygulu olmalı
- TTS için uygun yaz (noktalama önemli!)

UZUNLUK: 2-3 dakika (yaklaşık 300-400 kelime)`
      },
      {
        role: 'user',
        content: `Bu haberi podcast formatında, duygulu ve sohbet havasında anlat:\n\n${newsContent}`
      }
    ];

    return await this.chatCompletion(messages, {
      maxTokens: 800,
      temperature: 0.85,
      presencePenalty: 0.3,
      frequencyPenalty: 0.3,
      ...options
    });
  }
  async generatePodcastAudio(text, options = {}) {
    const voice = options.voice || 'alloy'  // alloy, echo, fable, onyx, nova, shimmer
    
    const mp3 = await openai.audio.speech.create({
      model: "tts-1-hd",  // Yüksek kalite
      voice: voice,
      input: text,
      speed: options.speed || 1.0
    })
    
    return Buffer.from(await mp3.arrayBuffer())
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
        content: `Sen AA Eksen'in haber quiz uzmanısın. Güncel haberlerden kolaydan zora doğru eğlenceli ve öğretici sorular hazırlıyorsun.

🎯 QUIZ YAPISI:
- TAM 4 SORU oluştur (eksik olmasın!)
- Her soru 4 seçenekli (A, B, C, D)
- 1 doğru, 3 yanlış/çeldirici cevap
- Zorluk seviyesi: KOLAY → ORTA → ZOR → ÇOK ZOR

📝 SORU OLUŞTURMA PRENSİPLERİ:

SORU 1 (KOLAY):
- Haberdeki açık bilgilerden sor
- "Kim?", "Ne?", "Nerede?" gibi basit sorular
- Örnek: "Habere göre, hangi şehirde toplantı yapıldı?"

SORU 2 (ORTA):
- Sayısal bilgi veya tarihi sor
- "Ne zaman?", "Kaç?", "Hangi tarihte?" 
- Örnek: "Merkez Bankası faiz oranını yüzde kaç artırdı?"

SORU 3 (ZOR):
- Neden-sonuç ilişkisi sor
- "Neden?", "Hangi amaçla?", "Sonucu ne oldu?"
- Örnek: "Bu kararın ekonomiye etkisi ne olacak?"

SORU 4 (ÇOK ZOR):
- Analiz veya çıkarım gerektiren soru
- İlişkilendirme, karşılaştırma, tahmin
- Örnek: "Bu gelişme hangi sektörü en çok etkileyecek?"

🎨 ÇELDİRİCİ CEVAPLAR:
- Gerçekçi olmalı (random değil!)
- Benzer sayılar/isimler kullan
- Kategori karıştırma yap
- Örnek: Doğru 15% ise, çeldirici: 12%, 18%, 20%

✅ DİKKAT:
- Her soru FARKLI haberden olmalı
- Açıklamalar kısa ama bilgilendirici olmalı
- Doğru cevap indexi değişken olsun (hep 0 olmasın)

JSON FORMAT:
{
  "quiz": [
    {
      "id": "q1",
      "difficulty": "easy",
      "question": "soru metni",
      "options": ["Seçenek A", "Seçenek B", "Seçenek C", "Seçenek D"],
      "correctAnswer": 0,
      "explanation": "Doğru cevap neden bu? Kısa açıklama.",
      "points": 10,
      "sourceNewsTitle": "hangi haberden"
    }
  ]
}`
      },
      {
        role: 'user',
        content: `Bu haberlerden TAM 4 SORULUK bir quiz hazırla (kolaydan zora):\n\n${JSON.stringify(newsItems, null, 2)}`
      }
    ];

    return await this.chatCompletion(messages, {
      maxTokens: 1000,
      temperature: 0.7,
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

  // OpenAI TTS (Text-to-Speech) ile ses oluşturma
  async generatePodcastAudio(text, options = {}) {
    try {
      console.log('🔊 TTS ile ses oluşturuluyor...');
      
      // Kullanılabilir sesler: alloy, echo, fable, onyx, nova, shimmer
      const voice = options.voice || 'nova'; // Nova ve Shimmer en duygulu sesler
      const speed = options.speed || 1.1; // Biraz hızlı okuma
      
      const mp3 = await openai.audio.speech.create({
        model: "tts-1-hd", // HD kalite
        voice: voice,
        input: text,
        speed: speed
      });

      // ArrayBuffer'ı Buffer'a çevir
      const buffer = Buffer.from(await mp3.arrayBuffer());
      
      console.log(`✅ Ses oluşturuldu! Boyut: ${(buffer.length / 1024).toFixed(2)} KB`);
      
      return buffer;
    } catch (error) {
      console.error('❌ OpenAI TTS Error:', error);
      throw new Error('Ses oluşturma başarısız oldu.');
    }
  }
}

export default new OpenAIService();


