package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class NuevoDestino(
    val nombre: String,
    val descripcion: String?,
    val coordx: Double,
    val coordy: Double,
    val dificultad: Int
)