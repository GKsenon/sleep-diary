package com.gksenon.sleepdiary.di

import android.content.Context
import androidx.room.Room
import com.gksenon.sleepdiary.data.SleepDatabase
import com.gksenon.sleepdiary.data.SynchronizedRoomDiary
import com.gksenon.sleepdiary.domain.Diary
import com.google.android.gms.wearable.Wearable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DiaryModule {

    @Singleton
    @Provides
    fun provideDiary(@ApplicationContext context: Context): Diary {
        val database = Room.databaseBuilder(context, SleepDatabase::class.java, "diary").build()
        val sleepDao = database.sleepDao()
        val dataClient = Wearable.getDataClient(context)
        return SynchronizedRoomDiary(sleepDao = sleepDao, dataClient = dataClient, coroutineScope = MainScope())
    }
}