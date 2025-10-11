package com.bysoftware.aaeksen.data.mapper

import com.bysoftware.aaeksen.data.remote.dto.CityDto
import com.bysoftware.aaeksen.data.remote.dto.CityNewsDto
import com.bysoftware.aaeksen.data.remote.dto.NewsCategoryDto
import com.bysoftware.aaeksen.domain.model.City
import com.bysoftware.aaeksen.domain.model.CityNews
import com.bysoftware.aaeksen.domain.model.NewsCategory

fun CityDto.toCity(): City {
    return City(
        id = id,
        name = name,
        totalNewsCount = totalNewsCount,
        coordinates = Pair(latitude, longitude),
        categoryNews = categoryNews ?: emptyMap()
    )
}

fun NewsCategoryDto.toNewsCategory(): NewsCategory {
    return NewsCategory(
        id = id,
        name = name,
        icon = icon,
        color = color
    )
}

fun CityNewsDto.toCityNews(): CityNews {
    return CityNews(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        source = source,
        publishDate = publishDate,
        timeAgo = timeAgo,
        categoryId = categoryId,
        cityId = cityId,
        url = url
    )
}
