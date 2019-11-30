package com.cau.capstone.beableto.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.RequestRealTimeLocation
import com.cau.capstone.beableto.data.RequestRegisterRealTimeLocation
import com.cau.capstone.beableto.data.ResponseRealTimeLocation
import com.cau.capstone.beableto.repository.SharedPreferenceController
import com.cau.capstone.beableto.service.LocationService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.activity_record.switch_realtime_gps
import java.time.LocalDate
import java.time.LocalDateTime

class RecordActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var map_loaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_record) as SupportMapFragment
        mapFragment.getMapAsync(this)

        switch_realtime_gps.isChecked =
            SharedPreferenceController.getRealTimeGps(this@RecordActivity)
        record_1h_view.isSelected = true

        record_1h.setOnClickListener {
            record_1h_view.isSelected = true
            record_3h_view.isSelected = false
            record_6h_view.isSelected = false
            record_12h_view.isSelected = false
            record_24h_view.isSelected = false
            mMap.clear()
            getRealTimeRoute(1)
        }

        record_3h.setOnClickListener {
            record_1h_view.isSelected = false
            record_3h_view.isSelected = true
            record_6h_view.isSelected = false
            record_12h_view.isSelected = false
            record_24h_view.isSelected = false
            mMap.clear()
            getRealTimeRoute(3)
        }

        record_6h.setOnClickListener {
            record_1h_view.isSelected = false
            record_3h_view.isSelected = false
            record_6h_view.isSelected = true
            record_12h_view.isSelected = false
            record_24h_view.isSelected = false
            mMap.clear()
            getRealTimeRoute(6)
        }

        record_12h.setOnClickListener {
            record_1h_view.isSelected = false
            record_3h_view.isSelected = false
            record_6h_view.isSelected = false
            record_12h_view.isSelected = true
            record_24h_view.isSelected = false
            mMap.clear()
            getRealTimeRoute(12)
        }

        record_24h.setOnClickListener {
            record_1h_view.isSelected = false
            record_3h_view.isSelected = false
            record_6h_view.isSelected = false
            record_12h_view.isSelected = false
            record_24h_view.isSelected = true
            mMap.clear()
            getRealTimeRoute(24)
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
            SharedPreferenceController.setRealTimeGps(
                this@RecordActivity,
                switch_realtime_gps.isChecked
            )
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
            val currentDate = LocalDateTime.now()
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            /*
            if (map_loaded) {
                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            latitude!!.toDouble(),
                            longitude!!.toDouble()
                        )
                    )
                )
            }
            */

            val requestRegisterRealTimeLocation = RequestRegisterRealTimeLocation(
                latitude.toFloat(),
                longitude.toFloat()
            )

            NetworkCore.getNetworkCore<BEABLETOAPI>()
                .requestRegisterRealTimeLocation(
                    SharedPreferenceController.getAuthorization(this@RecordActivity),
                    requestRegisterRealTimeLocation
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("RealTimeLocation_Register_Success", "Success")
                }, {
                    Log.d("RealTimeLocation_Register_Error", Log.getStackTraceString(it))
                })
            Toast.makeText(applicationContext, "$latitude $longitude", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val INIT = LatLng(37.50352, 126.95706)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
        mMap.isMyLocationEnabled = true
        getDeviceLocation()
        mapLoadedCallBack()
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            mFusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0F))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun mapLoadedCallBack() {
        mMap.setOnMapLoadedCallback {
            map_loaded = true
            getRealTimeRoute(1)
        }
    }

    private fun getRealTimeRoute(time: Int){
        NetworkCore.getNetworkCore<BEABLETOAPI>()
            .requestRealTimeLocation(
                SharedPreferenceController.getAuthorization(this@RecordActivity),
                RequestRealTimeLocation(time)
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response->
                Log.d("XXXXX", response.toString())
                if(response.locations != null)
                    drawPolyLine(response)
            }, {
                Log.d("RealTimeLocation_Register_Error", Log.getStackTraceString(it))
            })
    }

    private fun drawPolyLine(response: ResponseRealTimeLocation) {
        var before_latlng: LatLng? = null
        var current_latlng: LatLng
        for (i in response.locations.indices) {
            current_latlng =
                LatLng(response.locations[i].x.toDouble(), response.locations[i].y.toDouble())
            if (i != 0) {
                mMap.addPolyline(
                    PolylineOptions().add(before_latlng, current_latlng).width(10.0F).color(
                        Color.RED
                    )
                )
            }
            before_latlng = current_latlng
        }
    }
}