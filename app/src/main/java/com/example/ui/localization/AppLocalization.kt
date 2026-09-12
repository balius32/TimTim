package com.example.ui.localization

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(
    val code: String,
    val title: String,
    val nativeTitle: String,
    val subtitle: String,
    val isRtl: Boolean
) {
    EN(
        code = "en",
        title = "English",
        nativeTitle = "English",
        subtitle = "English",
        isRtl = false
    ),
    FA(
        code = "fa",
        title = "Persian",
        nativeTitle = "فارسی",
        subtitle = "فارسی",
        isRtl = true
    );

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return when (code?.lowercase()?.trim()) {
                "fa", "farsi", "persian" -> FA
                else -> EN
            }
        }
    }
}

interface AppStrings {
    val isRtl: Boolean
    val appName: String
    val languageName: String

    // Navigation & General
    val navTimesheet: String
    val navProfile: String
    val navSettings: String
    val navReport: String
    val navRemainingTime: String
    val back: String
    val cancel: String
    val confirm: String
    val save: String
    val reset: String
    val delete: String
    val edit: String
    val done: String
    val close: String
    val warning: String
    val success: String
    val error: String
    val none: String
    val copy: String
    val copied: String
    val day: String
    val now: String
    val off: String
    val gotIt: String
    val version: String

    // Timesheet Screen
    val monthlySummary: String
    val totalHours: String
    val workedHours: String
    val targetHours: String
    val overtime: String
    val deficit: String
    val deficitTime: String
    val dailyTarget: String
    val dayOff: String
    val noWorkRequired: String
    val workDay: String
    val entryTime: String
    val enterTime: String
    val exitTime: String
    val duration: String
    val status: String
    val today: String
    val backToToday: String
    val dailyLog: String
    val tapEnterExitHint: String
    val enterTimeLabel: String
    val exitTimeLabel: String
    val estimatedCheckout: String
    val estCheckOut: String
    val setOff: String
    val cancelDayOff: String
    val clear: String
    val remainingTime: String
    val dailySummary: String
    val logEntryNow: String
    val logExitNow: String
    val quickActionCheckedIn: String
    val quickActionExitPrompt: String
    val quickActionEntryPrompt: String
    val tapToLog: String
    val setAsDayOff: String
    val setAsWorkDay: String
    val clearTimes: String
    val clearAllDaysPrompt: String
    val liveRemainingBadge: String
    val noRecordsYet: String
    val balance: String
    val completedTarget: String
    val inProgress: String
    val remainingToExit: String
    val daysCount: String
    val workedDaysCount: String
    val offDaysCount: String
    val totalDaysSuffix: String
    val onTarget: String

    // Profile & Statistics Screen
    val profile: String
    val userProfile: String
    val username: String
    val monthlyPerformance: String
    val totalWorked: String
    val workingDaysCount: String
    val targetCompletion: String
    val averageDaily: String
    val quickAccess: String
    val settings: String
    val settingsSubtitle: String
    val openSettings: String
    val reportSubtitle: String
    val viewDetailedReport: String
    val privacy: String
    val changeAvatar: String
    val editUserName: String
    val editProfileName: String
    val userNameLabel: String
    val userNamePlaceholder: String
    val statistics: String
    val performanceSummary: String
    val highestWorkedDay: String
    val lowestWorkedDay: String
    val totalOvertimeLabel: String
    val totalDeficitLabel: String
    val activeCalendar: String
    val privacyTitle: String
    val privacySubtitle: String
    val privacyPolicyTitle: String
    val privacyPolicySubtitle: String
    val privacyPoint1Title: String
    val privacyPoint1Desc: String
    val privacyPoint2Title: String
    val privacyPoint2Desc: String
    val privacyPoint3Title: String
    val privacyPoint3Desc: String
    val profileSettingsDesc: String
    val profileReportDesc: String
    val profilePrivacyDesc: String
    val privacyLocalOnly: String
    val privacyLocalOnlyDesc: String
    val privacyNoTracking: String
    val privacyNoTrackingDesc: String
    val privacyBackupControl: String
    val privacyBackupControlDesc: String

    // Settings Screen
    val settingsTitle: String
    val appearanceSection: String
    val themeMode: String
    val themeSetting: String
    val themeSystem: String
    val themeLight: String
    val themeDark: String
    val colorAccent: String
    val languageSetting: String
    val languageSubtitle: String
    val calendarSystem: String
    val calendarTypeSetting: String
    val calendarSubtitle: String
    val gregorianCalendar: String
    val gregorianCalendarTitle: String
    val gregorianDesc: String
    val shamsiCalendar: String
    val persianCalendarTitle: String
    val shamsiDesc: String
    val workScheduleSection: String
    val dailyRequiredHours: String
    val dailyRequiredSubtitle: String
    val dailyTargetCard: String
    val targetTimeSubtitle: String
    val offDaysSetting: String
    val offDaysSubtitle: String
    val workHourLimits: String
    val workHourLimitsSubtitle: String
    val workLimits: String
    val minEnterTime: String
    val maxExitTime: String
    val minDailyHours: String
    val maxDailyHours: String
    val dataBackupSection: String
    val importExport: String
    val importExportSubtitle: String
    val importExportDialogTitle: String
    val importExportDialogSubtitle: String
    val exportAllDataTitle: String
    val exportAllDataDesc: String
    val shareText: String
    val saveFileText: String
    val importSavedDataTitle: String
    val importSavedDataDesc: String
    val selectJsonFileText: String
    val exportBackup: String
    val exportBackupSubtitle: String
    val importBackup: String
    val importBackupSubtitle: String
    val resetMonthRecords: String
    val resetAllData: String
    val resetAllDataSubtitle: String
    val resetAllConfirmTitle: String
    val resetAllConfirmMessage: String
    val clearAllDataAction: String
    val selectLanguageTitle: String
    val selectThemeTitle: String
    val selectThemeSubtitle: String
    val themeModeLabel: String
    val primaryColorLabel: String
    val selectCalendarTypeTitle: String
    val selectCalendarTypeSubtitle: String
    fun getAccentColorName(id: String): String

    // Report / Results Screen
    val reportTitle: String
    val monthlyReport: String
    val shareExportReport: String
    val exportReport: String
    val selectFormatFor: String
    val pdfDocument: String
    val pdfDocumentDesc: String
    val excelCsvSpreadsheet: String
    val excelCsvDesc: String
    val textSummary: String
    val textSummaryDesc: String
    val exportPdf: String
    val exportCsv: String
    val shareSummary: String
    val netBalance: String
    val surplusHours: String
    val deficitHours: String
    val balancedState: String
    val workOverview: String
    val requiredTarget: String
    val totalOvertime: String
    val totalDeficit: String
    val monthlyWorkDistribution: String
    val completed: String
    val underTarget: String
    val attendanceProgress: String
    val worked: String
    val remainingDays: String
    val dailyBreakdown: String
    val noDataForMonth: String
    val dateCol: String
    val dayCol: String
    val inCol: String
    val outCol: String
    val totalCol: String
    val diffCol: String
    val workDistribution: String
    val attendanceMetrics: String
    val exportShare: String
    val exportOptions: String
    val exportSummaryTextTitle: String
    val exportSummaryTextDesc: String

    // Month & Year Picker
    val selectMonthYear: String
    val selectMonthYearSubtitle: String
    val selectMonthYearDesc: String
    val jumpToCurrentMonth: String
    val previousMonth: String
    val nextMonth: String

    // Remaining Time Screen & Live Clock
    val remainingTimeTitle: String
    val activeShift: String
    val liveClock: String
    val timeLeft: String
    val timeRemaining: String
    val workedSoFar: String
    val elapsedWorkTime: String
    val elapsedWorkingTime: String
    val targetExitTime: String
    val overtimeRunning: String
    val overtimeAccumulated: String
    val targetAchieved: String
    val targetReached: String
    val ofTarget: String
    val checkIn: String
    val exitNow: String
    val exitNowButton: String
    val pickExitTime: String
    val liveWorkingTimer: String
    val dailyProgress: String
    val expectedExitAt: String

    // Time Picker Dialog
    val setEntryTimeTitle: String
    val setExitTimeTitle: String
    val hour: String
    val minute: String
    val setToNow: String
    val clearTime: String
    val timeValidationError: String
    val exitTimeCannotBeEarlier: String
    val enterTimeCannotBeLater: String
    val dailyTargetDesc: String

    // Onboarding Screen
    val welcomeTitle: String
    val welcomeSubtitle: String
    val selectLanguageStep: String
    val chooseCalendarStep: String
    val setDailyTargetStep: String
    val selectOffDaysStep: String
    val getStarted: String
    val skip: String
    val restoreBackupPrompt: String
    val restoreFromFile: String
    val continueSetup: String

    // Weekday Names
    val saturday: String
    val sunday: String
    val monday: String
    val tuesday: String
    val wednesday: String
    val thursday: String
    val friday: String

    val saturdayShort: String
    val sundayShort: String
    val mondayShort: String
    val tuesdayShort: String
    val wednesdayShort: String
    val thursdayShort: String
    val fridayShort: String
    val hourShort: String
    val minuteShort: String

    // Formatter functions
    fun getDayOfWeek(dayOfWeek: java.time.DayOfWeek): String
    fun getShortDayOfWeek(dayOfWeek: java.time.DayOfWeek): String
    fun formatHourMinute(hours: Int, minutes: Int): String
    fun formatDurationShort(minutes: Int): String
    fun formatMinutesSigned(minutes: Int): String
    fun formatDayNumber(day: Int): String
    fun formatMonthNumber(month: Int): String
    fun formatYear(year: Int): String
    fun formatPercent(percent: Int): String
    fun formatDaysLogged(completed: Int, total: Int): String
    fun formatDaysCount(count: Int): String
    fun formatNumber(number: Int): String
    fun formatTime(hour: Int, minute: Int): String
    fun formatDigits(text: String): String
}

object EnStrings : AppStrings {
    override val isRtl: Boolean = false
    override val appName: String = "TimTim"
    override val languageName: String = "English"

    override val navTimesheet: String = "Timesheet"
    override val navProfile: String = "Profile & Stats"
    override val navSettings: String = "Settings"
    override val navReport: String = "Monthly Report"
    override val navRemainingTime: String = "Time Left"
    override val back: String = "Back"
    override val cancel: String = "Cancel"
    override val confirm: String = "Confirm"
    override val save: String = "Save"
    override val reset: String = "Reset"
    override val delete: String = "Delete"
    override val edit: String = "Edit"
    override val done: String = "Done"
    override val close: String = "Close"
    override val warning: String = "Warning"
    override val success: String = "Success"
    override val error: String = "Error"
    override val none: String = "None"
    override val copy: String = "Copy"
    override val copied: String = "Copied"
    override val day: String = "Day"
    override val now: String = "Now"
    override val off: String = "Off"
    override val gotIt: String = "Got It"
    override val version: String = "Version 1.0.0 (Build 1)"

    override val monthlySummary: String = "Monthly Summary"
    override val totalHours: String = "Total Hours"
    override val workedHours: String = "Worked Hours"
    override val targetHours: String = "Target Hours"
    override val overtime: String = "Overtime"
    override val deficit: String = "Deficit"
    override val deficitTime: String = "Deficit Time"
    override val dailyTarget: String = "Daily Target"
    override val dayOff: String = "Day Off"
    override val noWorkRequired: String = "No work required today"
    override val workDay: String = "Work Day"
    override val entryTime: String = "Entry"
    override val enterTime: String = "Entry Time"
    override val exitTime: String = "Exit Time"
    override val duration: String = "Duration"
    override val status: String = "Status"
    override val today: String = "Today"
    override val backToToday: String = "Back to Today"
    override val dailyLog: String = "Daily Log"
    override val tapEnterExitHint: String = "Tap ENTER or EXIT to select time"
    override val enterTimeLabel: String = "Enter Time"
    override val exitTimeLabel: String = "Exit Time"
    override val estimatedCheckout: String = "Est. Checkout"
    override val estCheckOut: String = "Est. Check-Out"
    override val setOff: String = "Set Off"
    override val cancelDayOff: String = "Cancel Day Off"
    override val clear: String = "Clear"
    override val remainingTime: String = "Remaining Time"
    override val dailySummary: String = "Daily Summary"
    override val logEntryNow: String = "Enter"
    override val logExitNow: String = "Exit now"
    override val quickActionCheckedIn: String = "Checked in at"
    override val quickActionExitPrompt: String = "Tap to log check-out time"
    override val quickActionEntryPrompt: String = "You haven't logged check-in today"
    override val tapToLog: String = "Tap to set time"
    override val setAsDayOff: String = "Set as Day Off"
    override val setAsWorkDay: String = "Set as Work Day"
    override val clearTimes: String = "Clear Times"
    override val clearAllDaysPrompt: String = "This will clear all entered times across all days in this month."
    override val liveRemainingBadge: String = "Time Left"
    override val noRecordsYet: String = "No records logged yet"
    override val balance: String = "Balance"
    override val completedTarget: String = "Target Completed"
    override val inProgress: String = "In Progress"
    override val remainingToExit: String = "Remaining to Exit"
    override val daysCount: String = "Days"
    override val workedDaysCount: String = "Worked Days"
    override val offDaysCount: String = "Off Days"
    override val totalDaysSuffix: String = "DAYS"
    override val onTarget: String = "On Target"

    override val profile: String = "Profile"
    override val userProfile: String = "User Profile"
    override val username: String = "User Name"
    override val monthlyPerformance: String = "Monthly Performance"
    override val totalWorked: String = "Total Hours Worked"
    override val workingDaysCount: String = "Working Days"
    override val targetCompletion: String = "Target Completion"
    override val averageDaily: String = "Daily Average"
    override val quickAccess: String = "Quick Access"
    override val settings: String = "Settings"
    override val settingsSubtitle: String = "Theme, language, calendar & targets"
    override val openSettings: String = "App Settings"
    override val reportSubtitle: String = "Detailed charts, exports & balance"
    override val viewDetailedReport: String = "Detailed Monthly Report"
    override val privacy: String = "Privacy Policy"
    override val changeAvatar: String = "Change Avatar"
    override val editUserName: String = "Edit User Name"
    override val editProfileName: String = "Edit Profile Name"
    override val userNameLabel: String = "User Name"
    override val userNamePlaceholder: String = "Enter your name"
    override val statistics: String = "Statistics"
    override val performanceSummary: String = "Performance Summary"
    override val highestWorkedDay: String = "Highest Worked Day"
    override val lowestWorkedDay: String = "Lowest Worked Day"
    override val totalOvertimeLabel: String = "Total Overtime"
    override val totalDeficitLabel: String = "Total Deficit"
    override val activeCalendar: String = "Active Calendar"
    override val privacyTitle: String = "Privacy & Data Policy"
    override val privacySubtitle: String = "Your data stays on your device"
    override val privacyPolicyTitle: String = "Privacy & Offline Architecture"
    override val privacyPolicySubtitle: String = "Your work records and private data never leave your phone."
    override val privacyPoint1Title: String = "100% Offline Storage"
    override val privacyPoint1Desc: String = "All timesheet records, settings, and personal data are stored exclusively in your local SQLite Room database."
    override val privacyPoint2Title: String = "Zero Tracking & Telemetry"
    override val privacyPoint2Desc: String = "No analytics, no crash beacons, no cloud uploads. We never send your work data to external servers."
    override val privacyPoint3Title: String = "User Controlled Backups"
    override val privacyPoint3Desc: String = "Export and import your data anytime via plain JSON files with total transparency and ownership."
    override val profileSettingsDesc: String = "Daily target, theme & off days"
    override val profileReportDesc: String = "Distribution chart & attendance metrics"
    override val profilePrivacyDesc: String = "Offline Room storage & data safety"
    override val privacyLocalOnly: String = "100% Offline Storage"
    override val privacyLocalOnlyDesc: String = "All timesheet records, settings, and personal data are stored exclusively in your local SQLite Room database."
    override val privacyNoTracking: String = "Zero Tracking & Telemetry"
    override val privacyNoTrackingDesc: String = "No analytics, no crash beacons, no cloud uploads. We never send your work data to external servers."
    override val privacyBackupControl: String = "User Controlled Backups"
    override val privacyBackupControlDesc: String = "Export and import your data anytime via plain JSON files with total transparency and ownership."

    override val settingsTitle: String = "Settings"
    override val appearanceSection: String = "Appearance & Theme"
    override val themeMode: String = "Theme Mode"
    override val themeSetting: String = "Theme Mode"
    override val themeSystem: String = "System Default"
    override val themeLight: String = "Light Theme"
    override val themeDark: String = "Dark Theme"
    override val colorAccent: String = "Accent Color"
    override val languageSetting: String = "Language"
    override val languageSubtitle: String = "Switch between English and Persian (فارسی)"
    override val calendarSystem: String = "Calendar System"
    override val calendarTypeSetting: String = "Calendar System"
    override val calendarSubtitle: String = "Choose Gregorian or Solar Hijri (Shamsi)"
    override val gregorianCalendar: String = "Gregorian Calendar"
    override val gregorianCalendarTitle: String = "Gregorian Calendar"
    override val gregorianDesc: String = "Standard calendar (January – December)"
    override val shamsiCalendar: String = "Solar Hijri (Shamsi)"
    override val persianCalendarTitle: String = "Solar Hijri (Shamsi)"
    override val shamsiDesc: String = "Iranian calendar (Farvardin – Esfand)"
    override val workScheduleSection: String = "Work Schedule & Target"
    override val dailyRequiredHours: String = "Daily Required Hours"
    override val dailyRequiredSubtitle: String = "Target work duration per working day"
    override val dailyTargetCard: String = "Daily Required Hours"
    override val targetTimeSubtitle: String = "Target work duration per working day"
    override val offDaysSetting: String = "Weekly Off-Days"
    override val offDaysSubtitle: String = "Select days of the week configured as days off"
    override val workHourLimits: String = "Working Hours & Limits"
    override val workHourLimitsSubtitle: String = "Set early entry and late exit boundary caps"
    override val workLimits: String = "Work Limits"
    override val minEnterTime: String = "Earliest Entry Limit"
    override val maxExitTime: String = "Latest Exit Limit"
    override val minDailyHours: String = "Min Daily Hours"
    override val maxDailyHours: String = "Max Daily Hours"
    override val dataBackupSection: String = "Backup & Data Management"
    override val importExport: String = "Import & Export"
    override val importExportSubtitle: String = "Backup or restore all saved data"
    override val importExportDialogTitle: String = "Import & Export Data"
    override val importExportDialogSubtitle: String = "Backup your recorded attendance data or restore from a JSON backup file."
    override val exportAllDataTitle: String = "Export All Data"
    override val exportAllDataDesc: String = "Save all recorded days, shift times & settings"
    override val shareText: String = "Share"
    override val saveFileText: String = "Save File"
    override val importSavedDataTitle: String = "Import Saved Data"
    override val importSavedDataDesc: String = "Restore from an existing JSON backup file"
    override val selectJsonFileText: String = "Select JSON Backup File"
    override val exportBackup: String = "Export Backup (.json)"
    override val exportBackupSubtitle: String = "Save all your timesheet data to a local file"
    override val importBackup: String = "Import Backup (.json)"
    override val importBackupSubtitle: String = "Restore timesheet records from a backup file"
    override val resetMonthRecords: String = "Reset Current Month"
    override val resetAllData: String = "Reset All Recorded Data"
    override val resetAllDataSubtitle: String = "Permanently delete all months and reset app"
    override val resetAllConfirmTitle: String = "Reset All Recorded Data?"
    override val resetAllConfirmMessage: String = "This will permanently clear all logged timesheets, monthly targets, and reset all settings to defaults. This action cannot be undone."
    override val clearAllDataAction: String = "Yes, Clear All"
    override val selectLanguageTitle: String = "Select App Language"
    override val selectThemeTitle: String = "Select Theme"
    override val selectThemeSubtitle: String = "Choose your mode and primary color"
    override val themeModeLabel: String = "Mode"
    override val primaryColorLabel: String = "Primary Color"
    override val selectCalendarTypeTitle: String = "Select calendar type"
    override val selectCalendarTypeSubtitle: String = "Choose the calendar system used across the entire app"
    override fun getAccentColorName(id: String): String {
        return when (id.uppercase()) {
            "BLUE" -> "Royal Blue"
            "INDIGO" -> "Royal Indigo"
            "EMERALD" -> "Emerald Green"
            "AMBER" -> "Sunset Amber"
            "ROSE" -> "Rose Berry"
            "TEAL" -> "Teal Ocean"
            else -> id
        }
    }

    override val reportTitle: String = "Monthly Report"
    override val monthlyReport: String = "Monthly Report"
    override val shareExportReport: String = "Share & Export Report"
    override val exportReport: String = "Export Report"
    override val selectFormatFor: String = "Select Format for"
    override val pdfDocument: String = "PDF Document"
    override val pdfDocumentDesc: String = "Clean printable document of monthly timesheet"
    override val excelCsvSpreadsheet: String = "Excel / CSV Spreadsheet"
    override val excelCsvDesc: String = "Tabular data file compatible with Excel & Google Sheets"
    override val textSummary: String = "Text Summary"
    override val textSummaryDesc: String = "Share formatted text summary to messaging apps"
    override val exportPdf: String = "Export PDF"
    override val exportCsv: String = "Export CSV"
    override val shareSummary: String = "Share Summary"
    override val netBalance: String = "Net Balance"
    override val surplusHours: String = "Surplus Hours"
    override val deficitHours: String = "Deficit Hours"
    override val balancedState: String = "Balanced (On Target)"
    override val workOverview: String = "Work Overview"
    override val requiredTarget: String = "Required Target"
    override val totalOvertime: String = "Total Overtime"
    override val totalDeficit: String = "Total Deficit"
    override val monthlyWorkDistribution: String = "Monthly Work Distribution"
    override val completed: String = "Completed"
    override val underTarget: String = "Under Target"
    override val attendanceProgress: String = "Attendance Progress"
    override val worked: String = "Worked"
    override val remainingDays: String = "Remaining Days"
    override val dailyBreakdown: String = "Daily Breakdown"
    override val noDataForMonth: String = "No recorded data for this month"
    override val dateCol: String = "Date"
    override val dayCol: String = "Day"
    override val inCol: String = "Entry"
    override val outCol: String = "Exit"
    override val totalCol: String = "Total"
    override val diffCol: String = "Diff"
    override val workDistribution: String = "Work Distribution"
    override val attendanceMetrics: String = "Attendance Metrics"
    override val exportShare: String = "Export & Share"
    override val exportOptions: String = "Export Options"
    override val exportSummaryTextTitle: String = "Share Text Summary"
    override val exportSummaryTextDesc: String = "Share formatted text summary to messaging apps"

    override val selectMonthYear: String = "Select Month & Year"
    override val selectMonthYearSubtitle: String = "Quickly jump to any period"
    override val selectMonthYearDesc: String = "Quickly jump to any month or year in your records"
    override val jumpToCurrentMonth: String = "Jump to Current Month"
    override val previousMonth: String = "Previous Month"
    override val nextMonth: String = "Next Month"

    override val remainingTimeTitle: String = "Remaining Work Time"
    override val activeShift: String = "ACTIVE SHIFT"
    override val liveClock: String = "Live Clock"
    override val timeLeft: String = "Time Left"
    override val timeRemaining: String = "Time Remaining"
    override val workedSoFar: String = "Worked So Far"
    override val elapsedWorkTime: String = "Elapsed Work Time"
    override val elapsedWorkingTime: String = "Elapsed Working Time"
    override val targetExitTime: String = "Target Exit Time"
    override val overtimeRunning: String = "Overtime in Progress"
    override val overtimeAccumulated: String = "Overtime Accumulated"
    override val targetAchieved: String = "Daily Target Completed!"
    override val targetReached: String = "Target Reached"
    override val ofTarget: String = "of daily target"
    override val checkIn: String = "Check-In"
    override val exitNow: String = "Exit Now"
    override val exitNowButton: String = "Log Exit Now"
    override val pickExitTime: String = "Pick Exit Time"
    override val liveWorkingTimer: String = "Live Work Timer"
    override val dailyProgress: String = "Daily Progress"
    override val expectedExitAt: String = "Expected exit at"

    override val setEntryTimeTitle: String = "Set Company Entry Time"
    override val setExitTimeTitle: String = "Set Company Exit Time"
    override val hour: String = "Hour"
    override val minute: String = "Minute"
    override val setToNow: String = "Set to Current Time"
    override val clearTime: String = "Clear"
    override val timeValidationError: String = "Exit time cannot be earlier than entry time"
    override val exitTimeCannotBeEarlier: String = "Exit time cannot be earlier than entry time"
    override val enterTimeCannotBeLater: String = "Entry time cannot be later than exit time"
    override val dailyTargetDesc: String = "Set the required daily hours for this day"

    override val welcomeTitle: String = "Welcome to TimTim"
    override val welcomeSubtitle: String = "Smart, effortless work timesheet, overtime & deficit tracking"
    override val selectLanguageStep: String = "Choose Language"
    override val chooseCalendarStep: String = "Calendar System"
    override val setDailyTargetStep: String = "Daily Required Hours"
    override val selectOffDaysStep: String = "Weekly Off-Days"
    override val getStarted: String = "Get Started"
    override val skip: String = "Skip"
    override val restoreBackupPrompt: String = "If you have a previously saved backup file (.json), you can restore it now."
    override val restoreFromFile: String = "Select Backup File (.json)"
    override val continueSetup: String = "Continue Setup"

    override val saturday: String = "Saturday"
    override val sunday: String = "Sunday"
    override val monday: String = "Monday"
    override val tuesday: String = "Tuesday"
    override val wednesday: String = "Wednesday"
    override val thursday: String = "Thursday"
    override val friday: String = "Friday"

    override val saturdayShort: String = "Sat"
    override val sundayShort: String = "Sun"
    override val mondayShort: String = "Mon"
    override val tuesdayShort: String = "Tue"
    override val wednesdayShort: String = "Wed"
    override val thursdayShort: String = "Thu"
    override val fridayShort: String = "Fri"
    override val hourShort: String = "h"
    override val minuteShort: String = "m"

    override fun getDayOfWeek(dayOfWeek: java.time.DayOfWeek): String = when (dayOfWeek) {
        java.time.DayOfWeek.SATURDAY -> saturday
        java.time.DayOfWeek.SUNDAY -> sunday
        java.time.DayOfWeek.MONDAY -> monday
        java.time.DayOfWeek.TUESDAY -> tuesday
        java.time.DayOfWeek.WEDNESDAY -> wednesday
        java.time.DayOfWeek.THURSDAY -> thursday
        java.time.DayOfWeek.FRIDAY -> friday
    }

    override fun getShortDayOfWeek(dayOfWeek: java.time.DayOfWeek): String = when (dayOfWeek) {
        java.time.DayOfWeek.SATURDAY -> saturdayShort
        java.time.DayOfWeek.SUNDAY -> sundayShort
        java.time.DayOfWeek.MONDAY -> mondayShort
        java.time.DayOfWeek.TUESDAY -> tuesdayShort
        java.time.DayOfWeek.WEDNESDAY -> wednesdayShort
        java.time.DayOfWeek.THURSDAY -> thursdayShort
        java.time.DayOfWeek.FRIDAY -> fridayShort
    }

    override fun formatHourMinute(hours: Int, minutes: Int): String {
        return "${hours}h ${minutes.toString().padStart(2, '0')}m"
    }

    override fun formatDurationShort(minutes: Int): String {
        val h = minutes / 60
        val m = minutes % 60
        return if (m == 0) "${h}h" else "${h}h ${m}m"
    }

    override fun formatMinutesSigned(minutes: Int): String {
        val sign = if (minutes >= 0) "+" else "-"
        val absMin = kotlin.math.abs(minutes)
        val h = absMin / 60
        val m = absMin % 60
        return "$sign${h}h ${m.toString().padStart(2, '0')}m"
    }

    override fun formatDayNumber(day: Int): String = day.toString().padStart(2, '0')
    override fun formatMonthNumber(month: Int): String = month.toString().padStart(2, '0')
    override fun formatYear(year: Int): String = year.toString()
    override fun formatPercent(percent: Int): String = "$percent%"
    override fun formatDaysLogged(completed: Int, total: Int): String = "$completed/$total logged"
    override fun formatDaysCount(count: Int): String = "$count days"
    override fun formatNumber(number: Int): String = number.toString()
    override fun formatTime(hour: Int, minute: Int): String = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    override fun formatDigits(text: String): String = text
}

object FaStrings : AppStrings {
    override val isRtl: Boolean = true
    override val appName: String = "تیم‌تیم"
    override val languageName: String = "فارسی"

    override val navTimesheet: String = "ساعت کارکرد"
    override val navProfile: String = "پروفایل و آمار"
    override val navSettings: String = "تنظیمات"
    override val navReport: String = "گزارش ماهانه"
    override val navRemainingTime: String = "زمان باقیمانده"
    override val back: String = "بازگشت"
    override val cancel: String = "انصراف"
    override val confirm: String = "تأیید"
    override val save: String = "ذخیره"
    override val reset: String = "بازنشانی"
    override val delete: String = "حذف"
    override val edit: String = "ویرایش"
    override val done: String = "انجام شد"
    override val close: String = "بستن"
    override val warning: String = "هشدار"
    override val success: String = "موفق"
    override val error: String = "خطا"
    override val none: String = "ندارد"
    override val copy: String = "کپی"
    override val copied: String = "کپی شد"
    override val day: String = "روز"
    override val now: String = "الان"
    override val off: String = "تعطیل"
    override val gotIt: String = "متوجه شدم"
    override val version: String = "نسخه ۱.۰.۰ (ساخت ۱)"

    override val monthlySummary: String = "خلاصه وضعیت ماه"
    override val totalHours: String = "مجموع ساعت کارکرد"
    override val workedHours: String = "ساعت کارکرد"
    override val targetHours: String = "تعهد کاری"
    override val overtime: String = "اضافه‌کار"
    override val deficit: String = "کسرکار"
    override val deficitTime: String = "کسری کارکرد"
    override val dailyTarget: String = "موظفی روزانه"
    override val dayOff: String = "تعطیل"
    override val noWorkRequired: String = "امروز روز کاری نیست"
    override val workDay: String = "روز کاری"
    override val entryTime: String = "ورود"
    override val enterTime: String = "ساعت ورود"
    override val exitTime: String = "ساعت خروج"
    override val duration: String = "مدت"
    override val status: String = "وضعیت"
    override val today: String = "امروز"
    override val backToToday: String = "بازگشت به امروز"
    override val dailyLog: String = "ثبت روزانه"
    override val tapEnterExitHint: String = "برای ثبت ساعت روی ورود یا خروج بزنید"
    override val enterTimeLabel: String = "ساعت ورود"
    override val exitTimeLabel: String = "ساعت خروج"
    override val estimatedCheckout: String = "خروج تخمینی"
    override val estCheckOut: String = "خروج تخمینی"
    override val setOff: String = "تنظیم به عنوان تعطیل"
    override val cancelDayOff: String = "لغو حالت تعطیل"
    override val clear: String = "پاک کردن"
    override val remainingTime: String = "زمان باقیمانده"
    override val dailySummary: String = "خلاصه روز"
    override val logEntryNow: String = "ثبت ورود الان"
    override val logExitNow: String = "ثبت خروج الان"
    override val quickActionCheckedIn: String = "ورود ثبت شده در ساعت"
    override val quickActionExitPrompt: String = "برای ثبت خروج ضربه بزنید"
    override val quickActionEntryPrompt: String = "هنوز ساعت ورود امروز را ثبت نکرده‌اید"
    override val tapToLog: String = "ثبت ساعت"
    override val setAsDayOff: String = "تنظیم به عنوان روز تعطیل"
    override val setAsWorkDay: String = "تنظیم به عنوان روز کاری"
    override val clearTimes: String = "پاک کردن ساعات"
    override val clearAllDaysPrompt: String = "تمام ساعات ثبت شده در این ماه پاک خواهند شد."
    override val liveRemainingBadge: String = "زمان باقیمانده"
    override val noRecordsYet: String = "هنوز ساعتی ثبت نشده است"
    override val balance: String = "تراز کارکرد"
    override val completedTarget: String = "تکمیل موظفی"
    override val inProgress: String = "در حال کار"
    override val remainingToExit: String = "مانده تا خروج"
    override val daysCount: String = "روز"
    override val workedDaysCount: String = "روزهای کاری"
    override val offDaysCount: String = "روزهای تعطیل"
    override val totalDaysSuffix: String = "روز"
    override val onTarget: String = "تراز دقیق"

    override val profile: String = "پروفایل"
    override val userProfile: String = "حساب کاربری"
    override val username: String = "نام کاربر"
    override val monthlyPerformance: String = "عملکرد ماهانه"
    override val totalWorked: String = "مجموع ساعات کارکرد"
    override val workingDaysCount: String = "روزهای کاری سپری‌شده"
    override val targetCompletion: String = "درصد تحقق هدف"
    override val averageDaily: String = "میانگین کارکرد روزانه"
    override val quickAccess: String = "دسترسی سریع"
    override val settings: String = "تنظیمات"
    override val settingsSubtitle: String = "تم، زبان، تقویم و ساعات موظفی"
    override val openSettings: String = "تنظیمات برنامه"
    override val reportSubtitle: String = "نمودار کارکرد، خروجی و تراز ماه"
    override val viewDetailedReport: String = "گزارش کامل ماهانه"
    override val privacy: String = "حریم خصوصی"
    override val changeAvatar: String = "تغییر آواتار"
    override val editUserName: String = "ویرایش نام کاربری"
    override val editProfileName: String = "ویرایش نام کاربر"
    override val userNameLabel: String = "نام کاربر"
    override val userNamePlaceholder: String = "نام خود را وارد کنید"
    override val statistics: String = "آمار و ارقام"
    override val performanceSummary: String = "خلاصه عملکرد"
    override val highestWorkedDay: String = "بیشترین ساعت کارکرد"
    override val lowestWorkedDay: String = "کمترین ساعت کارکرد"
    override val totalOvertimeLabel: String = "مجموع اضافه‌کاری"
    override val totalDeficitLabel: String = "مجموع کسرکار"
    override val activeCalendar: String = "تقویم فعال"
    override val privacyTitle: String = "حریم خصوصی و ذخیره‌سازی داده"
    override val privacySubtitle: String = "داده‌های شما فقط روی گوشی شماست"
    override val privacyPolicyTitle: String = "حریم خصوصی و معماری کاملاً آفلاین"
    override val privacyPolicySubtitle: String = "ساعات کاری و اطلاعات شخصی شما هرگز از گوشی شما خارج نمی‌شوند."
    override val privacyPoint1Title: String = "ذخیره‌سازی کاملاً آفلاین"
    override val privacyPoint1Desc: String = "تمام ساعات ثبت‌شده، تنظیمات و اطلاعات کاربری منحصراً در پایگاه‌داده محلی دستگاه شما نگهداری می‌شوند."
    override val privacyPoint2Title: String = "بدون هیچ‌گونه رهگیری یا سرور ابری"
    override val privacyPoint2Desc: String = "هیچ ابزار تحلیل آماری یا جمع‌آوری داده وجود ندارد و هیچ اطلاعاتی به سرورهای خارجی ارسال نمی‌شود."
    override val privacyPoint3Title: String = "کنترل کامل بر فایل پشتیبان"
    override val privacyPoint3Desc: String = "هر زمان که مایل باشید می‌توانید از داده‌های خود خروجی JSON بگیرید یا آن را در گوشی دیگر بازیابی کنید."
    override val profileSettingsDesc: String = "موظفی روزانه، تم، تقویم و تعطیلات"
    override val profileReportDesc: String = "نمودار کارکرد و شاخص‌های حضور"
    override val profilePrivacyDesc: String = "ذخیره‌سازی آفلاین و امنیت کامل داده‌ها"
    override val privacyLocalOnly: String = "ذخیره‌سازی کاملاً آفلاین"
    override val privacyLocalOnlyDesc: String = "تمام ساعات ثبت‌شده، تنظیمات و اطلاعات کاربری منحصراً در پایگاه‌داده محلی دستگاه شما نگهداری می‌شوند."
    override val privacyNoTracking: String = "بدون هیچ‌گونه رهگیری یا سرور ابری"
    override val privacyNoTrackingDesc: String = "هیچ ابزار تحلیل آماری یا جمع‌آوری داده وجود ندارد و هیچ اطلاعاتی به سرورهای خارجی ارسال نمی‌شود."
    override val privacyBackupControl: String = "کنترل کامل بر فایل پشتیبان"
    override val privacyBackupControlDesc: String = "هر زمان که مایل باشید می‌توانید از داده‌های خود خروجی JSON بگیرید یا آن را در گوشی دیگر بازیابی کنید."

    override val settingsTitle: String = "تنظیمات"
    override val appearanceSection: String = "ظاهر و پوسته"
    override val themeMode: String = "حالت تم"
    override val themeSetting: String = "حالت تم"
    override val themeSystem: String = "پیرو سیستم"
    override val themeLight: String = "روشن"
    override val themeDark: String = "تاریک"
    override val colorAccent: String = "رنگ شاخص"
    override val languageSetting: String = "زبان برنامه"
    override val languageSubtitle: String = "تغییر زبان بین فارسی و انگلیسی"
    override val calendarSystem: String = "تقویم کاری"
    override val calendarTypeSetting: String = "نوع تقویم"
    override val calendarSubtitle: String = "انتخاب بین تقویم هجری شمسی و میلادی"
    override val gregorianCalendar: String = "تقویم میلادی"
    override val gregorianCalendarTitle: String = "تقویم میلادی"
    override val gregorianDesc: String = "تقویم استاندارد جهانی (ژانویه – دسامبر)"
    override val shamsiCalendar: String = "تقویم هجری شمسی"
    override val persianCalendarTitle: String = "تقویم هجری شمسی"
    override val shamsiDesc: String = "تقویم خورشیدی ایران (فروردین – اسفند)"
    override val workScheduleSection: String = "برنامه و ساعت کاری"
    override val dailyRequiredHours: String = "ساعت موظفی روزانه"
    override val dailyRequiredSubtitle: String = "میزان ساعت کار مورد نیاز در هر روز کاری"
    override val dailyTargetCard: String = "ساعت موظفی روزانه"
    override val targetTimeSubtitle: String = "میزان ساعت کار مورد نیاز در هر روز کاری"
    override val offDaysSetting: String = "روزهای تعطیل هفته"
    override val offDaysSubtitle: String = "تعیین روزهایی از هفته که تعطیل رسمی یا شرکت هستند"
    override val workHourLimits: String = "محدودیت‌های ساعت ورود و خروج"
    override val workHourLimitsSubtitle: String = "تعیین سقف زودترین ورود و دیرترین خروج مجاز"
    override val workLimits: String = "محدودیت‌های کاری"
    override val minEnterTime: String = "حداقل ساعت ورود"
    override val maxExitTime: String = "حداکثر ساعت خروج"
    override val minDailyHours: String = "حداقل ساعت کاری روزانه"
    override val maxDailyHours: String = "حداکثر ساعت کاری روزانه"
    override val dataBackupSection: String = "پشتیبان‌گیری و داده‌ها"
    override val importExport: String = "پشتیبان‌گیری و بازیابی"
    override val importExportSubtitle: String = "خروجی گرفتن یا بازیابی تمام اطلاعات ذخیره‌شده"
    override val importExportDialogTitle: String = "پشتیبان‌گیری و بازیابی اطلاعات"
    override val importExportDialogSubtitle: String = "تهیه نسخه پشتیبان از اطلاعات ثبت‌شده یا بازیابی از فایل JSON."
    override val exportAllDataTitle: String = "خروجی گرفتن از تمام اطلاعات"
    override val exportAllDataDesc: String = "ذخیره تمام روزهای ثبت‌شده، ساعات شیفت و تنظیمات"
    override val shareText: String = "اشتراک‌گذاری"
    override val saveFileText: String = "ذخیره فایل"
    override val importSavedDataTitle: String = "بازیابی اطلاعات ذخیره‌شده"
    override val importSavedDataDesc: String = "بارگذاری مجدد اطلاعات از فایل پشتیبان JSON"
    override val selectJsonFileText: String = "انتخاب فایل پشتیبان JSON"
    override val exportBackup: String = "خروجی فایل پشتیبان (.json)"
    override val exportBackupSubtitle: String = "ذخیره تمام ساعات و تنظیمات در فایل محلی"
    override val importBackup: String = "بازیابی از فایل پشتیبان (.json)"
    override val importBackupSubtitle: String = "بارگذاری مجدد اطلاعات از فایل پشتیبان"
    override val resetMonthRecords: String = "پاک کردن اطلاعات این ماه"
    override val resetAllData: String = "حذف کامل تمام اطلاعات"
    override val resetAllDataSubtitle: String = "حذف دائمی تمام ماه‌ها و بازنشانی برنامه"
    override val resetAllConfirmTitle: String = "حذف کامل تمام اطلاعات؟"
    override val resetAllConfirmMessage: String = "تمام ساعات ورود و خروج، اهداف ماهانه و تنظیمات به طور کامل حذف خواهند شد. این عملیات غیرقابل بازگشت است."
    override val clearAllDataAction: String = "بله، پاک کردن همه"
    override val selectLanguageTitle: String = "انتخاب زبان برنامه"
    override val selectThemeTitle: String = "انتخاب تم"
    override val selectThemeSubtitle: String = "انتخاب حالت نمایش و رنگ اصلی"
    override val themeModeLabel: String = "حالت"
    override val primaryColorLabel: String = "رنگ اصلی"
    override val selectCalendarTypeTitle: String = "انتخاب نوع تقویم"
    override val selectCalendarTypeSubtitle: String = "انتخاب سیستم تقویم مورد استفاده در سراسر برنامه"
    override fun getAccentColorName(id: String): String {
        return when (id.uppercase()) {
            "BLUE" -> "آبی شاهانه"
            "INDIGO" -> "نیلی شاهانه"
            "EMERALD" -> "سبز زمردی"
            "AMBER" -> "کهربایی غروب"
            "ROSE" -> "سرخ رز"
            "TEAL" -> "سبزآبی اقیانوس"
            else -> id
        }
    }

    override val reportTitle: String = "گزارش ماهانه"
    override val monthlyReport: String = "گزارش ماهانه"
    override val shareExportReport: String = "اشتراک‌گذاری و خروجی"
    override val exportReport: String = "خروجی گزارش"
    override val selectFormatFor: String = "انتخاب فرمت برای"
    override val pdfDocument: String = "سند چاپی PDF"
    override val pdfDocumentDesc: String = "سند چاپی استاندارد و مرتب از ساعات کارکرد ماه"
    override val excelCsvSpreadsheet: String = "فایل اکسل / CSV"
    override val excelCsvDesc: String = "فایل جدولی سازگار با اکسل و گوگل شیتس"
    override val textSummary: String = "متن خلاصه"
    override val textSummaryDesc: String = "ارسال خلاصه گزارش ماه در پیام‌رسان‌ها"
    override val exportPdf: String = "خروجی PDF"
    override val exportCsv: String = "خروجی Excel / CSV"
    override val shareSummary: String = "اشتراک‌گذاری خلاصه"
    override val netBalance: String = "تراز نهایی ماه"
    override val surplusHours: String = "مازاد کارکرد (اضافه‌کار)"
    override val deficitHours: String = "کسری کارکرد (کسرکار)"
    override val balancedState: String = "تراز دقیق (مطابق موظفی)"
    override val workOverview: String = "نمای کلی کارکرد"
    override val requiredTarget: String = "ساعت موظفی"
    override val totalOvertime: String = "مجموع اضافه‌کار"
    override val totalDeficit: String = "مجموع کسرکار"
    override val monthlyWorkDistribution: String = "توزیع کارکرد ماهانه"
    override val completed: String = "تکمیل شده"
    override val underTarget: String = "کمتر از موظفی"
    override val attendanceProgress: String = "پیشرفت حضور"
    override val worked: String = "کارکرد"
    override val remainingDays: String = "روزهای باقیمانده"
    override val dailyBreakdown: String = "ریز کارکرد روزانه"
    override val noDataForMonth: String = "برای این ماه اطلاعاتی ثبت نشده است"
    override val dateCol: String = "تاریخ"
    override val dayCol: String = "روز"
    override val inCol: String = "ورود"
    override val outCol: String = "خروج"
    override val totalCol: String = "کارکرد"
    override val diffCol: String = "تراز"
    override val workDistribution: String = "توزیع ساعات کارکرد"
    override val attendanceMetrics: String = "شاخص‌های حضور و غیاب"
    override val exportShare: String = "خروجی و اشتراک‌گذاری"
    override val exportOptions: String = "گزینه‌های خروجی"
    override val exportSummaryTextTitle: String = "اشتراک متن خلاصه"
    override val exportSummaryTextDesc: String = "ارسال خلاصه گزارش ماه در پیام‌رسان‌ها"

    override val selectMonthYear: String = "انتخاب ماه و سال"
    override val selectMonthYearSubtitle: String = "پرش سریع به هر دوره زمانی"
    override val selectMonthYearDesc: String = "پرش سریع به هر ماه یا سال در سوابق کاری شما"
    override val jumpToCurrentMonth: String = "پرش به ماه جاری"
    override val previousMonth: String = "ماه قبل"
    override val nextMonth: String = "ماه بعد"

    override val remainingTimeTitle: String = "زمان باقیمانده تا پایان کار"
    override val activeShift: String = "شیفت کاری فعال"
    override val liveClock: String = "ساعت زنده"
    override val timeLeft: String = "زمان باقیمانده"
    override val timeRemaining: String = "زمان باقیمانده"
    override val workedSoFar: String = "کارکرد تا این لحظه"
    override val elapsedWorkTime: String = "کارکرد سپری‌شده"
    override val elapsedWorkingTime: String = "مدت زمان کارکرد سپری‌شده"
    override val targetExitTime: String = "ساعت خروج هدف"
    override val overtimeRunning: String = "در حال اضافه‌کاری"
    override val overtimeAccumulated: String = "اضافه‌کار ذخیره‌شده"
    override val targetAchieved: String = "تعهد کاری امروز تکمیل شد!"
    override val targetReached: String = "تحقق تعهد کاری"
    override val ofTarget: String = "از تعهد کاری روزانه"
    override val checkIn: String = "ساعت ورود"
    override val exitNow: String = "ثبت خروج الان"
    override val exitNowButton: String = "ثبت خروج الان"
    override val pickExitTime: String = "انتخاب ساعت خروج"
    override val liveWorkingTimer: String = "تایمر زنده کارکرد"
    override val dailyProgress: String = "پیشرفت روزانه"
    override val expectedExitAt: String = "زمان تقریبی خروج:"

    override val setEntryTimeTitle: String = "ثبت ساعت ورود"
    override val setExitTimeTitle: String = "ثبت ساعت خروج"
    override val hour: String = "ساعت"
    override val minute: String = "دقیقه"
    override val setToNow: String = "تنظیم به ساعت فعلی"
    override val clearTime: String = "پاک کردن"
    override val timeValidationError: String = "ساعت خروج نمی‌تواند قبل از ساعت ورود باشد"
    override val exitTimeCannotBeEarlier: String = "ساعت خروج نمی‌تواند قبل از ساعت ورود باشد"
    override val enterTimeCannotBeLater: String = "ساعت ورود نمی‌تواند بعد از ساعت خروج باشد"
    override val dailyTargetDesc: String = "تعیین ساعت موظفی برای این روز مشخص"

    override val welcomeTitle: String = "به تیم‌تیم خوش آمدید"
    override val welcomeSubtitle: String = "مدیریت هوشمند ساعت کارکرد، اضافه‌کار و کسرکار"
    override val selectLanguageStep: String = "انتخاب زبان"
    override val chooseCalendarStep: String = "نوع تقویم کاری"
    override val setDailyTargetStep: String = "تعیین ساعت موظفی روزانه"
    override val selectOffDaysStep: String = "تعیین روزهای تعطیل هفته"
    override val getStarted: String = "شروع کار با برنامه"
    override val skip: String = "رد کردن"
    override val restoreBackupPrompt: String = "اگر فایل پشتیبان (.json) قبلی دارید، می‌توانید همین حالا آن را بازیابی کنید."
    override val restoreFromFile: String = "انتخاب فایل پشتیبان (.json)"
    override val continueSetup: String = "ادامه تنظیمات"

    override val saturday: String = "شنبه"
    override val sunday: String = "یکشنبه"
    override val monday: String = "دوشنبه"
    override val tuesday: String = "سه‌شنبه"
    override val wednesday: String = "چهارشنبه"
    override val thursday: String = "پنج‌شنبه"
    override val friday: String = "جمعه"

    override val saturdayShort: String = "ش"
    override val sundayShort: String = "۱ش"
    override val mondayShort: String = "۲ش"
    override val tuesdayShort: String = "۳ش"
    override val wednesdayShort: String = "۴ش"
    override val thursdayShort: String = "۵ش"
    override val fridayShort: String = "ج"
    override val hourShort: String = "ساعت"
    override val minuteShort: String = "دقیقه"

    override fun getDayOfWeek(dayOfWeek: java.time.DayOfWeek): String = when (dayOfWeek) {
        java.time.DayOfWeek.SATURDAY -> saturday
        java.time.DayOfWeek.SUNDAY -> sunday
        java.time.DayOfWeek.MONDAY -> monday
        java.time.DayOfWeek.TUESDAY -> tuesday
        java.time.DayOfWeek.WEDNESDAY -> wednesday
        java.time.DayOfWeek.THURSDAY -> thursday
        java.time.DayOfWeek.FRIDAY -> friday
    }

    override fun getShortDayOfWeek(dayOfWeek: java.time.DayOfWeek): String = when (dayOfWeek) {
        java.time.DayOfWeek.SATURDAY -> saturdayShort
        java.time.DayOfWeek.SUNDAY -> sundayShort
        java.time.DayOfWeek.MONDAY -> mondayShort
        java.time.DayOfWeek.TUESDAY -> tuesdayShort
        java.time.DayOfWeek.WEDNESDAY -> wednesdayShort
        java.time.DayOfWeek.THURSDAY -> thursdayShort
        java.time.DayOfWeek.FRIDAY -> fridayShort
    }

    override fun formatHourMinute(hours: Int, minutes: Int): String {
        return "${hours.toPersianDigits()} ساعت و ${minutes.toString().padStart(2, '0').toPersianDigits()} دقیقه"
    }

    override fun formatDurationShort(minutes: Int): String {
        val h = minutes / 60
        val m = minutes % 60
        return if (m == 0) "${h.toPersianDigits()} ساعت" else "${h.toPersianDigits()} ساعت و ${m.toPersianDigits()} دقیقه"
    }

    override fun formatMinutesSigned(minutes: Int): String {
        val sign = if (minutes >= 0) "+" else "-"
        val absMin = kotlin.math.abs(minutes)
        val h = absMin / 60
        val m = absMin % 60
        return "$sign${h.toPersianDigits()} ساعت و ${m.toString().padStart(2, '0').toPersianDigits()} دقیقه"
    }

    override fun formatDayNumber(day: Int): String = day.toString().padStart(2, '0').toPersianDigits()
    override fun formatMonthNumber(month: Int): String = month.toString().padStart(2, '0').toPersianDigits()
    override fun formatYear(year: Int): String = year.toString().toPersianDigits()
    override fun formatPercent(percent: Int): String = "%${percent.toPersianDigits()}"
    override fun formatDaysLogged(completed: Int, total: Int): String = "${completed.toPersianDigits()} از ${total.toPersianDigits()} ثبت‌شده"
    override fun formatDaysCount(count: Int): String = "${count.toPersianDigits()} روز"
    override fun formatNumber(number: Int): String = number.toString().toPersianDigits()
    override fun formatTime(hour: Int, minute: Int): String = "${hour.toString().padStart(2, '0').toPersianDigits()}:${minute.toString().padStart(2, '0').toPersianDigits()}"
    override fun formatDigits(text: String): String = text.toPersianDigits()
}

/**
 * Converts English ASCII digits (0-9) to Persian digits (۰-۹).
 */
fun String.toPersianDigits(): String {
    val builder = StringBuilder(this.length)
    for (char in this) {
        when (char) {
            '0' -> builder.append('۰')
            '1' -> builder.append('۱')
            '2' -> builder.append('۲')
            '3' -> builder.append('۳')
            '4' -> builder.append('۴')
            '5' -> builder.append('۵')
            '6' -> builder.append('۶')
            '7' -> builder.append('۷')
            '8' -> builder.append('۸')
            '9' -> builder.append('۹')
            else -> builder.append(char)
        }
    }
    return builder.toString()
}

fun Int.toPersianDigits(): String = this.toString().toPersianDigits()
fun Long.toPersianDigits(): String = this.toString().toPersianDigits()
fun Double.toPersianDigits(): String = this.toString().toPersianDigits()

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.EN }
val LocalAppStrings = staticCompositionLocalOf<AppStrings> { EnStrings }
