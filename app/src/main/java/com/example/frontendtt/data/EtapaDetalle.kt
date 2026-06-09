package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class EtapaDetalle(
    val horainicio: String,
    val duracion: Int,
    val destino: Destino
)