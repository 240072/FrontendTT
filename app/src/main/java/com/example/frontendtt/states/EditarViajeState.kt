package com.example.frontendtt.states

data class EditarViajeState (
    val nombre: String = "",
    val descripcion: String? = null,
    val coordx: Double = 0.0,
    val coordy: Double = 0.0,
    val dificultad: Int = 0,
    val idviaje: Int = 0,
    val iddestino: Int = 0,
    val horainicio: String = "",
    val horafin: String = "",
    val duracion: Int = 0
)