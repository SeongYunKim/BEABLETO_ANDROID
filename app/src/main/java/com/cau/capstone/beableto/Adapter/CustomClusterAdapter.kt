package com.cau.capstone.beableto.Adapter

import android.content.Context
import com.cau.capstone.beableto.data.ClusteringLocation
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.clustering.view.DefaultClusterRenderer


class CustomClusterAdapter(
    mContext: Context, map: GoogleMap,
    clusterManager: ClusterManager<ClusteringLocation>
) : DefaultClusterRenderer<ClusteringLocation>(mContext, map, clusterManager) {

    override fun onBeforeClusterItemRendered(
        item: ClusteringLocation,
        markerOptions: MarkerOptions
    ) {
        val slope = item.location_slope
        var markerDescriptor: BitmapDescriptor? = null
        if (slope == 0) {
            markerDescriptor =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        } else if (slope == 1) {
            markerDescriptor =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
        } else if (slope == 2) {
            markerDescriptor =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
        }
        markerOptions.icon(markerDescriptor)
    }
}