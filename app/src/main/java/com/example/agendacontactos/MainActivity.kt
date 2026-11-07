package com.example.agendacontactos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AgendaPantalla()
            }
        }
    }
}

@Composable
fun AgendaPantalla() {

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var siguienteId by remember { mutableIntStateOf(1) }
    var mensajeError by remember { mutableStateOf("") }

    val context = LocalContext.current
    val contactos = remember {
        mutableStateListOf<Contacto>()
    }

    Scaffold(
        containerColor = Color(0xFFF3F6FC)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Encabezado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = Color(0xFF6750A4), shape = RoundedCornerShape(16.dp)
                        ), contentAlignment = Alignment.Center
                ) {

                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_contactos
                        ),
                        contentDescription = "Icono de contactos",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )

                }

                Text(
                    text = "Mi Agenda",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263B83)
                )
            }

            Text(
                text = "Organiza tus contactos fácilmente", color = Color.DarkGray, fontSize = 15.sp
            )

            // Formulario
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono, onValueChange = { nuevoTelefono ->
                    telefono = nuevoTelefono.filter { it.isDigit() }.take(10)
                }, label = { Text("Número de teléfono") }, keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ), singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            Button(

                onClick = {
                    if (nombre.isBlank() || telefono.isBlank()) {
                        mensajeError = "Complete ambos campos"

                    } else if (!telefono.matches(Regex("09[0-9]{8}"))) {
                        mensajeError = "El celular debe tener 10 dígitos y comenzar con 09"

                    } else {
                        contactos.add(
                            Contacto(
                                id = siguienteId++,
                                nombre = nombre.trim(),
                                telefono = telefono.trim()
                            )
                        )

                        nombre = ""
                        telefono = ""
                        mensajeError = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Agregar contacto")
            }

            if (mensajeError.isNotEmpty()) {
                Text(
                    text = mensajeError, color = MaterialTheme.colorScheme.error
                )
            }

            HorizontalDivider()

            Text(
                text = "Total de contactos: ${contactos.size}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )


            if (contactos.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .background(
                            color = Color(0xFFEDE7F6), shape = RoundedCornerShape(20.dp)
                        )
                        .padding(24.dp), contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_contactos),
                            contentDescription = null,
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(44.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Tu agenda está vacía",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF30234F)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Agrega tu primer contacto utilizando el formulario de arriba.",
                            fontSize = 14.sp,
                            color = Color(0xFF656078),
                            textAlign = TextAlign.Center
                        )
                    }
                }

            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = contactos, key = { it.id }) { contacto ->
                        ContactoCard(contacto = contacto, onFavoritoClick = {
                            val indice = contactos.indexOfFirst {
                                it.id == contacto.id
                            }
                            if (indice != -1) {
                                contactos[indice] = contacto.copy(
                                    favorito = !contacto.favorito
                                )
                            }
                        }, onEliminarClick = {
                            contactos.remove(contacto)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ContactoCard(
    contacto: Contacto, onFavoritoClick: () -> Unit, onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        Color(0xFF6750A4), CircleShape
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contacto.nombre.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contacto.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
                Text(
                    text = contacto.telefono, fontSize = 13.sp, color = Color.DarkGray
                )
            }

            TextButton(onClick = onFavoritoClick) {
                Text(
                    text = if (contacto.favorito) "★" else "☆",
                    fontSize = 26.sp,
                    color = if (contacto.favorito) Color(0xFFFFB300)
                    else Color.Gray
                )
            }

            TextButton(onClick = onEliminarClick) {
                Text(
                    text = "Eliminar", color = Color(0xFFB3261E), fontSize = 12.sp
                )
            }
        }
    }
}

