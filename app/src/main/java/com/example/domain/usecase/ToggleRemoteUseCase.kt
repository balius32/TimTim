package com.example.domain.usecase

import com.example.domain.repository.WorkRepository

class ToggleRemoteUseCase(
    private val repository: WorkRepository
) {
    suspend operator fun invoke(year: Int, month: Int, dayNumber: Int): Result<Unit> = runCatching {
        repository.toggleRemote(year, month, dayNumber)
    }
}
