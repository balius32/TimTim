package com.example.domain.model

enum class DayStatus {
    OVERTIME,
    DEFICIT,
    EXACT,
    DAY_OFF,
    IN_PROGRESS,
    UNSET
}

data class DaySummary(
    val day: WorkDay,
    val workedMinutes: Int,
    val targetMinutes: Int,
    val diffMinutes: Int, // workedMinutes - targetMinutes
    val status: DayStatus
) {
    fun formattedWorkedDuration(): String {
        if (day.isDayOff) return "Day Off"
        if (!day.isComplete) {
            return if (day.hasEnterTime || day.hasExitTime) "In Progress" else "Not Logged"
        }
        val mins = workedMinutes
        val hours = mins / 60
        val remainingMins = mins % 60
        return "${hours}h ${remainingMins}m"
    }

    fun formattedWorkedHours(): String {
        if (day.isDayOff) return "Off"
        if (!day.isComplete) return "--"
        val mins = workedMinutes
        val h = mins / 60
        val m = mins % 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }
}
