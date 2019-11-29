package com.cau.capstone.beableto.data

data class ResponseLocationPhoto (
    val image: String?,
    val location_name: String?,
    val location_address: String?,
    val x_axis: Float?,
    val y_axis: Float?,
    val slope: Int?,
    val auto_door: Boolean?,
    val elevator: Boolean?,
    val toilet: Boolean?
)