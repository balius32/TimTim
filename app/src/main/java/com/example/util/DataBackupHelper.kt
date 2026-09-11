package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.AppSettingsEntity
import com.example.data.MonthTargetEntity
import com.example.data.WorkDayEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

data class BackupData(
    val version: Int = 1,
    val exportedAt: Long,
    val settings: AppSettingsEntity?,
    val monthTargets: List<MonthTargetEntity>,
    val workDays: List<WorkDayEntity>
)

object DataBackupHelper {

    fun generateBackupJson(
        settings: AppSettingsEntity?,
        monthTargets: List<MonthTargetEntity>,
        workDays: List<WorkDayEntity>
    ): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("app", "WorkTimesheet")
        root.put("exportedAt", System.currentTimeMillis())

        // Settings
        settings?.let { s ->
            val sObj = JSONObject()
            sObj.put("dailyRequiredMinutes", s.dailyRequiredMinutes)
            sObj.put("periodTitle", s.periodTitle)
            sObj.put("userName", s.userName)
            sObj.put("avatarId", s.avatarId)
            sObj.put("themeMode", s.themeMode)
            sObj.put("offDaysOfWeek", s.offDaysOfWeek)
            sObj.put("calendarType", s.calendarType)
            sObj.put("language", s.language)
            s.minDailyMinutes?.let { sObj.put("minDailyMinutes", it) }
            s.maxDailyMinutes?.let { sObj.put("maxDailyMinutes", it) }
            s.minEnterMinutes?.let { sObj.put("minEnterMinutes", it) }
            s.maxExitMinutes?.let { sObj.put("maxExitMinutes", it) }
            sObj.put("hasCompletedOnboarding", s.hasCompletedOnboarding)
            root.put("settings", sObj)
        }

        // Targets
        val targetsArray = JSONArray()
        for (t in monthTargets) {
            val tObj = JSONObject()
            tObj.put("year", t.year)
            tObj.put("month", t.month)
            tObj.put("dailyRequiredMinutes", t.dailyRequiredMinutes)
            targetsArray.put(tObj)
        }
        root.put("monthTargets", targetsArray)

        // Work Days
        val daysArray = JSONArray()
        for (d in workDays) {
            val dObj = JSONObject()
            dObj.put("year", d.year)
            dObj.put("month", d.month)
            dObj.put("dayNumber", d.dayNumber)
            if (d.enterHour != null) dObj.put("enterHour", d.enterHour)
            if (d.enterMinute != null) dObj.put("enterMinute", d.enterMinute)
            if (d.exitHour != null) dObj.put("exitHour", d.exitHour)
            if (d.exitMinute != null) dObj.put("exitMinute", d.exitMinute)
            dObj.put("isDayOff", d.isDayOff)
            dObj.put("note", d.note)
            daysArray.put(dObj)
        }
        root.put("workDays", daysArray)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): BackupData {
        val root = JSONObject(jsonString)
        val version = root.optInt("version", 1)
        val exportedAt = root.optLong("exportedAt", System.currentTimeMillis())

        val settingsObj = root.optJSONObject("settings")
        val settings = settingsObj?.let { s ->
            AppSettingsEntity(
                id = 1,
                dailyRequiredMinutes = s.optInt("dailyRequiredMinutes", 480),
                periodTitle = s.optString("periodTitle", "Monthly Work Timesheet"),
                userName = s.optString("userName", "username"),
                avatarId = s.optString("avatarId", "minimal_avatar"),
                themeMode = s.optString("themeMode", "SYSTEM"),
                offDaysOfWeek = s.optString("offDaysOfWeek", "FRIDAY"),
                calendarType = s.optString("calendarType", "GREGORIAN"),
                language = s.optString("language", "en"),
                minDailyMinutes = if (s.has("minDailyMinutes") && !s.isNull("minDailyMinutes")) s.optInt("minDailyMinutes") else null,
                maxDailyMinutes = if (s.has("maxDailyMinutes") && !s.isNull("maxDailyMinutes")) s.optInt("maxDailyMinutes") else null,
                minEnterMinutes = if (s.has("minEnterMinutes") && !s.isNull("minEnterMinutes")) s.optInt("minEnterMinutes") else null,
                maxExitMinutes = if (s.has("maxExitMinutes") && !s.isNull("maxExitMinutes")) s.optInt("maxExitMinutes") else null,
                hasCompletedOnboarding = s.optBoolean("hasCompletedOnboarding", true)
            )
        }

        val monthTargets = mutableListOf<MonthTargetEntity>()
        val targetsArray = root.optJSONArray("monthTargets")
        if (targetsArray != null) {
            for (i in 0 until targetsArray.length()) {
                val t = targetsArray.optJSONObject(i) ?: continue
                val y = t.optInt("year")
                val m = t.optInt("month")
                val dailyReq = t.optInt("dailyRequiredMinutes", 0)
                if (y > 0 && m in 1..12) {
                    monthTargets.add(
                        MonthTargetEntity(
                            year = y,
                            month = m,
                            dailyRequiredMinutes = dailyReq
                        )
                    )
                }
            }
        }

        val workDays = mutableListOf<WorkDayEntity>()
        val daysArray = root.optJSONArray("workDays")
        if (daysArray != null) {
            for (i in 0 until daysArray.length()) {
                val d = daysArray.optJSONObject(i) ?: continue
                val y = d.optInt("year")
                val m = d.optInt("month")
                val dayNum = d.optInt("dayNumber")
                if (y > 0 && m in 1..12 && dayNum in 1..31) {
                    workDays.add(
                        WorkDayEntity(
                            year = y,
                            month = m,
                            dayNumber = dayNum,
                            enterHour = if (d.has("enterHour") && !d.isNull("enterHour")) d.optInt("enterHour") else null,
                            enterMinute = if (d.has("enterMinute") && !d.isNull("enterMinute")) d.optInt("enterMinute") else null,
                            exitHour = if (d.has("exitHour") && !d.isNull("exitHour")) d.optInt("exitHour") else null,
                            exitMinute = if (d.has("exitMinute") && !d.isNull("exitMinute")) d.optInt("exitMinute") else null,
                            isDayOff = d.optBoolean("isDayOff", false),
                            note = d.optString("note", "")
                        )
                    )
                }
            }
        }

        return BackupData(
            version = version,
            exportedAt = exportedAt,
            settings = settings,
            monthTargets = monthTargets,
            workDays = workDays
        )
    }

    fun readTextFromUri(context: Context, uri: Uri): String {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { reader ->
                return reader.readText()
            }
        } ?: throw IllegalStateException("Unable to open input stream for URI")
    }

    fun writeTextToUri(context: Context, uri: Uri, content: String) {
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(content.toByteArray(Charsets.UTF_8))
            stream.flush()
        } ?: throw IllegalStateException("Unable to open output stream for URI")
    }

    fun shareBackupFile(context: Context, jsonContent: String) {
        val backupsDir = File(context.cacheDir, "backups").apply { mkdirs() }
        val fileName = "work_timesheet_backup_${System.currentTimeMillis()}.json"
        val file = File(backupsDir, fileName)
        FileOutputStream(file).use { out ->
            out.write(jsonContent.toByteArray(Charsets.UTF_8))
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Work Timesheet Backup")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Backup File"))
    }
}
