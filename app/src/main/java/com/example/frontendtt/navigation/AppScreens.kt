package com.iessanalberto.dam2.gestionies.navigation

sealed class AppScreens (val route: String){
    object LoginScreen: AppScreens (route = "login_screen")
    object RegisterScreen: AppScreens (route = "register_screen")
    object ViajeScreen: AppScreens (route = "viaje_screen/{viajeId}")
    object MenuScreen: AppScreens (route = "menu_screen")
    object ListaViajesScreen: AppScreens (route = "lista_viajes_screen")
    object NuevoViajeScreen: AppScreens (route = "nuevo_viaje_screen")
    object EditarViajeScreen: AppScreens (route = "editar_viaje_screen")
}
