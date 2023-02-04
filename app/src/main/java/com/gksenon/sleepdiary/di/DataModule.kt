package com.gksenon.sleepdiary.di

import android.content.Context
import androidx.room.Room
import com.gksenon.sleepdiary.data.SleepDao
import com.gksenon.sleepdiary.data.SleepDiaryDatabase
import com.gksenon.sleepdiary.data.SleepDiaryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideSleepDatabase(@ApplicationContext context: Context): SleepDiaryDatabase =
        Room.databaseBuilder(context, SleepDiaryDatabase::class.java, "diary").build()

    @Provides
    fun provideSleepDao(database: SleepDiaryDatabase): SleepDao = database.sleepDao()

    @Provides
    fun providesSleepDiaryRepository(sleepDao: SleepDao): SleepDiaryRepository =
        SleepDiaryRepository(sleepDao)
}