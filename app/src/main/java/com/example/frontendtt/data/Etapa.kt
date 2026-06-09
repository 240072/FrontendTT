package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class Etapa(
    val id: Int,
    val idviaje: Int,
    val iddestino: Int,
    val horainicio: String,
    val duracion: Int
)