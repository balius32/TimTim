package com.example.domain.usecase

import com.example.domain.repository.WorkRepository

class ToggleDayOffUseCase(
    private val repository: WorkRepository
) {
    suspend operator fun invoke(year: Int, month: Int, dayNumber: Int): Result<Unit> = runCatching {
        repository.toggleDayOff(year, month, dayNumber)
    }
}
