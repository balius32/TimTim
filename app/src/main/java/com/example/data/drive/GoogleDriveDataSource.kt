package com.example.data.drive

import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class GoogleDriveDataSource {

    suspend fun uploadJson(drive: Drive, json: String) = withContext(Dispatchers.IO) {
        val existingId = findBackupFileId(drive)
        val content = ByteArrayContent.fromString(BACKUP_MIME_TYPE, json)
        if (existingId != null) {
            drive.files().update(existingId, null, content).execute()
        } else {
            val metadata = File().apply {
                name = BACKUP_FILE_NAME
                parents = listOf(APP_DATA_FOLDER)
            }
            drive.files().create(metadata, content)
                .setFields("id")
                .execute()
        }
    }

    suspend fun downloadJson(drive: Drive): String = withContext(Dispatchers.IO) {
        val fileId = findBackupFileId(drive)
            ?: throw IllegalStateException("No backup file on Google Drive")
        val output = ByteArrayOutputStream()
        drive.files().get(fileId).executeMediaAndDownloadTo(output)
        output.toString(Charsets.UTF_8.name())
    }

    private fun findBackupFileId(drive: Drive): String? {
        val result = drive.files().list()
            .setSpaces(APP_DATA_FOLDER)
            .setQ("name = '$BACKUP_FILE_NAME' and trashed = false")
            .setFields("files(id, name)")
            .execute()
        return result.files?.firstOrNull()?.id
    }

    companion object {
        private const val BACKUP_FILE_NAME = "timtim_backup.json"
        private const val BACKUP_MIME_TYPE = "application/json"
        private const val APP_DATA_FOLDER = "appDataFolder"
    }
}
