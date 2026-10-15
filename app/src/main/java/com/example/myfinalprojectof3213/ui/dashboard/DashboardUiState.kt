package com.example.myfinalprojectof3213.ui.dashboard

import com.example.myfinalprojectof3213.data.model.DashboardEntity

data class DashboardUiState(
    val isLoading: Boolean = false,
    val entities: List<DashboardEntity> = emptyList(),
    val entityTotal: Int = 0,
    val errorMessage: String? = null,
    val hasLoaded: Boolean = false
)