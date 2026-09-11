package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.WorkDay
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.DeficitAmber
import com.example.ui.theme.DeficitAmberContainer
import com.example.ui.theme.DeficitRed
import com.example.ui.theme.OnDeficitAmberContainer
import com.example.ui.theme.OvertimeGreen
import com.example.ui.theme.OvertimeGreenContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Full Screen Bottom Sheet displaying live elapsed time and remaining/overtime with
 * a stunning interactive clock and circular animated progress visualization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWorkClockBottomSheet(
    day: WorkDay,
    dailyTargetMinutes: Int,
    onDismissRequest: () -> Unit,
    onLogExitClick: (() -> Unit)? = null,
    onPickExitTimeClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val strings = LocalAppStrings.current

    // Real-time ticking state updated every 50ms for smooth 60fps animations
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            currentTimeMillis = System.currentTimeMillis()
            delay(50L)
        }
    }

    val cal = remember(currentTimeMillis) {
        Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    }

    val currentHour = cal.get(Calendar.HOUR_OF_DAY)
    val currentMinute = cal.get(Calendar.MINUTE)
    val currentSecond = cal.get(Calendar.SECOND)
    val currentMillis = cal.get(Calendar.MILLISECOND)

    // Calculate elapsed time since check-in
    val enterHour = day.enterHour ?: currentHour
    val enterMinute = day.enterMinute ?: currentMinute

    val startTotalSeconds = enterHour * 3600 + enterMinute * 60
    var nowTotalSeconds = currentHour * 3600 + currentMinute * 60 + currentSecond
    if (nowTotalSeconds < startTotalSeconds) {
        nowTotalSeconds += 24 * 3600 // Handle shift crossing midnight
    }

    val elapsedTotalSeconds = (nowTotalSeconds - startTotalSeconds).coerceAtLeast(0)
    val elapsedHours = elapsedTotalSeconds / 3600
    val elapsedMinutes = (elapsedTotalSeconds % 3600) / 60
    val elapsedSeconds = elapsedTotalSeconds % 60

    val targetSeconds = (if (dailyTargetMinutes > 0) dailyTargetMinutes else 480) * 60
    val isTargetReached = elapsedTotalSeconds >= targetSeconds

    val remainingTotalSeconds = if (isTargetReached) {
        elapsedTotalSeconds - targetSeconds // Overtime seconds
    } else {
        targetSeconds - elapsedTotalSeconds
    }

    val remHours = remainingTotalSeconds / 3600
    val remMinutes = (remainingTotalSeconds % 3600) / 60
    val remSeconds = remainingTotalSeconds % 60

    val progressFraction = (elapsedTotalSeconds.toFloat() / targetSeconds.toFloat()).coerceIn(0f, 1f)

    // Projected checkout time
    val expectedExitTotalMinutes = (enterHour * 60 + enterMinute) + (if (dailyTargetMinutes > 0) dailyTargetMinutes else 480)
    val expectedExitHour = (expectedExitTotalMinutes / 60) % 24
    val expectedExitMinute = expectedExitTotalMinutes % 60

    // Pulsing glowing animation for live tracking
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val sweepAngleAnimated by animateFloatAsState(
        targetValue = progressFraction * 360f,
        animationSpec = tween(400, easing = LinearEasing),
        label = "sweep_progress"
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = modifier
            .fillMaxHeight(0.95f)
            .testTag("live_work_clock_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar: "LIVE SHIFT" badge, title, and close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = if (isTargetReached) OvertimeGreenContainer else DeficitAmberContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isTargetReached) OvertimeGreen else DeficitAmber
                                    )
                            )
                            Text(
                                text = if (isTargetReached) strings.overtime.uppercase() else strings.activeShift,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = if (isTargetReached) OvertimeGreen else OnDeficitAmberContainer
                            )
                        }
                    }

                    Text(
                        text = strings.liveClock,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(
                    onClick = onDismissRequest,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("close_live_clock_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.close,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Cool Animated Clock Canvas (Dial + Radial Progress + Clock Hands + Glow)
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                val accentProgressColor = if (isTargetReached) OvertimeGreen else primaryColor

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = (size.minDimension / 2f) - 16.dp.toPx()

                    // 1. Draw outer subtle track
                    drawCircle(
                        color = trackColor,
                        radius = radius,
                        center = center,
                        style = Stroke(width = 8.dp.toPx())
                    )

                    // 2. Draw 60 clock tick marks around the perimeter
                    for (i in 0 until 60) {
                        val angle = (i * 6f) * (Math.PI / 180f).toFloat()
                        val isHour = i % 5 == 0
                        val tickLength = if (isHour) 10.dp.toPx() else 5.dp.toPx()
                        val tickWidth = if (isHour) 2.5.dp.toPx() else 1.dp.toPx()
                        val tickColor = if (isHour) {
                            primaryColor.copy(alpha = 0.6f)
                        } else {
                            trackColor.copy(alpha = 0.4f)
                        }

                        val outerX = center.x + (radius - 10.dp.toPx()) * sin(angle)
                        val outerY = center.y - (radius - 10.dp.toPx()) * cos(angle)
                        val innerX = center.x + (radius - 10.dp.toPx() - tickLength) * sin(angle)
                        val innerY = center.y - (radius - 10.dp.toPx() - tickLength) * cos(angle)

                        drawLine(
                            color = tickColor,
                            start = Offset(innerX, innerY),
                            end = Offset(outerX, outerY),
                            strokeWidth = tickWidth,
                            cap = StrokeCap.Round
                        )
                    }

                    // 3. Draw active animated progress arc
                    if (sweepAngleAnimated > 0f) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.4f),
                                    accentProgressColor,
                                    accentProgressColor
                                ),
                                center = center
                            ),
                            startAngle = -90f,
                            sweepAngle = sweepAngleAnimated,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2f, radius * 2f),
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Glowing head marker on the arc
                        val headAngle = (-90f + sweepAngleAnimated) * (Math.PI / 180f).toFloat()
                        val headX = center.x + radius * cos(headAngle)
                        val headY = center.y + radius * sin(headAngle)

                        drawCircle(
                            color = accentProgressColor.copy(alpha = 0.35f * pulseGlow),
                            radius = 12.dp.toPx(),
                            center = Offset(headX, headY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = Offset(headX, headY)
                        )
                    }

                    // 4. Draw Analog Hands (Hour, Minute, Continuous Sweeping Second)
                    // Hour Hand
                    val hourAngle = ((currentHour % 12) + currentMinute / 60f) * 30f
                    rotate(hourAngle, pivot = center) {
                        drawLine(
                            color = primaryColor,
                            start = center,
                            end = Offset(center.x, center.y - radius * 0.42f),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Minute Hand
                    val minuteAngle = (currentMinute + currentSecond / 60f) * 6f
                    rotate(minuteAngle, pivot = center) {
                        drawLine(
                            color = primaryColor.copy(alpha = 0.85f),
                            start = center,
                            end = Offset(center.x, center.y - radius * 0.62f),
                            strokeWidth = 2.8.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Continuous Sweeping Second Hand with counter-balance
                    val continuousSeconds = currentSecond + (currentMillis / 1000f)
                    val secondAngle = continuousSeconds * 6f
                    rotate(secondAngle, pivot = center) {
                        // Main red/accent second pointer
                        drawLine(
                            color = if (isTargetReached) OvertimeGreen else DeficitAmber,
                            start = Offset(center.x, center.y + radius * 0.18f),
                            end = Offset(center.x, center.y - radius * 0.76f),
                            strokeWidth = 1.8.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Center jewel hub
                    drawCircle(
                        color = Color.White,
                        radius = 4.5.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = primaryColor,
                        radius = 2.5.dp.toPx(),
                        center = center
                    )
                }
            }

            // Central Digital Elapsed Time Display (Large, Monospace, Pulsing Colon)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.testTag("digital_elapsed_time_container")
            ) {
                Text(
                    text = strings.elapsedWorkTime,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val rawElapsed = String.format(
                        Locale.getDefault(),
                        "%02d:%02d:%02d",
                        elapsedHours,
                        elapsedMinutes,
                        elapsedSeconds
                    )
                    val formattedElapsed = strings.formatDigits(rawElapsed)

                    Text(
                        text = formattedElapsed,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 38.sp,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.testTag("elapsed_digital_text")
                    )
                }
            }

            // Remaining Time or Overtime Highlight Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isTargetReached) OvertimeGreenContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(
                    1.dp,
                    if (isTargetReached) OvertimeGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("remaining_time_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isTargetReached) OvertimeGreen.copy(alpha = 0.18f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isTargetReached) Icons.Default.CheckCircle else Icons.Default.HourglassBottom,
                                contentDescription = null,
                                tint = if (isTargetReached) OvertimeGreen else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isTargetReached) strings.overtimeAccumulated else strings.timeRemaining,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isTargetReached) {
                                    strings.targetReached
                                } else {
                                    val percentStr = strings.formatDigits(String.format(Locale.getDefault(), "%.1f", progressFraction * 100f))
                                    val targetH = strings.formatDigits((dailyTargetMinutes / 60).toString())
                                    val targetM = strings.formatDigits(String.format(Locale.getDefault(), "%02d", dailyTargetMinutes % 60))
                                    "$percentStr% ${strings.ofTarget} $targetH ${strings.hourShort} $targetM ${strings.minuteShort}"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    val rawRem = String.format(Locale.getDefault(), "%02d:%02d:%02d", remHours, remMinutes, remSeconds)
                    val remFormatted = if (isTargetReached) "+${strings.formatDigits(rawRem)}" else strings.formatDigits(rawRem)

                    Text(
                        text = remFormatted,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 17.sp
                        ),
                        color = if (isTargetReached) OvertimeGreen else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("remaining_digital_text")
                    )
                }
            }

            // Shift Context Metrics (Check-In Time, Target, Expected Exit)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Check In Metric Card
                MetricInfoCard(
                    icon = Icons.AutoMirrored.Filled.Login,
                    title = strings.checkIn,
                    value = day.formattedEnterTime(isFarsi = strings.isRtl),
                    modifier = Modifier.weight(1f)
                )

                // Daily Target Metric Card
                val tHours = strings.formatDigits((dailyTargetMinutes / 60).toString())
                val tMins = strings.formatDigits(if (dailyTargetMinutes % 60 > 0) "${dailyTargetMinutes % 60}" else "00")
                MetricInfoCard(
                    icon = Icons.Default.Flag,
                    title = strings.dailyTarget,
                    value = "$tHours ${strings.hourShort} $tMins ${strings.minuteShort}",
                    modifier = Modifier.weight(1f)
                )

                // Estimated Checkout Card
                MetricInfoCard(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = strings.estCheckOut,
                    value = strings.formatTime(expectedExitHour, expectedExitMinute),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons: "Exit Now" and "Pick Exit Time"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onLogExitClick != null) {
                    Button(
                        onClick = {
                            onLogExitClick()
                            onDismissRequest()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("live_clock_exit_now_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${strings.exitNow} (${strings.formatTime(currentHour, currentMinute)})",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                if (onPickExitTimeClick != null) {
                    OutlinedButton(
                        onClick = {
                            onPickExitTimeClick()
                            onDismissRequest()
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("live_clock_pick_exit_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.pickExitTime,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
