package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.WorkCalculationSummary
import com.example.domain.model.WorkDay
import com.example.ui.mvi.WorkUiState
import com.example.ui.theme.DeficitAmber
import com.example.ui.theme.DeficitAmberContainer
import com.example.ui.theme.DeficitRed
import com.example.ui.theme.DeficitRedContainer
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.OnDeficitAmberContainer
import com.example.ui.theme.OnDeficitRedContainer
import com.example.ui.theme.OnOvertimeGreenContainer
import com.example.ui.theme.OvertimeGreen
import com.example.ui.theme.OvertimeGreenContainer
import com.example.util.CalendarHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Calendar
import java.util.Locale

/**
 * Remaining Time Screen designed according to the project's Bento design system.
 *
 * Implements:
 * 1. Standard Material 3 Scaffold & TopAppBar with system theme integration.
 * 2. Cool animated glowing circular progress clock displaying live countdown (hh : mm : ss).
 * 3. Accurate elapsed & remaining calculation respecting day limits and midnight shifts.
 * 4. Structured Bento-style cards for check-in time, estimated check-out time, elapsed time, and daily progress.
 * 5. Full-width primary action button to "Exit Now" (log checkout).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemainingTimeScreen(
    uiState: WorkUiState,
    day: WorkDay?,
    onBackClick: () -> Unit,
    onExitNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Intercept hardware back button
    BackHandler {
        onBackClick()
    }

    val appColors = LocalAppColors.current

    // Real-time ticking state updated every 100ms
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            currentTimeMillis = System.currentTimeMillis()
            delay(100)
        }
    }

    val cal = remember(currentTimeMillis) {
        Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    }

    val currentHour = cal.get(Calendar.HOUR_OF_DAY)
    val currentMinute = cal.get(Calendar.MINUTE)
    val currentSecond = cal.get(Calendar.SECOND)

    // Resolve target day: passed day, or selected day, or today's entity, or first in-progress day
    val effectiveDay = day
        ?: uiState.selectedRemainingTimeDay
        ?: uiState.todayEntity
        ?: uiState.days.firstOrNull { it.hasEnterTime && !it.hasExitTime }

    val enterHour = effectiveDay?.enterHour ?: currentHour
    val enterMinute = effectiveDay?.enterMinute ?: currentMinute

    // Check if effectiveDay is today
    val calType = remember(uiState.settings.calendarType) {
        CalendarHelper.parseCalendarType(uiState.settings.calendarType)
    }
    val todayDate = remember(currentTimeMillis) {
        CalendarHelper.now(calType)
    }
    val isToday = effectiveDay?.year == todayDate.year &&
            effectiveDay?.month == todayDate.month &&
            effectiveDay?.dayNumber == todayDate.day

    val dailyTargetMinutes = if (uiState.settings.dailyRequiredMinutes > 0) {
        uiState.settings.dailyRequiredMinutes
    } else {
        480
    }

    // Elapsed seconds calculation
    val startTotalSeconds = enterHour * 3600 + enterMinute * 60
    var nowTotalSeconds = currentHour * 3600 + currentMinute * 60 + currentSecond

    val elapsedSeconds = if (isToday && nowTotalSeconds < startTotalSeconds) {
        // If it's today and the current time is before the start time, work hasn't started yet.
        0
    } else {
        if (nowTotalSeconds < startTotalSeconds) {
            nowTotalSeconds += 24 * 3600 // Shift crossing midnight for non-today days (like yesterday's record)
        }
        (nowTotalSeconds - startTotalSeconds).coerceAtLeast(0)
    }

    val targetSeconds = dailyTargetMinutes * 60
    val remainingSeconds = targetSeconds - elapsedSeconds

    val isOvertime = remainingSeconds < 0
    val displaySeconds = if (isOvertime) -remainingSeconds else remainingSeconds

    val remHours = displaySeconds / 3600
    val remMinutes = (displaySeconds % 3600) / 60
    val remSeconds = displaySeconds % 60

    val elapsedHours = elapsedSeconds / 3600
    val elapsedMinutes = (elapsedSeconds % 3600) / 60

    val progressFraction = if (targetSeconds > 0) {
        (elapsedSeconds.toFloat() / targetSeconds.toFloat()).coerceIn(0f, 1f)
    } else {
        1f
    }

    // Estimated checkout time (checkIn + dailyRequiredMinutes)
    val estTotalMinutes = (enterHour * 60 + enterMinute) + dailyTargetMinutes
    val estHour = (estTotalMinutes / 60) % 24
    val estMinute = estTotalMinutes % 60

    val statusColor = if (isOvertime) OvertimeGreen else MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Remaining Time",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("remaining_time_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onExitNow,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("exit_now_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Exit Now",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    letterSpacing = 0.3.sp
                                )
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize().testTag("remaining_time_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. HERO Bento Card: Dial Clock with Digits
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("remaining_time_hero_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Circular Progress Canvas Clock with Solid Color
                    Box(
                        modifier = Modifier.size(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val trackColor = MaterialTheme.colorScheme.surfaceVariant
                        val arcColor = statusColor

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 14.dp.toPx()
                            val halfStroke = strokeWidth / 2f
                            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

                            // Background Track
                            drawArc(
                                color = trackColor,
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = Offset(halfStroke, halfStroke),
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )

                            // Active Progress Arc (Solid color)
                            val sweepAngle = progressFraction * 360f
                            drawArc(
                                color = arcColor,
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = Offset(halfStroke, halfStroke),
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        // Digital Clock Center Display: "hh : mm : ss"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isOvertime) "Overtime" else "Remaining",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Digits row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.testTag("remaining_time_digits")
                            ) {
                                if (isOvertime) {
                                    Text(
                                        text = "+",
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            color = statusColor,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        modifier = Modifier.padding(end = 2.dp)
                                    )
                                }

                                Text(
                                    text = String.format(Locale.getDefault(), "%02d", remHours),
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = (-0.5).sp
                                    )
                                )

                                Text(
                                    text = ":",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )

                                Text(
                                    text = String.format(Locale.getDefault(), "%02d", remMinutes),
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = (-0.5).sp
                                    )
                                )

                                Text(
                                    text = ":",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )

                                Text(
                                    text = String.format(Locale.getDefault(), "%02d", remSeconds),
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = (-0.5).sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Target: ${dailyTargetMinutes / 60}h ${dailyTargetMinutes % 60}m",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }

            // 2. Bento Pair: Check In Time Card & Est. Check Out Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left: Check In Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("check_in_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Login,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = "Check In",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = String.format(Locale.getDefault(), "%02d:%02d", enterHour, enterMinute),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("check_in_value")
                        )
                    }
                }

                // Right: Est. Check Out Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("est_checkout_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = "Est. Check Out",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = String.format(Locale.getDefault(), "%02d:%02d", estHour, estMinute),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("est_checkout_value")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
