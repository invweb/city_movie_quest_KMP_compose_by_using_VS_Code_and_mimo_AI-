package com.questcity.ui.screens.questmap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.questcity.domain.geo.GeoUtils
import com.questcity.domain.model.Location
import com.questcity.domain.model.Quest
import com.questcity.ui.i18n.Language
import com.questcity.ui.i18n.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestMapScreen(
    state: MapState,
    onLocationSelected: (Int) -> Unit,
    onStartQuestAtLocation: (Int) -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    val language = strings.language

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.quest?.title?.get(language) ?: strings.map) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("\u2190")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.error)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    MapPlaceholder(
                        locations = state.locations,
                        userLat = state.userLat,
                        userLon = state.userLon,
                        selectedLocationIndex = state.selectedLocationIndex,
                        onLocationSelected = onLocationSelected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(16.dp)
                    )

                    LocationList(
                        locations = state.locations,
                        selectedLocationIndex = state.selectedLocationIndex,
                        userLat = state.userLat,
                        userLon = state.userLon,
                        onLocationSelected = onLocationSelected,
                        onStartQuestAtLocation = onStartQuestAtLocation,
                        language = language,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MapPlaceholder(
    locations: List<Location>,
    userLat: Double?,
    userLon: Double?,
    selectedLocationIndex: Int?,
    onLocationSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current

    Card(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { /* map interaction */ }
            ) {
                drawMapPlaceholder(locations, userLat, userLon, selectedLocationIndex)
            }

            if (userLat == null || userLon == null) {
                Text(
                    text = strings.detectingLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun DrawScope.drawMapPlaceholder(
    locations: List<Location>,
    userLat: Double?,
    userLon: Double?,
    selectedLocationIndex: Int?
) {
    if (locations.isEmpty()) return

    val centerX = size.width / 2
    val centerY = size.height / 2
    val scale = 1000f

    locations.forEachIndexed { index, location ->
        val offsetX = centerX + (location.lon * scale).toFloat() % size.width
        val offsetY = centerY - (location.lat * scale).toFloat() % size.height
        val isSelected = index == selectedLocationIndex
        val color = if (isSelected) Color.Red else Color.Blue

        drawCircle(
            color = color,
            radius = if (isSelected) 20f else 12f,
            center = Offset(offsetX, offsetY)
        )

        drawCircle(
            color = color.copy(alpha = 0.3f),
            radius = location.radiusM.toFloat(),
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

@Composable
fun LocationList(
    locations: List<Location>,
    selectedLocationIndex: Int?,
    userLat: Double?,
    userLon: Double?,
    onLocationSelected: (Int) -> Unit,
    onStartQuestAtLocation: (Int) -> Unit,
    language: Language,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(locations) { index, location ->
            val isNear = if (userLat != null && userLon != null) {
                GeoUtils.isUserInRadius(userLat, userLon, location.lat, location.lon, location.radiusM)
            } else {
                false
            }
            val isSelected = index == selectedLocationIndex

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLocationSelected(index) }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${strings.location} ${index + 1}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (isNear) {
                            AssistChip(
                                onClick = { },
                                label = { Text(strings.youAreHere) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${location.tasks.size} ${strings.task.lowercase()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onStartQuestAtLocation(index) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(strings.start)
                        }
                    }
                }
            }
        }
    }
}
