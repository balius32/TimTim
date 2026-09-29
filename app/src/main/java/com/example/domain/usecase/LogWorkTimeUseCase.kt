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
        if (isEnter) {
            repository.setEnterTime(day.year, day.month, day.dayNumber, hour, minute)
        } else {
            repository.setExitTime(day.year, day.month, day.dayNumber, hour, minute)
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

        if (isEnter) {
            repository.setEnterTime(year, month, dayNumber, hour, minute)
        } else {
            repository.setExitTime(year, month, dayNumber, hour, minute)
        }
    }
}
