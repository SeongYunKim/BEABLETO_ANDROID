package com.cau.capstone.beableto.activity

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cau.capstone.beableto.Adapter.LocationSelectAdapter
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.PlaceAPI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_select_location.*

class LocationSelectActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val placeAPI = PlaceAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_select_location) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val INIT = LatLng(37.50352, 126.95706)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
        mapLoadedCallBack()
    }

    private fun mapLoadedCallBack() {
        mMap.setOnMapLoadedCallback {
            val list = placeAPI.search_place_list(intent.getStringExtra("input"))
            while (list.isEmpty()) {
                Handler().postDelayed({}, 100)
            }
            val adapter = LocationSelectAdapter(list)
            recyclerview_select_location.layoutManager = LinearLayoutManager(this)
            recyclerview_select_location.adapter = adapter
            recyclerview_select_location.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                )
            )
            val first_location = LatLng(list[0].latitude.toDouble(), list[0].longitude.toDouble())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first_location, 18.0F))
            mMap.addMarker(MarkerOptions().position(first_location).title(list[0].name))
        }
    }
}