package com.example.traveltogethersupabase.data

import kotlinx.serialization.Serializable

@Serializable
data class RegistroUsuario(
    val alias: String,
    val nombre: String,
    val correo: String,
    val tabaco: String?,
    val mascota: String?,
)
