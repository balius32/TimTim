package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.ActivityOptionsCompat
import com.example.MainActivity
import com.example.R
import com.example.domain.model.WorkDay
import com.example.domain.repository.WorkRepository
import com.example.util.CalendarHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar
import java.util.Locale

class WorkRemainingWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val repository: WorkRepository by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action ?: return

        when (action) {
            ACTION_REFRESH_WIDGET -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, WorkRemainingWidgetProvider::class.java)
                )
                for (id in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, id)
                }
            }
            ACTION_CLOCK_IN -> {
                scope.launch {
                    try {
                        val settings = repository.getSettingsDirect()
                        val calType = CalendarHelper.parseCalendarType(settings.calendarType)
                        val now = CalendarHelper.now(calType)
                        repository.initializeMonthIfEmpty(now.year, now.month)
                        
                        val calendar = Calendar.getInstance()
                        var hour = calendar.get(Calendar.HOUR_OF_DAY)
                        var minute = calendar.get(Calendar.MINUTE)
                        val currentMins = hour * 60 + minute
                        
                        val minEnter = settings.minEnterMinutes
                        if (minEnter != null && minEnter > 0 && currentMins < minEnter) {
                            hour = minEnter / 60
                            minute = minEnter % 60
                        }
                        repository.setEnterTime(now.year, now.month, now.day, hour, minute)
                        
                        // Update widget
                        notifyWidgetUpdate(context)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            ACTION_CLOCK_OUT -> {
                scope.launch {
                    try {
                        val settings = repository.getSettingsDirect()
                        val calType = CalendarHelper.parseCalendarType(settings.calendarType)
                        val now = CalendarHelper.now(calType)
                        repository.initializeMonthIfEmpty(now.year, now.month)
                        
                        val calendar = Calendar.getInstance()
                        var hour = calendar.get(Calendar.HOUR_OF_DAY)
                        var minute = calendar.get(Calendar.MINUTE)
                        val currentMins = hour * 60 + minute
                        
                        val maxExit = settings.maxExitMinutes
                        if (maxExit != null && maxExit > 0 && currentMins > maxExit) {
                            hour = maxExit / 60
                            minute = maxExit % 60
                        }
                        repository.setExitTime(now.year, now.month, now.day, hour, minute)
                        
                        // Update widget
                        notifyWidgetUpdate(context)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        scope.launch {
            try {
                val views = RemoteViews(context.packageName, R.layout.widget_remaining_time)

                // 1. Setup Click on Widget Root to open MainActivity with smooth launch options
                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val animOptions = ActivityOptionsCompat.makeCustomAnimation(
                    context,
                    R.anim.widget_open_enter,
                    R.anim.widget_open_exit
                )
                val pendingMainIntent = PendingIntent.getActivity(
                    context,
                    0,
                    mainIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    animOptions.toBundle()
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingMainIntent)

                // 2. Setup Refresh button
                val refreshIntent = Intent(context, WorkRemainingWidgetProvider::class.java).apply {
                    this.action = ACTION_REFRESH_WIDGET
                }
                val pendingRefreshIntent = PendingIntent.getBroadcast(
                    context,
                    1,
                    refreshIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_btn_refresh, pendingRefreshIntent)

                // 3. Load today's work data
                val settings = repository.getSettingsDirect()
                val calType = CalendarHelper.parseCalendarType(settings.calendarType)
                val now = CalendarHelper.now(calType)
                val today = repository.getDayDirect(now.year, now.month, now.day)
                val targetMinutes = if (settings.dailyRequiredMinutes > 0) settings.dailyRequiredMinutes else 480

                bindWidgetData(context, views, today, targetMinutes)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun bindWidgetData(
        context: Context,
        views: RemoteViews,
        today: WorkDay?,
        targetMinutes: Int
    ) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTotalMinutes = currentHour * 60 + currentMinute

        if (today == null || (!today.hasEnterTime && !today.isDayOff)) {
            // Case 1: Not clocked in yet today
            views.setTextViewText(R.id.widget_timer_value, "--h --m")
            views.setTextViewText(R.id.widget_timer_subtitle, "Not clocked in yet today")
            views.setProgressBar(R.id.widget_progress_bar, 100, 0, false)
            views.setTextViewText(R.id.widget_info_left, "Daily Goal: ${targetMinutes / 60}h ${targetMinutes % 60}m")
            views.setTextViewText(R.id.widget_info_right, "Ready to start work")

            // Show Clock-In Quick Action button
            views.setViewVisibility(R.id.widget_btn_action, View.VISIBLE)
            views.setTextViewText(R.id.widget_btn_action, "Clock In")
            val clockInIntent = Intent(context, WorkRemainingWidgetProvider::class.java).apply {
                this.action = ACTION_CLOCK_IN
            }
            val pendingClockIn = PendingIntent.getBroadcast(
                context,
                2,
                clockInIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_action, pendingClockIn)
            return
        }

        if (today.isDayOff) {
            // Case 2: Day Off
            views.setTextViewText(R.id.widget_timer_value, "Day Off")
            views.setTextViewText(R.id.widget_timer_subtitle, "Scheduled day off")
            views.setProgressBar(R.id.widget_progress_bar, 100, 100, false)
            views.setTextViewText(R.id.widget_info_left, "Enjoy your rest!")
            views.setTextViewText(R.id.widget_info_right, "")
            views.setViewVisibility(R.id.widget_btn_action, View.GONE)
            return
        }

        val enterHour = today.enterHour ?: currentHour
        val enterMinute = today.enterMinute ?: currentMinute
        val enterTotalMinutes = enterHour * 60 + enterMinute

        // Target departure time
        val expectedExitTotalMinutes = enterTotalMinutes + targetMinutes
        val expectedExitHour = (expectedExitTotalMinutes / 60) % 24
        val expectedExitMinute = expectedExitTotalMinutes % 60
        val formattedExpectedExit = String.format(Locale.getDefault(), "%02d:%02d", expectedExitHour, expectedExitMinute)
        val formattedEnter = String.format(Locale.getDefault(), "%02d:%02d", enterHour, enterMinute)

        if (today.hasExitTime) {
            // Case 3: Shift is already completed (both enter and exit recorded)
            val workedMinutes = today.workedMinutes
            val workedH = workedMinutes / 60
            val workedM = workedMinutes % 60
            val diff = workedMinutes - targetMinutes

            views.setTextViewText(R.id.widget_timer_value, "${workedH}h ${workedM}m")
            val statusText = if (diff >= 0) {
                "Shift completed (+${diff / 60}h ${diff % 60}m overtime)"
            } else {
                val deficit = -diff
                "Shift completed (-${deficit / 60}h ${deficit % 60}m deficit)"
            }
            views.setTextViewText(R.id.widget_timer_subtitle, statusText)

            val progressPercent = ((workedMinutes.toFloat() / targetMinutes.toFloat()) * 100).toInt().coerceIn(0, 100)
            views.setProgressBar(R.id.widget_progress_bar, 100, progressPercent, false)

            val exitHour = today.exitHour ?: 0
            val exitMinute = today.exitMinute ?: 0
            val formattedExit = String.format(Locale.getDefault(), "%02d:%02d", exitHour, exitMinute)
            views.setTextViewText(R.id.widget_info_left, "In: $formattedEnter  •  Out: $formattedExit")
            views.setTextViewText(R.id.widget_info_right, "Target was $formattedExpectedExit")
            views.setViewVisibility(R.id.widget_btn_action, View.GONE)
            return
        }

        // Case 4: Currently at work (Entered, but hasn't exited yet)
        var elapsedMinutes = currentTotalMinutes - enterTotalMinutes
        if (elapsedMinutes < 0) {
            elapsedMinutes += 24 * 60 // Crossed midnight
        }

        val progressPercent = ((elapsedMinutes.toFloat() / targetMinutes.toFloat()) * 100).toInt().coerceIn(0, 100)
        views.setProgressBar(R.id.widget_progress_bar, 100, progressPercent, false)

        if (elapsedMinutes >= targetMinutes) {
            // Target hours reached! In overtime
            val overtimeMinutes = elapsedMinutes - targetMinutes
            val otH = overtimeMinutes / 60
            val otM = overtimeMinutes % 60
            views.setTextViewText(R.id.widget_timer_value, "+${otH}h ${otM}m")
            views.setTextViewText(R.id.widget_timer_subtitle, "Daily target reached! (Overtime)")
        } else {
            // Remaining time left to exit
            val remainingMinutes = targetMinutes - elapsedMinutes
            val remH = remainingMinutes / 60
            val remM = remainingMinutes % 60
            views.setTextViewText(R.id.widget_timer_value, "${remH}h ${remM}m")
            views.setTextViewText(R.id.widget_timer_subtitle, "Time left to exit")
        }

        views.setTextViewText(R.id.widget_info_left, "Check-in: $formattedEnter")
        views.setTextViewText(R.id.widget_info_right, "Target Exit: $formattedExpectedExit")

        // Offer Quick Clock-Out action button
        views.setViewVisibility(R.id.widget_btn_action, View.VISIBLE)
        views.setTextViewText(R.id.widget_btn_action, "Clock Out")
        val clockOutIntent = Intent(context, WorkRemainingWidgetProvider::class.java).apply {
            this.action = ACTION_CLOCK_OUT
        }
        val pendingClockOut = PendingIntent.getBroadcast(
            context,
            3,
            clockOutIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_btn_action, pendingClockOut)
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.widget.ACTION_REFRESH_WIDGET"
        const val ACTION_CLOCK_IN = "com.example.widget.ACTION_CLOCK_IN"
        const val ACTION_CLOCK_OUT = "com.example.widget.ACTION_CLOCK_OUT"

        /**
         * Helper method to trigger a widget refresh from anywhere in the app
         */
        fun notifyWidgetUpdate(context: Context) {
            val intent = Intent(context, WorkRemainingWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WIDGET
            }
            context.sendBroadcast(intent)
        }
    }
}
