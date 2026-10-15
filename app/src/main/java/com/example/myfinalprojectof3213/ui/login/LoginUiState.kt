package com.example.myfinalprojectof3213.ui.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val keypass: String? = null
)