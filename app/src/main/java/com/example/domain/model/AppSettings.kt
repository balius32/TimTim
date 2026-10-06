package com.example.domain.model

data class AppSettings(
    val id: Int = 1,
    val dailyRequiredMinutes: Int = 480,
    val offDaysOfWeek: String = "FRIDAY",
    val themeMode: String = "dark_teal",
    val calendarType: String = "GREGORIAN",
    val appLanguage: String = "SYSTEM",
    val userName: String = "username",
    val avatarId: String = "minimal_avatar",
    val minEnterMinutes: Int? = null,
    val maxExitMinutes: Int? = null,
    val minDailyMinutes: Int? = null,
    val maxDailyMinutes: Int? = null,
    val hasCompletedOnboarding: Boolean = false,
    val driveAccountEmail: String? = null,
    val driveLastBackupEpochMs: Long? = null
) {
    fun formattedDailyTarget(isFarsi: Boolean = false): String {
        if (dailyRequiredMinutes <= 0) return "_ _ : _ _"
        val h = dailyRequiredMinutes / 60
        val m = dailyRequiredMinutes % 60
        return if (isFarsi) {
            val raw = String.format(java.util.Locale.US, "%d:%02d", h, m)
            com.example.util.NumberFormatter.toFarsiDigits(raw)
        } else {
            if (m == 0) "${h}h 00m" else "${h}h ${m}m"
        }
    }
    fun formattedMinEnterTime(): String {
        val mins = minEnterMinutes ?: return "None"
        val h = mins / 60
        val m = mins % 60
        return String.format("%02d:%02d", h, m)
    }

    fun formattedMaxExitTime(): String {
        val mins = maxExitMinutes ?: return "None"
        val h = mins / 60
        val m = mins % 60
        return String.format("%02d:%02d", h, m)
    }

    fun formattedMinDailyLimit(): String {
        val mins = minDailyMinutes ?: return "None"
        val h = mins / 60
        val m = mins % 60
        return String.format("%02d:%02d", h, m)
    }

    fun formattedMaxDailyLimit(): String {
        val mins = maxDailyMinutes ?: return "None"
        val h = mins / 60
        val m = mins % 60
        return String.format("%02d:%02d", h, m)
    }
}
