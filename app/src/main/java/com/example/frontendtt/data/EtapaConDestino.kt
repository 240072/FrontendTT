package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class EtapaConDestino(
    val id: Int,
    val idviaje: Int,
    val iddestino: Int,
    val horainicio: String,
    val horafin: String,
    val diaviaje: Int,
    val destino: Destino
)