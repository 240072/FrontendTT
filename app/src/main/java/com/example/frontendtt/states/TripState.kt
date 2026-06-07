package com.example.traveltogethersupabase.states


import java.sql.Date


data class TripState (
    val nombre: String = "",
    val descripcion: String = "",
    val participantes: Int = 0,
    val fechaInicio: Date = Date.valueOf(java.time.LocalDate.now().toString()),
    val fechaFin: Date = Date.valueOf(java.time.LocalDate.now().toString()),
    val tabaco: Boolean = false,
    val mascota: String = "",
)