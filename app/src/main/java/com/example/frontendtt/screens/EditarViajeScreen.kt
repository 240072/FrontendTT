@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.frontendtt.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontendtt.components.DestinoItem
import com.example.frontendtt.ui.theme.*
import com.example.frontendtt.viewmodels.EditarViajeViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.util.Locale
import com.example.frontendtt.data.Dificultad.opcionesDificultad
import com.example.frontendtt.data.EtapaConDestino
import com.example.frontendtt.data.NuevaEtapa
import com.example.frontendtt.data.NuevoDestino
import com.example.frontendtt.states.EditarViajeState
import com.iessanalberto.dam2.gestionies.navigation.AppScreens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.Duration

@Composable
fun EditarViajeScreen(navController: NavController, viajeId: Int) {
    val editarViajeViewModel : EditarViajeViewModel = viewModel()
    val editarViajeState by editarViajeViewModel.editarViajeState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val diasDisponibles = editarViajeViewModel.diasDisponiblesState
    var mostrarFormulario by remember { mutableStateOf(false) }
    var destinoAEditar by remember { mutableStateOf<EtapaConDestino?>(null) }
    var destinoAEliminar by remember { mutableStateOf<EtapaConDestino?>(null) }
    var destinoAVer by remember { mutableStateOf<EtapaConDestino?>(null) }

    LaunchedEffect(viajeId) {
        editarViajeViewModel.getTripInfo(viajeId)
    }

    LaunchedEffect(viajeId, editarViajeState.diaviaje) {
        editarViajeViewModel.getDestinationsByDay(
            viajeId,
            editarViajeState.diaviaje
        )
    }

    // EFECTO NUEVO: Escucha cambios en destinoAEditar para precargar o limpiar el formulario
    LaunchedEffect(destinoAEditar) {
        if (destinoAEditar != null) {
            editarViajeViewModel.onNameChange(destinoAEditar!!.destino.nombre)
            editarViajeViewModel.onLocationChange(destinoAEditar!!.destino.ubicacion)
            editarViajeViewModel.onDescriptionChange(destinoAEditar!!.destino.descripcion)
            editarViajeViewModel.onInitialHourChange(destinoAEditar!!.horainicio)
            editarViajeViewModel.onFinalHourChange(destinoAEditar!!.horafin)
            editarViajeViewModel.onXCoordinateChange(destinoAEditar!!.destino.coordx)
            editarViajeViewModel.onYCoordinateChange(destinoAEditar!!.destino.coordy)
            editarViajeViewModel.onDificultyChange(destinoAEditar!!.destino.dificultad)
        } else {
            // Limpieza si es un nuevo destino
            editarViajeViewModel.onNameChange("")
            editarViajeViewModel.onLocationChange("")
            editarViajeViewModel.onDescriptionChange("")
            editarViajeViewModel.onInitialHourChange("00:00")
            editarViajeViewModel.onFinalHourChange("00:00")
            editarViajeViewModel.onXCoordinateChange(0.0)
            editarViajeViewModel.onYCoordinateChange(0.0)
            editarViajeViewModel.onDificultyChange(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(TravelGradientTop, TravelGradientMiddle, TravelGradientBottom)))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Editar Itinerario", style = MaterialTheme.typography.displaySmall, color = TravelPrimaryBlue, fontWeight = FontWeight.Bold)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Seleccionar día", fontWeight = FontWeight.Bold, color = TravelPrimaryBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        diasDisponibles.forEachIndexed { index, label ->
                            FilterChip(
                                selected = editarViajeState.diaviaje == index,
                                onClick = { editarViajeViewModel.onTripDayChange(index) },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TravelPrimaryBlue, selectedLabelColor = Color.White),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Log.d("Lista", editarViajeViewModel.etapasPorDiaState.toString())
                        }
                    }
                }

                Text("Destinos del día", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(editarViajeViewModel.etapasPorDiaState) { destino ->
                        DestinoItem(
                            nombre = destino.destino.nombre,
                            hora = destino.horainicio,
                            ubicacion = destino.destino.ubicacion,
                            onEdit = { destinoAEditar = destino; mostrarFormulario = true },
                            onDelete = { destinoAEliminar = destino },
                            onClick = { destinoAVer = destino }
                        )
                    }
                }

                Button(
                    onClick = { destinoAEditar = null; mostrarFormulario = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TravelPrimaryBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar destino", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { navController.navigate(AppScreens.MenuScreen.route) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TravelDeepNavy)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar y volver al menú", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (mostrarFormulario) {
            FormularioDestinoDialog(
                destinoExistente = destinoAEditar,
                onDismiss = { mostrarFormulario = false },
                onSave = { mostrarFormulario = false },
                viajeId = viajeId,
                editarViajeViewModel = editarViajeViewModel,
                editarViajeState = editarViajeState
            )
        }

        if (destinoAVer != null) {
            AlertDialog(
                onDismissRequest = { destinoAVer = null },
                confirmButton = { TextButton(onClick = { destinoAVer = null }) { Text("Cerrar", color = TravelPrimaryBlue, fontWeight = FontWeight.Bold) } },
                icon = { Icon(Icons.Default.Info, contentDescription = null, tint = TravelDeepNavy) },
                title = { Text(destinoAVer!!.destino.nombre, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp), tint = Color.Gray); Spacer(Modifier.width(8.dp)); Text(destinoAVer!!.horainicio) }
                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = Color.Gray); Spacer(Modifier.width(8.dp)); Text(destinoAVer!!.destino.ubicacion) }
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                        Text("Descripción:", fontWeight = FontWeight.Bold); Text(destinoAVer!!.destino.descripcion)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Dificultad: ", fontWeight = FontWeight.Bold)
                            val (emoji, color) = when (destinoAVer!!.destino.dificultad) {
                                2 -> "🔴" to Color.Red
                                1 -> "🟠" to Color(0xFFF57C00)
                                0 -> "🟢" to TravelPrimaryBlue
                                else -> "⚪" to Color.Gray
                            }
                            Text("$emoji ${opcionesDificultad[destinoAVer!!.destino.dificultad]}", color = color, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                containerColor = Color.White
            )
        }

        if (destinoAEliminar != null) {
            AlertDialog(
                onDismissRequest = { destinoAEliminar = null },
                title = { Text("¿Eliminar destino?") },
                text = { Text("¿Seguro que quieres eliminar este destino?") },
                confirmButton = {
                    TextButton(onClick = {
                        editarViajeViewModel.deleteStage(destinoAEliminar?.id ?: 0, viajeId, editarViajeState.diaviaje)
                        scope.launch { snackbarHostState.showSnackbar("Destino eliminado") }
                        destinoAEliminar = null
                    }) { Text("Eliminar", color = Color.Red) }
                },
                dismissButton = { TextButton(onClick = { destinoAEliminar = null }) { Text("Cancelar") } }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioDestinoDialog(
    destinoExistente: EtapaConDestino?,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    viajeId: Int,
    editarViajeViewModel : EditarViajeViewModel,
    editarViajeState: EditarViajeState
) {
    var showInicioPicker by remember { mutableStateOf(false) }
    var showFinPicker by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var searchLocationText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (destinoExistente == null) "Nuevo Destino" else "Editar Destino", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TravelPrimaryBlue)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Cerrar") }
                }
                OutlinedTextField(value = editarViajeState.nombre, onValueChange = { editarViajeViewModel.onNameChange(it) }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedCard(
                        onClick = { showInicioPicker = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) { Text("Inicio", style = MaterialTheme.typography.labelSmall, color = Color.Gray); Text(editarViajeState.horainicio, fontWeight = FontWeight.Bold) }
                    }
                    OutlinedCard(
                        onClick = { showFinPicker = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) { Text("Fin", style = MaterialTheme.typography.labelSmall, color = Color.Gray); Text(editarViajeState.horafin, fontWeight = FontWeight.Bold) }
                    }
                }

                OutlinedTextField(
                    value = editarViajeState.ubicacion,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Ubicación") },
                    modifier = Modifier.fillMaxWidth().clickable { searchLocationText = ""; showLocationDialog = true },
                    enabled = false,
                    trailingIcon = { Icon(Icons.Default.LocationOn, null, tint = TravelPrimaryBlue) }
                )

                OutlinedTextField(
                    value = editarViajeState.descripcion,
                    onValueChange = { editarViajeViewModel.onDescriptionChange(it) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Text("Dificultad", fontWeight = FontWeight.Bold)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    opcionesDificultad.forEach { (index, diff) ->
                        SegmentedButton(
                            selected = (index == editarViajeState.dificultad),
                            onClick = { editarViajeViewModel.onDificultyChange(index) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = opcionesDificultad.size)
                        ) { Text(diff, fontSize = 10.sp) }
                    }
                }

                Button(
                    onClick = {
                        if (destinoExistente == null) {
                            // INSERTAR NUEVO
                            val destino = NuevoDestino(editarViajeState.nombre, editarViajeState.ubicacion, editarViajeState.descripcion, editarViajeState.coordx, editarViajeState.coordy, editarViajeState.dificultad)
                            val etapa = NuevaEtapa(idviaje = viajeId, horainicio = editarViajeState.horainicio, horafin = editarViajeState.horafin, diaviaje = editarViajeState.diaviaje)
                            editarViajeViewModel.insertDestination(destino, etapa, viajeId, editarViajeState.diaviaje)
                        } else {
                            // ACTUALIZAR EXISTENTE
                            editarViajeViewModel.updateDestination(
                                etapaOriginal = destinoExistente,
                                nuevoNombre = editarViajeState.nombre,
                                nuevaUbicacion = editarViajeState.ubicacion,
                                nuevaDescripcion = editarViajeState.descripcion,
                                nuevaHoraInicio = editarViajeState.horainicio,
                                nuevaHoraFin = editarViajeState.horafin,
                                nuevaCoordX = editarViajeState.coordx,
                                nuevaCoordY = editarViajeState.coordy,
                                nuevaDificultad = editarViajeState.dificultad,
                                viajeId = viajeId,
                                diaActual = editarViajeState.diaviaje
                            )
                        }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TravelPrimaryBlue)
                ) {
                    Text(if (destinoExistente == null) "Agregar" else "Guardar")
                }
            }
        }
    }

    if (showInicioPicker) { TimePickerView(onDismiss = { showInicioPicker = false }, onConfirm = { h, m -> editarViajeViewModel.onInitialHourChange(String.format(Locale.US, "%02d:%02d", h, m)); showInicioPicker = false }) }
    if (showFinPicker) { TimePickerView(onDismiss = { showFinPicker = false }, onConfirm = { h, m -> editarViajeViewModel.onFinalHourChange(String.format(Locale.US, "%02d:%02d", h, m)); showFinPicker = false }) }

    if (showLocationDialog) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val lifecycleOwner = LocalLifecycleOwner.current

        var selectedGeoPoint by remember { mutableStateOf(GeoPoint(42.5689, -0.5496)) }

        var hasLocationPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            )
        }

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasLocationPermission = isGranted
        }

        LaunchedEffect(Unit) {
            if (!hasLocationPermission) {
                launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        fun buscarLugar(query: String) {
            if (query.isBlank()) return

            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val geocoder = android.location.Geocoder(context, Locale.getDefault())
                    val resultados = geocoder.getFromLocationName(query, 1)

                    if (!resultados.isNullOrEmpty()) {
                        val direccion = resultados[0]

                        editarViajeViewModel.onXCoordinateChange(direccion.longitude)
                        editarViajeViewModel.onYCoordinateChange(direccion.latitude)
                        val nuevoPunto = GeoPoint(direccion.latitude, direccion.longitude)

                        withContext(Dispatchers.Main) {
                            selectedGeoPoint = nuevoPunto
                            searchLocationText = direccion.getAddressLine(0) ?: query
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "No se ha encontrado ningún resultado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        Dialog(onDismissRequest = { showLocationDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().height(550.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Elegir Destino", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TravelPrimaryBlue)
                        IconButton(onClick = { showLocationDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }

                    OutlinedTextField(
                        value = searchLocationText,
                        onValueChange = { searchLocationText = it },
                        label = { Text("Nombre del lugar") },
                        placeholder = { Text("Ej: Playa de las Catedrales") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { buscarLugar(searchLocationText) }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar lugar")
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { buscarLugar(searchLocationText) })
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFEEEEEE))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AndroidView<MapView>(
                            factory = { ctx ->
                                Configuration.getInstance().userAgentValue = ctx.packageName
                                MapView(ctx).apply {
                                    setTileSource(TileSourceFactory.MAPNIK)
                                    setMultiTouchControls(true)
                                    controller.setZoom(15.0)
                                    controller.setCenter(selectedGeoPoint)

                                    val observer = LifecycleEventObserver { _, event ->
                                        when (event) {
                                            Lifecycle.Event.ON_RESUME -> onResume()
                                            Lifecycle.Event.ON_PAUSE -> onPause()
                                            else -> {}
                                        }
                                    }
                                    lifecycleOwner.lifecycle.addObserver(observer)

                                    if (hasLocationPermission) {
                                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
                                        try {
                                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                                if (location != null) {
                                                    val userPoint = GeoPoint(location.latitude, location.longitude)
                                                    selectedGeoPoint = userPoint
                                                }
                                            }
                                        } catch (e: SecurityException) {}
                                    }

                                    val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                            selectedGeoPoint = p
                                            editarViajeViewModel.onYCoordinateChange(p.latitude)
                                            editarViajeViewModel.onXCoordinateChange(p.longitude)
                                            searchLocationText = "Lat: ${String.format(Locale.US, "%.4f", p.latitude)}, Lng: ${String.format(Locale.US, "%.4f", p.longitude)}"
                                            return true
                                        }
                                        override fun longPressHelper(p: GeoPoint): Boolean = false
                                    })
                                    overlays.add(eventsOverlay)
                                }
                            },
                            update = { mapView ->
                                mapView.overlays.removeAll { it is Marker }
                                val marker = Marker(mapView).apply {
                                    position = selectedGeoPoint
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = "Destino seleccionado"
                                }
                                mapView.overlays.add(marker)
                                mapView.controller.animateTo(selectedGeoPoint)
                                mapView.invalidate()
                            }
                        )
                    }

                    Button(
                        onClick = {
                            editarViajeViewModel.onLocationChange(searchLocationText)
                            showLocationDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TravelPrimaryBlue)
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerView(onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit) {
    val state = rememberTimePickerState(is24Hour = true)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("OK", color = TravelPrimaryBlue) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        text = { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { TimePicker(state = state) } }
    )
}