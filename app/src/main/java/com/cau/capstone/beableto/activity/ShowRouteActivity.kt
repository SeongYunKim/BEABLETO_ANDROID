package com.cau.capstone.beableto.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.RequestRoute
import com.cau.capstone.beableto.repository.SharedPreferenceController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_show_route.*

class ShowRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var lastLocation: Location? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private var latitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var longitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var bus_latitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var bus_longitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var train_latitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var train_longitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var slope_list: MutableList<MutableList<Int>> = ArrayList()

    private var latitude: Float? = null
    private var longitude: Float? = null
    private var cur_latitude: Float? = null
    private var cur_longitude: Float? = null

    private val PERMISSION_CODE = 3456
    private var mLocationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_route)

        latitude = intent.getFloatExtra("latitude", 0.0F)
        longitude = intent.getFloatExtra("longitude", 0.0F)

        val type = intent.getStringExtra("type")
        val location_name = intent.getStringExtra("name")

        for (x in 0 until 5) {
            latitude_list.add(mutableListOf(0.0F))
            longitude_list.add(mutableListOf(0.0F))
            slope_list.add(mutableListOf(0))
            bus_latitude_list.add(mutableListOf(0.0F))
            bus_longitude_list.add(mutableListOf(0.0F))
            train_latitude_list.add(mutableListOf(0.0F))
            train_longitude_list.add(mutableListOf(0.0F))
        }

        if (type == "start") {

        } else if (type == "end") {
            et_search_end.setText(location_name)
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_show_route) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val INIT = LatLng(37.50352, 126.95706)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
        mMap.isMyLocationEnabled = true
        //getDeviceLocation()
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
        /*
        if (mLocationPermissionGranted) {
            getDeviceLocation()
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mMap.isMyLocationEnabled = true
        }
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationListener = MyLocationListener()
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000000,
                1000000.0F,
                locationListener
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        */
        val requestRoute =
            RequestRoute(
                SharedPreferenceController.getCurrentLocation(this).latitude.toFloat(),
                SharedPreferenceController.getCurrentLocation(this).longitude.toFloat(),
                //current_location.latitude.toFloat(),
                //current_location.longitude.toFloat(),
                latitude!!,
                longitude!!
            )
        NetworkCore.getNetworkCore<BEABLETOAPI>()
            .requestAdmin(
                SharedPreferenceController.getAuthorization(this), requestRoute
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d("SSibal", response.toString())
                for (ps in response.paths.indices) {
                    for (p in response.paths[ps].path) {
                        if (p.type == "walk") {
                            for (ws in p.walk_seq) {
                                latitude_list[ps].add(ws.start_x!!)
                                longitude_list[ps].add(ws.start_y!!)
                                latitude_list[ps].add(ws.end_x!!)
                                longitude_list[ps].add(ws.end_y!!)
                                slope_list[ps].add(ws.slope)
                            }
                        } else if (p.type == "bus") {
                            bus_latitude_list[ps].add(p.bus_start_x!!)
                            bus_longitude_list[ps].add(p.bus_start_y!!)
                            bus_latitude_list[ps].add(p.bus_end_x!!)
                            bus_longitude_list[ps].add(p.bus_end_y!!)
                        } else if (p.type == "train") {
                            train_latitude_list[ps].add(p.train_start_x!!)
                            train_longitude_list[ps].add(p.train_start_y!!)
                            train_latitude_list[ps].add(p.train_end_x!!)
                            train_longitude_list[ps].add(p.train_end_y!!)
                        }
                    }
                }
                drawPolyLine(0)
            }, {
                Log.d("SSibal_Error", Log.getStackTraceString(it))
            })
        Log.d("SSibal", latitude_list[3].toString())
        while (slope_list[0].size > 1) {
            Handler().postDelayed({}, 100)
        }
        mMap.clear()
        drawPolyLine(0)
    }

    private fun drawPolyLine(num: Int) {
        var before_latlng: LatLng? = null
        var current_latlng: LatLng
        Log.d("latitude_list", latitude_list[num].size.toString())
        Log.d("latitude_list/slope", slope_list[num].size.toString())
        for (i in latitude_list[num].indices) {
            if (i != 0) {
                current_latlng =
                    LatLng(latitude_list[num][i].toDouble(), longitude_list[num][i].toDouble())
                if (i % 2 == 0) {
                    if (slope_list[num][i / 2] == 0) {
                        mMap.addPolyline(
                            PolylineOptions().add(before_latlng, current_latlng).width(10.0F).color(
                                Color.YELLOW
                            )
                        )
                    } else if (slope_list[num][i / 2] == 1) {
                        mMap.addPolyline(
                            PolylineOptions().add(before_latlng, current_latlng).width(10.0F).color(
                                Color.RED
                            )
                        )
                    } else if (slope_list[num][i / 2] == 2) {
                        mMap.addPolyline(
                            PolylineOptions().add(before_latlng, current_latlng).width(10.0F).color(
                                Color.BLACK
                            )
                        )
                    } else if (slope_list[num][i / 2] == 3) {
                        mMap.addPolyline(
                            PolylineOptions().add(before_latlng, current_latlng).width(10.0F).color(
                                Color.GREEN
                            )
                        )
                    }
                }
                before_latlng = current_latlng
            }
        }
        for (i in bus_latitude_list[num].indices) {
            if (i != 0) {
                current_latlng =
                    LatLng(
                        bus_latitude_list[num][i].toDouble(),
                        bus_longitude_list[num][i].toDouble()
                    )
                if (i % 2 == 0) {
                    mMap.addPolyline(
                        PolylineOptions().add(before_latlng, current_latlng).width(10.0F).color(
                            Color.BLUE
                        )
                    )
                }
                before_latlng = current_latlng
            }
        }
        for (i in train_latitude_list[num].indices) {
            if (i != 0) {
                current_latlng =
                    LatLng(
                        train_latitude_list[num][i].toDouble(),
                        train_longitude_list[num][i].toDouble()
                    )
                if (i % 2 == 0) {
                    mMap.addPolyline(
                        PolylineOptions().add(before_latlng, current_latlng).width(10.0F).color(
                            Color.CYAN
                        )
                    )
                }
                before_latlng = current_latlng
            }
        }
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location?) {
            cur_latitude = location!!.latitude.toFloat()
            cur_longitude = location.longitude.toFloat()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderDisabled(provider: String?) {}
        override fun onProviderEnabled(provider: String?) {}
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationPermissionGranted = true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    PERMISSION_CODE
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_CODE
            )
        }
    }
}