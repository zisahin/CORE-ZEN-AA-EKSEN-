package com.bysoftware.aaeksen.data.firebase.repository

import com.bysoftware.aaeksen.core.constants.FirebaseConfig
import com.bysoftware.aaeksen.data.firebase.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Oyun işlemleri için Repository
 * Çengel bulmaca, Quiz ve Harita tahmin oyunlarını yönetir
 */
@Singleton
class GameRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    
    // ==================== ÇENGEL BULMACA ====================
    
    /**
     * Çengel bulmaca kategorilerini getir
     */
    suspend fun getCrosswordCategories(): Result<List<CrosswordCategory>> {
        return try {
            val categories = firestore.collection(FirebaseConfig.Collections.CROSSWORD_CATEGORIES)
                .whereEqualTo("isActive", true)
                .get()
                .await()
                .toObjects(CrosswordCategory::class.java)
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Belirli bir kategorinin kelime gruplarını getir
     */
    suspend fun getCrosswordWordGroups(categoryId: String): Result<List<CrosswordWordGroup>> {
        return try {
            val wordGroups = firestore.collection(FirebaseConfig.Collections.CROSSWORD_WORD_GROUPS)
                .whereEqualTo("categoryId", categoryId)
                .whereEqualTo("isActive", true)
                .get()
                .await()
                .toObjects(CrosswordWordGroup::class.java)
            Result.success(wordGroups)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Rastgele bir kelime grubu getir (günlük bulmaca için)
     */
    suspend fun getRandomWordGroup(): Result<CrosswordWordGroup?> {
        return try {
            val wordGroups = firestore.collection(FirebaseConfig.Collections.CROSSWORD_WORD_GROUPS)
                .whereEqualTo("isActive", true)
                .get()
                .await()
                .toObjects(CrosswordWordGroup::class.java)
            
            val random = wordGroups.randomOrNull()
            Result.success(random)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== QUIZ ====================
    
    /**
     * Quiz kategorilerini getir
     */
    suspend fun getQuizCategories(): Result<List<QuizCategory>> {
        return try {
            val categories = firestore.collection(FirebaseConfig.Collections.QUIZ_CATEGORIES)
                .whereEqualTo("isActive", true)
                .get()
                .await()
                .toObjects(QuizCategory::class.java)
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Belirli bir kategorinin quiz sorularını getir
     */
    suspend fun getQuizQuestions(
        categoryId: String,
        limit: Long = 10
    ): Result<List<QuizGameQuestion>> {
        return try {
            val questions = firestore.collection(FirebaseConfig.Collections.QUIZ_QUESTIONS)
                .whereEqualTo("categoryId", categoryId)
                .whereEqualTo("isActive", true)
                .limit(limit)
                .get()
                .await()
                .toObjects(QuizGameQuestion::class.java)
                .shuffled() // Karıştır
            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Rastgele quiz soruları getir
     */
    suspend fun getRandomQuizQuestions(limit: Long = 10): Result<List<QuizGameQuestion>> {
        return try {
            val questions = firestore.collection(FirebaseConfig.Collections.QUIZ_QUESTIONS)
                .whereEqualTo("isActive", true)
                .get()
                .await()
                .toObjects(QuizGameQuestion::class.java)
                .shuffled()
                .take(limit.toInt())
            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== HARİTA TAHMİN OYUNU ====================
    
    /**
     * Harita tahmin oyunlarını getir
     */
    suspend fun getMapGuessGames(limit: Long = 20): Result<List<MapGuessGame>> {
        return try {
            val games = firestore.collection(FirebaseConfig.Collections.MAP_GUESS_GAMES)
                .whereEqualTo("isActive", true)
                .limit(limit)
                .get()
                .await()
                .toObjects(MapGuessGame::class.java)
            Result.success(games)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Rastgele bir harita tahmin oyunu getir
     */
    suspend fun getRandomMapGuessGame(): Result<MapGuessGame?> {
        return try {
            val games = firestore.collection(FirebaseConfig.Collections.MAP_GUESS_GAMES)
                .whereEqualTo("isActive", true)
                .get()
                .await()
                .toObjects(MapGuessGame::class.java)
            
            val random = games.randomOrNull()
            Result.success(random)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Belirli bir habere ait harita tahmin oyununu getir
     */
    suspend fun getMapGuessGameByNewsId(newsId: String): Result<MapGuessGame?> {
        return try {
            val game = firestore.collection(FirebaseConfig.Collections.MAP_GUESS_GAMES)
                .whereEqualTo("newsId", newsId)
                .whereEqualTo("isActive", true)
                .limit(1)
                .get()
                .await()
                .toObjects(MapGuessGame::class.java)
                .firstOrNull()
            Result.success(game)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== KULLANICI OYUN İSTATİSTİKLERİ ====================
    
    /**
     * Kullanıcının oyun istatistiklerini getir
     */
    suspend fun getUserGameStats(userId: String): Result<UserGameStats?> {
        return try {
            val stats = firestore.collection(FirebaseConfig.Collections.USER_GAME_STATS)
                .document(userId)
                .get()
                .await()
                .toObject(UserGameStats::class.java)
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Çengel bulmaca çözüldü - istatistik güncelle
     */
    suspend fun updateCrosswordStats(userId: String, xpEarned: Int): Result<Unit> {
        return try {
            val docRef = firestore.collection(FirebaseConfig.Collections.USER_GAME_STATS)
                .document(userId)
            
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                
                if (snapshot.exists()) {
                    transaction.update(docRef,
                        "crosswordSolved", com.google.firebase.firestore.FieldValue.increment(1),
                        "totalXp", com.google.firebase.firestore.FieldValue.increment(xpEarned.toLong())
                    )
                } else {
                    val newStats = UserGameStats(
                        userId = userId,
                        crosswordSolved = 1,
                        totalXp = xpEarned.toLong()
                    )
                    transaction.set(docRef, newStats)
                }
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Quiz cevaplandı - istatistik güncelle
     */
    suspend fun updateQuizStats(userId: String, isCorrect: Boolean, xpEarned: Int): Result<Unit> {
        return try {
            val docRef = firestore.collection(FirebaseConfig.Collections.USER_GAME_STATS)
                .document(userId)
            
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                
                val updateMap = mutableMapOf<String, Any>(
                    "totalXp" to com.google.firebase.firestore.FieldValue.increment(xpEarned.toLong())
                )
                
                if (isCorrect) {
                    updateMap["quizCorrect"] = com.google.firebase.firestore.FieldValue.increment(1)
                } else {
                    updateMap["quizWrong"] = com.google.firebase.firestore.FieldValue.increment(1)
                }
                
                if (snapshot.exists()) {
                    transaction.update(docRef, updateMap)
                } else {
                    val newStats = UserGameStats(
                        userId = userId,
                        quizCorrect = if (isCorrect) 1 else 0,
                        quizWrong = if (!isCorrect) 1 else 0,
                        totalXp = xpEarned.toLong()
                    )
                    transaction.set(docRef, newStats)
                }
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Harita tahmin oyunu oynandı - istatistik güncelle
     */
    suspend fun updateMapGuessStats(userId: String, isCorrect: Boolean, xpEarned: Int): Result<Unit> {
        return try {
            val docRef = firestore.collection(FirebaseConfig.Collections.USER_GAME_STATS)
                .document(userId)
            
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                
                val updateMap = mutableMapOf<String, Any>(
                    "totalXp" to com.google.firebase.firestore.FieldValue.increment(xpEarned.toLong())
                )
                
                if (isCorrect) {
                    updateMap["mapGuessCorrect"] = com.google.firebase.firestore.FieldValue.increment(1)
                } else {
                    updateMap["mapGuessWrong"] = com.google.firebase.firestore.FieldValue.increment(1)
                }
                
                if (snapshot.exists()) {
                    transaction.update(docRef, updateMap)
                } else {
                    val newStats = UserGameStats(
                        userId = userId,
                        mapGuessCorrect = if (isCorrect) 1 else 0,
                        mapGuessWrong = if (!isCorrect) 1 else 0,
                        totalXp = xpEarned.toLong()
                    )
                    transaction.set(docRef, newStats)
                }
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
