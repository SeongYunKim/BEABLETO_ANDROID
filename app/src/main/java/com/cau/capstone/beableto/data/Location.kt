package com.cau.capstone.beableto.data

data class Location (
    val latitude: Float,
    val longitude: Float,
    val address: String,
    val name: String,
    val rate: Float?,
    val place_id: String,
    val types: List<String>?
)