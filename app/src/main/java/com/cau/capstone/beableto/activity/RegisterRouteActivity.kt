package com.cau.capstone.beableto.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.KeyEvent
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
import kotlinx.android.synthetic.main.activity_register_route.*

class RegisterRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private val REGISTER_ROAD = 1234
    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private var latitude: Float? = null
    private var longitude: Float? = null
    private var slope: Int? = null
    private var next_available: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_route)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_register_route1) as SupportMapFragment
        mapFragment.getMapAsync(this)

        layout_road_none.setOnClickListener {
            tv_road_none.isSelected = true
            view_road_none.isSelected = true
            layout_road_none.isSelected = true
            tv_road_gentle.isSelected = false
            view_road_gentle.isSelected = false
            layout_road_gentle.isSelected = false
            tv_road_sharp.isSelected = false
            view_road_sharp.isSelected = false
            layout_road_sharp.isSelected = false
        }

        layout_road_gentle.setOnClickListener {
            tv_road_none.isSelected = false
            view_road_none.isSelected = false
            layout_road_none.isSelected = false
            tv_road_gentle.isSelected = true
            view_road_gentle.isSelected = true
            layout_road_gentle.isSelected = true
            tv_road_sharp.isSelected = false
            view_road_sharp.isSelected = false
            layout_road_sharp.isSelected = false
        }

        layout_road_sharp.setOnClickListener {
            tv_road_none.isSelected = false
            view_road_none.isSelected = false
            layout_road_none.isSelected = false
            tv_road_gentle.isSelected = false
            view_road_gentle.isSelected = false
            layout_road_gentle.isSelected = false
            tv_road_sharp.isSelected = true
            view_road_sharp.isSelected = true
            layout_road_sharp.isSelected = true
        }

        btn_register_route_previous1.setOnClickListener {
            finish()
        }

        btn_register_route_next1.setOnClickListener {
            if (next_available) {
                if (layout_road_none.isSelected) slope = 0
                else if (layout_road_gentle.isSelected) slope = 1
                else if (layout_road_sharp.isSelected) slope = 2
                val intent = Intent(this, RegisterRouteActivity2::class.java)
                intent.putExtra("slope", slope)
                intent.putExtra("start_latitude", latitude)
                intent.putExtra("start_longitude", longitude)
                startActivityForResult(intent, REGISTER_ROAD)
            } else {
                Toast.makeText(this@RegisterRouteActivity, "도로의 시작점을 설정해 주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_register_route_setting1.setOnClickListener {
            if (layout_road_none.isSelected || layout_road_gentle.isSelected || layout_road_sharp.isSelected) {
                val center = mMap.cameraPosition.target
                latitude = center.latitude.toFloat()
                longitude = center.longitude.toFloat()
                next_available = true
                btn_register_route_next1.setTextColor(Color.parseColor("#000000"))
                register_route_info1.text = "다음 버튼을 누르세요"
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(LatLng(latitude!!.toDouble(), longitude!!.toDouble())).title("시작점"))
            } else {
                Toast.makeText(this@RegisterRouteActivity, "도로 경사를 선택해 주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REGISTER_ROAD && resultCode == Activity.RESULT_OK && data != null) {
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

        }

        mMap.setOnCameraMoveStartedListener {
            btn_register_route_setting1.isSelected = false
        }

        mMap.setOnCameraMoveListener {
            btn_register_route_setting1.isSelected = false
        }

        mMap.setOnCameraIdleListener {
            btn_register_route_setting1.isSelected = true
            /*
            next_available = false
            btn_register_route_next1.setTextColor(Color.parseColor("#909090"))
            register_route_info1.text = "지도를 움직여 도로의 시작점을 설정하세요"
            */
        }

        et_route_search1.setOnEditorActionListener(
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
        val searchString = et_route_search1.text.toString()
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