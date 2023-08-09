package com.gksenon.sleepdiary.di

import android.content.Context
import androidx.room.Room
import com.gksenon.sleepdiary.data.RoomDiary
import com.gksenon.sleepdiary.data.SleepDatabase
import com.gksenon.sleepdiary.domain.Diary
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DiaryModule {

    @Singleton
    @Provides
    fun provideDiary(@ApplicationContext context: Context): Diary {
        val database = Room.databaseBuilder(context, SleepDatabase::class.java, "diary").build()
        return RoomDiary(database.sleepDao())
    }
}