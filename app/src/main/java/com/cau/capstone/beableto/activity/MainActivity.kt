package com.cau.capstone.beableto.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cau.capstone.beableto.Adapter.CustomClusterAdapter
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.*
import com.cau.capstone.beableto.fragment.LocationPhotoFragment
import com.cau.capstone.beableto.repository.SharedPreferenceController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val GET_REGISTER_LOCATION = 9012
    private val SETTING = 7890
    private val PERMISSION_CODE = 3456
    private var mMap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment
    private var mLocationPermissionGranted = false
    private lateinit var lastLocation: Location
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var stair_marker: Boolean = true
    private var sharp_marker: Boolean = true
    private var gentle_marker: Boolean = true
    private var show_route: Boolean = true
    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private var isFabOpen: Boolean = false
    private lateinit var mClusterManger: ClusterManager<ClusteringLocation>

    private var location_x: Float = 0f
    private var location_y: Float = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_open = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)

        //mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        //mapFragment.getMapAsync(this)

        var setting = SharedPreferenceController.getSetting(this@MainActivity)
        stair_marker = setting.stair
        sharp_marker = setting.sharp
        gentle_marker = setting.gentle
        show_route = setting.route

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

        drawer_mypage.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        drawer_record.setOnClickListener {
            val intent = Intent(this, RecordActivity::class.java)
            startActivity(intent)
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
            val intent = Intent(this, AutoSuggestActivity::class.java)
            intent.putExtra("limit", "limit")
            startActivity(intent)
        }

        /*
        temp_marker.setOnClickListener {
            getMarkerInfo(getMapBound())
        }
        */
    }

    override fun onResume() {
        super.onResume()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        if (mMap != null) {
            mMap!!.clear()
            mClusterManger.clearItems()
            getAllMarkerInfo()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var latitude: Float?
        var longitude: Float?
        if (requestCode == GET_REGISTER_LOCATION && resultCode == Activity.RESULT_OK && data != null) {
            if (data.hasExtra("latitude") && data.hasExtra("longitude")) {
                mMap!!.clear()
                mClusterManger.clearItems()
                getAllMarkerInfo()
                latitude = data.getFloatExtra("latitude", 0.0F)
                longitude = data.getFloatExtra("longitude", 0.0F)
                Log.d("MainData", latitude.toString() + " " + longitude.toString())
                mMap!!.moveCamera(
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
            show_route = setting.route
            mMap!!.clear()
            mClusterManger.clearItems()
            getAllMarkerInfo()
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

    override fun onBackPressed() {
        if (main_marker_info.visibility == View.VISIBLE) {
            main_marker_info.visibility = View.GONE
        } else {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val INIT = LatLng(37.50352, 126.95706)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
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
            mMap!!.isMyLocationEnabled = true
            mClusterManger = ClusterManager(this, mMap)
            mapLoadedCallBack()
        }
    }

    private fun mapLoadedCallBack() {
        mMap!!.setOnMapLoadedCallback {
            mClusterManger.renderer = CustomClusterAdapter(this, mMap!!, mClusterManger)
            mMap!!.setOnMarkerClickListener(mClusterManger)
            //mMap.setOnCameraIdleListener(mClusterManger)
            mMap!!.clear()
            mClusterManger.clearItems()
            getAllMarkerInfo()
            if (show_route && mMap!!.cameraPosition.zoom > 15.0F) {
                getFragment(getMapBound())
            }

            mClusterManger.setOnClusterItemClickListener(object :
                ClusterManager.OnClusterItemClickListener<ClusteringLocation> {
                override fun onClusterItemClick(p0: ClusteringLocation?): Boolean {
                    main_marker_info.visibility = View.VISIBLE
                    vp_main_name.text = p0!!.location_name
                    vp_main_address.text = p0.location_address
                    location_x = p0.latitude.toFloat()
                    location_y = p0.longitude.toFloat()
                    mMap!!.animateCamera(
                        CameraUpdateFactory.newLatLng(
                            LatLng(location_x.toDouble(), location_y.toDouble())
                        )
                    )
                    when (p0.location_slope) {
                        0 -> {
                            vp_main_slope.text = "경사 완만"
                            vp_main_slope.setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.colorPrimary
                                )
                            )
                        }
                        1 -> {
                            vp_main_slope.text = "경사 급함"
                            vp_main_slope.setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.colorBlack
                                )
                            )
                        }
                        2 -> {
                            vp_main_slope.text = "계단"
                            vp_main_slope.setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.colorBlack
                                )
                            )
                        }
                    }
                    when (p0.location_auto_door) {
                        true -> {
                            vp_main_auto_door.text = "자동문"
                            vp_main_auto_door.setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.colorPrimary
                                )
                            )
                        }
                        false -> {
                            vp_main_auto_door.text = "수동문"
                            vp_main_auto_door.setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.colorBlack
                                )
                            )
                        }
                    }
                    when (p0.location_elevator) {
                        true -> {
                            vp_main_elevator.text = "있음"
                            vp_main_elevator.setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.colorPrimary
                                )
                            )
                        }
                        false -> {
                            vp_main_elevator.text = "없음"
                            vp_main_elevator.setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.colorBlack
                                )
                            )
                        }
                    }
                    when (p0.location_toilet) {
                        true -> {
                            vp_main_toilet.text = "있음"
                            vp_main_toilet.setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.colorPrimary
                                )
                            )
                        }
                        false -> {
                            vp_main_toilet.text = "없음"
                            vp_main_toilet.setTextColor(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.colorBlack
                                )
                            )
                        }
                    }
                    return true
                }
            })

            btn_image.setOnClickListener {
                val locationPhotoFragment = LocationPhotoFragment(this, location_x, location_y)
                locationPhotoFragment.show()
            }
        }

        mMap!!.setOnCameraChangeListener(object : GoogleMap.OnCameraChangeListener {
            override fun onCameraChange(p0: CameraPosition?) {
                mClusterManger.onCameraIdle()
                if (show_route && mMap!!.cameraPosition.zoom > 15.0F) {
                    //mMap.clear()
                    //mClusterManger.onCameraIdle()
                    getFragment(getMapBound())
                }
            }
        })

        et_main_place_search.setOnClickListener {
            val intent = Intent(this, AutoSuggestActivity::class.java)
            SharedPreferenceController.setCurrentLocation(
                this,
                lastLocation.latitude.toFloat(),
                lastLocation.longitude.toFloat()
            )
            startActivity(intent)
        }

        /*
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
        */
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                    if (location != null) {
                        lastLocation = location
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap!!.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng,
                                18.0F
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMapBound(): Pair<LatLng, LatLng> {
        val bounds = mMap!!.projection.visibleRegion.latLngBounds
        val northEast = bounds.northeast
        val southWest = bounds.southwest
        return Pair(northEast, southWest)
    }

    private fun getAllMarkerInfo() {
        NetworkCore.getNetworkCore<BEABLETOAPI>()
            .requestGetAllMarkers(
                SharedPreferenceController.getAuthorization(this@MainActivity)
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d("GetAll_Success", response.markers.toString())
                drawAllMarker(response)
            }, {
                Log.d("GetAll_Error", Log.getStackTraceString(it))
            })
    }

    /*
    private fun getMarkerInfo(pair: Pair<LatLng, LatLng>) {
        val requestMarkerOnMap = RequestMarkerOnMap(
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
                //drawMarker(response)
            }, {
                Log.d("Marker_Error", Log.getStackTraceString(it))
            })
    }
     */

    private fun getFragment(pair: Pair<LatLng, LatLng>) {
        val requestMarkerOnMap = RequestMarkerOnMap(
            pair.first.latitude.toString(),
            pair.first.longitude.toString(),
            pair.second.latitude.toString(),
            pair.second.longitude.toString()
        )

        NetworkCore.getNetworkCore<BEABLETOAPI>()
            .requestFragmentOnMap(
                SharedPreferenceController.getAuthorization(this@MainActivity), requestMarkerOnMap
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d("Marker_Success", response.fragments.toString())
                drawFragment(response)
            }, {
                Log.d("Marker_Error", Log.getStackTraceString(it))
            })
    }

    /*
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
     */

    private fun drawAllMarker(response: ResponseMarkerOnMap) {
        for (r in response.markers) {
            if ((r.slope == 0 && gentle_marker) || (r.slope == 1 && sharp_marker) || (r.slope == 2 && stair_marker))
                mClusterManger.addItem(
                    ClusteringLocation(
                        r.x_axis.toDouble(),
                        r.y_axis.toDouble(),
                        r.location_name,
                        r.address,
                        r.slope,
                        r.auto_door,
                        r.toilet,
                        r.elevator,
                        "안녕하세요"
                    )
                )
        }
        mClusterManger.onCameraIdle()
    }

    private fun drawFragment(response: ResponseFragmentOnMap) {
        var latitude_list: MutableList<Float> = ArrayList()
        var longitude_list: MutableList<Float> = ArrayList()
        var slope_list: MutableList<Int> = ArrayList()
        var current_latlng: LatLng
        var before_latlng: LatLng
        for (fragment in response.fragments) {
            latitude_list.add(fragment.start_x)
            longitude_list.add(fragment.start_y)
            latitude_list.add(fragment.end_x)
            longitude_list.add(fragment.end_y)
            slope_list.add(fragment.slope)
        }
        for (i in latitude_list.indices) {
            if (i % 2 == 0) {
                before_latlng = LatLng(latitude_list[i].toDouble(), longitude_list[i].toDouble())
                current_latlng =
                    LatLng(latitude_list[i + 1].toDouble(), longitude_list[i + 1].toDouble())
                if (slope_list[i / 2] == 0) {
                    mMap!!.addPolyline(
                        PolylineOptions().add(before_latlng, current_latlng).width(10.0F).color(
                            Color.YELLOW
                        )
                    )
                } else if (slope_list[i / 2] == 1) {
                    mMap!!.addPolyline(
                        PolylineOptions().add(before_latlng, current_latlng).width(10.0F).color(
                            Color.RED
                        )
                    )
                } else if (slope_list[i / 2] == 2) {
                    mMap!!.addPolyline(
                        PolylineOptions().add(before_latlng, current_latlng).width(10.0F).color(
                            Color.BLACK
                        )
                    )
                }
            }
        }
    }

    private fun anim() {
        if (isFabOpen) {
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
            fab3.isClickable = true
            isFabOpen = true
        }
    }
}