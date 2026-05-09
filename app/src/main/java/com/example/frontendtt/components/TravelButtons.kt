package com.example.frontendtt.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontendtt.ui.theme.TravelPrimaryBlue
import com.example.frontendtt.ui.theme.TravelSkyBlue

@Composable
fun PrimaryTravelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.75f)
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TravelPrimaryBlue,
            contentColor = Color.White
        ),
        border = BorderStroke(2.dp, TravelSkyBlue)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun SecondaryTravelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, TravelPrimaryBlue),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            color = TravelPrimaryBlue,
            fontWeight = FontWeight.Bold
        )
    }
}
