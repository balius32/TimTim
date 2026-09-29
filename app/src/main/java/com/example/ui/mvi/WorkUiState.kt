package com.example.ui.mvi

import androidx.annotation.StringRes
import com.example.domain.model.AppSettings
import com.example.domain.model.DaySummary
import com.example.domain.model.WorkCalculationSummary
import com.example.domain.model.WorkDay
import com.example.util.CalendarHelper
import com.example.util.CalendarType
import com.example.util.CurrentDate
import com.example.util.LocaleHelper
import com.example.util.toPersianDigits
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

    val isFarsi: Boolean
        get() = LocaleHelper.isFarsi(settings.appLanguage)

    @get:StringRes
    val monthResId: Int
        get() = CalendarHelper.getMonthResId(effectiveSelectedMonth, calendarType)

    val monthName: String
        get() = CalendarHelper.getMonthName(effectiveSelectedMonth, calendarType, isFarsi = isFarsi)

    @get:StringRes
    val reportMonthResId: Int
        get() = CalendarHelper.getMonthResId(if (reportMonth != 0) reportMonth else currentCalendarNow.month, calendarType)

    val reportMonthName: String
        get() = CalendarHelper.getMonthName(if (reportMonth != 0) reportMonth else currentCalendarNow.month, calendarType, isFarsi = isFarsi)

    @get:StringRes
    val currentCalendarMonthResId: Int
        get() = CalendarHelper.getMonthResId(currentCalendarNow.month, calendarType)

    val currentCalendarMonthName: String
        get() = CalendarHelper.getMonthName(currentCalendarNow.month, calendarType, isFarsi = isFarsi)

    val totalDaysInReportMonth: Int
        get() = CalendarHelper.getDaysInMonth(if (reportYear != 0) reportYear else currentCalendarNow.year, if (reportMonth != 0) reportMonth else currentCalendarNow.month, calendarType)

    val formattedYearMonth: String
        get() {
            val yy = (effectiveSelectedYear % 100).toString().padStart(2, '0')
            val mm = effectiveSelectedMonth.toString().padStart(2, '0')
            val raw = "$yy/$mm"
            return if (isFarsi) raw.toPersianDigits() else raw
        }

    val formattedFullYearMonth: String
        get() {
            val mm = effectiveSelectedMonth.toString().padStart(2, '0')
            val raw = "$effectiveSelectedYear/$mm"
            return if (isFarsi) raw.toPersianDigits() else raw
        }

    val formattedMonthHeader: String
        get() {
            val y = if (isFarsi) effectiveSelectedYear.toString().toPersianDigits() else effectiveSelectedYear.toString()
            return "$monthName $y"
        }

    val formattedReportMonthHeader: String
        get() {
            val yVal = if (reportYear != 0) reportYear else currentCalendarNow.year
            val y = if (isFarsi) yVal.toString().toPersianDigits() else yVal.toString()
            return "$reportMonthName $y"
        }

    val backToTodayLabel: String
        get() {
            val y = if (isFarsi) currentCalendarNow.year.toString().toPersianDigits() else currentCalendarNow.year.toString()
            return if (isFarsi) "بازگشت به امروز ($currentCalendarMonthName $y)"
            else "Back to Today ($currentCalendarMonthName $y)"
        }

    fun formattedMonthDay(dayNumber: Int): String {
        val mm = effectiveSelectedMonth.toString().padStart(2, '0')
        val dd = dayNumber.toString().padStart(2, '0')
        val raw = "$mm/$dd"
        return if (isFarsi) raw.toPersianDigits() else raw
    }

    fun formattedFullDate(dayNumber: Int): String {
        val raw = "$effectiveSelectedYear/$effectiveSelectedMonth/$dayNumber"
        return if (isFarsi) raw.toPersianDigits() else raw
    }

    fun getDayOfWeekLabel(dayNumber: Int, context: android.content.Context? = null): String {
        return CalendarHelper.getDayOfWeekLabel(effectiveSelectedYear, effectiveSelectedMonth, dayNumber, calendarType, context, isFarsi = isFarsi)
    }

    fun getShortDayOfWeekLabel(dayNumber: Int, context: android.content.Context? = null): String {
        return CalendarHelper.getShortDayOfWeekLabel(effectiveSelectedYear, effectiveSelectedMonth, dayNumber, calendarType, context, isFarsi = isFarsi)
    }

    @androidx.annotation.StringRes
    fun getDayOfWeekResId(dayNumber: Int): Int {
        val dow = CalendarHelper.getDayOfWeek(effectiveSelectedYear, effectiveSelectedMonth, dayNumber, calendarType)
            ?: java.time.DayOfWeek.SATURDAY
        return CalendarHelper.getDayOfWeekResId(dow)
    }

    @androidx.annotation.StringRes
    fun getShortDayOfWeekResId(dayNumber: Int): Int {
        val dow = CalendarHelper.getDayOfWeek(effectiveSelectedYear, effectiveSelectedMonth, dayNumber, calendarType)
            ?: java.time.DayOfWeek.SATURDAY
        return CalendarHelper.getShortDayOfWeekResId(dow)
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
                TodayQuickPrompt(TodayPromptType.EXIT_NOW, entity, entity.formattedEnterTime(isFarsi))
            } else {
                null
            }
        }
}
