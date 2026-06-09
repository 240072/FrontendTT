package com.iessanalberto.dam2.gestionies.navigation

import android.util.Log
import androidx.compose.runtime.Composable
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

@Composable
fun AppNavigation() {
    val loginViewModel: LoginViewModel = viewModel()
    val navController = rememberNavController()
        val startDestination = if (supabase.auth.currentUserOrNull() == null){
AppScreens.LoginScreen.route
    } else {
        AppScreens.MenuScreen.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
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
        composable(route = AppScreens.ViajeScreen.route,
            arguments = listOf(
                navArgument("viajeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            // Extraemos el id de forma segura. Si por alguna razón es nulo, le ponemos 0 por defecto.
            val viajeId = backStackEntry.arguments?.getInt("viajeId") ?: 0

            // Se lo pasamos a tu pantalla
            ViajeScreen(navController = navController, viajeId = viajeId)
        }
        composable(route = AppScreens.EditarViajeScreen.route) { 
            EditarViajeScreen(navController = navController) 
        }
    }
}
