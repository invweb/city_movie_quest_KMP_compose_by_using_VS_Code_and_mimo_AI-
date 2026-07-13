package com.questcity.domain.geo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeoUtilsTest {
    @Test
    fun `haversineDistance returns correct distance for known points`() {
        val lat1 = 59.9398
        val lon1 = 30.3146
        val lat2 = 59.9400
        val lon2 = 30.3160

        val distance = GeoUtils.haversineDistance(lat1, lon1, lat2, lon2)

        assertTrue(distance > 0 && distance < 200)
    }

    @Test
    fun `haversineDistance returns zero for same point`() {
        val lat = 59.9398
        val lon = 30.3146

        val distance = GeoUtils.haversineDistance(lat, lon, lat, lon)

        assertEquals(0.0, distance, 0.001)
    }

    @Test
    fun `isUserInRadius returns true when user is within radius`() {
        val userLat = 59.9399
        val userLon = 30.3147
        val targetLat = 59.9398
        val targetLon = 30.3146
        val radiusM = 200.0

        assertTrue(GeoUtils.isUserInRadius(userLat, userLon, targetLat, targetLon, radiusM))
    }

    @Test
    fun `isUserInRadius returns false when user is outside radius`() {
        val userLat = 59.9500
        val userLon = 30.3500
        val targetLat = 59.9398
        val targetLon = 30.3146
        val radiusM = 100.0

        assertFalse(GeoUtils.isUserInRadius(userLat, userLon, targetLat, targetLon, radiusM))
    }

    @Test
    fun `isUserInRadius accounts for GPS drift tolerance`() {
        val userLat = 59.9398
        val userLon = 30.3146
        val targetLat = 59.9398
        val targetLon = 30.3146
        val radiusM = 10.0

        assertTrue(GeoUtils.isUserInRadius(userLat, userLon, targetLat, targetLon, radiusM))
    }
}
