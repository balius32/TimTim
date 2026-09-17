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
