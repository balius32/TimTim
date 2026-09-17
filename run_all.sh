#!/bin/bash
cat << 'PYEOF' > patch_localization.py
import re

file_path = "app/src/main/java/com/example/ui/localization/AppLocalization.kt"
with open(file_path, "r") as f:
    content = f.read()

interface_adds = """    // Additional missing strings
    val invalidBackupFile: String
    val restoreBackupAndSkip: String
    val backupContains: String
    val recordsRecords: String
    val monthlyTargetSettings: String
    val savedProfileConfig: String
    val restoreWillComplete: String
    val backupRestoredSuccessfully: String
    val importFailed: String
    val restoring: String
    val yesRestoreAndSkip: String
    val readingBackupFile: String
    val pleaseWaitAttendance: String
    val stepProfileAvatar: String
    val stepCalendarSystem: String
    val stepTargetLimits: String
    val stepOffDays: String
    val stepThemeAppearance: String
    val displayNameTitle: String
    val displayNameSubtitle: String
    val enterNamePlaceholder: String
    val avatarStyleTitle: String
    val avatarStyleSubtitle: String
    val chooseCalendarSystem: String
    val dailyTargetTitle: String
    val setStandardRequiredHours: String
    val targetTimeLabel: String
    val tapMinEnterMaxExit: String
    val selectThemePrimaryColor: String
    val appearanceMode: String
    val primaryColorSelect: String
    val welcomeToTimTim: String
    val loggedCheckInSuccessfully: String
    val loggedCheckOutSuccessfully: String
    val monthRecordsReset: String
    val allAppDataCleared: String
    val failedToImportBackup: String
    val chooseProfilePicture: String
    val pickCustomIllustration: String
    val uploadFromPhone: String
    val selectPictureGalleryCamera: String
    val galleryLabel: String
    val defaultAvatarCollection: String
    val skipToDefaults: String
    val continueManualSetup: String
    val haveABackup: String
    val ifYouHaveBackup: String
    val startUsingTimTim: String
    val step: String
    val of: String
    val yesRestore: String
    val widgetTimeLeftToExit: String
    val widgetNotClockedIn: String
    val widgetReadyToStart: String
    val widgetClockIn: String
    val widgetScheduledDayOff: String
    val widgetEnjoyRest: String
    val widgetShiftCompleted: String
    val widgetTargetWas: String
    val widgetTargetReachedOvertime: String
    val widgetClockOut: String
    val widgetDailyGoal: String
    val widgetIn: String
    val widgetOut: String
    val widgetCheckIn: String
"""

en_adds = """    // Additional missing strings
    override val invalidBackupFile: String = "Invalid backup file"
    override val restoreBackupAndSkip: String = "Restore Backup & Skip Setup?"
    override val backupContains: String = "This backup file contains:"
    override val recordsRecords: String = "daily attendance records"
    override val monthlyTargetSettings: String = "monthly target settings"
    override val savedProfileConfig: String = "Saved profile & configuration"
    override val restoreWillComplete: String = "Restoring this data will complete onboarding and take you directly to your timesheet."
    override val backupRestoredSuccessfully: String = "Backup restored successfully"
    override val importFailed: String = "Import failed"
    override val restoring: String = "Restoring..."
    override val yesRestoreAndSkip: String = "Yes, Restore & Skip"
    override val readingBackupFile: String = "Reading Backup File..."
    override val pleaseWaitAttendance: String = "Please wait while your attendance and settings are being read."
    override val stepProfileAvatar: String = "Profile & Avatar"
    override val stepCalendarSystem: String = "Calendar System"
    override val stepTargetLimits: String = "Target & Limits"
    override val stepOffDays: String = "Off Days"
    override val stepThemeAppearance: String = "Theme & Appearance"
    override val displayNameTitle: String = "Display Name"
    override val displayNameSubtitle: String = "Enter your preferred name or nickname for the profile card."
    override val enterNamePlaceholder: String = "Enter your name or nickname"
    override val avatarStyleTitle: String = "Avatar Style"
    override val avatarStyleSubtitle: String = "Pick an avatar that represents your workspace identity."
    override val chooseCalendarSystem: String = "Choose the calendar system used across the entire app"
    override val dailyTargetTitle: String = "Daily Target"
    override val setStandardRequiredHours: String = "Set your standard required daily work hours"
    override val targetTimeLabel: String = "target time"
    override val tapMinEnterMaxExit: String = "Tap Min Enter or Max Exit to set limits"
    override val selectThemePrimaryColor: String = "Choose your mode and primary color"
    override val appearanceMode: String = "Appearance Mode"
    override val primaryColorSelect: String = "Primary Color"
    override val welcomeToTimTim: String = "Welcome to TimTim!"
    override val loggedCheckInSuccessfully: String = "Logged check-in time successfully"
    override val loggedCheckOutSuccessfully: String = "Logged check-out time successfully"
    override val monthRecordsReset: String = "Month records reset"
    override val allAppDataCleared: String = "All application data cleared"
    override val failedToImportBackup: String = "Failed to import backup data"
    override val chooseProfilePicture: String = "Choose Profile Picture"
    override val pickCustomIllustration: String = "Pick a custom illustration or upload your photo"
    override val uploadFromPhone: String = "Upload from Your Phone"
    override val selectPictureGalleryCamera: String = "Select any picture from your gallery or camera"
    override val galleryLabel: String = "Gallery"
    override val defaultAvatarCollection: String = "Default Avatar Collection"
    override val skipToDefaults: String = "Skip to Defaults"
    override val continueManualSetup: String = "Continue Manual Setup"
    override val haveABackup: String = "Have a Backup?"
    override val ifYouHaveBackup: String = "If you have a previously saved backup file (.json), you can restore it now and skip this setup process."
    override val startUsingTimTim: String = "Start Using TimTim"
    override val step: String = "Step"
    override val of: String = "of"
    override val yesRestore: String = "Yes, Restore"
    override val widgetTimeLeftToExit: String = "Time left to exit"
    override val widgetNotClockedIn: String = "Not clocked in yet today"
    override val widgetReadyToStart: String = "Ready to start work"
    override val widgetClockIn: String = "Clock In"
    override val widgetScheduledDayOff: String = "Scheduled day off"
    override val widgetEnjoyRest: String = "Enjoy your rest!"
    override val widgetShiftCompleted: String = "Shift completed"
    override val widgetTargetWas: String = "Target was"
    override val widgetTargetReachedOvertime: String = "Daily target reached! (Overtime)"
    override val widgetClockOut: String = "Clock Out"
    override val widgetDailyGoal: String = "Daily Goal:"
    override val widgetIn: String = "In:"
    override val widgetOut: String = "Out:"
    override val widgetCheckIn: String = "Check-in:"
"""

fa_adds = """    // Additional missing strings
    override val invalidBackupFile: String = "فایل پشتیبان نامعتبر است"
    override val restoreBackupAndSkip: String = "بازیابی و پرش از تنظیمات؟"
    override val backupContains: String = "این فایل پشتیبان شامل:"
    override val recordsRecords: String = "سوابق روزانه حضور"
    override val monthlyTargetSettings: String = "تنظیمات موظفی ماهانه"
    override val savedProfileConfig: String = "پروفایل و تنظیمات ذخیره شده"
    override val restoreWillComplete: String = "بازیابی این داده‌ها تنظیمات را کامل کرده و شما را به صفحه کارکرد می‌برد."
    override val backupRestoredSuccessfully: String = "فایل پشتیبان با موفقیت بازیابی شد"
    override val importFailed: String = "بازیابی ناموفق بود"
    override val restoring: String = "در حال بازیابی..."
    override val yesRestoreAndSkip: String = "بله، بازیابی کن و رد شو"
    override val readingBackupFile: String = "در حال خواندن فایل..."
    override val pleaseWaitAttendance: String = "لطفا صبر کنید تا اطلاعات و تنظیمات خوانده شوند."
    override val stepProfileAvatar: String = "پروفایل و آواتار"
    override val stepCalendarSystem: String = "تقویم"
    override val stepTargetLimits: String = "موظفی و محدودیت‌ها"
    override val stepOffDays: String = "روزهای تعطیل"
    override val stepThemeAppearance: String = "تم و ظاهر"
    override val displayNameTitle: String = "نام نمایشی"
    override val displayNameSubtitle: String = "نام یا لقب خود را برای پروفایل وارد کنید."
    override val enterNamePlaceholder: String = "نام خود را وارد کنید"
    override val avatarStyleTitle: String = "سبک آواتار"
    override val avatarStyleSubtitle: String = "یک آواتار برای محیط کار خود انتخاب کنید."
    override val chooseCalendarSystem: String = "تقویم مورد استفاده در برنامه را انتخاب کنید"
    override val dailyTargetTitle: String = "موظفی روزانه"
    override val setStandardRequiredHours: String = "ساعت کاری استاندارد روزانه خود را تعیین کنید"
    override val targetTimeLabel: String = "ساعت موظفی"
    override val tapMinEnterMaxExit: String = "برای تنظیم حداقل ورود و حداکثر خروج ضربه بزنید"
    override val selectThemePrimaryColor: String = "حالت و رنگ اصلی را انتخاب کنید"
    override val appearanceMode: String = "حالت نمایش"
    override val primaryColorSelect: String = "رنگ اصلی"
    override val welcomeToTimTim: String = "به تیم‌تیم خوش آمدید!"
    override val loggedCheckInSuccessfully: String = "ساعت ورود با موفقیت ثبت شد"
    override val loggedCheckOutSuccessfully: String = "ساعت خروج با موفقیت ثبت شد"
    override val monthRecordsReset: String = "اطلاعات این ماه پاک شد"
    override val allAppDataCleared: String = "تمام اطلاعات برنامه پاک شد"
    override val failedToImportBackup: String = "خطا در بازیابی فایل پشتیبان"
    override val chooseProfilePicture: String = "انتخاب عکس پروفایل"
    override val pickCustomIllustration: String = "یک آواتار انتخاب کنید یا عکس خود را بارگذاری کنید"
    override val uploadFromPhone: String = "بارگذاری از گوشی"
    override val selectPictureGalleryCamera: String = "یک عکس از گالری یا دوربین انتخاب کنید"
    override val galleryLabel: String = "گالری"
    override val defaultAvatarCollection: String = "مجموعه آواتارهای پیش‌فرض"
    override val skipToDefaults: String = "پرش به تنظیمات پیش‌فرض"
    override val continueManualSetup: String = "ادامه تنظیمات دستی"
    override val haveABackup: String = "فایل پشتیبان دارید؟"
    override val ifYouHaveBackup: String = "اگر فایل پشتیبان (.json) دارید، می‌توانید آن را بازیابی کنید و از این تنظیمات عبور کنید."
    override val startUsingTimTim: String = "شروع استفاده از تیم‌تیم"
    override val step: String = "مرحله"
    override val of: String = "از"
    override val yesRestore: String = "بله، بازیابی کن"
    override val widgetTimeLeftToExit: String = "زمان باقیمانده تا خروج"
    override val widgetNotClockedIn: String = "امروز هنوز ورود ثبت نشده"
    override val widgetReadyToStart: String = "آماده برای شروع کار"
    override val widgetClockIn: String = "ثبت ورود"
    override val widgetScheduledDayOff: String = "روز تعطیل برنامه‌ریزی شده"
    override val widgetEnjoyRest: String = "از استراحت خود لذت ببرید!"
    override val widgetShiftCompleted: String = "شیفت کاری کامل شد"
    override val widgetTargetWas: String = "هدف روزانه:"
    override val widgetTargetReachedOvertime: String = "هدف روزانه کامل شد! (اضافه‌کار)"
    override val widgetClockOut: String = "ثبت خروج"
    override val widgetDailyGoal: String = "موظفی روزانه:"
    override val widgetIn: String = "ورود:"
    override val widgetOut: String = "خروج:"
    override val widgetCheckIn: String = "ساعت ورود:"
"""

content = content.replace("    fun formatDigits(text: String): String\n}", "    fun formatDigits(text: String): String\n" + interface_adds + "}")
content = content.replace("    override fun formatDigits(text: String): String = text\n}", "    override fun formatDigits(text: String): String = text\n" + en_adds + "}")
content = content.replace("    override fun formatDigits(text: String): String = text.toPersianDigits()\n}", "    override fun formatDigits(text: String): String = text.toPersianDigits()\n" + fa_adds + "}")

with open(file_path, "w") as f:
    f.write(content)
PYEOF

python3 patch_localization.py

cat << 'PYEOF' > patch_onboarding.py
import re

file_path = "app/src/main/java/com/example/ui/feature/onboarding/OnboardingScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.material3.ExperimentalMaterial3Api\n", "import androidx.compose.material3.ExperimentalMaterial3Api\nimport com.example.ui.localization.LocalAppStrings\n")

replacements = [
    ('Toast.makeText(context, "Invalid backup file: ${e.localizedMessage}", Toast.LENGTH_LONG).show()',
     'Toast.makeText(context, "${strings.invalidBackupFile}: ${e.localizedMessage}", Toast.LENGTH_LONG).show()'),
    ('text = "TimTim"', 'text = strings.appName'),
    ('text = "Skip to Defaults"', 'text = strings.skipToDefaults'),
    ('text = if (currentStep == TOTAL_STEPS - 1) "Start Using TimTim" else "Continue"',
     'text = if (currentStep == TOTAL_STEPS - 1) strings.startUsingTimTim else strings.confirm'),
    ('text = "Step ${currentStep + 1} of $TOTAL_STEPS"',
     'text = "${strings.step} ${currentStep + 1} ${strings.of} $TOTAL_STEPS"'),
    ('text = "Have a Backup?"', 'text = strings.haveABackup'),
    ('text = "If you have a previously saved backup file (.json), you can restore it now and skip this setup process."',
     'text = strings.ifYouHaveBackup'),
    ('text = "Select Backup File (.json)"', 'text = strings.restoreFromFile'),
    ('text = "Continue Manual Setup"', 'text = strings.continueManualSetup'),
    ('text = "Restore Backup & Skip Setup?"', 'text = strings.restoreBackupAndSkip'),
    ('text = "This backup file contains:"', 'text = strings.backupContains'),
    ('text = "• ${backupData.workDays.size} daily attendance records\\n• ${backupData.monthTargets.size} monthly target settings\\n• Saved profile & configuration"',
     'text = "• ${backupData.workDays.size} ${strings.recordsRecords}\\n• ${backupData.monthTargets.size} ${strings.monthlyTargetSettings}\\n• ${strings.savedProfileConfig}"'),
    ('text = "Restoring this data will complete onboarding and take you directly to your timesheet."',
     'text = strings.restoreWillComplete'),
    ('if (success) "Backup restored successfully" else "Import failed: $msg"',
     'if (success) strings.backupRestoredSuccessfully else "${strings.importFailed}: $msg"'),
    ('Text("Restoring...", fontWeight = FontWeight.Bold)',
     'Text(strings.restoring, fontWeight = FontWeight.Bold)'),
    ('Text("Yes, Restore & Skip", fontWeight = FontWeight.Bold)',
     'Text(strings.yesRestoreAndSkip, fontWeight = FontWeight.Bold)'),
    ('text = "Reading Backup File..."', 'text = strings.readingBackupFile'),
    ('text = "Please wait while your attendance and settings are being read."', 'text = strings.pleaseWaitAttendance'),
    ('0 -> "Profile & Avatar"', '0 -> strings.stepProfileAvatar'),
    ('1 -> "Calendar System"', '1 -> strings.stepCalendarSystem'),
    ('2 -> "Target & Limits"', '2 -> strings.stepTargetLimits'),
    ('3 -> "Off Days"', '3 -> strings.stepOffDays'),
    ('4 -> "Theme & Appearance"', '4 -> strings.stepThemeAppearance'),
    ('title = "Display Name"', 'title = strings.displayNameTitle'),
    ('text = "Enter your preferred name or nickname for the profile card."', 'text = strings.displayNameSubtitle'),
    ('placeholder = { Text("Enter your name or nickname") }', 'placeholder = { Text(strings.enterNamePlaceholder) }'),
    ('title = "Avatar Style"', 'title = strings.avatarStyleTitle'),
    ('text = "Pick an avatar that represents your workspace identity."', 'text = strings.avatarStyleSubtitle'),
    ('title = "Calendar Type"', 'title = strings.calendarTypeSetting'),
    ('text = "Select calendar type"', 'text = strings.selectCalendarTypeTitle'),
    ('text = "Choose the calendar system used across the entire app"', 'text = strings.chooseCalendarSystem'),
    ('title = "Daily Target"', 'title = strings.dailyTargetTitle'),
    ('text = "Daily target"', 'text = strings.dailyTarget'),
    ('text = "Set your standard required daily work hours"', 'text = strings.setStandardRequiredHours'),
    ('text = "target time"', 'text = strings.targetTimeLabel'),
    ('title = "Work Limits"', 'title = strings.workLimits'),
    ('text = "Work Limits"', 'text = strings.workLimits'),
    ('text = "Tap Min Enter or Max Exit to set limits"', 'text = strings.tapMinEnterMaxExit'),
    ('label = "Min Enter"', 'label = strings.minEnterTime'),
    ('label = "Max Exit"', 'label = strings.maxExitTime'),
    ('title = "Select Theme"', 'title = strings.selectThemeTitle'),
    ('text = "Select Theme"', 'text = strings.selectThemeTitle'),
    ('text = "Choose your mode and primary color"', 'text = strings.selectThemePrimaryColor'),
    ('text = "Appearance Mode"', 'text = strings.appearanceMode'),
    ('text = "Primary Color"', 'text = strings.primaryColorSelect'),
    ('label = "System"', 'label = strings.themeSystem'),
    ('label = "Light"', 'label = strings.themeLight'),
    ('label = "Dark"', 'label = strings.themeDark'),
]

for old, new in replacements:
    content = content.replace(old, new)

funcs = [
    "fun OnboardingScreen(",
    "fun SetupProfileAvatarStep(",
    "fun SetupCalendarSystemStep(",
    "fun SetupTargetLimitsStep(",
    "fun SetupOffDaysStep(",
    "fun SetupThemeAppearanceStep(",
    "fun OnboardingStepContent("
]

for func in funcs:
    pattern = re.compile(re.escape(func) + r'[^\{]*\{')
    def repl(m):
        return m.group(0) + '\n    val strings = LocalAppStrings.current\n'
    content = pattern.sub(repl, content)

with open(file_path, "w") as f:
    f.write(content)
PYEOF

python3 patch_onboarding.py

cat << 'PYEOF' > patch_settings.py
import re

file_path = "app/src/main/java/com/example/ui/feature/settings/SettingsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

replacements = [
    ('Toast.makeText(context, "Export error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()',
     'Toast.makeText(context, "${strings.error}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()'),
    ('Text(\n                    text = "Restore Backup?",\n                    fontWeight = FontWeight.Bold,\n                    style = MaterialTheme.typography.titleLarge\n                )',
     'Text(\n                    text = strings.restoreBackupAndSkip,\n                    fontWeight = FontWeight.Bold,\n                    style = MaterialTheme.typography.titleLarge\n                )'),
    ('text = "This backup file contains:"', 'text = strings.backupContains'),
    ('text = "• ${backupData.workDays.size} daily attendance records\\n• ${backupData.monthTargets.size} monthly target settings\\n• Saved preferences & configurations"',
     'text = "• ${backupData.workDays.size} ${strings.recordsRecords}\\n• ${backupData.monthTargets.size} ${strings.monthlyTargetSettings}\\n• ${strings.savedProfileConfig}"'),
    ('text = "Would you like to import and restore all saved data now?"',
     'text = strings.restoreWillComplete'),
    ('if (success) "Backup restored successfully" else "Import failed: $msg"',
     'if (success) strings.backupRestoredSuccessfully else "${strings.importFailed}: $msg"'),
    ('Text("Yes, Restore", fontWeight = FontWeight.Bold)',
     'Text(strings.yesRestore, fontWeight = FontWeight.Bold)'),
    ('title = "Work Limits"', 'title = strings.workLimits'),
    ('subtitle = "Scroll to set Minimum Enter time limit"', 'subtitle = strings.tapMinEnterMaxExit'),
    ('subtitle = "Scroll to set Maximum Exit time limit"', 'subtitle = strings.tapMinEnterMaxExit'),
]

for old, new in replacements:
    content = content.replace(old, new)

with open(file_path, "w") as f:
    f.write(content)
PYEOF

python3 patch_settings.py

cat << 'PYEOF' > patch_avatar.py
import re

file_path = "app/src/main/java/com/example/ui/components/AvatarComponents.kt"
with open(file_path, "r") as f:
    content = f.read()

if "import com.example.ui.localization.LocalAppStrings" not in content:
    content = content.replace("import androidx.compose.material3.ExperimentalMaterial3Api\n", "import androidx.compose.material3.ExperimentalMaterial3Api\nimport com.example.ui.localization.LocalAppStrings\n")

if "val strings = LocalAppStrings.current" not in content:
    content = content.replace("fun AvatarSelectionBottomSheet(\n    currentAvatarId: String,\n    onAvatarSelected: (String) -> Unit,\n    onDismiss: () -> Unit\n) {",
                              "fun AvatarSelectionBottomSheet(\n    currentAvatarId: String,\n    onAvatarSelected: (String) -> Unit,\n    onDismiss: () -> Unit\n) {\n    val strings = LocalAppStrings.current")

replacements = [
    ('text = "Choose Profile Picture"', 'text = strings.chooseProfilePicture'),
    ('text = "Pick a custom illustration or upload your photo"', 'text = strings.pickCustomIllustration'),
    ('text = "Upload from Your Phone"', 'text = strings.uploadFromPhone'),
    ('text = "Select any picture from your gallery or camera"', 'text = strings.selectPictureGalleryCamera'),
    ('text = "Gallery"', 'text = strings.galleryLabel'),
    ('text = "Default Avatar Collection"', 'text = strings.defaultAvatarCollection'),
]

for old, new in replacements:
    content = content.replace(old, new)

with open(file_path, "w") as f:
    f.write(content)
PYEOF

python3 patch_avatar.py

cat << 'PYEOF' > patch_widget.py
import re

file_path = "app/src/main/java/com/example/widget/WorkRemainingWidgetProvider.kt"
with open(file_path, "r") as f:
    content = f.read()

if "import com.example.ui.localization.EnStrings" not in content:
    content = content.replace("import com.example.util.CalendarHelper", "import com.example.util.CalendarHelper\nimport com.example.ui.localization.EnStrings\nimport com.example.ui.localization.FaStrings")

content = content.replace("val settings = getAppSettingsUseCase()", "val settings = getAppSettingsUseCase()\n        val strings = if (settings.language == \"fa\") FaStrings else EnStrings")

replacements = [
    ('views.setTextViewText(R.id.widget_title, "Work Status")', 'views.setTextViewText(R.id.widget_title, strings.status)'),
    ('views.setTextViewText(R.id.widget_timer_subtitle, "Not clocked in yet today")', 'views.setTextViewText(R.id.widget_timer_subtitle, strings.widgetNotClockedIn)'),
    ('views.setTextViewText(R.id.widget_info_left, "Daily Goal: ${targetMinutes / 60}h ${targetMinutes % 60}m")', 'views.setTextViewText(R.id.widget_info_left, "${strings.widgetDailyGoal} ${strings.formatTime(targetMinutes / 60, targetMinutes % 60)}")'),
    ('views.setTextViewText(R.id.widget_info_right, "Ready to start work")', 'views.setTextViewText(R.id.widget_info_right, strings.widgetReadyToStart)'),
    ('views.setTextViewText(R.id.widget_btn_action, "Clock In")', 'views.setTextViewText(R.id.widget_btn_action, strings.widgetClockIn)'),
    ('views.setTextViewText(R.id.widget_timer_value, "Day Off")', 'views.setTextViewText(R.id.widget_timer_value, strings.dayOff)'),
    ('views.setTextViewText(R.id.widget_timer_subtitle, "Scheduled day off")', 'views.setTextViewText(R.id.widget_timer_subtitle, strings.widgetScheduledDayOff)'),
    ('views.setTextViewText(R.id.widget_info_left, "Enjoy your rest!")', 'views.setTextViewText(R.id.widget_info_left, strings.widgetEnjoyRest)'),
    ('views.setTextViewText(R.id.widget_timer_value, "${workedH}h ${workedM}m")', 'views.setTextViewText(R.id.widget_timer_value, strings.formatHourMinute(workedH, workedM))'),
    ('"Shift completed (+${diff / 60}h ${diff % 60}m overtime)"', '"${strings.widgetShiftCompleted} (+${strings.formatHourMinute(diff / 60, diff % 60)})"'),
    ('"Shift completed (-${deficit / 60}h ${deficit % 60}m deficit)"', '"${strings.widgetShiftCompleted} (-${strings.formatHourMinute(deficit / 60, deficit % 60)})"'),
    ('views.setTextViewText(R.id.widget_info_left, "In: $formattedEnter  •  Out: $formattedExit")', 'views.setTextViewText(R.id.widget_info_left, "${strings.widgetIn} ${strings.formatDigits(formattedEnter)}  •  ${strings.widgetOut} ${strings.formatDigits(formattedExit)}")'),
    ('views.setTextViewText(R.id.widget_info_right, "Target was $formattedExpectedExit")', 'views.setTextViewText(R.id.widget_info_right, "${strings.widgetTargetWas} ${strings.formatDigits(formattedExpectedExit)}")'),
    ('views.setTextViewText(R.id.widget_timer_value, "+${otH}h ${otM}m")', 'views.setTextViewText(R.id.widget_timer_value, "+${strings.formatHourMinute(otH, otM)}")'),
    ('views.setTextViewText(R.id.widget_timer_subtitle, "Daily target reached! (Overtime)")', 'views.setTextViewText(R.id.widget_timer_subtitle, strings.widgetTargetReachedOvertime)'),
    ('views.setTextViewText(R.id.widget_timer_value, "${remH}h ${remM}m")', 'views.setTextViewText(R.id.widget_timer_value, strings.formatHourMinute(remH, remM))'),
    ('views.setTextViewText(R.id.widget_timer_subtitle, "Time left to exit")', 'views.setTextViewText(R.id.widget_timer_subtitle, strings.widgetTimeLeftToExit)'),
    ('views.setTextViewText(R.id.widget_info_left, "Check-in: $formattedEnter")', 'views.setTextViewText(R.id.widget_info_left, "${strings.widgetCheckIn} ${strings.formatDigits(formattedEnter)}")'),
    ('views.setTextViewText(R.id.widget_info_right, "Target Exit: $formattedExpectedExit")', 'views.setTextViewText(R.id.widget_info_right, "${strings.targetExitTime}: ${strings.formatDigits(formattedExpectedExit)}")'),
    ('views.setTextViewText(R.id.widget_btn_action, "Clock Out")', 'views.setTextViewText(R.id.widget_btn_action, strings.widgetClockOut)'),
    ('views.setTextViewText(R.id.widget_timer_value, "--h --m")', 'views.setTextViewText(R.id.widget_timer_value, strings.formatDigits("--:--"))'),
]

for old, new in replacements:
    content = content.replace(old, new)

with open(file_path, "w") as f:
    f.write(content)
PYEOF

python3 patch_widget.py

cat << 'PYEOF' > app/src/main/java/com/example/ui/mvi/AppMessage.kt
package com.example.ui.mvi

import com.example.ui.localization.AppStrings

enum class AppMessage {
    LOGGED_CHECK_IN,
    LOGGED_CHECK_OUT,
    MONTH_RECORDS_RESET,
    ALL_APP_DATA_CLEARED,
    FAILED_TO_IMPORT_BACKUP,
    WELCOME_TO_TIMTIM,
    EXIT_TIME_CANNOT_BE_EARLIER,
    ENTER_TIME_CANNOT_BE_LATER,
    IMPORT_FAILED;

    fun asString(strings: AppStrings): String {
        return when (this) {
            LOGGED_CHECK_IN -> strings.loggedCheckInSuccessfully
            LOGGED_CHECK_OUT -> strings.loggedCheckOutSuccessfully
            MONTH_RECORDS_RESET -> strings.monthRecordsReset
            ALL_APP_DATA_CLEARED -> strings.allAppDataCleared
            FAILED_TO_IMPORT_BACKUP -> strings.failedToImportBackup
            WELCOME_TO_TIMTIM -> strings.welcomeToTimTim
            EXIT_TIME_CANNOT_BE_EARLIER -> strings.exitTimeCannotBeEarlier
            ENTER_TIME_CANNOT_BE_LATER -> strings.enterTimeCannotBeLater
            IMPORT_FAILED -> strings.importFailed
        }
    }
}
PYEOF

cat << 'PYEOF' > app/src/main/java/com/example/ui/mvi/WorkUiEffect.kt
package com.example.ui.mvi

sealed interface WorkUiEffect {
    data class ShowSnackbar(val message: AppMessage, val extra: String? = null) : WorkUiEffect
    data class ScrollToTop(val animated: Boolean = true) : WorkUiEffect
    data class TimeValidationError(val errorType: AppMessage) : WorkUiEffect
}
PYEOF

cat << 'PYEOF' > patch_viewmodel.py
import re

file_path = "app/src/main/java/com/example/ui/WorkViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("import com.example.ui.mvi.WorkUiEffect\n", "import com.example.ui.mvi.WorkUiEffect\nimport com.example.ui.mvi.AppMessage\n")

replacements = [
    ('WorkUiEffect.ShowSnackbar("Logged check-in time successfully")', 'WorkUiEffect.ShowSnackbar(AppMessage.LOGGED_CHECK_IN)'),
    ('WorkUiEffect.ShowSnackbar("Logged check-out time successfully")', 'WorkUiEffect.ShowSnackbar(AppMessage.LOGGED_CHECK_OUT)'),
    ('WorkUiEffect.ShowSnackbar("Month records reset")', 'WorkUiEffect.ShowSnackbar(AppMessage.MONTH_RECORDS_RESET)'),
    ('WorkUiEffect.ShowSnackbar("All application data cleared")', 'WorkUiEffect.ShowSnackbar(AppMessage.ALL_APP_DATA_CLEARED)'),
    ('_effects.send(WorkUiEffect.ShowSnackbar(msg))', '_effects.send(WorkUiEffect.ShowSnackbar(AppMessage.FAILED_TO_IMPORT_BACKUP, msg))'),
    ('_effects.send(WorkUiEffect.ShowSnackbar("Import failed: $errMsg"))', '_effects.send(WorkUiEffect.ShowSnackbar(AppMessage.IMPORT_FAILED, errMsg))'),
    ('WorkUiEffect.ShowSnackbar("Welcome to TimTim!")', 'WorkUiEffect.ShowSnackbar(AppMessage.WELCOME_TO_TIMTIM)'),
    ('WorkUiEffect.TimeValidationError(result.message)', 'WorkUiEffect.TimeValidationError(result.errorType)'),
]

for old, new in replacements:
    content = content.replace(old, new)

with open(file_path, "w") as f:
    f.write(content)
PYEOF

python3 patch_viewmodel.py

cat << 'PYEOF' > patch_main.py
import re

file_path = "app/src/main/java/com/example/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("import com.example.ui.mvi.WorkUiEffect\n", "import com.example.ui.mvi.WorkUiEffect\nimport com.example.ui.localization.LocalAppStrings\n")

content = content.replace("is WorkUiEffect.ShowSnackbar -> {\n                    snackbarHostState.showSnackbar(effect.message)\n                }",
                          "is WorkUiEffect.ShowSnackbar -> {\n                    val msg = effect.message.asString(strings) + (effect.extra?.let { \" $it\" } ?: \"\")\n                    snackbarHostState.showSnackbar(msg)\n                }")

content = content.replace("is WorkUiEffect.TimeValidationError -> {\n                    snackbarHostState.showSnackbar(effect.message)\n                }",
                          "is WorkUiEffect.TimeValidationError -> {\n                    snackbarHostState.showSnackbar(effect.errorType.asString(strings))\n                }")

with open(file_path, "w") as f:
    f.write(content)
PYEOF

python3 patch_main.py

cat << 'PYEOF' > patch_usecase.py
import re

file_path = "app/src/main/java/com/example/domain/usecase/LogWorkTimeUseCase.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("import com.example.domain.repository.WorkRepository\n", "import com.example.domain.repository.WorkRepository\nimport com.example.ui.mvi.AppMessage\n")

content = content.replace("data class Error(val message: String) : TimeValidationResult", "data class Error(val errorType: AppMessage) : TimeValidationResult")

content = content.replace('TimeValidationResult.Error("Exit time cannot be earlier than enter time")', 'TimeValidationResult.Error(AppMessage.EXIT_TIME_CANNOT_BE_EARLIER)')
content = content.replace('TimeValidationResult.Error("Enter time cannot be later than exit time")', 'TimeValidationResult.Error(AppMessage.ENTER_TIME_CANNOT_BE_LATER)')

with open(file_path, "w") as f:
    f.write(content)
PYEOF

python3 patch_usecase.py

cat << 'PYEOF' > fix_strings8.py
import re

file_path = "app/src/main/java/com/example/ui/feature/onboarding/OnboardingScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("fun SetupTargetLimitsStep(\n    strings: com.example.ui.localization.AppStrings,\n    settings", "fun SetupTargetLimitsStep(\n    settings")
content = content.replace("fun SetupCalendarSystemStep(\n    strings: com.example.ui.localization.AppStrings,\n    calendarType", "fun SetupCalendarSystemStep(\n    calendarType")
content = content.replace("fun SetupProfileAvatarStep(\n    strings: com.example.ui.localization.AppStrings,\n    settings", "fun SetupProfileAvatarStep(\n    settings")
content = content.replace("fun SetupOffDaysStep(\n    strings: com.example.ui.localization.AppStrings,\n    settings", "fun SetupOffDaysStep(\n    settings")
content = content.replace("fun SetupThemeAppearanceStep(\n    strings: com.example.ui.localization.AppStrings,\n    settings", "fun SetupThemeAppearanceStep(\n    settings")
content = content.replace("fun OnboardingStepContent(\n    strings: com.example.ui.localization.AppStrings,\n    currentStep", "fun OnboardingStepContent(\n    currentStep")

# In OnboardingStepContent
content = content.replace("SetupProfileAvatarStep(\n                            strings,\n                            settings", "SetupProfileAvatarStep(\n                            settings")
content = content.replace("SetupCalendarSystemStep(\n                            strings,\n                            calendarType", "SetupCalendarSystemStep(\n                            calendarType")
content = content.replace("SetupTargetLimitsStep(\n                            strings,\n                            settings", "SetupTargetLimitsStep(\n                            settings")
content = content.replace("SetupOffDaysStep(\n                            strings,\n                            settings", "SetupOffDaysStep(\n                            settings")
content = content.replace("SetupThemeAppearanceStep(\n                            strings,\n                            settings", "SetupThemeAppearanceStep(\n                            settings")

# Fix main screen
content = content.replace("OnboardingStepContent(\n                        strings,\n                        currentStep", "OnboardingStepContent(\n                        currentStep")


with open(file_path, "w") as f:
    f.write(content)
PYEOF
python3 fix_strings8.py
