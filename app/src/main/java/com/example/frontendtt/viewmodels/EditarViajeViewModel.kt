package com.example.frontendtt.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendtt.data.DetalleViaje
import com.example.frontendtt.data.NuevaEtapa
import com.example.frontendtt.data.NuevoDestino
import com.example.frontendtt.states.EditarViajeState
import com.example.frontendtt.states.LoginState
import com.example.traveltogethersupabase.data.NuevoViaje
import com.example.traveltogethersupabase.network.getDetallesViaje
import com.example.traveltogethersupabase.network.registrarDestino
import com.example.traveltogethersupabase.network.registrarEtapa
import com.example.traveltogethersupabase.network.registrarViaje
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import java.sql.Date
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal

class EditarViajeViewModel: ViewModel() {

    private val _editarViajeState = MutableStateFlow(EditarViajeState())
    val editarViajeState: StateFlow<EditarViajeState> = _editarViajeState.asStateFlow()
    var viajeState by mutableStateOf<DetalleViaje>(DetalleViaje(0,"","",null,"",""))
    var diasState by mutableIntStateOf(0)


    var diasDisponiblesState by mutableStateOf<List<String>>(emptyList())
        private set
    var idDestinoState: Int? by mutableStateOf(0 )

    fun onNameChange(nombre: String) {
        _editarViajeState.update { it.copy(nombre = nombre) }
    }
    fun onDescriptionChange(descripcion: String?) {
        _editarViajeState.update { it.copy(descripcion = descripcion) }
    }
    fun onInitialHourChange(horainicio: String) {
        _editarViajeState.update { it.copy(horainicio = horainicio) }
    }
    fun onFinalHourChange(horafin: String) {
        _editarViajeState.update { it.copy(horafin = horafin) }
    }
    fun onXCoordinateChange(coordx: Double) {
        _editarViajeState.update { it.copy(coordx = coordx) }
    }
    fun onYCoordinateChange(coordy: Double) {
        _editarViajeState.update { it.copy(coordy = coordy) }
    }
    fun onDificultyChange(dificultad: Int) {
        _editarViajeState.update { it.copy(dificultad = dificultad) }
    }
    fun onDurationChange(duracion: Int) {
        _editarViajeState.update { it.copy(duracion = duracion) }
    }
    fun getTripInfo(id: Int) {
        viewModelScope.launch {
          viajeState = getDetallesViaje(id)
          diasState = LocalDate.parse(viajeState.fechainicio).daysUntil(LocalDate.parse(viajeState.fechafin))+1
          diasDisponiblesState = List(diasState) { "Día ${it + 1}" }
        }

    }
    fun insertDestination(nuevoDestino: NuevoDestino): Int? {
        viewModelScope.launch {
            idDestinoState = registrarDestino(nuevoDestino)

        }
        return idDestinoState
    }
    fun insertStage(nuevaEtapa: NuevaEtapa) {
        viewModelScope.launch {
            registrarEtapa(nuevaEtapa)
        }
    }
}