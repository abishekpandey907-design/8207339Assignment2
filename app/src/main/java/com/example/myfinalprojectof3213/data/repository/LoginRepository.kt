package com.example.myfinalprojectof3213.data.repository

import com.example.myfinalprojectof3213.data.model.LoginRequest
import com.example.myfinalprojectof3213.data.remote.ApiService
import java.io.IOException

class LoginRepository(
    private val apiService: ApiService
) {

    suspend fun login(username: String, password: String): String {
        val request = LoginRequest(
            username = username,
            password = password
        )

        val response = apiService.login(request)

        if (!response.isSuccessful) {
            throw IOException(
                when (response.code()) {
                    400, 401, 403 ->
                        "Login was rejected. Check your student ID and first name."
                    else ->
                        "Login failed (HTTP ${response.code()}). Please try again."
                }
            )
        }

        val keypass = response.body()?.keypass

        if (keypass.isNullOrBlank()) {
            throw IOException("The server returned an empty login key.")
        }

        return keypass
    }
}