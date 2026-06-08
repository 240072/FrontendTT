package com.example.frontendtt.viewmodels

import androidx.lifecycle.ViewModel
import com.example.frontendtt.states.LoginState
import com.example.traveltogethersupabase.states.TripState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.sql.Date

class NewTripViewModel: ViewModel() {
    private val _tripState = MutableStateFlow(TripState())
    val tripState: StateFlow<TripState> = _tripState.asStateFlow()

    fun onNameChange(nombre: String) {
        _tripState.update { currentState ->
            currentState.copy(nombre = nombre) }
    }
    fun onDescriptionChange(descripcion: String) {
        _tripState.update { currentState ->
            currentState.copy(descripcion = descripcion) }
    }
    fun onParticipantsChange(participantes: Int) {
        _tripState.update { currentState ->
            currentState.copy(participantes = participantes) }
    }
    fun onInitialDateChange(fechaInicio: Date?) {
        _tripState.update { currentState ->
            currentState.copy(fechaInicio = fechaInicio) }
    }
    fun onFinalDateChange(fechaFin: Date?) {
        _tripState.update { currentState ->
            currentState.copy(fechaFin = fechaFin) }
    }
    fun onTobaccoChange(tabaco: Boolean) {
        _tripState.update { currentState ->
            currentState.copy(tabaco = tabaco) }
    }
    fun onPetChange(mascota: String) {
        _tripState.update { currentState ->
            currentState.copy(mascota = mascota) }
    }
}