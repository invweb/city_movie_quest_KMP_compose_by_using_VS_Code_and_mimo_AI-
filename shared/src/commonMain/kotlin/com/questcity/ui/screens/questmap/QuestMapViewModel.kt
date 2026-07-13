package com.questcity.ui.screens.questmap

import com.questcity.domain.geo.GeoUtils
import com.questcity.domain.model.Location
import com.questcity.domain.model.Quest
import com.questcity.domain.repository.QuestRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapState(
    val quest: Quest? = null,
    val locations: List<Location> = emptyList(),
    val userLat: Double? = null,
    val userLon: Double? = null,
    val selectedLocationIndex: Int? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class QuestMapViewModel(
    private val questRepository: QuestRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    fun loadQuest(questId: String) {
        scope.launch {
            try {
                val quest = questRepository.getQuestById(questId)
                val locations = questRepository.getLocationsForQuest(questId)
                _state.value = _state.value.copy(
                    quest = quest,
                    locations = locations,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateUserLocation(lat: Double, lon: Double) {
        _state.value = _state.value.copy(userLat = lat, userLon = lon)
    }

    fun onLocationSelected(index: Int) {
        _state.value = _state.value.copy(selectedLocationIndex = index)
    }

    fun isUserNearSelectedLocation(): Boolean {
        val state = _state.value
        val userLat = state.userLat ?: return false
        val userLon = state.userLon ?: return false
        val selectedIndex = state.selectedLocationIndex ?: return false
        val location = state.locations.getOrNull(selectedIndex) ?: return false

        return GeoUtils.isUserInRadius(userLat, userLon, location.lat, location.lon, location.radiusM)
    }
}
