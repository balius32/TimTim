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
