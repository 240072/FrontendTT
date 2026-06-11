@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.frontendtt.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
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
import com.example.frontendtt.data.NuevaEtapa
import com.example.frontendtt.data.NuevoDestino
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.Duration

data class DestinoViaje(
    val id: Int,
    val nombre: String,
    val hora: String,
    val ubicacion: String,
    val descripcion: String,
    val dificultad: String,
    val diaAsociado: Int
)

@Composable
fun EditarViajeScreen(navController: NavController, viajeId: Int) {
    val editarViajeViewModel : EditarViajeViewModel = viewModel()
    val editarViajeState by editarViajeViewModel.editarViajeState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    //var cantidadDias = remember{ editarViajeViewModel.diasState}
    //val cantidadDias = 4
    //val diasDisponibles = List(cantidadDias) { "Día ${it + 1}" }
    val diasDisponibles = editarViajeViewModel.diasDisponiblesState
    var diaSeleccionadoIndex by remember { mutableIntStateOf(0) }

    var listaDestinos by remember { mutableStateOf(mutableListOf<DestinoViaje>()) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var destinoAEditar by remember { mutableStateOf<DestinoViaje?>(null) }
    var destinoAEliminar by remember { mutableStateOf<DestinoViaje?>(null) }
    var destinoAVer by remember { mutableStateOf<DestinoViaje?>(null) }

    val destinosFiltrados = listaDestinos.filter { it.diaAsociado == diaSeleccionadoIndex }
    LaunchedEffect(viajeId) {
        editarViajeViewModel.getTripInfo(viajeId)
    }
    val viaje = editarViajeViewModel.viajeState
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
                                selected = diaSeleccionadoIndex == index,
                                onClick = { diaSeleccionadoIndex = index },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TravelPrimaryBlue, selectedLabelColor = Color.White),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }

                Text("Destinos del día", modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(destinosFiltrados) { destino ->
                        DestinoItem(
                            nombre = destino.nombre,
                            hora = destino.hora,
                            ubicacion = destino.ubicacion,
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
                    onClick = { scope.launch { snackbarHostState.showSnackbar("Viaje guardado") } },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TravelDeepNavy)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Viaje", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (mostrarFormulario) {
            FormularioDestinoDialog(
                destinoExistente = destinoAEditar,
                onDismiss = { mostrarFormulario = false },
                onSave = { nuevoDestino ->
                    if (destinoAEditar == null) {
                        listaDestinos.add(nuevoDestino.copy(id = listaDestinos.size + 1, diaAsociado = diaSeleccionadoIndex))
                        scope.launch { snackbarHostState.showSnackbar("Destino agregado correctamente") }
                    } else {
                        val index = listaDestinos.indexOfFirst { it.id == destinoAEditar!!.id }
                        listaDestinos[index] = nuevoDestino
                        scope.launch { snackbarHostState.showSnackbar("Cambios guardados") }
                    }
                    mostrarFormulario = false
                },
                viajeId = viajeId
            )
        }

        if (destinoAVer != null) {
            AlertDialog(
                onDismissRequest = { destinoAVer = null },
                confirmButton = { TextButton(onClick = { destinoAVer = null }) { Text("Cerrar", color = TravelPrimaryBlue, fontWeight = FontWeight.Bold) } },
                icon = { Icon(Icons.Default.Info, contentDescription = null, tint = TravelDeepNavy) },
                title = { Text(destinoAVer!!.nombre, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp), tint = Color.Gray); Spacer(Modifier.width(8.dp)); Text(destinoAVer!!.hora) }
                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = Color.Gray); Spacer(Modifier.width(8.dp)); Text(destinoAVer!!.ubicacion) }
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                        Text("Descripción:", fontWeight = FontWeight.Bold); Text(destinoAVer!!.descripcion)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Dificultad: ", fontWeight = FontWeight.Bold)
                            val (emoji, color) = when (destinoAVer!!.dificultad) {
                                "Alta" -> "🔴" to Color.Red
                                "Media" -> "🟠" to Color(0xFFF57C00)
                                "Baja" -> "🟢" to TravelPrimaryBlue
                                else -> "⚪" to Color.Gray
                            }
                            Text("$emoji ${destinoAVer!!.dificultad}", color = color, fontWeight = FontWeight.Bold)
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
                        listaDestinos.removeIf { it.id == destinoAEliminar?.id }
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
fun FormularioDestinoDialog(destinoExistente: DestinoViaje?, onDismiss: () -> Unit, onSave: (DestinoViaje) -> Unit, viajeId: Int) {
    val editarViajeViewModel : EditarViajeViewModel = viewModel()
    val editarViajeState by editarViajeViewModel.editarViajeState.collectAsState()
    var nombre by remember { mutableStateOf(destinoExistente?.nombre ?: "") }
    val horasIniciales = destinoExistente?.hora?.split(" - ")
    var horaInicio by remember { mutableStateOf(horasIniciales?.getOrNull(0) ?: "08:00") }
    var horaFin by remember { mutableStateOf(horasIniciales?.getOrNull(1) ?: "09:00") }
    var ubicacion by remember { mutableStateOf(destinoExistente?.ubicacion ?: "") }
    var descripcion by remember { mutableStateOf(destinoExistente?.descripcion ?: "") }
    val dificultades = listOf("Baja", "Media", "Alta")
    //var dificultadSeleccionada by remember { mutableStateOf(destinoExistente?.dificultad ?: "Media") }
    var dificultadSeleccionada = editarViajeState.dificultad
    var showInicioPicker by remember { mutableStateOf(false) }
    var showFinPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
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

                OutlinedTextField(value = ubicacion, onValueChange = { }, readOnly = true, label = { Text("Ubicación") }, modifier = Modifier.fillMaxWidth().clickable { searchLocationText = ""; showLocationDialog = true }, enabled = false, trailingIcon = { Icon(Icons.Default.LocationOn, null, tint = TravelPrimaryBlue) })
                editarViajeState.descripcion?.let { OutlinedTextField(value = it, onValueChange = { editarViajeViewModel.onDescriptionChange(it) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3) }

                Text("Dificultad", fontWeight = FontWeight.Bold)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    opcionesDificultad.forEach { (index, diff) ->
                        SegmentedButton(selected = (index == dificultadSeleccionada), onClick = { editarViajeViewModel.onDificultyChange(if (dificultadSeleccionada == index) 1 else index) }, shape = SegmentedButtonDefaults.itemShape(index = index, count = opcionesDificultad.size)) { Text(diff, fontSize = 10.sp) }
                    }
                }

                Button(
                    //onClick = { if (nombre.isNotBlank()) onSave(DestinoViaje(destinoExistente?.id ?: 0, nombre, "$horaInicio - $horaFin", ubicacion, descripcion, dificultadSeleccionada, 0)) },
                    onClick = {
                        val duration = Duration.between(LocalTime.parse(editarViajeState.horainicio),LocalTime.parse(editarViajeState.horafin)).toHours()
                        val idDestination = editarViajeViewModel.insertDestination(
                        NuevoDestino(editarViajeState.nombre,editarViajeState.descripcion, editarViajeState.coordx,editarViajeState.coordy,editarViajeState.dificultad))
                              editarViajeViewModel.insertStage(NuevaEtapa(viajeId, idDestination ?:0, editarViajeState.horainicio,duration.toInt()))},
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TravelPrimaryBlue)
                ) { Text(if (destinoExistente == null) "Agregar" else "Guardar") }
            }
        }
    }

    if (showInicioPicker) { TimePickerView(onDismiss = { showInicioPicker = false }, onConfirm = { h, m -> editarViajeViewModel.onInitialHourChange(String.format(Locale.US, "%02d:%02d", h, m)); showInicioPicker = false }) }
    if (showFinPicker) { TimePickerView(onDismiss = { showFinPicker = false }, onConfirm = { h, m -> editarViajeViewModel.onFinalHourChange(String.format(Locale.US, "%02d:%02d", h, m)); showFinPicker = false }) }

    if (showLocationDialog) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1. Estado para guardar la coordenada seleccionada (empieza en Jaca por defecto)
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

    // Función interna para buscar el texto en la base de datos de mapas
    fun buscarLugar(query: String) {
        if (query.isBlank()) return
        
        // El Geocoder hace una petición de red, por lo que usamos Dispatchers.IO
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val geocoder = android.location.Geocoder(context, Locale.getDefault())
                // Buscamos solo el primer resultado más preciso
                val resultados = geocoder.getFromLocationName(query, 1)
                
                if (!resultados.isNullOrEmpty()) {
                    val direccion = resultados[0]
                    editarViajeViewModel.onXCoordinateChange(direccion.longitude)
                    editarViajeViewModel.onYCoordinateChange(direccion.latitude)
                    Log.d("Point", editarViajeState.coordx.toString())
                    Log.d("Point", editarViajeState.coordy.toString())
                    val nuevoPunto = GeoPoint(direccion.latitude, direccion.longitude)
                    
                    // Volvemos al hilo principal para actualizar la UI de Compose
                    withContext(Dispatchers.Main) {
                        selectedGeoPoint = nuevoPunto
                        // Opcional: Actualiza el texto con el nombre oficial encontrado
                        searchLocationText = direccion.getAddressLine(0) ?: query
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Error de red o lugar no encontrado
            }
        }
    }

    Dialog(onDismissRequest = { showLocationDialog = false }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cabecera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Elegir Destino",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TravelPrimaryBlue
                    )
                    IconButton(onClick = { showLocationDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                // Buscador optimizado con botón de buscar y acción de teclado
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

                // Contenedor del Mapa
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

                                // CORRECCIÓN: El ciclo de vida se registra aquí UNA sola vez para evitar fugas de memoria
                                val observer = LifecycleEventObserver { _, event ->
                                    when (event) {
                                        Lifecycle.Event.ON_RESUME -> onResume()
                                        Lifecycle.Event.ON_PAUSE -> onPause()
                                        else -> {}
                                    }
                                }
                                lifecycleOwner.lifecycle.addObserver(observer)

                                // Si hay permiso de GPS, centra la pantalla en el usuario al abrir
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

                                // Listener para cuando el usuario toca manualmente cualquier punto del mapa
                                val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                        selectedGeoPoint = p
                                        searchLocationText = "Lat: ${String.format(Locale.US, "%.4f", p.latitude)}, Lng: ${String.format(Locale.US, "%.4f", p.longitude)}"
                                        return true
                                    }
                                    override fun longPressHelper(p: GeoPoint): Boolean = false
                                })
                                overlays.add(eventsOverlay)
                            }
                        },
                        update = { mapView ->
                            // Cada vez que cambia 'selectedGeoPoint', refrescamos el marcador y movemos la cámara
                            mapView.overlays.removeAll { it is Marker }
                            
                            val marker = Marker(mapView).apply {
                                position = selectedGeoPoint
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Destino seleccionado"
                            }
                            
                            mapView.overlays.add(marker)
                            mapView.controller.animateTo(selectedGeoPoint)
                            mapView.invalidate() // Fuerza el redibujado del mapa
                        }
                    )
                }

                // Botón Confirmar
                Button(
                    onClick = { 
                        // Te recomiendo guardar el string descriptivo, pero recuerda que el valor real está en 'selectedGeoPoint'
                        //locationFilter = searchLocationText
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
    // if (showLocationDialog) {
    //     var hasLocationPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) }
    //     val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasLocationPermission = it }
    //     LaunchedEffect(Unit) { if (!hasLocationPermission) launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }

    //     Dialog(onDismissRequest = { showLocationDialog = false }) {
    //         Card(modifier = Modifier.fillMaxWidth().height(550.dp), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
    //             Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    //                 Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
    //                     Text("Elegir Ubicación", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TravelPrimaryBlue)
    //                     IconButton(onClick = { showLocationDialog = false }) { Icon(Icons.Default.Close, null) }
    //                 }
    //                 OutlinedTextField(value = searchLocationText, onValueChange = { searchLocationText = it }, label = { Text("Nombre del lugar") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

    //                 val lifecycleOwner = LocalLifecycleOwner.current
    //                 Box(modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(20.dp)).background(Color(0xFFEEEEEE)).border(1.dp, Color.LightGray, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
    //                     AndroidView<MapView>(factory = { ctx ->
    //                         Configuration.getInstance().userAgentValue = ctx.packageName
    //                         MapView(ctx).apply {
    //                             setTileSource(TileSourceFactory.MAPNIK); setMultiTouchControls(true); controller.setZoom(15.0)
    //                             val defaultPoint = GeoPoint(42.5689, -0.5496); controller.setCenter(defaultPoint)
    //                             if (hasLocationPermission) {
    //                                 val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
    //                                 try { fusedLocationClient.lastLocation.addOnSuccessListener { if (it != null) controller.animateTo(GeoPoint(it.latitude, it.longitude)) } } catch (e: SecurityException) {}
    //                             }
    //                             overlays.add(MapEventsOverlay(object : MapEventsReceiver {
    //                                 override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
    //                                     searchLocationText = "Lat: ${String.format(Locale.US, "%.4f", p.latitude)}, Lng: ${String.format(Locale.US, "%.4f", p.longitude)}"
    //                                     overlays.removeAll { it is Marker }; val marker = Marker(this@apply); marker.position = p; marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); overlays.add(marker); invalidate(); return true
    //                                 }
    //                                 override fun longPressHelper(p: GeoPoint): Boolean = false
    //                             }))
    //                         }
    //                     }, update = { mv ->
    //                         val observer = LifecycleEventObserver { _, e -> when (e) { Lifecycle.Event.ON_RESUME -> mv.onResume(); Lifecycle.Event.ON_PAUSE -> mv.onPause(); else -> {} } }
    //                         lifecycleOwner.lifecycle.addObserver(observer)
    //                     })
    //                 }
    //                 Button(onClick = { ubicacion = searchLocationText; showLocationDialog = false }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = TravelPrimaryBlue)) { Text("Confirmar") }
    //             }
    //         }
    //     }
    // }
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
