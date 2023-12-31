package com.gksenon.sleepdiary.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gksenon.sleepdiary.domain.Tracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class StopSleepTrackerBroadcastReceiver: BroadcastReceiver() {

    @Inject
    lateinit var tracker: Tracker

    @Inject
    lateinit var notificationManager: SleepTrackerNotificationManager

    override fun onReceive(p0: Context?, p1: Intent?) {
        runBlocking { tracker.stop() }
        notificationManager.hideNotification()
    }
}