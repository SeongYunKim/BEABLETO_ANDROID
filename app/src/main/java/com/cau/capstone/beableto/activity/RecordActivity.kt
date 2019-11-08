package com.cau.capstone.beableto.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.repository.SharedPreferenceController
import com.cau.capstone.beableto.service.LocationService
import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.activity_record.switch_realtime_gps

class RecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        switch_realtime_gps.isChecked = SharedPreferenceController.getRealTimeGps(this@RecordActivity)
        record_1h_view.isSelected = true

        record_1h.setOnClickListener {
            record_1h_view.isSelected = true
            record_3h_view.isSelected = false
            record_6h_view.isSelected = false
            record_12h_view.isSelected = false
            record_24h_view.isSelected = false
        }

        record_3h.setOnClickListener {
            record_1h_view.isSelected = false
            record_3h_view.isSelected = true
            record_6h_view.isSelected = false
            record_12h_view.isSelected = false
            record_24h_view.isSelected = false
        }

        record_6h.setOnClickListener {
            record_1h_view.isSelected = false
            record_3h_view.isSelected = false
            record_6h_view.isSelected = true
            record_12h_view.isSelected = false
            record_24h_view.isSelected = false
        }

        record_12h.setOnClickListener {
            record_1h_view.isSelected = false
            record_3h_view.isSelected = false
            record_6h_view.isSelected = false
            record_12h_view.isSelected = true
            record_24h_view.isSelected = false
        }

        record_24h.setOnClickListener {
            record_1h_view.isSelected = false
            record_3h_view.isSelected = false
            record_6h_view.isSelected = false
            record_12h_view.isSelected = false
            record_24h_view.isSelected = true
        }

        btn_record_cancel.setOnClickListener {
            finish()
        }

        switch_realtime_gps.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                startTracking()
                LocalBroadcastManager.getInstance(this)
                    .registerReceiver(mMessageReceiver, IntentFilter("intent_action"))
            } else {
                stopService(Intent(this, LocationService::class.java))
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
            }
            SharedPreferenceController.setRealTimeGps(this@RecordActivity, switch_realtime_gps.isChecked)
        }
    }

    private fun startTracking() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    110
                )
            }
        }

        startForegroundService(Intent(this, LocationService::class.java))
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            Toast.makeText(applicationContext, "$latitude $longitude", Toast.LENGTH_SHORT).show()
        }
    }
}