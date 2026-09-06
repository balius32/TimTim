package com.example.ui.mvi

sealed interface WorkUiEffect {
    data class ShowSnackbar(val message: String) : WorkUiEffect
    data class ScrollToTop(val animated: Boolean = true) : WorkUiEffect
    data class TimeValidationError(val message: String) : WorkUiEffect
}
