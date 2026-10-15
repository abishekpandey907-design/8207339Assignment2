package com.example.myfinalprojectof3213.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinalprojectof3213.data.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        if (_uiState.value.isLoading) {
            return
        }

        val studentId = username.trim()

        if (studentId.isEmpty() || password.isBlank()) {
            _uiState.value = LoginUiState(
                errorMessage = "Enter your student ID and first name."
            )
            return
        }

        if (!studentId.all { it in '0'..'9' }) {
            _uiState.value = LoginUiState(
                errorMessage = "Enter your student ID using digits only, without s."
            )
            return
        }

        _uiState.value = LoginUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val keypass = loginRepository.login(
                    username = studentId,
                    password = password
                )

                _uiState.value = LoginUiState(keypass = keypass)
            } catch (exception: IOException) {
                _uiState.value = LoginUiState(
                    errorMessage = exception.message
                        ?: "Unable to log in. Please try again."
                )
            }
        }
    }

    fun clearLoginResult() {
        _uiState.value = LoginUiState()
    }
}