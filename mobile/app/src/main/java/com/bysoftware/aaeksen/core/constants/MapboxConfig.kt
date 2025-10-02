package com.bysoftware.aaeksen.core.constants

object MapboxConfig {
    // Mapbox Studio'dan aldığın Style URL'ini buraya ekle
    const val STYLE_URL = "style"
    
    // Örnek:
    // const val STYLE_URL = "mapbox://styles/john-doe/ckf3fz7ad0r3u1amqwg4v8w2o"
    
    // Alternatif olarak HTTP URL de kullanabilirsin:
    // const val STYLE_URL = "https://api.mapbox.com/styles/v1/your-username/your-style-id?access_token=your-token"
    
    // Türkiye koordinatları
    const val TURKEY_CENTER_LAT = 39.0
    const val TURKEY_CENTER_LNG = 35.3
    const val TURKEY_ZOOM = 3.8
}
