package com.cau.capstone.beableto

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.cau.capstone.beableto.Adapter.CustomInfoWindowAdapter
import com.cau.capstone.beableto.activity.RegisterLocationActivity
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.RequestMarkerOnMap
import com.cau.capstone.beableto.data.ResponseMarkerOnMap
import com.cau.capstone.beableto.repository.SharedPreferenceController
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
import kotlinx.android.synthetic.main.activity_modify_location.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val GET_REGISTER_LOCATION = 9012
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
        var latitude : Float?
        var longitude : Float?
        if (requestCode == GET_REGISTER_LOCATION && resultCode == Activity.RESULT_OK && data != null) {
            if (data.hasExtra("latitude") && data.hasExtra("longitude")) {
                latitude = data.getFloatExtra("latitude", 0.0F)
                longitude = data.getFloatExtra("longitude", 0.0F)
                Log.d("MainData", latitude.toString() + " " + longitude.toString())
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude.toDouble(), longitude.toDouble()), 18.0F))
            }
        } else {
            //위치 등록하다가 말았을 경우
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val INIT = LatLng(37.50352, 126.95706)
        //mMap.addMarker(
        //    MarkerOptions().position(INIT).title("중앙대학교 208관").snippet("진입로 경사: 완만\n엘리베이터: 있음\n장애인 화장실: 있음").icon(
        //        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        //    )
        //)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
        mapLoadedCallBack()
    }

    private fun mapLoadedCallBack() {
        mMap.setOnMapLoadedCallback {
            getMarkerInfo(getMapBound())
        }

        mMap.setOnCameraIdleListener {
            //mMap.clear()
            getMarkerInfo(getMapBound())
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
