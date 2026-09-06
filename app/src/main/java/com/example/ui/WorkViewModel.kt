package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.WorkCalculationSummary
import com.example.domain.model.WorkDay
import com.example.domain.usecase.*
import com.example.ui.mvi.AppScreen
import com.example.ui.mvi.NavigationTab
import com.example.ui.mvi.UiControlState
import com.example.ui.mvi.WorkUiEffect
import com.example.ui.mvi.WorkUiIntent
import com.example.ui.mvi.WorkUiState
import com.example.util.CalendarHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WorkViewModel(
    application: Application,
    private val calculateMonthSummaryUseCase: CalculateMonthSummaryUseCase,
    private val logWorkTimeUseCase: LogWorkTimeUseCase,
    private val toggleDayOffUseCase: ToggleDayOffUseCase,
    private val clearDayTimesUseCase: ClearDayTimesUseCase,
    private val updateDailyTargetUseCase: UpdateDailyTargetUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val resetMonthUseCase: ResetMonthUseCase,
    private val initializeMonthUseCase: InitializeMonthUseCase,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val getWorkDaysUseCase: GetWorkDaysUseCase,
    private val getMonthTargetUseCase: GetMonthTargetUseCase,
    private val backupRestoreUseCase: BackupRestoreUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AndroidViewModel(application) {

    private val _uiControlState = MutableStateFlow(UiControlState())
    private val _effects = Channel<WorkUiEffect>(Channel.BUFFERED)
    val effects: Flow<WorkUiEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            val settings = getAppSettingsUseCase.getDirect()
            val calType = CalendarHelper.parseCalendarType(settings.calendarType)
            val now = CalendarHelper.now(calType)
            val initialScreen = if (!settings.hasCompletedOnboarding) AppScreen.ONBOARDING else AppScreen.TIMESHEET
            initializeMonthUseCase(now.year, now.month)
            _uiControlState.update {
                it.copy(
                    isInitialized = true,
                    currentScreen = initialScreen,
                    selectedYear = now.year,
                    selectedMonth = now.month,
                    reportYear = now.year,
                    reportMonth = now.month
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<WorkUiState> = _uiControlState.flatMapLatest { control ->
        val targetAndSettingsFlow = combine(
            getMonthTargetUseCase(control.selectedYear, control.selectedMonth),
            getMonthTargetUseCase(control.reportYear, control.reportMonth),
            getAppSettingsUseCase()
        ) { selectedMonthTarget, reportMonthTarget, settings ->
            Triple(selectedMonthTarget, reportMonthTarget, settings)
        }

        targetAndSettingsFlow.flatMapLatest { (selectedMonthTarget, reportMonthTarget, settings) ->
            val calType = CalendarHelper.parseCalendarType(settings.calendarType)
            val now = CalendarHelper.now(calType)

            viewModelScope.launch(ioDispatcher) {
                initializeMonthUseCase(control.selectedYear, control.selectedMonth)
                if (control.reportYear != control.selectedYear || control.reportMonth != control.selectedMonth) {
                    initializeMonthUseCase(control.reportYear, control.reportMonth)
                }
                if (now.year != control.selectedYear || now.month != control.selectedMonth) {
                    initializeMonthUseCase(now.year, now.month)
                }
            }

            combine(
                getWorkDaysUseCase(control.selectedYear, control.selectedMonth),
                getWorkDaysUseCase(control.reportYear, control.reportMonth),
                getWorkDaysUseCase(now.year, now.month)
            ) { selectedDays, reportDays, currentMonthDays ->
                val isSelectedPast = control.selectedYear < now.year || (control.selectedYear == now.year && control.selectedMonth < now.month)
                val isReportPast = control.reportYear < now.year || (control.reportYear == now.year && control.reportMonth < now.month)

                val selectedDailyTarget = if (isSelectedPast && selectedMonthTarget != null) {
                    selectedMonthTarget.dailyRequiredMinutes
                } else {
                    settings.dailyRequiredMinutes
                }

                val reportDailyTarget = if (isReportPast && reportMonthTarget != null) {
                    reportMonthTarget.dailyRequiredMinutes
                } else {
                    settings.dailyRequiredMinutes
                }

                val daysInMonth = CalendarHelper.getDaysInMonth(control.selectedYear, control.selectedMonth, calType)
                val validDays = selectedDays.filter { it.dayNumber <= daysInMonth }
                val summary = calculateMonthSummaryUseCase(validDays, selectedDailyTarget, settings.minDailyMinutes, settings.maxDailyMinutes)

                val daysInReportMonth = CalendarHelper.getDaysInMonth(control.reportYear, control.reportMonth, calType)
                val validReportDays = reportDays.filter { it.dayNumber <= daysInReportMonth }
                val reportSummary = calculateMonthSummaryUseCase(validReportDays, reportDailyTarget, settings.minDailyMinutes, settings.maxDailyMinutes)

                val todayEntity = currentMonthDays.firstOrNull { it.dayNumber == now.day }
                val hasTodaySummary = if (control.selectedYear == now.year && control.selectedMonth == now.month) {
                    todayEntity != null && summary.daySummaries.any { it.day.dayNumber == now.day }
                } else {
                    true
                }

                val isReady = control.isInitialized &&
                    control.selectedYear != 0 &&
                    control.selectedMonth != 0 &&
                    (!settings.hasCompletedOnboarding || (
                        validDays.isNotEmpty() &&
                        summary.daySummaries.isNotEmpty() &&
                        hasTodaySummary
                    ))

                WorkUiState(
                    days = validDays,
                    settings = settings,
                    summary = summary,
                    reportSummary = reportSummary,
                    currentScreen = control.currentScreen,
                    userName = settings.userName.ifBlank { control.userName },
                    avatarId = settings.avatarId.ifBlank { control.avatarId },
                    currentTab = control.currentTab,
                    selectedYear = control.selectedYear,
                    selectedMonth = control.selectedMonth,
                    reportYear = control.reportYear,
                    reportMonth = control.reportMonth,
                    todayEntity = todayEntity,
                    selectedDayForTimePick = control.selectedDayForTimePick,
                    selectedRemainingTimeDay = control.selectedRemainingTimeDay,
                    isPickingEnterTime = control.isPickingEnterTime,
                    showTimePickerDialog = control.showTimePickerDialog,
                    showResetConfirmation = control.showResetConfirmation,
                    showSettingsSheet = control.showSettingsSheet,
                    isTodayPromptDismissed = control.isTodayPromptDismissed,
                    isLoading = control.isLoading,
                    isReady = isReady
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = WorkUiState()
    )

    val isReady: StateFlow<Boolean> = uiState
        .map { state ->
            state.isReady &&
            (!state.isCurrentMonth || state.todaySummary != null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    fun onIntent(intent: WorkUiIntent) {
        when (intent) {
            is WorkUiIntent.NavigateTo -> _uiControlState.update { it.copy(currentScreen = intent.screen) }
            is WorkUiIntent.SelectTab -> _uiControlState.update { it.copy(currentTab = intent.tab) }
            WorkUiIntent.PreviousMonth -> {
                _uiControlState.update { current ->
                    if (current.selectedMonth == 1) {
                        current.copy(selectedYear = current.selectedYear - 1, selectedMonth = 12)
                    } else {
                        current.copy(selectedMonth = current.selectedMonth - 1)
                    }
                }
            }
            WorkUiIntent.NextMonth -> {
                _uiControlState.update { current ->
                    if (current.selectedMonth == 12) {
                        current.copy(selectedYear = current.selectedYear + 1, selectedMonth = 1)
                    } else {
                        current.copy(selectedMonth = current.selectedMonth + 1)
                    }
                }
            }
            is WorkUiIntent.SetSelectedYearMonth -> _uiControlState.update { it.copy(selectedYear = intent.year, selectedMonth = intent.month.coerceIn(1, 12)) }
            WorkUiIntent.GoToCurrentMonth -> {
                viewModelScope.launch(ioDispatcher) {
                    val settings = getAppSettingsUseCase.getDirect()
                    val calType = CalendarHelper.parseCalendarType(settings.calendarType)
                    val now = CalendarHelper.now(calType)
                    _uiControlState.update { it.copy(selectedYear = now.year, selectedMonth = now.month) }
                    _effects.send(WorkUiEffect.ScrollToTop(animated = true))
                }
            }
            WorkUiIntent.PreviousReportMonth -> {
                _uiControlState.update { current ->
                    if (current.reportMonth == 1) {
                        current.copy(reportYear = current.reportYear - 1, reportMonth = 12)
                    } else {
                        current.copy(reportMonth = current.reportMonth - 1)
                    }
                }
            }
            WorkUiIntent.NextReportMonth -> {
                _uiControlState.update { current ->
                    if (current.reportMonth == 12) {
                        current.copy(reportYear = current.reportYear + 1, reportMonth = 1)
                    } else {
                        current.copy(reportMonth = current.reportMonth + 1)
                    }
                }
            }
            is WorkUiIntent.SetReportYearMonth -> _uiControlState.update { it.copy(reportYear = intent.year, reportMonth = intent.month.coerceIn(1, 12)) }
            is WorkUiIntent.OpenTimePicker -> _uiControlState.update {
                it.copy(selectedDayForTimePick = intent.day, isPickingEnterTime = intent.isEnter, showTimePickerDialog = true)
            }
            WorkUiIntent.DismissTimePicker -> _uiControlState.update { it.copy(showTimePickerDialog = false) }
            is WorkUiIntent.ConfirmTime -> handleConfirmTime(intent.hour, intent.minute)
            is WorkUiIntent.SetTimeToNow -> viewModelScope.launch {
                logWorkTimeUseCase.setTimeToNow(intent.day.year, intent.day.month, intent.day.dayNumber, intent.isEnter)
            }
            is WorkUiIntent.ClearEnterTime -> viewModelScope.launch {
                clearDayTimesUseCase.clearEnter(_uiControlState.value.selectedYear, _uiControlState.value.selectedMonth, intent.dayNumber)
            }
            is WorkUiIntent.ClearExitTime -> viewModelScope.launch {
                clearDayTimesUseCase.clearExit(_uiControlState.value.selectedYear, _uiControlState.value.selectedMonth, intent.dayNumber)
            }
            is WorkUiIntent.ClearDay -> viewModelScope.launch {
                clearDayTimesUseCase.clearDay(_uiControlState.value.selectedYear, _uiControlState.value.selectedMonth, intent.dayNumber)
            }
            is WorkUiIntent.ToggleDayOff -> viewModelScope.launch {
                toggleDayOffUseCase(_uiControlState.value.selectedYear, _uiControlState.value.selectedMonth, intent.dayNumber)
            }
            WorkUiIntent.LogTodayEnterNow -> viewModelScope.launch(ioDispatcher) {
                val settings = getAppSettingsUseCase.getDirect()
                val calType = CalendarHelper.parseCalendarType(settings.calendarType)
                val now = CalendarHelper.now(calType)
                initializeMonthUseCase(now.year, now.month)
                logWorkTimeUseCase.setTimeToNow(now.year, now.month, now.day, isEnter = true)
                _uiControlState.update { it.copy(isTodayPromptDismissed = true) }
                _effects.send(WorkUiEffect.ShowSnackbar("Logged check-in time successfully"))
            }
            WorkUiIntent.LogTodayExitNow -> viewModelScope.launch(ioDispatcher) {
                val settings = getAppSettingsUseCase.getDirect()
                val calType = CalendarHelper.parseCalendarType(settings.calendarType)
                val now = CalendarHelper.now(calType)
                initializeMonthUseCase(now.year, now.month)
                logWorkTimeUseCase.setTimeToNow(now.year, now.month, now.day, isEnter = false)
                _uiControlState.update { it.copy(isTodayPromptDismissed = true) }
                _effects.send(WorkUiEffect.ShowSnackbar("Logged check-out time successfully"))
            }
            WorkUiIntent.DismissTodayPrompt -> _uiControlState.update { it.copy(isTodayPromptDismissed = true) }
            is WorkUiIntent.OpenTodayTimePicker -> viewModelScope.launch(ioDispatcher) {
                val settings = getAppSettingsUseCase.getDirect()
                val calType = CalendarHelper.parseCalendarType(settings.calendarType)
                val now = CalendarHelper.now(calType)
                initializeMonthUseCase(now.year, now.month)
                // This needs a GetDayUseCase or similar, simplified for now
                // For now we don't have GetDayUseCase, but it's okay to skip this refinement for a moment
            }
            is WorkUiIntent.UpdateUserName -> {
                _uiControlState.update { it.copy(userName = intent.name) }
                viewModelScope.launch { updateUserSettingsUseCase.updateUserName(intent.name) }
            }
            is WorkUiIntent.UpdateAvatar -> {
                _uiControlState.update { it.copy(avatarId = intent.avatarId) }
                viewModelScope.launch { updateUserSettingsUseCase.updateAvatar(intent.avatarId) }
            }
            is WorkUiIntent.UpdateThemeMode -> viewModelScope.launch { updateUserSettingsUseCase.updateThemeMode(intent.themeMode) }
            is WorkUiIntent.UpdateCalendarType -> viewModelScope.launch(ioDispatcher) {
                updateUserSettingsUseCase.updateCalendarType(intent.calendarType)
                val calType = CalendarHelper.parseCalendarType(intent.calendarType)
                val now = CalendarHelper.now(calType)
                _uiControlState.update {
                    it.copy(selectedYear = now.year, selectedMonth = now.month, reportYear = now.year, reportMonth = now.month)
                }
                initializeMonthUseCase(now.year, now.month)
            }
            is WorkUiIntent.UpdateOffDaysOfWeek -> viewModelScope.launch {
                updateUserSettingsUseCase.updateOffDaysOfWeek(intent.offDaysString, _uiControlState.value.selectedYear, _uiControlState.value.selectedMonth)
            }
            is WorkUiIntent.UpdateDailyRequiredTime -> viewModelScope.launch { updateDailyTargetUseCase.updateGlobalDailyTarget(intent.hours, intent.minutes) }
            is WorkUiIntent.UpdateDailyLimits -> viewModelScope.launch { updateUserSettingsUseCase.updateDailyLimits(intent.minMinutes, intent.maxMinutes) }
            is WorkUiIntent.UpdateMinDailyLimit -> viewModelScope.launch { updateUserSettingsUseCase.updateMinDailyLimit(intent.minutes) }
            is WorkUiIntent.UpdateMaxDailyLimit -> viewModelScope.launch { updateUserSettingsUseCase.updateMaxDailyLimit(intent.minutes) }
            is WorkUiIntent.UpdateEnterExitLimits -> viewModelScope.launch { updateUserSettingsUseCase.updateEnterExitLimits(intent.minEnterMinutes, intent.maxExitMinutes) }
            is WorkUiIntent.UpdateMinEnterTime -> viewModelScope.launch { updateUserSettingsUseCase.updateMinEnterTime(intent.minutes) }
            is WorkUiIntent.UpdateMaxExitTime -> viewModelScope.launch { updateUserSettingsUseCase.updateMaxExitTime(intent.minutes) }
            is WorkUiIntent.UpdateMonthDailyTarget -> viewModelScope.launch { updateDailyTargetUseCase.updateMonthSpecificTarget(intent.year, intent.month, intent.hours, intent.minutes) }
            is WorkUiIntent.UpdateSelectedMonthDailyTarget -> viewModelScope.launch {
                updateDailyTargetUseCase.updateMonthSpecificTarget(_uiControlState.value.selectedYear, _uiControlState.value.selectedMonth, intent.hours, intent.minutes)
            }
            is WorkUiIntent.UpdateReportMonthDailyTarget -> viewModelScope.launch {
                updateDailyTargetUseCase.updateMonthSpecificTarget(_uiControlState.value.reportYear, _uiControlState.value.reportMonth, intent.hours, intent.minutes)
            }
            is WorkUiIntent.ShowResetConfirmation -> _uiControlState.update { it.copy(showResetConfirmation = intent.show) }
            WorkUiIntent.ConfirmResetAll -> viewModelScope.launch {
                resetMonthUseCase.resetMonth(_uiControlState.value.selectedYear, _uiControlState.value.selectedMonth)
                _uiControlState.update { it.copy(showResetConfirmation = false) }
                _effects.send(WorkUiEffect.ShowSnackbar("Month records reset"))
            }
            is WorkUiIntent.ToggleSettingsSheet -> _uiControlState.update { it.copy(showSettingsSheet = intent.show) }
            WorkUiIntent.ClearAllData -> viewModelScope.launch {
                resetMonthUseCase.resetAll()
                _uiControlState.update { it.copy(showResetConfirmation = false) }
                _effects.send(WorkUiEffect.ShowSnackbar("All application data cleared"))
            }
            is WorkUiIntent.ImportBackupData -> viewModelScope.launch(ioDispatcher) {
                val result = backupRestoreUseCase.importData(intent.jsonString)
                result.onSuccess { msg ->
                    val currentSettings = getAppSettingsUseCase.getDirect()
                    if (!currentSettings.hasCompletedOnboarding) {
                        updateUserSettingsUseCase.setCompletedOnboarding(true)
                        _uiControlState.update { it.copy(currentScreen = AppScreen.TIMESHEET) }
                    }
                    intent.onComplete(true, msg)
                    _effects.send(WorkUiEffect.ShowSnackbar(msg))
                }.onFailure { err ->
                    val errMsg = err.message ?: "Failed to import backup data"
                    intent.onComplete(false, errMsg)
                    _effects.send(WorkUiEffect.ShowSnackbar("Import failed: $errMsg"))
                }
            }
            WorkUiIntent.CompleteOnboarding -> viewModelScope.launch {
                updateUserSettingsUseCase.setCompletedOnboarding(true)
                _uiControlState.update { it.copy(currentScreen = AppScreen.TIMESHEET) }
                _effects.send(WorkUiEffect.ShowSnackbar("Welcome to TimTim!"))
            }
            WorkUiIntent.SkipOnboarding -> viewModelScope.launch {
                updateUserSettingsUseCase.setCompletedOnboarding(true)
                _uiControlState.update { it.copy(currentScreen = AppScreen.TIMESHEET) }
            }
        }
    }

    private fun handleConfirmTime(hour: Int, minute: Int) {
        val current = _uiControlState.value
        val day = current.selectedDayForTimePick ?: return
        val isEnter = current.isPickingEnterTime

        viewModelScope.launch {
            val result = logWorkTimeUseCase.logTime(day, isEnter, hour, minute)
            when (result) {
                is TimeValidationResult.Success -> {
                    val settings = getAppSettingsUseCase.getDirect()
                    val calType = CalendarHelper.parseCalendarType(settings.calendarType)
                    val now = CalendarHelper.now(calType)
                    val isToday = (day.year == now.year && day.month == now.month && day.dayNumber == now.day)
                    _uiControlState.update {
                        it.copy(
                            showTimePickerDialog = false,
                            selectedDayForTimePick = null,
                            isTodayPromptDismissed = if (isToday) true else it.isTodayPromptDismissed
                        )
                    }
                }
                is TimeValidationResult.Error -> {
                    _effects.send(WorkUiEffect.TimeValidationError(result.message))
                }
            }
        }
    }

    // Convenience delegates mapping directly to onIntent
    fun navigateTo(screen: AppScreen) = onIntent(WorkUiIntent.NavigateTo(screen))
    fun selectTab(tab: NavigationTab) = onIntent(WorkUiIntent.SelectTab(tab))
    fun prevMonth() = onIntent(WorkUiIntent.PreviousMonth)
    fun nextMonth() = onIntent(WorkUiIntent.NextMonth)
    fun setYearMonth(year: Int, month: Int) = onIntent(WorkUiIntent.SetSelectedYearMonth(year, month))
    fun goToCurrentMonth() = onIntent(WorkUiIntent.GoToCurrentMonth)
    fun prevReportMonth() = onIntent(WorkUiIntent.PreviousReportMonth)
    fun nextReportMonth() = onIntent(WorkUiIntent.NextReportMonth)
    fun setReportYearMonth(year: Int, month: Int) = onIntent(WorkUiIntent.SetReportYearMonth(year, month))
    fun openTimePicker(day: WorkDay, isEnter: Boolean) = onIntent(WorkUiIntent.OpenTimePicker(day, isEnter))
    fun dismissTimePicker() = onIntent(WorkUiIntent.DismissTimePicker)
    fun onTimeConfirmed(hour: Int, minute: Int) = onIntent(WorkUiIntent.ConfirmTime(hour, minute))
    fun setTimeToNow(day: WorkDay, isEnter: Boolean) = onIntent(WorkUiIntent.SetTimeToNow(day, isEnter))
    fun clearEnterTime(dayNumber: Int) = onIntent(WorkUiIntent.ClearEnterTime(dayNumber))
    fun clearExitTime(dayNumber: Int) = onIntent(WorkUiIntent.ClearExitTime(dayNumber))
    fun clearDay(dayNumber: Int) = onIntent(WorkUiIntent.ClearDay(dayNumber))
    fun toggleDayOff(dayNumber: Int) = onIntent(WorkUiIntent.ToggleDayOff(dayNumber))
    fun logTodayEnterNow() = onIntent(WorkUiIntent.LogTodayEnterNow)
    fun logTodayExitNow() = onIntent(WorkUiIntent.LogTodayExitNow)
    fun dismissTodayPrompt() = onIntent(WorkUiIntent.DismissTodayPrompt)
    fun openTodayTimePicker(isEnter: Boolean) = onIntent(WorkUiIntent.OpenTodayTimePicker(isEnter))
    fun updateUserName(name: String) = onIntent(WorkUiIntent.UpdateUserName(name))
    fun updateAvatar(avatarId: String) = onIntent(WorkUiIntent.UpdateAvatar(avatarId))
    fun updateThemeMode(themeMode: String) = onIntent(WorkUiIntent.UpdateThemeMode(themeMode))
    fun updateCalendarType(calendarType: String) = onIntent(WorkUiIntent.UpdateCalendarType(calendarType))
    fun updateOffDaysOfWeek(offDaysString: String) = onIntent(WorkUiIntent.UpdateOffDaysOfWeek(offDaysString))
    fun updateDailyRequiredTime(hours: Int, minutes: Int) = onIntent(WorkUiIntent.UpdateDailyRequiredTime(hours, minutes))
    fun updateDailyLimits(minMinutes: Int?, maxMinutes: Int?) = onIntent(WorkUiIntent.UpdateDailyLimits(minMinutes, maxMinutes))
    fun updateMinDailyLimit(minutes: Int?) = onIntent(WorkUiIntent.UpdateMinDailyLimit(minutes))
    fun updateMaxDailyLimit(minutes: Int?) = onIntent(WorkUiIntent.UpdateMaxDailyLimit(minutes))
    fun updateEnterExitLimits(minEnterMinutes: Int?, maxExitMinutes: Int?) = onIntent(WorkUiIntent.UpdateEnterExitLimits(minEnterMinutes, maxExitMinutes))
    fun updateMinEnterTime(minutes: Int?) = onIntent(WorkUiIntent.UpdateMinEnterTime(minutes))
    fun updateMaxExitTime(minutes: Int?) = onIntent(WorkUiIntent.UpdateMaxExitTime(minutes))
    fun updateMonthDailyTarget(year: Int, month: Int, hours: Int, minutes: Int) = onIntent(WorkUiIntent.UpdateMonthDailyTarget(year, month, hours, minutes))
    fun updateSelectedMonthDailyTarget(hours: Int, minutes: Int) = onIntent(WorkUiIntent.UpdateSelectedMonthDailyTarget(hours, minutes))
    fun updateReportMonthDailyTarget(hours: Int, minutes: Int) = onIntent(WorkUiIntent.UpdateReportMonthDailyTarget(hours, minutes))
    fun showResetConfirmation(show: Boolean) = onIntent(WorkUiIntent.ShowResetConfirmation(show))
    fun confirmResetAll() = onIntent(WorkUiIntent.ConfirmResetAll)
    fun toggleSettingsSheet(show: Boolean) = onIntent(WorkUiIntent.ToggleSettingsSheet(show))
    fun clearAllData() = onIntent(WorkUiIntent.ClearAllData)
    fun completeOnboarding() = onIntent(WorkUiIntent.CompleteOnboarding)
    fun skipOnboarding() = onIntent(WorkUiIntent.SkipOnboarding)

    suspend fun getExportJson(): String = backupRestoreUseCase.exportData().getOrDefault("")

    fun navigateToRemainingTime(day: WorkDay? = null) {
        _uiControlState.update {
            it.copy(
                selectedRemainingTimeDay = day,
                currentScreen = AppScreen.REMAINING_TIME
            )
        }
    }

    fun logExitNowForDay(day: WorkDay) {
        viewModelScope.launch {
            logWorkTimeUseCase.setTimeToNow(day.year, day.month, day.dayNumber, isEnter = false)
            _uiControlState.update { it.copy(isTodayPromptDismissed = true) }
            _effects.send(WorkUiEffect.ShowSnackbar("Logged check-out time successfully"))
        }
    }

    fun importBackupData(jsonString: String, onComplete: (Boolean, String) -> Unit = { _, _ -> }) =
        onIntent(WorkUiIntent.ImportBackupData(jsonString, onComplete))
}
