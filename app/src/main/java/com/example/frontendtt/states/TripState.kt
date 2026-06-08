package com.example.traveltogethersupabase.states


import java.sql.Date


data class TripState (
    val nombre: String = "",
    val descripcion: String = "",
    val participantes: Int = 1,
    val fechaInicio: Date? = null,
    val fechaFin: Date? = null,
    val tabaco: Boolean = false,
    val mascota: String = "NO",
)