import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    // Firebase plugins
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.bysoftware.aaeksen"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.bysoftware.aaeksen"
        minSdk = 26
        //noinspection EditedTargetSdkVersion
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // YouTube API Key'i local.properties'den al
        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }
        val youtubeApiKey = properties.getProperty("YOUTUBE_API_KEY") ?: "YOUR_YOUTUBE_API_KEY_HERE"
        buildConfigField("String", "YOUTUBE_API_KEY", "\"$youtubeApiKey\"")
        
        val mapboxAccessToken = properties.getProperty("MAPBOX_ACCESS_TOKEN") ?: "YOUR_MAPBOX_ACCESS_TOKEN_HERE"
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"$mapboxAccessToken\"")
        
        // Manifest'e Mapbox token'ını ekle
        manifestPlaceholders["MAPBOX_ACCESS_TOKEN"] = mapboxAccessToken
        
        // Resources'e Mapbox token'ını ekle
        resValue("string", "MAPBOX_ACCESS_TOKEN", mapboxAccessToken)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true // BuildConfig için gerekli
    }
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))

    implementation("androidx.compose.material3:material3:1.2.1")

    // Mapbox SDK
    //implementation("com.mapbox.maps:android-ndk27:11.15.2")

    implementation("com.mapbox.maps:android:11.15.2")
    implementation("com.mapbox.extension:maps-compose:11.15.2")
    
    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt("com.google.dagger:hilt-compiler:2.52")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    
    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // VideoView for vertical video playback
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
    implementation("androidx.media3:media3-common:1.2.1")
    
    // YouTube Player API (backup)
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    
    // Paging 3 for infinite scroll
    implementation("androidx.paging:paging-runtime:3.2.1")
    implementation("androidx.paging:paging-compose:3.2.1")
    
    // Accompanist for system UI controller (status bar)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    
    // Lottie for animations (optional)
    implementation("com.airbnb.android:lottie-compose:6.1.0")
    
    // Firebase BOM - Tüm Firebase kütüphanelerini tek seferde yönetir
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    
    // Firebase Core Services
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    
    // Google Play Services for authentication
    implementation("com.google.android.gms:play-services-auth:21.2.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
   // implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}