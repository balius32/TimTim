package com.example.domain.repository

import com.example.domain.model.AppSettings
import com.example.domain.model.MonthTarget
import com.example.domain.model.WorkDay
import kotlinx.coroutines.flow.Flow

interface WorkRepository {
    val settings: Flow<AppSettings>
    suspend fun getSettingsDirect(): AppSettings

    fun getDaysForMonth(year: Int, month: Int): Flow<List<WorkDay>>
    fun getMonthTarget(year: Int, month: Int): Flow<MonthTarget?>
    suspend fun getMonthTargetDirect(year: Int, month: Int): MonthTarget?

    suspend fun getDayDirect(year: Int, month: Int, dayNumber: Int): WorkDay?
    suspend fun getDaysCountForMonth(year: Int, month: Int): Int

    suspend fun initializeMonthIfEmpty(year: Int, month: Int)
    suspend fun setEnterTime(year: Int, month: Int, dayNumber: Int, hour: Int, minute: Int)
    suspend fun setExitTime(year: Int, month: Int, dayNumber: Int, hour: Int, minute: Int)
    suspend fun setDayTimes(year: Int, month: Int, dayNumber: Int, enterH: Int, enterM: Int, exitH: Int, exitM: Int)
    suspend fun clearEnterTime(year: Int, month: Int, dayNumber: Int)
    suspend fun clearExitTime(year: Int, month: Int, dayNumber: Int)
    suspend fun clearDay(year: Int, month: Int, dayNumber: Int)
    suspend fun toggleDayOff(year: Int, month: Int, dayNumber: Int)
    suspend fun updateThemeMode(themeMode: String)
    suspend fun updateCalendarType(calendarType: String)
    suspend fun updateOffDaysOfWeek(offDaysString: String, currentYear: Int, currentMonth: Int)
    suspend fun updateMonthDailyTarget(year: Int, month: Int, dailyRequiredMinutes: Int)
    suspend fun updateDailyRequiredMinutes(minutes: Int)
    suspend fun updateUserName(name: String)
    suspend fun updateAvatar(avatarId: String)
    suspend fun updateDailyLimits(minMinutes: Int?, maxMinutes: Int?)
    suspend fun updateMinDailyLimit(minutes: Int?)
    suspend fun updateMaxDailyLimit(minutes: Int?)
    suspend fun updateEnterExitLimits(minEnterMinutes: Int?, maxExitMinutes: Int?)
    suspend fun updateMinEnterTime(minutes: Int?)
    suspend fun updateMaxExitTime(minutes: Int?)
    suspend fun resetMonthDays(year: Int, month: Int)
    suspend fun resetAllDays()
    suspend fun setCompletedOnboarding(completed: Boolean)
    suspend fun exportAllDataJson(): String
    suspend fun importAllDataJson(jsonString: String): Result<String>
}
