package com.example.ui.mvi

import com.example.ui.localization.AppStrings

enum class AppMessage {
    LOGGED_CHECK_IN,
    LOGGED_CHECK_OUT,
    MONTH_RECORDS_RESET,
    ALL_APP_DATA_CLEARED,
    FAILED_TO_IMPORT_BACKUP,
    WELCOME_TO_TIMTIM,
    EXIT_TIME_CANNOT_BE_EARLIER,
    ENTER_TIME_CANNOT_BE_LATER,
    IMPORT_FAILED;

    fun asString(strings: AppStrings): String {
        return when (this) {
            LOGGED_CHECK_IN -> strings.loggedCheckInSuccessfully
            LOGGED_CHECK_OUT -> strings.loggedCheckOutSuccessfully
            MONTH_RECORDS_RESET -> strings.monthRecordsReset
            ALL_APP_DATA_CLEARED -> strings.allAppDataCleared
            FAILED_TO_IMPORT_BACKUP -> strings.failedToImportBackup
            WELCOME_TO_TIMTIM -> strings.welcomeToTimTim
            EXIT_TIME_CANNOT_BE_EARLIER -> strings.exitTimeCannotBeEarlier
            ENTER_TIME_CANNOT_BE_LATER -> strings.enterTimeCannotBeLater
            IMPORT_FAILED -> strings.importFailed
        }
    }
}
