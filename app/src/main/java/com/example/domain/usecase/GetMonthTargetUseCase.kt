package com.example.domain.usecase

import com.example.domain.model.MonthTarget
import com.example.domain.repository.WorkRepository
import kotlinx.coroutines.flow.Flow

class GetMonthTargetUseCase(
    private val repository: WorkRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<MonthTarget?> =
        repository.getMonthTarget(year, month)

    suspend fun getDirect(year: Int, month: Int): MonthTarget? =
        repository.getMonthTargetDirect(year, month)
}
