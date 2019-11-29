package com.cau.capstone.beableto.data

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusteringLocation(
    lat: Double,
    lng: Double,
    name: String,
    address: String?,
    slope: Int,
    auto_door: Boolean,
    toilet: Boolean,
    elevator: Boolean,
    snippet: String
) : ClusterItem {
    val latitude = lat
    val longitude = lng
    val location_name = name
    val location_address = address
    val location_slope = slope
    val location_auto_door = auto_door
    val location_toilet = toilet
    val location_elevator = elevator
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
}