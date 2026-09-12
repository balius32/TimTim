package com.example.widget

import android.content.Context
import com.example.domain.util.WidgetUpdater

class AppWidgetUpdater(private val context: Context) : WidgetUpdater {
    override fun updateWidget() {
        try {
            WorkRemainingWidgetProvider.notifyWidgetUpdate(context)
        } catch (e: Exception) {
            // Suppress widget notify errors
        }
    }
}
