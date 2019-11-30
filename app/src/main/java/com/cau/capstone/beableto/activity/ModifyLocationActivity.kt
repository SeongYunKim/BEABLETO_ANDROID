package com.cau.capstone.beableto.activity

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.Adapter.CustomInfoWindowAdapter
import com.cau.capstone.beableto.R
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_modify_location.*
import java.util.*

class ModifyLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private var latitude: Float? = null
    private var longitude: Float? = null
    private var address: String? = null
    private val LOCATION_SEARCH = 54809

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_location)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_modify) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btn_modify_next.isSelected = true

        btn_modify_location__cancel.setOnClickListener {
            finish()
        }

        btn_modify_next.setOnClickListener {
            if (et_modify_location_name.text.isNotEmpty()) {
                val intent = Intent()
                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                intent.putExtra("address", address)
                intent.putExtra("location_name", et_modify_location_name.text.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this@ModifyLocationActivity, "위치명을 입력해 주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        et_modify_place_search.setOnClickListener {
            val intent = Intent(this, AutoSuggestActivity::class.java)
            intent.putExtra("type", "start")
            startActivityForResult(intent, LOCATION_SEARCH)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val CUR: LatLng
        if (intent.hasExtra("use_photo")) {
            if (intent.getBooleanExtra("use_photo", false)) {
                if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
                    CUR = LatLng(
                        intent.getFloatExtra("latitude", 0.0F).toDouble(),
                        intent.getFloatExtra("longitude", 0.0F).toDouble()
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CUR, 18.0F))
                }
            } else {
                val INIT = LatLng(37.50352, 126.95706)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
                mMap.isMyLocationEnabled = true
                getDeviceLocation()
            }
        }
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
            getMarkerInfo(getMapBound())
        }

        mMap.setOnCameraMoveStartedListener {
            et_modify_location_name.text = null
            btn_modify_next.isSelected = false
            tv_modify_address.text = "위치 이동중 입니다."
        }

        mMap.setOnCameraMoveListener {
            et_modify_location_name.text = null
            btn_modify_next.isSelected = false
            tv_modify_address.text = "위치 이동중 입니다."
        }

        mMap.setOnCameraIdleListener {
            btn_modify_next.isSelected = true
            val center = mMap.cameraPosition.target
            latitude = center.latitude.toFloat()
            longitude = center.longitude.toFloat()
            address = getAddress(latitude!!, longitude!!)
            //Log.d("Center", center.latitude.toFloat().toString() + " " + center.longitude.toFloat())
            tv_modify_address.text =
                "[주소] " + address
            //mMap.clear()
            getMarkerInfo(getMapBound())
        }

        mMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            marker.position.latitude,
                            marker.position.longitude
                        ), 18.0F
                    )
                )
                et_modify_location_name.setText(marker.title)
                return true
            }
        })

        et_modify_place_search.setOnEditorActionListener(
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

    private fun geoLocate() {
        val searchString = et_modify_place_search.text.toString()
        val geocoder = Geocoder(this)
        var list: List<Address> = ArrayList()
        try {
            list = geocoder.getFromLocationName(searchString, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (list.isNotEmpty()) {
            val info = list[0]
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        info.latitude,
                        info.longitude
                    ), 18.0F
                )
            )
            address = getAddress(info.latitude.toFloat(), info.latitude.toFloat())
            tv_modify_address.text =
                "[주소] " + address
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
                SharedPreferenceController.getAuthorization(this), requestMarkerOnMap
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
                true -> snippet += "있음\n화장실: "
                false -> snippet += "없음\n화장실: "
            }
            when (toilet) {
                true -> snippet += "있음"
                false -> snippet += "없음"
            }
            markerOptions.snippet(snippet)
            mMap.addMarker(markerOptions)
        }
    }

    fun getAddress(latitude: Float, longitude: Float): String {
        val geoCoder = Geocoder(this, Locale.KOREAN)
        var photoAddress = ""
        try {
            val addresses: List<Address> =
                geoCoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
            if (addresses.isNotEmpty()) {
                val mAddress: Address = addresses[0]
                var buf: String?
                var iterator = 0
                buf = mAddress.getAddressLine(iterator)
                while (buf != null) {
                    if (iterator == 0)
                        photoAddress += buf
                    iterator += 1
                    buf = mAddress.getAddressLine(iterator)
                }
            }
            return photoAddress
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return photoAddress
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SEARCH && resultCode == Activity.RESULT_OK && data != null) {
            if (data.hasExtra("research_latitude")) {
                mMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            data.getFloatExtra("research_latitude", 0.0f).toDouble(),
                            data.getFloatExtra("research_longitude", 0.0f).toDouble()
                        ),
                        18.0F
                    )
                )
            }
        }
    }
}