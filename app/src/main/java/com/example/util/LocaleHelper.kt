package com.example.util

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.MainApplication
import java.util.Locale

object LocaleHelper {
    private const val PREFS_NAME = "app_preferences"
    private const val KEY_LANGUAGE = "app_language"

    @Volatile
    var currentLanguage: String = "SYSTEM"
        private set

    fun getSavedLanguage(context: Context): String {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getString(KEY_LANGUAGE, "SYSTEM") ?: "SYSTEM"
        } catch (e: Exception) {
            "SYSTEM"
        }
    }

    fun getLocaleForLanguage(languageCode: String): Locale {
        return when (languageCode.lowercase().trim()) {
            "fa", "farsi", "persian" -> Locale("fa")
            "en", "english" -> Locale("en")
            else -> {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Resources.getSystem().configuration.locales[0]
                    } else {
                        @Suppress("DEPRECATION")
                        Resources.getSystem().configuration.locale
                    }
                } catch (e: Exception) {
                    Locale.getDefault()
                }
            }
        }
    }

    fun applyLanguage(languageCode: String, context: Context? = null) {
        currentLanguage = languageCode
        val targetLocale = getLocaleForLanguage(languageCode)
        Locale.setDefault(targetLocale)

        val ctx = context ?: try { MainApplication.instance } catch (e: Exception) { null }

        // Persist to SharedPreferences for cold starts
        ctx?.let {
            try {
                it.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_LANGUAGE, languageCode)
                    .apply()
            } catch (_: Exception) {}
        }

        // Apply via AppCompatDelegate
        val locales = when (languageCode.lowercase().trim()) {
            "fa", "farsi", "persian" -> LocaleListCompat.forLanguageTags("fa")
            "en", "english" -> LocaleListCompat.forLanguageTags("en")
            else -> LocaleListCompat.getEmptyLocaleList()
        }
        try {
            AppCompatDelegate.setApplicationLocales(locales)
        } catch (_: Exception) {}

        // Update application and activity resources
        ctx?.let { c ->
            try {
                val config = Configuration(c.resources.configuration)
                config.setLocale(targetLocale)
                config.setLayoutDirection(targetLocale)
                @Suppress("DEPRECATION")
                c.resources.updateConfiguration(config, c.resources.displayMetrics)

                val appCtx = c.applicationContext
                if (appCtx != null && appCtx !== c) {
                    val appConfig = Configuration(appCtx.resources.configuration)
                    appConfig.setLocale(targetLocale)
                    appConfig.setLayoutDirection(targetLocale)
                    @Suppress("DEPRECATION")
                    appCtx.resources.updateConfiguration(appConfig, appCtx.resources.displayMetrics)
                }
            } catch (_: Exception) {}
        }
    }

    fun getLocalizedContext(context: Context, appLanguage: String? = null): Context {
        val lang = if (!appLanguage.isNullOrBlank()) appLanguage else getSavedLanguage(context)
        val targetLocale = getLocaleForLanguage(lang)
        Locale.setDefault(targetLocale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(targetLocale)
        config.setLayoutDirection(targetLocale)
        return context.createConfigurationContext(config)
    }

    fun isFarsi(context: Context? = null): Boolean {
        if (context != null) {
            try {
                val ctxLoc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.resources.configuration.locales[0]
                } else {
                    @Suppress("DEPRECATION")
                    context.resources.configuration.locale
                }
                if (ctxLoc?.language.equals("fa", ignoreCase = true)) return true
                if (ctxLoc?.language.equals("en", ignoreCase = true)) return false
            } catch (_: Exception) {}
        }
        return isFarsi(null, context)
    }

    fun isFarsi(appLanguage: String? = null, context: Context? = null): Boolean {
        if (appLanguage != null) {
            when (appLanguage.lowercase().trim()) {
                "fa", "farsi", "persian" -> return true
                "en", "english" -> return false
                "system" -> {
                    return try {
                        val sysLoc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Resources.getSystem().configuration.locales[0]
                        } else {
                            @Suppress("DEPRECATION")
                            Resources.getSystem().configuration.locale
                        }
                        sysLoc.language.equals("fa", ignoreCase = true)
                    } catch (e: Exception) {
                        Locale.getDefault().language.equals("fa", ignoreCase = true)
                    }
                }
            }
        }
        val saved = context?.let { getSavedLanguage(it) } ?: currentLanguage
        if (!saved.equals("SYSTEM", ignoreCase = true)) {
            when (saved.lowercase().trim()) {
                "fa", "farsi", "persian" -> return true
                "en", "english" -> return false
            }
        }
        return try {
            val sysLoc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources.getSystem().configuration.locales[0]
            } else {
                @Suppress("DEPRECATION")
                Resources.getSystem().configuration.locale
            }
            sysLoc.language.equals("fa", ignoreCase = true)
        } catch (e: Exception) {
            Locale.getDefault().language.equals("fa", ignoreCase = true)
        }
    }
}
