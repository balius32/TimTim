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

    fun formattedTotalWorked(isFarsi: Boolean = false): String = formatMinutes(totalWorkedMinutes, isFarsi)
    fun formattedRequiredTotal(isFarsi: Boolean = false): String = formatMinutes(requiredTotalMinutes, isFarsi)
    fun formattedDailyTarget(isFarsi: Boolean = false): String = formatMinutes(dailyTargetMinutes, isFarsi)
    fun formattedOvertime(isFarsi: Boolean = false): String = if (overtimeMinutes > 0) "+${formatMinutes(overtimeMinutes, isFarsi)}" else formatMinutes(0, isFarsi)
    fun formattedDeficit(isFarsi: Boolean = false): String = if (deficitMinutes > 0) "-${formatMinutes(deficitMinutes, isFarsi)}" else formatMinutes(0, isFarsi)

    fun formattedNetBalance(isFarsi: Boolean = false): String {
        if (netBalanceMinutes == 0) return formatMinutes(0, isFarsi)
        val sign = if (netBalanceMinutes > 0) "+" else "-"
        val absVal = kotlin.math.abs(netBalanceMinutes)
        return "$sign${formatMinutes(absVal, isFarsi)}"
    }

    val totalWorkedDecimalHours: String
        get() = String.format(java.util.Locale.getDefault(), "%.1fh", totalWorkedMinutes / 60.0)

    fun totalWorkedDecimalHours(isFarsi: Boolean = false): String {
        val res = String.format(java.util.Locale.getDefault(), "%.1fh", totalWorkedMinutes / 60.0)
        return if (isFarsi) toPersianDigits(res) else res
    }

    val averageDailyMinutes: Int
        get() = if (completedDaysCount > 0) totalWorkedMinutes / completedDaysCount else 0

    fun formattedAverageDaily(isFarsi: Boolean = false): String = formatMinutes(averageDailyMinutes, isFarsi)

    companion object {
        fun formatMinutes(totalMins: Int, isFarsi: Boolean = false): String {
            val h = totalMins / 60
            val m = totalMins % 60
            val res = "${h}h ${m}m"
            return if (isFarsi) toPersianDigits(res) else res
        }

        fun toPersianDigits(input: String): String {
            val builder = StringBuilder(input.length)
            for (char in input) {
                when (char) {
                    '0' -> builder.append('۰')
                    '1' -> builder.append('۱')
                    '2' -> builder.append('۲')
                    '3' -> builder.append('۳')
                    '4' -> builder.append('۴')
                    '5' -> builder.append('۵')
                    '6' -> builder.append('۶')
                    '7' -> builder.append('۷')
                    '8' -> builder.append('۸')
                    '9' -> builder.append('۹')
                    else -> builder.append(char)
                }
            }
            return builder.toString()
        }
    }
}
