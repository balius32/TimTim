package com.example.domain.reminder

interface CheckoutReminderScheduler {
    suspend fun syncToday()
    suspend fun showFiredReminder()
}
