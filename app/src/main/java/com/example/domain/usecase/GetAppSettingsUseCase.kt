package com.example.domain.usecase

import com.example.domain.model.AppSettings
import com.example.domain.repository.WorkRepository
import kotlinx.coroutines.flow.Flow

class GetAppSettingsUseCase(
    private val repository: WorkRepository
) {
    operator fun invoke(): Flow<AppSettings> = repository.settings

    suspend fun getDirect(): AppSettings = repository.getSettingsDirect()
}
