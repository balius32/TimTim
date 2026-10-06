package com.example.data.drive

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DriveAuthManager(
    private val context: Context
) {
    private val signInClient: GoogleSignInClient by lazy {
        // Drive uses account + OAuth scope only. Do NOT requestIdToken —
        // a missing/wrong Web client ID causes silent failure after account pick.
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
            .build()
        GoogleSignIn.getClient(context, options)
    }

    fun getSignInIntent(): Intent = signInClient.signInIntent

    suspend fun handleSignInResult(data: Intent?): Result<GoogleSignInAccount> = withContext(Dispatchers.IO) {
        runCatching {
            GoogleSignIn.getSignedInAccountFromIntent(data).await()
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(IllegalStateException(mapSignInError(it), it)) }
        )
    }

    suspend fun silentSignIn(): GoogleSignInAccount? = withContext(Dispatchers.IO) {
        runCatching {
            val account = signInClient.silentSignIn().await()
            if (GoogleSignIn.hasPermissions(account, Scope(DriveScopes.DRIVE_APPDATA))) account else null
        }.getOrNull()
    }

    fun getLastSignedInAccount(): GoogleSignInAccount? {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return if (account != null && GoogleSignIn.hasPermissions(account, Scope(DriveScopes.DRIVE_APPDATA))) {
            account
        } else {
            null
        }
    }

    suspend fun signOut() {
        signInClient.signOut().await()
    }

    fun buildDriveService(account: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(DriveScopes.DRIVE_APPDATA)
        ).apply {
            selectedAccount = account.account
        }
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("TimTim")
            .build()
    }

    private fun mapSignInError(error: Throwable): String {
        val api = error as? ApiException
            ?: error.cause as? ApiException
        return when (api?.statusCode) {
            GoogleSignInStatusCodes.SIGN_IN_CANCELLED ->
                "Google Sign-In was cancelled"
            GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS ->
                "Google Sign-In already in progress"
            GoogleSignInStatusCodes.SIGN_IN_FAILED ->
                "Google Sign-In failed. Try again."
            // CommonStatusCodes.DEVELOPER_ERROR = 10
            10 ->
                "Google Sign-In misconfigured (code 10). Check Android OAuth client package " +
                    "com.aistudio.worktracker.twuoys and debug SHA-1 in Google Cloud Console."
            else -> api?.statusMessage
                ?: error.message
                ?: "Google Sign-In failed"
        }
    }
}
