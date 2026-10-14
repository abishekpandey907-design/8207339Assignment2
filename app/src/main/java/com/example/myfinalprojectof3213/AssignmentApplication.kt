package com.example.myfinalprojectof3213

import android.app.Application
import com.example.myfinalprojectof3213.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AssignmentApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AssignmentApplication)
            modules(appModule)
        }
    }
}