package com.example.myfinalprojectof3213.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinalprojectof3213.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())

    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadDashboard(keypass: String, forceRefresh: Boolean = false) {
        val currentState = _uiState.value

        if (currentState.isLoading) {
            return
        }

        if (currentState.hasLoaded && !forceRefresh) {
            return
        }

        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                val dashboard = dashboardRepository.getDashboard(keypass)

                _uiState.value = DashboardUiState(
                    entities = dashboard.entities,
                    entityTotal = dashboard.entityTotal,
                    hasLoaded = true
                )
            } catch (exception: IOException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message
                        ?: "Unable to load the dashboard. Please try again."
                )
            }
        }
    }
}