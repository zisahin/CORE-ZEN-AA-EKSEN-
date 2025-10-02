package com.bysoftware.aaeksen.data.remote

import com.bysoftware.aaeksen.data.remote.dto.CityDto
import com.bysoftware.aaeksen.data.remote.dto.CityNewsDto
import com.bysoftware.aaeksen.data.remote.dto.NewsCategoryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface NewsMapApi {
    
    @GET("cities")
    suspend fun getCities(): List<CityDto>
    
    @GET("news-categories")
    suspend fun getNewsCategories(): List<NewsCategoryDto>
    
    @GET("cities/{cityId}/news/{categoryId}")
    suspend fun getCityNewsByCategory(
        @Path("cityId") cityId: String,
        @Path("categoryId") categoryId: String
    ): List<CityNewsDto>
}
