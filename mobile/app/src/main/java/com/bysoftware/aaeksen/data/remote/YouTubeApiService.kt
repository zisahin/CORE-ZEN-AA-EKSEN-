package com.bysoftware.aaeksen.data.remote

import com.bysoftware.aaeksen.data.remote.dto.YouTubeSearchResponse
import com.bysoftware.aaeksen.data.remote.dto.YouTubeVideoDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * YouTube Data API v3 service interface
 */
interface YouTubeApiService {
    
    companion object {
        const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
        
        // Anadolu Ajansı channel ID
        const val ANADOLU_AJANSI_CHANNEL_ID = "UCVbZYhcirOke2vipxs4lT2w"
        
        // API endpoints
        const val SEARCH_ENDPOINT = "search"
        const val VIDEOS_ENDPOINT = "videos"
        const val CHANNELS_ENDPOINT = "channels"
    }
    
    /**
     * Search for videos from Anadolu Ajansı channel
     * Filters for Shorts (duration < 60 seconds)
     */
    @GET(SEARCH_ENDPOINT)
    suspend fun searchChannelVideos(
        @Query("key") apiKey: String,
        @Query("channelId") channelId: String = ANADOLU_AJANSI_CHANNEL_ID,
        @Query("part") part: String = "snippet",
        @Query("order") order: String = "date",
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 20,
        @Query("pageToken") pageToken: String? = null
    ): YouTubeSearchResponse
    
    /**
     * Get detailed video information including statistics
     */
    @GET(VIDEOS_ENDPOINT)
    suspend fun getVideoDetails(
        @Query("key") apiKey: String,
        @Query("id") videoIds: String, // Comma-separated video IDs
        @Query("part") part: String = "snippet,statistics,contentDetails"
    ): YouTubeVideoDetailsResponse
    
    /**
     * Get channel information
     */
    @GET(CHANNELS_ENDPOINT)
    suspend fun getChannelInfo(
        @Query("key") apiKey: String,
        @Query("id") channelId: String = ANADOLU_AJANSI_CHANNEL_ID,
        @Query("part") part: String = "snippet,statistics"
    ): YouTubeSearchResponse
    
    /**
     * Search videos by query within the channel
     */
    @GET(SEARCH_ENDPOINT)
    suspend fun searchVideosInChannel(
        @Query("key") apiKey: String,
        @Query("channelId") channelId: String = ANADOLU_AJANSI_CHANNEL_ID,
        @Query("q") query: String,
        @Query("part") part: String = "snippet",
        @Query("order") order: String = "relevance",
        @Query("type") type: String = "video",
        @Query("videoDuration") videoDuration: String = "short",
        @Query("maxResults") maxResults: Int = 25,
        @Query("pageToken") pageToken: String? = null
    ): YouTubeSearchResponse
}
