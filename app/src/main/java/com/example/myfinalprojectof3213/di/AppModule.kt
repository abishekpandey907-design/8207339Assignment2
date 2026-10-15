package com.example.myfinalprojectof3213.di

import com.example.myfinalprojectof3213.ui.dashboard.DashboardViewModel
import com.example.myfinalprojectof3213.data.remote.ApiService
import com.example.myfinalprojectof3213.data.repository.DashboardRepository
import com.example.myfinalprojectof3213.data.repository.LoginRepository
import com.example.myfinalprojectof3213.ui.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://nit3213apinew.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ApiService> {
        get<Retrofit>().create(ApiService::class.java)
    }

    single<LoginRepository> {
        LoginRepository(apiService = get<ApiService>())
    }

    single<DashboardRepository> {
        DashboardRepository(apiService = get<ApiService>())
    }

    viewModel {
        LoginViewModel(loginRepository = get<LoginRepository>())
    }
    viewModel {
        DashboardViewModel(
            dashboardRepository = get<DashboardRepository>()
        )
    }
}