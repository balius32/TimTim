package com.example.domain.model

const val CHECKOUT_REMINDER_LEAD_MINUTES = 5

private const val MINUTES_PER_DAY = 24 * 60

/**
 * Clock time to fire the checkout reminder.
 * [dayOffset] is 0 for later today, 1 when the shift crosses midnight, or -1 when the lead
 * falls on the previous day.
 */
data class CheckoutReminderFire(
    val dayOffset: Int,
    val minuteOfDay: Int
) {
    val hour: Int
        get() = minuteOfDay / 60

    val minute: Int
        get() = minuteOfDay % 60
}

/**
 * Fire time is enter + daily target − [leadMinutes], without wrapping before the subtraction.
 * Matches [WorkDay.estimatedCheckoutMinutes] for the checkout clock, then steps back by the lead.
 */
fun checkoutReminderFire(
    enterHour: Int,
    enterMinute: Int,
    targetMinutes: Int,
    leadMinutes: Int = CHECKOUT_REMINDER_LEAD_MINUTES
): CheckoutReminderFire {
    val checkoutMinutes = enterHour * 60 + enterMinute + targetMinutes
    val fireMinutes = checkoutMinutes - leadMinutes
    return CheckoutReminderFire(
        dayOffset = floorDiv(fireMinutes, MINUTES_PER_DAY),
        minuteOfDay = floorMod(fireMinutes, MINUTES_PER_DAY)
    )
}

private fun floorDiv(value: Int, divisor: Int): Int {
    val remainder = value % divisor
    val quotient = value / divisor
    return if (remainder != 0 && value < 0) quotient - 1 else quotient
}

private fun floorMod(value: Int, divisor: Int): Int {
    val remainder = value % divisor
    return if (remainder != 0 && value < 0) remainder + divisor else remainder
}
