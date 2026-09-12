package com.example

import com.example.data.WorkDayEntity
import com.example.util.CalendarHelper
import com.example.util.CalendarType
import com.example.util.PersianDateHelper
import com.example.ui.localization.toPersianDigits
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {

    @Test
    fun testWorkDayCalculations() {
        // Standard 8h 30m shift (08:30 to 17:00) -> 510 minutes
        val day1 = WorkDayEntity(
            dayNumber = 1,
            enterHour = 8,
            enterMinute = 30,
            exitHour = 17,
            exitMinute = 0
        )
        assertTrue(day1.isComplete)
        assertEquals(510, day1.workedMinutes)
        assertEquals("8h 30m", day1.formattedWorkedDuration())
        assertEquals("08:30", day1.formattedEnterTime())
        assertEquals("17:00", day1.formattedExitTime())
    }

    @Test
    fun testOvernightShiftCalculation() {
        // Night shift 22:00 to 06:30 -> 8h 30m (510 minutes)
        val dayNight = WorkDayEntity(
            dayNumber = 2,
            enterHour = 22,
            enterMinute = 0,
            exitHour = 6,
            exitMinute = 30
        )
        assertTrue(dayNight.isComplete)
        assertEquals(510, dayNight.workedMinutes)
        assertEquals("8h 30m", dayNight.formattedWorkedDuration())
    }

    @Test
    fun testIncompleteAndDayOff() {
        val unsetDay = WorkDayEntity(dayNumber = 3)
        assertFalse(unsetDay.isComplete)
        assertEquals(0, unsetDay.workedMinutes)
        assertEquals("_ _ : _ _", unsetDay.formattedEnterTime())
        assertEquals("Not Logged", unsetDay.formattedWorkedDuration())

        val enterOnlyDay = WorkDayEntity(
            dayNumber = 4,
            enterHour = 9,
            enterMinute = 0
        )
        assertFalse(enterOnlyDay.isComplete)
        assertEquals("In Progress", enterOnlyDay.formattedWorkedDuration())

        val exitOnlyDay = WorkDayEntity(
            dayNumber = 5,
            exitHour = 17,
            exitMinute = 30
        )
        assertFalse(exitOnlyDay.isComplete)
        assertEquals("In Progress", exitOnlyDay.formattedWorkedDuration())

        val dayOff = WorkDayEntity(
            dayNumber = 6,
            enterHour = 8,
            enterMinute = 0,
            exitHour = 16,
            exitMinute = 0,
            isDayOff = true
        )
        assertFalse(dayOff.isComplete)
        assertEquals(0, dayOff.workedMinutes)
        assertEquals("Day Off", dayOff.formattedWorkedDuration())
    }

    @Test
    fun testAvatarStyleDefaults() {
        val firstStyle = com.example.ui.components.AvatarStyle.entries.first()
        assertEquals("minimal_avatar", firstStyle.id)
        assertEquals("Calm Emerald", firstStyle.title)
        assertFalse(com.example.ui.components.AvatarStyle.entries.any { it.title.equals("Wireframe", ignoreCase = true) })
    }

    @Test
    fun testPersianAndGregorianConversionRoundTrip() {
        // Test date round trip
        val gregorianDate = LocalDate.of(2026, 8, 24)
        val persianDate = PersianDateHelper.toPersianDate(gregorianDate)
        val backToGregorian = PersianDateHelper.toGregorianDate(persianDate.year, persianDate.month, persianDate.day)
        
        assertEquals(gregorianDate, backToGregorian)
    }

    @Test
    fun testCalendarDateCasting() {
        // Converting 2026-08-24 (Gregorian) to Solar Hijri
        val shamsiDate = CalendarHelper.convertDate(2026, 8, 24, CalendarType.GREGORIAN, CalendarType.HIJRI_SHAMSI)
        
        // Convert back to Gregorian
        val gregDate = CalendarHelper.convertDate(shamsiDate.year, shamsiDate.month, shamsiDate.day, CalendarType.HIJRI_SHAMSI, CalendarType.GREGORIAN)
        
        assertEquals(2026, gregDate.year)
        assertEquals(8, gregDate.month)
        assertEquals(24, gregDate.day)
    }

    @Test
    fun testTodayInAnyDateIsToday() {
        val gregNow = CalendarHelper.now(CalendarType.GREGORIAN)
        val shamsiNow = CalendarHelper.now(CalendarType.HIJRI_SHAMSI)

        // Converting today's date from Gregorian gives today's date in Shamsi
        val convertedToShamsi = CalendarHelper.convertDate(gregNow.year, gregNow.month, gregNow.day, CalendarType.GREGORIAN, CalendarType.HIJRI_SHAMSI)
        assertEquals(shamsiNow.year, convertedToShamsi.year)
        assertEquals(shamsiNow.month, convertedToShamsi.month)
        assertEquals(shamsiNow.day, convertedToShamsi.day)

        // Converting today's date from Shamsi gives today's date in Gregorian
        val convertedToGregorian = CalendarHelper.convertDate(shamsiNow.year, shamsiNow.month, shamsiNow.day, CalendarType.HIJRI_SHAMSI, CalendarType.GREGORIAN)
        assertEquals(gregNow.year, convertedToGregorian.year)
        assertEquals(gregNow.month, convertedToGregorian.month)
        assertEquals(gregNow.day, convertedToGregorian.day)
    }

    @Test
    fun testTimePickerFarsiDigitFormatting() {
        // Test that 0..23 hours and 0..59 minutes format with leading zeros and correct Persian digits
        for (h in 0..23) {
            val formattedAscii = String.format(java.util.Locale.US, "%02d", h)
            val persian = formattedAscii.toPersianDigits()
            assertEquals(2, persian.length)
            assertTrue(persian.all { char -> char in '۰'..'۹' })
        }
        assertEquals("۰۰", "00".toPersianDigits())
        assertEquals("۰۸", "08".toPersianDigits())
        assertEquals("۰۹", "09".toPersianDigits())
        assertEquals("۱۷", "17".toPersianDigits())
        assertEquals("۲۳", "23".toPersianDigits())
        assertEquals("۵۹", "59".toPersianDigits())
    }

    @Test
    fun testWheelCenteredIndexOffsets() {
        // When visible items count is 3, the center item is offset by 1
        val repeatCount = 500
        val totalHours = 24
        val initialHour = 8
        val hourStartIndex = (repeatCount / 2) * totalHours + initialHour
        val firstVisibleIndex = hourStartIndex - 1

        // Center item (index at center slot) is firstVisibleIndex + 1 = hourStartIndex
        val centerIndex = firstVisibleIndex + 1
        assertEquals(hourStartIndex, centerIndex)
        assertEquals(initialHour, centerIndex % totalHours)
    }
}

