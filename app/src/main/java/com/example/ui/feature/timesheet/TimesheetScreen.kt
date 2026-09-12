package com.example.ui.feature.timesheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.unit.dp
import com.example.util.toPersianDigits
import androidx.compose.ui.unit.sp
import com.example.domain.model.DayStatus
import com.example.domain.model.DaySummary
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import com.example.domain.model.WorkCalculationSummary
import com.example.domain.model.WorkDay
import com.example.ui.WorkViewModel
import com.example.ui.mvi.WorkUiEffect
import com.example.ui.mvi.WorkUiIntent
import com.example.ui.mvi.WorkUiState
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalAppStrings
import com.example.ui.components.CustomAvatarDisplay
import com.example.ui.components.DayItemReportBottomSheet
import com.example.ui.components.LiveWorkClockBottomSheet
import com.example.ui.components.MonthYearPickerBottomSheet
import com.example.ui.components.TodayQuickActionBanner
import com.example.ui.theme.DeficitAmber
import com.example.ui.theme.DeficitAmberContainer
import com.example.ui.theme.DeficitRed
import com.example.ui.theme.DeficitRedContainer
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.OnDeficitAmberContainer
import com.example.ui.theme.OvertimeGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TimesheetScreen(
    uiState: WorkUiState,
    viewModel: WorkViewModel,
    onProfileClick: () -> Unit,
    onNavigateToRemainingTime: (WorkDay) -> Unit = { viewModel.navigateToRemainingTime(it) },
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Automatically respond to ScrollToTop side effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            if (effect is WorkUiEffect.ScrollToTop) {
                if (effect.animated) {
                    listState.animateScrollToItem(0)
                } else {
                    listState.scrollToItem(0)
                }
            }
        }
    }

    // Scroll to top when month changes
    LaunchedEffect(uiState.selectedYear, uiState.selectedMonth) {
        listState.animateScrollToItem(0)
    }

    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 1
        }
    }

    var showMonthPickerSheet by remember { mutableStateOf(false) }
    var reportDaySummary by remember { mutableStateOf<DaySummary?>(null) }

    if (showMonthPickerSheet) {
        MonthYearPickerBottomSheet(
            currentSelectedYear = uiState.effectiveSelectedYear,
            currentSelectedMonth = uiState.effectiveSelectedMonth,
            calendarType = uiState.calendarType,
            onDismissRequest = { showMonthPickerSheet = false },
            onMonthYearSelected = { year, month ->
                viewModel.setYearMonth(year, month)
            }
        )
    }

    reportDaySummary?.let { selectedDaySummary ->
        DayItemReportBottomSheet(
            daySummary = selectedDaySummary,
            formattedFullDate = uiState.formattedFullDate(selectedDaySummary.day.dayNumber),
            dayOfWeek = uiState.getDayOfWeekLabel(selectedDaySummary.day.dayNumber),
            onDismissRequest = { reportDaySummary = null },
            onEditEnterTime = {
                reportDaySummary = null
                viewModel.openTimePicker(selectedDaySummary.day, isEnter = true)
            },
            onEditExitTime = {
                reportDaySummary = null
                viewModel.openTimePicker(selectedDaySummary.day, isEnter = false)
            }
        )
    }

    val strings = LocalAppStrings.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 12.dp)
                    ) {
                        // Left: App Title (tapping scrolls to top / today)
                        Text(
                            text = strings.appName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                }
                        )

                        // Right: Profile avatar button navigating to Profile page
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onProfileClick() }
                                .padding(2.dp)
                                .testTag("profile_top_button")
                        ) {
                            CustomAvatarDisplay(
                                avatarId = uiState.avatarId,
                                sizeDp = 38.dp,
                                showBorder = true
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.testTag("app_top_bar")
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showScrollToTop,
                enter = fadeIn(tween(200)) + slideInVertically(tween(250)) { it / 2 },
                exit = fadeOut(tween(150)) + slideOutVertically(tween(200)) { it / 2 }
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.testTag("scroll_to_today_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = if (uiState.isCurrentMonth) strings.today else strings.backToToday
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 48.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("timesheet_screen")
        ) {
            // 0. Quick Today Action Banner (Pop-up at top if today's Enter or Exit is missing)
            if (uiState.quickTodayPrompt != null) {
                item(key = "today_quick_action_prompt") {
                    TodayQuickActionBanner(
                        prompt = uiState.quickTodayPrompt,
                        onEnterNow = { viewModel.logTodayEnterNow() },
                        onExitNow = { viewModel.logTodayExitNow() },
                        onPickTime = { isEnter -> viewModel.openTodayTimePicker(isEnter) },
                        onDismiss = { viewModel.dismissTodayPrompt() }
                    )
                }
            }

            // 1. Top Summary Card matching Wireframe: Month selector & Period overview
            item(key = "hero_summary_card") {
                WireframeHeroSummaryCard(
                    uiState = uiState,
                    onPrevMonth = { viewModel.prevMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onMonthClick = { showMonthPickerSheet = true }
                )
            }

            // 2. Today Section (Shown when viewing the current month)
            val currentTodaySummary = uiState.todaySummary
            if (uiState.isCurrentMonth && currentTodaySummary != null) {
                item(key = "today_section_item") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)
                    ) {
                        TodaySectionHeader(
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )

                        WireframeDailyLogRowCard(
                            daySummary = currentTodaySummary,
                            formattedMonthDay = uiState.formattedMonthDay(currentTodaySummary.day.dayNumber),
                            dayOfWeek = uiState.getDayOfWeekLabel(currentTodaySummary.day.dayNumber),
                            isToday = true,
                            onEnterClick = {
                                viewModel.openTimePicker(currentTodaySummary.day, isEnter = true)
                            },
                            onExitClick = {
                                viewModel.openTimePicker(currentTodaySummary.day, isEnter = false)
                            },
                            onToggleDayOff = {
                                viewModel.toggleDayOff(currentTodaySummary.day.dayNumber)
                            },
                            onClearDay = {
                                viewModel.clearDay(currentTodaySummary.day.dayNumber)
                            },
                            onOpenRemainingTime = {
                                onNavigateToRemainingTime(it)
                            },
                            onOpenItemReport = {
                                reportDaySummary = it
                            },
                            modifier = Modifier.testTag("today_card")
                        )
                    }
                }
            }

            // 3. Pinned "Back to Today" Button above Daily Log (Shown when viewing other months)
            if (!uiState.isCurrentMonth) {
                stickyHeader(key = "pinned_back_to_today_header") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 4.dp)
                    ) {
                        ReturnToCurrentMonthBanner(
                            title = uiState.backToTodayLabel,
                            onReturnClick = {
                                viewModel.goToCurrentMonth()
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 4. Section Heading: "Daily Log"
            item(key = "daily_log_section_header") {
                DailyLogSectionHeader(
                    completedCount = uiState.summary.completedDaysCount,
                    totalCount = uiState.totalDaysInCurrentMonth,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            // 5. Daily Log List (Day 1 through Day 29/30/31)
            items(
                items = uiState.summary.daySummaries,
                key = { it.day.dayNumber }
            ) { daySummary ->
                val isCurrentDay = uiState.isCurrentMonth && daySummary.day.dayNumber == uiState.currentDayOfMonth
                WireframeDailyLogRowCard(
                    daySummary = daySummary,
                    formattedMonthDay = uiState.formattedMonthDay(daySummary.day.dayNumber),
                    dayOfWeek = uiState.getDayOfWeekLabel(daySummary.day.dayNumber),
                    isToday = isCurrentDay,
                    onEnterClick = {
                        viewModel.openTimePicker(daySummary.day, isEnter = true)
                    },
                    onExitClick = {
                        viewModel.openTimePicker(daySummary.day, isEnter = false)
                    },
                    onToggleDayOff = {
                        viewModel.toggleDayOff(daySummary.day.dayNumber)
                    },
                    onClearDay = {
                        viewModel.clearDay(daySummary.day.dayNumber)
                    },
                    onOpenRemainingTime = {
                        onNavigateToRemainingTime(it)
                    },
                    onOpenItemReport = {
                        reportDaySummary = it
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
fun FarsiAnnotatedDuration(
    text: String,
    isFarsi: Boolean,
    baseFontSize: TextUnit,
    color: Color = Color.White,
    modifier: Modifier = Modifier
) {
    if (isFarsi) {
        val annotated = remember(text, baseFontSize) {
            buildAnnotatedString {
                val parts = text.split(" ")
                parts.forEachIndexed { index, part ->
                    if (part == "ساعت" || part == "دقیقه" || part == "و") {
                        withStyle(style = SpanStyle(fontSize = (baseFontSize.value * 0.55).sp, fontWeight = FontWeight.Normal)) {
                            append(part)
                        }
                    } else {
                        withStyle(style = SpanStyle(fontSize = baseFontSize, fontWeight = FontWeight.ExtraBold)) {
                            append(part)
                        }
                    }
                    if (index < parts.lastIndex) {
                        append(" ")
                    }
                }
            }
        }
        Text(
            text = annotated,
            style = LocalTextStyle.current.copy(
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.sp
            ),
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    } else {
        Text(
            text = text,
            style = LocalTextStyle.current.copy(
                fontSize = baseFontSize,
                fontWeight = FontWeight.ExtraBold
            ),
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }
}

/**
 * Top Summary Card matching the wireframe:
 * ┌─────────────────────────────────────────┐
 * │ yy/MM                                   │
 * │                                         │
 * │ Total hour                              │
 * │                                         │
 * │ overtime                    defect time │
 * └─────────────────────────────────────────┘
 */
@Composable
fun WireframeHeroSummaryCard(
    uiState: WorkUiState,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val summary = uiState.summary
    val strings = LocalAppStrings.current

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_summary_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top Row: Month selector & Days Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Month Switcher with fixed width constraints and equal height
                Surface(
                    color = Color(0x30FFFFFF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = onPrevMonth,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("prev_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                                contentDescription = strings.back,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onMonthClick() }
                                .padding(horizontal = 2.dp)
                                .testTag("yy_mm_label")
                        ) {
                            Text(
                                text = "${uiState.monthName} ${uiState.effectiveSelectedYear}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.3.sp
                                ),
                                color = Color.White,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = onNextMonth,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("next_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                contentDescription = strings.done,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Subtitle badge matching the height of month switcher
                Surface(
                    color = Color(0x25FFFFFF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(
                            text = "${uiState.totalDaysInCurrentMonth} ${strings.totalDaysSuffix}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Middle: Total Hours (matches wireframe)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = strings.totalHours,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                FarsiAnnotatedDuration(
                    text = summary.formattedTotalWorked(isFarsi = uiState.isFarsi),
                    isFarsi = uiState.isFarsi,
                    baseFontSize = if (uiState.isFarsi) 26.sp else 38.sp,
                    color = Color.White,
                    modifier = Modifier.testTag("total_hour_value")
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x35FFFFFF))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Row: Overtime (Start) and Deficit Time (End)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Overtime
                Column {
                    Text(
                        text = strings.overtime,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = Color.White.copy(alpha = 0.92f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    FarsiAnnotatedDuration(
                        text = summary.formattedOvertime(isFarsi = uiState.isFarsi),
                        isFarsi = uiState.isFarsi,
                        baseFontSize = if (uiState.isFarsi) 15.sp else 22.sp,
                        color = Color.White,
                        modifier = Modifier.testTag("overtime_value")
                    )
                    Text(
                        text = strings.formatDaysCount(summary.overtimeDaysCount),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                // Deficit Time
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = strings.deficitTime,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = Color.White.copy(alpha = 0.92f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    FarsiAnnotatedDuration(
                        text = summary.formattedDeficit(isFarsi = uiState.isFarsi),
                        isFarsi = uiState.isFarsi,
                        baseFontSize = if (uiState.isFarsi) 15.sp else 22.sp,
                        color = Color.White,
                        modifier = Modifier.testTag("defect_time_value")
                    )
                    Text(
                        text = strings.formatDaysCount(summary.deficitDaysCount),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

/**
 * Section Header matching wireframe "Today"
 */
@Composable
fun TodaySectionHeader(
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = strings.today,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.testTag("today_section_header")
        )
    }
}

/**
 * Text button component shown when viewing other months.
 */
@Composable
fun ReturnToCurrentMonthBanner(
    title: String = "",
    onReturnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val effectiveTitle = title.ifBlank { strings.backToToday }
    Surface(
        onClick = onReturnClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        modifier = modifier.testTag("return_to_current_month_button")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Today,
                contentDescription = effectiveTitle,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = effectiveTitle,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.2.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Section Header matching wireframe "Daily Log"
 */
@Composable
fun DailyLogSectionHeader(
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = strings.dailyLog,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.testTag("daily_log_header")
            )
            Text(
                text = strings.tapEnterExitHint,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = strings.formatDaysLogged(completedCount, totalCount),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

/**
 * Clean Daily Log Card precisely matching the wireframe sketch.
 */
@Composable
fun WireframeDailyLogRowCard(
    daySummary: DaySummary,
    formattedMonthDay: String,
    dayOfWeek: String = "",
    isToday: Boolean = false,
    onEnterClick: () -> Unit,
    onExitClick: () -> Unit,
    onToggleDayOff: () -> Unit,
    onClearDay: () -> Unit,
    onOpenRemainingTime: (WorkDay) -> Unit = {},
    onOpenItemReport: (DaySummary) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val day = daySummary.day
    val isDayOff = day.isDayOff
    val isComplete = day.isComplete
    val isInProgress = !isDayOff && day.hasEnterTime && !day.hasExitTime
    val strings = LocalAppStrings.current
    val isFarsi = strings.isRtl
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDayOff) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isToday) 1.5.dp else 1.dp,
            color = if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .testTag(if (isToday) "today_card_${day.dayNumber}" else "day_row_${day.dayNumber}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Top Row: [DayOfWeek / MM/dd] | [Status/Hours] | [3-Dots Menu]
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column: Day of week + MM/dd date
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = if (dayOfWeek.isNotBlank()) dayOfWeek else "${strings.day} ${if (isFarsi) day.dayNumber.toPersianDigits() else day.dayNumber.toString()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = formattedMonthDay,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Middle/Right: Hours & Delta
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    if (isDayOff) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = strings.dayOff,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else if (isComplete) {
                        val workedText = daySummary.formattedWorkedDuration(isFarsi = isFarsi)
                        val diffMins = daySummary.diffMinutes
                        val diffColor = when {
                            diffMins > 0 -> OvertimeGreen
                            diffMins < 0 -> DeficitRed
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        val diffSign = if (diffMins > 0) "+" else if (diffMins < 0) "-" else ""
                        val diffFormatted = "$diffSign${WorkCalculationSummary.formatMinutes(if (diffMins < 0) -diffMins else diffMins, isFarsi = isFarsi)}"

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = workedText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = diffFormatted,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = diffColor
                            )
                        }
                    } else if (day.hasEnterTime || day.hasExitTime) {
                        Surface(
                            color = DeficitAmberContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = if (isInProgress) {
                                Modifier.clickable { onOpenRemainingTime(day) }
                            } else Modifier
                        ) {
                            Text(
                                text = strings.inProgress,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                ),
                                color = OnDeficitAmberContainer,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Far End: 3-Dots Menu Button (⋮)
                Box(
                    contentAlignment = Alignment.CenterEnd
                ) {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("day_menu_btn_${day.dayNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Dropdown Popup with "Remaining Time" / "Daily Summary", "Set Off", and "Clear"
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        if (!isDayOff) {
                            if (isToday && !day.hasExitTime) {
                                // For today without exit time: show Remaining Time
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = strings.remainingTime,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onOpenRemainingTime(day)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Timelapse,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    modifier = Modifier.testTag("day_menu_remaining_time_${day.dayNumber}")
                                )
                            } else {
                                // When exit time is set (or for any past/completed day): show Daily Summary opening the bottom sheet
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = strings.dailySummary,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onOpenItemReport(daySummary)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Insights,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    modifier = Modifier.testTag("day_menu_item_report_${day.dayNumber}")
                                )
                            }
                        }

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (isDayOff) strings.cancelDayOff else strings.setOff,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onToggleDayOff()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isDayOff) Icons.Default.EventAvailable else Icons.Default.EventBusy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.testTag("day_menu_set_off_${day.dayNumber}")
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = strings.clear,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    color = DeficitRed
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onClearDay()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = DeficitRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.testTag("day_menu_clear_${day.dayNumber}")
                        )
                    }
                }
            }

            // Soft animated visibility for Enter/Exit Time Boxes
            AnimatedVisibility(
                visible = !isDayOff,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(animationSpec = tween(220)),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeOut(animationSpec = tween(180))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Middle Row: [ Enter Time Box ]  →  [ Exit Time Box ]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Enter Time Box
                        WireframeTimeBox(
                            label = strings.enterTime,
                            timeText = if (day.hasEnterTime) day.formattedEnterTime(isFarsi = isFarsi) else "_ _ : _ _",
                            isSet = day.hasEnterTime,
                            enabled = true,
                            onClick = onEnterClick,
                            modifier = Modifier.weight(1f),
                            testTag = "enter_btn_${day.dayNumber}"
                        )

                        // Arrow Flow "→"
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "to",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(18.dp)
                        )

                        // Exit Time Box
                        WireframeTimeBox(
                            label = strings.exitTime,
                            timeText = if (day.hasExitTime) day.formattedExitTime(isFarsi = isFarsi) else "_ _ : _ _",
                            isSet = day.hasExitTime,
                            enabled = true,
                            onClick = onExitClick,
                            modifier = Modifier.weight(1f),
                            testTag = "exit_btn_${day.dayNumber}"
                        )
                    }
                }
            }
        }
    }
}

/**
 * Time Box matching wireframe box with neutral, clean styling.
 */
@Composable
fun WireframeTimeBox(
    label: String,
    timeText: String,
    isSet: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = when {
            !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            isSet -> MaterialTheme.colorScheme.surface
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (!enabled) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                ),
                color = if (!enabled) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = timeText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = when {
                    !enabled -> MaterialTheme.colorScheme.outlineVariant
                    isSet -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                }
            )
        }
    }
}
