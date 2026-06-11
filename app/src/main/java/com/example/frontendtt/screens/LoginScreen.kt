@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.frontendtt.screens
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.frontendtt.R
import com.example.frontendtt.components.PrimaryTravelButton
import com.example.frontendtt.components.SecondaryTravelButton
import com.example.frontendtt.ui.theme.*
import com.example.frontendtt.viewmodels.LoginViewModel
import com.iessanalberto.dam2.gestionies.navigation.AppScreens
// import com.example.frontendtt.viewmodels.LoginViewModel
import com.example.traveltogethersupabase.network.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlin.time.Duration

@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel) {
    // val loginViewModel: LoginViewModel = viewModel()
    val loginState by loginViewModel.loginState.collectAsState()
    
    var showLogin by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var intentoEnviar by remember { mutableStateOf(false) }
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
    val errorPassword = intentoEnviar && (loginState.password.isBlank() || loginState.password.length<6)
    val errorEmail = intentoEnviar && (loginState.correo.isBlank() || !loginState.correo.matches(emailRegex))
    Scaffold(
        containerColor = TravelSkyBlue
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(TravelGradientTop, TravelGradientMiddle, TravelGradientBottom)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /* -------------------------------------------------------
               TERCIO SUPERIOR — LOGO
            ------------------------------------------------------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logott),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                )
            }

            /* -------------------------------------------------------
               CUERPO DINÁMICO
            ------------------------------------------------------- */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (!showLogin) {
                    /* ---------------- VISTA INICIAL ---------------- */
                    PrimaryTravelButton(
                        text = "Acceder",
                        onClick = { showLogin = true }
                    )

                    SecondaryTravelButton(
                        text = "Registrarse",
                        onClick = { navController.navigate(AppScreens.RegisterScreen.route) }
                    )
                } else {
                    /* ---------------- FORMULARIO LOGIN ---------------- */

                    Text(
                        text = "Bienvenido viajero ✈️",
                        fontSize = 22.sp,
                        color = TravelPrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = loginState.correo,
                        onValueChange = { loginViewModel.onCorreoChange(it) },
                        label = { Text("Usuario") },
                        isError = errorEmail,
                        supportingText = {
                            if (errorEmail) {
                                    val mensaje = if (loginState.correo.isBlank()) "El correo es obligatorio" else "El formato no es válido"
                                Text(mensaje, color = MaterialTheme.colorScheme.error)

                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TravelPrimaryBlue,
                            unfocusedBorderColor = TravelPrimaryBlue.copy(alpha = 0.3f),
                            focusedLabelColor = TravelPrimaryBlue,
                            cursorColor = TravelPrimaryBlue,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = loginState.password,
                        onValueChange = { loginViewModel.onPasswordChange(it) },
                        label = { Text("Contraseña") },
                        isError = errorPassword,
                        supportingText = {
                            if (errorPassword) {
                                val mensaje = if (loginState.password.isBlank()) "La contraseña es obligatoria" else "La contraseña tiene que tener mínimo 6 caracteres"
                                Text(mensaje, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TravelPrimaryBlue,
                            unfocusedBorderColor = TravelPrimaryBlue.copy(alpha = 0.3f),
                            focusedLabelColor = TravelPrimaryBlue,
                            cursorColor = TravelPrimaryBlue,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    PrimaryTravelButton(
                        text = "Aceptar",
                        onClick = {
                            intentoEnviar = true
                            val todoEsValido = loginState.password.isNotBlank() && loginState.password.length>=6 && loginState.correo.isNotBlank() && loginState.correo.matches(emailRegex)

                            if (todoEsValido) {
                                scope.launch {
                                    try {
                                        supabase.auth.signInWith(Email) {
                                            email = loginState.correo
                                            password = loginState.password
                                        }
                                        Log.d(
                                            "AUTH",
                                            "session=${supabase.auth.currentSessionOrNull()}"
                                        )

                                        Log.d(
                                            "AUTH",
                                            "user=${supabase.auth.currentUserOrNull()}"
                                        )
                                        navController.navigate(AppScreens.MenuScreen.route)
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error al loguearse",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
            }
                    )

                    SecondaryTravelButton(
                        text = "Registrarse",
                        onClick = {  navController.navigate(AppScreens.RegisterScreen.route) }
                    )
                }
            }
        }
    }
}
