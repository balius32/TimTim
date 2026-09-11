package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.example.domain.model.DayStatus
import com.example.domain.model.DaySummary
import com.example.domain.model.WorkCalculationSummary
import com.example.ui.components.MonthYearPickerBottomSheet
import com.example.ui.localization.LocalAppStrings
import com.example.ui.WorkViewModel
import com.example.ui.mvi.WorkUiIntent
import com.example.ui.mvi.WorkUiState
import com.example.ui.theme.DeficitAmber
import com.example.ui.theme.DeficitRed
import com.example.ui.theme.OvertimeGreen
import com.example.util.ReportExportHelper

/**
 * Dedicated Full Screen for Monthly Report.
 * Displays:
 * 1. Monthly Work Hours & Summary stats
 * 2. Monthly Work Distribution (Visual Bar Chart)
 * 3. Attendance Metrics
 * 4. Share & Export functionality (Text, PDF, Excel/CSV)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    uiState: WorkUiState,
    viewModel: WorkViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val summary = uiState.reportSummary
    val settings = uiState.settings
    var showExportSheet by remember { mutableStateOf(false) }
    var showMonthPickerSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showMonthPickerSheet) {
        val effectiveReportYear = if (uiState.reportYear != 0) uiState.reportYear else uiState.currentCalendarNow.year
        val effectiveReportMonth = if (uiState.reportMonth != 0) uiState.reportMonth else uiState.currentCalendarNow.month

        MonthYearPickerBottomSheet(
            currentSelectedYear = effectiveReportYear,
            currentSelectedMonth = effectiveReportMonth,
            calendarType = uiState.calendarType,
            onDismissRequest = { showMonthPickerSheet = false },
            onMonthYearSelected = { year, month ->
                viewModel.setReportYearMonth(year, month)
            }
        )
    }

    // Intercept hardware back button
    BackHandler {
        onBackClick()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.monthlyReport,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("report_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.back,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Info with interactive month switcher
            ReportHeaderSection(
                monthLabel = uiState.formattedReportMonthHeader,
                onPrevMonth = { viewModel.prevReportMonth() },
                onNextMonth = { viewModel.nextReportMonth() },
                onMonthClick = { showMonthPickerSheet = true }
            )

            // 1. Summary Metrics Cards (Worked, Target, Overtime, Deficit)
            ReportKeyMetricsGrid(summary = summary)

            // 2. Monthly Work Distribution (Visual Bar Chart)
            MonthlyWorkDistributionSection(
                daySummaries = summary.daySummaries,
                targetMinutes = summary.dailyTargetMinutes
            )

            // 3. Attendance & Progress Metrics
            AttendanceMetricsSection(
                summary = summary,
                totalDaysInMonth = uiState.totalDaysInReportMonth
            )

            // 4. Share / Export Action Button
            Button(
                onClick = { showExportSheet = true },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("export_report_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.shareExportReport,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal Bottom Sheet for Export Format Selection
    if (showExportSheet) {
        ModalBottomSheet(
            onDismissRequest = { showExportSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 36.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = strings.exportReport,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${strings.selectFormatFor} ${uiState.formattedReportMonthHeader}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { showExportSheet = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.close,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Format Options
                ExportFormatOptionCard(
                    title = strings.pdfDocument,
                    subtitle = strings.pdfDocumentDesc,
                    icon = Icons.Default.PictureAsPdf,
                    iconBgColor = Color(0xFFEF4444).copy(alpha = 0.15f),
                    iconTintColor = Color(0xFFDC2626),
                    testTag = "export_option_pdf",
                    onClick = {
                        showExportSheet = false
                        ReportExportHelper.sharePdfReport(context, summary, uiState)
                    }
                )

                ExportFormatOptionCard(
                    title = strings.excelCsvSpreadsheet,
                    subtitle = strings.excelCsvDesc,
                    icon = Icons.Default.TableChart,
                    iconBgColor = Color(0xFF10B981).copy(alpha = 0.15f),
                    iconTintColor = Color(0xFF059669),
                    testTag = "export_option_excel",
                    onClick = {
                        showExportSheet = false
                        ReportExportHelper.shareCsvExcel(context, summary, uiState)
                    }
                )

                ExportFormatOptionCard(
                    title = strings.textSummary,
                    subtitle = strings.textSummaryDesc,
                    icon = Icons.Default.Description,
                    iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                    iconTintColor = MaterialTheme.colorScheme.primary,
                    testTag = "export_option_text",
                    onClick = {
                        showExportSheet = false
                        ReportExportHelper.shareTextSummary(context, summary, uiState)
                    }
                )
            }
        }
    }
}

/**
 * Option Card for Export Formats in BottomSheet
 */
@Composable
private fun ExportFormatOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Report Header showing month banner with previous/next month navigation.
 */
@Composable
private fun ReportHeaderSection(
    monthLabel: String,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthClick: () -> Unit = {}
) {
    val strings = LocalAppStrings.current
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Month navigation row with clean centered alignment and balanced touch targets
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prev Month Button
            IconButton(
                onClick = onPrevMonth,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("report_prev_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                    contentDescription = strings.previousMonth,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Month Title centered cleanly, clickable to open Month & Year Picker
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onMonthClick() }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = monthLabel,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 0.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("report_month_title")
                )
            }

            // Next Month Button
            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("report_next_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                    contentDescription = strings.nextMonth,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * 1. Summary Metrics 2x2 Grid.
 */
@Composable
private fun ReportKeyMetricsGrid(summary: WorkCalculationSummary) {
    val strings = LocalAppStrings.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = strings.workOverview,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Worked
            MetricCard(
                title = strings.totalWorked,
                value = summary.formattedTotalWorked(),
                accentColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.weight(1f)
            )

            // Target
            MetricCard(
                title = strings.requiredTarget,
                value = summary.formattedRequiredTotal(),
                accentColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Overtime
            MetricCard(
                title = strings.totalOvertime,
                value = summary.formattedOvertime(),
                accentColor = OvertimeGreen,
                backgroundColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.weight(1f)
            )

            // Deficit
            MetricCard(
                title = strings.totalDeficit,
                value = summary.formattedDeficit(),
                accentColor = DeficitRed,
                backgroundColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    accentColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                ),
                color = accentColor
            )
        }
    }
}

/**
 * 2. Monthly Work Distribution (Visual Bar Chart).
 */
@Composable
private fun MonthlyWorkDistributionSection(
    daySummaries: List<DaySummary>,
    targetMinutes: Int
) {
    val strings = LocalAppStrings.current
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = strings.monthlyWorkDistribution,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DistributionLegendItem(color = MaterialTheme.colorScheme.primary, label = strings.completed)
                DistributionLegendItem(color = OvertimeGreen, label = strings.overtime)
                DistributionLegendItem(color = DeficitAmber, label = strings.underTarget)
                DistributionLegendItem(color = MaterialTheme.colorScheme.outlineVariant, label = strings.dayOff)
            }

            // Horizontal Scrollable Bar Chart
            val scrollState = rememberScrollState()
            val maxMinutes = 12 * 60 // 12 hours max scale

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                daySummaries.forEach { daySummary ->
                    val workedMinutes = daySummary.workedMinutes
                    val status = daySummary.status

                    val barColor = when (status) {
                        DayStatus.OVERTIME -> OvertimeGreen
                        DayStatus.DEFICIT -> DeficitAmber
                        DayStatus.EXACT -> MaterialTheme.colorScheme.primary
                        DayStatus.DAY_OFF -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        DayStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        DayStatus.UNSET -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    }

                    val barHeightFraction = (workedMinutes.toFloat() / maxMinutes).coerceIn(0.06f, 1f)
                    val barHeightDp = (140 * barHeightFraction).dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.width(22.dp)
                    ) {
                        if (workedMinutes > 0) {
                            Text(
                                text = "${workedMinutes / 60}h",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        }

                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height(barHeightDp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(barColor)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${daySummary.day.dayNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DistributionLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 3. Attendance & Progress Metrics.
 */
@Composable
private fun AttendanceMetricsSection(
    summary: WorkCalculationSummary,
    totalDaysInMonth: Int
) {
    val strings = LocalAppStrings.current
    val completedDays = summary.completedDaysCount
    val offDays = summary.offDaysCount
    val workingDays = summary.workingDaysCount
    val remainingDays = (workingDays - completedDays).coerceAtLeast(0)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = strings.attendanceProgress,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Stat counters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AttendanceBadge(
                    icon = Icons.Default.CheckCircle,
                    label = strings.worked,
                    count = "$completedDays",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                AttendanceBadge(
                    icon = Icons.Default.EventBusy,
                    label = strings.offDaysCount,
                    count = "$offDays",
                    tint = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )

                AttendanceBadge(
                    icon = Icons.Default.PendingActions,
                    label = strings.remainingDays,
                    count = "$remainingDays",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AttendanceBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
