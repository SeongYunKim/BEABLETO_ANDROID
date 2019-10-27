package com.cau.capstone.beableto.Adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import com.cau.capstone.beableto.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.*

class CustomInfoWindowAdapter(val context: Context) : GoogleMap.InfoWindowAdapter {

    var mWindow : View? = (context as Activity).layoutInflater.inflate(R.layout.custom_info_window, null)

    fun renderWindowText(marker: Marker, view: View) {
        val tv_marker_title: TextView = view.findViewById(R.id.info_title)
        tv_marker_title.text = marker.title
        val tv_marker_snippet: TextView = view.findViewById(R.id.info_snippet)
        tv_marker_snippet.text = marker.snippet
    }

    override fun getInfoContents(p0: Marker?): View {
        renderWindowText(p0!!, mWindow!!)
        return mWindow!!
    }

    override fun getInfoWindow(p0: Marker?): View? {
        renderWindowText(p0!!, mWindow!!)
        return mWindow!!
    }
}