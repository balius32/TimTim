package com.example.domain.usecase

import com.example.domain.model.WorkDay
import com.example.domain.repository.WorkRepository

class GetDayUseCase(
    private val repository: WorkRepository
) {
    suspend operator fun invoke(year: Int, month: Int, dayNumber: Int): WorkDay? {
        repository.initializeMonthIfEmpty(year, month)
        return repository.getDayDirect(year, month, dayNumber)
    }
}
