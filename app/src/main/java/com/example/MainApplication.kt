package com.example

import android.app.Application
import com.example.di.appModule
import com.example.domain.reminder.CheckoutReminderScheduler
import com.example.util.LocaleHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    companion object {
        lateinit var instance: MainApplication
            private set

        fun getAppContext(): Application = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val savedLang = LocaleHelper.getSavedLanguage(this)
        LocaleHelper.applyLanguage(savedLang, this)
        if (org.koin.core.context.GlobalContext.getOrNull() == null) {
            startKoin {
                androidLogger()
                androidContext(this@MainApplication)
                modules(appModule)
            }
        }
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            runCatching { GlobalContext.get().get<CheckoutReminderScheduler>().syncToday() }
        }
    }
}
