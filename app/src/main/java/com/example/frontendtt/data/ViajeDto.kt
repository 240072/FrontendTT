package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class ViajeDto(
    val id: Int,
    val nombre: String,
    val fechainicio: String, // Las fechas suelen viajar como String (YYYY-MM-DD)
    val fechafin: String
)
