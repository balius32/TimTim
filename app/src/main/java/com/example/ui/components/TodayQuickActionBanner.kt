package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.LocalAppStrings
import com.example.ui.mvi.TodayPromptType
import com.example.ui.mvi.TodayQuickPrompt
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clean and modern top attendance banner prompting the user to quickly Check In or Check Out today.
 * Supports swipe to dismiss as well as direct tap dismiss.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayQuickActionBanner(
    prompt: TodayQuickPrompt?,
    onEnterNow: () -> Unit,
    onExitNow: () -> Unit,
    onPickTime: (isEnter: Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    // Current time formatted
    var currentTimeString by remember {
        mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
    }

    LaunchedEffect(prompt) {
        while (true) {
            currentTimeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            delay(15_000)
        }
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.StartToEnd || dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(prompt) {
        if (prompt != null) {
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    AnimatedVisibility(
        visible = prompt != null,
        enter = expandVertically(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                fadeIn(animationSpec = tween(250, easing = FastOutSlowInEasing)),
        exit = shrinkVertically(animationSpec = tween(250, easing = FastOutSlowInEasing)) +
                fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)),
        modifier = modifier
    ) {
        if (prompt != null) {
            val isEnter = prompt.type == TodayPromptType.ENTER_NOW

            val currentMins = try {
                val parts = currentTimeString.split(":")
                val h = parts[0].toInt()
                val m = parts[1].toInt()
                h * 60 + m
            } catch (e: Exception) {
                0
            }

            val enterHour = prompt.todayEntity.enterHour
            val enterMinute = prompt.todayEntity.enterMinute
            val enterMins = if (enterHour != null && enterMinute != null) {
                enterHour * 60 + enterMinute
            } else {
                null
            }

            val isExitTimeInvalid = !isEnter && enterMins != null && currentMins < enterMins

            val primaryColor = MaterialTheme.colorScheme.primary
            val cardBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(18.dp))
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("today_quick_action_banner")
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Top Info Row: Title & Subtitle + Close Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Header Title & Context Description
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(1.dp)
                            ) {
                                Text(
                                    text = if (isEnter) strings.logEntryNow else strings.logExitNow,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isEnter) {
                                        strings.quickActionEntryPrompt
                                    } else if (isExitTimeInvalid) {
                                        strings.exitTimeCannotBeEarlier
                                    } else {
                                        "${strings.quickActionCheckedIn} ${strings.formatDigits(prompt.enterTimeFormatted)}"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = if (isExitTimeInvalid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Close / Dismiss button (still available for tapping)
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .testTag("today_banner_dismiss_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = strings.close,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Action Buttons Row (Balanced & Solid)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Quick 1-Tap Log Button
                            Button(
                                onClick = {
                                    if (isEnter) onEnterNow() else onExitNow()
                                },
                                enabled = if (isEnter) true else !isExitTimeInvalid,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryColor,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag(if (isEnter) "btn_enter_now" else "btn_exit_now")
                            ) {
                                Icon(
                                    imageVector = if (isEnter) Icons.AutoMirrored.Filled.Login else Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEnter) "${strings.logEntryNow} (${strings.formatDigits(currentTimeString)})" else "${strings.logExitNow} (${strings.formatDigits(currentTimeString)})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Pick Custom Time Button
                            OutlinedButton(
                                onClick = { onPickTime(isEnter) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .weight(0.75f)
                                    .height(40.dp)
                                    .testTag("btn_pick_custom_time")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.tapToLog,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
