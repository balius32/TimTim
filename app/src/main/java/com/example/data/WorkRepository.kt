package com.example.data

import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.AppSettings
import com.example.domain.model.MonthTarget
import com.example.domain.model.WorkDay
import com.example.domain.repository.WorkRepository as DomainWorkRepository
import com.example.util.CalendarHelper
import com.example.util.CalendarType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WorkRepository(
    private val workDao: WorkDao
) : DomainWorkRepository {

    override fun getDaysForMonth(year: Int, month: Int): Flow<List<WorkDay>> =
        workDao.getDaysForMonth(year, month).map { list ->
            list.map { it.toDomain() }
        }

    override val settings: Flow<AppSettings> =
        workDao.getSettingsFlow().map {
            it?.toDomain() ?: AppSettings()
        }

    override suspend fun getSettingsDirect(): AppSettings = withContext(Dispatchers.IO) {
        workDao.getSettingsDirect()?.toDomain() ?: AppSettings()
    }

    override suspend fun getDaysCountForMonth(year: Int, month: Int): Int = withContext(Dispatchers.IO) {
        workDao.getDaysCountForMonth(year, month)
    }

    override fun getMonthTarget(year: Int, month: Int): Flow<MonthTarget?> =
        workDao.getMonthTargetFlow(year, month).map { it?.toDomain() }

    override suspend fun getMonthTargetDirect(year: Int, month: Int): MonthTarget? = withContext(Dispatchers.IO) {
        workDao.getMonthTargetDirect(year, month)?.toDomain()
    }

    fun getAllMonthTargets(): Flow<List<MonthTarget>> =
        workDao.getAllMonthTargetsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun initializeMonthIfEmpty(year: Int, month: Int): Unit = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        val calType = CalendarHelper.parseCalendarType(currentSettings.calendarType)
        
        // Self-healing migration: if days exist with mismatched year system for current calendar, cast them
        val allDays = workDao.getAllDaysDirect()
        val hasMismatchedData = allDays.any { 
            (it.year > 1600 && calType == CalendarType.HIJRI_SHAMSI && (it.enterHour != null || it.exitHour != null || it.note.isNotBlank())) ||
            (it.year < 1600 && calType == CalendarType.GREGORIAN && (it.enterHour != null || it.exitHour != null || it.note.isNotBlank()))
        }
        if (hasMismatchedData) {
            updateCalendarType(currentSettings.calendarType)
            return@withContext
        }

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
                    enterHour = null,
                    enterMinute = null,
                    exitHour = null,
                    exitMinute = null,
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

        // Lock/snapshot the daily target for past months if not already established
        val now = CalendarHelper.now(calType)
        val isPastMonth = year < now.year || (year == now.year && month < now.month)
        if (isPastMonth && workDao.getMonthTargetDirect(year, month) == null) {
            workDao.saveMonthTarget(MonthTargetEntity(year, month, currentSettings.dailyRequiredMinutes))
        }
    }

    private suspend fun ensurePastMonthTargetExists(year: Int, month: Int) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        val calType = CalendarHelper.parseCalendarType(currentSettings.calendarType)
        val now = CalendarHelper.now(calType)
        val isPastMonth = year < now.year || (year == now.year && month < now.month)
        if (isPastMonth && workDao.getMonthTargetDirect(year, month) == null) {
            workDao.saveMonthTarget(MonthTargetEntity(year, month, currentSettings.dailyRequiredMinutes))
        }
    }

    override suspend fun setEnterTime(year: Int, month: Int, dayNumber: Int, hour: Int, minute: Int) = withContext(Dispatchers.IO) {
        ensurePastMonthTargetExists(year, month)
        val existing = workDao.getDay(year, month, dayNumber) ?: WorkDayEntity(year = year, month = month, dayNumber = dayNumber)
        workDao.insertOrUpdateDay(
            existing.copy(
                enterHour = hour,
                enterMinute = minute,
                isDayOff = false
            )
        )
    }

    override suspend fun setExitTime(year: Int, month: Int, dayNumber: Int, hour: Int, minute: Int) = withContext(Dispatchers.IO) {
        ensurePastMonthTargetExists(year, month)
        val existing = workDao.getDay(year, month, dayNumber) ?: WorkDayEntity(year = year, month = month, dayNumber = dayNumber)
        workDao.insertOrUpdateDay(
            existing.copy(
                exitHour = hour,
                exitMinute = minute,
                isDayOff = false
            )
        )
    }

    override suspend fun setDayTimes(year: Int, month: Int, dayNumber: Int, enterH: Int, enterM: Int, exitH: Int, exitM: Int) = withContext(Dispatchers.IO) {
        ensurePastMonthTargetExists(year, month)
        val existing = workDao.getDay(year, month, dayNumber) ?: WorkDayEntity(year = year, month = month, dayNumber = dayNumber)
        workDao.insertOrUpdateDay(
            existing.copy(
                enterHour = enterH,
                enterMinute = enterM,
                exitHour = exitH,
                exitMinute = exitM,
                isDayOff = false
            )
        )
    }

    override suspend fun clearEnterTime(year: Int, month: Int, dayNumber: Int) = withContext(Dispatchers.IO) {
        val existing = workDao.getDay(year, month, dayNumber) ?: return@withContext
        workDao.insertOrUpdateDay(existing.copy(enterHour = null, enterMinute = null))
    }

    override suspend fun clearExitTime(year: Int, month: Int, dayNumber: Int) = withContext(Dispatchers.IO) {
        val existing = workDao.getDay(year, month, dayNumber) ?: return@withContext
        workDao.insertOrUpdateDay(existing.copy(exitHour = null, exitMinute = null))
    }

    override suspend fun clearDay(year: Int, month: Int, dayNumber: Int) = withContext(Dispatchers.IO) {
        val existing = workDao.getDay(year, month, dayNumber) ?: return@withContext
        workDao.insertOrUpdateDay(
            existing.copy(
                enterHour = null,
                enterMinute = null,
                exitHour = null,
                exitMinute = null,
                isDayOff = false,
                note = ""
            )
        )
    }

    override suspend fun toggleDayOff(year: Int, month: Int, dayNumber: Int) = withContext(Dispatchers.IO) {
        val existing = workDao.getDay(year, month, dayNumber) ?: WorkDayEntity(year = year, month = month, dayNumber = dayNumber)
        val newDayOff = !existing.isDayOff
        workDao.insertOrUpdateDay(
            existing.copy(
                isDayOff = newDayOff,
                enterHour = if (newDayOff) null else existing.enterHour,
                enterMinute = if (newDayOff) null else existing.enterMinute,
                exitHour = if (newDayOff) null else existing.exitHour,
                exitMinute = if (newDayOff) null else existing.exitMinute
            )
        )
    }

    override suspend fun updateDailyRequiredMinutes(minutes: Int) = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        val oldMinutes = currentSettings.dailyRequiredMinutes
        val calType = CalendarHelper.parseCalendarType(currentSettings.calendarType)
        val now = CalendarHelper.now(calType)

        // 1. Snapshot any past months that had days recorded with the previous target if not already locked
        for (m in 1..12) {
            val pastYear = if (m < now.month) now.year else now.year - 1
            val pastMonth = m
            if (pastYear < now.year || (pastYear == now.year && pastMonth < now.month)) {
                if (workDao.getMonthTargetDirect(pastYear, pastMonth) == null && workDao.getDaysCountForMonth(pastYear, pastMonth) > 0) {
                    workDao.saveMonthTarget(MonthTargetEntity(pastYear, pastMonth, oldMinutes))
                }
            }
        }

        // 2. Clear any target overrides for the current month and all future months so they dynamically use the new settings target
        workDao.clearFutureAndCurrentMonthTargets(now.year, now.month)

        // 3. Save the new settings
        workDao.saveSettings(currentSettings.copy(dailyRequiredMinutes = minutes))
    }

    override suspend fun updateUserName(name: String) = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(userName = name))
    }

    override suspend fun updateAvatar(avatarId: String) = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(avatarId = avatarId))
    }

    override suspend fun updateDailyLimits(minMinutes: Int?, maxMinutes: Int?) = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(
            currentSettings.copy(
                minDailyMinutes = minMinutes,
                maxDailyMinutes = maxMinutes,
                minEnterMinutes = minMinutes,
                maxExitMinutes = maxMinutes
            )
        )
    }

    override suspend fun updateMinDailyLimit(minutes: Int?) = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(
            currentSettings.copy(
                minDailyMinutes = minutes,
                minEnterMinutes = minutes
            )
        )
    }

    override suspend fun updateMaxDailyLimit(minutes: Int?) = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(
            currentSettings.copy(
                maxDailyMinutes = minutes,
                maxExitMinutes = minutes
            )
        )
    }

    override suspend fun updateEnterExitLimits(minEnterMinutes: Int?, maxExitMinutes: Int?) = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(
            currentSettings.copy(
                minEnterMinutes = minEnterMinutes,
                maxExitMinutes = maxExitMinutes,
                minDailyMinutes = minEnterMinutes,
                maxDailyMinutes = maxExitMinutes
            )
        )
    }

    override suspend fun updateMinEnterTime(minutes: Int?) = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(
            currentSettings.copy(
                minEnterMinutes = minutes,
                minDailyMinutes = minutes
            )
        )
    }

    override suspend fun updateMaxExitTime(minutes: Int?) = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(
            currentSettings.copy(
                maxExitMinutes = minutes,
                maxDailyMinutes = minutes
            )
        )
    }

    override suspend fun updateThemeMode(themeMode: String) = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(currentSettings.copy(themeMode = themeMode))
    }

    override suspend fun updateCalendarType(calendarType: String): Unit = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        val oldType = CalendarHelper.parseCalendarType(currentSettings.calendarType)
        val newType = CalendarHelper.parseCalendarType(calendarType)

        // 1. Fetch all existing records from DB
        val allDays = workDao.getAllDaysDirect()
        val allTargets = workDao.getAllMonthTargetsDirect()

        // 2. Filter days that have user attendance logs or notes
        val daysWithData = allDays.filter {
            it.enterHour != null || it.exitHour != null || it.note.isNotBlank()
        }

        // 3. Convert all daily logs from oldType to newType
        val convertedDays = daysWithData.map { day ->
            val isGregorianSource = if (day.year > 1600) true else if (day.year < 1600) false else (oldType == CalendarType.GREGORIAN)
            val actualFromType = if (isGregorianSource) CalendarType.GREGORIAN else CalendarType.HIJRI_SHAMSI
            val convertedDate = CalendarHelper.convertDate(day.year, day.month, day.dayNumber, actualFromType, newType)

            day.copy(
                year = convertedDate.year,
                month = convertedDate.month,
                dayNumber = convertedDate.day
            )
        }

        // 4. Convert past month targets to corresponding new calendar year/month
        val convertedTargets = allTargets.map { target ->
            val isGregorianSource = if (target.year > 1600) true else if (target.year < 1600) false else (oldType == CalendarType.GREGORIAN)
            val actualFromType = if (isGregorianSource) CalendarType.GREGORIAN else CalendarType.HIJRI_SHAMSI
            val convertedDate = CalendarHelper.convertDate(target.year, target.month, 15, actualFromType, newType)

            MonthTargetEntity(
                year = convertedDate.year,
                month = convertedDate.month,
                dailyRequiredMinutes = target.dailyRequiredMinutes
            )
        }.distinctBy { Pair(it.year, it.month) }

        // 5. Replace DB contents with converted items
        workDao.deleteEntireWorkDaysTable()
        if (convertedDays.isNotEmpty()) {
            workDao.insertDays(convertedDays)
        }

        workDao.deleteAllMonthTargetsDirect()
        if (convertedTargets.isNotEmpty()) {
            workDao.insertMonthTargets(convertedTargets)
        }

        // 6. Save updated calendarType in settings
        workDao.saveSettings(currentSettings.copy(calendarType = calendarType))

        // 7. Initialize months for the new calendar
        val now = CalendarHelper.now(newType)
        initializeMonthIfEmpty(now.year, now.month)

        val uniqueMonths: List<Pair<Int, Int>> = convertedDays.map { Pair(it.year, it.month) }.distinct()
        for (pair in uniqueMonths) {
            val y = pair.first
            val m = pair.second
            if (y != now.year || m != now.month) {
                initializeMonthIfEmpty(y, m)
            }
        }
    }

    override suspend fun updateOffDaysOfWeek(offDaysString: String, currentYear: Int, currentMonth: Int) = withContext(Dispatchers.IO) {
        val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        val calType = CalendarHelper.parseCalendarType(currentSettings.calendarType)
        workDao.saveSettings(currentSettings.copy(offDaysOfWeek = offDaysString))
        // Apply off days to un-entered days in current month
        val offDaysList = if (offDaysString.isBlank()) emptyList() else offDaysString.split(",").map { it.trim() }
        val daysInMonth = CalendarHelper.getDaysInMonth(currentYear, currentMonth, calType)
        for (dayNum in 1..daysInMonth) {
            val day = workDao.getDay(currentYear, currentMonth, dayNum)
            if (day != null) {
                val dow = CalendarHelper.getDayOfWeek(currentYear, currentMonth, dayNum, calType)
                val isSelectedOff = dow != null && offDaysList.contains(dow.name)
                if (day.enterHour == null && day.exitHour == null) {
                    workDao.insertOrUpdateDay(day.copy(isDayOff = isSelectedOff))
                } else if (isSelectedOff) {
                    workDao.insertOrUpdateDay(day.copy(isDayOff = true))
                } else {
                    workDao.insertOrUpdateDay(day.copy(isDayOff = false))
                }
            }
        }
    }

    override suspend fun resetMonthDays(year: Int, month: Int) = withContext(Dispatchers.IO) {
        workDao.clearMonthDays(year, month)
    }

    override suspend fun resetAllDays() = withContext(Dispatchers.IO) {
        workDao.clearAllDays()
    }

    override suspend fun setCompletedOnboarding(completed: Boolean) = withContext(Dispatchers.IO) {
        val current = workDao.getSettingsDirect() ?: AppSettingsEntity()
        workDao.saveSettings(current.copy(hasCompletedOnboarding = completed))
    }

    override suspend fun getDayDirect(year: Int, month: Int, dayNumber: Int): WorkDay? = withContext(Dispatchers.IO) {
        workDao.getDay(year, month, dayNumber)?.toDomain()
    }

    override suspend fun updateMonthDailyTarget(year: Int, month: Int, dailyRequiredMinutes: Int) = withContext(Dispatchers.IO) {
        workDao.saveMonthTarget(MonthTargetEntity(year, month, dailyRequiredMinutes))
    }

    override suspend fun exportAllDataJson(): String = withContext(Dispatchers.IO) {
        val settings = workDao.getSettingsDirect() ?: AppSettingsEntity()
        val monthTargets = workDao.getAllMonthTargetsDirect()
        val workDays = workDao.getAllDaysDirect()
        com.example.util.DataBackupHelper.generateBackupJson(settings, monthTargets, workDays)
    }

    override suspend fun importAllDataJson(jsonString: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val backupData = com.example.util.DataBackupHelper.parseBackupJson(jsonString)

            // 1. Restore settings if present
            backupData.settings?.let { s ->
                workDao.saveSettings(s)
            }

            // 2. Restore month targets if present
            if (backupData.monthTargets.isNotEmpty()) {
                workDao.insertMonthTargets(backupData.monthTargets)
            }

            // 3. Restore work days if present
            if (backupData.workDays.isNotEmpty()) {
                workDao.insertDays(backupData.workDays)
            }

            // 4. Make sure current month is initialized
            val currentSettings = workDao.getSettingsDirect() ?: AppSettingsEntity()
            val calType = CalendarHelper.parseCalendarType(currentSettings.calendarType)
            val now = CalendarHelper.now(calType)
            initializeMonthIfEmpty(now.year, now.month)

            val summaryMsg = "Restored ${backupData.workDays.size} daily logs and ${backupData.monthTargets.size} monthly configurations"
            Result.success(summaryMsg)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
