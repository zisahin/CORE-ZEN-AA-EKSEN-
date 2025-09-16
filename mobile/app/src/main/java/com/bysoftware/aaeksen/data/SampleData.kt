package com.bysoftware.aaeksen.data

object SampleData {
    val breakingNews = listOf(
        NewsItem(
            id = "1",
            title = "More storms hit central US with tornadoes and severe winds, while the West and North brace for more rain and snow.",
            description = "CNN — As severe storms prompted at least nine tornado reports in parts of the central US, a barrage of snow, rain and harsh wind was forecast Monday in places from the West Coast to the Great Lakes, including some still without power following a similar string of severe weather last week.",
            imageUrl = "https://images.unsplash.com/photo-1547036967-23d11aacaee0?w=800&h=400&fit=crop",
            source = "CNN Indonesia",
            date = "Feb 28,2023",
            category = "World",
            isBreaking = true
        ),
        NewsItem(
            id = "breaking2",
            title = "Breaking: Major earthquake strikes coastal region, tsunami warning issued",
            description = "Emergency services are responding to a magnitude 7.2 earthquake that struck the Pacific coast early this morning.",
            imageUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800&h=400&fit=crop",
            source = "BBC News",
            date = "Sep 13,2025",
            category = "World",
            isBreaking = true
        ),
        NewsItem(
            id = "breaking3",
            title = "Tech giants announce groundbreaking AI partnership that could reshape industry",
            description = "Major technology companies have announced a collaboration to develop next-generation artificial intelligence systems.",
            imageUrl = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800&h=400&fit=crop",
            source = "TechCrunch",
            date = "Sep 13,2025",
            category = "Technology",
            isBreaking = true
        )
    )

    val recommendedNews = listOf(
        NewsItem(
            id = "2",
            title = "FIFA announces new regulations to tackle racism in football",
            description = "",
            imageUrl = "https://example.com/fifa.jpg",
            source = "Kristin Watson",
            date = "Feb 28,2023",
            category = "Sports",
            author = "Kristin Watson"
        ),
        NewsItem(
            id = "3",
            title = "Major wildfires continue to ravage Australia's forests",
            description = "",
            imageUrl = "https://example.com/wildfire.jpg",
            source = "Marvin McKinney",
            date = "Feb 28,2023",
            category = "Nature",
            author = "Marvin McKinney"
        ),
        NewsItem(
            id = "4",
            title = "UEFA announces new competition format",
            description = "",
            imageUrl = "https://example.com/uefa.jpg",
            source = "Jane Cooper",
            date = "Feb 28,2023",
            category = "Sports",
            author = "Jane Cooper"
        ),
        NewsItem(
            id = "4",
            title = "UEFA announces new competition format",
            description = "",
            imageUrl = "https://example.com/uefa.jpg",
            source = "Jane Cooper",
            date = "Feb 28,2023",
            category = "Sports",
            author = "Jane Cooper"
        )
    )

    val categories = listOf(
        CategoryTab("All", isSelected = true),
        CategoryTab("Politic"),
        CategoryTab("Sport"),
        CategoryTab("Education"),
        CategoryTab("Gaming")
    )
}

