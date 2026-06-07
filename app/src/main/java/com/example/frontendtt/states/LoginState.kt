package com.example.frontendtt.states


data class LoginState(
    val correo: String = "",
    val name: String = "",
    val alias: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val petOption: String? = null,
    val tobaccoOption: String? = null,
    val isLoading: Boolean = false
)
