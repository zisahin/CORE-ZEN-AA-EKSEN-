// Firebase Cloud Functions - functions/index.js
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const { Translate } = require('@google-cloud/translate').v2;
const { LanguageServiceClient } = require('@google-cloud/language');

admin.initializeApp();

const translate = new Translate();
const language = new LanguageServiceClient();

// AI Haber Özeti Oluşturma
exports.generateNewsSummary = functions.https.onCall(async (data, context) => {
    try {
        const { newsId, type = 'daily' } = data;
        
        // Haberi Firestore'dan al
        const newsDoc = await admin.firestore()
            .collection('news')
            .doc(newsId)
            .get();
        
        if (!newsDoc.exists) {
            throw new functions.https.HttpsError('not-found', 'News not found');
        }
        
        const news = newsDoc.data();
        
        // AI ile özet oluştur (gerçek implementasyon için OpenAI API kullanılabilir)
        const summary = await generateAISummary(news.content, type);
        
        // Özeti Firestore'a kaydet
        const summaryDoc = await admin.firestore()
            .collection('ai_summaries')
            .add({
                newsId: newsId,
                summary: summary,
                type: type,
                createdAt: admin.firestore.FieldValue.serverTimestamp(),
                sentiment: await analyzeSentiment(news.content)
            });
        
        return { summaryId: summaryDoc.id, summary: summary };
        
    } catch (error) {
        console.error('Error generating summary:', error);
        throw new functions.https.HttpsError('internal', 'Failed to generate summary');
    }
});

// Haber Doğrulama
exports.verifyNews = functions.https.onCall(async (data, context) => {
    try {
        const { newsUrl, newsContent } = data;
        
        // AA kaynaklarında ara
        const verificationResult = await verifyAgainstAASources(newsContent);
        
        // Sonucu kaydet
        const verificationDoc = await admin.firestore()
            .collection('ai_verification')
            .add({
                userId: context.auth.uid,
                newsUrl: newsUrl,
                newsContent: newsContent,
                verificationResult: verificationResult.status,
                confidence: verificationResult.confidence,
                sources: verificationResult.sources,
                explanation: verificationResult.explanation,
                createdAt: admin.firestore.FieldValue.serverTimestamp()
            });
        
        return {
            verificationId: verificationDoc.id,
            result: verificationResult.status,
            confidence: verificationResult.confidence,
            explanation: verificationResult.explanation
        };
        
    } catch (error) {
        console.error('Error verifying news:', error);
        throw new functions.https.HttpsError('internal', 'Failed to verify news');
    }
});

// AI Anket Oluşturma
exports.generateAIPoll = functions.https.onCall(async (data, context) => {
    try {
        const { category = 'general' } = data;
        
        // AI ile anket sorusu oluştur
        const poll = await generatePollQuestion(category);
        
        // Anketi Firestore'a kaydet
        const pollDoc = await admin.firestore()
            .collection('ai_polls')
            .add({
                question: poll.question,
                options: poll.options,
                category: category,
                targetAudience: 'all',
                isActive: true,
                responses: {},
                expiresAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000), // 7 gün
                createdAt: admin.firestore.FieldValue.serverTimestamp()
            });
        
        return { pollId: pollDoc.id, poll: poll };
        
    } catch (error) {
        console.error('Error generating poll:', error);
        throw new functions.https.HttpsError('internal', 'Failed to generate poll');
    }
});

// Geri Bildirim İşleme
exports.processFeedback = functions.https.onCall(async (data, context) => {
    try {
        const { newsId, type, message, rating } = data;
        
        // Geri bildirimi kaydet
        const feedbackDoc = await admin.firestore()
            .collection('feedback')
            .add({
                userId: context.auth.uid,
                newsId: newsId,
                type: type,
                message: message,
                rating: rating,
                status: 'pending',
                createdAt: admin.firestore.FieldValue.serverTimestamp()
            });
        
        // Admin'e bildirim gönder
        await sendNotificationToAdmins({
            title: 'Yeni Geri Bildirim',
            body: `${type} kategorisinde yeni geri bildirim alındı`,
            data: { feedbackId: feedbackDoc.id, newsId: newsId }
        });
        
        return { feedbackId: feedbackDoc.id };
        
    } catch (error) {
        console.error('Error processing feedback:', error);
        throw new functions.https.HttpsError('internal', 'Failed to process feedback');
    }
});

// Kullanıcı Skorunu Güncelleme
exports.updateUserScore = functions.https.onCall(async (data, context) => {
    try {
        const { points, gameType } = data;
        const userId = context.auth.uid;
        
        // Kullanıcı skorunu güncelle
        await admin.firestore()
            .collection('users')
            .doc(userId)
            .update({
                totalScore: admin.firestore.FieldValue.increment(points),
                [`gameScores.${gameType}`]: admin.firestore.FieldValue.increment(points)
            });
        
        // Level kontrolü
        const userDoc = await admin.firestore()
            .collection('users')
            .doc(userId)
            .get();
        
        const userData = userDoc.data();
        const newScore = userData.totalScore + points;
        const newLevel = Math.floor(newScore / 1000) + 1;
        
        if (newLevel > userData.level) {
            await admin.firestore()
                .collection('users')
                .doc(userId)
                .update({ level: newLevel });
            
            // Level up bildirimi gönder
            await sendNotificationToUser(userId, {
                title: 'Level Up! 🎉',
                body: `Tebrikler! Level ${newLevel}'e yükseldiniz!`
            });
        }
        
        return { newScore: newScore, newLevel: newLevel };
        
    } catch (error) {
        console.error('Error updating user score:', error);
        throw new functions.https.HttpsError('internal', 'Failed to update user score');
    }
});

// Bildirim Gönderme
exports.sendNotification = functions.https.onCall(async (data, context) => {
    try {
        const { userId, title, body, data: notificationData } = data;
        
        await sendNotificationToUser(userId, {
            title: title,
            body: body,
            data: notificationData
        });
        
        return { success: true };
        
    } catch (error) {
        console.error('Error sending notification:', error);
        throw new functions.https.HttpsError('internal', 'Failed to send notification');
    }
});

// Günlük Görevleri Oluşturma
exports.generateDailyTasks = functions.pubsub.schedule('0 0 * * *').onRun(async (context) => {
    try {
        const tasks = [
            {
                title: '5 Haber Oku',
                description: 'Bugün 5 haber okuyarak güncel kal',
                type: 'read_news',
                targetValue: 5,
                rewardPoints: 50
            },
            {
                title: '1 Oyun Oyna',
                description: 'Bugün en az 1 oyun oyna',
                type: 'play_game',
                targetValue: 1,
                rewardPoints: 30
            },
            {
                title: '1 Haber Paylaş',
                description: 'Önemli bir haberi arkadaşlarınla paylaş',
                type: 'share_news',
                targetValue: 1,
                rewardPoints: 20
            }
        ];
        
        // Görevleri Firestore'a ekle
        const batch = admin.firestore().batch();
        tasks.forEach(task => {
            const docRef = admin.firestore().collection('daily_tasks').doc();
            batch.set(docRef, {
                ...task,
                isActive: true,
                expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000), // 24 saat
                createdAt: admin.firestore.FieldValue.serverTimestamp()
            });
        });
        
        await batch.commit();
        
        console.log('Daily tasks generated successfully');
        
    } catch (error) {
        console.error('Error generating daily tasks:', error);
    }
});

// Yardımcı Fonksiyonlar
async function generateAISummary(content, type) {
    // Gerçek implementasyon için OpenAI API kullanılabilir
    // Şimdilik basit bir özet oluşturuyoruz
    const sentences = content.split('.').slice(0, 3);
    return sentences.join('. ') + '.';
}

async function analyzeSentiment(text) {
    try {
        const [result] = await language.analyzeSentiment({
            document: { content: text, type: 'PLAIN_TEXT' }
        });
        
        const sentiment = result.documentSentiment.score;
        if (sentiment > 0.1) return 'positive';
        if (sentiment < -0.1) return 'negative';
        return 'neutral';
    } catch (error) {
        console.error('Error analyzing sentiment:', error);
        return 'neutral';
    }
}

async function verifyAgainstAASources(content) {
    // Gerçek implementasyon için AA API'si kullanılabilir
    // Şimdilik mock data döndürüyoruz
    return {
        status: 'verified',
        confidence: 0.85,
        sources: ['AA Haber Merkezi', 'AA Doğrulama Servisi'],
        explanation: 'Haber AA kaynaklarında doğrulandı.'
    };
}

async function generatePollQuestion(category) {
    const polls = {
        general: {
            question: 'Yaşam şartlarından memnun musunuz?',
            options: ['Evet, memnunum', 'Kısmen memnunum', 'Hayır, memnun değilim']
        },
        politics: {
            question: 'Hangi konuda daha fazla haber görmek istersiniz?',
            options: ['Ekonomi', 'Siyaset', 'Spor', 'Teknoloji']
        },
        social: {
            question: 'Hangi sosyal medya platformunu daha çok kullanıyorsunuz?',
            options: ['Instagram', 'Twitter', 'TikTok', 'YouTube']
        }
    };
    
    return polls[category] || polls.general;
}

async function sendNotificationToUser(userId, notification) {
    // FCM ile bildirim gönderme implementasyonu
    const userDoc = await admin.firestore()
        .collection('users')
        .doc(userId)
        .get();
    
    if (userDoc.exists) {
        const userData = userDoc.data();
        if (userData.fcmToken) {
            await admin.messaging().send({
                token: userData.fcmToken,
                notification: {
                    title: notification.title,
                    body: notification.body
                },
                data: notification.data || {}
            });
        }
    }
}

async function sendNotificationToAdmins(notification) {
    // Admin kullanıcılarına bildirim gönderme
    const adminUsers = await admin.firestore()
        .collection('users')
        .where('role', '==', 'admin')
        .get();
    
    const tokens = adminUsers.docs
        .map(doc => doc.data().fcmToken)
        .filter(token => token);
    
    if (tokens.length > 0) {
        await admin.messaging().sendMulticast({
            tokens: tokens,
            notification: {
                title: notification.title,
                body: notification.body
            },
            data: notification.data || {}
        });
    }
}

