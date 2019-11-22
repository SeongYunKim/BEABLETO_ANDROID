package com.cau.capstone.beableto.data

import java.io.Serializable

data class Route(
    var time: Int?,
    var bus: Boolean,
    var subway: Boolean
) : Serializable