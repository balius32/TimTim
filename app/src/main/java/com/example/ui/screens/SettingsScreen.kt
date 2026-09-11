package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WorkViewModel
import com.example.ui.mvi.AppScreen
import com.example.ui.mvi.WorkUiIntent
import com.example.ui.mvi.WorkUiState
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalAppStrings
import com.example.ui.localization.AppStrings
import com.example.ui.components.LimitTimePickerDialog
import com.example.ui.components.TargetTimePickerDialog
import com.example.ui.theme.ACCENT_COLOR_OPTIONS
import com.example.ui.theme.DeficitRed
import com.example.ui.theme.OvertimeGreen
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.buildThemeModeString
import com.example.ui.theme.parseThemeSettings
import com.example.util.BackupData
import com.example.util.CalendarHelper
import com.example.util.CalendarType
import com.example.util.DataBackupHelper
import kotlinx.coroutines.launch

/**
 * Settings Screen:
 * 1. Language Card
 * 2. Theme Card
 * 3. Calendar Type Card
 * 4. Daily Target Card
 * 5. Off Days Card
 * 6. Daily Limits Card (Min / Max)
 * 7. Import / Export Card
 * 8. Reset / Clear All Data Item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: WorkUiState,
    viewModel: WorkViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appLanguage = LocalAppLanguage.current
    val settings = uiState.settings
    val calendarType = uiState.calendarType
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showLanguageBottomSheet by remember { mutableStateOf(false) }
    var showTargetTimePicker by remember { mutableStateOf(false) }
    var showMinEnterLimitPicker by remember { mutableStateOf(false) }
    var showMaxExitLimitPicker by remember { mutableStateOf(false) }
    var showThemeBottomSheet by remember { mutableStateOf(false) }
    var showCalendarTypeBottomSheet by remember { mutableStateOf(false) }
    var showOffDaysBottomSheet by remember { mutableStateOf(false) }
    var showClearAllConfirmDialog by remember { mutableStateOf(false) }
    var showImportExportBottomSheet by remember { mutableStateOf(false) }
    var pendingExportJson by remember { mutableStateOf<String?>(null) }
    var pendingImportData by remember { mutableStateOf<Pair<BackupData, String>?>(null) }
    var showImportConfirmDialog by remember { mutableStateOf(false) }

    // Launcher for Save Document (Export to file)
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null && pendingExportJson != null) {
            try {
                DataBackupHelper.writeTextToUri(context, uri, pendingExportJson!!)
                Toast.makeText(context, strings.success, Toast.LENGTH_SHORT).show()
                pendingExportJson = null
            } catch (e: Exception) {
                Toast.makeText(context, "${strings.error}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Launcher for Open Document (Import file)
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val jsonString = DataBackupHelper.readTextFromUri(context, uri)
                val backupData = DataBackupHelper.parseBackupJson(jsonString)
                pendingImportData = Pair(backupData, jsonString)
                showImportConfirmDialog = true
            } catch (e: Exception) {
                Toast.makeText(context, "${strings.error}: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
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
                        text = strings.settingsTitle,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("settings_back_button")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Language Card
            WireframeLanguageCard(
                languageCode = settings.language,
                onClick = { showLanguageBottomSheet = true }
            )

            // 2. Theme Card
            WireframeThemeCard(
                themeSettingString = settings.themeMode,
                onClick = { showThemeBottomSheet = true }
            )

            // 3. Calendar Type Card
            WireframeCalendarTypeCard(
                calendarType = calendarType,
                onClick = { showCalendarTypeBottomSheet = true }
            )

            // 4. Daily Target Card
            WireframeDailyTargetCard(
                dailyMinutes = settings.dailyRequiredMinutes,
                onClick = { showTargetTimePicker = true }
            )

            // 5. Off Days Card
            WireframeOffDaysCard(
                offDaysString = settings.offDaysOfWeek,
                calendarType = calendarType,
                onClick = { showOffDaysBottomSheet = true }
            )

            // 6. Daily Limits Card (Min Enter / Max Exit Limit)
            WireframeDailyLimitsCard(
                minMinutes = settings.minEnterMinutes,
                maxMinutes = settings.maxExitMinutes,
                onMinEnterClick = { showMinEnterLimitPicker = true },
                onMaxExitClick = { showMaxExitLimitPicker = true }
            )

            // 7. Import and Export Card
            WireframeImportExportCard(
                onClick = { showImportExportBottomSheet = true }
            )

            // 8. Reset All Recorded Data Card
            WireframeResetAllDataCard(
                onClick = { showClearAllConfirmDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Language Selection Modal BottomSheet
    if (showLanguageBottomSheet) {
        LanguageSelectionBottomSheet(
            currentLanguageCode = settings.language,
            onLanguageSelected = { newLang ->
                viewModel.updateLanguage(newLang.code)
                showLanguageBottomSheet = false
            },
            onDismiss = { showLanguageBottomSheet = false }
        )
    }

    // 1. Daily Target Time Picker Sheet
    if (showTargetTimePicker) {
        val currentH = settings.dailyRequiredMinutes / 60
        val currentM = settings.dailyRequiredMinutes % 60
        TargetTimePickerDialog(
            initialHour = currentH,
            initialMinute = currentM,
            onConfirm = { hour, minute ->
                viewModel.updateDailyRequiredTime(hour, minute)
                showTargetTimePicker = false
            },
            onDismiss = {
                showTargetTimePicker = false
            }
        )
    }

    // 2. Min Enter Limit Picker Sheet (Work Limits)
    if (showMinEnterLimitPicker) {
        val currentMin = settings.minEnterMinutes
        val initialH = if (currentMin != null && currentMin > 0) currentMin / 60 else 7
        val initialM = if (currentMin != null && currentMin > 0) currentMin % 60 else 0
        LimitTimePickerDialog(
            title = "Work Limits",
            subtitle = "Scroll to set Minimum Enter time limit",
            initialHour = initialH,
            initialMinute = initialM,
            onConfirm = { hour, minute ->
                val totalMinutes = hour * 60 + minute
                viewModel.updateMinEnterTime(totalMinutes)
                showMinEnterLimitPicker = false
            },
            onTurnOff = {
                viewModel.updateMinEnterTime(null)
                showMinEnterLimitPicker = false
            },
            onDismiss = {
                showMinEnterLimitPicker = false
            }
        )
    }

    // 3. Max Exit Limit Picker Sheet (Work Limits)
    if (showMaxExitLimitPicker) {
        val currentMax = settings.maxExitMinutes
        val initialH = if (currentMax != null && currentMax > 0) currentMax / 60 else 19
        val initialM = if (currentMax != null && currentMax > 0) currentMax % 60 else 0
        LimitTimePickerDialog(
            title = "Work Limits",
            subtitle = "Scroll to set Maximum Exit time limit",
            initialHour = initialH,
            initialMinute = initialM,
            onConfirm = { hour, minute ->
                val totalMinutes = hour * 60 + minute
                viewModel.updateMaxExitTime(totalMinutes)
                showMaxExitLimitPicker = false
            },
            onTurnOff = {
                viewModel.updateMaxExitTime(null)
                showMaxExitLimitPicker = false
            },
            onDismiss = {
                showMaxExitLimitPicker = false
            }
        )
    }

    // 3. Theme Selection Modal BottomSheet (3 items in a row, primary color picker, Cancel & Save buttons)
    if (showThemeBottomSheet) {
        ThemeSelectionBottomSheet(
            currentTheme = settings.themeMode,
            onThemeSaved = { newTheme ->
                viewModel.updateThemeMode(newTheme)
                showThemeBottomSheet = false
            },
            onDismiss = { showThemeBottomSheet = false }
        )
    }

    // 4. Calendar Type Selection Modal BottomSheet
    if (showCalendarTypeBottomSheet) {
        CalendarTypeBottomSheet(
            currentCalendarType = calendarType,
            onCalendarTypeSelected = { newCalType ->
                viewModel.updateCalendarType(newCalType.name)
                showCalendarTypeBottomSheet = false
            },
            onDismiss = { showCalendarTypeBottomSheet = false }
        )
    }

    // 5. Off Days Modal BottomSheet (With fixed comfortable spacing between title and items)
    if (showOffDaysBottomSheet) {
        OffDaysBottomSheet(
            offDaysString = settings.offDaysOfWeek,
            calendarType = calendarType,
            onOffDaysChanged = { newOffDays ->
                viewModel.updateOffDaysOfWeek(newOffDays)
            },
            onDismiss = { showOffDaysBottomSheet = false }
        )
    }

    // 4. Clear All Confirmation Dialog
    if (showClearAllConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirmDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.errorContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(26.dp)
                    )
                }
            },
            title = {
                Text(
                    text = strings.resetAllConfirmTitle,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = strings.resetAllConfirmMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearAllConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeficitRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.clearAllDataAction, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirmDialog = false }) {
                    Text(strings.cancel, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // 5. Import & Export BottomSheet
    if (showImportExportBottomSheet) {
        ImportExportBottomSheet(
            onExportShare = {
                coroutineScope.launch {
                    try {
                        val json = viewModel.getExportJson()
                        DataBackupHelper.shareBackupFile(context, json)
                        showImportExportBottomSheet = false
                    } catch (e: Exception) {
                        Toast.makeText(context, "Export error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onExportSaveFile = {
                coroutineScope.launch {
                    try {
                        val json = viewModel.getExportJson()
                        pendingExportJson = json
                        val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(java.util.Date())
                        createDocumentLauncher.launch("timesheet_backup_$timestamp.json")
                        showImportExportBottomSheet = false
                    } catch (e: Exception) {
                        Toast.makeText(context, "Export error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onImportFile = {
                showImportExportBottomSheet = false
                openDocumentLauncher.launch("*/*")
            },
            onDismiss = { showImportExportBottomSheet = false }
        )
    }

    // 6. Confirm Local Data Restore Dialog
    if (showImportConfirmDialog && pendingImportData != null) {
        val (backupData, rawJson) = pendingImportData!!
        AlertDialog(
            onDismissRequest = {
                showImportConfirmDialog = false
                pendingImportData = null
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Restore Backup?",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "This backup file contains:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "• ${backupData.workDays.size} daily attendance records\n• ${backupData.monthTargets.size} monthly target settings\n• Saved preferences & configurations",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Would you like to import and restore all saved data now?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val appContext = context.applicationContext
                        viewModel.importBackupData(rawJson) { success, msg ->
                            try {
                                Toast.makeText(
                                    appContext,
                                    if (success) "Backup restored successfully" else "Import failed: $msg",
                                    Toast.LENGTH_LONG
                                ).show()
                            } catch (e: Exception) {
                                // Ignore toast display failures
                            }
                        }
                        showImportConfirmDialog = false
                        pendingImportData = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_restore_button")
                ) {
                    Text("Yes, Restore", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImportConfirmDialog = false
                    pendingImportData = null
                }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

/**
 * 0. Language Card
 */
@Composable
private fun WireframeLanguageCard(
    languageCode: String,
    onClick: () -> Unit
) {
    val currentLang = AppLanguage.fromCode(languageCode)
    val strings = LocalAppStrings.current

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("language_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = strings.languageSetting,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Open language selector",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${currentLang.nativeTitle} (${currentLang.title})",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Language Selection Modal BottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelectionBottomSheet(
    currentLanguageCode: String,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val strings = LocalAppStrings.current
    var selectedLanguage by remember {
        mutableStateOf(AppLanguage.fromCode(currentLanguageCode))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("language_selection_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.selectLanguageTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppLanguage.values().forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    Card(
                        onClick = {
                            selectedLanguage = lang
                            onLanguageSelected(lang)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("language_option_${lang.code}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = lang.nativeTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = lang.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedLanguage = lang
                                    onLanguageSelected(lang)
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(strings.done, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * 1. Daily Target Card
 */
@Composable
private fun WireframeDailyTargetCard(
    dailyMinutes: Int,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val hours = dailyMinutes / 60
    val minutes = dailyMinutes % 60
    val formattedTime = if (dailyMinutes > 0) strings.formatTime(hours, minutes) else "_ _ : _ _"

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("daily_target_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = strings.dailyRequiredHours,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Open daily target",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = strings.dailyRequiredSubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = if (dailyMinutes > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * 2. Theme Card
 */
@Composable
private fun WireframeThemeCard(
    themeSettingString: String,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val (mode, colorId) = parseThemeSettings(themeSettingString)
    val modeName = when (mode) {
        "LIGHT" -> strings.themeLight
        "DARK" -> strings.themeDark
        else -> strings.themeSystem
    }
    val colorOption = ACCENT_COLOR_OPTIONS.firstOrNull { it.id.equals(colorId, ignoreCase = true) }
        ?: ACCENT_COLOR_OPTIONS[0]

    val displayText = "$modeName – ${colorOption.name}"

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("theme_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = strings.themeMode,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Open theme selector",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 3. Calendar Type Card
 */
@Composable
private fun WireframeCalendarTypeCard(
    calendarType: CalendarType,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val calName = if (calendarType == CalendarType.HIJRI_SHAMSI) strings.shamsiCalendar else strings.gregorianCalendar
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("calendar_type_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = strings.calendarSystem,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Open calendar type selector",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = calName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 3. Off Days Card
 */
@Composable
private fun WireframeOffDaysCard(
    offDaysString: String,
    calendarType: CalendarType = CalendarType.GREGORIAN,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val selectedSet = remember(offDaysString) {
        if (offDaysString.isBlank()) emptySet()
        else offDaysString.split(",").map { it.trim() }.toSet()
    }

    val isGregorian = calendarType == CalendarType.GREGORIAN
    val dayOrder = if (isGregorian) {
        listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
    } else {
        listOf("SATURDAY", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY")
    }

    val shortNameMap = if (isGregorian) {
        mapOf(
            "MONDAY" to "Mon",
            "TUESDAY" to "Tue",
            "WEDNESDAY" to "Wed",
            "THURSDAY" to "Thu",
            "FRIDAY" to "Fri",
            "SATURDAY" to "Sat",
            "SUNDAY" to "Sun"
        )
    } else if (strings.isRtl) {
        mapOf(
            "SATURDAY" to strings.saturday,
            "SUNDAY" to strings.sunday,
            "MONDAY" to strings.monday,
            "TUESDAY" to strings.tuesday,
            "WEDNESDAY" to strings.wednesday,
            "THURSDAY" to strings.thursday,
            "FRIDAY" to strings.friday
        )
    } else {
        mapOf(
            "SATURDAY" to "Shanbeh",
            "SUNDAY" to "Yekshanbeh",
            "MONDAY" to "Doshanbeh",
            "TUESDAY" to "Seshanbeh",
            "WEDNESDAY" to "Chaharshanbeh",
            "THURSDAY" to "Panjshanbeh",
            "FRIDAY" to "Jomeh"
        )
    }

    val formattedDaysList = dayOrder
        .filter { selectedSet.contains(it) }
        .mapNotNull { shortNameMap[it] }

    val daysDisplayText = when {
        formattedDaysList.isEmpty() -> strings.none
        else -> formattedDaysList.joinToString(" - ")
    }

    val countText = strings.formatDaysCount(selectedSet.size)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("off_days_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = strings.offDaysSetting,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Open off days",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = daysDisplayText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = countText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Import & Export (Backup & Restore) Card
 */
@Composable
private fun WireframeImportExportCard(
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("import_export_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ImportExport,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = strings.importExport,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.importExportSubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Open import and export",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Import & Export BottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportExportBottomSheet(
    onExportShare: () -> Unit,
    onExportSaveFile: () -> Unit,
    onImportFile: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 10.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        },
        modifier = Modifier.testTag("import_export_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = strings.importExportDialogTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = strings.importExportDialogSubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Export Section Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = strings.exportAllDataTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.exportAllDataDesc,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onExportShare,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("export_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.shareText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onExportSaveFile,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("export_save_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.saveFileText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Import Section Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = strings.importSavedDataTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.importSavedDataDesc,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = onImportFile,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("import_file_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.selectJsonFileText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 4. Reset All Recorded Data Card
 * Styled identically with the other settings cards (same 20.dp rounded shape, border, padding, and elevation)
 */
@Composable
private fun WireframeResetAllDataCard(
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("reset_all_data_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = strings.resetAllData,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Clear all data",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Theme Selection BottomSheet:
 * - 3 Theme modes in a single ROW (System, Light, Dark)
 * - Selected mode displays a prominent accent border
 * - Primary color palette picker below
 * - Save & Cancel buttons at bottom
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelectionBottomSheet(
    currentTheme: String,
    onThemeSaved: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val (initialMode, initialColor) = parseThemeSettings(currentTheme)

    var selectedMode by remember { mutableStateOf(initialMode) }
    var selectedColorId by remember { mutableStateOf(initialColor) }

    val activeAccent = ACCENT_COLOR_OPTIONS.firstOrNull { it.id.equals(selectedColorId, ignoreCase = true) }
        ?: ACCENT_COLOR_OPTIONS[0]

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 10.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        },
        modifier = Modifier.testTag("theme_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = strings.selectThemeTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = strings.selectThemeSubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ========================================================
            // 1. Theme Modes: 3 Items in a ROW with Border when Selected
            // ========================================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = strings.themeModeLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // System Option
                    ThemeModeRowTile(
                        label = strings.themeSystem,
                        icon = Icons.Default.SettingsBrightness,
                        isSelected = selectedMode == "SYSTEM",
                        accentColor = activeAccent.color,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedMode = "SYSTEM" }
                    )

                    // Light Option
                    ThemeModeRowTile(
                        label = strings.themeLight,
                        icon = Icons.Default.LightMode,
                        isSelected = selectedMode == "LIGHT",
                        accentColor = activeAccent.color,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedMode = "LIGHT" }
                    )

                    // Dark Option
                    ThemeModeRowTile(
                        label = strings.themeDark,
                        icon = Icons.Default.DarkMode,
                        isSelected = selectedMode == "DARK",
                        accentColor = activeAccent.color,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedMode = "DARK" }
                    )
                }
            }

            // ========================================================
            // 2. Primary Color Picker (Below the modes)
            // ========================================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = strings.primaryColorLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ACCENT_COLOR_OPTIONS.forEach { option ->
                        val isColorSelected = selectedColorId.equals(option.id, ignoreCase = true)
                        val colorNameLocalized = strings.getAccentColorName(option.id)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clickable { selectedColorId = option.id }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(option.color)
                                    .border(
                                        width = if (isColorSelected) 3.dp else 1.dp,
                                        color = if (isColorSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isColorSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Text(
                                text = colorNameLocalized.split(" ").firstOrNull() ?: colorNameLocalized,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isColorSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isColorSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ========================================================
            // 3. Save & Cancel Action Buttons
            // ========================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("cancel_theme_button")
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
                        val savedThemeString = buildThemeModeString(selectedMode, selectedColorId)
                        onThemeSaved(savedThemeString)
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = activeAccent.color,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
                    modifier = Modifier.testTag("save_theme_button")
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

/**
 * Clean Mode Tile for the 3-in-a-row layout in the Theme bottom sheet.
 * Displays a distinct accent border when selected.
 */
@Composable
private fun ThemeModeRowTile(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) accentColor else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier.height(76.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Calendar Type Selection Modal BottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTypeBottomSheet(
    currentCalendarType: CalendarType,
    onCalendarTypeSelected: (CalendarType) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedType by remember(currentCalendarType) { mutableStateOf(currentCalendarType) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 6.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        },
        modifier = Modifier.testTag("calendar_type_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header with minimal vertical spacing
            Column(
                modifier = Modifier.padding(bottom = 0.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = strings.selectCalendarTypeTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = strings.selectCalendarTypeSubtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Radio item list matching Off Days list style
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                val calendarOptions = CalendarType.entries
                calendarOptions.forEachIndexed { index, calOption ->
                    val isSelected = selectedType == calOption
                    val isDefault = calOption == CalendarType.GREGORIAN
                    val calTitle = when (calOption) {
                        CalendarType.GREGORIAN -> strings.gregorianCalendarTitle
                        CalendarType.HIJRI_SHAMSI -> strings.persianCalendarTitle
                    }
                    val calSubtitle = when (calOption) {
                        CalendarType.HIJRI_SHAMSI -> strings.shamsiDesc
                        else -> strings.gregorianDesc
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedType = calOption
                            }
                            .padding(horizontal = 4.dp, vertical = 11.dp)
                            .testTag("calendar_option_${calOption.name.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedType = calOption },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.size(22.dp)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Text(
                                    text = calTitle,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 15.sp
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = calSubtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (index < calendarOptions.size - 1) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            // Bottom Action Buttons: Cancel & Done
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("cancel_calendar_type_button")
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
                        onCalendarTypeSelected(selectedType)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
                    modifier = Modifier.testTag("save_calendar_type_button")
                ) {
                    Text(
                        text = strings.done,
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

/**
 * Off Days Checklist BottomSheet:
 * - Reduced compact spacing between header and checklist items
 * - Clean item list adapting to chosen calendar system
 * - Cancel & Done action buttons
 */
data class WeekdayItem(
    val dayOfWeekName: String,
    val displayName: String,
    val shortName: String,
    val farsiName: String,
    val farsiShort: String
)

private val GREGORIAN_DAYS_OF_WEEK = listOf(
    WeekdayItem("MONDAY", "Monday", "Mon", "دوشنبه", "د"),
    WeekdayItem("TUESDAY", "Tuesday", "Tue", "سه‌شنبه", "س"),
    WeekdayItem("WEDNESDAY", "Wednesday", "Wed", "چهارشنبه", "چ"),
    WeekdayItem("THURSDAY", "Thursday", "Thu", "پنج‌شنبه", "پ"),
    WeekdayItem("FRIDAY", "Friday", "Fri", "جمعه", "ج"),
    WeekdayItem("SATURDAY", "Saturday", "Sat", "شنبه", "ش"),
    WeekdayItem("SUNDAY", "Sunday", "Sun", "یکشنبه", "ی")
)

private val SHAMSI_DAYS_OF_WEEK = listOf(
    WeekdayItem("SATURDAY", "Saturday", "Sat", "شنبه", "ش"),
    WeekdayItem("SUNDAY", "Sunday", "Sun", "یکشنبه", "ی"),
    WeekdayItem("MONDAY", "Monday", "Mon", "دوشنبه", "د"),
    WeekdayItem("TUESDAY", "Tuesday", "Tue", "سه‌شنبه", "س"),
    WeekdayItem("WEDNESDAY", "Wednesday", "Wed", "چهارشنبه", "چ"),
    WeekdayItem("THURSDAY", "Thursday", "Thu", "پنج‌شنبه", "پ"),
    WeekdayItem("FRIDAY", "Friday", "Fri", "جمعه", "ج")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OffDaysBottomSheet(
    offDaysString: String,
    calendarType: CalendarType = CalendarType.GREGORIAN,
    onOffDaysChanged: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentSelected by remember(offDaysString) {
        mutableStateOf(
            if (offDaysString.isBlank()) emptySet()
            else offDaysString.split(",").map { it.trim() }.toSet()
        )
    }

    val daysList = if (calendarType == CalendarType.GREGORIAN) GREGORIAN_DAYS_OF_WEEK else SHAMSI_DAYS_OF_WEEK

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 6.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        },
        modifier = Modifier.testTag("off_days_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header with minimal vertical spacing
            Column(
                modifier = Modifier.padding(bottom = 0.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = strings.offDaysSetting,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = strings.offDaysSubtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Checklist of single items with comfortable padding
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                daysList.forEachIndexed { index, item ->
                    val isChecked = currentSelected.contains(item.dayOfWeekName)
                    val dayTitle = if (calendarType == CalendarType.HIJRI_SHAMSI || (strings.isRtl && calendarType != CalendarType.GREGORIAN)) item.farsiName else item.displayName

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                currentSelected = if (isChecked) {
                                    currentSelected - item.dayOfWeekName
                                } else {
                                    currentSelected + item.dayOfWeekName
                                }
                            }
                            .padding(horizontal = 4.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    currentSelected = if (checked) {
                                        currentSelected + item.dayOfWeekName
                                    } else {
                                        currentSelected - item.dayOfWeekName
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.size(22.dp)
                            )

                            Text(
                                text = dayTitle,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp
                                ),
                                color = if (isChecked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isChecked) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = strings.off,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    if (index < daysList.size - 1) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            // Bottom Action Buttons: Cancel & Done
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("cancel_off_days_button")
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
                        onOffDaysChanged(currentSelected.joinToString(","))
                        onDismiss()
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
                    modifier = Modifier.testTag("save_off_days_button")
                ) {
                    Text(
                        text = strings.done,
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

/**
 * Daily Limits Card (Min Enter / Max Exit Limit)
 * Styled exactly like the Enter/Exit component on the main screen (WireframeDailyLogRowCard).
 */
@Composable
private fun WireframeDailyLimitsCard(
    minMinutes: Int?,
    maxMinutes: Int?,
    onMinEnterClick: () -> Unit,
    onMaxExitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val minText = if (minMinutes != null && minMinutes > 0) {
        val h = minMinutes / 60
        val m = minMinutes % 60
        strings.formatTime(h, m)
    } else "_ _ : _ _"

    val maxText = if (maxMinutes != null && maxMinutes > 0) {
        val h = maxMinutes / 60
        val m = maxMinutes % 60
        strings.formatTime(h, m)
    } else "_ _ : _ _"

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_limits_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Top Row: Title "Work Limits" & Subtitle with Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = strings.workLimits,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.workHourLimitsSubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Middle Row: [ Min Enter Box ]  →  [ Max Exit Box ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                WireframeTimeBox(
                    label = strings.minEnterTime,
                    timeText = minText,
                    isSet = minMinutes != null && minMinutes > 0,
                    enabled = true,
                    onClick = onMinEnterClick,
                    modifier = Modifier.weight(1f),
                    testTag = "min_enter_limit_box"
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "to",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(18.dp)
                )

                WireframeTimeBox(
                    label = strings.maxExitTime,
                    timeText = maxText,
                    isSet = maxMinutes != null && maxMinutes > 0,
                    enabled = true,
                    onClick = onMaxExitClick,
                    modifier = Modifier.weight(1f),
                    testTag = "max_exit_limit_box"
                )
            }
        }
    }
}

