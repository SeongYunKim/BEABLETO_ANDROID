package com.cau.capstone.beableto.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.cau.capstone.beableto.Adapter.RouteDetailAdapter
import com.cau.capstone.beableto.Adapter.RoutePagerAdapter
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.HelpCenter
import com.cau.capstone.beableto.data.RequestRoute
import com.cau.capstone.beableto.data.Route
import com.cau.capstone.beableto.data.RouteDetail
import com.cau.capstone.beableto.fragment.HelpCenterFragment
import com.cau.capstone.beableto.fragment.RouteFragment
import com.cau.capstone.beableto.repository.SharedPreferenceController
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil.decode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_show_route.*
import kotlinx.android.synthetic.main.help_center_dialog.*

class ShowRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var route_pager_adapter: RoutePagerAdapter? = null

    private var time_list: MutableList<Int> = ArrayList()
    private var latitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var longitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var bus_latitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var bus_longitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var bus_poly_list: MutableList<MutableList<String>> = ArrayList()
    private var train_latitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var train_longitude_list: MutableList<MutableList<Float>> = ArrayList()
    private var train_poly_list: MutableList<MutableList<String>> = ArrayList()
    private var slope_list: MutableList<MutableList<Int>> = ArrayList()
    private var route_detail_list = MutableList(5) { arrayListOf<RouteDetail>() }
    private var walk_time_list = arrayListOf<Int>()

    private var latitude: Float? = null
    private var longitude: Float? = null
    private var start_latitude: Float? = null
    private var start_longitude: Float? = null
    private var end_latitude: Float? = null
    private var end_longitude: Float? = null

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
            bus_poly_list.add(mutableListOf(""))
            train_latitude_list.add(mutableListOf(0.0F))
            train_longitude_list.add(mutableListOf(0.0F))
            train_poly_list.add(mutableListOf(""))
        }

        if (type == "start") {

        } else if (type == "end") {
            et_search_start.setText("현위치")
            et_search_end.setText(location_name)
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_show_route) as SupportMapFragment
        mapFragment.getMapAsync(this)

        route_pager_adapter = RoutePagerAdapter(supportFragmentManager)
        viewpager_show_route.adapter = route_pager_adapter

        viewpager_show_route.clipToPadding = false
        val dp_value = 60
        val d = resources.displayMetrics.density
        val margin = (dp_value + d).toInt()
        viewpager_show_route.setPadding(margin, 0, margin * 5, 0)
        viewpager_show_route.pageMargin = margin / 2

        viewpager_show_route.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                mMap.clear()
                drawPolyLine(position)
                adjustCamera()
            }
        })

        btn_help_center.setOnClickListener {
            val help_center =
                HelpCenter(35.1978078f, 129.0799214f, "부산광역시 특별교통총괄본부", 158, "051-466-8800")
            val helpCenterFragment = HelpCenterFragment(this, help_center)
            helpCenterFragment.show()
        }
    }

    override fun onBackPressed() {
        if (ll_route_detail.visibility == View.VISIBLE) {
            ll_route_detail.visibility = View.GONE
            rl_select_route.visibility = View.VISIBLE
            scrollview_route.pageScroll(View.FOCUS_UP)
        } else {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val INIT = LatLng(37.50352, 126.95706)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
        mMap.isMyLocationEnabled = true
        mapLoadedCallBack()
    }

    private fun mapLoadedCallBack() {
        mMap.setOnMapLoadedCallback {
            start_latitude = SharedPreferenceController.getCurrentLocation(this).latitude.toFloat()
            start_longitude =
                SharedPreferenceController.getCurrentLocation(this).longitude.toFloat()
            end_latitude = latitude
            end_longitude = longitude
            val requestRoute =
                RequestRoute(
                    start_latitude!!,
                    start_longitude!!,
                    end_latitude!!,
                    end_longitude!!
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
                        var time = 0
                        var walk_time = 0
                        var sub_time: Int
                        route_detail_list[ps].add(
                            RouteDetail(
                                -1,
                                0,
                                et_search_start.text.toString(),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                            )
                        )
                        for (p in response.paths[ps].path) {
                            sub_time = 0
                            if (p.time != null) {
                                sub_time = p.time.value
                                time += sub_time
                                if (p.type == "walk") {
                                    walk_time += sub_time
                                }
                            }
                            if (p.type == "walk") {
                                for (ws in p.walk_seq) {
                                    latitude_list[ps].add(ws.start_x!!)
                                    longitude_list[ps].add(ws.start_y!!)
                                    latitude_list[ps].add(ws.end_x!!)
                                    longitude_list[ps].add(ws.end_y!!)
                                    slope_list[ps].add(ws.slope)
                                }
                                route_detail_list[ps].add(
                                    RouteDetail(
                                        0,
                                        sub_time,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                    )
                                )
                            } else if (p.type == "bus") {
                                bus_latitude_list[ps].add(p.bus_start_x!!)
                                bus_longitude_list[ps].add(p.bus_start_y!!)
                                bus_latitude_list[ps].add(p.bus_end_x!!)
                                bus_longitude_list[ps].add(p.bus_end_y!!)
                                bus_poly_list[ps].add(p.bus_poly!!)
                                route_detail_list[ps].add(
                                    RouteDetail(
                                        1,
                                        sub_time,
                                        p.departure,
                                        p.arrival,
                                        p.bus_line,
                                        null,
                                        p.bus_height,
                                        p.bus_area,
                                        p.color
                                    )
                                )
                            } else if (p.type == "train") {
                                train_latitude_list[ps].add(p.train_start_x!!)
                                train_longitude_list[ps].add(p.train_start_y!!)
                                train_latitude_list[ps].add(p.train_end_x!!)
                                train_longitude_list[ps].add(p.train_end_y!!)
                                train_poly_list[ps].add(p.train_poly!!)
                                route_detail_list[ps].add(
                                    RouteDetail(
                                        2,
                                        sub_time,
                                        p.departure,
                                        p.arrival,
                                        p.train_line,
                                        p.walk_sub,
                                        null,
                                        null,
                                        p.color
                                    )
                                )
                            }
                        }
                        route_detail_list[ps].add(
                            RouteDetail(
                                -1,
                                0,
                                et_search_end.text.toString(),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                            )
                        )
                        walk_time_list.add(walk_time)
                        time_list.add(time)
                    }
                    Log.d("time_list", time_list.toString())
                    for (i in time_list.indices) {
                        val route_fragment = RouteFragment()
                        val bundle = Bundle()
                        bundle.putSerializable(
                            "route_info",
                            Route(
                                time_list[i],
                                walk_time_list[i],
                                bus_poly_list[i].size > 1,
                                train_poly_list[i].size > 1,
                                route_detail_list[i]
                            )
                        )
                        route_fragment.arguments = bundle
                        route_pager_adapter!!.addItem(route_fragment)
                    }
                    route_pager_adapter!!.notifyDataSetChanged()
                    drawPolyLine(0)
                    adjustCamera()
                }, {
                    Log.d("SSibal_Error", Log.getStackTraceString(it))
                })
        }
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
        for (a in bus_poly_list[num].indices) {
            //decodePoly(polyline)
            if (a != 0) {
                val busLatLngList = decode(bus_poly_list[num][a])
                for (i in 0..busLatLngList.size - 2) {
                    mMap.addPolyline(
                        PolylineOptions().add(
                            busLatLngList[i],
                            busLatLngList[i + 1]
                        ).width(10.0F).color(
                            Color.BLUE
                        )
                    )
                }
            }
        }
        for (a in train_poly_list[num].indices) {
            //decodePoly(polyline)
            if (a != 0) {
                val trainLatLngList = decode(train_poly_list[num][a])
                for (i in 0..trainLatLngList.size - 2) {
                    mMap.addPolyline(
                        PolylineOptions().add(
                            trainLatLngList[i],
                            trainLatLngList[i + 1]
                        ).width(10.0F).color(
                            Color.CYAN
                        )
                    )
                }
            }
        }
    }

    private fun adjustCamera() {
        val start_marker = mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    start_latitude!!.toDouble(),
                    start_longitude!!.toDouble()
                )
            ).title("시작점")
        )
        val end_marker = mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    end_latitude!!.toDouble(),
                    end_longitude!!.toDouble()
                )
            ).title("도착점")
        )
        val bound_markers_list: MutableList<Marker> = ArrayList()
        val padding = 400
        bound_markers_list.add(start_marker)
        bound_markers_list.add(end_marker)
        val builder = LatLngBounds.Builder()
        for (m in bound_markers_list) {
            builder.include(m.position)
        }
        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.animateCamera(cu)
    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = arrayListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = (encoded[index++] - 63).toInt()
                result = result or (b and 0x1f) shl shift
                shift += 5
            } while (b >= 0x20)

            var dlat: Int
            if ((result and 1) != 0)
                dlat = -(result shl 1)
            else
                dlat = result shl 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = (encoded[index++] - 63).toInt()
                result = result or (b and 0x1f) shl shift
                shift += 5
            } while (b >= 0x20)

            var dlng: Int
            if ((result and 1) != 0)
                dlng = -(result shl 1)
            else
                dlng = result shl 1
            lng += dlng

            val latlng = LatLng((lat.toDouble() / 1E5) * 1E6, (lng.toDouble() / 1E5) * 1E6)
            Log.d("Decode", latlng.toString())
            poly.add(latlng)
        }
        return poly
    }
}
