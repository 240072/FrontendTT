package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class ListaViajes(
    val id: Int,
    val idcreador: String,
    val nombre: String,
    val descripcion: String?,
    val participantes: Int,
    val fechainicio: String,
    val fechafin: String,
    val tabaco: Boolean,
    val mascota: String,
)
