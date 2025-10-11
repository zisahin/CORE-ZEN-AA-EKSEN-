package com.bysoftware.aaeksen.core.constants

// Firebase Configuration
object FirebaseConfig {
    // Firebase Collections
    object Collections {
        // Ana Koleksiyonlar
        const val USERS = "users"
        const val NEWS = "news"
        const val TIME_TUNNEL_CATEGORIES = "time_tunnel_categories"
        
        // Oyun Koleksiyonları
        const val CROSSWORD_CATEGORIES = "crossword_categories"
        const val CROSSWORD_WORD_GROUPS = "crossword_word_groups"
        const val QUIZ_CATEGORIES = "quiz_categories"
        const val QUIZ_QUESTIONS = "quiz_questions"
        const val MAP_GUESS_GAMES = "map_guess_games"
        const val USER_GAME_STATS = "user_game_stats"
        
        // Kullanıcı Sistemi
        const val USER_PROFILES = "user_profiles"
        const val DAILY_TASKS = "daily_tasks"
        const val USER_TASK_PROGRESS = "user_task_progress"
        const val BADGES = "badges"
        const val USER_BADGES = "user_badges"
        const val USER_STATS = "user_stats"
        const val XP_HISTORY = "xp_history"
        
        // AI Sistemi
        const val AI_CHATS = "ai_chats"
        const val AI_QUESTIONS = "ai_questions"
        const val USER_QUESTION_RESPONSES = "user_question_responses"
        
        // Video Sistemi
        const val VIDEOS = "videos"
        const val VIDEO_GENERATION_REQUESTS = "video_generation_requests"
        const val VIDEO_PLAYLISTS = "video_playlists"
        const val VIDEO_COMMENTS = "video_comments"
        const val VIDEO_STATS = "video_stats"
    }
    
    // Analytics Events
    object Analytics {
        // Haber Events
        const val NEWS_VIEWED = "news_viewed"
        const val NEWS_SHARED = "news_shared"
        const val NEWS_LIKED = "news_liked"
        
        // Oyun Events
        const val GAME_STARTED = "game_started"
        const val GAME_COMPLETED = "game_completed"
        const val CROSSWORD_SOLVED = "crossword_solved"
        const val QUIZ_ANSWERED = "quiz_answered"
        const val MAP_GUESS_PLAYED = "map_guess_played"
        
        // AI Events
        const val AI_CHAT_STARTED = "ai_chat_started"
        const val AI_QUESTION_ANSWERED = "ai_question_answered"
        
        // Görev Events
        const val DAILY_TASK_COMPLETED = "daily_task_completed"
        const val BADGE_EARNED = "badge_earned"
        
        // Premium Events
        const val PREMIUM_PURCHASED = "premium_purchased"
    }
    
    // XP Puanları
    object XP {
        const val NEWS_READ = 5
        const val NEWS_SHARE = 10
        const val QUIZ_CORRECT = 10
        const val CROSSWORD_WORD = 5
        const val CROSSWORD_CATEGORY_COMPLETE = 50
        const val MAP_GUESS_CORRECT = 20
        const val DAILY_TASK_COMPLETE = 20
    }
    
    // Oyun Kategorileri
    object GameCategories {
        const val POLITICS = "Siyaset"
        const val ECONOMY = "Ekonomi"
        const val SPORTS = "Spor"
        const val AGENDA = "Gündem"
        const val TECHNOLOGY = "Teknoloji"
        const val HEALTH = "Sağlık"
    }
}



