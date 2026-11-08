
package com.example.ecomarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecomarket.ui.theme.EcoMarketTheme

private val EcoGreen = Color(0xFF267447)
private val EcoBackground = Color(0xFFF5F8F3)

data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val imagen: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EcoMarketTheme {
                EcoMarketApp()
            }
        }
    }
}

@Composable
fun EcoMarketApp() {

    val productos = listOf(
        Producto(1, "Botella reutilizable", "Accesorios", 12.50, ""),
        Producto(2, "Bolsa ecológica", "Hogar", 5.00, ""),
        Producto(3, "Cepillo de bambú", "Higiene", 4.50, ""),
        Producto(4, "Maceta reciclada", "Hogar", 8.00, ""),
        Producto(5, "Vaso reutilizable", "Accesorios", 6.75, ""),
        Producto(6, "Jabón natural", "Higiene", 3.50, "")
    )

    val categorias = listOf(
        "Todos", "Hogar", "Higiene", "Accesorios"
    )

    var busqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var seccionSeleccionada by remember { mutableStateOf("Inicio") }

    val productosFiltrados = productos.filter {
        val coincideNombre = it.nombre.contains(
            busqueda, ignoreCase = true
        )

        val coincideCategoria =
            categoriaSeleccionada == "Todos" ||
                    it.categoria == categoriaSeleccionada

        coincideNombre && coincideCategoria
    }

    Scaffold(
        containerColor = EcoBackground,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                listOf(
                    "Inicio" to "",
                    "Favoritos" to "",
                    "Perfil" to ""
                ).forEach { (nombre, emoji) ->

                    NavigationBarItem(
                        selected = seccionSeleccionada == nombre,
                        onClick = { seccionSeleccionada = nombre },
                        icon = { Text(emoji) },
                        label = { Text(nombre) }
                    )
                }
            }
        }
    ) { padding ->

        if (seccionSeleccionada == "Inicio") {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EcoGreen)
                        .padding(20.dp)
                ) {

                    Text(
                        text = "🌿 EcoMarket",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Compra mejor, vive más verde.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }

                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    placeholder = {
                        Text("Buscar productos...")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Text(
                    text = "Categorías",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263D2C),
                    modifier = Modifier.padding(
                        horizontal = 16.dp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                androidx.compose.foundation.lazy.LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categorias) { categoria ->

                        FilterChip(
                            selected = categoriaSeleccionada == categoria,
                            onClick = {
                                categoriaSeleccionada = categoria
                            },
                            label = { Text(categoria) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Productos destacados",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263D2C),
                    modifier = Modifier.padding(
                        horizontal = 16.dp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (productosFiltrados.isEmpty()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No encontramos productos.")
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {

                        items(productosFiltrados) { producto ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 6.dp
                                    ),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                )
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .size(76.dp)
                                            .background(
                                                Color(0xFFEAF3E9),
                                                RoundedCornerShape(14.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = producto.imagen,
                                            fontSize = 36.sp
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier.width(14.dp)
                                    )

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        Text(
                                            text = producto.nombre,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )

                                        Text(
                                            text = producto.categoria,
                                            color = Color.Gray,
                                            fontSize = 13.sp
                                        )

                                        Spacer(
                                            modifier = Modifier.height(8.dp)
                                        )

                                        Text(
                                            text = "$ ${"%.2f".format(
                                                java.util.Locale.US,
                                                producto.precio
                                            )}",
                                            color = EcoGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        )
                                    }

                                    Text(
                                        text = "♡",
                                        color = EcoGreen,
                                        fontSize = 27.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (seccionSeleccionada) {
                        "Favoritos" -> "Tus productos favoritos"
                        else -> "Mi perfil"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
