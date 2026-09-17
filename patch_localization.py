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
