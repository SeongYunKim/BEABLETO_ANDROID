package com.cau.capstone.beableto.fragment

import com.cau.capstone.beableto.data.HelpCenter
import kotlinx.android.synthetic.main.help_center_dialog.*

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import com.cau.capstone.beableto.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HelpCenterFragment(context: Context, help_center: HelpCenter) : Dialog(context) {

    val mContext = context
    val help_center = help_center

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        setContentView(R.layout.help_center_dialog)

        tv_help_center.text = "기관명: " + help_center.name
        tv_car_num.text = "보유차량대수: " + help_center.car

        val mMapView = findViewById<MapView>(R.id.map_help_center)
        MapsInitializer.initialize(context)
        mMapView.onCreate(onSaveInstanceState())
        mMapView.onResume()
        mMapView.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(p0: GoogleMap?) {
                val INIT = LatLng(help_center.x_axis.toDouble(), help_center.y_axis.toDouble())
                p0!!.addMarker(MarkerOptions().position(INIT))
                p0.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
            }
        })

        dialog_close.setOnClickListener {
            dismiss()
        }

        dialog_call.setOnClickListener {
            val tel: String = "tel:" + help_center.phone
            mContext.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(tel)))
        }
    }
}