package com.cau.capstone.beableto.activity

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_register_route.*

class AdminActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private var latitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var longitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var bus_latitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var bus_longitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var train_latitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var train_longitude_list: MutableList<MutableList<Float>> = ArrayList()

    private var slope_list: MutableList<MutableList<Int>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        admin_start_x.setText(intent.getFloatExtra("start_latitude", 0.0F).toString())
        admin_start_y.setText(intent.getFloatExtra("start_longitude", 0.0F).toString())
        admin_end_x.setText(intent.getFloatExtra("end_latitude", 0.0F).toString())
        admin_end_y.setText(intent.getFloatExtra("end_longitude", 0.0F).toString())

        for (x in 0 until 5) {
            latitude_list.add(mutableListOf(0.0F))
            longitude_list.add(mutableListOf(0.0F))
            slope_list.add(mutableListOf(0))
            bus_latitude_list.add(mutableListOf(0.0F))
            bus_longitude_list.add(mutableListOf(0.0F))
            train_latitude_list.add(mutableListOf(0.0F))
            train_longitude_list.add(mutableListOf(0.0F))
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_admin) as SupportMapFragment
        mapFragment.getMapAsync(this)

        admin_search.setOnClickListener {
            val requestAdmin =
                RequestRoute(
                    admin_start_x.text.toString().toFloat(),
                    admin_start_y.text.toString().toFloat(),
                    admin_end_x.text.toString().toFloat(),
                    admin_end_y.text.toString().toFloat()
                )
            NetworkCore.getNetworkCore<BEABLETOAPI>()
                .requestAdmin(
                    SharedPreferenceController.getAuthorization(this), requestAdmin
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.d("SSibal", response.toString())
                    Log.d("SSibal_latitude", latitude_list[0].toString())
                    for (ps in response.paths.indices) {
                        for (p in response.paths[ps].path) {
                            if (p.type == "walk") {
                                var is_first: Boolean = true
                                //latitude_list.add(ps.walk_start_x!!)
                                //longitude_list.add(ps.walk_start_y!!)
                                for (ws in p.walk_seq) {
                                    //if (is_first) {
                                        latitude_list[ps].add(ws.start_x!!)
                                        longitude_list[ps].add(ws.start_y!!)
                                        //is_first = false
                                    //}
                                    latitude_list[ps].add(ws.end_x!!)
                                    longitude_list[ps].add(ws.end_y!!)
                                    slope_list[ps].add(ws.slope)
                                }
                                //slope_list[ps].add(0)
                                //latitude_list.add(ps.walk_end_x!!)
                                //longitude_list.add(ps.walk_end_y!!)
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
                }, {
                    Log.d("SSibal_Error", Log.getStackTraceString(it))
                })
            Log.d("SSibal", latitude_list[3].toString())
        }

        admin_route1.setOnClickListener {
            mMap.clear()
            drawPolyLine(0)
        }

        admin_route2.setOnClickListener {
            mMap.clear()
            drawPolyLine(1)
        }

        admin_route3.setOnClickListener {
            mMap.clear()
            drawPolyLine(2)
        }

        admin_route4.setOnClickListener {
            mMap.clear()
            drawPolyLine(3)
        }

        admin_route5.setOnClickListener {
            mMap.clear()
            drawPolyLine(4)
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
    }

    private fun drawPolyLine(num: Int): LatLng {
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
                            Color.BLUE
                        )
                    )
                }
                before_latlng = current_latlng
            }
        }
        return before_latlng!!
    }
}