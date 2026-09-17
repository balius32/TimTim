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
