package com.questcity.ui.questmap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.questcity.domain.model.Location

@Composable
expect fun PlatformMap(
    locations: List<Location>,
    userLat: Double?,
    userLon: Double?,
    selectedLocationIndex: Int?,
    onLocationSelected: (Int) -> Unit,
    modifier: Modifier
)
