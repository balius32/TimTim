package com.example.data.drive

import com.example.domain.repository.GoogleDriveRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class GoogleDriveRepositoryImpl(
    private val authManager: DriveAuthManager,
    private val dataSource: GoogleDriveDataSource
) : GoogleDriveRepository {

    override suspend fun uploadBackupJson(json: String): Result<Unit> = runCatching {
        val account = resolveAccount()
        val drive = authManager.buildDriveService(account)
        dataSource.uploadJson(drive, json)
    }

    override suspend fun downloadBackupJson(): Result<String> = runCatching {
        val account = resolveAccount()
        val drive = authManager.buildDriveService(account)
        dataSource.downloadJson(drive)
    }

    override suspend fun disconnect(): Result<Unit> = runCatching {
        authManager.signOut()
    }

    override suspend fun isSignedIn(): Boolean {
        return authManager.getLastSignedInAccount() != null || authManager.silentSignIn() != null
    }

    override suspend fun signedInEmail(): String? {
        return authManager.getLastSignedInAccount()?.email
            ?: authManager.silentSignIn()?.email
    }

    private suspend fun resolveAccount(): GoogleSignInAccount {
        return authManager.getLastSignedInAccount()
            ?: authManager.silentSignIn()
            ?: throw IllegalStateException("Not signed in to Google Drive")
    }
}
