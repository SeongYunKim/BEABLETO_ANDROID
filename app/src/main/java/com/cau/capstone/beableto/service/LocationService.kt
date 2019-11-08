package com.cau.capstone.beableto.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.activity.RecordActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationService : Service() {

    private val Channel_ID = "Channel_01"
    private val Notification_ID = 13579

    override fun onCreate() {
        super.onCreate()
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = getString(R.string.app_name)
        val mChannel = NotificationChannel(Channel_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
        mNotificationManager.createNotificationChannel(mChannel)
        startForeground(Notification_ID, getNotification())
    }

    private fun getNotification(): Notification {
        val intent = Intent(this, RecordActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val text = "실시간 위치 제공을 취소하려면 누르세요"
        val builder = NotificationCompat.Builder(this, Channel_ID)
            .setContentText(text)
            .setContentTitle("BEABLETO는 언제나 당신과 함께합니다")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker(text)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "내 활동 기록 보기", pendingIntent)

        builder.setChannelId(Channel_ID)
        return builder.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTracking()
        return START_STICKY
    }

    private fun startTracking() {
        val locationRequest = LocationRequest.create()
        locationRequest.setInterval(1 * 1000)
        locationRequest.setFastestInterval(1 * 1000)
        locationRequest.setPriority(PRIORITY_HIGH_ACCURACY)
        locationRequest.setSmallestDisplacement(20.0F)

        try {
            LocationServices.getFusedLocationProviderClient(applicationContext)
                .requestLocationUpdates(locationRequest, object :
                    LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        onLocationChanged(locationResult!!.lastLocation)
                    }
                }, Looper.myLooper())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    fun onLocationChanged(location: Location?) {
        if (location != null) {
            if (location.accuracy < 500.0f) {
                val localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext)
                val intent = Intent("intent_action")
                intent.putExtra("latitude", location.latitude)
                intent.putExtra("longitude", location.longitude)
                localBroadcastManager.sendBroadcast(intent)
            }
        }
    }
}