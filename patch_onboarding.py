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
