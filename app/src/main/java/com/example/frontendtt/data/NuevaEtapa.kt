package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class NuevaEtapa(

    val idviaje: Int,
    val iddestino: Int,
    val horainicio: String,
    val duracion: Int
)