package com.example.traveltogethersupabase.data
import java.sql.Date
import kotlinx.serialization.Serializable

@Serializable
data class NuevoViaje(
    val idcreador: String,
    val nombre: String,
    val descripcion: String?,
    val participantes: Int,
    val fechainicio: String,
    val fechafin: String,
    val tabaco: Boolean,
    val mascota: String,
)
