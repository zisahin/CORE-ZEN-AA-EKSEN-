import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import dotenv from 'dotenv';
import cron from 'node-cron';

// Route imports
import chatRoutes from './routes/chat.js';
import summaryRoutes from './routes/summary.js';
import timelineRoutes from './routes/timeline.js';
import podcastRoutes from './routes/podcast.js';
import questionRoutes from './routes/questions.js';
import quizRoutes from './routes/quiz.js';
import rssRoutes from './routes/rss.js';
import citiesRoutes from './routes/cities.js';
import dailyTasksService from './services/dailyTasks.js';

// Load environment variables
dotenv.config();

const app = express();
const PORT = process.env.PORT || 3001;

// Middleware
app.use(helmet());
app.use(cors({
  origin: process.env.WEB_URL || 'http://localhost:3000',
  credentials: true
}));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ 
    status: 'OK', 
    service: 'AI Service',
    version: '1.0.0',
    timestamp: new Date().toISOString(),
    description: 'AA Habercilik AI Entegrasyon Servisi'
  });
});

// API Routes
app.use('/api/chat', chatRoutes);
app.use('/api/summary', summaryRoutes);
app.use('/api/timeline', timelineRoutes);
app.use('/api/podcast', podcastRoutes);
app.use('/api/questions', questionRoutes);
app.use('/api/quiz', quizRoutes);
app.use('/api/rss', rssRoutes);
app.use('/api/cities', citiesRoutes);

// Günlük görevler için cron job'lar
cron.schedule('0 6 * * *', () => {
  console.log('🌅 Günlük AI görevleri başlatılıyor...');
  dailyTasksService.generateDailyQuestions();
  dailyTasksService.generateDailyQuizzes();
  dailyTasksService.analyzeNewsForTimelines();
});

// Haftalık özetler için cron job
cron.schedule('0 8 * * 1', () => {
  console.log('📊 Haftalık AI analizleri başlatılıyor...');
  dailyTasksService.generateWeeklySummaries();
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error('❌ AI Service Error:', err);
  res.status(err.status || 500).json({
    error: 'Bir hata oluştu',
    message: process.env.NODE_ENV === 'development' ? err.message : 'Internal server error'
  });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    error: 'Endpoint bulunamadı',
    availableEndpoints: [
      '/api/chat',
      '/api/summary', 
      '/api/timeline',
      '/api/podcast',
      '/api/questions',
      '/api/quiz',
      '/api/rss',
      '/api/cities'
    ]
  });
});

app.listen(PORT, () => {
  console.log(`🤖 AI Service sunucusu port ${PORT}'de çalışıyor`);
  console.log(`📱 Web sitesi: ${process.env.WEB_URL || 'http://localhost:3000'}`);
  console.log(`🔧 Environment: ${process.env.NODE_ENV || 'development'}`);
  console.log('✅ Tüm AI özellikleri hazır!');
});

export default app;


