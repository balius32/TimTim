package com.example.domain.usecase

import com.example.domain.model.WorkDay
import com.example.domain.repository.WorkRepository

sealed interface TimeValidationResult {
    data object Success : TimeValidationResult
    data class Error(val message: String) : TimeValidationResult
}

class LogWorkTimeUseCase(
    private val repository: WorkRepository
) {
    suspend fun logTime(
        day: WorkDay,
        isEnter: Boolean,
        hour: Int,
        minute: Int
    ): TimeValidationResult {
        val (clampedHour, clampedMinute) = clampToEnterExitLimits(isEnter, hour, minute)
        if (isEnter) {
            repository.setEnterTime(day.year, day.month, day.dayNumber, clampedHour, clampedMinute)
        } else {
            repository.setExitTime(day.year, day.month, day.dayNumber, clampedHour, clampedMinute)
        }
        return TimeValidationResult.Success
    }

    suspend fun setTimeToNow(
        year: Int,
        month: Int,
        dayNumber: Int,
        isEnter: Boolean
    ) {
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        val (clampedHour, clampedMinute) = clampToEnterExitLimits(isEnter, hour, minute)

        if (isEnter) {
            repository.setEnterTime(year, month, dayNumber, clampedHour, clampedMinute)
        } else {
            repository.setExitTime(year, month, dayNumber, clampedHour, clampedMinute)
        }
    }

    /**
     * Enforces settings limits:
     * - Enter before minEnter → clamp up to minEnter
     * - Exit after maxExit → clamp down to maxExit
     */
    private suspend fun clampToEnterExitLimits(
        isEnter: Boolean,
        hour: Int,
        minute: Int
    ): Pair<Int, Int> {
        val settings = repository.getSettingsDirect()
        val currentMins = hour * 60 + minute

        if (isEnter) {
            val minEnter = settings.minEnterMinutes
            if (minEnter != null && minEnter > 0 && currentMins < minEnter) {
                return minEnter / 60 to minEnter % 60
            }
        } else {
            val maxExit = settings.maxExitMinutes
            if (maxExit != null && maxExit > 0 && currentMins > maxExit) {
                return maxExit / 60 to maxExit % 60
            }
        }
        return hour to minute
    }
}
