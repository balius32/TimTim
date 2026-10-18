package com.example

import com.example.domain.model.CHECKOUT_REMINDER_LEAD_MINUTES
import com.example.domain.model.checkoutReminderFire
import org.junit.Assert.assertEquals
import org.junit.Test

class CheckoutReminderTest {

    @Test
    fun sameDayCheckoutFiresFiveMinutesEarlier() {
        val fire = checkoutReminderFire(enterHour = 9, enterMinute = 0, targetMinutes = 480)
        assertEquals(0, fire.dayOffset)
        assertEquals(16, fire.hour)
        assertEquals(55, fire.minute)
    }

    @Test
    fun shiftCrossingMidnightFiresNextDay() {
        val fire = checkoutReminderFire(enterHour = 20, enterMinute = 0, targetMinutes = 480)
        assertEquals(1, fire.dayOffset)
        assertEquals(3, fire.hour)
        assertEquals(55, fire.minute)
    }

    @Test
    fun checkoutAtMidnightFiresFiveMinutesBeforeOnSameDay() {
        val fire = checkoutReminderFire(enterHour = 16, enterMinute = 0, targetMinutes = 480)
        assertEquals(0, fire.dayOffset)
        assertEquals(23, fire.hour)
        assertEquals(55, fire.minute)
    }

    @Test
    fun leadBeforeEnterRollsToPreviousDay() {
        val fire = checkoutReminderFire(
            enterHour = 0,
            enterMinute = 2,
            targetMinutes = 1,
            leadMinutes = 5
        )
        assertEquals(-1, fire.dayOffset)
        assertEquals(23, fire.hour)
        assertEquals(58, fire.minute)
    }

    @Test
    fun defaultLeadIsFiveMinutes() {
        assertEquals(5, CHECKOUT_REMINDER_LEAD_MINUTES)
        val withDefault = checkoutReminderFire(enterHour = 8, enterMinute = 30, targetMinutes = 480)
        val explicit = checkoutReminderFire(
            enterHour = 8,
            enterMinute = 30,
            targetMinutes = 480,
            leadMinutes = 5
        )
        assertEquals(explicit, withDefault)
        assertEquals(16, withDefault.hour)
        assertEquals(25, withDefault.minute)
    }
}
