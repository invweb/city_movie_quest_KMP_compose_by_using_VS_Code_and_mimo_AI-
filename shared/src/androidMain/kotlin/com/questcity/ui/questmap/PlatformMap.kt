package com.questcity.ui.questmap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.questcity.domain.model.Location

@Composable
actual fun PlatformMap(
    locations: List<Location>,
    userLat: Double?,
    userLon: Double?,
    selectedLocationIndex: Int?,
    onLocationSelected: (Int) -> Unit,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable { /* canvas interaction */ }
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val scale = 2000f

            locations.forEachIndexed { index, location ->
                val offsetX = centerX + (location.lon * scale).toFloat() % size.width
                val offsetY = centerY - (location.lat * scale).toFloat() % size.height
                val isSelected = index == selectedLocationIndex

                drawCircle(
                    color = if (isSelected) Color.Red else Color.Blue,
                    radius = if (isSelected) 20f else 12f,
                    center = Offset(offsetX, offsetY)
                )

                drawCircle(
                    color = if (isSelected) Color.Red.copy(alpha = 0.3f) else Color.Blue.copy(alpha = 0.3f),
                    radius = location.radiusM.toFloat() / 2,
                    center = Offset(offsetX, offsetY)
                )
            }

            if (userLat != null && userLon != null) {
                val userX = centerX + (userLon * scale).toFloat() % size.width
                val userY = centerY - (userLat * scale).toFloat() % size.height
                drawCircle(
                    color = Color.Green,
                    radius = 8f,
                    center = Offset(userX, userY)
                )
            }
        }
    }
}
