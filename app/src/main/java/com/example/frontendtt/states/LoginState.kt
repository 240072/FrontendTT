package com.example.frontendtt.states


data class LoginState(
    val correo: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isLoading: Boolean = false
)
