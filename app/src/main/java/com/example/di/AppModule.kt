package com.example.di

import com.example.data.AppDatabase
import com.example.data.WorkRepository
import com.example.data.drive.DriveAuthManager
import com.example.data.drive.GoogleDriveDataSource
import com.example.data.drive.GoogleDriveRepositoryImpl
import com.example.domain.repository.GoogleDriveRepository
import com.example.domain.repository.WorkRepository as DomainWorkRepository
import com.example.domain.usecase.*
import com.example.ui.WorkViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().workDao() }

    single<DomainWorkRepository> {
        WorkRepository(
            workDao = get(),
            ioDispatcher = Dispatchers.IO
        )
    }

    single { CalculateMonthSummaryUseCase() }
    single { LogWorkTimeUseCase(get()) }
    single { ToggleDayOffUseCase(get()) }
    single { ClearDayTimesUseCase(get()) }
    single { UpdateDailyTargetUseCase(get()) }
    single { UpdateUserSettingsUseCase(get()) }
    single { ResetMonthUseCase(get()) }
    single { InitializeMonthUseCase(get()) }
    single { GetAppSettingsUseCase(get()) }
    single { GetWorkDaysUseCase(get()) }
    single { GetMonthTargetUseCase(get()) }
    single { GetDayUseCase(get()) }
    single { BackupRestoreUseCase(get()) }
    single { DriveAuthManager(androidContext()) }
    single { GoogleDriveDataSource() }
    single<GoogleDriveRepository> { GoogleDriveRepositoryImpl(get(), get()) }
    single { DriveBackupUseCase(get(), get(), get()) }

    viewModel {
        WorkViewModel(
            application = androidApplication(),
            calculateMonthSummaryUseCase = get(),
            logWorkTimeUseCase = get(),
            toggleDayOffUseCase = get(),
            clearDayTimesUseCase = get(),
            updateDailyTargetUseCase = get(),
            updateUserSettingsUseCase = get(),
            resetMonthUseCase = get(),
            initializeMonthUseCase = get(),
            getAppSettingsUseCase = get(),
            getWorkDaysUseCase = get(),
            getMonthTargetUseCase = get(),
            getDayUseCase = get(),
            backupRestoreUseCase = get(),
            driveBackupUseCase = get(),
            driveAuthManager = get(),
            ioDispatcher = Dispatchers.IO
        )
    }
}
