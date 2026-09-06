package com.example.domain.model

data class WorkCalculationSummary(
    val totalWorkedMinutes: Int = 0,
    val requiredTotalMinutes: Int = 0,
    val overtimeMinutes: Int = 0,
    val deficitMinutes: Int = 0,
    val netBalanceMinutes: Int = 0,
    val completedDaysCount: Int = 0,
    val totalWorkDaysCount: Int = 31,
    val workingDaysCount: Int = 31,
    val offDaysCount: Int = 0,
    val overtimeDaysCount: Int = 0,
    val deficitDaysCount: Int = 0,
    val exactDaysCount: Int = 0,
    val daySummaries: List<DaySummary> = emptyList(),
    val dailyTargetMinutes: Int = 480
) {
    val isNetSurplus: Boolean
        get() = netBalanceMinutes >= 0

    fun formattedTotalWorked(): String = formatMinutes(totalWorkedMinutes)
    fun formattedRequiredTotal(): String = formatMinutes(requiredTotalMinutes)
    fun formattedDailyTarget(): String = formatMinutes(dailyTargetMinutes)
    fun formattedOvertime(): String = if (overtimeMinutes > 0) "+${formatMinutes(overtimeMinutes)}" else formatMinutes(0)
    fun formattedDeficit(): String = if (deficitMinutes > 0) "-${formatMinutes(deficitMinutes)}" else formatMinutes(0)

    fun formattedNetBalance(): String {
        if (netBalanceMinutes == 0) return formatMinutes(0)
        val sign = if (netBalanceMinutes > 0) "+" else "-"
        val absVal = kotlin.math.abs(netBalanceMinutes)
        return "$sign${formatMinutes(absVal)}"
    }

    val totalWorkedDecimalHours: String
        get() = String.format(java.util.Locale.getDefault(), "%.1fh", totalWorkedMinutes / 60.0)

    val averageDailyMinutes: Int
        get() = if (completedDaysCount > 0) totalWorkedMinutes / completedDaysCount else 0

    fun formattedAverageDaily(): String = formatMinutes(averageDailyMinutes)

    companion object {
        fun formatMinutes(totalMins: Int): String {
            val h = totalMins / 60
            val m = totalMins % 60
            return "${h}h ${m}m"
        }
    }
}
