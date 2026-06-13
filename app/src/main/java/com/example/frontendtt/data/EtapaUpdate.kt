package com.example.frontendtt.data

import kotlinx.serialization.Serializable

@Serializable
data class EtapaUpdate(
    val horainicio: String,
    val horafin: String,

)