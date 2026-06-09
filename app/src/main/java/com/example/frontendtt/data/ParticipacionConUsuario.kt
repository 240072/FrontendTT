package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class ParticipacionConUsuario(
    val usuario: UsuarioNombre
)