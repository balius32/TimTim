package com.example.ui.mvi

import com.example.domain.model.WorkDay

sealed interface WorkUiIntent {
    // Navigation Intents
    data class NavigateTo(val screen: AppScreen) : WorkUiIntent
    data class SelectTab(val tab: NavigationTab) : WorkUiIntent

    // Month Navigation Intents
    data object PreviousMonth : WorkUiIntent
    data object NextMonth : WorkUiIntent
    data class SetSelectedYearMonth(val year: Int, val month: Int) : WorkUiIntent
    data object GoToCurrentMonth : WorkUiIntent

    // Report Month Navigation Intents
    data object PreviousReportMonth : WorkUiIntent
    data object NextReportMonth : WorkUiIntent
    data class SetReportYearMonth(val year: Int, val month: Int) : WorkUiIntent

    // Time Logging Intents
    data class OpenTimePicker(val day: WorkDay, val isEnter: Boolean) : WorkUiIntent
    data object DismissTimePicker : WorkUiIntent
    data class ConfirmTime(val hour: Int, val minute: Int) : WorkUiIntent
    data class SetTimeToNow(val day: WorkDay, val isEnter: Boolean) : WorkUiIntent
    data class ClearEnterTime(val dayNumber: Int) : WorkUiIntent
    data class ClearExitTime(val dayNumber: Int) : WorkUiIntent
    data class ClearDay(val dayNumber: Int) : WorkUiIntent
    data class ToggleDayOff(val dayNumber: Int) : WorkUiIntent

    // Quick Action (Today) Banner Intents
    data object LogTodayEnterNow : WorkUiIntent
    data object LogTodayExitNow : WorkUiIntent
    data object DismissTodayPrompt : WorkUiIntent
    data class OpenTodayTimePicker(val isEnter: Boolean) : WorkUiIntent

    // User Profile & Settings Intents
    data class UpdateUserName(val name: String) : WorkUiIntent
    data class UpdateAvatar(val avatarId: String) : WorkUiIntent
    data class UpdateThemeMode(val themeMode: String) : WorkUiIntent
    data class UpdateCalendarType(val calendarType: String) : WorkUiIntent
    data class UpdateLanguage(val language: String) : WorkUiIntent
    data class UpdateOffDaysOfWeek(val offDaysString: String) : WorkUiIntent
    data class UpdateDailyRequiredTime(val hours: Int, val minutes: Int) : WorkUiIntent
    data class UpdateDailyLimits(val minMinutes: Int?, val maxMinutes: Int?) : WorkUiIntent
    data class UpdateMinDailyLimit(val minutes: Int?) : WorkUiIntent
    data class UpdateMaxDailyLimit(val minutes: Int?) : WorkUiIntent
    data class UpdateEnterExitLimits(val minEnterMinutes: Int?, val maxExitMinutes: Int?) : WorkUiIntent
    data class UpdateMinEnterTime(val minutes: Int?) : WorkUiIntent
    data class UpdateMaxExitTime(val minutes: Int?) : WorkUiIntent
    data class UpdateMonthDailyTarget(val year: Int, val month: Int, val hours: Int, val minutes: Int) : WorkUiIntent
    data class UpdateSelectedMonthDailyTarget(val hours: Int, val minutes: Int) : WorkUiIntent
    data class UpdateReportMonthDailyTarget(val hours: Int, val minutes: Int) : WorkUiIntent

    // Dialog & UI Control Intents
    data class ShowResetConfirmation(val show: Boolean) : WorkUiIntent
    data object ConfirmResetAll : WorkUiIntent
    data class ToggleSettingsSheet(val show: Boolean) : WorkUiIntent
    data object ClearAllData : WorkUiIntent
    data class ImportBackupData(val jsonString: String, val onComplete: (Boolean, String) -> Unit = { _, _ -> }) : WorkUiIntent

    // Onboarding Intents
    data object CompleteOnboarding : WorkUiIntent
    data object SkipOnboarding : WorkUiIntent
}

enum class AppScreen {
    ONBOARDING,
    TIMESHEET,
    PROFILE,
    SETTINGS,
    REPORT,
    REMAINING_TIME
}


enum class NavigationTab {
    TIMESHEET,
    RESULTS
}
