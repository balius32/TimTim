package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.domain.model.DayStatus
import com.example.domain.model.DaySummary
import com.example.domain.model.WorkDay
import com.example.ui.screens.WireframeDailyLogRowCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun timesheet_row_screenshot() {
        val sampleDay = WorkDay(
            year = 2026,
            month = 8,
            dayNumber = 1,
            enterHour = 8,
            enterMinute = 30,
            exitHour = 17,
            exitMinute = 15
        )
        val daySummary = DaySummary(
            day = sampleDay,
            workedMinutes = 525,
            targetMinutes = 480,
            diffMinutes = 45,
            status = DayStatus.OVERTIME
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                WireframeDailyLogRowCard(
                    daySummary = daySummary,
                    formattedMonthDay = "08/01",
                    dayOfWeek = "Fri",
                    onEnterClick = {},
                    onExitClick = {},
                    onToggleDayOff = {},
                    onClearDay = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
    }
}
