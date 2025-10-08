package com.bysoftware.aaeksen.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * YouTube Search API Response
 */
data class YouTubeSearchResponse(
    @SerializedName("items")
    val items: List<YouTubeVideoItemDto>,
    @SerializedName("nextPageToken")
    val nextPageToken: String? = null,
    @SerializedName("prevPageToken")
    val prevPageToken: String? = null,
    @SerializedName("pageInfo")
    val pageInfo: PageInfoDto
)

/**
 * YouTube Video Item from Search API
 */
data class YouTubeVideoItemDto(
    @SerializedName("id")
    val id: VideoIdDto,
    @SerializedName("snippet")
    val snippet: VideoSnippetDto
)

/**
 * Video ID object
 */
data class VideoIdDto(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("videoId")
    val videoId: String?
)

/**
 * Video snippet with metadata
 */
data class VideoSnippetDto(
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("channelId")
    val channelId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("thumbnails")
    val thumbnails: ThumbnailsDto,
    @SerializedName("channelTitle")
    val channelTitle: String,
    @SerializedName("tags")
    val tags: List<String>? = null,
    @SerializedName("categoryId")
    val categoryId: String? = null
)

/**
 * Thumbnails object
 */
data class ThumbnailsDto(
    @SerializedName("default")
    val default: ThumbnailDetailDto,
    @SerializedName("medium")
    val medium: ThumbnailDetailDto,
    @SerializedName("high")
    val high: ThumbnailDetailDto,
    @SerializedName("standard")
    val standard: ThumbnailDetailDto? = null,
    @SerializedName("maxres")
    val maxres: ThumbnailDetailDto? = null
)

/**
 * Individual thumbnail details
 */
data class ThumbnailDetailDto(
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int
)

/**
 * Page info for pagination
 */
data class PageInfoDto(
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("resultsPerPage")
    val resultsPerPage: Int
)

/**
 * YouTube Video Details API Response (for statistics)
 */
data class YouTubeVideoDetailsResponse(
    @SerializedName("items")
    val items: List<VideoDetailsItemDto>
)

/**
 * Video details item with statistics
 */
data class VideoDetailsItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("statistics")
    val statistics: VideoStatisticsDto,
    @SerializedName("contentDetails")
    val contentDetails: VideoContentDetailsDto? = null
)

/**
 * Video statistics (views, likes, etc.)
 */
data class VideoStatisticsDto(
    @SerializedName("viewCount")
    val viewCount: String? = "0",
    @SerializedName("likeCount")
    val likeCount: String? = "0",
    @SerializedName("dislikeCount")
    val dislikeCount: String? = "0", // Note: YouTube removed public dislike counts
    @SerializedName("favoriteCount")
    val favoriteCount: String? = "0",
    @SerializedName("commentCount")
    val commentCount: String? = "0"
)

/**
 * Video content details (duration, etc.)
 */
data class VideoContentDetailsDto(
    @SerializedName("duration")
    val duration: String, // ISO 8601 format: PT4M13S
    @SerializedName("dimension")
    val dimension: String? = null,
    @SerializedName("definition")
    val definition: String? = null,
    @SerializedName("caption")
    val caption: String? = null
)