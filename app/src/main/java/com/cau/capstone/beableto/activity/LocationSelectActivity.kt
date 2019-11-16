package com.cau.capstone.beableto.activity

import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cau.capstone.beableto.Adapter.LocationSelectAdapter
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.PlaceAPI
import com.cau.capstone.beableto.data.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_select_location.*
import java.util.ArrayList

class LocationSelectActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val placeAPI = PlaceAPI()
    var list: ArrayList<Location> = ArrayList()
    var item_touch: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_select_location) as SupportMapFragment
        mapFragment.getMapAsync(this)

        recyclerview_select_location.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                item_touch = false
            }
        })

        recyclerview_select_location.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    if (e.action == MotionEvent.ACTION_UP && item_touch) {
                        val position = rv.getChildAdapterPosition(child)
                        recyclerview_select_location.visibility = View.GONE
                        recyclerview_select_location.visibility = View.VISIBLE
                        val selected_location = LatLng(
                            list[position].latitude.toDouble(),
                            list[position].longitude.toDouble()
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selected_location, 18.0F))
                    } else if (e.action == MotionEvent.ACTION_DOWN) {
                        item_touch = true
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val INIT = LatLng(37.50352, 126.95706)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
        mapLoadedCallBack()
    }

    private fun mapLoadedCallBack() {
        mMap.setOnMapLoadedCallback {
            list = placeAPI.search_place_list(intent.getStringExtra("input"))
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
            val first_location =
                LatLng(list[0].latitude.toDouble(), list[0].longitude.toDouble())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first_location, 18.0F))
            mMap.addMarker(MarkerOptions().position(first_location).title(list[0].name))
        }
    }
}