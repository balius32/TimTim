package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TimTim", appName)
  }

  @Test
  fun testBackupGenerationAndSafeParsing() {
    val settings = com.example.data.AppSettingsEntity(
        dailyRequiredMinutes = 450,
        userName = "Alice",
        calendarType = "GREGORIAN"
    )
    val targets = listOf(
        com.example.data.MonthTargetEntity(year = 2026, month = 9, dailyRequiredMinutes = 450)
    )
    val days = listOf(
        com.example.data.WorkDayEntity(year = 2026, month = 9, dayNumber = 1, enterHour = 9, enterMinute = 0, exitHour = 17, exitMinute = 30)
    )

    val json = com.example.util.DataBackupHelper.generateBackupJson(settings, targets, days)
    val parsed = com.example.util.DataBackupHelper.parseBackupJson(json)

    assertEquals(450, parsed.settings?.dailyRequiredMinutes)
    assertEquals("Alice", parsed.settings?.userName)
    assertEquals(1, parsed.monthTargets.size)
    assertEquals(1, parsed.workDays.size)
    assertEquals(9, parsed.workDays[0].enterHour)
  }

  @Test
  fun testMalformedBackupJsonDoesNotCrash() {
    val partialJson = """{"version":1,"settings":{"dailyRequiredMinutes":420},"workDays":[{"year":2026,"month":9,"dayNumber":5}]}"""
    val parsed = com.example.util.DataBackupHelper.parseBackupJson(partialJson)

    assertEquals(420, parsed.settings?.dailyRequiredMinutes)
    assertEquals(1, parsed.workDays.size)
    assertEquals(5, parsed.workDays[0].dayNumber)
  }
}
