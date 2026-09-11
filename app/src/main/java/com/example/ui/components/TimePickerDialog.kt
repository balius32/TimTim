package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.DeficitRed
import com.example.ui.theme.DeficitRedContainer
import com.example.ui.theme.OnDeficitRedContainer
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs

private const val REPEAT_COUNT = 500
private val ITEM_HEIGHT = 74.dp
private val VISIBLE_ITEMS_COUNT = 3
private const val TOTAL_HOURS = 24
private const val TOTAL_MINUTES = 60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    isEnterTime: Boolean,
    dayNumber: Int,
    existingEnterHour: Int? = null,
    existingEnterMinute: Int? = null,
    existingExitHour: Int? = null,
    existingExitMinute: Int? = null,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onShowSnackbar: ((String) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()
    val strings = LocalAppStrings.current

    var selectedHour by remember { mutableIntStateOf(initialHour.coerceIn(0, 23)) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute.coerceIn(0, 59)) }

    val hourStartIndex = remember {
        (REPEAT_COUNT / 2) * TOTAL_HOURS + initialHour.coerceIn(0, 23)
    }
    val minuteStartIndex = remember {
        (REPEAT_COUNT / 2) * TOTAL_MINUTES + initialMinute.coerceIn(0, 59)
    }

    val hourListState = rememberLazyListState(initialFirstVisibleItemIndex = hourStartIndex)
    val minuteListState = rememberLazyListState(initialFirstVisibleItemIndex = minuteStartIndex)

    val hourSnapFlingBehavior = rememberSnapFlingBehavior(lazyListState = hourListState)
    val minuteSnapFlingBehavior = rememberSnapFlingBehavior(lazyListState = minuteListState)

    // Validation calculation: Exit time cannot be before Enter time
    val existingEnterMinutes = if (existingEnterHour != null && existingEnterMinute != null) {
        existingEnterHour * 60 + existingEnterMinute
    } else null

    val existingExitMinutes = if (existingExitHour != null && existingExitMinute != null) {
        existingExitHour * 60 + existingExitMinute
    } else null

    val currentSelectedMinutes = selectedHour * 60 + selectedMinute

    val isExitBeforeEnter = !isEnterTime && existingEnterMinutes != null && currentSelectedMinutes < existingEnterMinutes
    val isEnterAfterExit = isEnterTime && existingExitMinutes != null && currentSelectedMinutes > existingExitMinutes
    val hasValidationError = isExitBeforeEnter || isEnterAfterExit

    val validationErrorMessage = when {
        isExitBeforeEnter -> "${strings.exitTimeCannotBeEarlier} (${strings.formatTime(existingEnterHour ?: 0, existingEnterMinute ?: 0)})"
        isEnterAfterExit -> "${strings.enterTimeCannotBeLater} (${strings.formatTime(existingExitHour ?: 0, existingExitMinute ?: 0)})"
        else -> null
    }

    // Track center selected hours with soft responsiveness
    LaunchedEffect(hourListState) {
        snapshotFlow {
            val layoutInfo = hourListState.layoutInfo
            val center = layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs((item.offset + item.size / 2) - center)
            }?.index
        }
            .filter { it != null }
            .distinctUntilChanged()
            .collect { index ->
                if (index != null) {
                    selectedHour = index % TOTAL_HOURS
                }
            }
    }

    // Track center selected minutes with soft responsiveness
    LaunchedEffect(minuteListState) {
        snapshotFlow {
            val layoutInfo = minuteListState.layoutInfo
            val center = layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs((item.offset + item.size / 2) - center)
            }?.index
        }
            .filter { it != null }
            .distinctUntilChanged()
            .collect { index ->
                if (index != null) {
                    selectedMinute = index % TOTAL_MINUTES
                }
            }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 8.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        },
        modifier = Modifier.testTag("time_picker_dialog")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Context Sub-label
            Text(
                text = "${if (isEnterTime) strings.enterTime else strings.exitTime} • $dayNumber",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Scrollable Number Picker with Soft Continuous Animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                contentAlignment = Alignment.Center
            ) {
                // Center row highlight capsule
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(ITEM_HEIGHT)
                ) {}

                // Dual Wheels + Colon Layout matching sketch
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour Wheel Column (00..23)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                        contentAlignment = Alignment.Center
                    ) {
                        SoftWheelColumn(
                            listState = hourListState,
                            snapFlingBehavior = hourSnapFlingBehavior,
                            totalItems = TOTAL_HOURS,
                            itemHeight = ITEM_HEIGHT,
                            onItemClick = { index ->
                                coroutineScope.launch {
                                    hourListState.animateScrollToItem(index)
                                }
                            },
                            testTag = "hour_wheel"
                        )
                    }

                    // Central Colon ":"
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 44.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(32.dp)
                            .padding(bottom = 6.dp)
                    )

                    // Minute Wheel Column (00..59)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                        contentAlignment = Alignment.Center
                    ) {
                        SoftWheelColumn(
                            listState = minuteListState,
                            snapFlingBehavior = minuteSnapFlingBehavior,
                            totalItems = TOTAL_MINUTES,
                            itemHeight = ITEM_HEIGHT,
                            onItemClick = { index ->
                                coroutineScope.launch {
                                    minuteListState.animateScrollToItem(index)
                                }
                            },
                            testTag = "minute_wheel"
                        )
                    }
                }
            }

            // Inline Validation Warning Banner
            AnimatedVisibility(
                visible = hasValidationError,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = DeficitRedContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = DeficitRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = validationErrorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            color = OnDeficitRedContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Actions Row: "Now", "Cancel", "Save"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        val cal = Calendar.getInstance()
                        val currentH = cal.get(Calendar.HOUR_OF_DAY)
                        val currentM = cal.get(Calendar.MINUTE)

                        val currentHourBase = (hourListState.firstVisibleItemIndex / TOTAL_HOURS) * TOTAL_HOURS
                        val targetHourIndex = currentHourBase + currentH

                        val currentMinuteBase = (minuteListState.firstVisibleItemIndex / TOTAL_MINUTES) * TOTAL_MINUTES
                        val targetMinuteIndex = currentMinuteBase + currentM

                        coroutineScope.launch {
                            launch { hourListState.animateScrollToItem(targetHourIndex) }
                            launch { minuteListState.animateScrollToItem(targetMinuteIndex) }
                        }
                    },
                    modifier = Modifier.testTag("now_time_button")
                ) {
                    Text(
                        text = strings.now,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            letterSpacing = 0.3.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_time_button")
                    ) {
                        Text(
                            text = strings.cancel,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            if (hasValidationError) {
                                validationErrorMessage?.let { onShowSnackbar?.invoke(it) }
                            } else {
                                onConfirm(selectedHour, selectedMinute)
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasValidationError) DeficitRed.copy(alpha = 0.7f) else MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 26.dp, vertical = 12.dp),
                        modifier = Modifier.testTag("confirm_time_button")
                    ) {
                        Text(
                            text = strings.save,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Time Picker Bottom Sheet for setting Daily Target hours and minutes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    title: String? = null,
    subtitle: String? = null,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val strings = LocalAppStrings.current
    val effectiveTitle = title ?: strings.dailyTarget
    val effectiveSubtitle = subtitle ?: strings.dailyTargetDesc
    val coroutineScope = rememberCoroutineScope()

    var selectedHour by remember { mutableIntStateOf(initialHour.coerceIn(0, 23)) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute.coerceIn(0, 59)) }

    val hourStartIndex = remember {
        (REPEAT_COUNT / 2) * TOTAL_HOURS + initialHour.coerceIn(0, 23)
    }
    val minuteStartIndex = remember {
        (REPEAT_COUNT / 2) * TOTAL_MINUTES + initialMinute.coerceIn(0, 59)
    }

    val hourListState = rememberLazyListState(initialFirstVisibleItemIndex = hourStartIndex)
    val minuteListState = rememberLazyListState(initialFirstVisibleItemIndex = minuteStartIndex)

    val hourSnapFlingBehavior = rememberSnapFlingBehavior(lazyListState = hourListState)
    val minuteSnapFlingBehavior = rememberSnapFlingBehavior(lazyListState = minuteListState)

    LaunchedEffect(hourListState) {
        snapshotFlow {
            val layoutInfo = hourListState.layoutInfo
            val center = layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs((item.offset + item.size / 2) - center)
            }?.index
        }
            .filter { it != null }
            .distinctUntilChanged()
            .collect { index ->
                if (index != null) {
                    selectedHour = index % TOTAL_HOURS
                }
            }
    }

    LaunchedEffect(minuteListState) {
        snapshotFlow {
            val layoutInfo = minuteListState.layoutInfo
            val center = layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs((item.offset + item.size / 2) - center)
            }?.index
        }
            .filter { it != null }
            .distinctUntilChanged()
            .collect { index ->
                if (index != null) {
                    selectedMinute = index % TOTAL_MINUTES
                }
            }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 8.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        },
        modifier = Modifier.testTag("target_time_picker_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = effectiveTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = effectiveSubtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Dual Scrollable Wheel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(ITEM_HEIGHT)
                ) {}

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                        contentAlignment = Alignment.Center
                    ) {
                        SoftWheelColumn(
                            listState = hourListState,
                            snapFlingBehavior = hourSnapFlingBehavior,
                            totalItems = TOTAL_HOURS,
                            itemHeight = ITEM_HEIGHT,
                            onItemClick = { index ->
                                coroutineScope.launch {
                                    hourListState.animateScrollToItem(index)
                                }
                            },
                            testTag = "target_hour_wheel"
                        )
                    }

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Light,
                            fontSize = 38.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                        contentAlignment = Alignment.Center
                    ) {
                        SoftWheelColumn(
                            listState = minuteListState,
                            snapFlingBehavior = minuteSnapFlingBehavior,
                            totalItems = TOTAL_MINUTES,
                            itemHeight = ITEM_HEIGHT,
                            onItemClick = { index ->
                                coroutineScope.launch {
                                    minuteListState.animateScrollToItem(index)
                                }
                            },
                            testTag = "target_minute_wheel"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("cancel_target_button")
                ) {
                    Text(
                        text = strings.cancel,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        onConfirm(selectedHour, selectedMinute)
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
                    modifier = Modifier.testTag("confirm_target_button")
                ) {
                    Text(
                        text = strings.save,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        ),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

/**
 * Bottom Sheet with number picker for Work Limits (Min Enter / Max Exit Limit)
 * Features dual-wheel number picker, an "Off" button to disable the limit, "Cancel" and "Save".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitTimePickerDialog(
    title: String? = null,
    subtitle: String? = null,
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onTurnOff: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val strings = LocalAppStrings.current
    val effectiveTitle = title ?: strings.workLimits
    val effectiveSubtitle = subtitle ?: ""
    val coroutineScope = rememberCoroutineScope()

    var selectedHour by remember { mutableIntStateOf(initialHour.coerceIn(0, 23)) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute.coerceIn(0, 59)) }

    val hourStartIndex = remember {
        (REPEAT_COUNT / 2) * TOTAL_HOURS + initialHour.coerceIn(0, 23)
    }
    val minuteStartIndex = remember {
        (REPEAT_COUNT / 2) * TOTAL_MINUTES + initialMinute.coerceIn(0, 59)
    }

    val hourListState = rememberLazyListState(initialFirstVisibleItemIndex = hourStartIndex)
    val minuteListState = rememberLazyListState(initialFirstVisibleItemIndex = minuteStartIndex)

    val hourSnapFlingBehavior = rememberSnapFlingBehavior(lazyListState = hourListState)
    val minuteSnapFlingBehavior = rememberSnapFlingBehavior(lazyListState = minuteListState)

    LaunchedEffect(hourListState) {
        snapshotFlow {
            val layoutInfo = hourListState.layoutInfo
            val center = layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs((item.offset + item.size / 2) - center)
            }?.index
        }
            .filter { it != null }
            .distinctUntilChanged()
            .collect { index ->
                if (index != null) {
                    selectedHour = index % TOTAL_HOURS
                }
            }
    }

    LaunchedEffect(minuteListState) {
        snapshotFlow {
            val layoutInfo = minuteListState.layoutInfo
            val center = layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs((item.offset + item.size / 2) - center)
            }?.index
        }
            .filter { it != null }
            .distinctUntilChanged()
            .collect { index ->
                if (index != null) {
                    selectedMinute = index % TOTAL_MINUTES
                }
            }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 8.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        },
        modifier = Modifier.testTag("limit_time_picker_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = effectiveTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (effectiveSubtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = effectiveSubtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dual Scrollable Wheel (Number Picker)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(ITEM_HEIGHT)
                ) {}

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                        contentAlignment = Alignment.Center
                    ) {
                        SoftWheelColumn(
                            listState = hourListState,
                            snapFlingBehavior = hourSnapFlingBehavior,
                            totalItems = TOTAL_HOURS,
                            itemHeight = ITEM_HEIGHT,
                            onItemClick = { index ->
                                coroutineScope.launch {
                                    hourListState.animateScrollToItem(index)
                                }
                            },
                            testTag = "limit_hour_wheel"
                        )
                    }

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Light,
                            fontSize = 38.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ITEM_HEIGHT * VISIBLE_ITEMS_COUNT),
                        contentAlignment = Alignment.Center
                    ) {
                        SoftWheelColumn(
                            listState = minuteListState,
                            snapFlingBehavior = minuteSnapFlingBehavior,
                            totalItems = TOTAL_MINUTES,
                            itemHeight = ITEM_HEIGHT,
                            onItemClick = { index ->
                                coroutineScope.launch {
                                    minuteListState.animateScrollToItem(index)
                                }
                            },
                            testTag = "limit_minute_wheel"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons: "Off" on Left, "Cancel" and "Save" on Right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Off Button: Turns off the limit (no background color, red text color, no icon)
                TextButton(
                    onClick = onTurnOff,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    modifier = Modifier.testTag("limit_off_button")
                ) {
                    Text(
                        text = strings.off,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_limit_button")
                    ) {
                        Text(
                            text = strings.cancel,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            onConfirm(selectedHour, selectedMinute)
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
                        modifier = Modifier.testTag("confirm_limit_button")
                    ) {
                        Text(
                            text = strings.save,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            ),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable wheel column with continuous, silky smooth distance-based interpolation.
 */
@Composable
fun SoftWheelColumn(
    listState: LazyListState,
    snapFlingBehavior: androidx.compose.foundation.gestures.FlingBehavior,
    totalItems: Int,
    itemHeight: Dp,
    onItemClick: (Int) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    LazyColumn(
        state = listState,
        flingBehavior = snapFlingBehavior,
        contentPadding = PaddingValues(vertical = itemHeight),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight * VISIBLE_ITEMS_COUNT)
            .testTag(testTag)
    ) {
        items(
            count = totalItems * REPEAT_COUNT,
            key = { it }
        ) { index ->
            val value = index % totalItems
            val valueString = strings.formatDigits(String.format(java.util.Locale.getDefault(), "%02d", value))

            val distanceFraction by remember(listState) {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val center = layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2f
                    val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                    if (itemInfo != null) {
                        val itemCenter = itemInfo.offset + itemInfo.size / 2f
                        val dist = abs(itemCenter - center)
                        val maxDist = itemInfo.size.toFloat().coerceAtLeast(1f)
                        (dist / maxDist).coerceIn(0f, 1f)
                    } else {
                        1f
                    }
                }
            }

            val scale = 1.0f - (distanceFraction * 0.22f)
            val alpha = 1.0f - (distanceFraction * 0.68f)
            val isPrimary = distanceFraction < 0.3f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .clickable { onItemClick(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = valueString,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = if (isPrimary) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 48.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-1).sp
                    ),
                    color = if (isPrimary) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .scale(scale)
                        .alpha(alpha)
                )
            }
        }
    }
}
