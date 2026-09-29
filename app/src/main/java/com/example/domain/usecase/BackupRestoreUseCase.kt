package com.example.domain.usecase

import com.example.domain.repository.WorkRepository

class BackupRestoreUseCase(
    private val repository: WorkRepository
) {
    suspend fun exportData(): Result<String> = runCatching {
        repository.exportAllDataJson()
    }

    suspend fun importData(jsonString: String): Result<String> =
        repository.importAllDataJson(jsonString)
}
