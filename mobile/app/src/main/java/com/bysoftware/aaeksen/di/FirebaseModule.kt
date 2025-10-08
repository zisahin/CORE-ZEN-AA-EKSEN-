package com.bysoftware.aaeksen.di

import com.bysoftware.aaeksen.data.firebase.SampleDataInitializer
import com.bysoftware.aaeksen.data.firebase.repository.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    // ==================== FIREBASE SERVICES ====================
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics = FirebaseAnalytics.getInstance(
        com.bysoftware.aaeksen.AAEksenApplication.getInstance()
    )
    
    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
    
    // ==================== REPOSITORIES ====================
    
    @Provides
    @Singleton
    fun provideNewsRepository(
        firestore: FirebaseFirestore
    ): NewsRepository = NewsRepository(firestore)
    
    @Provides
    @Singleton
    fun provideGameRepository(
        firestore: FirebaseFirestore
    ): GameRepository = GameRepository(firestore)
    
    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): UserRepository = UserRepository(firestore, auth)
    
    @Provides
    @Singleton
    fun provideAIRepository(
        firestore: FirebaseFirestore
    ): AIRepository = AIRepository(firestore)
    
    @Provides
    @Singleton
    fun provideSampleDataInitializer(
        firestore: FirebaseFirestore
    ): SampleDataInitializer = SampleDataInitializer(firestore)
}




