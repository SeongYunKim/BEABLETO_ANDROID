package com.cau.capstone.beableto

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
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cau.capstone.beableto.Adapter.CustomInfoWindowAdapter
import com.cau.capstone.beableto.Adapter.PlaceAutoSuggestAdapter
import com.cau.capstone.beableto.activity.RegisterLocationActivity
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
    private val PERMISSION_CODE = 3456
    private lateinit var mMap: GoogleMap
    private var mLocationPermissionGranted = false
    private lateinit var lastLocation: Location
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //val placeAutoSuggestAdapter = PlaceAutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line)
        //main_autocomplete.setAdapter(placeAutoSuggestAdapter)

        getLocationPermission()

        temp_logout.setOnClickListener {
            SharedPreferenceController.logout(this@MainActivity)
            val intent = Intent(this, InitActivity::class.java)
            startActivity(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity()
            }
        }

        main_register_location.setOnClickListener {
            val intent = Intent(this, RegisterLocationActivity::class.java)
            startActivityForResult(intent, GET_REGISTER_LOCATION)
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
            mMap.addMarker(markerOptions)
        }
    }
}