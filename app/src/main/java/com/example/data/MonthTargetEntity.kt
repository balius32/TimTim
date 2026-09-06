package com.example.data

import androidx.room.Entity

@Entity(
    tableName = "month_targets",
    primaryKeys = ["year", "month"]
)
data class MonthTargetEntity(
    val year: Int,
    val month: Int,
    val dailyRequiredMinutes: Int
) {
    val dailyRequiredHours: Int
        get() = dailyRequiredMinutes / 60

    val dailyRequiredRemainderMinutes: Int
        get() = dailyRequiredMinutes % 60

    fun formattedDailyTarget(): String {
        val h = dailyRequiredHours
        val m = dailyRequiredRemainderMinutes
        return if (m == 0) "${h}h 00m" else "${h}h ${m}m"
    }
}
