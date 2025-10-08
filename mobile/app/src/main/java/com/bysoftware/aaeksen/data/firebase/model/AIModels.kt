package com.bysoftware.aaeksen.data.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * AI Chat Geçmişi
 */
data class FirebaseAIChat(
    @DocumentId val id: String = "",
    val userId: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val context: String = "general", // general, news_specific, verification
    val isActive: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

/**
 * Chat Mesajı
 */
data class ChatMessage(
    val id: String = "",
    val content: String = "",
    val isUser: Boolean = true, // true = kullanıcı, false = AI
    val timestamp: Timestamp? = null
)

/**
 * AA AI Soruyor - Anket Sistemi
 */
data class AIQuestion(
    @DocumentId val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val category: String = "", // social, political, economic, vb.
    val responses: Map<String, Int> = emptyMap(), // option -> count
    val totalResponses: Int = 0,
    val isActive: Boolean = true,
    val expiresAt: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

/**
 * Kullanıcı Anket Cevabı
 */
data class UserQuestionResponse(
    @DocumentId val id: String = "",
    val userId: String = "",
    val questionId: String = "",
    val selectedOption: String = "",
    @ServerTimestamp val answeredAt: Timestamp? = null
)
