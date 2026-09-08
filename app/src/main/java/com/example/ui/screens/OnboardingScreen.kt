package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WorkViewModel
import com.example.ui.components.AvatarStyle
import com.example.ui.components.CustomAvatarDisplay
import com.example.ui.components.LimitTimePickerDialog
import com.example.ui.components.TargetTimePickerDialog
import com.example.ui.mvi.WorkUiState
import com.example.ui.theme.ACCENT_COLOR_OPTIONS
import com.example.ui.theme.buildThemeModeString
import com.example.ui.theme.parseThemeSettings
import com.example.util.CalendarType
import com.example.util.DataBackupHelper
import kotlinx.coroutines.launch

private const val TOTAL_STEPS = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    uiState: WorkUiState,
    viewModel: WorkViewModel,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentStep by remember { mutableIntStateOf(0) }
    var slideDirection by remember { mutableIntStateOf(1) }

    // Dialog state for restoring backup on the first step of onboarding
    var showInitialBackupPromptDialog by remember { mutableStateOf(true) }
    var showImportConfirmDialog by remember { mutableStateOf(false) }
    var pendingImportData by remember { mutableStateOf<Pair<com.example.util.BackupData, String>?>(null) }
    var isReadingFile by remember { mutableStateOf(false) }
    var isRestoringBackup by remember { mutableStateOf(false) }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            isReadingFile = true
            coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val jsonString = DataBackupHelper.readTextFromUri(context, uri)
                    val backupData = DataBackupHelper.parseBackupJson(jsonString)
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        isReadingFile = false
                        pendingImportData = Pair(backupData, jsonString)
                        showImportConfirmDialog = true
                    }
                } catch (e: Exception) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        isReadingFile = false
                        Toast.makeText(context, "Invalid backup file: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TimTim",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    TextButton(
                        onClick = onSkip,
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "Skip to Defaults",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
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
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = {
                                slideDirection = -1
                                currentStep--
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("onboarding_bottom_back_button"),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Back", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Button(
                        onClick = {
                            if (currentStep < TOTAL_STEPS - 1) {
                                slideDirection = 1
                                currentStep++
                            } else {
                                onComplete()
                            }
                        },
                        modifier = Modifier
                            .weight(if (currentStep > 0) 1.5f else 1f)
                            .height(52.dp)
                            .testTag(if (currentStep == TOTAL_STEPS - 1) "onboarding_finish_button" else "onboarding_next_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (currentStep == TOTAL_STEPS - 1) "Start Using TimTim" else "Continue",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (currentStep == TOTAL_STEPS - 1) Icons.Default.RocketLaunch else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Step Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Step ${currentStep + 1} of $TOTAL_STEPS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = getStepTitle(currentStep),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (currentStep + 1).toFloat() / TOTAL_STEPS.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (slideDirection > 0) {
                        (slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn(animationSpec = tween(300)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> -width } + fadeOut(animationSpec = tween(300)))
                    } else {
                        (slideInHorizontally(animationSpec = tween(300)) { width -> -width } + fadeIn(animationSpec = tween(300)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut(animationSpec = tween(300)))
                    }
                },
                label = "onboarding_step_anim"
            ) { targetStep ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (targetStep) {
                        0 -> OnboardingProfileStep(uiState = uiState, viewModel = viewModel)
                        1 -> OnboardingCalendarStep(uiState = uiState, viewModel = viewModel)
                        2 -> OnboardingTargetAndLimitsStep(uiState = uiState, viewModel = viewModel)
                        3 -> OnboardingOffDaysStep(uiState = uiState, viewModel = viewModel)
                        4 -> OnboardingThemeStep(uiState = uiState, viewModel = viewModel)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Dialog shown on the first page of onboarding asking if user has a backup file
    if (currentStep == 0 && showInitialBackupPromptDialog) {
        AlertDialog(
            onDismissRequest = {
                showInitialBackupPromptDialog = false
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Have a Backup?",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "If you have a previously saved backup file of your attendance and settings, you can restore it now and skip this setup process.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInitialBackupPromptDialog = false
                        openDocumentLauncher.launch("*/*")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("onboarding_select_backup_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Backup File", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showInitialBackupPromptDialog = false
                    },
                    modifier = Modifier.testTag("onboarding_dismiss_backup_button")
                ) {
                    Text(
                        text = "Continue Setup",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Confirmation dialog before applying the chosen backup file
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
                        .size(52.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Restore Backup & Skip Setup?",
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
                        text = "• ${backupData.workDays.size} daily attendance records\n• ${backupData.monthTargets.size} monthly target settings\n• Saved profile & configuration",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Restoring this data will complete onboarding and take you directly to your timesheet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isRestoringBackup = true
                        val appContext = context.applicationContext
                        viewModel.importBackupData(rawJson) { success, msg ->
                            isRestoringBackup = false
                            try {
                                Toast.makeText(
                                    appContext,
                                    if (success) "Backup restored successfully" else "Import failed: $msg",
                                    Toast.LENGTH_LONG
                                ).show()
                            } catch (e: Exception) {
                                // Ignore toast failures
                            }
                            if (success) {
                                showImportConfirmDialog = false
                                pendingImportData = null
                                onComplete()
                            }
                        }
                    },
                    enabled = !isRestoringBackup,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("onboarding_confirm_restore_button")
                ) {
                    if (isRestoringBackup) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Restoring...", fontWeight = FontWeight.Bold)
                    } else {
                        Text("Yes, Restore & Skip", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (!isRestoringBackup) {
                            showImportConfirmDialog = false
                            pendingImportData = null
                        }
                    },
                    enabled = !isRestoringBackup
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Progress Dialog when reading selected backup file
    if (isReadingFile) {
        AlertDialog(
            onDismissRequest = { /* Non-dismissible while reading */ },
            icon = {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
            },
            title = {
                Text(
                    text = "Reading Backup File...",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Please wait while your attendance and settings are being read.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {},
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

private fun getStepTitle(step: Int): String = when (step) {
    0 -> "Profile & Avatar"
    1 -> "Calendar System"
    2 -> "Target & Limits"
    3 -> "Off Days"
    4 -> "Theme & Appearance"
    else -> ""
}

/**
 * Reusable Settings-style Card Container for onboarding steps
 */
@Composable
private fun SettingsStyleCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    trailingBadge: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (trailingBadge != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = trailingBadge,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            content()
        }
    }
}

// -------------------------------------------------------------
// Step 0: Profile & Avatar Setup (Welcome card removed as requested)
// -------------------------------------------------------------
@Composable
fun OnboardingProfileStep(
    uiState: WorkUiState,
    viewModel: WorkViewModel
) {
    var userNameInput by remember(uiState.userName) { mutableStateOf(uiState.userName) }

    // Name input card
    SettingsStyleCard(
        title = "Display Name",
        icon = Icons.Default.Face
    ) {
        Text(
            text = "Enter your preferred name or nickname for the profile card.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = userNameInput,
            onValueChange = {
                userNameInput = it
                viewModel.updateUserName(it)
            },
            placeholder = { Text("Enter your name or nickname") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_name_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )
    }

    // Avatar card
    SettingsStyleCard(
        title = "Avatar Style",
        icon = Icons.Default.Palette
    ) {
        Text(
            text = "Pick an avatar that represents your workspace identity.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(AvatarStyle.entries) { avatar ->
                val isSelected = uiState.avatarId == avatar.id
                Card(
                    onClick = { viewModel.updateAvatar(avatar.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .width(90.dp)
                        .testTag("onboarding_avatar_${avatar.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            CustomAvatarDisplay(
                                avatarId = avatar.id,
                                modifier = Modifier.size(52.dp)
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = avatar.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Step 1: Calendar System (Matching CalendarTypeBottomSheet in Settings)
// -------------------------------------------------------------
@Composable
fun OnboardingCalendarStep(
    uiState: WorkUiState,
    viewModel: WorkViewModel
) {
    val selectedType = uiState.calendarType

    SettingsStyleCard(
        title = "Calendar Type",
        icon = Icons.Default.DateRange
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Select calendar type",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Choose the calendar system used across the entire app",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Radio item list matching Settings CalendarTypeBottomSheet
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            val calendarOptions = CalendarType.entries
            calendarOptions.forEachIndexed { index, calOption ->
                val isSelected = selectedType == calOption

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.updateCalendarType(calOption.name)
                        }
                        .padding(horizontal = 4.dp, vertical = 11.dp)
                        .testTag("onboarding_calendar_option_${calOption.name.lowercase()}"),
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
                            onClick = { viewModel.updateCalendarType(calOption.name) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.size(22.dp)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Text(
                                text = calOption.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = calOption.subtitle,
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
    }
}

// -------------------------------------------------------------
// Step 2: Daily Target & Work Limits (Combined step)
// -------------------------------------------------------------
@Composable
fun OnboardingTargetAndLimitsStep(
    uiState: WorkUiState,
    viewModel: WorkViewModel
) {
    val totalMins = uiState.settings.dailyRequiredMinutes
    val hours = totalMins / 60
    val minutes = totalMins % 60
    val formattedTime = if (totalMins > 0) String.format("%02d:%02d", hours, minutes) else "_ _ : _ _"
    var showTargetPicker by remember { mutableStateOf(false) }

    if (showTargetPicker) {
        TargetTimePickerDialog(
            initialHour = if (totalMins > 0) hours else 0,
            initialMinute = if (totalMins > 0) minutes else 0,
            onConfirm = { h, m ->
                viewModel.updateDailyRequiredTime(h, m)
                showTargetPicker = false
            },
            onDismiss = { showTargetPicker = false }
        )
    }

    val minMinutes = uiState.settings.minEnterMinutes
    val maxMinutes = uiState.settings.maxExitMinutes

    var showMinEnterLimitPicker by remember { mutableStateOf(false) }
    var showMaxExitLimitPicker by remember { mutableStateOf(false) }

    val minText = if (minMinutes != null && minMinutes > 0) {
        val h = minMinutes / 60
        val m = minMinutes % 60
        String.format("%02d:%02d", h, m)
    } else "_ _ : _ _"

    val maxText = if (maxMinutes != null && maxMinutes > 0) {
        val h = maxMinutes / 60
        val m = maxMinutes % 60
        String.format("%02d:%02d", h, m)
    } else "_ _ : _ _"

    if (showMinEnterLimitPicker) {
        val initialH = if (minMinutes != null && minMinutes > 0) minMinutes / 60 else 7
        val initialM = if (minMinutes != null && minMinutes > 0) minMinutes % 60 else 0
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

    if (showMaxExitLimitPicker) {
        val initialH = if (maxMinutes != null && maxMinutes > 0) maxMinutes / 60 else 19
        val initialM = if (maxMinutes != null && maxMinutes > 0) maxMinutes % 60 else 0
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

    // 1. Daily Target Card (without mins badge and without quick adjust)
    SettingsStyleCard(
        title = "Daily Target",
        icon = Icons.Default.Timer
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Daily target",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Set your standard required daily work hours",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Tap to open time picker (matching Settings WireframeDailyTargetCard)
        Card(
            onClick = { showTargetPicker = true },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("onboarding_target_time_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "target time",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = if (totalMins > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }

    // 2. Work Limits Card (without preset chips)
    SettingsStyleCard(
        title = "Work Limits",
        icon = Icons.Default.Tune
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Work Limits",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Tap Min Enter or Max Exit to set limits",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Middle Row: [ Min Enter Box ]  →  [ Max Exit Box ] (Exact wireframe layout)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            WireframeTimeBox(
                label = "Min Enter",
                timeText = minText,
                isSet = minMinutes != null && minMinutes > 0,
                enabled = true,
                onClick = { showMinEnterLimitPicker = true },
                modifier = Modifier.weight(1f),
                testTag = "onboarding_min_enter_limit_box"
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
                label = "Max Exit",
                timeText = maxText,
                isSet = maxMinutes != null && maxMinutes > 0,
                enabled = true,
                onClick = { showMaxExitLimitPicker = true },
                modifier = Modifier.weight(1f),
                testTag = "onboarding_max_exit_limit_box"
            )
        }
    }
}

// -------------------------------------------------------------
// Step 3: Weekly Off Days (Matching OffDaysBottomSheet in Settings))
// -------------------------------------------------------------
data class OnboardingWeekdayItem(
    val dayOfWeekName: String,
    val displayName: String
)

private val GREGORIAN_DAYS_LIST = listOf(
    OnboardingWeekdayItem("MONDAY", "Monday"),
    OnboardingWeekdayItem("TUESDAY", "Tuesday"),
    OnboardingWeekdayItem("WEDNESDAY", "Wednesday"),
    OnboardingWeekdayItem("THURSDAY", "Thursday"),
    OnboardingWeekdayItem("FRIDAY", "Friday"),
    OnboardingWeekdayItem("SATURDAY", "Saturday"),
    OnboardingWeekdayItem("SUNDAY", "Sunday")
)

private val SHAMSI_DAYS_LIST = listOf(
    OnboardingWeekdayItem("SATURDAY", "Shanbeh"),
    OnboardingWeekdayItem("SUNDAY", "Yekshanbeh"),
    OnboardingWeekdayItem("MONDAY", "Doshanbeh"),
    OnboardingWeekdayItem("TUESDAY", "Seshanbeh"),
    OnboardingWeekdayItem("WEDNESDAY", "Chaharshanbeh"),
    OnboardingWeekdayItem("THURSDAY", "Panjshanbeh"),
    OnboardingWeekdayItem("FRIDAY", "Jomeh")
)

@Composable
fun OnboardingOffDaysStep(
    uiState: WorkUiState,
    viewModel: WorkViewModel
) {
    val offDaysString = uiState.settings.offDaysOfWeek
    val currentSelected = remember(offDaysString) {
        if (offDaysString.isBlank()) emptySet()
        else offDaysString.split(",").map { it.trim().uppercase() }.filter { it.isNotEmpty() }.toSet()
    }

    val daysList = if (uiState.calendarType == CalendarType.GREGORIAN) GREGORIAN_DAYS_LIST else SHAMSI_DAYS_LIST

    SettingsStyleCard(
        title = "Off Days",
        icon = Icons.Default.CalendarMonth,
        trailingBadge = "${currentSelected.size} days"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Select days off",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Selected days will be automatically designated OFF in daily logs",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Checklist matching OffDaysBottomSheet in Settings
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            daysList.forEachIndexed { index, item ->
                val isChecked = currentSelected.contains(item.dayOfWeekName)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val nextSet = if (isChecked) {
                                currentSelected - item.dayOfWeekName
                            } else {
                                currentSelected + item.dayOfWeekName
                            }
                            viewModel.updateOffDaysOfWeek(nextSet.joinToString(","))
                        }
                        .padding(horizontal = 4.dp, vertical = 11.dp)
                        .testTag("onboarding_offday_${item.dayOfWeekName}"),
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
                                val nextSet = if (checked) {
                                    currentSelected + item.dayOfWeekName
                                } else {
                                    currentSelected - item.dayOfWeekName
                                }
                                viewModel.updateOffDaysOfWeek(nextSet.joinToString(","))
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.size(22.dp)
                        )

                        Text(
                            text = item.displayName,
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
                                text = "OFF",
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
    }
}

// -------------------------------------------------------------
// Step 4: Theme Selection (Matching ThemeSelectionBottomSheet in Settings, Summary configuration removed)
// -------------------------------------------------------------
@Composable
fun OnboardingThemeStep(
    uiState: WorkUiState,
    viewModel: WorkViewModel
) {
    val (selectedMode, selectedColorId) = remember(uiState.settings.themeMode) {
        parseThemeSettings(uiState.settings.themeMode)
    }

    val activeAccent = ACCENT_COLOR_OPTIONS.firstOrNull { it.id.equals(selectedColorId, ignoreCase = true) }
        ?: ACCENT_COLOR_OPTIONS[0]

    SettingsStyleCard(
        title = "Select Theme",
        icon = Icons.Default.Palette
    ) {
        // Header matching ThemeSelectionBottomSheet
        Column(
            modifier = Modifier.padding(bottom = 2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Select Theme",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Choose your mode and primary color",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 1. Theme mode 3-in-a-row layout matching Settings ThemeSelectionBottomSheet
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Appearance Mode",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeModeRowTile(
                    label = "System",
                    icon = Icons.Default.SettingsBrightness,
                    isSelected = selectedMode == "SYSTEM",
                    accentColor = activeAccent.color,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.updateThemeMode(buildThemeModeString("SYSTEM", selectedColorId))
                    }
                )

                ThemeModeRowTile(
                    label = "Light",
                    icon = Icons.Default.LightMode,
                    isSelected = selectedMode == "LIGHT",
                    accentColor = activeAccent.color,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.updateThemeMode(buildThemeModeString("LIGHT", selectedColorId))
                    }
                )

                ThemeModeRowTile(
                    label = "Dark",
                    icon = Icons.Default.DarkMode,
                    isSelected = selectedMode == "DARK",
                    accentColor = activeAccent.color,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.updateThemeMode(buildThemeModeString("DARK", selectedColorId))
                    }
                )
            }
        }

        // 2. Primary Color Picker matching Settings ThemeSelectionBottomSheet
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Primary Color",
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

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clickable {
                                viewModel.updateThemeMode(buildThemeModeString(selectedMode, option.id))
                            }
                            .padding(4.dp)
                            .testTag("onboarding_theme_color_${option.id}")
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
                            text = option.name.split(" ").firstOrNull() ?: option.name,
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
    }
}

/**
 * Clean Mode Tile for the 3-in-a-row layout in the Theme step.
 * Displays a distinct accent border when selected.
 */
@Composable
private fun ThemeModeRowTile(
    label: String,
    icon: ImageVector,
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
