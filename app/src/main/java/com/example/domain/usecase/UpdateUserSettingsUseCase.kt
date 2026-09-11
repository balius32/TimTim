package com.example.domain.usecase

import com.example.domain.repository.WorkRepository

class UpdateUserSettingsUseCase(
    private val repository: WorkRepository
) {
    suspend fun updateUserName(name: String): Result<Unit> = runCatching {
        repository.updateUserName(name)
    }

    suspend fun updateAvatar(avatarId: String): Result<Unit> = runCatching {
        repository.updateAvatar(avatarId)
    }

    suspend fun updateThemeMode(themeMode: String): Result<Unit> = runCatching {
        repository.updateThemeMode(themeMode)
    }

    suspend fun updateCalendarType(calendarType: String): Result<Unit> = runCatching {
        repository.updateCalendarType(calendarType)
    }

    suspend fun updateLanguage(language: String): Result<Unit> = runCatching {
        repository.updateLanguage(language)
    }

    suspend fun updateOffDaysOfWeek(offDaysString: String, currentYear: Int, currentMonth: Int): Result<Unit> = runCatching {
        repository.updateOffDaysOfWeek(offDaysString, currentYear, currentMonth)
    }

    suspend fun updateDailyLimits(minMinutes: Int?, maxMinutes: Int?): Result<Unit> = runCatching {
        repository.updateDailyLimits(minMinutes, maxMinutes)
    }

    suspend fun updateMinDailyLimit(minutes: Int?): Result<Unit> = runCatching {
        repository.updateMinDailyLimit(minutes)
    }

    suspend fun updateMaxDailyLimit(minutes: Int?): Result<Unit> = runCatching {
        repository.updateMaxDailyLimit(minutes)
    }

    suspend fun updateEnterExitLimits(minEnterMinutes: Int?, maxExitMinutes: Int?): Result<Unit> = runCatching {
        repository.updateEnterExitLimits(minEnterMinutes, maxExitMinutes)
    }

    suspend fun updateMinEnterTime(minutes: Int?): Result<Unit> = runCatching {
        repository.updateMinEnterTime(minutes)
    }

    suspend fun updateMaxExitTime(minutes: Int?): Result<Unit> = runCatching {
        repository.updateMaxExitTime(minutes)
    }

    suspend fun setCompletedOnboarding(completed: Boolean): Result<Unit> = runCatching {
        repository.setCompletedOnboarding(completed)
    }
}
