package com.cau.capstone.beableto.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.activity.MainActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

public class LocationService : Service(){

    private val TAG = "LocationService"
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
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val text = "We are staking you"
        val builder = Notification.Builder(this, Channel_ID)
            .setContentText(text)
            .setContentTitle("Gps Tracker")
            //.setOngoing(true)
            //.setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker(text)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "번쩍", pendingIntent)

            builder.setChannelId(Channel_ID) // Channel ID
        return builder.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTracking();
        return START_STICKY
    }

    private fun startTracking() {
        val locationRequest = LocationRequest.create()
        locationRequest.setInterval(100*1000)
        locationRequest.setFastestInterval(100*1000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        try{
            LocationServices.getFusedLocationProviderClient(applicationContext)
                .requestLocationUpdates(locationRequest, object :
                    LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        onLocationChanged(locationResult!!.lastLocation)
                    }
                }, Looper.myLooper())
        } catch(e: Exception){
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    fun onLocationChanged(location: Location?) {
        if (location != null) {
            Log.e(
                TAG,
                "position: " + location.latitude + ", " + location.longitude + " accuracy: " + location.accuracy
            )
            val localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext)
            val intent = Intent("intent_action")
            intent.putExtra("latitude", location.latitude)
            intent.putExtra("longitude", location.longitude)
            localBroadcastManager.sendBroadcast(intent)
            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            if (location.accuracy < 500.0f) {
                //stopLocationUpdates();

                //TODO: send locations
            }
        }
    }



}