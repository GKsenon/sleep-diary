package com.gksenon.sleepdiary.di

import android.content.Context
import com.gksenon.sleepdiary.data.DataStoreTracker
import com.gksenon.sleepdiary.domain.Diary
import com.gksenon.sleepdiary.domain.Tracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class TrackerModule {

    @Provides
    @Singleton
    fun provideTracker(diary: Diary, @ApplicationContext context: Context): Tracker = DataStoreTracker(diary, context)
}