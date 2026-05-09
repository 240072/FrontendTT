package com.iessanalberto.dam2.gestionies.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontendtt.screens.*
import com.example.frontendtt.viewmodels.LoginViewModel

@Composable
fun AppNavigation() {
    val loginViewModel: LoginViewModel = viewModel()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route) {
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
        composable(route = AppScreens.ViajeScreen.route) { 
            ViajeScreen(navController = navController) 
        }
        composable(route = AppScreens.EditarViajeScreen.route) { 
            EditarViajeScreen(navController = navController) 
        }
    }
}
