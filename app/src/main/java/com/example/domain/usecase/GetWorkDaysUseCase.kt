package com.example.domain.usecase

import com.example.domain.model.WorkDay
import com.example.domain.repository.WorkRepository
import kotlinx.coroutines.flow.Flow

class GetWorkDaysUseCase(
    private val repository: WorkRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<List<WorkDay>> =
        repository.getDaysForMonth(year, month)
}
