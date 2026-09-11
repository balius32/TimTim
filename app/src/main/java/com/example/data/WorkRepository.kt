package com.example.data

import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.AppSettings
import com.example.domain.model.MonthTarget
import com.example.domain.model.WorkDay
import com.example.domain.repository.WorkRepository as DomainWorkRepository
import com.example.util.CalendarHelper
import com.example.util.CalendarType
import com.example.util.DataBackupHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WorkRepository(
    private val workDao: WorkDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DomainWorkRepository {

    override val settings: Flow<AppSettings>
        get() = workDao.getSettingsFlow().map { it?.toDomain() ?: AppSettings() }

    override suspend fun getSettingsDirect(): AppSettings = withContext(ioDispatcher) {
        workDao.getSettingsDirect()?.toDomain() ?: AppSettings()
    }

    override fun getDaysForMonth(year: Int, month: Int): Flow<List<WorkDay>> =
        workDao.getDaysForMonth(year, month).map { list ->
            list.map { it.toDomain() }
        }

    override fun getMonthTarget(year: Int, month: Int): Flow<MonthTarget?> =
        workDao.getMonthTargetFlow(year, month).map { it?.toDomain() }

    override suspend fun getMonthTargetDirect(year: Int, month: Int): MonthTarget? = withContext(ioDispatcher) {
        workDao.getMonthTargetDirect(year, month)?.toDomain()
    }

    override suspend fun getDayDirect(year: Int, month: Int, dayNumber: Int): WorkDay? = withContext(ioDispatcher) {
        workDao.getDay(year, month, dayNumber)?.toDomain()
    }

    override suspend fun getDaysCountForMonth(year: Int, month: Int): Int = withContext(ioDispatcher) {
        workDao.getDaysCountForMonth(year, month)
    }

    override suspend fun initializeMonthIfEmpty(year: Int, month: Int): Unit = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        val calType = CalendarHelper.parseCalendarType(currentSettings.calendarType)

        val daysInMonth = CalendarHelper.getDaysInMonth(year, month, calType)
        val offDaysList = currentSettings.getOffDaysList()

        val missingDays = (1..daysInMonth).mapNotNull { dayNum ->
            val existing = workDao.getDay(year, month, dayNum)
            if (existing == null) {
                val dayOfWeek = CalendarHelper.getDayOfWeek(year, month, dayNum, calType)
                val shouldBeOff = dayOfWeek != null && offDaysList.contains(dayOfWeek.name)
                WorkDayEntity(
                    year = year,
                    month = month,
                    dayNumber = dayNum,
                    isDayOff = shouldBeOff
                )
            } else null
        }
        if (missingDays.isNotEmpty()) {
            workDao.insertDays(missingDays)
        }

        if (workDao.getSettingsDirect() == null) {
            workDao.saveSettings(AppSettingsEntity())
        }

        val now = CalendarHelper.now(calType)
        val isPastMonth = year < now.year || (year == now.year && month < now.month)
        if (isPastMonth && workDao.getMonthTargetDirect(year, month) == null) {
            workDao.saveMonthTarget(MonthTargetEntity(year, month, currentSettings.dailyRequiredMinutes))
        }
    }

    override suspend fun setEnterTime(year: Int, month: Int, dayNumber: Int, hour: Int, minute: Int) = withContext(ioDispatcher) {
        val existing = workDao.getDay(year, month, dayNumber) ?: WorkDayEntity(year = year, month = month, dayNumber = dayNumber)
        workDao.insertOrUpdateDay(existing.copy(enterHour = hour, enterMinute = minute, isDayOff = false))
    }

    override suspend fun setExitTime(year: Int, month: Int, dayNumber: Int, hour: Int, minute: Int) = withContext(ioDispatcher) {
        val existing = workDao.getDay(year, month, dayNumber) ?: WorkDayEntity(year = year, month = month, dayNumber = dayNumber)
        workDao.insertOrUpdateDay(existing.copy(exitHour = hour, exitMinute = minute, isDayOff = false))
    }

    override suspend fun setDayTimes(year: Int, month: Int, dayNumber: Int, enterH: Int, enterM: Int, exitH: Int, exitM: Int) = withContext(ioDispatcher) {
        val existing = workDao.getDay(year, month, dayNumber) ?: WorkDayEntity(year = year, month = month, dayNumber = dayNumber)
        workDao.insertOrUpdateDay(existing.copy(enterHour = enterH, enterMinute = enterM, exitHour = exitH, exitMinute = exitM, isDayOff = false))
    }

    override suspend fun clearEnterTime(year: Int, month: Int, dayNumber: Int) = withContext(ioDispatcher) {
        val existing = workDao.getDay(year, month, dayNumber) ?: return@withContext
        workDao.insertOrUpdateDay(existing.copy(enterHour = null, enterMinute = null))
    }

    override suspend fun clearExitTime(year: Int, month: Int, dayNumber: Int) = withContext(ioDispatcher) {
        val existing = workDao.getDay(year, month, dayNumber) ?: return@withContext
        workDao.insertOrUpdateDay(existing.copy(exitHour = null, exitMinute = null))
    }

    override suspend fun clearDay(year: Int, month: Int, dayNumber: Int) = withContext(ioDispatcher) {
        val existing = workDao.getDay(year, month, dayNumber) ?: return@withContext
        workDao.insertOrUpdateDay(existing.copy(enterHour = null, enterMinute = null, exitHour = null, exitMinute = null, isDayOff = false, note = ""))
    }

    override suspend fun toggleDayOff(year: Int, month: Int, dayNumber: Int) = withContext(ioDispatcher) {
        val existing = workDao.getDay(year, month, dayNumber) ?: WorkDayEntity(year = year, month = month, dayNumber = dayNumber)
        val newDayOff = !existing.isDayOff
        workDao.insertOrUpdateDay(existing.copy(isDayOff = newDayOff))
    }

    override suspend fun updateThemeMode(themeMode: String) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(themeMode = themeMode))
    }

    override suspend fun updateCalendarType(calendarType: String) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        if (currentSettings.calendarType != calendarType) {
            workDao.deleteEntireWorkDaysTable()
            workDao.deleteAllMonthTargetsDirect()
            workDao.saveSettings(currentSettings.copy(calendarType = calendarType))
        }
    }

    override suspend fun updateLanguage(language: String) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(language = language))
    }

    override suspend fun updateOffDaysOfWeek(offDaysString: String, currentYear: Int, currentMonth: Int) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(offDaysOfWeek = offDaysString))
    }

    override suspend fun updateMonthDailyTarget(year: Int, month: Int, dailyRequiredMinutes: Int) = withContext(ioDispatcher) {
        workDao.saveMonthTarget(MonthTargetEntity(year, month, dailyRequiredMinutes))
    }

    override suspend fun updateDailyRequiredMinutes(minutes: Int) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(dailyRequiredMinutes = minutes))
    }

    override suspend fun updateUserName(name: String) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(userName = name))
    }

    override suspend fun updateAvatar(avatarId: String) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(avatarId = avatarId))
    }

    override suspend fun updateDailyLimits(minMinutes: Int?, maxMinutes: Int?) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(minDailyMinutes = minMinutes, maxDailyMinutes = maxMinutes))
    }

    override suspend fun updateMinDailyLimit(minutes: Int?) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(minDailyMinutes = minutes))
    }

    override suspend fun updateMaxDailyLimit(minutes: Int?) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(maxDailyMinutes = minutes))
    }

    override suspend fun updateEnterExitLimits(minEnterMinutes: Int?, maxExitMinutes: Int?) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(minEnterMinutes = minEnterMinutes, maxExitMinutes = maxExitMinutes))
    }

    override suspend fun updateMinEnterTime(minutes: Int?) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(minEnterMinutes = minutes))
    }

    override suspend fun updateMaxExitTime(minutes: Int?) = withContext(ioDispatcher) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(maxExitMinutes = minutes))
    }

    override suspend fun resetMonthDays(year: Int, month: Int) = withContext(ioDispatcher) {
        workDao.clearMonthDays(year, month)
    }

    override suspend fun resetAllDays() = withContext(ioDispatcher) {
        workDao.clearAllDays()
    }

    override suspend fun setCompletedOnboarding(completed: Boolean) = withContext(ioDispatcher) {
        val current = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(current.copy(hasCompletedOnboarding = completed))
    }

    override suspend fun exportAllDataJson(): String = withContext(ioDispatcher) {
        val settings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        val monthTargets = workDao.getAllMonthTargetsDirect()
        val workDays = workDao.getAllDaysDirect()
        DataBackupHelper.generateBackupJson(settings, monthTargets, workDays)
    }

    override suspend fun importAllDataJson(jsonString: String): Result<String> = withContext(ioDispatcher) {
        runCatching {
            val backupData = DataBackupHelper.parseBackupJson(jsonString)
            backupData.settings?.let { workDao.saveSettings(it) }
            if (backupData.monthTargets.isNotEmpty()) workDao.insertMonthTargets(backupData.monthTargets)
            if (backupData.workDays.isNotEmpty()) workDao.insertDays(backupData.workDays)
            "Import successful"
        }
    }
}
