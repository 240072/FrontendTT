package com.example.frontendtt.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendtt.states.LoginState
import com.example.traveltogethersupabase.network.verificarSiAliasExiste
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel: ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    var aliasState by mutableStateOf(false)






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
    fun onNameChange(name: String) {
        _loginState.update { it.copy(name = name) }
    }
    fun onAliasChange(alias: String) {
        _loginState.update { it.copy(alias = alias) }
    }
    fun onPetOptionChange(petOption: String?) {
        _loginState.update { it.copy(petOption = petOption) }
    }
    fun onTobaccoOptionChange(tobaccoOption: String?) {
        _loginState.update { it.copy(tobaccoOption = tobaccoOption) }
    }

    fun checkIfAliasExists(alias:String): Boolean {
        viewModelScope.launch {
            aliasState = verificarSiAliasExiste(alias)
        }
        return aliasState
    }

}