package com.cau.capstone.beableto.data

import com.google.gson.annotations.SerializedName

data class RequestRoute(
    @SerializedName("start_x_axis")
    var start_x_axis: Float,
    @SerializedName("start_y_axis")
    var start_y_axis: Float,
    @SerializedName("end_x_axis")
    var end_x_axis: Float,
    @SerializedName("end_y_axis")
    var end_y_axis: Float
)