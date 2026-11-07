package com.example.agendacontactos

data class Contacto(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val favorito: Boolean = false
)
