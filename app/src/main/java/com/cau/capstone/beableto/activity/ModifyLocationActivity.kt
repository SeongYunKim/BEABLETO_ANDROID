package com.cau.capstone.beableto.activity

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.RequestMarkerOnMap
import com.cau.capstone.beableto.data.ResponseMarkerOnMap
import com.cau.capstone.beableto.repository.SharedPreferenceController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_modify_location.*
import java.util.*

class ModifyLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var latitude: Float? = null
    private var longitude: Float? = null
    private var address: String? = null

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
            val intent = Intent()
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("address", address)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val CUR: LatLng
        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
            CUR = LatLng(
                intent.getFloatExtra("latitude", 0.0F).toDouble(),
                intent.getFloatExtra("longitude", 0.0F).toDouble()
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CUR, 18.0F))
        }
        mapLoadedCallBack()
    }

    private fun mapLoadedCallBack() {
        mMap.setOnMapLoadedCallback {
            getMarkerInfo(getMapBound())
        }

        mMap.setOnCameraMoveStartedListener {
            btn_modify_next.isSelected = false
            tv_modify_address.text = "위치 이동중 입니다."
        }

        mMap.setOnCameraMoveListener {
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
            mMap.clear()
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
        for (marker in response.markers) {
            val location = LatLng(marker.x_axis.toDouble(), marker.y_axis.toDouble())
            mMap.addMarker(MarkerOptions().position(location).title(marker.location_name))
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
}