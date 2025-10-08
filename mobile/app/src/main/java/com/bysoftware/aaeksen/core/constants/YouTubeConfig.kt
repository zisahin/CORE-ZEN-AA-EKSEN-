package com.bysoftware.aaeksen.core.constants

import com.bysoftware.aaeksen.BuildConfig

object YouTubeConfig {
    // API Key BuildConfig'den alınır (local.properties'den)
    val API_KEY: String = BuildConfig.YOUTUBE_API_KEY
    
    // Anadolu Ajansı YouTube Channel ID
    const val ANADOLU_AJANSI_CHANNEL_ID = "UCVbZYhcirOke2vipxs4lT2w"
    
    // API Base URL
    const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    
    // Search parameters
    const val DEFAULT_MAX_RESULTS = 20
    const val VIDEO_DURATION_SHORT = "short" // Under 4 minutes
    const val ORDER_DATE = "date"
    const val ORDER_RELEVANCE = "relevance"
    const val TYPE_VIDEO = "video"
    
    // Parts to request from API
    const val PART_SNIPPET = "snippet"
    const val PART_STATISTICS = "statistics"
    const val PART_CONTENT_DETAILS = "contentDetails"
    const val PART_ALL = "$PART_SNIPPET,$PART_STATISTICS,$PART_CONTENT_DETAILS"
}
