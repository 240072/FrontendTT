package com.example.frontendtt.data

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UsuarioNombre(
    val id: String,
    val nombre: String
)