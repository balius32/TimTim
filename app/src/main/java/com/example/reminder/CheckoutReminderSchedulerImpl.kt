package com.example.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.WorkDao
import com.example.data.mapper.toDomain
import com.example.domain.model.checkoutReminderFire
import com.example.domain.reminder.CheckoutReminderScheduler
import com.example.ui.theme.ACCENT_COLOR_OPTIONS
import com.example.ui.theme.parseThemeSettings
import com.example.util.CalendarHelper
import com.example.util.LocaleHelper
import com.example.util.NumberFormatter
import androidx.compose.ui.graphics.toArgb
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

class CheckoutReminderSchedulerImpl(
    private val context: Context,
    private val workDao: WorkDao
) : CheckoutReminderScheduler {

    override suspend fun syncToday() {
        cancelAlarm()
        cancelNotification()

        val settings = workDao.getSettingsDirect() ?: return
        if (!settings.checkoutReminderEnabled) return

        val calendarType = CalendarHelper.parseCalendarType(settings.calendarType)
        val today = CalendarHelper.now(calendarType)
        val day = workDao.getDay(today.year, today.month, today.day) ?: return
        if (day.isDayOff || day.enterHour == null || day.enterMinute == null) return
        if (day.exitHour != null && day.exitMinute != null) return

        val targetMinutes = settings.dailyRequiredMinutes.takeIf { it > 0 } ?: DEFAULT_TARGET_MINUTES
        val fire = checkoutReminderFire(day.enterHour, day.enterMinute, targetMinutes)
        val triggerAtMillis = LocalDate.now()
            .plusDays(fire.dayOffset.toLong())
            .atTime(fire.hour, fire.minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        if (triggerAtMillis <= System.currentTimeMillis()) return

        scheduleExact(triggerAtMillis)
    }

    override suspend fun showFiredReminder() {
        val settings = workDao.getSettingsDirect() ?: return
        if (!settings.checkoutReminderEnabled) return

        val calendarType = CalendarHelper.parseCalendarType(settings.calendarType)
        val today = CalendarHelper.now(calendarType)
        val dayEntity = workDao.getDay(today.year, today.month, today.day) ?: return
        if (dayEntity.isDayOff || dayEntity.enterHour == null || dayEntity.enterMinute == null) return
        if (dayEntity.exitHour != null && dayEntity.exitMinute != null) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val targetMinutes = settings.dailyRequiredMinutes.takeIf { it > 0 } ?: DEFAULT_TARGET_MINUTES
        val checkoutMinutes = dayEntity.toDomain().estimatedCheckoutMinutes(targetMinutes) ?: return
        val rawTime = String.format(
            Locale.US,
            "%02d:%02d",
            checkoutMinutes / 60,
            checkoutMinutes % 60
        )
        val shownTime = if (LocaleHelper.isFarsi(settings.appLanguage)) {
            NumberFormatter.toFarsiDigits(rawTime)
        } else {
            rawTime
        }

        val localized = LocaleHelper.getLocalizedContext(context, settings.appLanguage)
        ensureChannel(localized)
        val openApp = PendingIntent.getActivity(
            context,
            REQUEST_CODE_CONTENT,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_checkout)
            .setContentTitle(localized.getString(R.string.settings_remind_check_out))
            .setContentText(localized.getString(R.string.notif_checkout_reminder_body, shownTime))
            .setColor(accentColorArgb(settings.themeMode))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openApp)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun scheduleExact(triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pending = alarmPendingIntent()
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        if (canExact) {
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
                return
            } catch (_: SecurityException) {
                // Exact access was revoked between the check and the schedule call.
            }
        }
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
    }

    private fun cancelAlarm() {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(alarmPendingIntent())
    }

    private fun cancelNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    private fun alarmPendingIntent(): PendingIntent {
        val intent = Intent(context, CheckoutReminderReceiver::class.java).apply {
            action = CheckoutReminderIntents.ACTION_FIRE
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun ensureChannel(localized: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            localized.getString(R.string.notif_checkout_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = localized.getString(R.string.settings_remind_check_out_desc)
        }
        manager.createNotificationChannel(channel)
    }

    private fun accentColorArgb(themeMode: String): Int {
        val (mode, colorId) = parseThemeSettings(themeMode)
        val accent = ACCENT_COLOR_OPTIONS.firstOrNull { it.id.equals(colorId, ignoreCase = true) }
            ?: ACCENT_COLOR_OPTIONS.first()
        val isDark = when (mode) {
            "DARK" -> true
            "LIGHT" -> false
            else -> {
                val night = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                night == Configuration.UI_MODE_NIGHT_YES
            }
        }
        val color = if (isDark) accent.darkPrimary else accent.color
        return color.toArgb()
    }

    companion object {
        const val CHANNEL_ID = "checkout_reminder"
        private const val NOTIFICATION_ID = 4105
        private const val REQUEST_CODE_ALARM = 7101
        private const val REQUEST_CODE_CONTENT = 7102
        private const val DEFAULT_TARGET_MINUTES = 480
    }
}
