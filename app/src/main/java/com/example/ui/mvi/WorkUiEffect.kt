package com.example.ui.mvi

sealed interface WorkUiEffect {
    data class ShowSnackbar(val message: AppMessage, val extra: String? = null) : WorkUiEffect
    data class ScrollToTop(val animated: Boolean = true) : WorkUiEffect
    data class TimeValidationError(val errorType: AppMessage) : WorkUiEffect
}
