package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class NuevaEtapa(

    val idviaje: Int,
    val iddestino: Int = 0,
    val horainicio: String,
    val duracion: Int
)