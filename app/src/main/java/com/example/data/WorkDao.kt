package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkDao {

    @Query("SELECT * FROM work_days WHERE year = :year AND month = :month ORDER BY dayNumber ASC")
    fun getDaysForMonth(year: Int, month: Int): Flow<List<WorkDayEntity>>

    @Query("SELECT * FROM work_days WHERE year = :year AND month = :month AND dayNumber = :dayNumber LIMIT 1")
    suspend fun getDay(year: Int, month: Int, dayNumber: Int): WorkDayEntity?

    @Query("SELECT COUNT(*) FROM work_days WHERE year = :year AND month = :month")
    suspend fun getDaysCountForMonth(year: Int, month: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(days: List<WorkDayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDay(day: WorkDayEntity)

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)

    @Query("SELECT * FROM month_targets WHERE year = :year AND month = :month LIMIT 1")
    fun getMonthTargetFlow(year: Int, month: Int): Flow<MonthTargetEntity?>

    @Query("SELECT * FROM month_targets WHERE year = :year AND month = :month LIMIT 1")
    suspend fun getMonthTargetDirect(year: Int, month: Int): MonthTargetEntity?

    @Query("SELECT * FROM month_targets")
    fun getAllMonthTargetsFlow(): Flow<List<MonthTargetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMonthTarget(monthTarget: MonthTargetEntity)

    @Query("DELETE FROM month_targets WHERE year > :year OR (year = :year AND month >= :month)")
    suspend fun clearFutureAndCurrentMonthTargets(year: Int, month: Int)

    @Query("SELECT * FROM work_days")
    suspend fun getAllDaysDirect(): List<WorkDayEntity>

    @Query("DELETE FROM work_days")
    suspend fun deleteEntireWorkDaysTable()

    @Query("SELECT * FROM month_targets")
    suspend fun getAllMonthTargetsDirect(): List<MonthTargetEntity>

    @Query("DELETE FROM month_targets")
    suspend fun deleteAllMonthTargetsDirect()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthTargets(targets: List<MonthTargetEntity>)

    @Query("UPDATE work_days SET enterHour = NULL, enterMinute = NULL, exitHour = NULL, exitMinute = NULL, isDayOff = 0, note = '' WHERE year = :year AND month = :month")
    suspend fun clearMonthDays(year: Int, month: Int)

    @Query("UPDATE work_days SET enterHour = NULL, enterMinute = NULL, exitHour = NULL, exitMinute = NULL, isDayOff = 0, note = ''")
    suspend fun clearAllDays()
}
