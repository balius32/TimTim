package com.example.domain.repository

interface GoogleDriveRepository {
    suspend fun uploadBackupJson(json: String): Result<Unit>
    suspend fun downloadBackupJson(): Result<String>
    suspend fun disconnect(): Result<Unit>
    suspend fun isSignedIn(): Boolean
    suspend fun signedInEmail(): String?
}
