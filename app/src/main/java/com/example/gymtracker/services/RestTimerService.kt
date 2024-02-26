package com.example.gymtracker.services

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.CountDownTimer
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.gymtracker.MainActivity
import com.example.gymtracker.R
import com.example.gymtracker.data.repository.TimerServiceRepository
import com.example.gymtracker.ui.utils.toTimer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class RestTimerService : LifecycleService() {

    //Inject repository
    @Inject
    lateinit var timerServiceRepository: TimerServiceRepository

    private lateinit var timer: CountDownTimer
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: Notification.Builder

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = Notification.Builder(this, REST_TIMER_CHANNEL_ID)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val timerInterval = intent?.extras?.getLong(TIMER_INTERVAL)
        val timerDuration = intent?.extras?.getLong(TIMER_DURATION)
        val workoutId = intent?.extras?.getLong(WORKOUT_ID)

        if (intent?.action == STOP_ACTION) {
            timer.cancel()
            // Update timer state to stop
            timerServiceRepository.onTimerStateChange(false)
            //Change timer value to 0
            timerServiceRepository.onTimerTick(0)
            stopSelf()
        } else {

            timer = object : CountDownTimer(timerDuration!!, timerInterval!!) {

                override fun onTick(p0: Long) {
                    timerServiceRepository.onTimerTick(p0)
                    updateNotification(p0)
                }

                override fun onFinish() {
                    // stop timer
                    timerServiceRepository.onTimerStateChange(false)
                    timerServiceRepository.onTimerTick(0)
                    stopSelf()
                }
            }

            startForeground(workoutId!!)
            // Timer started
            timerServiceRepository.onTimerStateChange(true)
            timer.start()

        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        timer.cancel()
        timerServiceRepository.onTimerStateChange(false)
        timerServiceRepository.onTimerTick(0)
        super.onDestroy()
    }

    private fun updateNotification(timer: Long) {

        val notification = notificationBuilder
            .setSmallIcon(R.drawable.outline_timer_24)
            .setContentTitle(getString(R.string.rest_timer_notification_title))
            .setContentText(timer.toTimer())
            .setOnlyAlertOnce(true)
            .build()

        notificationManager.notify(1, notification)

    }


    private fun startForeground(workoutId: Long) {

        /* Pending intent to cancel the service and close the timer */
        val cancelServiceIntent = Intent(this, RestTimerService::class.java).apply {
            action = STOP_ACTION
        }
        val cancelServicePendingIntent =
            PendingIntent.getService(this, 0, cancelServiceIntent, PendingIntent.FLAG_IMMUTABLE)

        /* This deep link intent will navigate directly to the workout diary page where the rest timer
        * was triggered.*/
        val deepLinkIntent = Intent(Intent.ACTION_VIEW, "https://www.gymtracker.com/$workoutId".toUri(), this, MainActivity::class.java)

        val deepLinkPendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }


        val icon = Icon.createWithResource(this, R.drawable.round_clear_24)

        val action: Notification.Action = Notification.Action.Builder(icon, getString(R.string.dismiss_sr), cancelServicePendingIntent).build()

        try {

            val notification = notificationBuilder
                .setSmallIcon(R.drawable.outline_timer_24)
                .setContentTitle(getString(R.string.rest_timer_notification_title))
                .setContentText("Timer")
                .setOnlyAlertOnce(true)
                .setContentIntent(deepLinkPendingIntent)
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
        const val WORKOUT_ID = "workout_id"
    }
}