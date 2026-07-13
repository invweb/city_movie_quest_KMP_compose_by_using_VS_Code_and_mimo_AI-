package com.questcity.ui.questmap

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayWithRadius

@Composable
actual fun PlatformMap(
    locations: List<com.questcity.domain.model.Location>,
    userLat: Double?,
    userLon: Double?,
    selectedLocationIndex: Int?,
    onLocationSelected: (Int) -> Unit,
    modifier: Modifier
) {
    AndroidView(
        factory = { context ->
            Configuration.getInstance().userAgentValue = context.packageName
            val mapView = MapView(context)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            mapView.controller.setZoom(15.0)

            locations.forEachIndexed { index, location ->
                val point = GeoPoint(location.lat, location.lon)
                val marker = Marker(mapView)
                marker.position = point
                marker.title = "Локация ${index + 1}"
                marker.setOnMarkerClickListener { _, _ ->
                    onLocationSelected(index)
                    true
                }
                mapView.overlays.add(marker)

                val circle = OverlayWithRadius(
                    point,
                    location.radiusM
                )
                mapView.overlays.add(circle)
            }

            if (userLat != null && userLon != null) {
                val userPoint = GeoPoint(userLat, userLon)
                val userMarker = Marker(mapView)
                userMarker.position = userPoint
                userMarker.title = "Вы здесь"
                mapView.overlays.add(userMarker)
                mapView.controller.animateTo(userPoint)
            } else if (locations.isNotEmpty()) {
                val firstLocation = locations.first()
                mapView.controller.animateTo(GeoPoint(firstLocation.lat, firstLocation.lon))
            }

            mapView
        },
        modifier = modifier
    )
}
