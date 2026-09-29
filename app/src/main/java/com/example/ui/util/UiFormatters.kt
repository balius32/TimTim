package com.example.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.domain.model.DayStatus
import com.example.domain.model.DaySummary
import com.example.domain.model.WorkCalculationSummary
import com.example.domain.model.WorkDay
import com.example.ui.theme.LocalIsFarsi
import com.example.util.localizeDigits

@Composable
@ReadOnlyComposable
fun formatDurationMinutes(minutes: Int): String {
    val absMins = kotlin.math.abs(minutes)
    val hours = absMins / 60
    val remMins = absMins % 60
    val isFarsi = LocalIsFarsi.current
    if (isFarsi) {
        val raw = String.format(java.util.Locale.US, "%d:%02d", hours, remMins)
        return raw.localizeDigits(isFarsi = true)
    }
    val h = stringResource(R.string.unit_h)
    val m = stringResource(R.string.unit_m)
    val raw = stringResource(R.string.duration_format_hm, hours, h, remMins, m)
    return raw.localizeDigits(isFarsi = false)
}

@Composable
@ReadOnlyComposable
fun formatDurationSigned(minutes: Int): String {
    val formatted = formatDurationMinutes(minutes)
    val isFarsi = LocalIsFarsi.current
    return when {
        minutes > 0 -> if (isFarsi) "+$formatted" else stringResource(R.string.duration_plus_signed, formatted)
        minutes < 0 -> if (isFarsi) "-$formatted" else stringResource(R.string.duration_minus_signed, formatted)
        else -> formatted
    }
}

@Composable
@ReadOnlyComposable
fun formatHoursOnly(hours: Int): String {
    val isFarsi = LocalIsFarsi.current
    if (isFarsi) {
        val raw = String.format(java.util.Locale.US, "%d:00", hours)
        return raw.localizeDigits(isFarsi = true)
    }
    val h = stringResource(R.string.unit_h)
    val raw = stringResource(R.string.duration_format_h, hours, h)
    return raw.localizeDigits(isFarsi = false)
}

@Composable
@ReadOnlyComposable
fun WorkDay.formatWorkedDuration(): String {
    if (isDayOff) return stringResource(R.string.status_day_off)
    if (!isComplete) {
        return if (hasEnterTime || hasExitTime) {
            stringResource(R.string.status_in_progress)
        } else {
            stringResource(R.string.status_not_logged)
        }
    }
    return formatDurationMinutes(workedMinutes)
}

@Composable
@ReadOnlyComposable
fun DaySummary.formatWorkedDuration(): String = day.formatWorkedDuration()

@Composable
@ReadOnlyComposable
fun DaySummary.formatDiffDuration(): String = formatDurationSigned(diffMinutes)

@Composable
@ReadOnlyComposable
fun DayStatus.localizedName(): String = when (this) {
    DayStatus.DAY_OFF -> stringResource(R.string.status_day_off)
    DayStatus.OVERTIME -> stringResource(R.string.status_overtime)
    DayStatus.DEFICIT -> stringResource(R.string.status_deficit)
    DayStatus.EXACT -> stringResource(R.string.status_met_target)
    DayStatus.IN_PROGRESS -> stringResource(R.string.status_in_progress)
    DayStatus.UNSET -> stringResource(R.string.status_not_logged)
}

@Composable
@ReadOnlyComposable
fun WorkCalculationSummary.formatTotalWorked(): String = formatDurationMinutes(totalWorkedMinutes)

@Composable
@ReadOnlyComposable
fun WorkCalculationSummary.formatHomeHeroTotalWorked(): String {
    val totalMins = totalWorkedMinutes
    val hours = totalMins / 60
    val remMins = totalMins % 60
    val isFarsi = LocalIsFarsi.current
    if (isFarsi) {
        val raw = when {
            hours > 0 && remMins > 0 -> "$hours\u00A0ساعت ، $remMins\u00A0دقیقه"
            hours > 0 -> "$hours\u00A0ساعت"
            remMins > 0 -> "$remMins\u00A0دقیقه"
            else -> "۰\u00A0ساعت"
        }
        return raw.localizeDigits(isFarsi = true)
    }
    return formatTotalWorked()
}

@Composable
@ReadOnlyComposable
fun WorkCalculationSummary.formatRequiredTotal(): String = formatDurationMinutes(requiredTotalMinutes)

@Composable
@ReadOnlyComposable
fun WorkCalculationSummary.formatDailyTarget(): String = formatDurationMinutes(dailyTargetMinutes)

@Composable
@ReadOnlyComposable
fun WorkCalculationSummary.formatOvertime(): String =
    if (overtimeMinutes > 0) formatDurationSigned(overtimeMinutes) else formatDurationMinutes(0)

@Composable
@ReadOnlyComposable
fun WorkCalculationSummary.formatDeficit(): String =
    if (deficitMinutes > 0) formatDurationSigned(-deficitMinutes) else formatDurationMinutes(0)

@Composable
@ReadOnlyComposable
fun WorkCalculationSummary.formatNetBalance(): String =
    formatDurationSigned(netBalanceMinutes)
