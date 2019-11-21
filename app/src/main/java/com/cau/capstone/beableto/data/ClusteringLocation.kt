package com.cau.capstone.beableto.data

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusteringLocation(lat: Double, lng: Double, slope: Int, snippet: String) : ClusterItem {
    val latitude = lat
    val longitude = lng
    val location_slope = slope
    val location_snippet = snippet

    override fun getPosition(): LatLng {
        return LatLng(latitude, longitude)
    }

    override fun getTitle(): String? {
        return null
    }

    override fun getSnippet(): String? {
        return location_snippet
    }

    fun getLocationSlope(): Int {
        return location_slope
    }
}