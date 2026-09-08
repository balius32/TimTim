package com.example.domain.model

import java.util.Locale

data class WorkDay(
    val year: Int = 1405,
    val month: Int = 5,
    val dayNumber: Int = 1,
    val enterHour: Int? = null,
    val enterMinute: Int? = null,
    val exitHour: Int? = null,
    val exitMinute: Int? = null,
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

    fun formattedWorkedHours(): String {
        if (isDayOff) return "Off"
        if (!isComplete) return "--"
        val mins = workedMinutes
        val h = mins / 60
        val m = mins % 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }

    /**
     * Calculate estimated checkout total minutes from enter time plus required target minutes.
     * Returns null if enter time is not set.
     */
    fun estimatedCheckoutMinutes(targetMinutes: Int): Int? {
        if (!hasEnterTime) return null
        val enterTotal = (enterHour ?: 0) * 60 + (enterMinute ?: 0)
        return (enterTotal + targetMinutes) % (24 * 60)
    }

    /**
     * Formats estimated checkout time as HH:mm string based on enter time and target minutes.
     */
    fun formattedEstimatedCheckout(targetMinutes: Int): String {
        val est = estimatedCheckoutMinutes(targetMinutes) ?: return "_ _ : _ _"
        val h = est / 60
        val m = est % 60
        return String.format(Locale.getDefault(), "%02d:%02d", h, m)
    }
}
