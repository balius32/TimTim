package com.example.util

import java.util.Locale

/**
 * Utility for formatting and localizing numbers and times into Persian (Farsi) digits (۰-۹).
 * Provides bidirectional translation between ASCII Latin digits and Eastern Arabic/Persian digits.
 */
object NumberFormatter {

    /**
     * Converts all ASCII digits ('0'-'9') in the input string to Persian digits ('۰'-'۹').
     */
    fun toFarsiDigits(input: String?): String {
        if (input == null) return ""
        val sb = StringBuilder(input.length)
        for (c in input) {
            when (c) {
                '0' -> sb.append('۰')
                '1' -> sb.append('۱')
                '2' -> sb.append('۲')
                '3' -> sb.append('۳')
                '4' -> sb.append('۴')
                '5' -> sb.append('۵')
                '6' -> sb.append('۶')
                '7' -> sb.append('۷')
                '8' -> sb.append('۸')
                '9' -> sb.append('۹')
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    /**
     * Converts Persian or Arabic digits in the input string to standard ASCII Latin digits ('0'-'9').
     */
    fun toLatinDigits(input: String?): String {
        if (input == null) return ""
        val sb = StringBuilder(input.length)
        for (c in input) {
            when (c) {
                '۰', '٠' -> sb.append('0')
                '۱', '١' -> sb.append('1')
                '۲', '٢' -> sb.append('2')
                '۳', '٣' -> sb.append('3')
                '۴', '٤' -> sb.append('4')
                '۵', '٥' -> sb.append('5')
                '۶', '٦' -> sb.append('6')
                '۷', '٧' -> sb.append('7')
                '۸', '٨' -> sb.append('8')
                '۹', '٩' -> sb.append('9')
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    /**
     * Formats a number with Persian digits if [isFarsi] is true.
     */
    fun format(number: Number, isFarsi: Boolean): String {
        val raw = number.toString()
        return if (isFarsi) toFarsiDigits(raw) else raw
    }

    /**
     * Formats hours and minutes into "HH:mm" with Persian digits if [isFarsi] is true.
     */
    fun formatTime(hour: Int, minute: Int, isFarsi: Boolean): String {
        val raw = String.format(Locale.US, "%02d:%02d", hour, minute)
        return if (isFarsi) toFarsiDigits(raw) else raw
    }

    /**
     * Formats hours, minutes, and seconds into "HH:mm:ss" with Persian digits if [isFarsi] is true.
     * Each unit is always zero-padded to 2 digits (e.g. 3s → "03" / "۰۳").
     */
    fun formatTimeWithSeconds(hour: Int, minute: Int, second: Int, isFarsi: Boolean): String {
        val raw = listOf(hour, minute, second).joinToString(":") { unit ->
            unit.coerceAtLeast(0).toString().padStart(2, '0')
        }
        return if (isFarsi) toFarsiDigits(raw) else raw
    }

    /**
     * Localizes all digits in a string according to [isFarsi].
     */
    fun localize(text: String, isFarsi: Boolean): String {
        return if (isFarsi) toFarsiDigits(text) else text
    }
}

// Convenient Extension Functions
fun String.toPersianDigits(): String = NumberFormatter.toFarsiDigits(this)
fun String.toLatinDigits(): String = NumberFormatter.toLatinDigits(this)
fun String.localizeDigits(isFarsi: Boolean): String = NumberFormatter.localize(this, isFarsi)
fun Int.localizeDigits(isFarsi: Boolean): String = NumberFormatter.format(this, isFarsi)
fun Long.localizeDigits(isFarsi: Boolean): String = NumberFormatter.format(this, isFarsi)
