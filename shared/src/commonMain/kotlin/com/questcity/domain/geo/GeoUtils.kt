package com.questcity.domain.geo

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object GeoUtils {
    const val EARTH_RADIUS_M = 6_371_000.0
    const val DEFAULT_RADIUS_M = 150.0
    const val GPS_DRIFT_TOLERANCE_M = 30.0
    private val DEG_TO_RAD: Double = PI / 180.0

    fun haversineDistance(userLat: Double, userLon: Double, targetLat: Double, targetLon: Double): Double {
        val dLat = (targetLat - userLat) * DEG_TO_RAD
        val dLon = (targetLon - userLon) * DEG_TO_RAD
        val latRad = userLat * DEG_TO_RAD
        val targetLatRad = targetLat * DEG_TO_RAD

        val a = sin(dLat / 2.0).pow(2) + cos(latRad) * cos(targetLatRad) * sin(dLon / 2.0).pow(2)
        val c = 2.0 * asin(sqrt(a))
        return EARTH_RADIUS_M * c
    }

    fun isUserInRadius(
        userLat: Double,
        userLon: Double,
        targetLat: Double,
        targetLon: Double,
        radiusM: Double = DEFAULT_RADIUS_M
    ): Boolean {
        val distance = haversineDistance(userLat, userLon, targetLat, targetLon)
        return distance <= radiusM + GPS_DRIFT_TOLERANCE_M
    }
}
