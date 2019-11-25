package com.cau.capstone.beableto.data

import java.io.Serializable

data class Route(
    var time: Int?,
    var walk_time: Int?,
    var bus: Boolean,
    var subway: Boolean,
    var route_detail_list: ArrayList<RouteDetail>
) : Serializable