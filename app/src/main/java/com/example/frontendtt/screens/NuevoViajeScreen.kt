@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.frontendtt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import com.example.frontendtt.components.showDatePicker
import com.example.frontendtt.ui.theme.*

@Composable
fun NuevoViajeScreen(navController: NavController) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var nombreViaje by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var numParticipantes by remember { mutableStateOf("1") }
    var admiteMascotas by remember { mutableStateOf<Boolean?>(null) }
    var admiteTabaco by remember { mutableStateOf<Boolean?>(null) }
    var startDate by remember { mutableStateOf<String?>(null) }
    var endDate by remember { mutableStateOf<String?>(null) }

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
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(value = nombreViaje, onValueChange = { nombreViaje = it }, label = { Text("Nombre de Viaje") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(12.dp))

                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = numParticipantes,
                                onValueChange = {},
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
                                    DropdownMenuItem(text = { Text(opcion) }, onClick = { numParticipantes = opcion; expanded = false })
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
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(Icons.Default.ThumbUp, contentDescription = "Sí", tint = if (admiteMascotas == true) TravelPrimaryBlue else Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(32.dp).clickable { admiteMascotas = true })
                                Icon(Icons.Default.ThumbDown, contentDescription = "No", tint = if (admiteMascotas == false) Color.Red else Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(32.dp).clickable { admiteMascotas = false })
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
                                Icon(Icons.Default.ThumbUp, contentDescription = "Sí", tint = if (admiteTabaco == true) TravelPrimaryBlue else Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(32.dp).clickable { admiteTabaco = true })
                                Icon(Icons.Default.ThumbDown, contentDescription = "No", tint = if (admiteTabaco == false) Color.Red else Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(32.dp).clickable { admiteTabaco = false })
                            }
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker(context) { if (startDate == null || endDate != null) { startDate = it; endDate = null } else { endDate = it } } }.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Calendario", modifier = Modifier.size(44.dp), tint = TravelPrimaryBlue)
                        Column {
                            Text("Selecciona fechas", fontWeight = FontWeight.Bold, color = TravelPrimaryBlue, style = MaterialTheme.typography.titleMedium)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(startDate ?: "Inicio", color = if (startDate == null) Color.Gray else TravelEarth)
                                Icon(painter = painterResource(id = android.R.drawable.ic_media_play), contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                Text(endDate ?: "Fin", color = if (endDate == null) Color.Gray else TravelEarth)
                            }
                        }
                    }
                }

                Button(
                    onClick = { navController.navigate("editar_viaje_screen") },
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
