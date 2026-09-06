package com.example.domain.usecase

import com.example.domain.repository.WorkRepository

class UpdateDailyTargetUseCase(
    private val repository: WorkRepository
) {
    suspend fun updateGlobalDailyTarget(hours: Int, minutes: Int) {
        val totalMinutes = (hours * 60 + minutes).coerceIn(60, 24 * 60)
        repository.updateDailyRequiredMinutes(totalMinutes)
    }

    suspend fun updateMonthSpecificTarget(year: Int, month: Int, hours: Int, minutes: Int) {
        val totalMinutes = (hours * 60 + minutes).coerceIn(60, 24 * 60)
        repository.updateMonthDailyTarget(year, month, totalMinutes)
    }
}
