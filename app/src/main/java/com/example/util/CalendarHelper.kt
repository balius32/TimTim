package com.example.util

import androidx.annotation.StringRes
import com.example.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

enum class CalendarType(
    val id: String,
    val title: String,
    val subtitle: String,
    @get:StringRes val titleRes: Int,
    @get:StringRes val subtitleRes: Int
) {
    GREGORIAN(
        id = "GREGORIAN",
        title = "Gregorian",
        subtitle = "Standard calendar (January – December)",
        titleRes = R.string.calendar_gregorian_name,
        subtitleRes = R.string.calendar_gregorian_desc
    ),
    HIJRI_SHAMSI(
        id = "HIJRI_SHAMSI",
        title = "Hijri Shamsi",
        subtitle = "Solar Hijri calendar",
        titleRes = R.string.calendar_shamsi_name,
        subtitleRes = R.string.calendar_shamsi_desc
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
    @get:StringRes val nameRes: Int = 0,
    @get:StringRes val shortNameRes: Int = 0
)

object CalendarHelper {

    val GREGORIAN_MONTH_RES_IDS = listOf(
        R.string.month_january,
        R.string.month_february,
        R.string.month_march,
        R.string.month_april,
        R.string.month_may,
        R.string.month_june,
        R.string.month_july,
        R.string.month_august,
        R.string.month_september,
        R.string.month_october,
        R.string.month_november,
        R.string.month_december
    )

    val GREGORIAN_MONTH_NAMES_EN = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val GREGORIAN_MONTH_NAMES_FA = listOf(
        "ژانویه", "فوریه", "مارس", "آوریل", "مه", "ژوئن",
        "ژوئیه", "اوت", "سپتامبر", "اکتبر", "نوامبر", "دسامبر"
    )

    val SHAMSI_MONTH_NAMES_EN = listOf(
        "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar",
        "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand"
    )

    val SHAMSI_MONTH_NAMES_FA = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    val GREGORIAN_MONTHS: List<String>
        get() = try {
            val ctx = com.example.MainApplication.instance
            GREGORIAN_MONTH_RES_IDS.map { ctx.getString(it) }
        } catch (e: Exception) {
            GREGORIAN_MONTH_NAMES_EN
        }

    val GREGORIAN_WEEKDAYS = listOf(
        WeekdayDefinition(DayOfWeek.MONDAY, "Monday", "Mon", R.string.day_monday, R.string.day_monday_short),
        WeekdayDefinition(DayOfWeek.TUESDAY, "Tuesday", "Tue", R.string.day_tuesday, R.string.day_tuesday_short),
        WeekdayDefinition(DayOfWeek.WEDNESDAY, "Wednesday", "Wed", R.string.day_wednesday, R.string.day_wednesday_short),
        WeekdayDefinition(DayOfWeek.THURSDAY, "Thursday", "Thu", R.string.day_thursday, R.string.day_thursday_short),
        WeekdayDefinition(DayOfWeek.FRIDAY, "Friday", "Fri", R.string.day_friday, R.string.day_friday_short),
        WeekdayDefinition(DayOfWeek.SATURDAY, "Saturday", "Sat", R.string.day_saturday, R.string.day_saturday_short),
        WeekdayDefinition(DayOfWeek.SUNDAY, "Sunday", "Sun", R.string.day_sunday, R.string.day_sunday_short)
    )

    val SHAMSI_WEEKDAYS = listOf(
        WeekdayDefinition(DayOfWeek.SATURDAY, "Saturday", "Sat", R.string.day_saturday, R.string.day_saturday_short),
        WeekdayDefinition(DayOfWeek.SUNDAY, "Sunday", "Sun", R.string.day_sunday, R.string.day_sunday_short),
        WeekdayDefinition(DayOfWeek.MONDAY, "Monday", "Mon", R.string.day_monday, R.string.day_monday_short),
        WeekdayDefinition(DayOfWeek.TUESDAY, "Tuesday", "Tue", R.string.day_tuesday, R.string.day_tuesday_short),
        WeekdayDefinition(DayOfWeek.WEDNESDAY, "Wednesday", "Wed", R.string.day_wednesday, R.string.day_wednesday_short),
        WeekdayDefinition(DayOfWeek.THURSDAY, "Thursday", "Thu", R.string.day_thursday, R.string.day_thursday_short),
        WeekdayDefinition(DayOfWeek.FRIDAY, "Friday", "Fri", R.string.day_friday, R.string.day_friday_short)
    )

    @StringRes
    fun getDayOfWeekResId(dayOfWeek: DayOfWeek): Int {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> R.string.day_monday
            DayOfWeek.TUESDAY -> R.string.day_tuesday
            DayOfWeek.WEDNESDAY -> R.string.day_wednesday
            DayOfWeek.THURSDAY -> R.string.day_thursday
            DayOfWeek.FRIDAY -> R.string.day_friday
            DayOfWeek.SATURDAY -> R.string.day_saturday
            DayOfWeek.SUNDAY -> R.string.day_sunday
        }
    }

    @StringRes
    fun getShortDayOfWeekResId(dayOfWeek: DayOfWeek): Int {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> R.string.day_monday_short
            DayOfWeek.TUESDAY -> R.string.day_tuesday_short
            DayOfWeek.WEDNESDAY -> R.string.day_wednesday_short
            DayOfWeek.THURSDAY -> R.string.day_thursday_short
            DayOfWeek.FRIDAY -> R.string.day_friday_short
            DayOfWeek.SATURDAY -> R.string.day_saturday_short
            DayOfWeek.SUNDAY -> R.string.day_sunday_short
        }
    }

    @StringRes
    fun getMonthResId(month: Int, calendarType: CalendarType = CalendarType.GREGORIAN): Int {
        return when (calendarType) {
            CalendarType.GREGORIAN -> when (month) {
                1 -> R.string.month_january
                2 -> R.string.month_february
                3 -> R.string.month_march
                4 -> R.string.month_april
                5 -> R.string.month_may
                6 -> R.string.month_june
                7 -> R.string.month_july
                8 -> R.string.month_august
                9 -> R.string.month_september
                10 -> R.string.month_october
                11 -> R.string.month_november
                12 -> R.string.month_december
                else -> R.string.month_january
            }
            CalendarType.HIJRI_SHAMSI -> when (month) {
                1 -> R.string.month_farvardin
                2 -> R.string.month_ordibehesht
                3 -> R.string.month_khordad
                4 -> R.string.month_tir
                5 -> R.string.month_mordad
                6 -> R.string.month_shahrivar
                7 -> R.string.month_mehr
                8 -> R.string.month_aban
                9 -> R.string.month_azar
                10 -> R.string.month_dey
                11 -> R.string.month_bahman
                12 -> R.string.month_esfand
                else -> R.string.month_farvardin
            }
        }
    }

    @StringRes
    fun getShortMonthResId(month: Int, calendarType: CalendarType = CalendarType.GREGORIAN): Int {
        return when (calendarType) {
            CalendarType.GREGORIAN -> when (month) {
                1 -> R.string.month_january_short
                2 -> R.string.month_february_short
                3 -> R.string.month_march_short
                4 -> R.string.month_april_short
                5 -> R.string.month_may_short
                6 -> R.string.month_june_short
                7 -> R.string.month_july_short
                8 -> R.string.month_august_short
                9 -> R.string.month_september_short
                10 -> R.string.month_october_short
                11 -> R.string.month_november_short
                12 -> R.string.month_december_short
                else -> R.string.month_january_short
            }
            CalendarType.HIJRI_SHAMSI -> when (month) {
                1 -> R.string.month_farvardin_short
                2 -> R.string.month_ordibehesht_short
                3 -> R.string.month_khordad_short
                4 -> R.string.month_tir_short
                5 -> R.string.month_mordad_short
                6 -> R.string.month_shahrivar_short
                7 -> R.string.month_mehr_short
                8 -> R.string.month_aban_short
                9 -> R.string.month_azar_short
                10 -> R.string.month_dey_short
                11 -> R.string.month_bahman_short
                12 -> R.string.month_esfand_short
                else -> R.string.month_farvardin_short
            }
        }
    }

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

    fun getMonthName(
        month: Int,
        calendarType: CalendarType,
        context: android.content.Context? = null,
        isFarsi: Boolean? = null
    ): String {
        val farsi = isFarsi ?: (if (context != null) isFarsi(context) else LocaleHelper.isFarsi())
        val safeMonth = month.coerceIn(1, 12)
        return if (farsi) {
            when (calendarType) {
                CalendarType.HIJRI_SHAMSI -> SHAMSI_MONTH_NAMES_FA[safeMonth - 1]
                CalendarType.GREGORIAN -> GREGORIAN_MONTH_NAMES_FA[safeMonth - 1]
            }
        } else {
            when (calendarType) {
                CalendarType.HIJRI_SHAMSI -> SHAMSI_MONTH_NAMES_EN[safeMonth - 1]
                CalendarType.GREGORIAN -> GREGORIAN_MONTH_NAMES_EN[safeMonth - 1]
            }
        }
    }

    fun getShortMonthName(
        month: Int,
        calendarType: CalendarType,
        context: android.content.Context? = null,
        isFarsi: Boolean? = null
    ): String {
        val farsi = isFarsi ?: (if (context != null) isFarsi(context) else LocaleHelper.isFarsi())
        val safeMonth = month.coerceIn(1, 12)
        return if (farsi) {
            when (calendarType) {
                CalendarType.HIJRI_SHAMSI -> SHAMSI_MONTH_NAMES_FA[safeMonth - 1]
                CalendarType.GREGORIAN -> GREGORIAN_MONTH_NAMES_FA[safeMonth - 1]
            }
        } else {
            when (calendarType) {
                CalendarType.HIJRI_SHAMSI -> SHAMSI_MONTH_NAMES_EN[safeMonth - 1].take(3)
                CalendarType.GREGORIAN -> GREGORIAN_MONTH_NAMES_EN[safeMonth - 1].take(3)
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

    fun isFarsi(context: android.content.Context? = null): Boolean {
        return LocaleHelper.isFarsi(context)
    }

    fun getDayOfWeekName(dayOfWeek: DayOfWeek, isFarsi: Boolean): String {
        return if (isFarsi) {
            when (dayOfWeek) {
                DayOfWeek.SATURDAY -> "شنبه"
                DayOfWeek.SUNDAY -> "یکشنبه"
                DayOfWeek.MONDAY -> "دوشنبه"
                DayOfWeek.TUESDAY -> "سه‌شنبه"
                DayOfWeek.WEDNESDAY -> "چهارشنبه"
                DayOfWeek.THURSDAY -> "پنج‌شنبه"
                DayOfWeek.FRIDAY -> "جمعه"
            }
        } else {
            when (dayOfWeek) {
                DayOfWeek.SATURDAY -> "Saturday"
                DayOfWeek.SUNDAY -> "Sunday"
                DayOfWeek.MONDAY -> "Monday"
                DayOfWeek.TUESDAY -> "Tuesday"
                DayOfWeek.WEDNESDAY -> "Wednesday"
                DayOfWeek.THURSDAY -> "Thursday"
                DayOfWeek.FRIDAY -> "Friday"
            }
        }
    }

    fun getShortDayOfWeekName(dayOfWeek: DayOfWeek, isFarsi: Boolean): String {
        return if (isFarsi) {
            when (dayOfWeek) {
                DayOfWeek.SATURDAY -> "شنبه"
                DayOfWeek.SUNDAY -> "یکشنبه"
                DayOfWeek.MONDAY -> "دوشنبه"
                DayOfWeek.TUESDAY -> "سه‌شنبه"
                DayOfWeek.WEDNESDAY -> "چهارشنبه"
                DayOfWeek.THURSDAY -> "پنج‌شنبه"
                DayOfWeek.FRIDAY -> "جمعه"
            }
        } else {
            when (dayOfWeek) {
                DayOfWeek.SATURDAY -> "Sat"
                DayOfWeek.SUNDAY -> "Sun"
                DayOfWeek.MONDAY -> "Mon"
                DayOfWeek.TUESDAY -> "Tue"
                DayOfWeek.WEDNESDAY -> "Wed"
                DayOfWeek.THURSDAY -> "Thu"
                DayOfWeek.FRIDAY -> "Fri"
            }
        }
    }

    fun getDayOfWeekLabel(
        year: Int,
        month: Int,
        day: Int,
        calendarType: CalendarType,
        context: android.content.Context? = null,
        isFarsi: Boolean? = null
    ): String {
        val dow = getDayOfWeek(year, month, day, calendarType) ?: return ""
        val farsi = isFarsi ?: (if (context != null) isFarsi(context) else LocaleHelper.isFarsi())
        return getDayOfWeekName(dow, farsi)
    }

    fun getShortDayOfWeekLabel(
        year: Int,
        month: Int,
        day: Int,
        calendarType: CalendarType,
        context: android.content.Context? = null,
        isFarsi: Boolean? = null
    ): String {
        val dow = getDayOfWeek(year, month, day, calendarType) ?: return ""
        val farsi = isFarsi ?: (if (context != null) isFarsi(context) else LocaleHelper.isFarsi())
        return getShortDayOfWeekName(dow, farsi)
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
