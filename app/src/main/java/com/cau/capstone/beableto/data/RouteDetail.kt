package com.cau.capstone.beableto.data

data class RouteDetail (
    var type: Int,
    var time: Int,
    var start: String?,
    var end: String?,
    var transfer_id: String?,
    var elevator: String?,
    var bus_height: Int?,
    var bus_area: String?
)