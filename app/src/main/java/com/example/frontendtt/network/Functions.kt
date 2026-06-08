package com.example.traveltogethersupabase.network

import com.example.frontendtt.data.ListaViajes
import com.example.traveltogethersupabase.data.NuevoViaje
import com.example.traveltogethersupabase.data.RegistroUsuario
import com.example.traveltogethersupabase.network.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlin.uuid.Uuid

suspend fun enviarRegistro(usuario: RegistroUsuario) {
    try {
        supabase.from("usuario").insert(usuario)
        // Si llegas aquí, se envió correctamente
    } catch (e: Exception) {
        e.printStackTrace()
        // Aquí podrías manejar el error (ej. falta de internet)
    }
}
suspend fun registrarViaje(viaje: NuevoViaje) {
    try {
        supabase.from("viaje").insert(viaje)
        // Si llegas aquí, se envió correctamente
    } catch (e: Exception) {
        e.printStackTrace()
        // Aquí podrías manejar el error (ej. falta de internet)
    }
}

suspend fun getViajesDelUsuario(): List<ListaViajes> {
    val userId = supabase.auth.currentUserOrNull()?.id
        ?: return emptyList()

    val result = supabase
        .postgrest["viaje"]
        .select {
            filter {
                eq("idcreador", userId)
            }
        }

    return result.decodeList<ListaViajes>()
}
suspend fun borrarViaje(id: Int) {
    supabase
        .postgrest["viajes"]
        .delete {
            filter {
                eq("id", id)
            }
        }
}
suspend fun cerrarSesion() {
    try {
        supabase.auth.signOut()
        // Aquí deberías redirigir al usuario a la pantalla de Login
    } catch (e: Exception) {
        println("Error al cerrar sesión: ${e.message}")
    }
}