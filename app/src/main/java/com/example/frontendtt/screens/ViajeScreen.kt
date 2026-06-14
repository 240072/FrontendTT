@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.frontendtt.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontendtt.components.DestinoItem
import com.example.frontendtt.components.SectionHeader
import com.example.frontendtt.data.Dificultad.opcionesDificultad
import com.example.frontendtt.data.EtapaDetalle
import com.example.frontendtt.data.UnirParticipacion
import com.example.frontendtt.ui.theme.*
import com.example.frontendtt.viewmodels.ViajeViewModel
import com.example.traveltogethersupabase.network.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun ViajeScreen(viajeId: Int, navController: NavController) {

    val viajeViewModel: ViajeViewModel = viewModel()
    val miUserId = remember { supabase.auth.currentUserOrNull()?.id }

    LaunchedEffect(viajeId) {
        viajeViewModel.getTrip(viajeId)
        viajeViewModel.getParticipants(viajeId,miUserId)
        viajeViewModel.getStages(viajeId)



    }
    val participantes = viajeViewModel.participantesState
    val etapas = viajeViewModel.etapasState
    val viaje = viajeViewModel.viajeState



    var destinoSeleccionado by remember { mutableStateOf<EtapaDetalle?>(null) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember {SnackbarHostState()}



    //val yaParticipa = participantes.any { it.id == userId }
    Log.d("Info",participantes.toString())
    Log.d("info", viaje.toString())
    Log.d("Info", etapas.toString())
    Log.d("Info", viajeViewModel.yaParticipa.toString())
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(TravelGradientTop, TravelGradientMiddle, TravelGradientBottom)))
    ) {
        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                /* 🪧 CARTEL PRINCIPAL */
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = viaje.nombre,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TravelPrimaryBlue,
                                letterSpacing = 1.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        viaje.descripcion?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = TravelEarth
                            )
                        }
                    }
                }

                /* 🔘 BOTÓN UNIRME */
                if (!viajeViewModel.yaParticipa) {
                    Button(
                        onClick = { viajeViewModel.joinTrip(UnirParticipacion(miUserId!!,viajeId))
                        scope.launch { snackbarHostState.showSnackbar("Te has unido al viaje") }},
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TravelPrimaryBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Unirme al Viaje", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                /* 👥 SECCIÓN PARTICIPANTES */
                SectionHeader(title = "Participantes")
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    participantes.forEach { participante ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = TravelPrimaryBlue)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = participante.nombre, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                            }
                        }
                    }
                }

                /* 📅 CALENDARIO */
                SectionHeader(title = "Calendario del Viaje")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = TravelDeepNavy, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Desde: ${viaje.fechainicio}", fontWeight = FontWeight.Medium)
                            Text("Hasta: ${viaje.fechafin}", fontWeight = FontWeight.Medium)
                        }
                    }
                }

                /* 🌍 DESTINOS DEL VIAJE */
                SectionHeader(title = "Destinos del viaje")
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    etapas.forEach { destino ->
                        DestinoItem(
                            nombre = destino.destino.nombre,
                            hora = "${destino.horainicio} - ${destino.horafin}",
                            ubicacion = destino.destino.ubicacion,
                            onClick = { destinoSeleccionado =
                                destino
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    /* 🪟 DIALOGO DE DESTINO */
    destinoSeleccionado?.let { destino ->
        AlertDialog(
            onDismissRequest = { destinoSeleccionado = null },
            confirmButton = {
                TextButton(onClick = { destinoSeleccionado = null }) {
                    Text("Cerrar", color = TravelPrimaryBlue, fontWeight = FontWeight.Bold)
                }
            },
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = TravelDeepNavy) },
            title = { Text(destino.destino.nombre, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    destino.destino.descripcion.let { Text(it) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
                    Row { Text("Horario: ", fontWeight = FontWeight.Bold); Text("${destino.horainicio} - ${destino.horafin}")}
                    Row { Text("Ubicación: ", fontWeight = FontWeight.Bold); Text(destino.destino.ubicacion) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Dificultad: ", fontWeight = FontWeight.Bold)
                        val (emoji, color) = when (destino.destino.dificultad) {
                            2 -> "🔴" to Color.Red
                            1 -> "🟠" to Color(0xFFF57C00)
                            0 -> "🟢" to TravelPrimaryBlue
                            else -> "⚪" to Color.Gray
                        }

                        Text("$emoji ${opcionesDificultad[destino.destino.dificultad]}", color = color, fontWeight = FontWeight.Bold)
                    }
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White
        )
    }
}














