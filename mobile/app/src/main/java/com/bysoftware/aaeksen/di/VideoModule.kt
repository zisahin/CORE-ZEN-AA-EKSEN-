package com.bysoftware.aaeksen.di

import com.bysoftware.aaeksen.data.remote.YouTubeApiService
import com.bysoftware.aaeksen.data.repository.VideoRepositoryImpl
import com.bysoftware.aaeksen.domain.repository.VideoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VideoModule {

    @Binds
    @Singleton
    abstract fun bindVideoRepository(
        videoRepositoryImpl: VideoRepositoryImpl
    ): VideoRepository

    companion object {
        @Provides
        @Singleton
        fun provideYouTubeApiService(retrofit: Retrofit): YouTubeApiService {
            return retrofit.create(YouTubeApiService::class.java)
        }
    }
}
