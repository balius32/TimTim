package com.example

import android.app.Application
import com.example.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (org.koin.core.context.GlobalContext.getOrNull() == null) {
            startKoin {
                androidLogger()
                androidContext(this@MainApplication)
                modules(appModule)
            }
        }
    }
}
