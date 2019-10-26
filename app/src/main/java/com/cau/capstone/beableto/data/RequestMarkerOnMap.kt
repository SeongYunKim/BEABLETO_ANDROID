package com.cau.capstone.beableto.data

import com.google.gson.annotations.SerializedName

data class RequestMarkerOnMap (
    @SerializedName("rnx")
    //var east_north_latitude: String,
    var rnx: String,
    @SerializedName("rny")
    //var east_north_longitude: String,
    var rny: String,
    @SerializedName("lsx")
    //var west_south_latitude: String,
    var lsx: String,
    @SerializedName("lsy")
    //var west_south_longitude: String
    var lsy: String
)