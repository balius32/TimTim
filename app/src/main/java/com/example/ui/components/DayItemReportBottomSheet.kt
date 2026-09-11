package com.example.ui.components

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DaySummary
import com.example.domain.model.WorkCalculationSummary
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.DeficitRed
import com.example.ui.theme.OvertimeGreen

/**
 * Clean Modal Bottom Sheet for Day Breakdown / Daily Summary:
 * 1. Title formatted with space and muted/smaller text for day of week: "1405/5/16 (Seshanbe)"
 * 2. Total worked item placed above the 4 items
 * 3. 4 items in a 2x2 grid (thinner cards, perfectly centered horizontally & vertically):
 *    - Enter Time (Login icon) & Exit Time (Logout icon)
 *    - Est. Checkout & Overtime / Deficit (colored time text: OvertimeGreen / DeficitRed)
 * 4. Close button at bottom
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayItemReportBottomSheet(
    daySummary: DaySummary,
    formattedFullDate: String,
    dayOfWeek: String,
    onDismissRequest: () -> Unit,
    onEditEnterTime: (() -> Unit)? = null,
    onEditExitTime: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val strings = LocalAppStrings.current
    val isFarsi = strings.isRtl
    val day = daySummary.day

    val isComplete = day.isComplete
    val isDayOff = day.isDayOff
    val diffMins = daySummary.diffMinutes

    // Determine Overtime/Deficit item title, value, and specific time text color
    val overtimeDeficitTitle = when {
        !isComplete -> "${strings.overtime} / ${strings.deficit}"
        diffMins > 0 -> strings.overtime
        diffMins < 0 -> strings.deficit
        else -> strings.onTarget
    }

    val overtimeDeficitValue = when {
        !isComplete -> "_ _ : _ _"
        diffMins > 0 -> "+${WorkCalculationSummary.formatMinutes(diffMins, isFarsi = isFarsi)}"
        diffMins < 0 -> "-${WorkCalculationSummary.formatMinutes(-diffMins, isFarsi = isFarsi)}"
        else -> if (isFarsi) "۰h ۰۰m" else "0h 00m"
    }

    val overtimeDeficitColor = when {
        !isComplete -> MaterialTheme.colorScheme.onSurface
        diffMins > 0 -> OvertimeGreen
        diffMins < 0 -> DeficitRed
        else -> MaterialTheme.colorScheme.onSurface
    }

    val overtimeDeficitIcon: ImageVector = when {
        diffMins >= 0 -> Icons.AutoMirrored.Filled.TrendingUp
        else -> Icons.AutoMirrored.Filled.TrendingDown
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = modifier.testTag("day_item_report_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 4.dp, bottom = 28.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Date with space and smaller/opacity (DayOfWeek) e.g. "1405/5/16 (Seshanbe)"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                val annotatedTitle = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        append(formattedFullDate)
                    }
                    if (dayOfWeek.isNotBlank()) {
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                            )
                        ) {
                            append("($dayOfWeek)")
                        }
                    }
                }

                Text(
                    text = annotatedTitle,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            // Status Banner if Day Off
            if (isDayOff) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = strings.dayOff,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.noWorkRequired,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Total Worked Row: MOVED ABOVE the 4 items
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("report_total_worked_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = strings.totalWorked,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = daySummary.formattedWorkedDuration(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // 4 items with compact thinner height and perfectly centered horizontal & vertical text:
                // Row 1: Enter Time & Exit Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CompactDetailTile(
                        icon = Icons.AutoMirrored.Filled.Login,
                        title = strings.enterTime,
                        value = if (day.hasEnterTime) day.formattedEnterTime() else "_ _ : _ _",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("report_enter_tile")
                    )

                    CompactDetailTile(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = strings.exitTime,
                        value = if (day.hasExitTime) day.formattedExitTime() else "_ _ : _ _",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("report_exit_tile")
                    )
                }

                // Row 2: Est. Checkout & Overtime/Deficit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CompactDetailTile(
                        icon = Icons.Default.MoreTime,
                        title = strings.estCheckOut,
                        value = if (day.hasEnterTime) daySummary.formattedEstimatedCheckout() else "_ _ : _ _",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("report_est_checkout_tile")
                    )

                    CompactDetailTile(
                        icon = overtimeDeficitIcon,
                        title = overtimeDeficitTitle,
                        value = overtimeDeficitValue,
                        valueColor = overtimeDeficitColor,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("report_overtime_deficit_tile")
                    )
                }
            }

            // Close button at the bottom
            OutlinedButton(
                onClick = onDismissRequest,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("dismiss_item_report_sheet_btn")
            ) {
                Text(
                    text = strings.close,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/**
 * Compact item card with text and icons strictly centered both horizontally and vertically.
 */
@Composable
private fun CompactDetailTile(
    icon: ImageVector,
    title: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        modifier = modifier.height(76.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = valueColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
