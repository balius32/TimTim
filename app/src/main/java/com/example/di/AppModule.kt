package com.example.di

import com.example.data.AppDatabase
import com.example.data.WorkRepository
import com.example.domain.repository.WorkRepository as DomainWorkRepository
import com.example.domain.usecase.*
import com.example.ui.WorkViewModel
import com.example.domain.util.WidgetUpdater
import com.example.widget.AppWidgetUpdater
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().workDao() }

    single<DomainWorkRepository> {
        WorkRepository(
            workDao = get(),
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

    single<WidgetUpdater> { AppWidgetUpdater(androidContext()) }

    viewModel {
        WorkViewModel(
            widgetUpdater = get(),
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
        )
    }
}
