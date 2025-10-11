package com.bysoftware.aaeksen.di

import com.bysoftware.aaeksen.data.repository.NewsRepositoryImpl
import com.bysoftware.aaeksen.domain.repository.NewsRepository
import com.bysoftware.aaeksen.data.firebase.repository.GamificationRepository
import com.bysoftware.aaeksen.data.firebase.repository.UserRepository
import com.bysoftware.aaeksen.data.firebase.repository.VideoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository
}
