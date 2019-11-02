package com.cau.capstone.beableto.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.Coordinate
import com.cau.capstone.beableto.data.RequestRegisterRoute
import com.cau.capstone.beableto.repository.SharedPreferenceController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_route3.*

class RegisterRouteActivity3 : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var cu: CameraUpdate

    private var start_latitude: Float? = null
    private var start_longitude: Float? = null
    private var start_latlng: LatLng? = null
    private var end_latitude: Float? = null
    private var end_longitude: Float? = null
    private var end_latlng: LatLng? = null
    private var middle_latitude: Float? = null
    private var middle_longitude: Float? = null
    private var middle_latlng: LatLng? = null
    private var slope: Int? = null
    private var next_available: Boolean = true
    private var latitude_list: MutableList<Float> = ArrayList()
    private var longitude_list: MutableList<Float> = ArrayList()
    private var coordinate_list: MutableList<Coordinate> = ArrayList()

    private var is_rest_middle: Boolean = false
    private var middle_num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_route3)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_register_route3) as SupportMapFragment
        mapFragment.getMapAsync(this)
        center_marker3.visibility = View.INVISIBLE

        if (intent.hasExtra("start_latitude") && intent.hasExtra("start_longitude") && intent.hasExtra(
                "slope"
            ) && intent.hasExtra("end_latitude") && intent.hasExtra("end_longitude")
        ) {
            start_latitude = intent.getFloatExtra("start_latitude", 0.0F)
            start_longitude = intent.getFloatExtra("start_longitude", 0.0F)
            slope = intent.getIntExtra("slope", 0)
            end_latitude = intent.getFloatExtra("end_latitude", 0.0F)
            end_longitude = intent.getFloatExtra("end_longitude", 0.0F)
            start_latlng = LatLng(
                start_latitude!!.toDouble(),
                start_longitude!!.toDouble()
            )
            end_latlng = LatLng(
                end_latitude!!.toDouble(),
                end_longitude!!.toDouble()
            )
        }

        btn_register_route_previous3.setOnClickListener {
            finish()
        }

        btn_register_route_next3.setOnClickListener {
            if (!is_rest_middle) {
                //TODO 서버로 도로 정보 보내기
                for (i in latitude_list.indices) {
                    val coordinate = Coordinate(latitude_list[i], longitude_list[i])
                    coordinate_list.add(coordinate)
                }
                coordinate_list.add(Coordinate(end_latitude!!, end_longitude!!))
                Log.d("coordinate_list", coordinate_list.toString())
                val requestRegisterRoute = RequestRegisterRoute(coordinate_list, slope!!)

                intent.putExtra("start_latitude", start_latitude!!)
                intent.putExtra("start_longitude", start_longitude!!)
                intent.putExtra("end_latitude", end_latitude!!)
                intent.putExtra("end_longitude", end_longitude!!)
                setResult(Activity.RESULT_OK, intent)
                finish()
                /*
                NetworkCore.getNetworkCore<BEABLETOAPI>()
                    .requestRegisterRoute(
                        SharedPreferenceController.getAuthorization(this), requestRegisterRoute
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val intent = Intent()
                        intent.putExtra("start_latitude", start_latitude)
                        intent.putExtra("start_longitude", start_longitude)
                        intent.putExtra("end_latitude", end_latitude)
                        intent.putExtra("end_longitude", end_longitude)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }, {
                        Log.d("Route_Register_Error", Log.getStackTraceString(it))
                    })

                 */
            } else {
                Toast.makeText(
                    this@RegisterRouteActivity3,
                    "도로의 중간점을 설정하거나 삭제해 주세요.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        btn_register_route_setting3.setOnClickListener {
            if (is_rest_middle) {
                val center = mMap.cameraPosition.target
                middle_latitude = center.latitude.toFloat()
                middle_longitude = center.longitude.toFloat()
                latitude_list.add(middle_latitude!!)
                longitude_list.add(middle_longitude!!)
                btn_register_route_setting3.isSelected = false
                is_rest_middle = false
                next_available = true
                mMap.clear()
                val before_latlng = drawPolyLine()
                mMap.addMarker(MarkerOptions().position(end_latlng!!))
                mMap.addPolyline(
                    PolylineOptions().add(before_latlng, end_latlng).width(10.0F).color(
                        Color.RED
                    )
                )
                center_marker3.visibility = View.INVISIBLE
                btn_register_route_next3.setTextColor(Color.parseColor("#000000"))
                register_route_info3.text = "중간점을 추가하거나 완료 버튼을 누르세요"
            } else {
                Toast.makeText(this@RegisterRouteActivity3, "중간점을 추가해 주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_add_middle.setOnClickListener {
            if (is_rest_middle) {
                Toast.makeText(
                    this@RegisterRouteActivity3,
                    "추가한 중간점 설정을 먼저 완료해 주세요.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                next_available = false
                center_marker3.visibility = View.VISIBLE
                is_rest_middle = true
                btn_register_route_next3.setTextColor(Color.parseColor("#909090"))
                middle_num++
            }
        }

        btn_delete_middle.setOnClickListener {
            if (is_rest_middle) {
                center_marker3.visibility = View.INVISIBLE
                is_rest_middle = false
                mMap.clear()
                val before_latlng = drawPolyLine()
                mMap.addMarker(MarkerOptions().position(end_latlng!!))
                mMap.addPolyline(
                    PolylineOptions().add(before_latlng, end_latlng).width(10.0F).color(
                        Color.RED
                    )
                )
                middle_num--
                btn_register_route_next3.setTextColor(Color.parseColor("#ffffff"))
            } else if (middle_num != 0) {
                latitude_list.removeAt(middle_num)
                longitude_list.removeAt(middle_num)
                mMap.clear()
                val before_latlng = drawPolyLine()
                mMap.addMarker(MarkerOptions().position(end_latlng!!))
                mMap.addPolyline(
                    PolylineOptions().add(before_latlng, end_latlng).width(10.0F).color(
                        Color.RED
                    )
                )
                middle_num--
            } else {
                Toast.makeText(this@RegisterRouteActivity3, "제거할 중간점이 없습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val INIT = LatLng(37.50352, 126.95706)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
        mMap.isMyLocationEnabled = true
        val bound_markers_list: MutableList<Marker> = ArrayList()
        val start_marker = mMap.addMarker(MarkerOptions().position(start_latlng!!).title("시작점"))
        val end_marker = mMap.addMarker(MarkerOptions().position(end_latlng!!).title("도착점"))
        mMap.addPolyline(
            PolylineOptions().add(start_latlng, end_latlng).width(10.0F).color(
                Color.RED
            )
        )
        latitude_list.add(start_latitude!!)
        longitude_list.add(start_longitude!!)
        val padding = 100
        bound_markers_list.add(start_marker)
        bound_markers_list.add(end_marker)
        val builder = LatLngBounds.Builder()
        for (m in bound_markers_list) {
            builder.include(m.position)
        }
        val bounds = builder.build()
        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
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
            mMap.animateCamera(cu)
        }

        mMap.setOnCameraMoveStartedListener {
            btn_register_route_setting3.isSelected = false
            if (is_rest_middle) center_marker3.visibility = View.VISIBLE
        }

        mMap.setOnCameraMoveListener {
            btn_register_route_setting3.isSelected = false
            if (is_rest_middle) center_marker3.visibility = View.VISIBLE
        }

        mMap.setOnCameraIdleListener {
            if (is_rest_middle) {
                var before_latlng: LatLng
                btn_register_route_setting3.isSelected = true
                next_available = false
                val center = mMap.cameraPosition.target
                middle_latlng = LatLng(center.latitude, center.longitude)
                mMap.clear()
                before_latlng = drawPolyLine()
                mMap.addMarker(
                    MarkerOptions().position(middle_latlng!!).icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                )
                mMap.addMarker(MarkerOptions().position(end_latlng!!))
                mMap.addPolyline(
                    PolylineOptions().add(before_latlng, middle_latlng).width(10.0F).color(
                        Color.RED
                    )
                )
                mMap.addPolyline(
                    PolylineOptions().add(middle_latlng, end_latlng).width(10.0F).color(
                        Color.RED
                    )
                )
                center_marker3.visibility = View.INVISIBLE
                register_route_info3.text = "지도를 움직여 도로의 중간점을 설정하세요"
            }
        }

        et_route_search3.setOnEditorActionListener(
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
        val searchString = et_route_search3.text.toString()
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

    private fun drawPolyLine(): LatLng {
        var before_latlng: LatLng? = null
        var current_latlng: LatLng
        for (i in latitude_list.indices) {
            current_latlng =
                LatLng(latitude_list[i].toDouble(), longitude_list[i].toDouble())
            mMap.addMarker(
                MarkerOptions().position(
                    current_latlng
                )
            )
            if (i != 0) {
                mMap.addPolyline(
                    PolylineOptions().add(before_latlng, current_latlng).width(10.0F).color(
                        Color.RED
                    )
                )
            }
            before_latlng = current_latlng
        }
        return before_latlng!!
    }
}