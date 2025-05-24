package com.team.score.Utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.team.score.MainActivity
import com.team.score.R


class TrackingService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var elapsedSeconds = 0

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate() {
        super.onCreate()
        startForegroundServiceWithNotification()
        initLocationTracking()
        startTimer()
    }

    private fun startForegroundServiceWithNotification() {
        val channelId = "tracking_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "운동 기록",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("record", true)
            // 🔥 앱이 백그라운드에 있을 경우 기존 task로 복귀 (새로 만들지 않음)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("스코어에 운동 기록 중👣")
            .setContentText("운동 위치와 시간을 계속해서 기록하고 있어요")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        startForeground(1, notification)
    }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun initLocationTracking() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    DistanceUtil.onLocationUpdate(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun startTimer() {
        TimerManager.startedAtMillis = System.currentTimeMillis()
        TimerManager.startedAtIso = TimeUtil.convertMillisToIso(TimerManager.startedAtMillis ?: 0)
        elapsedSeconds = MyApplication.recordTimer

        TimerManager.save(this)

        timerRunnable = object : Runnable {
            override fun run() {
                elapsedSeconds++
                MyApplication.recordTimer = elapsedSeconds
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timerRunnable!!)
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        timerRunnable?.let { handler.removeCallbacks(it) }

        TimerManager.completedAtMillis = System.currentTimeMillis()
        TimerManager.completedAtIso = TimeUtil.convertMillisToIso(TimerManager.completedAtMillis!!)
        TimerManager.isRunning = false
        TimerManager.clear(this)

        super.onDestroy()
    }


    override fun onBind(intent: Intent?): IBinder? = null
}
