package com.bysoftware.aaeksen.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CityDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("totalNewsCount") val totalNewsCount: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("categoryNews") val categoryNews: Map<String, Int>? = null
)

data class NewsCategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("color") val color: String
)

data class CityNewsDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("source") val source: String,
    @SerializedName("publishDate") val publishDate: String,
    @SerializedName("timeAgo") val timeAgo: String,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("cityId") val cityId: String,
    @SerializedName("url") val url: String
)
