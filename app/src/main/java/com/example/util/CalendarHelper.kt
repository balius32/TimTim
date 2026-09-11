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

    val GREGORIAN_MONTHS_FA = listOf(
        "ژانویه", "فوریه", "مارس", "آوریل", "مه", "ژوئن",
        "ژوئیه", "اوت", "سپتامبر", "اکتبر", "نوامبر", "دسامبر"
    )

    val GREGORIAN_WEEKDAYS = listOf(
        WeekdayDefinition(DayOfWeek.MONDAY, "Monday", "Mon", "دوشنبه", "د"),
        WeekdayDefinition(DayOfWeek.TUESDAY, "Tuesday", "Tue", "سه‌شنبه", "س"),
        WeekdayDefinition(DayOfWeek.WEDNESDAY, "Wednesday", "Wed", "چهارشنبه", "چ"),
        WeekdayDefinition(DayOfWeek.THURSDAY, "Thursday", "Thu", "پنج‌شنبه", "پ"),
        WeekdayDefinition(DayOfWeek.FRIDAY, "Friday", "Fri", "جمعه", "ج"),
        WeekdayDefinition(DayOfWeek.SATURDAY, "Saturday", "Sat", "شنبه", "ش"),
        WeekdayDefinition(DayOfWeek.SUNDAY, "Sunday", "Sun", "یکشنبه", "ی")
    )

    val SHAMSI_WEEKDAYS = listOf(
        WeekdayDefinition(DayOfWeek.SATURDAY, "Saturday", "Sat", "شنبه", "ش"),
        WeekdayDefinition(DayOfWeek.SUNDAY, "Sunday", "Sun", "یکشنبه", "ی"),
        WeekdayDefinition(DayOfWeek.MONDAY, "Monday", "Mon", "دوشنبه", "د"),
        WeekdayDefinition(DayOfWeek.TUESDAY, "Tuesday", "Tue", "سه‌شنبه", "س"),
        WeekdayDefinition(DayOfWeek.WEDNESDAY, "Wednesday", "Wed", "چهارشنبه", "چ"),
        WeekdayDefinition(DayOfWeek.THURSDAY, "Thursday", "Thu", "پنج‌شنبه", "پ"),
        WeekdayDefinition(DayOfWeek.FRIDAY, "Friday", "Fri", "جمعه", "ج")
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

    fun getMonthName(month: Int, calendarType: CalendarType, isFarsi: Boolean = false): String {
        return when (calendarType) {
            CalendarType.GREGORIAN -> {
                if (month in 1..12) {
                    if (isFarsi) GREGORIAN_MONTHS_FA[month - 1] else GREGORIAN_MONTHS[month - 1]
                } else "Month $month"
            }
            CalendarType.HIJRI_SHAMSI -> {
                PersianDateHelper.getMonthName(month, isFarsi = isFarsi)
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

    fun getDayOfWeekLabel(year: Int, month: Int, day: Int, calendarType: CalendarType, isFarsi: Boolean = false): String {
        return when (calendarType) {
            CalendarType.GREGORIAN -> {
                val dow = getDayOfWeek(year, month, day, calendarType) ?: return if (isFarsi) "روز" else "Day"
                if (isFarsi) {
                    when (dow) {
                        DayOfWeek.MONDAY -> "دوشنبه"
                        DayOfWeek.TUESDAY -> "سه‌شنبه"
                        DayOfWeek.WEDNESDAY -> "چهارشنبه"
                        DayOfWeek.THURSDAY -> "پنج‌شنبه"
                        DayOfWeek.FRIDAY -> "جمعه"
                        DayOfWeek.SATURDAY -> "شنبه"
                        DayOfWeek.SUNDAY -> "یکشنبه"
                    }
                } else {
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
            }
            CalendarType.HIJRI_SHAMSI -> {
                PersianDateHelper.getDayOfWeekLabel(year, month, day, isFarsi = isFarsi)
            }
        }
    }

    fun getShortDayOfWeekLabel(year: Int, month: Int, day: Int, calendarType: CalendarType, isFarsi: Boolean = false): String {
        return when (calendarType) {
            CalendarType.GREGORIAN -> {
                val dow = getDayOfWeek(year, month, day, calendarType) ?: return if (isFarsi) "روز" else "Day"
                if (isFarsi) {
                    when (dow) {
                        DayOfWeek.MONDAY -> "د"
                        DayOfWeek.TUESDAY -> "س"
                        DayOfWeek.WEDNESDAY -> "چ"
                        DayOfWeek.THURSDAY -> "پ"
                        DayOfWeek.FRIDAY -> "ج"
                        DayOfWeek.SATURDAY -> "ش"
                        DayOfWeek.SUNDAY -> "ی"
                    }
                } else {
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
            }
            CalendarType.HIJRI_SHAMSI -> {
                PersianDateHelper.getShortDayOfWeekLabel(year, month, day, isFarsi = isFarsi)
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
