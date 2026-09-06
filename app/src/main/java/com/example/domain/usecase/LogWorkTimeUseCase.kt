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
        var finalHour = hour
        var finalMinute = minute
        var selectedMins = hour * 60 + minute

        val settings = repository.getSettingsDirect()

        if (isEnter) {
            val minEnter = settings.minEnterMinutes
            if (minEnter != null && minEnter > 0 && selectedMins < minEnter) {
                finalHour = minEnter / 60
                finalMinute = minEnter % 60
                selectedMins = minEnter
            }
        } else {
            val maxExit = settings.maxExitMinutes
            if (maxExit != null && maxExit > 0 && selectedMins > maxExit) {
                finalHour = maxExit / 60
                finalMinute = maxExit % 60
                selectedMins = maxExit
            }
        }

        if (!isEnter && day.hasEnterTime) {
            val enterMins = (day.enterHour ?: 0) * 60 + (day.enterMinute ?: 0)
            if (selectedMins < enterMins) {
                return TimeValidationResult.Error("Exit time cannot be earlier than enter time")
            }
        } else if (isEnter && day.hasExitTime) {
            val exitMins = (day.exitHour ?: 0) * 60 + (day.exitMinute ?: 0)
            if (selectedMins > exitMins) {
                return TimeValidationResult.Error("Enter time cannot be later than exit time")
            }
        }

        if (isEnter) {
            repository.setEnterTime(day.year, day.month, day.dayNumber, finalHour, finalMinute)
        } else {
            repository.setExitTime(day.year, day.month, day.dayNumber, finalHour, finalMinute)
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
        var hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        var minute = calendar.get(java.util.Calendar.MINUTE)
        val currentMins = hour * 60 + minute

        val settings = repository.getSettingsDirect()

        if (isEnter) {
            val minEnter = settings.minEnterMinutes
            if (minEnter != null && minEnter > 0 && currentMins < minEnter) {
                hour = minEnter / 60
                minute = minEnter % 60
            }
            repository.setEnterTime(year, month, dayNumber, hour, minute)
        } else {
            val maxExit = settings.maxExitMinutes
            if (maxExit != null && maxExit > 0 && currentMins > maxExit) {
                hour = maxExit / 60
                minute = maxExit % 60
            }
            repository.setExitTime(year, month, dayNumber, hour, minute)
        }
    }
}
