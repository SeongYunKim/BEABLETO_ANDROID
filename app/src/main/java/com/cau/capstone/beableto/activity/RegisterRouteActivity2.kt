package com.cau.capstone.beableto.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_register_route2.*

class RegisterRouteActivity2 : AppCompatActivity(), OnMapReadyCallback {
    private val REGISTER_ROAD2 = 5678
    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private var start_latitude: Float? = null
    private var start_longitude: Float? = null
    private var start_latlng: LatLng? = null
    private var end_latitude: Float? = null
    private var end_longitude: Float? = null
    private var end_latlng: LatLng? = null
    private var slope: Int? = null
    private var next_available: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_route2)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_register_route2) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btn_register_route_previous2.setOnClickListener {
            finish()
        }

        btn_register_route_next2.setOnClickListener {
            if (next_available) {
                val intent = Intent(this, RegisterRouteActivity3::class.java)
                intent.putExtra("slope", slope)
                intent.putExtra("start_latitude", start_latitude)
                intent.putExtra("start_longitude", start_longitude)
                intent.putExtra("end_latitude", end_latitude)
                intent.putExtra("end_longitude", end_longitude)
                startActivityForResult(intent, REGISTER_ROAD2)
            } else {
                Toast.makeText(this@RegisterRouteActivity2, "도로의 도착점을 설정해 주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_register_route_setting2.setOnClickListener {
            val center = mMap.cameraPosition.target
            end_latitude = center.latitude.toFloat()
            end_longitude = center.longitude.toFloat()
            next_available = true
            btn_register_route_next2.setTextColor(Color.parseColor("#000000"))
            register_route_info2.text = "다음 버튼을 누르세요"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REGISTER_ROAD2 && resultCode == Activity.RESULT_OK && data != null) {
            val intent = Intent()
            if (data.hasExtra("start_latitude") && data.hasExtra("start_longitude") && data.hasExtra(
                    "end_latitude"
                ) && data.hasExtra("end_longitude")
            ) {
                intent.putExtra("start_latitude", data.getFloatExtra("start_latitude", 0.0F))
                intent.putExtra("start_longitude", data.getFloatExtra("start_longitude", 0.0F))
                intent.putExtra("end_latitude", data.getFloatExtra("end_latitude", 0.0F))
                intent.putExtra("end_longitude", data.getFloatExtra("end_longitude", 0.0F))
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        } else {

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (intent.hasExtra("start_latitude") && intent.hasExtra("start_longitude") && intent.hasExtra(
                "slope"
            )
        ) {
            start_latitude = intent.getFloatExtra("start_latitude", 0.0F)
            start_longitude = intent.getFloatExtra("start_longitude", 0.0F)
            slope = intent.getIntExtra("slope", 0)
            start_latlng = LatLng(
                start_latitude!!.toDouble(),
                start_longitude!!.toDouble()
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start_latlng, 18.0F))
        }
        mMap.isMyLocationEnabled = true
        mMap.addMarker(MarkerOptions().position(start_latlng!!).title("시작점"))
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

        }

        mMap.setOnCameraMoveStartedListener {
            btn_register_route_setting2.isSelected = false
            center_marker2.visibility = View.VISIBLE
        }

        mMap.setOnCameraMoveListener {
            btn_register_route_setting2.isSelected = false
            center_marker2.visibility = View.VISIBLE
        }

        mMap.setOnCameraIdleListener {
            btn_register_route_setting2.isSelected = true
            next_available = false
            val center = mMap.cameraPosition.target
            end_latlng =
                LatLng(center.latitude.toFloat().toDouble(), center.longitude.toFloat().toDouble())
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(start_latlng!!).title("시작점"))
            mMap.addMarker(MarkerOptions().position(end_latlng!!).title("도착점"))
            mMap.addPolyline(
                PolylineOptions().add(start_latlng, end_latlng).width(10.0F).color(
                    Color.RED
                )
            )
            center_marker2.visibility = View.INVISIBLE
            btn_register_route_next2.setTextColor(Color.parseColor("#909090"))
            register_route_info2.text = "지도를 움직여 도로의 도착점을 설정하세요"
        }

        et_route_search2.setOnEditorActionListener(
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
        val searchString = et_route_search2.text.toString()
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
        }
    }
}