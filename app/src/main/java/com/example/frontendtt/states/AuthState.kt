package com.example.traveltogethersupabase.states

data class AuthState(
    val alias: String = "",
    val nombre: String = "",
    val password: String = "",
    val correo: String = "",
    val isLogged: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

