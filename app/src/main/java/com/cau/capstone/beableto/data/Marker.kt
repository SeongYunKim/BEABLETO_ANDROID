package com.cau.capstone.beableto.data

data class Marker(
    val location_name: String,
    val x_axis: Float,
    val y_axis: Float,
    val slope: Int,
    val auto_door: Boolean,
    val elevator: Boolean,
    val toilet: Boolean,
    val address: String
)