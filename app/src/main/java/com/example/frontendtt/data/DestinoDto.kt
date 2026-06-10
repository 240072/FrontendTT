package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class DestinoDto(
    val id: Int,
    val nombre: String,
    //val ubicacion: String?,
    val coordx: Double,
    val coordy: Double,
    val dificultad: Int
)
