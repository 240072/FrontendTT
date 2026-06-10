package com.example.frontendtt.data

import kotlinx.serialization.Serializable



@Serializable
data class EtapaConDetalles(
    val id: Int,
    val horainicio: String,
    val duracion: Int,
    val destino: DestinoDto, // Coincide con el nodo 'destino' del select
    val viaje: ViajeDto      // Coincide con el nodo 'viaje' del select
)