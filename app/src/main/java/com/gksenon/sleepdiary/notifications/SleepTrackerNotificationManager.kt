package com.gksenon.sleepdiary.notifications

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import com.gksenon.sleepdiary.MainActivity
import com.gksenon.sleepdiary.R
import java.util.Date
import javax.inject.Inject

private const val CHANNEL_ID = "sleep_tracker"
private const val NOTIFICATION_ID = 0

class SleepTrackerNotificationManager @Inject constructor(private val context: Context) {

    fun showNotification(start: Date) {
        createNotificationChannel()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(R.drawable.ic_sleep)
            .setCustomContentView(buildNotificationLayout(start))
            .addAction(R.drawable.ic_stop, context.getString(R.string.stop_tracking), buildStopTrackerIntent())
            .setContentIntent(buildContentIntent())
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED)
                notify(NOTIFICATION_ID, builder.build())
        }
    }

    fun hideNotification() {
        with(NotificationManagerCompat.from(context)) {
            if (checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED)
                cancel(NOTIFICATION_ID)
        }
    }

    private fun buildNotificationLayout(start: Date) =
        RemoteViews(context.packageName, R.layout.notification_tracker).apply {
            val bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime()
            val chronometerBase = start.time - bootTime
            setChronometer(R.id.tracker_chronometer, chronometerBase, null, true)
        }

    private fun buildContentIntent(): PendingIntent? {
        val contentIntent = Intent(
            Intent.ACTION_VIEW,
            "https://com.gksenon.sleepdiary/tracker".toUri(),
            context,
            MainActivity::class.java
        ).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) }

        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(contentIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }

    private fun buildStopTrackerIntent(): PendingIntent? {
        val stopIntent = Intent(context, StopSleepTrackerBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.tracker_notifications_channel_name)
            val descriptionText = context.getString(R.string.tracker_notifications_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}