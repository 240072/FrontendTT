package com.example.frontendtt.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendtt.data.DetalleViaje
import com.example.frontendtt.data.EtapaDetalle
import com.example.frontendtt.data.ListaViajes
import com.example.frontendtt.data.UnirParticipacion
import com.example.frontendtt.data.UsuarioNombre
import com.example.traveltogethersupabase.network.getDetallesViaje
import com.example.traveltogethersupabase.network.obtenerEtapasDetalle
import com.example.traveltogethersupabase.network.obtenerParticipantes
import com.example.traveltogethersupabase.network.unirseViaje
import kotlinx.coroutines.launch

class ViajeViewModel: ViewModel () {

    var participantesState by mutableStateOf<List<UsuarioNombre>>(emptyList())
        private set
    var etapasState by mutableStateOf<List<EtapaDetalle>>(emptyList())
        private set

    var viajeState by mutableStateOf<DetalleViaje>(DetalleViaje(0,"","",null,"",""))
    var yaParticipa by mutableStateOf(false)
    fun getParticipants(id: Int, userId: String?) {
        viewModelScope.launch {
            participantesState = obtenerParticipantes(id)
            yaParticipa = participantesState.any{it.id == userId}
        }
    }
    fun getStages(id: Int) {
        viewModelScope.launch {
            etapasState = obtenerEtapasDetalle(id)
        }
    }
    fun getTrip(id: Int) {
        viewModelScope.launch {
            viajeState = getDetallesViaje(id)
        }
    }
    fun joinTrip(usuario: UnirParticipacion){
        viewModelScope.launch {
            unirseViaje(usuario)
            getParticipants(usuario.idviaje,usuario.idusuario)

        }
    }
}