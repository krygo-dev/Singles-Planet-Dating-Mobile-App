package com.krygodev.singlesplanet.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.krygodev.singlesplanet.repository.AuthenticationRepository
import com.krygodev.singlesplanet.repository.AuthenticationRepositoryImpl
import com.krygodev.singlesplanet.repository.ProfileRepository
import com.krygodev.singlesplanet.repository.ProfileRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthenticationRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): AuthenticationRepository {
        return AuthenticationRepositoryImpl(firebaseAuth, firebaseFirestore)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        firebaseFirestore: FirebaseFirestore
    ): ProfileRepository {
        return ProfileRepositoryImpl(firebaseFirestore)
    }
}