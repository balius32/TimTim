package com.example.util

import androidx.annotation.StringRes
import com.example.R
import java.time.LocalDate

/**
 * Persian / Solar Hijri (Shamsi) Calendar Utility.
 * Provides accurate bidirectional conversion between Gregorian and Solar Hijri (Jalali) dates,
 * valid day counts per Shamsi month (months 1-6 have 31 days, 7-11 have 30 days, 12 has 29 or 30 days in leap years),
 * Shamsi month names, and standard Gregorian day-of-week names (Saturday, Sunday, ...).
 */
object PersianDateHelper {

    data class PersianDate(
        val year: Int,
        val month: Int, // 1 to 12
        val day: Int    // 1 to 31
    )

    val HIJRI_MONTH_RES_IDS = listOf(
        R.string.month_farvardin,
        R.string.month_ordibehesht,
        R.string.month_khordad,
        R.string.month_tir,
        R.string.month_mordad,
        R.string.month_shahrivar,
        R.string.month_mehr,
        R.string.month_aban,
        R.string.month_azar,
        R.string.month_dey,
        R.string.month_bahman,
        R.string.month_esfand
    )

    val MONTH_NAMES: List<String>
        get() = try {
            val ctx = com.example.MainApplication.instance
            HIJRI_MONTH_RES_IDS.map { ctx.getString(it) }
        } catch (e: Exception) {
            listOf(
                "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar",
                "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand"
            )
        }

    @StringRes
    fun getMonthResId(month: Int): Int {
        return when (month) {
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

    @StringRes
    fun getShortMonthResId(month: Int): Int {
        return when (month) {
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

    fun getMonthName(month: Int, context: android.content.Context? = null): String {
        val resId = getMonthResId(month)
        return try {
            val ctx = context ?: try { com.example.MainApplication.instance } catch (e: Exception) { null }
            ctx?.getString(resId) ?: if (month in 1..12) MONTH_NAMES[month - 1] else "Month $month"
        } catch (e: Exception) {
            if (month in 1..12) MONTH_NAMES[month - 1] else "Month $month"
        }
    }

    /**
     * Checks whether a Shamsi year is a leap year (Kabiseh).
     */
    fun isLeapYear(year: Int): Boolean {
        // Algorithm for 33-year cycle Persian leap year
        val a = 0.025
        val b = 266
        var leap = ((year + 38) * 31) % 128
        return leap < 31
    }

    /**
     * Returns the exact number of days in the specified Persian year and month:
     * - Months 1..6: 31 days
     * - Months 7..11: 30 days
     * - Month 12: 30 days if leap year, else 29 days
     */
    fun getDaysInMonth(year: Int, month: Int): Int {
        return when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            month == 12 -> if (isLeapYear(year)) 30 else 29
            else -> 30
        }
    }

    /**
     * Converts a Gregorian LocalDate to PersianDate.
     */
    fun toPersianDate(gregorianDate: LocalDate): PersianDate {
        return gregorianToPersian(
            gregorianDate.year,
            gregorianDate.monthValue,
            gregorianDate.dayOfMonth
        )
    }

    /**
     * Returns today's Persian date.
     */
    fun nowPersian(): PersianDate {
        return toPersianDate(LocalDate.now())
    }

    /**
     * Converts a Persian year, month, day to Gregorian LocalDate.
     */
    fun toGregorianDate(persianYear: Int, persianMonth: Int, persianDay: Int): LocalDate {
        val (gy, gm, gd) = persianToGregorian(persianYear, persianMonth, persianDay)
        return LocalDate.of(gy, gm, gd)
    }

    /**
     * Returns Java DayOfWeek (e.g. DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, ...) for a given Shamsi date.
     */
    fun getDayOfWeek(persianYear: Int, persianMonth: Int, persianDay: Int): java.time.DayOfWeek? {
        return try {
            val daysInM = getDaysInMonth(persianYear, persianMonth)
            val safeDay = persianDay.coerceIn(1, daysInM)
            toGregorianDate(persianYear, persianMonth, safeDay).dayOfWeek
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Returns the Gregorian day of week label for a given date:
     * Saturday, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday.
     */
    fun getDayOfWeekLabel(persianYear: Int, persianMonth: Int, persianDay: Int, context: android.content.Context? = null): String {
        return try {
            val daysInM = getDaysInMonth(persianYear, persianMonth)
            val safeDay = persianDay.coerceIn(1, daysInM)
            val gDate = toGregorianDate(persianYear, persianMonth, safeDay)
            val dow = gDate.dayOfWeek
            val ctx = context ?: try { com.example.MainApplication.instance } catch (e: Exception) { null }
            ctx?.getString(CalendarHelper.getDayOfWeekResId(dow))
                ?: CalendarHelper.getDayOfWeekName(dow, CalendarHelper.isFarsi(ctx))
        } catch (e: Exception) {
            "Day"
        }
    }

    fun getShortDayOfWeekLabel(persianYear: Int, persianMonth: Int, persianDay: Int, context: android.content.Context? = null): String {
        return try {
            val daysInM = getDaysInMonth(persianYear, persianMonth)
            val safeDay = persianDay.coerceIn(1, daysInM)
            val gDate = toGregorianDate(persianYear, persianMonth, safeDay)
            val dow = gDate.dayOfWeek
            val ctx = context ?: try { com.example.MainApplication.instance } catch (e: Exception) { null }
            ctx?.getString(CalendarHelper.getShortDayOfWeekResId(dow))
                ?: CalendarHelper.getShortDayOfWeekName(dow, CalendarHelper.isFarsi(ctx))
        } catch (e: Exception) {
            "Day"
        }
    }

    /**
     * Standard Gregorian to Jalali/Persian algorithm.
     */
    private fun gregorianToPersian(gy: Int, gm: Int, gd: Int): PersianDate {
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        var gy2 = gy - 1600
        var gm2 = gm - 1
        var gd2 = gd - 1

        var gDayNo = 365 * gy2 + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400)
        for (i in 0 until gm2) {
            gDayNo += gDaysInMonth[i + 1]
        }
        if (gm2 > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd2

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        var jd = 0
        for (i in 0 until 11) {
            val daysInCurrentM = if (i < 6) 31 else 30
            if (jDayNo < daysInCurrentM) {
                jm = i + 1
                jd = jDayNo + 1
                break
            }
            jDayNo -= daysInCurrentM
        }
        if (jm == 0) {
            jm = 12
            jd = jDayNo + 1
        }

        return PersianDate(jy, jm, jd)
    }

    /**
     * Standard Jalali to Gregorian algorithm.
     */
    private fun persianToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        var jy2 = jy - 979
        var jm2 = jm - 1
        var jd2 = jd - 1

        var jDayNo = 365 * jy2 + (jy2 / 33) * 8 + ((jy2 % 33 + 3) / 4)
        for (i in 0 until jm2) {
            jDayNo += if (i < 6) 31 else 30
        }
        jDayNo += jd2

        var gDayNo = jDayNo + 79

        var gy = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097

        var leap = true
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524)
            gDayNo %= 36524
            if (gDayNo >= 365) {
                gDayNo++
            } else {
                leap = false
            }
        }

        gy += 4 * (gDayNo / 1461)
        gDayNo %= 1461

        if (gDayNo >= 366) {
            leap = false
            gDayNo--
            gy += gDayNo / 365
            gDayNo %= 365
        }

        val gDaysInMonth = intArrayOf(
            31,
            if (leap || (gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)) 29 else 28,
            31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        )

        var gm = 0
        var gd = 0
        for (i in 0 until 12) {
            if (gDayNo < gDaysInMonth[i]) {
                gm = i + 1
                gd = gDayNo + 1
                break
            }
            gDayNo -= gDaysInMonth[i]
        }

        return Triple(gy, gm, gd)
    }
}
