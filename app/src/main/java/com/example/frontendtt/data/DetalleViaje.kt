package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class DetalleViaje(
    val id: Int,
    val idcreador: String,
    val nombre: String,
    val descripcion: String?,
    val fechainicio: String,
    val fechafin: String

    )
