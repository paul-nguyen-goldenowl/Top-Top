package com.ln.toptop.di.auth.video

import com.ln.toptop.data.local.video.RecordRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VideoModule {

    @Singleton
    @Provides
    fun provideRecordRepo() = RecordRepo()
}