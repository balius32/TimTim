package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.domain.reminder.CheckoutReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CheckoutReminderReceiver : BroadcastReceiver(), KoinComponent {

    private val scheduler: CheckoutReminderScheduler by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                if (action == CheckoutReminderIntents.ACTION_FIRE) {
                    scheduler.showFiredReminder()
                } else {
                    scheduler.syncToday()
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
