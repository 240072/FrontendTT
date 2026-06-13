package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class DestinoUpdate(
    val nombre: String,
    val ubicacion: String,
    val descripcion: String,
    val coordx: Double,
    val coordy: Double,
    val dificultad: Int,
)