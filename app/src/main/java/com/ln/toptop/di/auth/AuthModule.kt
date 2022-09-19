package com.ln.toptop.di.auth

import android.app.Application
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ln.toptop.data.datastore.AppDataStore
import com.ln.toptop.data.datastore.AppDataStoreManager
import com.ln.toptop.data.network.auth.AuthRepo
import com.ln.toptop.data.network.user.UserRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Singleton
    @Provides
    fun provideAppDataStoreManager(
        application: Application
    ): AppDataStore {
        return AppDataStoreManager(application)
    }

    @Singleton
    @Provides
    fun provideAuthRepo(
        appDataStoreManager: AppDataStoreManager
    ): AuthRepo = AuthRepo(Firebase.auth, appDataStoreManager)

    @Singleton
    @Provides
    fun provideUserRepo(): UserRepo = UserRepo(Firebase.auth, Firebase.firestore)
}
