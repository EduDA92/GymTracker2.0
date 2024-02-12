package com.example.gymtracker.ui.workoutDiary

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.net.toUri
import com.example.gymtracker.MainActivity
import com.example.gymtracker.R

class RestTimerService : Service() {

    private lateinit var timer: CountDownTimer
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: Notification.Builder
    private lateinit var cancelServiceIntent: Intent
    private lateinit var cancelServicePendingIntent: PendingIntent
    private lateinit var deepLinkIntent: PendingIntent

    val test = Intent()


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = Notification.Builder(this, REST_TIMER_CHANNEL_ID)

        /* Pending intent to cancel the service and close the timer */
        cancelServiceIntent = Intent(this, RestTimerService::class.java).apply {
            action = STOP_ACTION
        }
        cancelServicePendingIntent =
            PendingIntent.getService(this, 0, cancelServiceIntent, PendingIntent.FLAG_IMMUTABLE)


        /* This deep link intent will navigate directly to the workout diary page where the rest timer
        * was triggered, TODO this is a test, still need to pass the workoutId via intent extra and more.. */
        val test = Intent(Intent.ACTION_VIEW, "https://www.gymtracker.com/1".toUri(), this, MainActivity::class.java)

        deepLinkIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(test)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val timerInterval = intent?.extras?.getLong(TIMER_INTERVAL)
        val timerDuration = intent?.extras?.getLong(TIMER_DURATION)



        if (intent?.action == STOP_ACTION) {
            timer.cancel()
            stopSelf()
        } else {

            timer = object : CountDownTimer(timerDuration!!, timerInterval!!) {

                override fun onTick(p0: Long) {
                    updateNotification(p0)
                }

                override fun onFinish() {
                    stopSelf()
                }
            }

            startForeground()
            timer.start()

        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun updateNotification(timer: Long) {

        val notification = notificationBuilder
            .setSmallIcon(R.drawable.outline_timer_24)
            .setContentTitle(getString(R.string.rest_timer_notification_title))
            .setContentText(timer.toString())
            .setOnlyAlertOnce(true)
            .build()

        notificationManager.notify(1, notification)

    }


    private fun startForeground() {

        val icon = Icon.createWithResource(this, R.drawable.round_clear_24)

        val action: Notification.Action = Notification.Action.Builder(icon, getString(R.string.dismiss_sr), cancelServicePendingIntent).build()

        try {

            val notification = notificationBuilder
                .setSmallIcon(R.drawable.outline_timer_24)
                .setContentTitle(getString(R.string.rest_timer_notification_title))
                .setContentText("Timer")
                .setOnlyAlertOnce(true)
                .setContentIntent(deepLinkIntent)
                .addAction(action)
                .build()

            startForeground(1, notification)

        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException
            ) {
                // App not in a valid state to start foreground service
                // (e.g. started from bg)
            }

        }
    }

    companion object {
        const val STOP_ACTION = "Stop_service"
        const val REST_TIMER_CHANNEL_ID = "Rest_timer_channel"
        const val TIMER_DURATION = "Rest_timer_duration"
        const val TIMER_INTERVAL = "Rest_timer_interval"
    }
}