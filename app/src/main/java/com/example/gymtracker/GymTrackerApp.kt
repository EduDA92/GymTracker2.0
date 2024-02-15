package com.example.gymtracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.gymtracker.ui.workoutDiary.services.RestTimerService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GymTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        /* Create a notification channel for the rest timer */
        createRestTimerNotificationChannel()

    }

    private fun createRestTimerNotificationChannel() {
        val name = getString(R.string.rest_timer_channel_name)
        val descriptionText = getString(R.string.rest_timer_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channelId = RestTimerService.REST_TIMER_CHANNEL_ID

        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        // Register the channel
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}