package com.example.domain.usecase

import com.example.domain.model.WorkDay
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateMonthSummaryUseCaseTest {

    private val useCase = CalculateMonthSummaryUseCase()

    @Test
    fun remoteAndInPersonCounts_excludeOffDaysAndUnset() {
        val days = listOf(
            WorkDay(dayNumber = 1, enterHour = 9, enterMinute = 0, exitHour = 17, exitMinute = 0, isRemote = true),
            WorkDay(dayNumber = 2, enterHour = 9, enterMinute = 0, exitHour = 17, exitMinute = 0, isRemote = false),
            WorkDay(dayNumber = 3, enterHour = 9, enterMinute = 0, isRemote = true), // in progress remote
            WorkDay(dayNumber = 4, isDayOff = true, isRemote = true), // off — ignored even if remote flag set
            WorkDay(dayNumber = 5) // unset — ignored
        )

        val summary = useCase(days, dailyRequiredMinutes = 480)

        assertEquals(2, summary.remoteDaysCount)
        assertEquals(1, summary.inPersonDaysCount)
    }

    @Test
    fun remoteCounts_zeroWhenNoLoggedWorkDays() {
        val days = listOf(
            WorkDay(dayNumber = 1, isDayOff = true),
            WorkDay(dayNumber = 2)
        )

        val summary = useCase(days, dailyRequiredMinutes = 480)

        assertEquals(0, summary.remoteDaysCount)
        assertEquals(0, summary.inPersonDaysCount)
    }
}
