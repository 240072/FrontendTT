package com.example.frontendtt.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontendtt.data.ListaViajes
import com.example.traveltogethersupabase.network.borrarViaje

import com.example.traveltogethersupabase.network.getViajesDelUsuario
import kotlinx.coroutines.launch

class ListaViajesViewModel: ViewModel() {
    var viajesState by mutableStateOf<List<ListaViajes>>(emptyList())
        private set

    fun cargarViajes() {
        viewModelScope.launch {
            viajesState = getViajesDelUsuario()
        }
    }
    fun deleteTrip(id: Int) {
        viewModelScope.launch {
            borrarViaje(id)
            cargarViajes()
        }
    }
}