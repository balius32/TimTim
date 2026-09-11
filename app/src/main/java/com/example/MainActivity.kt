package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.WorkViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.ui.mvi.AppScreen
import com.example.ui.mvi.WorkUiEffect
import com.example.ui.mvi.WorkUiIntent
import com.example.ui.components.AppTimePickerDialog
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RemainingTimeScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TimesheetScreen

import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSecondaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.launch

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.EnStrings
import com.example.ui.localization.FaStrings
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalAppStrings

class MainActivity : ComponentActivity() {

    private val viewModel: WorkViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Hold the splash screen until initial data and settings are retrieved from database
        splashScreen.setKeepOnScreenCondition {
            !viewModel.isReady.value
        }

        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val appLanguage = AppLanguage.fromCode(uiState.settings.language)
            val appStrings = if (appLanguage == AppLanguage.FA) FaStrings else EnStrings
            val layoutDirection = if (appLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(
                LocalLayoutDirection provides layoutDirection,
                LocalAppLanguage provides appLanguage,
                LocalAppStrings provides appStrings
            ) {
                MyApplicationTheme(themeMode = uiState.settings.themeMode) {
                    WorkHoursApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkHoursApp(
    viewModel: WorkViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    val appLanguage = LocalAppLanguage.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle MVI Side Effects asynchronously
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is WorkUiEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is WorkUiEffect.TimeValidationError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is WorkUiEffect.ScrollToTop -> {
                    // Scroll effect handled at screen level if needed
                }
            }
        }
    }

    val screenOrder = remember {
        mapOf(
            AppScreen.ONBOARDING to -1,
            AppScreen.TIMESHEET to 0,
            AppScreen.PROFILE to 1,
            AppScreen.SETTINGS to 2,
            AppScreen.REPORT to 3,
            AppScreen.REMAINING_TIME to 4
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.testTag("app_snackbar_host")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState.currentScreen,
                transitionSpec = {
                    val isForward = (screenOrder[targetState] ?: 0) > (screenOrder[initialState] ?: 0)
                    val rtlMultiplier = if (appLanguage.isRtl) -1 else 1
                    if (isForward) {
                        (slideInHorizontally(animationSpec = tween(340, easing = FastOutSlowInEasing)) { fullWidth -> (fullWidth * 0.35f * rtlMultiplier).toInt() } +
                         fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing))) togetherWith
                        (slideOutHorizontally(animationSpec = tween(280, easing = FastOutSlowInEasing)) { fullWidth -> -(fullWidth * 0.25f * rtlMultiplier).toInt() } +
                         fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing)))
                    } else {
                        (slideInHorizontally(animationSpec = tween(340, easing = FastOutSlowInEasing)) { fullWidth -> -(fullWidth * 0.35f * rtlMultiplier).toInt() } +
                         fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing))) togetherWith
                        (slideOutHorizontally(animationSpec = tween(280, easing = FastOutSlowInEasing)) { fullWidth -> (fullWidth * 0.25f * rtlMultiplier).toInt() } +
                         fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing)))
                    }
                },
                label = "screen_navigation_content"
            ) { screen ->
                when (screen) {
                    AppScreen.ONBOARDING -> {
                        OnboardingScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onComplete = { viewModel.completeOnboarding() },
                            onSkip = { viewModel.skipOnboarding() }
                        )
                    }
                    AppScreen.TIMESHEET -> {
                        TimesheetScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onProfileClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                            onNavigateToRemainingTime = { day ->
                                viewModel.navigateToRemainingTime(day)
                            }
                        )
                    }
                    AppScreen.PROFILE -> {
                        ProfileScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onBackClick = { viewModel.navigateTo(AppScreen.TIMESHEET) },
                            onNavigateToSettings = { viewModel.navigateTo(AppScreen.SETTINGS) },
                            onNavigateToReport = { viewModel.navigateTo(AppScreen.REPORT) }
                        )
                    }
                    AppScreen.SETTINGS -> {
                        SettingsScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onBackClick = { viewModel.navigateTo(AppScreen.PROFILE) }
                        )
                    }
                    AppScreen.REPORT -> {
                        ReportScreen(
                            uiState = uiState,
                            viewModel = viewModel,
                            onBackClick = { viewModel.navigateTo(AppScreen.PROFILE) }
                        )
                    }
                    AppScreen.REMAINING_TIME -> {
                        val targetDay = uiState.selectedRemainingTimeDay ?: uiState.todayEntity
                        RemainingTimeScreen(
                            uiState = uiState,
                            day = targetDay,
                            onBackClick = { viewModel.navigateTo(AppScreen.TIMESHEET) },
                            onExitNow = {
                                if (targetDay != null) {
                                    viewModel.logExitNowForDay(targetDay)
                                } else {
                                    viewModel.logTodayExitNow()
                                }
                                viewModel.navigateTo(AppScreen.TIMESHEET)
                            }
                        )
                    }
                }
            }
        }

        // Time Picker Dialog
        if (uiState.showTimePickerDialog && uiState.selectedDayForTimePick != null) {
            val day = uiState.selectedDayForTimePick!!
            val isEnter = uiState.isPickingEnterTime
            val initialH = if (isEnter) day.enterHour ?: 8 else day.exitHour ?: 17
            val initialM = if (isEnter) day.enterMinute ?: 0 else day.exitMinute ?: 0

            val dialogTitle = if (isEnter) {
                "${strings.setEntryTimeTitle} (${strings.dayCol} ${day.dayNumber})"
            } else {
                "${strings.setExitTimeTitle} (${strings.dayCol} ${day.dayNumber})"
            }

            AppTimePickerDialog(
                title = dialogTitle,
                initialHour = initialH,
                initialMinute = initialM,
                isEnterTime = isEnter,
                dayNumber = day.dayNumber,
                existingEnterHour = day.enterHour,
                existingEnterMinute = day.enterMinute,
                existingExitHour = day.exitHour,
                existingExitMinute = day.exitMinute,
                onConfirm = { hour, minute ->
                    viewModel.onTimeConfirmed(hour, minute)
                },
                onDismiss = {
                    viewModel.dismissTimePicker()
                },
                onClear = {
                    if (isEnter) {
                        viewModel.clearEnterTime(day.dayNumber)
                    } else {
                        viewModel.clearExitTime(day.dayNumber)
                    }
                    viewModel.dismissTimePicker()
                },
                onShowSnackbar = { message ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
        }

        // Reset Confirmation Dialog
        if (uiState.showResetConfirmation) {
            AlertDialog(
                onDismissRequest = { viewModel.showResetConfirmation(false) },
                icon = {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = {
                    Text(
                        text = strings.resetAllConfirmTitle,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(strings.resetAllConfirmMessage)
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.confirmResetAll() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.testTag("confirm_reset_button")
                    ) {
                        Text(strings.reset)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { viewModel.showResetConfirmation(false) },
                        modifier = Modifier.testTag("cancel_reset_button")
                    ) {
                        Text(strings.cancel)
                    }
                },
                modifier = Modifier.testTag("reset_confirmation_dialog")
            )
        }
    }
}
