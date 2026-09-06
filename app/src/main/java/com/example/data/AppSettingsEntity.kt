package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val dailyRequiredMinutes: Int = 0, // Default 0 (unset daily target)
    val periodTitle: String = "Monthly Work Timesheet",
    val userName: String = "username",
    val avatarId: String = "minimal_avatar",
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK", "EMERALD", "PURPLE", "AMBER"
    val offDaysOfWeek: String = "FRIDAY", // Comma-separated Java DayOfWeek names e.g. "FRIDAY" or "THURSDAY,FRIDAY"
    val calendarType: String = "GREGORIAN", // "GREGORIAN" (default) or "HIJRI_SHAMSI"
    val minEnterMinutes: Int? = null, // e.g. 7 * 60 = 420 for 07:00 (if picked earlier like 06:00, sets to 07:00)
    val maxExitMinutes: Int? = null, // e.g. 19 * 60 = 1140 for 19:00 (if picked later like 20:00, sets to 19:00)
    val minDailyMinutes: Int? = null,
    val maxDailyMinutes: Int? = null,
    val hasCompletedOnboarding: Boolean = false
) {
    val dailyRequiredHours: Int
        get() = dailyRequiredMinutes / 60

    val dailyRequiredRemainderMinutes: Int
        get() = dailyRequiredMinutes % 60

    fun formattedDailyTarget(): String {
        if (dailyRequiredMinutes <= 0) return "_ _ : _ _"
        val h = dailyRequiredHours
        val m = dailyRequiredRemainderMinutes
        return if (m == 0) "${h}h 00m" else "${h}h ${m}m"
    }

    fun formattedMinEnterTime(): String {
        val mins = minEnterMinutes ?: return "None"
        val h = mins / 60
        val m = mins % 60
        return String.format("%02d:%02d", h, m)
    }

    fun formattedMaxExitTime(): String {
        val mins = maxExitMinutes ?: return "None"
        val h = mins / 60
        val m = mins % 60
        return String.format("%02d:%02d", h, m)
    }

    fun formattedMinDailyLimit(): String {
        val mins = minDailyMinutes ?: return "None"
        val h = mins / 60
        val m = mins % 60
        return String.format("%02d:%02d", h, m)
    }

    fun formattedMaxDailyLimit(): String {
        val mins = maxDailyMinutes ?: return "None"
        val h = mins / 60
        val m = mins % 60
        return String.format("%02d:%02d", h, m)
    }

    fun getOffDaysList(): List<String> {
        if (offDaysOfWeek.isBlank()) return emptyList()
        return offDaysOfWeek.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}
