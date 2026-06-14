@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.frontendtt.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontendtt.data.ListaViajes
import com.example.frontendtt.ui.theme.*
import com.example.frontendtt.viewmodels.ListaViajesViewModel
import com.example.traveltogethersupabase.network.SupabaseClient.supabase
import com.iessanalberto.dam2.gestionies.navigation.AppScreens
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun ListaViajesScreen(navController: NavController) {

    val listaViajesViewModel: ListaViajesViewModel = viewModel()
    LaunchedEffect(Unit) {
        listaViajesViewModel.cargarViajes()
    }

    val viajes = listaViajesViewModel.viajesState
    val userId = supabase.auth.currentUserOrNull()?.id
    var viajeAEliminar by remember { mutableStateOf<ListaViajes?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(TravelGradientTop, TravelGradientMiddle, TravelGradientBottom)))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(AppScreens.NuevoViajeScreen.route) },
                    containerColor = TravelPrimaryBlue
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo viaje", tint = Color.White)
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = "Mi lista de viajes",
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = TravelPrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(viajes, key = { it.id }) { viaje ->
                        Card(
                            onClick = { navController.navigate(AppScreens.ViajeScreen.route.replace("{viajeId}", viaje.id.toString())) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(20.dp).padding(end = 40.dp)) {
                                    Text(text = viaje.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TravelDeepNavy)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    viaje.descripcion?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray) }
                                }
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre botones
                                ) {
                                    if (userId == viaje.idcreador) {
                                        IconButton(
                                            onClick = {
                                                navController.navigate(
                                                    AppScreens.EditarViajeScreen.route.replace(
                                                        "{viajeId}",
                                                        viaje.id.toString()
                                                    )
                                                )
                                            },

                                            ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Editar",
                                                tint = Color.Red.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { viajeAEliminar = viaje },

                                        ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = Color.Red.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (viajeAEliminar != null) {
            AlertDialog(
                onDismissRequest = { viajeAEliminar = null },
                title = { Text("¿Abandonar viaje?") },
                text = { Text(if (userId == viajeAEliminar?.idcreador)"¿Estás seguro de que quieres eliminar el viaje \"${viajeAEliminar?.nombre}\"?" else "¿Estás seguro de que quieres abandonar el viaje \"${viajeAEliminar?.nombre}\"?") },
                confirmButton = {
                    TextButton(onClick = {
                        val nombreViaje = viajeAEliminar?.nombre

                        if (userId == viajeAEliminar?.idcreador) {
                            listaViajesViewModel.deleteTrip(viajeAEliminar?.id ?: 0)
                        } else {
                            listaViajesViewModel.deleteParticipation(userId!!,
                                viajeAEliminar?.id ?: 0
                            )
                        }
                        scope.launch { snackbarHostState.showSnackbar("Has abandonado el viaje $nombreViaje") }
                        viajeAEliminar = null
                    }) { Text("Confirmar", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { viajeAEliminar = null }) { Text("Cancelar") }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White
            )
        }
    }
}
