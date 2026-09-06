package com.example.domain.usecase

import com.example.domain.repository.WorkRepository

class ClearDayTimesUseCase(
    private val repository: WorkRepository
) {
    suspend fun clearEnter(year: Int, month: Int, dayNumber: Int) {
        repository.clearEnterTime(year, month, dayNumber)
    }

    suspend fun clearExit(year: Int, month: Int, dayNumber: Int) {
        repository.clearExitTime(year, month, dayNumber)
    }

    suspend fun clearDay(year: Int, month: Int, dayNumber: Int) {
        repository.clearDay(year, month, dayNumber)
    }
}
