@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.frontendtt.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontendtt.components.showDatePicker
import com.example.frontendtt.ui.theme.*
import com.example.frontendtt.viewmodels.NewTripViewModel
import com.example.traveltogethersupabase.data.MascotaTrip.opcionesMascotaTrip
import com.example.traveltogethersupabase.data.NuevoViaje
import com.example.traveltogethersupabase.network.SupabaseClient.supabase
import com.example.traveltogethersupabase.network.registrarViaje
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import java.sql.Date
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun NuevoViajeScreen(navController: NavController) {

    val newTripViewModel: NewTripViewModel = viewModel()
    val tripState by newTripViewModel.tripState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val mascotaSeleccionada = tripState.mascota

    val user = supabase.auth.currentUserOrNull()
    val uuid = user?.id
    val scope = rememberCoroutineScope()


    var expanded by remember { mutableStateOf(false) }
    val opcionesParticipantes = (1..12).map { it.toString() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(TravelGradientTop, TravelGradientMiddle, TravelGradientBottom)))
    ) {
        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(text = "Planifica tu Aventura", style = MaterialTheme.typography.headlineMedium, color = TravelPrimaryBlue, fontWeight = FontWeight.Bold)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(value = tripState.nombre, onValueChange = { newTripViewModel.onNameChange(it) }, label = { Text("Nombre de Viaje") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = tripState.descripcion, onValueChange = { newTripViewModel.onDescriptionChange(it) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp))

                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = tripState.participantes.toString(),
                                onValueChange = {newTripViewModel.onParticipantsChange(it.toInt())},
                                readOnly = true,
                                label = { Text("Número de Participantes") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                opcionesParticipantes.forEach { opcion ->
                                    DropdownMenuItem(text = { Text(opcion) }, onClick = { newTripViewModel.onParticipantsChange(opcion.toInt()) ; expanded = false })
                                }
                            }
                        }

                        // Mascotas
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Pets, contentDescription = null, tint = TravelEarth)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Mascotas", fontWeight = FontWeight.Bold, color = TravelEarth)
                            }
                            //Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                               // Icon(Icons.Default.ThumbUp, contentDescription = "Sí", tint = if (admiteMascotas == true) TravelPrimaryBlue else Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(32.dp).clickable { admiteMascotas = true })
                                // Icon(Icons.Default.ThumbDown, contentDescription = "No", tint = if (admiteMascotas == false) Color.Red else Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(32.dp).clickable { admiteMascotas = false })
                                opcionesMascotaTrip.forEach { (texto, valor) ->
                                FilterChip(
                                    selected = (valor == mascotaSeleccionada),
                                    onClick = {
                                        newTripViewModel.onPetChange(
                                            if (mascotaSeleccionada == valor) "NO" else valor
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = texto,
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TravelPrimaryBlue,
                                        selectedLabelColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                            }
                        }

                        // Tabaco
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SmokeFree, contentDescription = null, tint = TravelEarth)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tabaco", fontWeight = FontWeight.Bold, color = TravelEarth)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(Icons.Default.ThumbUp, contentDescription = "Sí", tint = if (tripState.tabaco) TravelPrimaryBlue else Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(32.dp).clickable { newTripViewModel.onTobaccoChange(true) })
                                Icon(Icons.Default.ThumbDown, contentDescription = "No", tint = if (!tripState.tabaco) Color.Red else Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(32.dp).clickable { newTripViewModel.onTobaccoChange(false) })
                            }
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker(context) { if (tripState.fechaInicio == null || tripState.fechaFin != null) { newTripViewModel.onInitialDateChange(Date.valueOf(it)); newTripViewModel.onFinalDateChange(null) } else { newTripViewModel.onFinalDateChange(Date.valueOf(it)) } } }.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Calendario", modifier = Modifier.size(44.dp), tint = TravelPrimaryBlue)
                        Column {
                            Text("Selecciona fechas", fontWeight = FontWeight.Bold, color = TravelPrimaryBlue, style = MaterialTheme.typography.titleMedium)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(if (tripState.fechaInicio.toString() == null) "Inicio" else tripState.fechaInicio.toString(), color = if (tripState.fechaInicio == null) Color.Gray else TravelEarth)
                                Icon(painter = painterResource(id = android.R.drawable.ic_media_play), contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                Text(tripState.fechaFin.toString() ?: "Fin", color = if (tripState.fechaFin == null) Color.Gray else TravelEarth)
                            }
                        }
                    }
                }

                Button(
                    onClick = { scope.launch {
                try {
                    val insertarViaje = NuevoViaje(
                        uuid.toString(),
                        tripState.nombre,
                        tripState.descripcion,
                        tripState.participantes,
                        tripState.fechaInicio.toString(),
                        tripState.fechaFin.toString(),
                        tripState.tabaco,
                        tripState.mascota
                    )
                    registrarViaje(insertarViaje)
                    Log.d("Auth",insertarViaje.toString())
                    navController.navigate("editar_viaje_screen")

                } catch (e: Exception) {
                    println("Error en el registro: ${e.message}")

                }
            } },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TravelPrimaryBlue)
                ) {
                    Text("Crear Viaje", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
