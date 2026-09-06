package com.example.ui.mvi

import com.example.domain.model.AppSettings
import com.example.domain.model.DaySummary
import com.example.domain.model.WorkCalculationSummary
import com.example.domain.model.WorkDay
import com.example.util.CalendarHelper
import com.example.util.CalendarType
import com.example.util.CurrentDate
import java.time.LocalDate

enum class TodayPromptType {
    ENTER_NOW,
    EXIT_NOW
}

data class TodayQuickPrompt(
    val type: TodayPromptType,
    val todayEntity: WorkDay,
    val enterTimeFormatted: String = ""
)

data class UiControlState(
    val currentScreen: AppScreen = AppScreen.TIMESHEET,
    val userName: String = "username",
    val avatarId: String = "minimal_avatar",
    val currentTab: NavigationTab = NavigationTab.TIMESHEET,
    val selectedYear: Int = 0,
    val selectedMonth: Int = 0,
    val reportYear: Int = 0,
    val reportMonth: Int = 0,
    val selectedDayForTimePick: WorkDay? = null,
    val selectedRemainingTimeDay: WorkDay? = null,
    val isPickingEnterTime: Boolean = true,
    val showTimePickerDialog: Boolean = false,
    val showResetConfirmation: Boolean = false,
    val showSettingsSheet: Boolean = false,
    val isTodayPromptDismissed: Boolean = false,
    val isLoading: Boolean = false,
    val isInitialized: Boolean = false
)

data class WorkUiState(
    val days: List<WorkDay> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val summary: WorkCalculationSummary = WorkCalculationSummary(),
    val reportSummary: WorkCalculationSummary = WorkCalculationSummary(),
    val currentScreen: AppScreen = AppScreen.TIMESHEET,
    val userName: String = "username",
    val avatarId: String = "minimal_avatar",
    val currentTab: NavigationTab = NavigationTab.TIMESHEET,
    val selectedYear: Int = 0,
    val selectedMonth: Int = 0,
    val reportYear: Int = 0,
    val reportMonth: Int = 0,
    val todayEntity: WorkDay? = null,
    val selectedDayForTimePick: WorkDay? = null,
    val selectedRemainingTimeDay: WorkDay? = null,
    val isPickingEnterTime: Boolean = true,
    val showTimePickerDialog: Boolean = false,
    val showResetConfirmation: Boolean = false,
    val showSettingsSheet: Boolean = false,
    val isTodayPromptDismissed: Boolean = false,
    val isLoading: Boolean = false,
    val isReady: Boolean = false
) {
    val calendarType: CalendarType
        get() = CalendarHelper.parseCalendarType(settings.calendarType)

    val currentCalendarNow: CurrentDate
        get() = CalendarHelper.now(calendarType)

    val effectiveSelectedYear: Int
        get() = if (selectedYear != 0) selectedYear else currentCalendarNow.year

    val effectiveSelectedMonth: Int
        get() = if (selectedMonth != 0) selectedMonth else currentCalendarNow.month

    val totalDaysInCurrentMonth: Int
        get() = CalendarHelper.getDaysInMonth(effectiveSelectedYear, effectiveSelectedMonth, calendarType)

    val monthName: String
        get() = CalendarHelper.getMonthName(effectiveSelectedMonth, calendarType)

    val reportMonthName: String
        get() = CalendarHelper.getMonthName(if (reportMonth != 0) reportMonth else currentCalendarNow.month, calendarType)

    val currentCalendarMonthName: String
        get() = CalendarHelper.getMonthName(currentCalendarNow.month, calendarType)

    val totalDaysInReportMonth: Int
        get() = CalendarHelper.getDaysInMonth(if (reportYear != 0) reportYear else currentCalendarNow.year, if (reportMonth != 0) reportMonth else currentCalendarNow.month, calendarType)

    val formattedYearMonth: String
        get() {
            val yy = (effectiveSelectedYear % 100).toString().padStart(2, '0')
            val mm = effectiveSelectedMonth.toString().padStart(2, '0')
            return "$yy/$mm"
        }

    val formattedFullYearMonth: String
        get() {
            val mm = effectiveSelectedMonth.toString().padStart(2, '0')
            return "$effectiveSelectedYear/$mm"
        }

    val formattedMonthHeader: String
        get() = "$monthName $effectiveSelectedYear"

    val formattedReportMonthHeader: String
        get() = "$reportMonthName ${if (reportYear != 0) reportYear else currentCalendarNow.year}"

    val backToTodayLabel: String
        get() = "Back to Today ($currentCalendarMonthName ${currentCalendarNow.year})"

    fun formattedMonthDay(dayNumber: Int): String {
        val mm = effectiveSelectedMonth.toString().padStart(2, '0')
        val dd = dayNumber.toString().padStart(2, '0')
        return "$mm/$dd"
    }

    fun getDayOfWeekLabel(dayNumber: Int): String {
        return CalendarHelper.getDayOfWeekLabel(effectiveSelectedYear, effectiveSelectedMonth, dayNumber, calendarType)
    }

    fun getShortDayOfWeekLabel(dayNumber: Int): String {
        return CalendarHelper.getShortDayOfWeekLabel(effectiveSelectedYear, effectiveSelectedMonth, dayNumber, calendarType)
    }

    val isCurrentMonth: Boolean
        get() {
            if (selectedYear == 0 || selectedMonth == 0) return true
            val now = currentCalendarNow
            return now.year == selectedYear && now.month == selectedMonth
        }

    val currentDayOfMonth: Int?
        get() {
            return try {
                val now = currentCalendarNow
                if (isCurrentMonth) {
                    now.day.coerceIn(1, totalDaysInCurrentMonth)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

    val todaySummary: DaySummary?
        get() {
            val dayNum = currentDayOfMonth ?: return null
            return summary.daySummaries.firstOrNull { it.day.dayNumber == dayNum }
        }

    val quickTodayPrompt: TodayQuickPrompt?
        get() {
            if (isTodayPromptDismissed) return null
            val entity = todayEntity ?: return null
            if (entity.isDayOff) return null

            return if (entity.enterHour == null) {
                TodayQuickPrompt(TodayPromptType.ENTER_NOW, entity)
            } else if (entity.exitHour == null) {
                TodayQuickPrompt(TodayPromptType.EXIT_NOW, entity, entity.formattedEnterTime())
            } else {
                null
            }
        }
}
