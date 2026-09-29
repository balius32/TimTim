package com.example.domain.usecase

import com.example.domain.model.DayStatus
import com.example.domain.model.DaySummary
import com.example.domain.model.WorkCalculationSummary
import com.example.domain.model.WorkDay

class CalculateMonthSummaryUseCase {

    operator fun invoke(
        days: List<WorkDay>,
        dailyRequiredMinutes: Int,
        minDailyMinutes: Int? = null,
        maxDailyMinutes: Int? = null
    ): WorkCalculationSummary {
        var totalWorkedMins = 0
        var overtimeMins = 0
        var deficitMins = 0
        var completedCount = 0
        var offDaysCount = 0
        var overtimeDaysCount = 0
        var deficitDaysCount = 0
        var exactDaysCount = 0

        val targetForCalc = if (dailyRequiredMinutes > 0) dailyRequiredMinutes else 480

        val summaries = days.map { day ->
            if (day.isDayOff) {
                offDaysCount++
                DaySummary(
                    day = day,
                    workedMinutes = 0,
                    targetMinutes = 0,
                    diffMinutes = 0,
                    status = DayStatus.DAY_OFF
                )
            } else if (day.isComplete) {
                completedCount++
                val worked = day.workedMinutes

                totalWorkedMins += worked
                val diff = worked - targetForCalc

                val status = when {
                    diff > 0 -> {
                        overtimeMins += diff
                        overtimeDaysCount++
                        DayStatus.OVERTIME
                    }
                    diff < 0 -> {
                        val deficit = -diff
                        deficitMins += deficit
                        deficitDaysCount++
                        DayStatus.DEFICIT
                    }
                    else -> {
                        exactDaysCount++
                        DayStatus.EXACT
                    }
                }
                DaySummary(
                    day = day,
                    workedMinutes = worked,
                    targetMinutes = targetForCalc,
                    diffMinutes = diff,
                    status = status
                )
            } else if (day.hasEnterTime || day.hasExitTime) {
                DaySummary(
                    day = day,
                    workedMinutes = 0,
                    targetMinutes = targetForCalc,
                    diffMinutes = 0,
                    status = DayStatus.IN_PROGRESS
                )
            } else {
                DaySummary(
                    day = day,
                    workedMinutes = 0,
                    targetMinutes = targetForCalc,
                    diffMinutes = 0,
                    status = DayStatus.UNSET
                )
            }
        }

        val totalDays = days.size
        val workingDays = days.count { !it.isDayOff }
        val requiredTotal = workingDays * targetForCalc
        val netBalance = overtimeMins - deficitMins
        val loggedDaysCount = completedCount + offDaysCount

        return WorkCalculationSummary(
            totalWorkedMinutes = totalWorkedMins,
            requiredTotalMinutes = requiredTotal,
            overtimeMinutes = overtimeMins,
            deficitMinutes = deficitMins,
            netBalanceMinutes = netBalance,
            completedDaysCount = completedCount,
            loggedDaysCount = loggedDaysCount,
            totalWorkDaysCount = totalDays,
            workingDaysCount = workingDays,
            offDaysCount = offDaysCount,
            overtimeDaysCount = overtimeDaysCount,
            deficitDaysCount = deficitDaysCount,
            exactDaysCount = exactDaysCount,
            daySummaries = summaries,
            dailyTargetMinutes = targetForCalc
        )
    }
}
