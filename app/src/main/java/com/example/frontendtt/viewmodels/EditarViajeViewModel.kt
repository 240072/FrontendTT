package com.example.frontendtt.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendtt.data.DetalleViaje
import com.example.frontendtt.data.EtapaConDestino
import com.example.frontendtt.data.NuevaEtapa
import com.example.frontendtt.data.NuevoDestino
import com.example.frontendtt.states.EditarViajeState
import com.example.traveltogethersupabase.network.SupabaseClient.supabase
import com.example.traveltogethersupabase.network.eliminarEtapa
import com.example.traveltogethersupabase.network.getDetallesViaje
import com.example.traveltogethersupabase.network.obtenerDestinosPorDia
import com.example.traveltogethersupabase.network.registrarDestino
import com.example.traveltogethersupabase.network.registrarEtapa
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

class EditarViajeViewModel: ViewModel() {

    private val _editarViajeState = MutableStateFlow(EditarViajeState())
    val editarViajeState: StateFlow<EditarViajeState> = _editarViajeState.asStateFlow()
    var viajeState by mutableStateOf<DetalleViaje>(DetalleViaje(0,"","",null,"",""))
    private set
    var diasState by mutableIntStateOf(0)
    private set
    var etapasPorDiaState by mutableStateOf<List<EtapaConDestino>>(emptyList())
    private set
    var idEtapaState by mutableStateOf<Int?>(null)
        private set


    var diasDisponiblesState by mutableStateOf<List<String>>(emptyList())
        private set
    var idDestinoState: Int? by mutableStateOf(0 )
    private set
    fun onNameChange(nombre: String) {
        _editarViajeState.update { it.copy(nombre = nombre) }
    }
    fun onDescriptionChange(descripcion: String) {
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
    fun onLocationChange(ubicacion: String) {
        _editarViajeState.update { it.copy(ubicacion = ubicacion) }
    }
    fun onTripDayChange (diaviaje: Int) {
        _editarViajeState.update { it.copy(diaviaje = diaviaje) }
    }
    fun getTripInfo(id: Int) {
        viewModelScope.launch {
          viajeState = getDetallesViaje(id)
          diasState = LocalDate.parse(viajeState.fechainicio).daysUntil(LocalDate.parse(viajeState.fechafin))+1
          diasDisponiblesState = List(diasState) { "Día ${it + 1}" }
        }

    }
    fun insertDestination(nuevoDestino: NuevoDestino, nuevaEtapa: NuevaEtapa, idViaje:Int,diaviaje: Int) {
        viewModelScope.launch {
            idDestinoState = registrarDestino(nuevoDestino)
            if (idDestinoState != 0) {
                val copiarIdEnEtapa = nuevaEtapa.copy(iddestino = idDestinoState!!)
                val idEtapa = registrarEtapa(copiarIdEnEtapa)
                idEtapaState = idEtapa

                etapasPorDiaState = obtenerDestinosPorDia(idViaje, diaviaje)

            }

        }

    }
    fun getDestinationsByDay(idViaje: Int, diaviaje: Int) {
        viewModelScope.launch {
            etapasPorDiaState = obtenerDestinosPorDia(idViaje,diaviaje)
        }

    }
    fun deleteStage(id:Int,idViaje: Int,diaviaje: Int){
        viewModelScope.launch {
            eliminarEtapa(id)
            etapasPorDiaState = obtenerDestinosPorDia(idViaje, diaviaje)
        }

    }

    fun updateDestination(
        etapaOriginal: EtapaConDestino,
        nuevoNombre: String,
        nuevaUbicacion: String,
        nuevaDescripcion: String,
        nuevaHoraInicio: String,
        nuevaHoraFin: String,
        nuevaCoordX: Double,
        nuevaCoordY: Double,
        nuevaDificultad: Int,
        viajeId: Int,
        diaActual: Int
    ) {
        viewModelScope.launch {
            try {
                // 1. Modificar la tabla de Destinos usando su ID único
                supabase.postgrest["destino"].update({
                    set("nombre", nuevoNombre)
                    set("ubicacion", nuevaUbicacion)
                    set("descripcion", nuevaDescripcion)
                    set("coordx", nuevaCoordX)
                    set("coordy", nuevaCoordY)
                    set("dificultad", nuevaDificultad)
                }) {
                    filter { eq("id", etapaOriginal.destino.id) }
                }

                // 2. Modificar la tabla de Etapas usando su ID único
                supabase.postgrest["etapa"].update({
                    set("horainicio", nuevaHoraInicio)
                    set("horafin", nuevaHoraFin)
                }) {
                    filter { eq("id", etapaOriginal.id) }
                }

                // 3. Forzar el refresco de la UI volviendo a consultar los datos del día
                getDestinationsByDay(viajeId, diaActual)

            } catch (e: Exception) {
                Log.e("SupabaseUpdate", "Error al actualizar la etapa: ${e.localizedMessage}")
            }
        }
    }
}
