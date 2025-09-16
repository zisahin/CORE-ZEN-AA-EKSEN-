package com.bysoftware.aaeksen.data

data class NewsItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val source: String,
    val date: String,
    val category: String,
    val isBreaking: Boolean = false,
    val readTime: String? = null,
    val author: String? = null,
    val likes: Int = 0,
    val comments: Int = 0
)

data class CategoryTab(
    val name: String,
    val icon: String? = null,
    val isSelected: Boolean = false
)

