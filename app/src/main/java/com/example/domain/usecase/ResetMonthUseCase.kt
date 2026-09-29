package com.example.domain.usecase

import com.example.domain.repository.WorkRepository

class ResetMonthUseCase(
    private val repository: WorkRepository
) {
    suspend fun resetMonth(year: Int, month: Int): Result<Unit> = runCatching {
        repository.resetMonthDays(year, month)
    }

    suspend fun resetAll(): Result<Unit> = runCatching {
        repository.resetAllDays()
    }
}
