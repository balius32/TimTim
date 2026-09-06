package com.example.domain.usecase

import com.example.domain.repository.WorkRepository

class InitializeMonthUseCase(
    private val repository: WorkRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Result<Unit> = runCatching {
        repository.initializeMonthIfEmpty(year, month)
    }
}
