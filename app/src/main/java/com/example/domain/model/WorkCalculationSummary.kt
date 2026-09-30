package com.example.domain.model

data class WorkCalculationSummary(
    val totalWorkedMinutes: Int = 0,
    val requiredTotalMinutes: Int = 0,
    val overtimeMinutes: Int = 0,
    val deficitMinutes: Int = 0,
    val netBalanceMinutes: Int = 0,
    val completedDaysCount: Int = 0,
    val loggedDaysCount: Int = 0,
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

    fun formattedTotalWorked(isFarsi: Boolean = false): String = formatMinutes(totalWorkedMinutes, isFarsi)
    fun formattedRequiredTotal(isFarsi: Boolean = false): String = formatMinutes(requiredTotalMinutes, isFarsi)
    fun formattedDailyTarget(isFarsi: Boolean = false): String = formatMinutes(dailyTargetMinutes, isFarsi)
    fun formattedOvertime(isFarsi: Boolean = false): String =
        if (overtimeMinutes > 0) {
            com.example.util.NumberFormatter.withSign(formatMinutes(overtimeMinutes, isFarsi), positive = true, isFarsi)
        } else {
            formatMinutes(0, isFarsi)
        }

    fun formattedDeficit(isFarsi: Boolean = false): String =
        if (deficitMinutes > 0) {
            com.example.util.NumberFormatter.withSign(formatMinutes(deficitMinutes, isFarsi), positive = false, isFarsi)
        } else {
            formatMinutes(0, isFarsi)
        }

    fun formattedNetBalance(isFarsi: Boolean = false): String {
        if (netBalanceMinutes == 0) return formatMinutes(0, isFarsi)
        val absVal = kotlin.math.abs(netBalanceMinutes)
        return com.example.util.NumberFormatter.withSign(
            formatMinutes(absVal, isFarsi),
            positive = netBalanceMinutes > 0,
            isFarsi = isFarsi
        )
    }

    val totalWorkedDecimalHours: String
        get() = String.format(java.util.Locale.getDefault(), "%.1fh", totalWorkedMinutes / 60.0)

    val averageDailyMinutes: Int
        get() = if (completedDaysCount > 0) totalWorkedMinutes / completedDaysCount else 0

    fun formattedAverageDaily(isFarsi: Boolean = false): String = formatMinutes(averageDailyMinutes, isFarsi)

    companion object {
        fun formatMinutes(totalMins: Int, isFarsi: Boolean = false): String {
            val abs = kotlin.math.abs(totalMins)
            val h = abs / 60
            val m = abs % 60
            return if (isFarsi) {
                val raw = String.format(java.util.Locale.US, "%d:%02d", h, m)
                com.example.util.NumberFormatter.toFarsiDigits(raw)
            } else {
                "${h}h ${m}m"
            }
        }
    }
}
