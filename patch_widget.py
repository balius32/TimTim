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
