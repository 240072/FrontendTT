@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.frontendtt.screens

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
import com.example.frontendtt.R
import com.example.frontendtt.components.PrimaryTravelButton
import com.example.frontendtt.components.SecondaryTravelButton
import com.example.frontendtt.ui.theme.*
import com.example.frontendtt.viewmodels.LoginViewModel
import com.iessanalberto.dam2.gestionies.navigation.AppScreens

@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel) {

    var showLogin by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                        value = user,
                        onValueChange = { user = it },
                        label = { Text("Usuario") },
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
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
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
                        onClick = { navController.navigate(AppScreens.MenuScreen.route) }
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
