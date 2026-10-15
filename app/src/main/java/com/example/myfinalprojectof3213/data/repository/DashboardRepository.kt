package com.example.myfinalprojectof3213.data.repository

import com.example.myfinalprojectof3213.data.model.DashboardResponse
import com.example.myfinalprojectof3213.data.remote.ApiService
import java.io.IOException

class DashboardRepository(
    private val apiService: ApiService
) {

    suspend fun getDashboard(keypass: String): DashboardResponse {
        if (keypass.isBlank()) {
            throw IOException("Missing login keypass. Please log in again.")
        }

        val response = apiService.getDashboard(keypass)

        if (!response.isSuccessful) {
            throw IOException(
                "Unable to load dashboard (HTTP ${response.code()}). Please try again."
            )
        }

        return response.body()
            ?: throw IOException("The server returned an empty dashboard response.")
    }
}