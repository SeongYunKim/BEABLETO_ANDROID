package com.cau.capstone.beableto.data

data class SubPath(
    val type: String,
    val time: Time?,
    val walk_start_x: Float?,
    val walk_start_y: Float?,
    val walk_end_x: Float?,
    val walk_end_y: Float?,
    val walk_seq: List<WalkSequence>,
    val walk_sub: String?,
    val bus_start_x: Float?,
    val bus_start_y: Float?,
    val bus_end_x: Float?,
    val bus_end_y: Float?,
    val bus_line: String?,
    val bus_area: String?,
    val bus_height: Int?,
    val bus_poly: String?,
    val train_start_x: Float?,
    val train_start_y: Float?,
    val train_end_x: Float?,
    val train_end_y: Float?,
    val train_line: String?,
    val train_poly: String?,
    val color: String?,
    val departure: String?,
    val arrival: String?
)