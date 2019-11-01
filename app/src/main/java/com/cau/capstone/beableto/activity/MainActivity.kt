package com.cau.capstone.beableto.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cau.capstone.beableto.Adapter.CustomInfoWindowAdapter
import com.cau.capstone.beableto.R
//import com.cau.capstone.beableto.Adapter.PlaceAutoSuggestAdapter
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.RequestMarkerOnMap
import com.cau.capstone.beableto.data.ResponseMarkerOnMap
import com.cau.capstone.beableto.repository.SharedPreferenceController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val GET_REGISTER_LOCATION = 9012
    private val SETTING = 7890
    private val PERMISSION_CODE = 3456
    private lateinit var mMap: GoogleMap
    private var mLocationPermissionGranted = false
    private lateinit var lastLocation: Location
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var stair_marker: Boolean = true
    private var sharp_marker: Boolean = true
    private var gentle_marker: Boolean = true
    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private var isFabOpen : Boolean = false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_open = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        var setting = SharedPreferenceController.getSetting(this@MainActivity)
        stair_marker = setting.stair
        sharp_marker = setting.sharp
        gentle_marker = setting.gentle

        //val placeAutoSuggestAdapter = PlaceAutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line)
        //main_autocomplete.setAdapter(placeAutoSuggestAdapter)

        getLocationPermission()

        drawer_logout.setOnClickListener {
            SharedPreferenceController.logout(this@MainActivity)
            val intent = Intent(this, InitActivity::class.java)
            startActivity(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity()
            }
        }

        drawer_setting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivityForResult(intent, SETTING)
        }

        ic_menu.setOnClickListener {
            if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.openDrawer(Gravity.LEFT)
            }
        }

        fab.setOnClickListener {
            anim()
        }

        fab1.setOnClickListener {
            anim()
            val intent = Intent(this, RegisterLocationActivity::class.java)
            startActivityForResult(intent, GET_REGISTER_LOCATION)
        }

        fab2.setOnClickListener {
            anim()
            val intent = Intent(this, RegisterRouteActivity::class.java)
            startActivity(intent)
        }

        fab3.setOnClickListener {
            anim()
        }

        /*
        temp_marker.setOnClickListener {
            getMarkerInfo(getMapBound())
        }
        */
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var latitude: Float?
        var longitude: Float?
        if (requestCode == GET_REGISTER_LOCATION && resultCode == Activity.RESULT_OK && data != null) {
            if (data.hasExtra("latitude") && data.hasExtra("longitude")) {
                latitude = data.getFloatExtra("latitude", 0.0F)
                longitude = data.getFloatExtra("longitude", 0.0F)
                Log.d("MainData", latitude.toString() + " " + longitude.toString())
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            latitude.toDouble(),
                            longitude.toDouble()
                        ), 18.0F
                    )
                )
            }
        } else {
            //위치 등록하다가 말았을 경우
        }
        if (requestCode == SETTING && resultCode == Activity.RESULT_OK) {
            var setting = SharedPreferenceController.getSetting(this@MainActivity)
            stair_marker = setting.stair
            sharp_marker = setting.sharp
            gentle_marker = setting.gentle
            mMap.clear()
            getMarkerInfo(getMapBound())
        } else {
            //설정 하다가 말았을 경우
        }
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val INIT = LatLng(37.50352, 126.95706)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
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
            mapLoadedCallBack()
        }
    }

    private fun mapLoadedCallBack() {
        mMap.setOnMapLoadedCallback {
            getMarkerInfo(getMapBound())
        }

        mMap.setOnCameraIdleListener {
            //mMap.clear()
            getMarkerInfo(getMapBound())
        }


        et_main_place_search.setOnEditorActionListener(
            object : TextView.OnEditorActionListener {
                override fun onEditorAction(
                    v: TextView?,
                    actionId: Int,
                    event: KeyEvent?
                ): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event?.action == KeyEvent.ACTION_DOWN || event?.action == KeyEvent.KEYCODE_ENTER) {
                        geoLocate()
                    }
                    return false
                }
            }
        )
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                    if (location != null) {
                        lastLocation = location
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0F))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun geoLocate() {
        val searchString = et_main_place_search.text.toString()
        val geocoder = Geocoder(this)
        var list: List<Address> = ArrayList()
        try {
            list = geocoder.getFromLocationName(searchString, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (list.isNotEmpty()) {
            val address = list[0]
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        address.latitude,
                        address.longitude
                    ), 18.0F
                )
            )
        }
    }

    private fun getMapBound(): Pair<LatLng, LatLng> {
        val bounds = mMap.projection.visibleRegion.latLngBounds
        val northEast = bounds.northeast
        val southWest = bounds.southwest
        return Pair(northEast, southWest)
    }

    private fun getMarkerInfo(pair: Pair<LatLng, LatLng>) {
        var requestMarkerOnMap = RequestMarkerOnMap(
            pair.first.latitude.toString(),
            pair.first.longitude.toString(),
            pair.second.latitude.toString(),
            pair.second.longitude.toString()
        )

        NetworkCore.getNetworkCore<BEABLETOAPI>()
            .requestMarkerOnMap(
                SharedPreferenceController.getAuthorization(this@MainActivity), requestMarkerOnMap
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d("Marker_Success", response.markers.toString())
                drawMarker(response)
            }, {
                Log.d("Marker_Error", Log.getStackTraceString(it))
            })
    }

    private fun drawMarker(response: ResponseMarkerOnMap) {
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
        var slope: Int
        var elevator: Boolean
        var toilet: Boolean
        for (marker in response.markers) {
            var snippet = "진입로 경사: "
            val location = LatLng(marker.x_axis.toDouble(), marker.y_axis.toDouble())
            val markerOptions = MarkerOptions().position(location).title(marker.location_name)
            slope = marker.slope
            elevator = marker.elevator
            toilet = marker.toilet
            when (slope) {
                0 -> {
                    snippet += "완만\n엘리베이터: "
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                }
                1 -> {
                    snippet += "급함\n엘리베이터: "
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                }
                2 -> {
                    snippet += "계단\n엘리베이터: "
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                }
            }
            when (elevator) {
                true -> snippet += "있음\n장애인 화장실: "
                false -> snippet += "없음\n장애인 화장실: "
            }
            when (toilet) {
                true -> snippet += "있음"
                false -> snippet += "없음"
            }
            markerOptions.snippet(snippet)
            if ((slope == 0 && gentle_marker) || (slope == 1 && sharp_marker) || (slope == 2 && stair_marker)) {
                mMap.addMarker(markerOptions)
            }
        }
    }

    private fun anim() {
        if(isFabOpen) {
            fab1.startAnimation(fab_close)
            fab2.startAnimation(fab_close)
            fab3.startAnimation(fab_close)
            fab1.isClickable = false
            fab2.isClickable = false
            fab3.isClickable = false
            isFabOpen = false
        } else {
            fab1.startAnimation(fab_open)
            fab2.startAnimation(fab_open)
            fab3.startAnimation(fab_open)
            fab1.isClickable = true
            fab2.isClickable = true
            fab3.isClickable = false
            isFabOpen = true
        }
    }
}