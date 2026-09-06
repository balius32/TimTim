package com.example.data

import androidx.room.Entity
import java.util.Locale

@Entity(
    tableName = "work_days",
    primaryKeys = ["year", "month", "dayNumber"]
)
data class WorkDayEntity(
    val year: Int = 1405,
    val month: Int = 5,
    val dayNumber: Int = 1, // 1 to 31
    val enterHour: Int? = null, // 0 to 23
    val enterMinute: Int? = null, // 0 to 59
    val exitHour: Int? = null, // 0 to 23
    val exitMinute: Int? = null, // 0 to 59
    val isDayOff: Boolean = false,
    val note: String = ""
) {
    val hasEnterTime: Boolean
        get() = enterHour != null && enterMinute != null

    val hasExitTime: Boolean
        get() = exitHour != null && exitMinute != null

    val isComplete: Boolean
        get() = hasEnterTime && hasExitTime && !isDayOff

    /**
     * Total minutes worked on this day.
     * Handles shifts crossing midnight if exit time is earlier than enter time.
     */
    val workedMinutes: Int
        get() {
            if (!isComplete) return 0
            val enterTotal = (enterHour ?: 0) * 60 + (enterMinute ?: 0)
            var exitTotal = (exitHour ?: 0) * 60 + (exitMinute ?: 0)
            if (exitTotal < enterTotal) {
                exitTotal += 24 * 60 // Crossed midnight
            }
            return exitTotal - enterTotal
        }

    fun formattedEnterTime(): String {
        return if (hasEnterTime) {
            String.format(Locale.getDefault(), "%02d:%02d", enterHour, enterMinute)
        } else {
            "_ _ : _ _"
        }
    }

    fun formattedExitTime(): String {
        return if (hasExitTime) {
            String.format(Locale.getDefault(), "%02d:%02d", exitHour, exitMinute)
        } else {
            "_ _ : _ _"
        }
    }

    fun formattedWorkedDuration(): String {
        if (isDayOff) return "Day Off"
        if (!isComplete) {
            return if (hasEnterTime || hasExitTime) "In Progress" else "Not Logged"
        }
        val mins = workedMinutes
        val hours = mins / 60
        val remainingMins = mins % 60
        return "${hours}h ${remainingMins}m"
    }
}
