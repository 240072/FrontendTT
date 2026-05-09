package com.example.frontendtt.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import com.example.frontendtt.states.LoginState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update



class LoginViewModel: ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()




    // Instantiate a Google sign-in request
    val googleIdOption = GetGoogleIdOption.Builder()
        // Your server's client ID, not your Android client ID.
        //.setServerClientId("177445505400-rarpc2mo5otpe5oaap39m0go4k18gcfk.apps.googleusercontent.com")
        .setServerClientId("firebase-adminsdk-fbsvc@gestionies-33707.iam.gserviceaccount.com")
        // Only show accounts previously used to sign in.
        .setFilterByAuthorizedAccounts(false)
        .build()

    // Create the Credential Manager request
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    // Funciones para modificar las variables de estado
    fun onCorreoChange(correo: String) {
        _loginState.update { currentState ->
            currentState.copy(correo = correo) }
    }

    fun onPasswordChange(password: String) {
        _loginState.update { it.copy(password = password) }
    }
    fun onRepeatPasswordChange(repeatPassword: String) {
        _loginState.update { it.copy(repeatPassword = repeatPassword) }
    }



}