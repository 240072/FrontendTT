package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class Viaje(
    val id: Int,
    val idcreador: String,
    val nombre: String,
    val descripcion: String?,
    val fechainicio: String,
    val fechafin: String,
    val tabaco: Boolean,
    val mascota: String,

)
