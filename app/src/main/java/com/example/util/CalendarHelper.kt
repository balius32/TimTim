package com.example.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

enum class CalendarType(
    val id: String,
    val title: String,
    val subtitle: String
) {
    GREGORIAN(
        id = "GREGORIAN",
        title = "Gregorian",
        subtitle = "Standard calendar (January – December)"
    ),
    HIJRI_SHAMSI(
        id = "HIJRI_SHAMSI",
        title = "Hijri Shamsi",
        subtitle = "Solar Hijri calendar (Farvardin – Esfand)"
    )
}

data class CurrentDate(
    val year: Int,
    val month: Int,
    val day: Int
)

data class WeekdayDefinition(
    val dayOfWeek: DayOfWeek,
    val gregorianName: String,
    val gregorianShort: String,
    val shamsiName: String,
    val shamsiShort: String
)

object CalendarHelper {

    val GREGORIAN_MONTHS = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val GREGORIAN_WEEKDAYS = listOf(
        WeekdayDefinition(DayOfWeek.MONDAY, "Monday", "Mon", "Doshanbeh", "2-Shanbeh"),
        WeekdayDefinition(DayOfWeek.TUESDAY, "Tuesday", "Tue", "Seshanbeh", "3-Shanbeh"),
        WeekdayDefinition(DayOfWeek.WEDNESDAY, "Wednesday", "Wed", "Chaharshanbeh", "4-Shanbeh"),
        WeekdayDefinition(DayOfWeek.THURSDAY, "Thursday", "Thu", "Panjshanbeh", "5-Shanbeh"),
        WeekdayDefinition(DayOfWeek.FRIDAY, "Friday", "Fri", "Jomeh", "Jomeh"),
        WeekdayDefinition(DayOfWeek.SATURDAY, "Saturday", "Sat", "Shanbeh", "Shanbeh"),
        WeekdayDefinition(DayOfWeek.SUNDAY, "Sunday", "Sun", "Yekshanbeh", "1-Shanbeh")
    )

    val SHAMSI_WEEKDAYS = listOf(
        WeekdayDefinition(DayOfWeek.SATURDAY, "Saturday", "Sat", "Shanbeh", "Shanbeh"),
        WeekdayDefinition(DayOfWeek.SUNDAY, "Sunday", "Sun", "Yekshanbeh", "1-Shanbeh"),
        WeekdayDefinition(DayOfWeek.MONDAY, "Monday", "Mon", "Doshanbeh", "2-Shanbeh"),
        WeekdayDefinition(DayOfWeek.TUESDAY, "Tuesday", "Tue", "Seshanbeh", "3-Shanbeh"),
        WeekdayDefinition(DayOfWeek.WEDNESDAY, "Wednesday", "Wed", "Chaharshanbeh", "4-Shanbeh"),
        WeekdayDefinition(DayOfWeek.THURSDAY, "Thursday", "Thu", "Panjshanbeh", "5-Shanbeh"),
        WeekdayDefinition(DayOfWeek.FRIDAY, "Friday", "Fri", "Jomeh", "Jomeh")
    )

    fun parseCalendarType(typeString: String?): CalendarType {
        return when (typeString?.trim()?.uppercase()) {
            "HIJRI_SHAMSI", "SHAMSI", "PERSIAN", "JALALI", "HIJRI SHAMSI" -> CalendarType.HIJRI_SHAMSI
            else -> CalendarType.GREGORIAN // Default is Gregorian
        }
    }

    fun now(calendarType: CalendarType): CurrentDate {
        return when (calendarType) {
            CalendarType.GREGORIAN -> {
                val now = LocalDate.now()
                CurrentDate(now.year, now.monthValue, now.dayOfMonth)
            }
            CalendarType.HIJRI_SHAMSI -> {
                val p = PersianDateHelper.nowPersian()
                CurrentDate(p.year, p.month, p.day)
            }
        }
    }

    fun getDaysInMonth(year: Int, month: Int, calendarType: CalendarType): Int {
        return when (calendarType) {
            CalendarType.GREGORIAN -> {
                try {
                    val safeMonth = month.coerceIn(1, 12)
                    YearMonth.of(year, safeMonth).lengthOfMonth()
                } catch (e: Exception) {
                    30
                }
            }
            CalendarType.HIJRI_SHAMSI -> {
                PersianDateHelper.getDaysInMonth(year, month)
            }
        }
    }

    fun getMonthName(month: Int, calendarType: CalendarType): String {
        return when (calendarType) {
            CalendarType.GREGORIAN -> {
                if (month in 1..12) GREGORIAN_MONTHS[month - 1] else "Month $month"
            }
            CalendarType.HIJRI_SHAMSI -> {
                PersianDateHelper.getMonthName(month)
            }
        }
    }

    fun getDayOfWeek(year: Int, month: Int, day: Int, calendarType: CalendarType): DayOfWeek? {
        return try {
            when (calendarType) {
                CalendarType.GREGORIAN -> {
                    val daysInM = getDaysInMonth(year, month, calendarType)
                    val safeDay = day.coerceIn(1, daysInM)
                    LocalDate.of(year, month.coerceIn(1, 12), safeDay).dayOfWeek
                }
                CalendarType.HIJRI_SHAMSI -> {
                    PersianDateHelper.getDayOfWeek(year, month, day)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getDayOfWeekLabel(year: Int, month: Int, day: Int, calendarType: CalendarType): String {
        return when (calendarType) {
            CalendarType.GREGORIAN -> {
                val dow = getDayOfWeek(year, month, day, calendarType) ?: return "Day"
                when (dow) {
                    DayOfWeek.MONDAY -> "Monday"
                    DayOfWeek.TUESDAY -> "Tuesday"
                    DayOfWeek.WEDNESDAY -> "Wednesday"
                    DayOfWeek.THURSDAY -> "Thursday"
                    DayOfWeek.FRIDAY -> "Friday"
                    DayOfWeek.SATURDAY -> "Saturday"
                    DayOfWeek.SUNDAY -> "Sunday"
                }
            }
            CalendarType.HIJRI_SHAMSI -> {
                PersianDateHelper.getDayOfWeekLabel(year, month, day)
            }
        }
    }

    fun getShortDayOfWeekLabel(year: Int, month: Int, day: Int, calendarType: CalendarType): String {
        return when (calendarType) {
            CalendarType.GREGORIAN -> {
                val dow = getDayOfWeek(year, month, day, calendarType) ?: return "Day"
                when (dow) {
                    DayOfWeek.MONDAY -> "Mon"
                    DayOfWeek.TUESDAY -> "Tue"
                    DayOfWeek.WEDNESDAY -> "Wed"
                    DayOfWeek.THURSDAY -> "Thu"
                    DayOfWeek.FRIDAY -> "Fri"
                    DayOfWeek.SATURDAY -> "Sat"
                    DayOfWeek.SUNDAY -> "Sun"
                }
            }
            CalendarType.HIJRI_SHAMSI -> {
                PersianDateHelper.getShortDayOfWeekLabel(year, month, day)
            }
        }
    }

    fun getWeekdayList(calendarType: CalendarType): List<WeekdayDefinition> {
        return when (calendarType) {
            CalendarType.GREGORIAN -> GREGORIAN_WEEKDAYS
            CalendarType.HIJRI_SHAMSI -> SHAMSI_WEEKDAYS
        }
    }

    /**
     * Converts a calendar date from one CalendarType system to another accurately.
     * Gregorian <-> Solar Hijri (Persian)
     */
    fun convertDate(
        year: Int,
        month: Int,
        day: Int,
        fromType: CalendarType,
        toType: CalendarType
    ): CurrentDate {
        if (fromType == toType) {
            return CurrentDate(year, month, day)
        }

        return when {
            fromType == CalendarType.GREGORIAN && toType == CalendarType.HIJRI_SHAMSI -> {
                try {
                    val daysInM = getDaysInMonth(year, month, CalendarType.GREGORIAN)
                    val safeDay = day.coerceIn(1, daysInM)
                    val safeMonth = month.coerceIn(1, 12)
                    val gDate = LocalDate.of(year, safeMonth, safeDay)
                    val p = PersianDateHelper.toPersianDate(gDate)
                    CurrentDate(p.year, p.month, p.day)
                } catch (e: Exception) {
                    val pNow = PersianDateHelper.nowPersian()
                    CurrentDate(pNow.year, pNow.month, pNow.day)
                }
            }
            fromType == CalendarType.HIJRI_SHAMSI && toType == CalendarType.GREGORIAN -> {
                try {
                    val daysInM = getDaysInMonth(year, month, CalendarType.HIJRI_SHAMSI)
                    val safeDay = day.coerceIn(1, daysInM)
                    val safeMonth = month.coerceIn(1, 12)
                    val gDate = PersianDateHelper.toGregorianDate(year, safeMonth, safeDay)
                    CurrentDate(gDate.year, gDate.monthValue, gDate.dayOfMonth)
                } catch (e: Exception) {
                    val now = LocalDate.now()
                    CurrentDate(now.year, now.monthValue, now.dayOfMonth)
                }
            }
            else -> CurrentDate(year, month, day)
        }
    }
}
