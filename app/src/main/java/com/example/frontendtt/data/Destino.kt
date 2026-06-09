package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class Destino(
    val id: Int,
    val nombre: String,
    val descripcion: String?,
    val coordx: Double,
    val coordy: Double,
    val dificultad: Int
)