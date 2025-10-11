package com.bysoftware.aaeksen.data.firebase

import android.content.Context
import android.util.Log
import com.bysoftware.aaeksen.core.constants.FirebaseConfig
import com.bysoftware.aaeksen.data.firebase.model.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase'e örnek verileri yükleyen sınıf
 * Uygulama ilk açıldığında bir kez çalışır
 */
@Singleton
class SampleDataInitializer @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "SampleDataInitializer"
        private const val PREFS_NAME = "firebase_init"
        private const val KEY_DATA_INITIALIZED = "data_initialized"
    }

    /**
     * Örnek verileri yükle (tek seferlik)
     */
    suspend fun initializeSampleData(context: Context): Result<Unit> {
        return try {
            // Daha önce yüklenmiş mi kontrol et
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isInitialized = prefs.getBoolean(KEY_DATA_INITIALIZED, false)

            if (isInitialized) {
                Log.d(TAG, "Örnek veriler zaten yüklenmiş, atlıyorum...")
                return Result.success(Unit)
            }

            Log.d(TAG, "Örnek veriler yükleniyor...")

            // Sırayla verileri yükle
            loadNews()
            loadTimeTunnelCategories()
            loadCrosswordData()
            loadQuizData()
            loadDailyTasks()
            loadBadges()
            loadAIQuestions()
            loadSampleUser()
            loadSampleVideos()

            // Başarılı, kaydet
            prefs.edit().putBoolean(KEY_DATA_INITIALIZED, true).apply()
            Log.d(TAG, "✅ Tüm örnek veriler başarıyla yüklendi!")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Örnek veriler yüklenirken hata: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Verileri sıfırla (sadece geliştirme için - manuel kullanım)
     */
    fun resetInitializationFlag(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DATA_INITIALIZED, false).apply()
        Log.d(TAG, "⚠️ Başlatma bayrağı manuel olarak sıfırlandı")
    }

    // ==================== HABERLER ====================

    private suspend fun loadNews() {
        Log.d(TAG, "Haberler yükleniyor...")
        
        val newsList = listOf(
            FirebaseNews(
                title = "İstanbul'da Yeni Metro Hattı Hizmete Açıldı",
                content = "İstanbul Büyükşehir Belediyesi, şehrin ulaşım ağını genişleten yeni metro hattını bugün düzenlenen törenle hizmete açtı. Mecidiyeköy-Mahmutbey arasında uzanan 15 kilometrelik hat, günde ortalama 300 bin yolcuya hizmet verecek. Belediye Başkanı, 'Bu proje İstanbul'un ulaşım sorununa önemli bir çözüm getiriyor' dedi.",
                author = "Mehmet Yılmaz",
                category = "Ulaşım",
                location = "İstanbul",
                imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=800",
                imageCaption = "Yeni metro hattının açılış töreni",
                xpPoints = 10,
                breaking = true,
                shortSummary = "İstanbul'da yeni metro hattı açıldı. Günde 300 bin yolcuya hizmet verecek.",
                mediumSummary = "İstanbul Büyükşehir Belediyesi, Mecidiyeköy-Mahmutbey arasında uzanan 15 kilometrelik yeni metro hattını hizmete açtı. Hat günde 300 bin yolcuya hizmet verecek.",
                podcastVersion = "Bugün İstanbul'da heyecan verici bir gelişme yaşandı. Şehrin ulaşım ağını genişleten yeni metro hattı törenle hizmete açıldı...",
                mapGuessDetail = "Büyükşehir Belediyesi, şehrin ulaşım ağını genişleten yeni metro hattını bugün düzenlenen törenle hizmete açtı. Mecidiyeköy-Mahmutbey arasında uzanan 15 kilometrelik hat, günde ortalama 300 bin yolcuya hizmet verecek.",
                crosswordWords = listOf("metro", "ulaşım", "istanbul", "hat", "yolcu", "belediye", "tören", "proje"),
                quizQuestions = listOf(
                    QuizQuestion(
                        question = "Yeni metro hattı hangi şehirde açıldı?",
                        options = listOf("İstanbul", "Ankara", "İzmir", "Bursa"),
                        correctAnswerIndex = 0,
                        explanation = "Haber İstanbul'daki yeni metro hattını anlatıyor.",
                        xpPoints = 5
                    ),
                    QuizQuestion(
                        question = "Metro hattı günde kaç yolcuya hizmet verecek?",
                        options = listOf("100 bin", "200 bin", "300 bin", "400 bin"),
                        correctAnswerIndex = 2,
                        explanation = "Haberde günde ortalama 300 bin yolcuya hizmet vereceği belirtiliyor.",
                        xpPoints = 5
                    )
                ),
                publishedAt = Timestamp.now(),
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            ),
            FirebaseNews(
                title = "Ankara'da Teknoloji Zirvesi Başladı",
                content = "Türkiye'nin en büyük teknoloji etkinliklerinden biri olan TechSummit Ankara, bugün başladı. 3 gün sürecek zirvede yapay zeka, blockchain ve siber güvenlik konuları ele alınacak. Etkinliğe 50'den fazla ülkeden 5 bin katılımcı bekleniyor.",
                author = "Ayşe Demir",
                category = "Teknoloji",
                location = "Ankara",
                imageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800",
                imageCaption = "TechSummit Ankara açılış töreni",
                xpPoints = 10,
                shortSummary = "Ankara'da TechSummit başladı. 5 bin katılımcı bekleniyor.",
                mediumSummary = "Türkiye'nin en büyük teknoloji etkinliği TechSummit Ankara bugün başladı. 3 gün sürecek zirvede yapay zeka ve siber güvenlik konuları ele alınacak.",
                podcastVersion = "Ankara'da teknoloji tutkunları için heyecan verici günler başladı. TechSummit Ankara kapılarını açtı...",
                mapGuessDetail = "Türkiye'nin en büyük teknoloji etkinliklerinden biri olan TechSummit, bugün başladı. 3 gün sürecek zirvede yapay zeka, blockchain ve siber güvenlik konuları ele alınacak.",
                crosswordWords = listOf("teknoloji", "zirve", "ankara", "yapay", "zeka", "blockchain", "güvenlik"),
                quizQuestions = listOf(
                    QuizQuestion(
                        question = "TechSummit kaç gün sürecek?",
                        options = listOf("1 gün", "2 gün", "3 gün", "4 gün"),
                        correctAnswerIndex = 2,
                        explanation = "Etkinlik 3 gün sürecek.",
                        xpPoints = 5
                    )
                ),
                publishedAt = Timestamp.now(),
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            ),
            FirebaseNews(
                title = "İzmir'de Kültür Festivali Coşkusu",
                content = "İzmir Kültür Festivali, renkli açılış töreniyle başladı. Festival kapsamında 2 hafta boyunca konserler, tiyatro gösterileri ve sergiler düzenlenecek. İzmir Büyükşehir Belediye Başkanı, 'Festival şehrimize kültürel zenginlik katıyor' dedi.",
                author = "Ali Kaya",
                category = "Kültür",
                location = "İzmir",
                imageUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800",
                imageCaption = "İzmir Kültür Festivali açılış töreni",
                xpPoints = 10,
                shortSummary = "İzmir'de Kültür Festivali başladı. 2 hafta sürecek.",
                mediumSummary = "İzmir Kültür Festivali renkli törenle başladı. 2 hafta boyunca konserler, tiyatro gösterileri ve sergiler düzenlenecek.",
                podcastVersion = "İzmir'de kültür ve sanat rüzgarı esiyor. Şehrin en büyük festivali başladı...",
                mapGuessDetail = "Kültür Festivali, renkli açılış töreniyle başladı. Festival kapsamında 2 hafta boyunca konserler, tiyatro gösterileri ve sergiler düzenlenecek.",
                crosswordWords = listOf("kültür", "festival", "izmir", "konser", "tiyatro", "sergi", "sanat"),
                quizQuestions = listOf(
                    QuizQuestion(
                        question = "Festival kaç hafta sürecek?",
                        options = listOf("1 hafta", "2 hafta", "3 hafta", "1 ay"),
                        correctAnswerIndex = 1,
                        explanation = "Festival 2 hafta sürecek.",
                        xpPoints = 5
                    )
                ),
                publishedAt = Timestamp.now(),
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            ),
            FirebaseNews(
                title = "Bursa'da Organik Tarım Projesi",
                content = "Bursa Büyükşehir Belediyesi, organik tarımı teşvik etmek için yeni bir proje başlattı. Proje kapsamında çiftçilere eğitim ve tohum desteği verilecek. Belediye Başkanı, 'Sağlıklı gıda üretimi için çalışıyoruz' dedi.",
                author = "Fatma Şahin",
                category = "Tarım",
                location = "Bursa",
                imageUrl = "https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=800",
                imageCaption = "Organik tarım arazisi",
                xpPoints = 10,
                shortSummary = "Bursa'da organik tarım projesi başladı.",
                mediumSummary = "Bursa Büyükşehir Belediyesi organik tarımı teşvik için proje başlattı. Çiftçilere eğitim ve tohum desteği verilecek.",
                podcastVersion = "Bursa'da tarımda yeni bir dönem başlıyor. Organik tarım projesi hayata geçiyor...",
                mapGuessDetail = "Büyükşehir Belediyesi, organik tarımı teşvik etmek için yeni bir proje başlattı. Proje kapsamında çiftçilere eğitim ve tohum desteği verilecek.",
                crosswordWords = listOf("organik", "tarım", "bursa", "çiftçi", "tohum", "eğitim", "proje"),
                quizQuestions = listOf(
                    QuizQuestion(
                        question = "Proje kapsamında çiftçilere ne verilecek?",
                        options = listOf("Sadece eğitim", "Sadece tohum", "Eğitim ve tohum", "Para"),
                        correctAnswerIndex = 2,
                        explanation = "Çiftçilere hem eğitim hem de tohum desteği verilecek.",
                        xpPoints = 5
                    )
                ),
                publishedAt = Timestamp.now(),
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            ),
            FirebaseNews(
                title = "Antalya'da Turizm Rekoru",
                content = "Antalya, bu yıl 15 milyon turisti ağırlayarak rekor kırdı. Vali, 'Antalya turizmin başkenti olmaya devam ediyor' açıklamasında bulundu. Oteller %95 doluluk oranına ulaştı.",
                author = "Hasan Yılmaz",
                category = "Turizm",
                location = "Antalya",
                imageUrl = "https://images.unsplash.com/photo-1527004013197-933c4bb611b3?w=800",
                imageCaption = "Antalya sahil şeridi",
                xpPoints = 10,
                breaking = true,
                shortSummary = "Antalya 15 milyon turistle rekor kırdı.",
                mediumSummary = "Antalya bu yıl 15 milyon turisti ağırlayarak turizm rekoru kırdı. Otellerde %95 doluluk oranına ulaşıldı.",
                podcastVersion = "Antalya'dan müjdeli haber geldi. Şehir turizm rekorunu kırdı...",
                mapGuessDetail = "Şehir, bu yıl 15 milyon turisti ağırlayarak rekor kırdı. Oteller %95 doluluk oranına ulaştı.",
                crosswordWords = listOf("turizm", "antalya", "rekor", "turist", "otel", "doluluk"),
                quizQuestions = listOf(
                    QuizQuestion(
                        question = "Antalya bu yıl kaç milyon turist ağırladı?",
                        options = listOf("10 milyon", "12 milyon", "15 milyon", "20 milyon"),
                        correctAnswerIndex = 2,
                        explanation = "Antalya bu yıl 15 milyon turist ağırladı.",
                        xpPoints = 5
                    )
                ),
                publishedAt = Timestamp.now(),
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
        )

        newsList.forEach { news ->
            firestore.collection(FirebaseConfig.Collections.NEWS)
                .add(news)
                .await()
        }
        
        Log.d(TAG, "✅ ${newsList.size} haber eklendi")
    }

    // ==================== ZAMAN TÜNELİ ====================

    private suspend fun loadTimeTunnelCategories() {
        Log.d(TAG, "Zaman tüneli kategorileri yükleniyor...")
        
        val categories = listOf(
            TimeTunnelCategory(
                title = "Ekrem İmamoğlu Tutuklanma Süreci",
                description = "İstanbul Büyükşehir Belediye Başkanı Ekrem İmamoğlu'nun yargı süreciyle ilgili gelişmeler",
                coverImageUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=800",
                newsIds = emptyList(),
                active = true,
                createdAt = Timestamp.now()
            ),
            TimeTunnelCategory(
                title = "Özgür Özel'in CHP Genel Başkanlığı Süreci",
                description = "Özgür Özel'in CHP Genel Başkanı seçilme süreciyle ilgili haberler",
                coverImageUrl = "https://images.unsplash.com/photo-1540910419892-4a36d2c3266c?w=800",
                newsIds = emptyList(),
                active = true,
                createdAt = Timestamp.now()
            ),
            TimeTunnelCategory(
                title = "2024 Yerel Seçimleri",
                description = "Türkiye genelinde yapılan yerel seçim sürecinin kronolojisi",
                coverImageUrl = "https://images.unsplash.com/photo-1495954484750-af469f2f9be5?w=800",
                newsIds = emptyList(),
                active = true,
                createdAt = Timestamp.now()
            )
        )

        categories.forEach { category ->
            firestore.collection(FirebaseConfig.Collections.TIME_TUNNEL_CATEGORIES)
                .add(category)
                .await()
        }
        
        Log.d(TAG, "✅ ${categories.size} zaman tüneli kategorisi eklendi")
    }

    // ==================== ÇENGEL BULMACA ====================

    private suspend fun loadCrosswordData() {
        Log.d(TAG, "Çengel bulmaca verileri yükleniyor...")
        
        // Kategorileri ekle
        val categoryIds = mutableMapOf<String, String>()
        
        val categories = listOf(
            CrosswordCategory(
                name = "Siyaset",
                description = "Siyasetle ilgili kelimeler",
                iconUrl = "https://img.icons8.com/color/96/politics.png",
                xpPoints = 50,
                wordGroups = emptyList(),
                isActive = true,
                createdAt = Timestamp.now()
            ),
            CrosswordCategory(
                name = "Ekonomi",
                description = "Ekonomiyle ilgili kelimeler",
                iconUrl = "https://img.icons8.com/color/96/money.png",
                xpPoints = 50,
                wordGroups = emptyList(),
                isActive = true,
                createdAt = Timestamp.now()
            ),
            CrosswordCategory(
                name = "Spor",
                description = "Sporla ilgili kelimeler",
                iconUrl = "https://img.icons8.com/color/96/soccer-ball.png",
                xpPoints = 50,
                wordGroups = emptyList(),
                isActive = true,
                createdAt = Timestamp.now()
            )
        )

        categories.forEach { category ->
            val docRef = firestore.collection(FirebaseConfig.Collections.CROSSWORD_CATEGORIES)
                .add(category)
                .await()
            categoryIds[category.name] = docRef.id
        }

        // Kelime gruplarını ekle
        val wordGroups = listOf(
            CrosswordWordGroup(
                categoryId = categoryIds["Siyaset"] ?: "",
                words = listOf(
                    CrosswordWord("MECLIS", "Yasama organı", 5),
                    CrosswordWord("SEÇİM", "Demokratik süreç", 5),
                    CrosswordWord("PARTI", "Siyasi oluşum", 5),
                    CrosswordWord("BAŞKAN", "Yönetici", 5),
                    CrosswordWord("BAKAN", "Bakanlık yöneticisi", 5),
                    CrosswordWord("MİLLETVEKİLİ", "Halk temsilcisi", 5),
                    CrosswordWord("ANAYASA", "Temel kanun", 5),
                    CrosswordWord("HÜKÜMET", "İcra organı", 5),
                    CrosswordWord("OY", "Seçim hakkı", 5),
                    CrosswordWord("KOALISYON", "İttifak", 5)
                ),
                difficulty = "medium",
                isActive = true,
                createdAt = Timestamp.now()
            ),
            CrosswordWordGroup(
                categoryId = categoryIds["Ekonomi"] ?: "",
                words = listOf(
                    CrosswordWord("ENFLASYON", "Fiyat artışı", 5),
                    CrosswordWord("BORSA", "Menkul kıymet piyasası", 5),
                    CrosswordWord("DOLAR", "Amerikan para birimi", 5),
                    CrosswordWord("FAİZ", "Borçlanma maliyeti", 5),
                    CrosswordWord("BÜTÇE", "Mali plan", 5),
                    CrosswordWord("İHRACAT", "Dışa satış", 5),
                    CrosswordWord("İTHALAT", "Dışardan alım", 5),
                    CrosswordWord("VERGİ", "Mali yükümlülük", 5),
                    CrosswordWord("BANKA", "Mali kurum", 5),
                    CrosswordWord("KREDİ", "Borç", 5)
                ),
                difficulty = "medium",
                isActive = true,
                createdAt = Timestamp.now()
            ),
            CrosswordWordGroup(
                categoryId = categoryIds["Spor"] ?: "",
                words = listOf(
                    CrosswordWord("FUTBOL", "En popüler takım sporu", 5),
                    CrosswordWord("BASKETBOL", "Potaya top atma oyunu", 5),
                    CrosswordWord("VOLEYBOL", "Fileli oyun", 5),
                    CrosswordWord("YÜZME", "Su sporu", 5),
                    CrosswordWord("KOŞU", "Atletizm dalı", 5),
                    CrosswordWord("TENİS", "Raketli oyun", 5),
                    CrosswordWord("GÜREŞ", "Milli spor", 5),
                    CrosswordWord("BOKS", "Yumruklu dövüş", 5),
                    CrosswordWord("ATICILIK", "Hedef sporları", 5),
                    CrosswordWord("JİMNASTİK", "Cimnastik", 5)
                ),
                difficulty = "easy",
                isActive = true,
                createdAt = Timestamp.now()
            )
        )

        wordGroups.forEach { group ->
            firestore.collection(FirebaseConfig.Collections.CROSSWORD_WORD_GROUPS)
                .add(group)
                .await()
        }
        
        Log.d(TAG, "✅ ${categories.size} çengel bulmaca kategorisi ve ${wordGroups.size} kelime grubu eklendi")
    }

    // ==================== QUIZ ====================

    private suspend fun loadQuizData() {
        Log.d(TAG, "Quiz verileri yükleniyor...")
        
        // Quiz kategorilerini ekle
        val categoryIds = mutableMapOf<String, String>()
        
        val categories = listOf(
            QuizCategory(
                name = "Genel Kültür",
                description = "Genel kültür soruları",
                iconUrl = "https://img.icons8.com/color/96/brain.png",
                questionIds = emptyList(),
                isActive = true,
                createdAt = Timestamp.now()
            ),
            QuizCategory(
                name = "Tarih",
                description = "Tarih soruları",
                iconUrl = "https://img.icons8.com/color/96/clock.png",
                questionIds = emptyList(),
                isActive = true,
                createdAt = Timestamp.now()
            ),
            QuizCategory(
                name = "Coğrafya",
                description = "Coğrafya soruları",
                iconUrl = "https://img.icons8.com/color/96/globe.png",
                questionIds = emptyList(),
                isActive = true,
                createdAt = Timestamp.now()
            )
        )

        categories.forEach { category ->
            val docRef = firestore.collection(FirebaseConfig.Collections.QUIZ_CATEGORIES)
                .add(category)
                .await()
            categoryIds[category.name] = docRef.id
        }

        // Quiz sorularını ekle
        val questions = listOf(
            QuizGameQuestion(
                categoryId = categoryIds["Genel Kültür"] ?: "",
                question = "Türkiye'nin başkenti neresidir?",
                options = listOf("İstanbul", "Ankara", "İzmir", "Bursa"),
                correctAnswerIndex = 1,
                explanation = "Türkiye'nin başkenti 1923 yılından beri Ankara'dır.",
                xpPoints = 10,
                difficulty = "easy",
                isActive = true,
                createdAt = Timestamp.now()
            ),
            QuizGameQuestion(
                categoryId = categoryIds["Genel Kültür"] ?: "",
                question = "Türkiye kaç ile sahiptir?",
                options = listOf("79", "80", "81", "82"),
                correctAnswerIndex = 2,
                explanation = "Türkiye 81 ile sahiptir.",
                xpPoints = 10,
                difficulty = "easy",
                isActive = true,
                createdAt = Timestamp.now()
            ),
            QuizGameQuestion(
                categoryId = categoryIds["Tarih"] ?: "",
                question = "Türkiye Cumhuriyeti hangi yıl kurulmuştur?",
                options = listOf("1920", "1921", "1922", "1923"),
                correctAnswerIndex = 3,
                explanation = "Türkiye Cumhuriyeti 29 Ekim 1923'te ilan edilmiştir.",
                xpPoints = 10,
                difficulty = "easy",
                isActive = true,
                createdAt = Timestamp.now()
            ),
            QuizGameQuestion(
                categoryId = categoryIds["Coğrafya"] ?: "",
                question = "Türkiye'nin en büyük gölü hangisidir?",
                options = listOf("Van Gölü", "Tuz Gölü", "Beyşehir Gölü", "Eğirdir Gölü"),
                correctAnswerIndex = 0,
                explanation = "Van Gölü Türkiye'nin en büyük gölüdür.",
                xpPoints = 10,
                difficulty = "medium",
                isActive = true,
                createdAt = Timestamp.now()
            )
        )

        questions.forEach { question ->
            firestore.collection(FirebaseConfig.Collections.QUIZ_QUESTIONS)
                .add(question)
                .await()
        }
        
        Log.d(TAG, "✅ ${categories.size} quiz kategorisi ve ${questions.size} soru eklendi")
    }

    // ==================== GÜNLÜK GÖREVLER ====================

    private suspend fun loadDailyTasks() {
        Log.d(TAG, "Günlük görevler yükleniyor...")
        
        // Bugünün sonunu hesapla (23:59:59)
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        val endOfDay = Timestamp(calendar.time)
        
        val tasks = listOf(
            DailyTask(
                title = "2 Haber Oku",
                description = "Bugün en az 2 haber oku ve bilgilen",
                type = "read_news",
                targetCount = 2,
                xpReward = 20,
                expiresAt = endOfDay,
                active = true,
                createdAt = Timestamp.now()
            ),
            DailyTask(
                title = "1 Haber Paylaş",
                description = "İlginç bulduğun bir haberi paylaş",
                type = "share_news",
                targetCount = 1,
                xpReward = 15,
                expiresAt = endOfDay,
                active = true,
                createdAt = Timestamp.now()
            ),
            DailyTask(
                title = "5 Quiz Sorusu Çöz",
                description = "Quiz oyununda 5 soru çöz",
                type = "solve_quiz",
                targetCount = 5,
                xpReward = 25,
                expiresAt = endOfDay,
                active = true,
                createdAt = Timestamp.now()
            ),
            DailyTask(
                title = "1 Çengel Bulmaca Tamamla",
                description = "Bir çengel bulmaca tamamla",
                type = "solve_crossword",
                targetCount = 1,
                xpReward = 30,
                expiresAt = endOfDay,
                active = true,
                createdAt = Timestamp.now()
            )
        )

        tasks.forEach { task ->
            firestore.collection(FirebaseConfig.Collections.DAILY_TASKS)
                .add(task)
                .await()
        }
        
        Log.d(TAG, "✅ ${tasks.size} günlük görev eklendi")
    }

    // ==================== ROZETLER ====================

    private suspend fun loadBadges() {
        Log.d(TAG, "Rozetler yükleniyor...")
        
        val badges = listOf(
            Badge(
                name = "İlk Adım",
                description = "İlk haberini okudun",
                iconUrl = "https://img.icons8.com/color/96/first-place.png",
                rarity = "common",
                requirement = mapOf("type" to "news_read", "count" to 1),
                xpReward = 10,
                active = true,
                createdAt = Timestamp.now()
            ),
            Badge(
                name = "Haber Kurdu",
                description = "10 haber okudun",
                iconUrl = "https://img.icons8.com/color/96/news.png",
                rarity = "rare",
                requirement = mapOf("type" to "news_read", "count" to 10),
                xpReward = 50,
                active = true,
                createdAt = Timestamp.now()
            ),
            Badge(
                name = "Quiz Ustası",
                description = "50 quiz sorusu çözdün",
                iconUrl = "https://img.icons8.com/color/96/brain.png",
                rarity = "epic",
                requirement = mapOf("type" to "quiz_solved", "count" to 50),
                xpReward = 100,
                active = true,
                createdAt = Timestamp.now()
            ),
            Badge(
                name = "Kelime Avcısı",
                description = "20 çengel bulmaca tamamladın",
                iconUrl = "https://img.icons8.com/color/96/crossword.png",
                rarity = "epic",
                requirement = mapOf("type" to "crossword_solved", "count" to 20),
                xpReward = 100,
                active = true,
                createdAt = Timestamp.now()
            ),
            Badge(
                name = "Coğrafya Dehası",
                description = "25 harita tahmini doğru bildin",
                iconUrl = "https://img.icons8.com/color/96/globe.png",
                rarity = "legendary",
                requirement = mapOf("type" to "map_guess_correct", "count" to 25),
                xpReward = 200,
                active = true,
                createdAt = Timestamp.now()
            )
        )

        badges.forEach { badge ->
            firestore.collection(FirebaseConfig.Collections.BADGES)
                .add(badge)
                .await()
        }
        
        Log.d(TAG, "✅ ${badges.size} rozet eklendi")
    }

    // ==================== AI SORULARI ====================

    private suspend fun loadAIQuestions() {
        Log.d(TAG, "AI soruları yükleniyor...")
        
        // 1 hafta sonrasını hesapla
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 7)
        val oneWeekLater = Timestamp(calendar.time)
        
        val questions = listOf(
            AIQuestion(
                question = "Yaşam şartlarından memnun musunuz?",
                options = listOf("Evet, memnunum", "Kısmen memnunum", "Hayır, memnun değilim"),
                category = "social",
                responses = mapOf(
                    "Evet, memnunum" to 0,
                    "Kısmen memnunum" to 0,
                    "Hayır, memnun değilim" to 0
                ),
                totalResponses = 0,
                isActive = true,
                expiresAt = oneWeekLater,
                createdAt = Timestamp.now()
            ),
            AIQuestion(
                question = "Ülkenin ekonomik gidişatını nasıl değerlendiriyorsunuz?",
                options = listOf("İyi gidiyor", "Orta", "Kötü gidiyor"),
                category = "economic",
                responses = mapOf(
                    "İyi gidiyor" to 0,
                    "Orta" to 0,
                    "Kötü gidiyor" to 0
                ),
                totalResponses = 0,
                isActive = true,
                expiresAt = oneWeekLater,
                createdAt = Timestamp.now()
            ),
            AIQuestion(
                question = "Dijital haberciliği geleneksel haberciliğe tercih eder misiniz?",
                options = listOf("Evet", "Hayır", "Fark etmez"),
                category = "technology",
                responses = mapOf(
                    "Evet" to 0,
                    "Hayır" to 0,
                    "Fark etmez" to 0
                ),
                totalResponses = 0,
                isActive = true,
                expiresAt = oneWeekLater,
                createdAt = Timestamp.now()
            )
        )

        questions.forEach { question ->
            firestore.collection(FirebaseConfig.Collections.AI_QUESTIONS)
                .add(question)
                .await()
        }
        
        Log.d(TAG, "✅ ${questions.size} AI sorusu eklendi")
    }

    /**
     * Örnek kullanıcı verisi yükle
     */
    private suspend fun loadSampleUser() {
        val sampleUser = FirebaseUser(
            uid = "sample_user_123",
            email = "kamelia@example.com",
            username = "Kamelia Ahmed",
            photoUrl = "https://images.unsplash.com/photo-1494790108755-2616b332c913?w=150&h=150&fit=crop&crop=face",
            newsReadCount = 45,
            newsSharedCount = 12,
            totalXp = 222,
            badges = listOf(
                UserBadge(
                    badgeId = "confident_reader",
                    name = "Confident\nReader",
                    description = "Read 10 news articles",
                    iconUrl = "https://images.unsplash.com/photo-1614680376573-df3480f0c6ff?w=100&h=100&fit=crop",
                    earnedAt = Timestamp.now(),
                    rarity = "common"
                ),
                UserBadge(
                    badgeId = "responsible_reader",
                    name = "Responsible\nReader",
                    description = "Share 5 news articles",
                    iconUrl = "https://images.unsplash.com/photo-1614680376573-df3480f0c6ff?w=100&h=100&fit=crop",
                    rarity = "rare"
                ),
                UserBadge(
                    badgeId = "serious_learner",
                    name = "Serious\nLearner",
                    description = "Complete 3 quizzes",
                    iconUrl = "https://images.unsplash.com/photo-1614680376573-df3480f0c6ff?w=100&h=100&fit=crop",
                    rarity = "epic"
                )
            ),
            isPremium = true,
            premiumExpiryDate = Timestamp.now(),
            dailyTasks = listOf("task1", "task2"),
            completedTasks = listOf("task3"),
            notificationsEnabled = true,
            darkModeEnabled = false,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )

        // Kullanıcıyı Firebase'e ekle
        firestore.collection(FirebaseConfig.Collections.USERS)
            .document(sampleUser.uid)
            .set(sampleUser)
            .await()

        Log.d(TAG, "✅ Örnek kullanıcı eklendi: ${sampleUser.username}")
    }

    /**
     * Örnek video verileri yükle - Sadece AI oluşturulmuş videolar
     */
    private suspend fun loadSampleVideos() {
        // AI oluşturulmuş videolar için placeholder - gerçek videolar backend tarafından oluşturulacak
        Log.d(TAG, "📹 Sample video yükleme atlandı - AI videolar backend tarafından oluşturulacak")
        
        // Sadece örnek video generation request'leri ekleyelim
        val sampleRequests = listOf(
            VideoGenerationRequest(
                id = "request_sample_001",
                newsId = "news_001", // Mevcut haber ID'si
                userId = "sample_user_123",
                requestType = VideoRequestType.MANUAL,
                config = VideoGenerationConfig(
                    resolution = "1080p",
                    quality = VideoQuality.HIGH,
                    includeBackgroundMusic = true,
                    includeSubtitles = true
                ),
                status = VideoGenerationStatus.PENDING, // Backend tarafından işlenecek
                progress = 0,
                currentStep = "Video oluşturma bekleniyor",
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
        )

        // Sadece örnek istekleri ekle - gerçek videolar backend oluşturacak
        for (request in sampleRequests) {
            firestore.collection(FirebaseConfig.Collections.VIDEO_GENERATION_REQUESTS)
                .document(request.id)
                .set(request)
                .await()
        }
        
        Log.d(TAG, "✅ Video generation request'leri eklendi - Backend işlemeye hazır")
    }
}

