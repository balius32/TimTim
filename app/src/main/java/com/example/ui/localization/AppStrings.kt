package com.example.ui.localization

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.ui.theme.LocalIsFarsi
import com.example.util.CalendarHelper
import com.example.util.LocaleHelper
import com.example.util.localizeDigits

/**
 * Centralized String resource accessor for the TimTim application.
 *
 * Resource file location in project:
 * - `app/src/main/res/values/strings.xml`
 *
 * Usage in Jetpack Compose:
 *
 * 1. Native Compose `stringResource`:
 * ```kotlin
 * Text(text = stringResource(R.string.btn_save))
 * ```
 *
 * 2. Using `AppStrings` helpers:
 * ```kotlin
 * Button(onClick = { ... }) {
 *     Text(text = AppStrings.Buttons.save())
 * }
 * ```
 */
object AppStrings {

    object Units {
        val hRes: Int = R.string.unit_h
        val mRes: Int = R.string.unit_m
        val sRes: Int = R.string.unit_s
        val daysRes: Int = R.string.unit_days
        val daysCapitalRes: Int = R.string.unit_days_capital
        val timePlaceholderRes: Int = R.string.time_placeholder
        val durationPlaceholderRes: Int = R.string.duration_placeholder
        val durationEmptyHmRes: Int = R.string.duration_empty_hm

        @Composable
        @ReadOnlyComposable
        fun h(): String = stringResource(hRes)

        @Composable
        @ReadOnlyComposable
        fun m(): String = stringResource(mRes)

        @Composable
        @ReadOnlyComposable
        fun s(): String = stringResource(sRes)

        @Composable
        @ReadOnlyComposable
        fun timePlaceholder(): String = stringResource(timePlaceholderRes)

        @Composable
        @ReadOnlyComposable
        fun durationPlaceholder(): String = stringResource(durationPlaceholderRes)

        @Composable
        @ReadOnlyComposable
        fun durationEmptyHm(): String = stringResource(durationEmptyHmRes, h(), m())

        @Composable
        @ReadOnlyComposable
        fun days(count: Int): String {
            val isFarsi = LocalIsFarsi.current
            return stringResource(daysRes, count).localizeDigits(isFarsi)
        }

        @Composable
        @ReadOnlyComposable
        fun daysCapital(count: Int): String {
            val isFarsi = LocalIsFarsi.current
            return stringResource(daysCapitalRes, count).localizeDigits(isFarsi)
        }

        @Composable
        @ReadOnlyComposable
        fun formatDuration(minutes: Int): String {
            val absMins = kotlin.math.abs(minutes)
            val hours = absMins / 60
            val remMins = absMins % 60
            val isFarsi = LocalIsFarsi.current
            if (isFarsi) {
                val raw = String.format(java.util.Locale.US, "%d:%02d", hours, remMins)
                return raw.localizeDigits(isFarsi = true)
            }
            return stringResource(R.string.duration_format_hm, hours, h(), remMins, m()).localizeDigits(false)
        }

        @Composable
        @ReadOnlyComposable
        fun formatHoursOnly(hours: Int): String {
            val isFarsi = LocalIsFarsi.current
            if (isFarsi) {
                val raw = String.format(java.util.Locale.US, "%d:00", hours)
                return raw.localizeDigits(isFarsi = true)
            }
            return stringResource(R.string.duration_format_h, hours, h()).localizeDigits(false)
        }

        @Composable
        @ReadOnlyComposable
        fun formatMinutesOnly(minutes: Int): String {
            val isFarsi = LocalIsFarsi.current
            if (isFarsi) {
                val raw = String.format(java.util.Locale.US, "0:%02d", minutes)
                return raw.localizeDigits(isFarsi = true)
            }
            return stringResource(R.string.duration_format_m, minutes, m()).localizeDigits(false)
        }

        @Composable
        @ReadOnlyComposable
        fun formatSignedDuration(minutes: Int): String {
            val formatted = formatDuration(minutes)
            val isFarsi = LocalIsFarsi.current
            return when {
                minutes > 0 -> if (isFarsi) "+$formatted" else stringResource(R.string.duration_plus_signed, formatted)
                minutes < 0 -> if (isFarsi) "-$formatted" else stringResource(R.string.duration_minus_signed, formatted)
                else -> formatted
            }
        }

        fun formatDuration(context: Context, minutes: Int): String {
            val absMins = kotlin.math.abs(minutes)
            val hours = absMins / 60
            val remMins = absMins % 60
            val isFarsi = LocaleHelper.isFarsi(context)
            if (isFarsi) {
                val raw = String.format(java.util.Locale.US, "%d:%02d", hours, remMins)
                return raw.localizeDigits(isFarsi = true)
            }
            val hStr = context.getString(hRes)
            val mStr = context.getString(mRes)
            return context.getString(R.string.duration_format_hm, hours, hStr, remMins, mStr).localizeDigits(false)
        }

        fun formatSignedDuration(context: Context, minutes: Int): String {
            val formatted = formatDuration(context, minutes)
            val isFarsi = LocaleHelper.isFarsi(context)
            return when {
                minutes > 0 -> if (isFarsi) "+$formatted" else context.getString(R.string.duration_plus_signed, formatted)
                minutes < 0 -> if (isFarsi) "-$formatted" else context.getString(R.string.duration_minus_signed, formatted)
                else -> formatted
            }
        }
    }

    object Status {
        val dayOffRes: Int = R.string.status_day_off
        val inProgressRes: Int = R.string.status_in_progress
        val notLoggedRes: Int = R.string.status_not_logged
        val metTargetRes: Int = R.string.status_met_target
        val overtimeRes: Int = R.string.status_overtime
        val deficitRes: Int = R.string.status_deficit
        val offBadgeRes: Int = R.string.status_off_badge
        val onTargetRes: Int = R.string.status_on_target
        val overtimeOrDeficitRes: Int = R.string.status_overtime_or_deficit
        val scheduledDayOffRes: Int = R.string.status_scheduled_day_off
        val enjoyRestRes: Int = R.string.status_enjoy_rest

        @Composable
        @ReadOnlyComposable
        fun dayOff(): String = stringResource(dayOffRes)

        @Composable
        @ReadOnlyComposable
        fun inProgress(): String = stringResource(inProgressRes)

        @Composable
        @ReadOnlyComposable
        fun notLogged(): String = stringResource(notLoggedRes)

        @Composable
        @ReadOnlyComposable
        fun metTarget(): String = stringResource(metTargetRes)

        @Composable
        @ReadOnlyComposable
        fun overtime(): String = stringResource(overtimeRes)

        @Composable
        @ReadOnlyComposable
        fun deficit(): String = stringResource(deficitRes)

        @Composable
        @ReadOnlyComposable
        fun offBadge(): String = stringResource(offBadgeRes)

        @Composable
        @ReadOnlyComposable
        fun onTarget(): String = stringResource(onTargetRes)

        @Composable
        @ReadOnlyComposable
        fun overtimeOrDeficit(): String = stringResource(overtimeOrDeficitRes)

        @Composable
        @ReadOnlyComposable
        fun scheduledDayOff(): String = stringResource(scheduledDayOffRes)

        @Composable
        @ReadOnlyComposable
        fun enjoyRest(): String = stringResource(enjoyRestRes)

        fun localizedName(context: Context, status: com.example.domain.model.DayStatus): String = when (status) {
            com.example.domain.model.DayStatus.DAY_OFF -> context.getString(dayOffRes)
            com.example.domain.model.DayStatus.OVERTIME -> context.getString(overtimeRes)
            com.example.domain.model.DayStatus.DEFICIT -> context.getString(deficitRes)
            com.example.domain.model.DayStatus.EXACT -> context.getString(metTargetRes)
            com.example.domain.model.DayStatus.IN_PROGRESS -> context.getString(inProgressRes)
            com.example.domain.model.DayStatus.UNSET -> context.getString(notLoggedRes)
        }
    }

    object App {
        val nameRes: Int = R.string.app_name
        val subtitleRes: Int = R.string.app_subtitle
        val versionRes: Int = R.string.app_version
        val betaRes: Int = R.string.label_beta

        @Composable
        @ReadOnlyComposable
        fun name(): String = stringResource(nameRes)

        @Composable
        @ReadOnlyComposable
        fun subtitle(): String = stringResource(subtitleRes)

        @Composable
        @ReadOnlyComposable
        fun version(): String = stringResource(versionRes)

        @Composable
        @ReadOnlyComposable
        fun beta(): String = stringResource(betaRes)
    }

    object Buttons {
        val saveRes: Int = R.string.btn_save
        val cancelRes: Int = R.string.btn_cancel
        val confirmRes: Int = R.string.btn_confirm
        val doneRes: Int = R.string.btn_done
        val closeRes: Int = R.string.btn_close
        val backRes: Int = R.string.btn_back
        val nextRes: Int = R.string.btn_next
        val skipRes: Int = R.string.btn_skip
        val editRes: Int = R.string.btn_edit
        val deleteRes: Int = R.string.btn_delete
        val clearRes: Int = R.string.btn_clear
        val resetRes: Int = R.string.btn_reset
        val resetAllRes: Int = R.string.btn_reset_all
        val applyRes: Int = R.string.btn_apply
        val selectRes: Int = R.string.btn_select
        val changeRes: Int = R.string.btn_change
        val shareRes: Int = R.string.btn_share
        val exportRes: Int = R.string.btn_export
        val importRes: Int = R.string.btn_import
        val nowRes: Int = R.string.btn_now
        val todayRes: Int = R.string.btn_today
        val exitNowRes: Int = R.string.btn_exit_now
        val checkInRes: Int = R.string.btn_check_in
        val checkOutRes: Int = R.string.btn_check_out
        val getStartedRes: Int = R.string.btn_get_started
        val continueRes: Int = R.string.btn_continue
        val retryRes: Int = R.string.btn_retry
        val okRes: Int = R.string.btn_ok
        val dismissRes: Int = R.string.btn_dismiss

        @Composable
        @ReadOnlyComposable
        fun save(): String = stringResource(saveRes)

        @Composable
        @ReadOnlyComposable
        fun cancel(): String = stringResource(cancelRes)

        @Composable
        @ReadOnlyComposable
        fun confirm(): String = stringResource(confirmRes)

        @Composable
        @ReadOnlyComposable
        fun done(): String = stringResource(doneRes)

        @Composable
        @ReadOnlyComposable
        fun close(): String = stringResource(closeRes)

        @Composable
        @ReadOnlyComposable
        fun back(): String = stringResource(backRes)

        @Composable
        @ReadOnlyComposable
        fun next(): String = stringResource(nextRes)

        @Composable
        @ReadOnlyComposable
        fun skip(): String = stringResource(skipRes)

        @Composable
        @ReadOnlyComposable
        fun edit(): String = stringResource(editRes)

        @Composable
        @ReadOnlyComposable
        fun delete(): String = stringResource(deleteRes)

        @Composable
        @ReadOnlyComposable
        fun clear(): String = stringResource(clearRes)

        @Composable
        @ReadOnlyComposable
        fun reset(): String = stringResource(resetRes)

        @Composable
        @ReadOnlyComposable
        fun resetAll(): String = stringResource(resetAllRes)

        @Composable
        @ReadOnlyComposable
        fun apply(): String = stringResource(applyRes)

        @Composable
        @ReadOnlyComposable
        fun exitNow(): String = stringResource(exitNowRes)

        @Composable
        @ReadOnlyComposable
        fun checkIn(): String = stringResource(checkInRes)

        @Composable
        @ReadOnlyComposable
        fun checkOut(): String = stringResource(checkOutRes)
    }

    object Titles {
        val timesheetRes: Int = R.string.title_timesheet
        val profileRes: Int = R.string.title_profile
        val settingsRes: Int = R.string.title_settings
        val reportRes: Int = R.string.title_report
        val remainingTimeRes: Int = R.string.title_remaining_time
        val onboardingRes: Int = R.string.title_onboarding

        @Composable
        @ReadOnlyComposable
        fun timesheet(): String = stringResource(timesheetRes)

        @Composable
        @ReadOnlyComposable
        fun profile(): String = stringResource(profileRes)

        @Composable
        @ReadOnlyComposable
        fun settings(): String = stringResource(settingsRes)

        @Composable
        @ReadOnlyComposable
        fun report(): String = stringResource(reportRes)

        @Composable
        @ReadOnlyComposable
        fun remainingTime(): String = stringResource(remainingTimeRes)

        @Composable
        @ReadOnlyComposable
        fun onboarding(): String = stringResource(onboardingRes)
    }

    object Timesheet {
        val totalWorkRes: Int = R.string.timesheet_total_work
        val overtimeRes: Int = R.string.timesheet_overtime
        val deficitRes: Int = R.string.timesheet_deficit
        val workSummaryRes: Int = R.string.timesheet_work_summary
        val todayRes: Int = R.string.timesheet_today
        val enterTimeRes: Int = R.string.timesheet_enter_time
        val exitTimeRes: Int = R.string.timesheet_exit_time
        val presenceRes: Int = R.string.timesheet_presence
        val vacationRes: Int = R.string.timesheet_vacation
        val sickLeaveRes: Int = R.string.timesheet_sick_leave
        val missionRes: Int = R.string.timesheet_mission
        val holidayRes: Int = R.string.timesheet_holiday
        val offDayRes: Int = R.string.timesheet_off_day
        val noRecordRes: Int = R.string.timesheet_no_record
        val editEnterRes: Int = R.string.timesheet_edit_enter
        val editExitRes: Int = R.string.timesheet_edit_exit
        val clearEnterRes: Int = R.string.timesheet_clear_enter
        val clearExitRes: Int = R.string.timesheet_clear_exit
        val emptyDaysRes: Int = R.string.timesheet_empty_days
        val quickActiveRes: Int = R.string.timesheet_quick_action_active
        val quickNotStartedRes: Int = R.string.timesheet_quick_action_not_started
        val dayNumberRes: Int = R.string.timesheet_day_number

        @Composable
        @ReadOnlyComposable
        fun totalWork(): String = stringResource(totalWorkRes)

        @Composable
        @ReadOnlyComposable
        fun overtime(): String = stringResource(overtimeRes)

        @Composable
        @ReadOnlyComposable
        fun deficit(): String = stringResource(deficitRes)

        @Composable
        @ReadOnlyComposable
        fun workSummary(): String = stringResource(workSummaryRes)

        @Composable
        @ReadOnlyComposable
        fun today(): String = stringResource(todayRes)

        @Composable
        @ReadOnlyComposable
        fun enterTime(): String = stringResource(enterTimeRes)

        @Composable
        @ReadOnlyComposable
        fun exitTime(): String = stringResource(exitTimeRes)

        @Composable
        @ReadOnlyComposable
        fun dayNumber(day: Int): String = stringResource(dayNumberRes, day)
    }

    object RemainingTime {
        val headerRes: Int = R.string.remaining_time_header
        val overtimeBadgeRes: Int = R.string.remaining_time_overtime_badge
        val remainingBadgeRes: Int = R.string.remaining_time_remaining_badge
        val checkInRes: Int = R.string.remaining_time_check_in
        val estCheckoutRes: Int = R.string.remaining_time_est_checkout
        val elapsedRes: Int = R.string.remaining_time_elapsed
        val dailyProgressRes: Int = R.string.remaining_time_daily_progress
        val targetRes: Int = R.string.remaining_time_target
        val notStartedRes: Int = R.string.remaining_time_not_started
        val exitButtonRes: Int = R.string.remaining_time_exit_button
        val subtitleRes: Int = R.string.remaining_time_subtitle

        @Composable
        @ReadOnlyComposable
        fun header(): String = stringResource(headerRes)

        @Composable
        @ReadOnlyComposable
        fun overtime(): String = stringResource(overtimeBadgeRes)

        @Composable
        @ReadOnlyComposable
        fun remaining(): String = stringResource(remainingBadgeRes)

        @Composable
        @ReadOnlyComposable
        fun checkIn(): String = stringResource(checkInRes)

        @Composable
        @ReadOnlyComposable
        fun estCheckout(): String = stringResource(estCheckoutRes)

        @Composable
        @ReadOnlyComposable
        fun elapsed(): String = stringResource(elapsedRes)

        @Composable
        @ReadOnlyComposable
        fun dailyProgress(): String = stringResource(dailyProgressRes)

        @Composable
        @ReadOnlyComposable
        fun target(): String = stringResource(targetRes)

        @Composable
        @ReadOnlyComposable
        fun notStarted(): String = stringResource(notStartedRes)

        @Composable
        @ReadOnlyComposable
        fun exitButton(): String = stringResource(exitButtonRes)
    }

    object TimePicker {
        val titleEnterRes: Int = R.string.time_picker_title_enter
        val titleExitRes: Int = R.string.time_picker_title_exit
        val hourRes: Int = R.string.time_picker_hour
        val minuteRes: Int = R.string.time_picker_minute
        val clearTimeRes: Int = R.string.time_picker_clear_time
        val setNowRes: Int = R.string.time_picker_set_now
        val confirmRes: Int = R.string.time_picker_confirm
        val warningExitBeforeEnterRes: Int = R.string.time_picker_warning_exit_before_enter

        @Composable
        @ReadOnlyComposable
        fun titleEnter(day: Int): String = stringResource(titleEnterRes, day)

        @Composable
        @ReadOnlyComposable
        fun titleExit(day: Int): String = stringResource(titleExitRes, day)

        @Composable
        @ReadOnlyComposable
        fun hour(): String = stringResource(hourRes)

        @Composable
        @ReadOnlyComposable
        fun minute(): String = stringResource(minuteRes)

        @Composable
        @ReadOnlyComposable
        fun clearTime(): String = stringResource(clearTimeRes)

        @Composable
        @ReadOnlyComposable
        fun setNow(): String = stringResource(setNowRes)

        @Composable
        @ReadOnlyComposable
        fun confirm(): String = stringResource(confirmRes)
    }

    object Report {
        val titleRes: Int = R.string.report_title
        val summaryTitleRes: Int = R.string.report_summary_title
        val workedHoursRes: Int = R.string.report_worked_hours
        val targetHoursRes: Int = R.string.report_target_hours
        val netDifferenceRes: Int = R.string.report_net_difference
        val presenceDaysRes: Int = R.string.report_presence_days
        val attendanceOverviewRes: Int = R.string.report_attendance_overview
        val presentDaysRes: Int = R.string.report_present_days
        val offHolidaysRes: Int = R.string.report_off_holidays
        val hourlyLeavesRes: Int = R.string.report_hourly_leaves
        val dailyLeavesRes: Int = R.string.report_daily_leaves
        val dailySickLeavesRes: Int = R.string.report_daily_sick_leaves
        val missionDaysRes: Int = R.string.report_mission_days
        val distributionTitleRes: Int = R.string.report_distribution_title
        val exportTitleRes: Int = R.string.report_export_title
        val exportTextRes: Int = R.string.report_export_text
        val exportCsvRes: Int = R.string.report_export_csv
        val exportPdfRes: Int = R.string.report_export_pdf

        @Composable
        @ReadOnlyComposable
        fun title(): String = stringResource(titleRes)

        @Composable
        @ReadOnlyComposable
        fun summaryTitle(): String = stringResource(summaryTitleRes)

        @Composable
        @ReadOnlyComposable
        fun workedHours(): String = stringResource(workedHoursRes)

        @Composable
        @ReadOnlyComposable
        fun targetHours(): String = stringResource(targetHoursRes)

        @Composable
        @ReadOnlyComposable
        fun netDifference(): String = stringResource(netDifferenceRes)

        @Composable
        @ReadOnlyComposable
        fun exportTitle(): String = stringResource(exportTitleRes)
    }

    object Profile {
        val titleRes: Int = R.string.profile_title
        val defaultNameRes: Int = R.string.profile_default_name
        val editNameRes: Int = R.string.profile_edit_name
        val enterNameHintRes: Int = R.string.profile_enter_name_hint
        val changeAvatarRes: Int = R.string.profile_change_avatar
        val chooseAvatarRes: Int = R.string.profile_choose_avatar
        val chooseAvatarDescRes: Int = R.string.profile_choose_avatar_desc
        val reportSubtitleRes: Int = R.string.profile_report_subtitle
        val settingsSubtitleRes: Int = R.string.profile_settings_subtitle
        val privacyTitleRes: Int = R.string.profile_privacy_title
        val privacySubtitleRes: Int = R.string.profile_privacy_subtitle
        val localStorageNoticeRes: Int = R.string.profile_local_storage_notice

        @Composable
        @ReadOnlyComposable
        fun title(): String = stringResource(titleRes)

        @Composable
        @ReadOnlyComposable
        fun defaultName(): String = stringResource(defaultNameRes)

        @Composable
        @ReadOnlyComposable
        fun editName(): String = stringResource(editNameRes)

        @Composable
        @ReadOnlyComposable
        fun changeAvatar(): String = stringResource(changeAvatarRes)
    }

    object Settings {
        val titleRes: Int = R.string.settings_title
        val workScheduleRes: Int = R.string.settings_work_schedule
        val dailyRequiredHoursRes: Int = R.string.settings_daily_required_hours
        val hoursRes: Int = R.string.settings_hours
        val minutesRes: Int = R.string.settings_minutes
        val calendarTypeRes: Int = R.string.settings_calendar_type
        val calendarSolarRes: Int = R.string.settings_calendar_solar
        val calendarGregorianRes: Int = R.string.settings_calendar_gregorian
        val appearanceRes: Int = R.string.settings_appearance
        val themeSystemRes: Int = R.string.settings_theme_system
        val themeLightRes: Int = R.string.settings_theme_light
        val themeDarkRes: Int = R.string.settings_theme_dark
        val notificationsRes: Int = R.string.settings_notifications
        val remindCheckInRes: Int = R.string.settings_remind_check_in
        val remindCheckOutRes: Int = R.string.settings_remind_check_out
        val dataManagementRes: Int = R.string.settings_data_management
        val backupRestoreRes: Int = R.string.settings_backup_restore
        val exportBackupRes: Int = R.string.settings_export_backup
        val importBackupRes: Int = R.string.settings_import_backup
        val resetAllDataRes: Int = R.string.settings_reset_all_data
        val resetConfirmTitleRes: Int = R.string.settings_reset_confirm_title
        val resetConfirmMessageRes: Int = R.string.settings_reset_confirm_message

        @Composable
        @ReadOnlyComposable
        fun title(): String = stringResource(titleRes)

        @Composable
        @ReadOnlyComposable
        fun workSchedule(): String = stringResource(workScheduleRes)

        @Composable
        @ReadOnlyComposable
        fun dailyRequiredHours(): String = stringResource(dailyRequiredHoursRes)

        @Composable
        @ReadOnlyComposable
        fun calendarType(): String = stringResource(calendarTypeRes)

        @Composable
        @ReadOnlyComposable
        fun appearance(): String = stringResource(appearanceRes)
    }

    object Messages {
        val savedSuccessfullyRes: Int = R.string.msg_saved_successfully
        val clearedSuccessfullyRes: Int = R.string.msg_cleared_successfully
        val backupExportedRes: Int = R.string.msg_backup_exported
        val backupImportedRes: Int = R.string.msg_backup_imported
        val invalidTimeRes: Int = R.string.msg_invalid_time
        val exitBeforeEnterRes: Int = R.string.msg_exit_before_enter
        val allDaysResetRes: Int = R.string.msg_all_days_reset

        @Composable
        @ReadOnlyComposable
        fun savedSuccessfully(): String = stringResource(savedSuccessfullyRes)

        @Composable
        @ReadOnlyComposable
        fun clearedSuccessfully(): String = stringResource(clearedSuccessfullyRes)

        @Composable
        @ReadOnlyComposable
        fun invalidTime(): String = stringResource(invalidTimeRes)
    }

    object Accessibility {
        val backButtonRes: Int = R.string.cd_back_button
        val profileButtonRes: Int = R.string.cd_profile_button
        val settingsButtonRes: Int = R.string.cd_settings_button
        val calendarIconRes: Int = R.string.cd_calendar_icon
        val clockIconRes: Int = R.string.cd_clock_icon
        val statusIconRes: Int = R.string.cd_status_icon
        val avatarRes: Int = R.string.cd_avatar
        val closeRes: Int = R.string.cd_close

        @Composable
        @ReadOnlyComposable
        fun backButton(): String = stringResource(backButtonRes)

        @Composable
        @ReadOnlyComposable
        fun close(): String = stringResource(closeRes)
    }

    object Dates {
        // Day of Week Res IDs
        val mondayRes: Int = R.string.day_monday
        val tuesdayRes: Int = R.string.day_tuesday
        val wednesdayRes: Int = R.string.day_wednesday
        val thursdayRes: Int = R.string.day_thursday
        val fridayRes: Int = R.string.day_friday
        val saturdayRes: Int = R.string.day_saturday
        val sundayRes: Int = R.string.day_sunday

        val mondayShortRes: Int = R.string.day_monday_short
        val tuesdayShortRes: Int = R.string.day_tuesday_short
        val wednesdayShortRes: Int = R.string.day_wednesday_short
        val thursdayShortRes: Int = R.string.day_thursday_short
        val fridayShortRes: Int = R.string.day_friday_short
        val saturdayShortRes: Int = R.string.day_saturday_short
        val sundayShortRes: Int = R.string.day_sunday_short

        // Gregorian Month Res IDs
        val januaryRes: Int = R.string.month_january
        val februaryRes: Int = R.string.month_february
        val marchRes: Int = R.string.month_march
        val aprilRes: Int = R.string.month_april
        val mayRes: Int = R.string.month_may
        val juneRes: Int = R.string.month_june
        val julyRes: Int = R.string.month_july
        val augustRes: Int = R.string.month_august
        val septemberRes: Int = R.string.month_september
        val octoberRes: Int = R.string.month_october
        val novemberRes: Int = R.string.month_november
        val decemberRes: Int = R.string.month_december

        val januaryShortRes: Int = R.string.month_january_short
        val februaryShortRes: Int = R.string.month_february_short
        val marchShortRes: Int = R.string.month_march_short
        val aprilShortRes: Int = R.string.month_april_short
        val mayShortRes: Int = R.string.month_may_short
        val juneShortRes: Int = R.string.month_june_short
        val julyShortRes: Int = R.string.month_july_short
        val augustShortRes: Int = R.string.month_august_short
        val septemberShortRes: Int = R.string.month_september_short
        val octoberShortRes: Int = R.string.month_october_short
        val novemberShortRes: Int = R.string.month_november_short
        val decemberShortRes: Int = R.string.month_december_short

        // Solar Hijri Month Res IDs
        val farvardinRes: Int = R.string.month_farvardin
        val ordibeheshtRes: Int = R.string.month_ordibehesht
        val khordadRes: Int = R.string.month_khordad
        val tirRes: Int = R.string.month_tir
        val mordadRes: Int = R.string.month_mordad
        val shahrivarRes: Int = R.string.month_shahrivar
        val mehrRes: Int = R.string.month_mehr
        val abanRes: Int = R.string.month_aban
        val azarRes: Int = R.string.month_azar
        val deyRes: Int = R.string.month_dey
        val bahmanRes: Int = R.string.month_bahman
        val esfandRes: Int = R.string.month_esfand

        val farvardinShortRes: Int = R.string.month_farvardin_short
        val ordibeheshtShortRes: Int = R.string.month_ordibehesht_short
        val khordadShortRes: Int = R.string.month_khordad_short
        val tirShortRes: Int = R.string.month_tir_short
        val mordadShortRes: Int = R.string.month_mordad_short
        val shahrivarShortRes: Int = R.string.month_shahrivar_short
        val mehrShortRes: Int = R.string.month_mehr_short
        val abanShortRes: Int = R.string.month_aban_short
        val azarShortRes: Int = R.string.month_azar_short
        val deyShortRes: Int = R.string.month_dey_short
        val bahmanShortRes: Int = R.string.month_bahman_short
        val esfandShortRes: Int = R.string.month_esfand_short

        // Date Picker Strings
        val selectMonthYearRes: Int = R.string.picker_select_month_year
        val jumpToPeriodRes: Int = R.string.picker_jump_to_period
        val currentMonthRes: Int = R.string.picker_current_month
        val previousYearRes: Int = R.string.picker_previous_year
        val nextYearRes: Int = R.string.picker_next_year
        val backToTodayRes: Int = R.string.date_back_to_today

        fun getDayRes(dayOfWeek: java.time.DayOfWeek): Int = when (dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> mondayRes
            java.time.DayOfWeek.TUESDAY -> tuesdayRes
            java.time.DayOfWeek.WEDNESDAY -> wednesdayRes
            java.time.DayOfWeek.THURSDAY -> thursdayRes
            java.time.DayOfWeek.FRIDAY -> fridayRes
            java.time.DayOfWeek.SATURDAY -> saturdayRes
            java.time.DayOfWeek.SUNDAY -> sundayRes
        }

        fun getDayShortRes(dayOfWeek: java.time.DayOfWeek): Int = when (dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> mondayShortRes
            java.time.DayOfWeek.TUESDAY -> tuesdayShortRes
            java.time.DayOfWeek.WEDNESDAY -> wednesdayShortRes
            java.time.DayOfWeek.THURSDAY -> thursdayShortRes
            java.time.DayOfWeek.FRIDAY -> fridayShortRes
            java.time.DayOfWeek.SATURDAY -> saturdayShortRes
            java.time.DayOfWeek.SUNDAY -> sundayShortRes
        }

        fun getMonthRes(month: Int, calendarType: com.example.util.CalendarType = com.example.util.CalendarType.GREGORIAN): Int =
            CalendarHelper.getMonthResId(month, calendarType)

        fun getMonthShortRes(month: Int, calendarType: com.example.util.CalendarType = com.example.util.CalendarType.GREGORIAN): Int =
            CalendarHelper.getShortMonthResId(month, calendarType)

        @Composable
        @ReadOnlyComposable
        fun dayName(dayOfWeek: java.time.DayOfWeek): String = stringResource(getDayRes(dayOfWeek))

        @Composable
        @ReadOnlyComposable
        fun dayShortName(dayOfWeek: java.time.DayOfWeek): String = stringResource(getDayShortRes(dayOfWeek))

        @Composable
        @ReadOnlyComposable
        fun monthName(month: Int, calendarType: com.example.util.CalendarType = com.example.util.CalendarType.GREGORIAN): String =
            stringResource(getMonthRes(month, calendarType))

        @Composable
        @ReadOnlyComposable
        fun monthShortName(month: Int, calendarType: com.example.util.CalendarType = com.example.util.CalendarType.GREGORIAN): String =
            stringResource(getMonthShortRes(month, calendarType))

        @Composable
        @ReadOnlyComposable
        fun selectMonthYear(): String = stringResource(selectMonthYearRes)

        @Composable
        @ReadOnlyComposable
        fun jumpToPeriod(): String = stringResource(jumpToPeriodRes)

        @Composable
        @ReadOnlyComposable
        fun currentMonth(): String = stringResource(currentMonthRes)

        @Composable
        @ReadOnlyComposable
        fun previousYear(): String = stringResource(previousYearRes)

        @Composable
        @ReadOnlyComposable
        fun nextYear(): String = stringResource(nextYearRes)
    }
}
