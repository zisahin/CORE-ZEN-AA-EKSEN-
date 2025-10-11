package com.bysoftware.aaeksen.data.firebase.repository

import com.bysoftware.aaeksen.core.constants.FirebaseConfig
import com.bysoftware.aaeksen.data.firebase.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI işlemleri için Repository
 * Chat geçmişi ve AA AI Soruyor anket sistemi
 */
@Singleton
class AIRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    
    // ==================== AI CHAT ====================
    
    /**
     * Yeni AI chat başlat
     */
    suspend fun startAIChat(userId: String, context: String = "general"): Result<String> {
        return try {
            val chat = FirebaseAIChat(
                userId = userId,
                context = context,
                messages = emptyList(),
                isActive = true,
                createdAt = com.google.firebase.Timestamp.now()
            )
            
            val docRef = firestore.collection(FirebaseConfig.Collections.AI_CHATS)
                .add(chat)
                .await()
            
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Chat'e mesaj ekle
     */
    suspend fun addChatMessage(
        chatId: String,
        message: String,
        isUser: Boolean
    ): Result<Unit> {
        return try {
            val newMessage = ChatMessage(
                id = System.currentTimeMillis().toString(),
                content = message,
                isUser = isUser,
                timestamp = com.google.firebase.Timestamp.now()
            )
            
            firestore.collection(FirebaseConfig.Collections.AI_CHATS)
                .document(chatId)
                .update(
                    "messages", com.google.firebase.firestore.FieldValue.arrayUnion(newMessage),
                    "updatedAt", com.google.firebase.Timestamp.now()
                )
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Kullanıcının chat geçmişini getir
     */
    suspend fun getUserChats(userId: String): Result<List<FirebaseAIChat>> {
        return try {
            val chats = firestore.collection(FirebaseConfig.Collections.AI_CHATS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("isActive", true)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
                .toObjects(FirebaseAIChat::class.java)
            Result.success(chats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Belirli bir chat'i getir
     */
    suspend fun getChat(chatId: String): Result<FirebaseAIChat?> {
        return try {
            val chat = firestore.collection(FirebaseConfig.Collections.AI_CHATS)
                .document(chatId)
                .get()
                .await()
                .toObject(FirebaseAIChat::class.java)
            Result.success(chat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Chat'i sonlandır
     */
    suspend fun endChat(chatId: String): Result<Unit> {
        return try {
            firestore.collection(FirebaseConfig.Collections.AI_CHATS)
                .document(chatId)
                .update("isActive", false)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== AA AI SORUYOR (ANKET SİSTEMİ) ====================
    
    /**
     * Aktif AI sorularını getir
     */
    suspend fun getActiveQuestions(limit: Long = 10): Result<List<AIQuestion>> {
        return try {
            val questions = firestore.collection(FirebaseConfig.Collections.AI_QUESTIONS)
                .whereEqualTo("isActive", true)
                .whereGreaterThan("expiresAt", com.google.firebase.Timestamp.now())
                .orderBy("expiresAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
                .toObjects(AIQuestion::class.java)
            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Rastgele bir aktif soru getir (ana ekranda göstermek için)
     */
    suspend fun getRandomActiveQuestion(): Result<AIQuestion?> {
        return try {
            val questions = firestore.collection(FirebaseConfig.Collections.AI_QUESTIONS)
                .whereEqualTo("isActive", true)
                .whereGreaterThan("expiresAt", com.google.firebase.Timestamp.now())
                .get()
                .await()
                .toObjects(AIQuestion::class.java)
            
            val random = questions.randomOrNull()
            Result.success(random)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Kullanıcı soruyu cevapladı
     */
    suspend fun answerQuestion(
        userId: String,
        questionId: String,
        selectedOption: String
    ): Result<Unit> {
        return try {
            // Önce kullanıcının bu soruyu daha önce cevaplayıp cevaplamadığını kontrol et
            val existingResponse = firestore.collection(FirebaseConfig.Collections.USER_QUESTION_RESPONSES)
                .whereEqualTo("userId", userId)
                .whereEqualTo("questionId", questionId)
                .limit(1)
                .get()
                .await()
            
            if (!existingResponse.isEmpty) {
                return Result.failure(Exception("Question already answered"))
            }
            
            // Cevabı kaydet
            val response = UserQuestionResponse(
                userId = userId,
                questionId = questionId,
                selectedOption = selectedOption,
                answeredAt = com.google.firebase.Timestamp.now()
            )
            
            firestore.collection(FirebaseConfig.Collections.USER_QUESTION_RESPONSES)
                .add(response)
                .await()
            
            // Sorunun cevap sayısını güncelle
            val questionRef = firestore.collection(FirebaseConfig.Collections.AI_QUESTIONS)
                .document(questionId)
            
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(questionRef)
                val question = snapshot.toObject(AIQuestion::class.java)
                
                if (question != null) {
                    val updatedResponses = question.responses.toMutableMap()
                    updatedResponses[selectedOption] = (updatedResponses[selectedOption] ?: 0) + 1
                    
                    transaction.update(questionRef,
                        "responses", updatedResponses,
                        "totalResponses", question.totalResponses + 1
                    )
                }
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Kullanıcının bir soruya cevap verip vermediğini kontrol et
     */
    suspend fun hasUserAnsweredQuestion(
        userId: String,
        questionId: String
    ): Result<Boolean> {
        return try {
            val response = firestore.collection(FirebaseConfig.Collections.USER_QUESTION_RESPONSES)
                .whereEqualTo("userId", userId)
                .whereEqualTo("questionId", questionId)
                .limit(1)
                .get()
                .await()
            
            Result.success(!response.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Soru sonuçlarını getir
     */
    suspend fun getQuestionResults(questionId: String): Result<AIQuestion?> {
        return try {
            val question = firestore.collection(FirebaseConfig.Collections.AI_QUESTIONS)
                .document(questionId)
                .get()
                .await()
                .toObject(AIQuestion::class.java)
            Result.success(question)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Kullanıcının cevapladığı soruları getir
     */
    suspend fun getUserAnsweredQuestions(userId: String): Result<List<UserQuestionResponse>> {
        return try {
            val responses = firestore.collection(FirebaseConfig.Collections.USER_QUESTION_RESPONSES)
                .whereEqualTo("userId", userId)
                .orderBy("answeredAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(UserQuestionResponse::class.java)
            Result.success(responses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
