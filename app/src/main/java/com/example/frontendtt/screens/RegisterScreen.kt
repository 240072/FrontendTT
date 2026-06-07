@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
package com.example.frontendtt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.SmokeFree
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log

import com.example.frontendtt.ui.theme.*
import com.iessanalberto.dam2.gestionies.navigation.AppScreens
import com.example.traveltogethersupabase.data.Tabaco.opcionesTabaco
import com.example.traveltogethersupabase.data.Mascota.opcionesMascota
import com.example.frontendtt.viewmodels.LoginViewModel
import com.example.traveltogethersupabase.network.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Composable
fun RegisterScreen(navController: NavController) {

    val loginViewModel: LoginViewModel = viewModel()
    val loginState by loginViewModel.loginState.collectAsState()
    

    
    val mascotaSeleccionada = loginState.petOption

    
    val tabacoSeleccionado = loginState.tobaccoOption
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(TravelGradientTop, TravelGradientMiddle, TravelGradientBottom)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Únete a la Aventura",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TravelPrimaryBlue,
                    fontWeight = FontWeight.Bold
                )

                /* 🧑‍💻 FORMULARIO PRINCIPAL */
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = loginState.name,
                            onValueChange = { loginViewModel.onNameChange(it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = loginState.alias,
                            onValueChange = { loginViewModel.onAliasChange(it) },
                            label = { Text("Alias") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = loginState.correo,
                            onValueChange = { loginViewModel.onCorreoChange(it) },
                            label = { Text("Correo") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = loginState.password,
                            onValueChange = { loginViewModel.onPasswordChange(it) },
                            label = { Text("Contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                /* 🐾 SECCIÓN MASCOTAS */
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Mascotas",
                                style = MaterialTheme.typography.titleLarge,
                                color = TravelEarth,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Pets, contentDescription = null, tint = TravelEarth, modifier = Modifier.size(20.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            opcionesMascota.forEach { (texto, valor) ->
    FilterChip(
        selected = (valor == mascotaSeleccionada),
        onClick = {
            loginViewModel.onPetOptionChange(
                if (mascotaSeleccionada == valor) null else valor
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
                }

                /* 🚬 SECCIÓN TABACO */
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Tabaco",
                                style = MaterialTheme.typography.titleLarge,
                                color = TravelEarth,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.SmokeFree, contentDescription = null, tint = TravelEarth, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
           opcionesTabaco.forEach { (texto, valor) ->
    FilterChip(
        selected = (valor == tabacoSeleccionado),
        onClick = {
            loginViewModel.onTobaccoOptionChange(
                if (tabacoSeleccionado == valor) null else valor
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
                }


                Button(
                    onClick = { scope.launch {
                    try {
                        // 1. Registrar en Auth (Esto crea el UUID en el esquema privado)
                        val user = supabase.auth.signUpWith(Email) {
                            email = loginState.correo
                            password = loginState.password

                            // PASO CRÍTICO: Enviar metadatos para que el Trigger de SQL los reciba
                            data = buildJsonObject {
                                put("alias", loginState.alias)
                                put("nombre", loginState.name)
                                put("tabaco", tabacoSeleccionado)
                                put("mascota", mascotaSeleccionada)
                            }
                        }

                        // 2. ¿Necesitas llamar a enviarRegistro()?
                        // Si configuraste el TRIGGER que te pasé antes en SQL,
                        // ¡YA NO ES NECESARIO! El Trigger lo hace solo.

                        // Si NO usas trigger, tendrías que hacerlo así:
                        /*
                        val userId = user?.id ?: return@launch
                        val nuevoUsuario = RegistroUsuario(
                            id = userId, // Usamos el UUID real
                            alias = loginState.alias,
                            ...
                        )
                        enviarRegistro(nuevoUsuario)
                        */

                        Log.i("TAG","Registro exitoso para: ${user?.email}")

    } catch (e: io.github.jan.supabase.exceptions.RestException) {
    // .toString() en las excepciones de Supabase suele formatear el JSON del error completo
    Log.e("TAG", "RestException detectado: ${e.toString()}")
    Log.e("TAG", "Mensaje del error: ${e.message}")
} catch (e: Exception) {
    Log.e("TAG", "Error inesperado no relacionado con Supabase", e)
}
                } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TravelPrimaryBlue)
                ) {
                    Text("Finalizar Registro", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
