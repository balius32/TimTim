package com.example.domain.usecase

import com.example.domain.repository.WorkRepository

class SaveDayNoteUseCase(
    private val repository: WorkRepository
) {
    suspend operator fun invoke(
        year: Int,
        month: Int,
        dayNumber: Int,
        note: String
    ): Result<Unit> = runCatching {
        repository.setDayNote(year, month, dayNumber, note)
    }
}
