package com.example.frontendtt.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendtt.data.EtapaConDetalles
import com.example.frontendtt.states.MenuState
import com.example.traveltogethersupabase.network.buscarEtapasConDestinoYViaje
import com.example.traveltogethersupabase.network.cerrarSesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MenuViewModel: ViewModel () {
    private val _menuState = MutableStateFlow(MenuState())
    val menuState: StateFlow<MenuState> = _menuState.asStateFlow()
    var listaDestinosState by mutableStateOf<List<EtapaConDetalles>>(emptyList())
        private set
    var listaFiltradaState by mutableStateOf<List<EtapaConDetalles>>(emptyList())
        private set
    fun buscarDestinos(fechainicio: String?, fechafin: String?, coordy: Double, coordx: Double, distancia:Double) {
        viewModelScope.launch {
            listaDestinosState = buscarEtapasConDestinoYViaje(fechainicio, fechafin)

            if (coordx == 0.0) {
                // Si la coordenada es 0.0, saltamos el cálculo y asignamos la lista completa
                listaFiltradaState = listaDestinosState
                Log.d ("Sin coordenadas", listaFiltradaState.size.toString())
            } else {
                listaFiltradaState = listaDestinosState.filter { etapa ->

                    val distanciaPuntos = calcularDistancia(
                        lat1 = coordy, // coordy
                        lon1 = coordx, // coordx
                        lat2 = etapa.destino.coordy,
                        lon2 = etapa.destino.coordx
                    )
                    distanciaPuntos <= distancia
                }
                Log.d ("Con coordenadas", listaFiltradaState.size.toString())
            }
        }

    }

    fun closeSession(){
        viewModelScope.launch {
            cerrarSesion()
        }
    }
    fun onInitialDateChange(fechainicio: String) {
        _menuState.update { it.copy(fechainicio = fechainicio) }
    }
    fun onFinalDateChange(fechafin: String) {
        _menuState.update { it.copy(fechafin = fechafin) }
    }
    fun onXCoordinateChange(coordx: Double) {
        _menuState.update { it.copy(coordx = coordx) }
    }
    fun onYCoordinateChange(coordy: Double) {
        _menuState.update { it.copy(coordy = coordy) }
    }
    fun onDistanceChange(distancia: Double) {
        _menuState.update { it.copy(distancia = distancia) }
    }
    // Función auxiliar para calcular la distancia en kilómetros entre dos coordenadas
    fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Radio de la Tierra en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }



}