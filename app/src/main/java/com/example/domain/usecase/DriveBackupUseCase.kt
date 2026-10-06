package com.example.domain.usecase

import com.example.domain.repository.GoogleDriveRepository
import com.example.domain.repository.WorkRepository

class DriveBackupUseCase(
    private val googleDriveRepository: GoogleDriveRepository,
    private val backupRestoreUseCase: BackupRestoreUseCase,
    private val workRepository: WorkRepository
) {
    suspend fun backupToDrive(): Result<Unit> = runCatching {
        val json = backupRestoreUseCase.exportData().getOrThrow()
        googleDriveRepository.uploadBackupJson(json).getOrThrow()
        workRepository.updateDriveLastBackup(System.currentTimeMillis())
    }

    suspend fun restoreFromDrive(): Result<String> {
        return googleDriveRepository.downloadBackupJson()
    }

    suspend fun disconnectDrive(): Result<Unit> = runCatching {
        googleDriveRepository.disconnect().getOrThrow()
        workRepository.updateDriveConnection(accountEmail = null, lastBackupEpochMs = null)
    }

    suspend fun onDriveConnected(accountEmail: String): Result<Unit> = runCatching {
        workRepository.updateDriveConnection(accountEmail = accountEmail, lastBackupEpochMs = null)
    }

    suspend fun refreshConnectedEmailFromGoogle(): String? {
        return googleDriveRepository.signedInEmail()
    }
}
