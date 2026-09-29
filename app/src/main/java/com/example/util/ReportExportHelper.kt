package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.R
import com.example.domain.model.DayStatus
import com.example.domain.model.WorkCalculationSummary
import com.example.ui.localization.AppStrings
import com.example.ui.mvi.WorkUiState
import java.io.File
import java.io.FileOutputStream

typealias UiState = WorkUiState

object ReportExportHelper {

    /**
     * Share plain text summary
     */
    fun shareTextSummary(context: Context, summary: WorkCalculationSummary, uiState: UiState) {
        try {
            val workedStr = AppStrings.Units.formatDuration(context, summary.totalWorkedMinutes)
            val targetTotalStr = AppStrings.Units.formatDuration(context, summary.requiredTotalMinutes)
            val dailyTargetStr = AppStrings.Units.formatDuration(context, summary.dailyTargetMinutes)
            val overtimeStr = AppStrings.Units.formatSignedDuration(context, summary.overtimeMinutes)
            val deficitStr = AppStrings.Units.formatSignedDuration(context, -summary.deficitMinutes)
            val netBalanceStr = AppStrings.Units.formatSignedDuration(context, summary.netBalanceMinutes)
            val targetDetails = context.getString(
                R.string.export_target_required_details,
                targetTotalStr,
                dailyTargetStr,
                summary.workingDaysCount
            )

            val reportText = buildString {
                appendLine("📊 ${context.getString(R.string.export_report_title)} - ${uiState.formattedReportMonthHeader}")
                appendLine(context.getString(R.string.export_user_label, uiState.userName))
                appendLine("────────────────────────────────────────")
                appendLine("⏱ ${context.getString(R.string.export_total_worked)}:     $workedStr")
                appendLine("🎯 ${context.getString(R.string.export_target_required)}: $targetDetails")
                appendLine("🟢 ${context.getString(R.string.export_overtime)}:         $overtimeStr")
                appendLine("🔴 ${context.getString(R.string.export_deficit)}:          $deficitStr")
                appendLine("⚖️ ${context.getString(R.string.export_net_balance)}:      $netBalanceStr")
                appendLine("✅ ${context.getString(R.string.export_completed_days)}:   ${summary.completedDaysCount}/${uiState.totalDaysInReportMonth}")
                appendLine("🌴 ${context.getString(R.string.export_off_days)}:         ${summary.offDaysCount}")
                appendLine("💼 ${context.getString(R.string.export_working_days)}:     ${summary.workingDaysCount}")
                appendLine("────────────────────────────────────────")
                appendLine(context.getString(R.string.export_generated_by))
            }

            // Copy to clipboard as convenient backup
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                val clip = ClipData.newPlainText("Timesheet Report", reportText)
                clipboard?.setPrimaryClip(clip)
            } catch (_: Exception) {}

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "${context.getString(R.string.export_report_title)} - ${uiState.formattedReportMonthHeader}")
                putExtra(Intent.EXTRA_TEXT, reportText)
            }
            val chooser = Intent.createChooser(intent, context.getString(R.string.export_share_text_chooser)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, e.localizedMessage ?: "Error sharing text", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Share CSV / Excel compatible spreadsheet with daily breakdown and totals
     */
    fun shareCsvExcel(context: Context, summary: WorkCalculationSummary, uiState: UiState) {
        try {
            val effectiveYear = if (uiState.reportYear != 0) uiState.reportYear else uiState.currentCalendarNow.year
            val effectiveMonth = if (uiState.reportMonth != 0) uiState.reportMonth else uiState.currentCalendarNow.month

            val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val fileName = "Timesheet_${effectiveYear}_${effectiveMonth}.csv"
            val file = File(reportsDir, fileName)
            if (file.exists()) {
                file.delete()
            }

            val totalWorked = AppStrings.Units.formatDuration(context, summary.totalWorkedMinutes)
            val reqTotal = AppStrings.Units.formatDuration(context, summary.requiredTotalMinutes)
            val overtime = AppStrings.Units.formatSignedDuration(context, summary.overtimeMinutes)
            val deficit = AppStrings.Units.formatSignedDuration(context, -summary.deficitMinutes)
            val netBalance = AppStrings.Units.formatSignedDuration(context, summary.netBalanceMinutes)

            FileOutputStream(file).use { out ->
                // UTF-8 BOM for Microsoft Excel compatibility with special characters/Persian
                out.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

                val writer = out.bufferedWriter(Charsets.UTF_8)
                writer.write("${context.getString(R.string.export_report_title)},${uiState.formattedReportMonthHeader}\n")
                writer.write("${context.getString(R.string.profile_name_label)},${uiState.userName}\n")
                writer.write("${context.getString(R.string.export_total_worked)},$totalWorked\n")
                writer.write("${context.getString(R.string.export_target_required)},$reqTotal\n")
                writer.write("${context.getString(R.string.export_overtime)},$overtime\n")
                writer.write("${context.getString(R.string.export_deficit)},$deficit\n")
                writer.write("${context.getString(R.string.export_net_balance)},$netBalance\n")
                writer.write("${context.getString(R.string.export_completed_days)},${summary.completedDaysCount}\n")
                writer.write("${context.getString(R.string.export_off_days)},${summary.offDaysCount}\n\n")

                // Table Header
                writer.write(
                    listOf(
                        context.getString(R.string.export_col_day),
                        context.getString(R.string.input_date_label),
                        context.getString(R.string.export_col_enter),
                        context.getString(R.string.export_col_exit),
                        context.getString(R.string.export_col_worked),
                        context.getString(R.string.export_col_target),
                        context.getString(R.string.export_col_diff),
                        context.getString(R.string.export_col_status),
                        context.getString(R.string.export_col_notes)
                    ).joinToString(",") + "\n"
                )

                summary.daySummaries.forEach { item ->
                    val dayNum = item.day.dayNumber
                    val dateFormatted = "$effectiveYear/${effectiveMonth.toString().padStart(2, '0')}/${dayNum.toString().padStart(2, '0')}"
                    val enter = item.day.formattedEnterTime()
                    val exit = item.day.formattedExitTime()
                    val worked = AppStrings.Units.formatDuration(context, item.workedMinutes)
                    val target = AppStrings.Units.formatDuration(context, item.targetMinutes)
                    val diffWithSign = AppStrings.Units.formatSignedDuration(context, item.diffMinutes)
                    val status = AppStrings.Status.localizedName(context, item.status)
                    val notes = item.day.note.replace(",", " ")

                    writer.write("$dayNum,$dateFormatted,$enter,$exit,$worked,$target,$diffWithSign,$status,\"$notes\"\n")
                }

                writer.flush()
            }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "${context.getString(R.string.export_report_title)} CSV/Excel - ${uiState.formattedReportMonthHeader}")
                putExtra(Intent.EXTRA_STREAM, uri)
                clipData = ClipData.newRawUri("Timesheet CSV Report", uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, context.getString(R.string.export_share_csv_chooser)).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.export_csv_error, e.localizedMessage ?: ""), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Generate high-quality native PDF Report and share
     */
    fun sharePdfReport(context: Context, summary: WorkCalculationSummary, uiState: UiState) {
        var pdfDoc: PdfDocument? = null
        try {
            val effectiveYear = if (uiState.reportYear != 0) uiState.reportYear else uiState.currentCalendarNow.year
            val effectiveMonth = if (uiState.reportMonth != 0) uiState.reportMonth else uiState.currentCalendarNow.month

            pdfDoc = PdfDocument()
            val pageWidth = 595 // A4 standard width in points at 72dpi
            val pageHeight = 842 // A4 standard height
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply { isAntiAlias = true }

            // Header Background
            paint.color = Color.parseColor("#1565C0") // Primary Blue
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 95f, paint)

            // Header Text
            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 18f
            canvas.drawText(context.getString(R.string.export_report_title), 28f, 40f, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 12f
            canvas.drawText("${context.getString(R.string.nav_report)}: ${uiState.formattedReportMonthHeader}    |    ${context.getString(R.string.export_user_label, uiState.userName)}", 28f, 68f, paint)

            // Summary Stats Cards Banner
            var yPos = 118f
            paint.color = Color.parseColor("#F1F5F9")
            canvas.drawRoundRect(24f, yPos, (pageWidth - 24).toFloat(), yPos + 60f, 8f, 8f, paint)

            val colW = (pageWidth - 48f) / 4f
            val cardTitles = listOf(
                context.getString(R.string.export_total_worked),
                context.getString(R.string.export_target_required),
                context.getString(R.string.export_overtime),
                context.getString(R.string.export_deficit)
            )
            val cardValues = listOf(
                AppStrings.Units.formatDuration(context, summary.totalWorkedMinutes),
                AppStrings.Units.formatDuration(context, summary.requiredTotalMinutes),
                AppStrings.Units.formatSignedDuration(context, summary.overtimeMinutes),
                AppStrings.Units.formatSignedDuration(context, -summary.deficitMinutes)
            )
            val cardColors = listOf(
                Color.parseColor("#1565C0"),
                Color.parseColor("#475569"),
                Color.parseColor("#16A34A"),
                Color.parseColor("#DC2626")
            )

            for (i in 0..3) {
                val cx = 24f + (i * colW) + 12f
                paint.color = Color.parseColor("#64748B")
                paint.textSize = 9.5f
                paint.typeface = Typeface.DEFAULT
                canvas.drawText(cardTitles[i], cx, yPos + 22f, paint)

                paint.color = cardColors[i]
                paint.textSize = 13f
                paint.typeface = Typeface.DEFAULT_BOLD
                canvas.drawText(cardValues[i], cx, yPos + 46f, paint)
            }

            yPos += 78f

            // Table Header
            paint.color = Color.parseColor("#2563EB")
            canvas.drawRect(24f, yPos, (pageWidth - 24).toFloat(), yPos + 24f, paint)

            paint.color = Color.WHITE
            paint.textSize = 9.5f
            paint.typeface = Typeface.DEFAULT_BOLD

            val headers = listOf(
                context.getString(R.string.export_col_day),
                context.getString(R.string.export_col_enter),
                context.getString(R.string.export_col_exit),
                context.getString(R.string.export_col_worked),
                context.getString(R.string.export_col_target),
                context.getString(R.string.export_col_diff),
                context.getString(R.string.export_col_status)
            )
            val colPositions = floatArrayOf(30f, 75f, 135f, 195f, 265f, 335f, 410f)

            headers.forEachIndexed { i, title ->
                canvas.drawText(title, colPositions[i], yPos + 16f, paint)
            }

            yPos += 24f

            // Table Rows
            paint.textSize = 8.5f
            val rowHeight = 17.5f

            summary.daySummaries.forEachIndexed { index, item ->
                if (yPos + rowHeight > pageHeight - 40f) return@forEachIndexed

                // Zebra striping
                paint.color = if (index % 2 == 0) Color.parseColor("#F8FAFC") else Color.WHITE
                canvas.drawRect(24f, yPos, (pageWidth - 24).toFloat(), yPos + rowHeight, paint)

                // Row text
                paint.typeface = Typeface.DEFAULT
                paint.color = Color.parseColor("#1E293B")

                val diffFormatted = AppStrings.Units.formatSignedDuration(context, item.diffMinutes)
                val statusText = AppStrings.Status.localizedName(context, item.status)

                val statusColor = when (item.status) {
                    DayStatus.DAY_OFF -> Color.parseColor("#2563EB")
                    DayStatus.OVERTIME -> Color.parseColor("#16A34A")
                    DayStatus.DEFICIT -> Color.parseColor("#DC2626")
                    DayStatus.EXACT -> Color.parseColor("#475569")
                    DayStatus.IN_PROGRESS -> Color.parseColor("#D97706")
                    DayStatus.UNSET -> Color.parseColor("#94A3B8")
                }

                val workedText = if (item.workedMinutes > 0) AppStrings.Units.formatDuration(context, item.workedMinutes) else "-"
                val targetText = if (item.targetMinutes > 0) AppStrings.Units.formatDuration(context, item.targetMinutes) else "-"

                canvas.drawText("${item.day.dayNumber}", colPositions[0], yPos + 12f, paint)
                canvas.drawText(item.day.formattedEnterTime(), colPositions[1], yPos + 12f, paint)
                canvas.drawText(item.day.formattedExitTime(), colPositions[2], yPos + 12f, paint)
                canvas.drawText(workedText, colPositions[3], yPos + 12f, paint)
                canvas.drawText(targetText, colPositions[4], yPos + 12f, paint)
                canvas.drawText(diffFormatted, colPositions[5], yPos + 12f, paint)

                paint.color = statusColor
                paint.typeface = Typeface.DEFAULT_BOLD
                canvas.drawText(statusText, colPositions[6], yPos + 12f, paint)

                yPos += rowHeight
            }

            // Footer
            paint.color = Color.parseColor("#94A3B8")
            paint.textSize = 8.5f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("${context.getString(R.string.export_generated_by)}  •  ${uiState.formattedReportMonthHeader}", 28f, (pageHeight - 20).toFloat(), paint)

            pdfDoc.finishPage(page)

            val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val pdfFile = File(reportsDir, "Timesheet_${effectiveYear}_${effectiveMonth}.pdf")
            if (pdfFile.exists()) {
                pdfFile.delete()
            }
            FileOutputStream(pdfFile).use { out ->
                pdfDoc.writeTo(out)
            }
            pdfDoc.close()
            pdfDoc = null

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_SUBJECT, "${context.getString(R.string.export_report_title)} PDF - ${uiState.formattedReportMonthHeader}")
                putExtra(Intent.EXTRA_STREAM, uri)
                clipData = ClipData.newRawUri("Timesheet PDF Report", uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, context.getString(R.string.export_share_pdf_chooser)).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.export_pdf_error, e.localizedMessage ?: ""), Toast.LENGTH_SHORT).show()
        } finally {
            try {
                pdfDoc?.close()
            } catch (_: Exception) {}
        }
    }
}
