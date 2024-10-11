package com.sommerengineering.baraudio.login

import android.content.Context
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption

class GoogleAuthenticationProvider (
    private val activityContext: Context
) {

    // instantiate google sign-in request
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true) // filter by accounts already used to sign-in
        .setAutoSelectEnabled(true) // automatic sign-in for users who register with their single account
        .build()

    // todo sign-up with setFilterByAuthorizedAccounts(false)

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    // todo sign-out with clearCredentialState()
}