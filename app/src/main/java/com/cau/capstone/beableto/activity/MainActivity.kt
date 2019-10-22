package com.cau.capstone.beableto

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.cau.capstone.beableto.activity.RegisterLocationActivity
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.repository.SharedPreferenceController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        temp.text = Build.VERSION.SDK_INT.toString()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        /*
        NetworkCore.getNetworkCore<BEABLETOAPI>()
            .test2(
                SharedPreferenceController.getAuthorization(this@MainActivity)
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response ->
                Log.d("list_data", response.phone)
                Toast.makeText(this@MainActivity, response.phone + "/" + response.guardian_phone, Toast.LENGTH_SHORT).show()
                temp.text = response.phone
            }, {
                Log.d("list_data", Log.getStackTraceString(it))
            })
        */

        temp_logout.setOnClickListener {
            SharedPreferenceController.logout(this@MainActivity)
            val intent = Intent(this, InitActivity::class.java)
            startActivity(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity()
            }
        }

        temp_register_location.setOnClickListener {
            val intent = Intent(this, RegisterLocationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap){
        mMap = googleMap

        val SEOUL = LatLng(37.502777, 126.956665)
        mMap.addMarker(MarkerOptions().position(SEOUL).title("Marker in Seoul"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10F))
    }
}
