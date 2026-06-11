package com.iessanalberto.dam2.gestionies.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.frontendtt.screens.*
import com.example.frontendtt.viewmodels.LoginViewModel
import com.example.traveltogethersupabase.network.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun AppNavigation() {
    val loginViewModel: LoginViewModel = viewModel()
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    // Usamos LaunchedEffect para monitorear el estado real de la sesión de Supabase
    LaunchedEffect(Unit) {
        supabase.auth.sessionStatus.collect { estado ->
            when (estado) {

                is SessionStatus.Authenticated -> {
                    // 1. ¡Aquí capturamos la sesión exitosa!
                    val sesion = estado.session
                    val usuario = sesion.user

                    // 2. Imprimimos TODO lo que contiene en el Logcat
                    Log.d("SUPABASE_DEBUG", "=== SESIÓN ENCONTRADA ===")
                    Log.d("SUPABASE_DEBUG", "ID de Usuario: ${usuario?.id}")
                    Log.d("SUPABASE_DEBUG", "Email: ${usuario?.email}")
                    Log.d("SUPABASE_DEBUG", "Metadatos del usuario: ${usuario?.userMetadata}")
                    Log.d("SUPABASE_DEBUG", "Token de Acceso (JWT): ${sesion.accessToken}")
                    Log.d("SUPABASE_DEBUG", "=========================")

                    // Redirigimos al menú
                    startDestination = AppScreens.MenuScreen.route
                }
                is SessionStatus.NotAuthenticated -> {
                    Log.d("SUPABASE_DEBUG", "No hay sesión activa o ha expirado.")
                    startDestination = AppScreens.LoginScreen.route
                }
                else -> {
                    // Por si acaso cambia a otro estado desconocido, asegurar ruta segura
                    //startDestination = AppScreens.LoginScreen.route
                }
            }
        }
    }
    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // Un spinner de carga nativo de Material 3
        }
    } else {
        NavHost(navController = navController, startDestination = startDestination!!) {
            composable(route = AppScreens.LoginScreen.route) {
                LoginScreen(navController = navController, loginViewModel = loginViewModel)
            }
            composable(route = AppScreens.RegisterScreen.route) {
                RegisterScreen(navController = navController)
            }
            composable(route = AppScreens.MenuScreen.route) {
                MenuScreen(navController = navController)
            }
            composable(route = AppScreens.ListaViajesScreen.route) {
                ListaViajesScreen(navController = navController)
            }
            composable(route = AppScreens.NuevoViajeScreen.route) {
                NuevoViajeScreen(navController = navController)
            }
            composable(
                route = AppScreens.ViajeScreen.route,
                arguments = listOf(
                    navArgument("viajeId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                // Extraemos el id de forma segura. Si por alguna razón es nulo, le ponemos 0 por defecto.
                val viajeId = backStackEntry.arguments?.getInt("viajeId") ?: 0

                // Se lo pasamos a tu pantalla
                ViajeScreen(navController = navController, viajeId = viajeId)
            }
            composable(
                route = AppScreens.EditarViajeScreen.route,
                arguments = listOf(
                    navArgument("viajeId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                // Extraemos el id de forma segura. Si por alguna razón es nulo, le ponemos 0 por defecto.
                val viajeId = backStackEntry.arguments?.getInt("viajeId") ?: 0
                EditarViajeScreen(navController = navController, viajeId = viajeId)
            }
        }
    }
}
