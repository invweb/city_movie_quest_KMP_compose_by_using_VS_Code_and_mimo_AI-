package com.questcity.ui.questmap

import com.questcity.domain.model.Location

interface MapAdapter {
    fun addMarker(lat: Double, lon: Double, title: String, snippet: String?)
    fun addCircle(lat: Double, lon: Double, radiusM: Double, color: Int)
    fun clear()
    fun animateTo(lat: Double, lon: Double, zoom: Double)
}
