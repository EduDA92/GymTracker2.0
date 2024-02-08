package com.example.gymtracker.ui.workoutDiary

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.gymtracker.R

class RestTimerService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> startForeground()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground() {

        try {

            val notification = Notification.Builder(this, REST_TIMER_CHANNEL_ID)
                .setSmallIcon(R.drawable.outline_timer_24)
                .setContentTitle(getString(R.string.rest_timer_notification_title))
                .setContentText("Testing")
                .build()

            Log.e("RESTTIMERSERVICE", "SERVICE STARTED")

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

    enum class Actions { START, STOP }

    companion object {
        const val REST_TIMER_CHANNEL_ID = "Rest_timer_channel"
    }
}