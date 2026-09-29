package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = MainApplication::class)
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

  @Test
  fun testMainActivityFirstLaunch() {
    val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
    controller.setup()
    org.junit.Assert.assertNotNull(controller.get())
  }

  @Test
  fun testFarsiTimePickerStringFormatting() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val farsiContext = com.example.util.LocaleHelper.getLocalizedContext(context, "fa")
    val enterTitle = farsiContext.getString(R.string.time_picker_title_enter, 15)
    val exitTitle = farsiContext.getString(R.string.time_picker_title_exit, 15)
    val dayNumberStr = farsiContext.getString(R.string.timesheet_day_number, 15)
    val dayFormat = farsiContext.getString(R.string.time_picker_day_format, "ورود", 15)

    org.junit.Assert.assertTrue(enterTitle.contains("15") || enterTitle.contains("۱۵"))
    org.junit.Assert.assertTrue(exitTitle.contains("15") || exitTitle.contains("۱۵"))
    org.junit.Assert.assertTrue(dayNumberStr.contains("15") || dayNumberStr.contains("۱۵"))
    org.junit.Assert.assertTrue(dayFormat.contains("ورود"))
  }

  @Test
  fun testFarsiDurationFormattingWithColon() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val enContext = com.example.util.LocaleHelper.getLocalizedContext(context, "en")
    val faContext = com.example.util.LocaleHelper.getLocalizedContext(context, "fa")

    // 14 hours 3 minutes = 843 minutes
    val enDuration = com.example.ui.localization.AppStrings.Units.formatDuration(enContext, 843)
    val faDuration = com.example.ui.localization.AppStrings.Units.formatDuration(faContext, 843)
    val faSignedPositive = com.example.ui.localization.AppStrings.Units.formatSignedDuration(faContext, 843)
    val faSignedNegative = com.example.ui.localization.AppStrings.Units.formatSignedDuration(faContext, -75)

    assertEquals("14h 3m", enDuration)
    assertEquals("۱۴:۰۳", faDuration)
    assertEquals("+۱۴:۰۳", faSignedPositive)
    assertEquals("-۱:۱۵", faSignedNegative)

    // Companion format in WorkCalculationSummary
    val enSummary = com.example.domain.model.WorkCalculationSummary.formatMinutes(843, isFarsi = false)
    val faSummary = com.example.domain.model.WorkCalculationSummary.formatMinutes(843, isFarsi = true)
    assertEquals("14h 3m", enSummary)
    assertEquals("۱۴:۰۳", faSummary)
  }

  @Test
  fun testFarsiHomeHeroTotalHoursFormatting() {
    val totalMins = 15 * 60 + 12 // 15h 12m
    val h = totalMins / 60
    val m = totalMins % 60
    val raw = "$h\u00A0ساعت ، $m\u00A0دقیقه"
    val localized = com.example.util.NumberFormatter.toFarsiDigits(raw)
    assertEquals("۱۵\u00A0ساعت ، ۱۲\u00A0دقیقه", localized)
  }

  @Test
  fun testReportExportMethodsDoNotCrash() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val summary = com.example.domain.model.WorkCalculationSummary(
      totalWorkedMinutes = 500,
      requiredTotalMinutes = 480,
      dailyTargetMinutes = 480,
      overtimeMinutes = 20,
      deficitMinutes = 0,
      netBalanceMinutes = 20,
      completedDaysCount = 1,
      offDaysCount = 0,
      workingDaysCount = 1,
      daySummaries = listOf(
        com.example.domain.model.DaySummary(
          day = com.example.domain.model.WorkDay(
            dayNumber = 1,
            enterHour = 8,
            enterMinute = 0,
            exitHour = 16,
            exitMinute = 20
          ),
          workedMinutes = 500,
          targetMinutes = 480,
          diffMinutes = 20,
          status = com.example.domain.model.DayStatus.OVERTIME
        )
      )
    )
    val uiState = com.example.ui.mvi.WorkUiState(
      reportYear = 1403,
      reportMonth = 7,
      userName = "Tester"
    )

    com.example.util.ReportExportHelper.shareTextSummary(context, summary, uiState)
    com.example.util.ReportExportHelper.shareCsvExcel(context, summary, uiState)
    com.example.util.ReportExportHelper.sharePdfReport(context, summary, uiState)
  }

  @Test
  fun testAccurateDayWorkHoursAndDeficitOvertime() {
    val calc = com.example.domain.usecase.CalculateMonthSummaryUseCase()

    // Day 1: Overtime day (08:30 to 17:15) = 525 mins (8h 45m). Target: 480 mins (8h 00m)
    val day1 = com.example.domain.model.WorkDay(
      dayNumber = 1,
      enterHour = 8,
      enterMinute = 30,
      exitHour = 17,
      exitMinute = 15
    )
    assertEquals(525, day1.workedMinutes)

    // Day 2: Deficit day (08:30 to 16:00) = 450 mins (7h 30m). Target: 480 mins (8h 00m)
    val day2 = com.example.domain.model.WorkDay(
      dayNumber = 2,
      enterHour = 8,
      enterMinute = 30,
      exitHour = 16,
      exitMinute = 0
    )
    assertEquals(450, day2.workedMinutes)

    // Day 3: In progress (checked in at 09:00, no checkout)
    val day3 = com.example.domain.model.WorkDay(
      dayNumber = 3,
      enterHour = 9,
      enterMinute = 0
    )
    assertEquals(0, day3.workedMinutes)

    // Day 4: Unset (not logged)
    val day4 = com.example.domain.model.WorkDay(
      dayNumber = 4
    )
    assertEquals(0, day4.workedMinutes)

    // Calculate month summary with daily target 480 mins (8h)
    val result = calc(listOf(day1, day2, day3, day4), dailyRequiredMinutes = 480)

    val s1 = result.daySummaries[0]
    assertEquals(525, s1.workedMinutes)
    assertEquals(480, s1.targetMinutes)
    assertEquals(45, s1.diffMinutes)
    assertEquals(com.example.domain.model.DayStatus.OVERTIME, s1.status)

    val s2 = result.daySummaries[1]
    assertEquals(450, s2.workedMinutes)
    assertEquals(480, s2.targetMinutes)
    assertEquals(-30, s2.diffMinutes)
    assertEquals(com.example.domain.model.DayStatus.DEFICIT, s2.status)

    val s3 = result.daySummaries[2]
    assertEquals(0, s3.workedMinutes)
    assertEquals(480, s3.targetMinutes)
    assertEquals(0, s3.diffMinutes) // In-progress day should NOT have fake max deficit
    assertEquals(com.example.domain.model.DayStatus.IN_PROGRESS, s3.status)

    val s4 = result.daySummaries[3]
    assertEquals(0, s4.workedMinutes)
    assertEquals(480, s4.targetMinutes)
    assertEquals(0, s4.diffMinutes) // Unset day should NOT have fake max deficit
    assertEquals(com.example.domain.model.DayStatus.UNSET, s4.status)

    assertEquals(525 + 450, result.totalWorkedMinutes)
    assertEquals(45, result.overtimeMinutes)
    assertEquals(30, result.deficitMinutes)
    assertEquals(15, result.netBalanceMinutes) // +45 - 30 = +15 net
  }
}
