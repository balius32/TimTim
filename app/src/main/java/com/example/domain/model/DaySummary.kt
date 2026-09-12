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
    fun formattedWorkedDuration(isFarsi: Boolean = false): String {
        if (day.isDayOff) return if (isFarsi) "تعطیل" else "Day Off"
        if (!day.isComplete) {
            return if (day.hasEnterTime || day.hasExitTime) (if (isFarsi) "در حال کار" else "In Progress") else (if (isFarsi) "ثبت‌نشده" else "Not Logged")
        }
        val mins = workedMinutes
        val hours = mins / 60
        val remainingMins = mins % 60
        return if (isFarsi) {
            WorkCalculationSummary.toPersianDigits("${hours} ساعت و ${remainingMins} دقیقه")
        } else {
            "${hours}h ${remainingMins}m"
        }
    }

    fun formattedWorkedHours(isFarsi: Boolean = false): String {
        if (day.isDayOff) return if (isFarsi) "تعطیل" else "Off"
        if (!day.isComplete) return "--"
        val mins = workedMinutes
        val h = mins / 60
        val m = mins % 60
        return if (isFarsi) {
            if (h > 0) {
                WorkCalculationSummary.toPersianDigits("${h} ساعت و ${m} دقیقه")
            } else {
                WorkCalculationSummary.toPersianDigits("${m} دقیقه")
            }
        } else {
            if (h > 0) "${h}h ${m}m" else "${m}m"
        }
    }

    /**
     * Estimated checkout formatted string: based on day's enter time and the target minutes.
     */
    fun formattedEstimatedCheckout(isFarsi: Boolean = false): String {
        val target = if (targetMinutes > 0) targetMinutes else 480
        val res = day.formattedEstimatedCheckout(target)
        return if (isFarsi) WorkCalculationSummary.toPersianDigits(res) else res
    }

    val isOvertime: Boolean
        get() = diffMinutes > 0

    val isDeficit: Boolean
        get() = diffMinutes < 0
}
