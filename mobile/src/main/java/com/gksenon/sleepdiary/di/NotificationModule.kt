package com.gksenon.sleepdiary.di

import android.content.Context
import com.gksenon.sleepdiary.notifications.SleepTrackerNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class NotificationModule {

    @Provides
    fun provideSleepTrackerNotificationManager(@ApplicationContext context: Context) =
        SleepTrackerNotificationManager(context)
}