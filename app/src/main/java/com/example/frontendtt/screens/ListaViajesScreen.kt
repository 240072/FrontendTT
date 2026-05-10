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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontendtt.ui.theme.*
import com.iessanalberto.dam2.gestionies.navigation.AppScreens
import kotlinx.coroutines.launch

/* ---------------------------------------------------- */
/* 🧳 MODELO DE DATOS */
/* ---------------------------------------------------- */
data class UserTrip(
    val id: Int,
    val nombre: String,
    val descripcion: String
)

@Composable
fun ListaViajesScreen(navController: NavController) {

    var listaViajes by remember {
        mutableStateOf(
            listOf(
                UserTrip(1, "Crucero por el Mediterráneo", "Explorando las islas griegas y costas italianas."),
                UserTrip(2, "Senderismo en los Alpes", "Ruta de 7 días por los picos más altos de Europa."),
                UserTrip(3, "Safari en Kenia", "Aventura salvaje observando a los cinco grandes."),
                UserTrip(4, "Tokio y Kioto", "Inmersión cultural en el corazón de Japón.")
            )
        )
    }

    var viajeAEliminar by remember { mutableStateOf<UserTrip?>(null) }
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
                    items(listaViajes, key = { it.id }) { viaje ->
                        Card(
                            onClick = { navController.navigate(AppScreens.ViajeScreen.route) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(20.dp).padding(end = 40.dp)) {
                                    Text(text = viaje.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TravelDeepNavy)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = viaje.descripcion, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                                }
                                IconButton(
                                    onClick = { viajeAEliminar = viaje },
                                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.7f))
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
                text = { Text("¿Estás seguro de que quieres abandonar el viaje \"${viajeAEliminar?.nombre}\"?") },
                confirmButton = {
                    TextButton(onClick = {
                        val nombreViaje = viajeAEliminar?.nombre
                        listaViajes = listaViajes.filter { it.id != viajeAEliminar?.id }
                        viajeAEliminar = null
                        scope.launch { snackbarHostState.showSnackbar("Has abandonado el viaje $nombreViaje") }
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
