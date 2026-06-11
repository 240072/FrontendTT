package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class ParticipacionConViaje(
    val idusuario: String,
    val idviaje: Int,
    // Supabase mapeará automáticamente la relación con la tabla viaje aquí
    val viaje: ListaViajes
)