package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class EtapaDetalle(
    val horainicio: String,
    val horafin: String,
    val destino: Destino
)