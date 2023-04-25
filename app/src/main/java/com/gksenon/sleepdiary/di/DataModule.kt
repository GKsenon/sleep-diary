package com.gksenon.sleepdiary.di

import android.content.Context
import androidx.room.Room
import com.gksenon.sleepdiary.data.SleepDao
import com.gksenon.sleepdiary.data.SleepDatabase
import com.gksenon.sleepdiary.data.SleepRepository
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
    fun provideSleepDatabase(@ApplicationContext context: Context): SleepDatabase =
        Room.databaseBuilder(context, SleepDatabase::class.java, "diary").build()

    @Provides
    fun provideSleepDao(database: SleepDatabase): SleepDao = database.sleepDao()

    @Provides
    fun providesSleepDiaryRepository(@ApplicationContext context: Context, sleepDao: SleepDao): SleepRepository =
        SleepRepository(context, sleepDao)
}