@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.frontendtt.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.frontendtt.R
import com.example.frontendtt.components.showDatePicker
import com.example.frontendtt.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.iessanalberto.dam2.gestionies.navigation.AppScreens
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

/* ---------------------------------------------------- */
/* 🧳 MODELO VIAJE DEMO */
/* ---------------------------------------------------- */

data class Trip(val title: String, val location: String, val dateStart: Long, val dateEnd: Long)

fun getTrips(start: String?, end: String?, locationFilter: String): List<Trip> {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val today = System.currentTimeMillis()
    val dayInMs = 24 * 60 * 60 * 1000L

    val allTrips = listOf(
        Trip("Escapada Montaña", "Pirineos", today - 10 * dayInMs, today - 5 * dayInMs),
        Trip("Ruta Costera", "Costa Mediterránea", today + 2 * dayInMs, today + 7 * dayInMs),
        Trip("Aventura Natural", "Asturias", today - 1 * dayInMs, today + 4 * dayInMs),
        Trip("Nieve y Sol", "Sierra Nevada", today + 20 * dayInMs, today + 25 * dayInMs),
        Trip("Ruta Gastronómica", "Galicia", today + 5 * dayInMs, today + 10 * dayInMs)
    )

    val filterStart = start?.let { try { sdf.parse(it)?.time } catch (e: Exception) { null } }
    val filterEnd = end?.let { try { sdf.parse(it)?.time } catch (e: Exception) { null } }

    return allTrips.filter { trip ->
        val matchesLocation = locationFilter.isBlank() || trip.location.contains(locationFilter, ignoreCase = true)
        val matchesDate = if (filterStart != null && filterEnd != null) {
            trip.dateStart <= filterEnd && trip.dateEnd >= filterStart
        } else {
            trip.dateEnd >= today
        }
        matchesLocation && matchesDate
    }
}

/* ---------------------------------------------------- */
/* 🌍 SCREEN PRINCIPAL */
/* ---------------------------------------------------- */

@Composable
fun MenuScreen(navController: NavController) {

    val context = LocalContext.current

    var startDate by remember { mutableStateOf<String?>(null) }
    var endDate by remember { mutableStateOf<String?>(null) }
    var locationFilter by remember { mutableStateOf("") }
    
    var showLocationDialog by remember { mutableStateOf(false) }
    var searchLocationText by remember { mutableStateOf("") }
    var rangeKm by remember { mutableFloatStateOf(10f) }

    val trips = remember(startDate, endDate, locationFilter) {
        getTrips(startDate, endDate, locationFilter)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppScreens.NuevoViajeScreen.route) },
                containerColor = ForestGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo viaje", tint = Color.White)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(TravelGradientTop, TravelGradientMiddle, TravelGradientBottom)
                    )
                )
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        BorderStroke(
                            3.dp,
                            Brush.horizontalGradient(




                                listOf(TravelPrimaryBlue, TravelDeepNavy)
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .clickable { navController.navigate(AppScreens.ListaViajesScreen.route) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tu_logo),
                    contentDescription = "Mis Viajes",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = TravelPrimaryBlue.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Mis Viajes",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
                    )
                }
            }

            /* 📅 SELECTOR FECHAS */
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDatePicker(context) { 
                                if (startDate == null || endDate != null) {
                                    startDate = it
                                    endDate = null
                                } else {
                                    endDate = it
                                }
                            }
                        }
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendario",
                        modifier = Modifier.size(44.dp),
                        tint = TravelPrimaryBlue
                    )
                    
                    Column {
                        Text(
                            "Selecciona fechas",
                            fontWeight = FontWeight.Bold,
                            color = TravelPrimaryBlue,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                        Text(
                            if (startDate == null) "Inicio" else startDate!!,
                            color = if (startDate == null) Color.Gray else TravelEarth
                        )
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_media_play),
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color.Gray
                            )
                        Text(
                            if (endDate == null) "Fin" else endDate!!,
                            color = if (endDate == null) Color.Gray else TravelEarth
                        )
                        }
                    }
                }
            }

            /* 📍 FILTRO UBICACIÓN Y RANGO */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    onClick = { 
                        searchLocationText = ""
                        showLocationDialog = true 
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TravelPrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = locationFilter.ifBlank { "Elegir ubicación" },
                            color = if (locationFilter.isBlank()) Color.Gray else Color.Black,
                            maxLines = 1
                        )
                    }
                }

                Column(
                    modifier = Modifier.width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${rangeKm.toInt()} KM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TravelPrimaryBlue
                    )
                    Slider(
                        value = rangeKm,
                        onValueChange = { rangeKm = it },
                        valueRange = 1f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = TravelPrimaryBlue,
                            activeTrackColor = TravelPrimaryBlue,
                            inactiveTrackColor = TravelPrimaryBlue.copy(alpha = 0.2f)
                        )
                    )
                }
            }

            /* 🧳 LISTA DE VIAJES */
            Text(
                "Viajes Propuestos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trips) { trip ->
                    Card(
                        onClick = { 
                            navController.navigate(AppScreens.ViajeScreen.route)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(trip.title, fontWeight = FontWeight.Bold)
                            Text(trip.location, color = TravelEarth)
                        }
                    }
                }
            }
        }
        
        if (showLocationDialog) {
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

                        OutlinedTextField(
                            value = searchLocationText,
                            onValueChange = { searchLocationText = it },
                            label = { Text("Nombre del lugar") },
                            placeholder = { Text("Ej: Playa de las Catedrales") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        val lifecycleOwner = LocalLifecycleOwner.current
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
                                        controller.setCenter(GeoPoint(42.5689, -0.5496)) // Jaca

                                        if (hasLocationPermission) {
                                            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
                                            try {
                                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                                    if (location != null) {
                                                        val userPoint = GeoPoint(location.latitude, location.longitude)
                                                        controller.animateTo(userPoint)
                                                    }
                                                }
                                            } catch (e: SecurityException) {}
                                        }
                                        
                                        val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                                            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                                searchLocationText = "Lat: ${String.format(Locale.US, "%.4f", p.latitude)}, Lng: ${String.format(Locale.US, "%.4f", p.longitude)}"
                                                overlays.removeAll { it is Marker }
                                                val marker = Marker(this@apply)
                                                marker.position = p
                                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                                marker.title = "Ubicación seleccionada"
                                                overlays.add(marker)
                                                invalidate()
                                                return true
                                            }
                                            override fun longPressHelper(p: GeoPoint): Boolean = false
                                        })
                                        overlays.add(eventsOverlay)
                                    }
                                },
                                update = { mapView ->
                                    val observer = LifecycleEventObserver { _, event ->
                                        when (event) {
                                            Lifecycle.Event.ON_RESUME -> mapView.onResume()
                                            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                                            else -> {}
                                        }
                                    }
                                    lifecycleOwner.lifecycle.addObserver(observer)
                                }
                            )
                        }

                        Button(
                            onClick = { 
                                locationFilter = searchLocationText
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
}
