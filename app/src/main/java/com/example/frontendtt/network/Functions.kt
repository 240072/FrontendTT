package com.example.traveltogethersupabase.network

import com.example.frontendtt.data.Destino
import com.example.frontendtt.data.DetalleViaje
import com.example.frontendtt.data.Etapa
import com.example.frontendtt.data.EtapaConDetalles
import com.example.frontendtt.data.EtapaDetalle
import com.example.frontendtt.data.ListaViajes
import com.example.frontendtt.data.NuevaEtapa
import com.example.frontendtt.data.NuevoDestino
import com.example.frontendtt.data.ParticipacionConUsuario
import com.example.frontendtt.data.UnirParticipacion
import com.example.frontendtt.data.UsuarioNombre
import com.example.frontendtt.data.Viaje
import com.example.traveltogethersupabase.data.NuevoViaje
import com.example.traveltogethersupabase.data.RegistroUsuario
import com.example.traveltogethersupabase.network.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

suspend fun enviarRegistro(usuario: RegistroUsuario) {
    try {
        supabase.from("usuario").insert(usuario)
        // Si llegas aquí, se envió correctamente
    } catch (e: Exception) {
        e.printStackTrace()
        // Aquí podrías manejar el error (ej. falta de internet)
    }
}
suspend fun unirseViaje(usuario: UnirParticipacion){
    supabase.from("participacion").insert(usuario)
}
// Cambiamos el tipo de retorno a Int? (o String? si tu ID es un UUID)
suspend fun registrarViaje(viaje: NuevoViaje): Int? {
    return try {
        // Al añadir .select(), Supabase devuelve la fila creada
        val response = supabase.from("viaje")
            .insert(viaje) {
                select()
            }
            // Decodificamos esa fila usando tu modelo de viaje completo (el que sí tiene ID)
            .decodeSingle<Viaje>()

        response.id // Devolvemos el ID generado por la base de datos
    } catch (e: Exception) {
        e.printStackTrace()
        null // Si hay un error (ej. sin internet), devolvemos null
    }
}
suspend fun registrarDestino(destino: NuevoDestino): Int? {
    return try {
        // Al añadir .select(), Supabase devuelve la fila creada
        val response = supabase.from("destino")
            .insert(destino) {
                select()
            }
            // Decodificamos esa fila usando tu modelo de viaje completo (el que sí tiene ID)
            .decodeSingle<Destino>()

        response.id // Devolvemos el ID generado por la base de datos
    } catch (e: Exception) {
        e.printStackTrace()
        null // Si hay un error (ej. sin internet), devolvemos null
    }
}
suspend fun registrarEtapa(etapa: NuevaEtapa){
    try {
        // Al añadir .select(), Supabase devuelve la fila creada
        supabase.from("etapa")
            .insert(etapa)
    } catch (e: Exception) {
        e.printStackTrace()
        null // Si hay un error (ej. sin internet), devolvemos null
    }
}
suspend fun buscarEtapasConDestinoYViaje(
    fechainicio: String?,
    fechafin: String?,
): List<EtapaConDetalles> { // Luego definimos esta clase abajo

    // 1. Especificamos qué columnas queremos de la etapa y de sus tablas relacionadas.
    // Usamos la sintaxis de Supabase: tabla_relacionada(columna1, columna2)
    val columnasASeleccionar = """
        id,
        horainicio,
        duracion,
        destino (
            id, nombre, coordx, coordy, dificultad
        ),
        viaje!inner (
            id, nombre, fechainicio, fechafin
        )
    """.trimIndent()

    // 2. Lanzamos la consulta sobre la tabla 'etapa' pasándole las columnas
    val respuesta = supabase.from("etapa").select(Columns.raw(columnasASeleccionar)) {

        // 3. ¡Aquí dentro ocurre la magia de los filtros!
        filter {
            // Filtro por ubicación si el usuario la escribió


            // Filtro por fecha si el usuario la seleccionó
            if (!fechainicio.isNullOrBlank()) {
                // Buscamos viajes que empiecen a partir de esa fecha
                gte("viaje.fechainicio", fechainicio)
            }
            if (!fechafin.isNullOrBlank()) {
                // Buscamos viajes que empiecen a partir de esa fecha
                lte("viaje.fechainicio", fechafin)
            }

        }
    }

    // 4. Parseamos el resultado a objetos de Kotlin
    return respuesta.decodeList<EtapaConDetalles>()
}
suspend fun getDetalleViajesDelUsuario(): List<ListaViajes> {
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
suspend fun getDetallesViaje(id: Int): DetalleViaje {


    val result = supabase
        .postgrest["viaje"]
        .select {
            filter {
                eq("id", id)
            }
        }

    return result.decodeSingle()
}
suspend fun obtenerParticipantes(idViaje: Int): List<UsuarioNombre> {
    return try {

        val resultado = supabase.postgrest["participacion"]
            .select(
                columns = Columns.raw(
                    """
                    usuario(id,nombre)                  
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("idviaje", idViaje)
                }
            }
            .decodeList<ParticipacionConUsuario>()

        resultado.map { UsuarioNombre(
            id = it.usuario.id,
            nombre = it.usuario.nombre
        )}

    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
suspend fun obtenerEtapasDetalle(idViaje: Int): List<EtapaDetalle> {
    return try {

        // 1. Obtener etapas del viaje
        val etapas = supabase.postgrest["etapa"]
            .select {
                filter {
                    eq("idviaje", idViaje)
                }
            }
            .decodeList<Etapa>()

        if (etapas.isEmpty()) {
            return emptyList()
        }

        // 2. Obtener todos los ids de destino
        val idsDestino = etapas.map { it.iddestino }

        // 3. Obtener todos los destinos
        val destinos = supabase.postgrest["destino"]
            .select()
            .decodeList<Destino>()
            .filter { it.id in idsDestino }

        // 4. Construir la respuesta final
        etapas.mapNotNull { etapa ->

            val destino = destinos.find {
                it.id == etapa.iddestino
            }

            destino?.let {
                EtapaDetalle(
                    horainicio = etapa.horainicio,
                    duracion = etapa.duracion,
                    destino = it
                )
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
suspend fun borrarViaje(id: Int) {
    supabase
        .postgrest["viaje"]
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