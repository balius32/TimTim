package com.example.domain.usecase

import com.example.domain.repository.WorkRepository

class UpdateUserSettingsUseCase(
    private val repository: WorkRepository
) {
    suspend fun updateUserName(name: String) {
        repository.updateUserName(name)
    }

    suspend fun updateAvatar(avatarId: String) {
        repository.updateAvatar(avatarId)
    }

    suspend fun updateThemeMode(themeMode: String) {
        repository.updateThemeMode(themeMode)
    }

    suspend fun updateCalendarType(calendarType: String) {
        repository.updateCalendarType(calendarType)
    }

    suspend fun updateOffDaysOfWeek(offDaysString: String, currentYear: Int, currentMonth: Int) {
        repository.updateOffDaysOfWeek(offDaysString, currentYear, currentMonth)
    }

    suspend fun updateDailyLimits(minMinutes: Int?, maxMinutes: Int?) {
        repository.updateDailyLimits(minMinutes, maxMinutes)
    }

    suspend fun updateMinDailyLimit(minutes: Int?) {
        repository.updateMinDailyLimit(minutes)
    }

    suspend fun updateMaxDailyLimit(minutes: Int?) {
        repository.updateMaxDailyLimit(minutes)
    }

    suspend fun updateEnterExitLimits(minEnterMinutes: Int?, maxExitMinutes: Int?) {
        repository.updateEnterExitLimits(minEnterMinutes, maxExitMinutes)
    }

    suspend fun updateMinEnterTime(minutes: Int?) {
        repository.updateMinEnterTime(minutes)
    }

    suspend fun updateMaxExitTime(minutes: Int?) {
        repository.updateMaxExitTime(minutes)
    }

    suspend fun setCompletedOnboarding(completed: Boolean) {
        repository.setCompletedOnboarding(completed)
    }
}
