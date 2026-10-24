package com.example.ui.screens

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ui.WorkViewModel
import com.example.ui.mvi.WorkUiIntent

internal fun requestCheckoutReminder(
    context: Context,
    viewModel: WorkViewModel,
    exactAlarmSettingsLauncher: ActivityResultLauncher<Intent>,
    requestNotificationPermission: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestNotificationPermission()
            return
        }
    }
    persistCheckoutReminderIfAlarmsAllowed(context, viewModel, exactAlarmSettingsLauncher)
}

internal fun persistCheckoutReminderIfAlarmsAllowed(
    context: Context,
    viewModel: WorkViewModel,
    exactAlarmSettingsLauncher: ActivityResultLauncher<Intent>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
            val opened = runCatching {
                exactAlarmSettingsLauncher.launch(exactAlarmSettingsIntent(context))
            }.isSuccess
            if (!opened) {
                finishCheckoutReminderEnable(context, viewModel)
            }
            return
        }
    }
    finishCheckoutReminderEnable(context, viewModel)
}

internal fun finishCheckoutReminderEnable(context: Context, viewModel: WorkViewModel) {
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val exactAllowed = alarmManager == null ||
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
        alarmManager.canScheduleExactAlarms()
    viewModel.onIntent(WorkUiIntent.SetCheckoutReminderEnabled(true))
    if (!exactAllowed) {
        viewModel.showSnackbar(context.getString(R.string.msg_checkout_reminder_inexact))
    }
}

internal fun exactAlarmSettingsIntent(context: Context): Intent {
    return Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
}
