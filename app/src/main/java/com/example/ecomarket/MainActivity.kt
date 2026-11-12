package com.example.ecomarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ecomarket.model.Product
import com.example.ecomarket.model.FavoriteProductEntity
import com.example.ecomarket.ui.theme.EcoMarketTheme
import com.example.ecomarket.utils.traducirProducto
import com.example.ecomarket.utils.traducirCategoria
import com.example.ecomarket.utils.TraductorAutomatico
import com.example.ecomarket.viewmodel.ProductViewModel
import java.util.Locale

private val EcoGreen = Color(0xFF267447)
private val EcoBackground = Color(0xFFF5F8F3)

// Convertir favorito guardado en Room a producto
private fun FavoriteProductEntity.toProduct(): Product {
    return Product(
        id = id,
        title = title,
        description = description,
        category = category,
        price = price,
        thumbnail = thumbnail,
        rating = rating,
        stock = stock
    )
}

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
fun EcoMarketApp(
    productViewModel: ProductViewModel = viewModel()
) {

    val productos by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val error by productViewModel.error.collectAsState()

    val favoritos by productViewModel.favorites.collectAsState(
        initial = emptyList()
    )

    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
    }

    val categorias = remember(productos) {
        listOf("Todos") + productos
            .map { it.category }
            .distinct()
            .sorted()
    }

    var busqueda by remember { mutableStateOf("") }

    var categoriaSeleccionada by remember {
        mutableStateOf("Todos")
    }

    var seccionSeleccionada by remember {
        mutableStateOf("Inicio")
    }

    var productoSeleccionado by remember {
        mutableStateOf<Product?>(null)
    }

    val productosFiltrados = productos.filter { producto ->

        val nombreTraducido = traducirProducto(producto.title)

        val coincideNombre =
            producto.title.contains(busqueda, ignoreCase = true) ||
                    nombreTraducido.contains(busqueda, ignoreCase = true) ||
                    producto.description.contains(busqueda, ignoreCase = true)

        val coincideCategoria =
            categoriaSeleccionada == "Todos" ||
                    producto.category == categoriaSeleccionada

        coincideNombre && coincideCategoria
    }

    Scaffold(
        containerColor = EcoBackground,

        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {

                listOf(
                    "Inicio",
                    "Favoritos",
                    "Mi perfil"
                ).forEach { nombre ->

                    NavigationBarItem(
                        selected = seccionSeleccionada == nombre,
                        onClick = {
                            seccionSeleccionada = nombre
                        },
                        icon = {
                            Text(
                                when (nombre) {
                                    "Inicio" -> "🏠"
                                    "Favoritos" -> "♥"
                                    else -> "👤"
                                }
                            )
                        },
                        label = {
                            Text(nombre)
                        }
                    )
                }
            }
        }

    ) { padding ->

        when (seccionSeleccionada) {

            // PANTALLA PRINCIPAL
            "Inicio" -> {

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
                        onValueChange = {
                            busqueda = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 16.dp
                            ),
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
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(categorias) { categoria ->

                            FilterChip(
                                selected = categoriaSeleccionada == categoria,
                                onClick = {
                                    categoriaSeleccionada = categoria
                                },
                                label = {
                                    Text(
                                        if (categoria == "Todos") "Todos"
                                        else traducirCategoria(categoria)
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Productos destacados",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263D2C),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    when {

                        isLoading -> {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    CircularProgressIndicator(
                                        color = EcoGreen
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text("Cargando productos...")
                                }
                            }
                        }

                        error != null -> {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Text(
                                    text = error ?: "Error desconocido",
                                    color = Color.Red
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        productViewModel.loadProducts()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EcoGreen
                                    )
                                ) {
                                    Text("Reintentar")
                                }
                            }
                        }

                        productosFiltrados.isEmpty() -> {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No encontramos productos.")
                            }
                        }

                        else -> {

                            LazyColumn(
                                modifier = Modifier.weight(1f)
                            ) {

                                items(
                                    items = productosFiltrados,
                                    key = { it.id }
                                ) { producto ->

                                    val esFavorito by productViewModel
                                        .isFavorite(producto.id)
                                        .collectAsState(initial = false)

                                    ProductCard(
                                        producto = producto,
                                        esFavorito = esFavorito,
                                        onFavoriteClick = {
                                            if (esFavorito) {
                                                productViewModel.removeFavorite(producto.id)
                                            } else {
                                                productViewModel.addFavorite(producto)
                                            }
                                        },
                                        onProductClick = {
                                            productoSeleccionado = producto
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // PANTALLA DE FAVORITOS
            "Favoritos" -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {

                    Text(
                        text = "♥ Mis favoritos",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreen,
                        modifier = Modifier.padding(16.dp)
                    )

                    if (favoritos.isEmpty()) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "♡",
                                    fontSize = 55.sp,
                                    color = EcoGreen
                                )

                                Text(
                                    text = "Todavía no tienes favoritos",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = "Agrega productos tocando el corazón.",
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                        }

                    } else {

                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            items(
                                items = favoritos,
                                key = { it.id }
                            ) { favorito ->

                                ProductCard(
                                    producto = favorito.toProduct(),
                                    esFavorito = true,
                                    onFavoriteClick = {
                                        productViewModel.removeFavorite(favorito.id)
                                    },
                                    onProductClick = {
                                        productoSeleccionado = favorito.toProduct()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // PANTALLA MI PERFIL
            else -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(EcoBackground)
                        .padding(padding)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(60.dp))
                            .background(Color(0xFFDCEEDD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "",
                            fontSize = 55.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "Usuario EcoMarket",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreen
                    )

                    Text(
                        text = "Bienvenido a tu espacio ecológico",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                text = "Mi cuenta",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreen
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(" Nombre: Usuario EcoMarket")

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Aplicación: EcoMarket")

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Plataforma: Tienda ecológica")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                text = "Mi actividad",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreen
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "♥  Productos favoritos: ${favoritos.size}",
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    Text(
                        text = "EcoMarket © 2026",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "Compra mejor, vive más verde.",
                        color = EcoGreen,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    // Ventana de detalle del producto seleccionado
    productoSeleccionado?.let { producto ->

        val esFavorito by productViewModel
            .isFavorite(producto.id)
            .collectAsState(initial = false)

        ProductDetailDialog(
            producto = producto,
            esFavorito = esFavorito,
            onDismiss = {
                productoSeleccionado = null
            },
            onFavoriteClick = {
                if (esFavorito) {
                    productViewModel.removeFavorite(producto.id)
                } else {
                    productViewModel.addFavorite(producto)
                }
            }
        )
    }
}

// TARJETA DE PRODUCTO
@Composable
fun ProductCard(
    producto: Product,
    esFavorito: Boolean,
    onFavoriteClick: () -> Unit,
    onProductClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 6.dp
            )
            .clickable { onProductClick() },
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

            AsyncImage(
                model = producto.thumbnail,
                contentDescription = traducirProducto(producto.title),
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEAF3E9)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = traducirProducto(producto.title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = traducirCategoria(producto.category),
                    color = Color.Gray,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Precio: $ ${String.format(Locale.US, "%.2f", producto.price)}",
                    color = EcoGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "Disponibilidad: ${producto.stock} unidades",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Text(
                    text = "Calificación: ${producto.rating}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            // Botón de favoritos
            IconButton(
                onClick = onFavoriteClick
            ) {

                Text(
                    text = if (esFavorito) "♥" else "♡",
                    color = if (esFavorito) Color.Red else Color.Gray,
                    fontSize = 30.sp
                )
            }
        }
    }
}

// VENTANA DE DETALLE DEL PRODUCTO
@Composable
fun ProductDetailDialog(
    producto: Product,
    esFavorito: Boolean,
    onDismiss: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    var descripcionTraducida by remember(producto.id) {
        mutableStateOf("Traduciendo descripción...")
    }

    LaunchedEffect(producto.id, producto.description) {
        descripcionTraducida =
            TraductorAutomatico.traducir(producto.description)
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 650.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AsyncImage(
                    model = producto.thumbnail,
                    contentDescription = traducirProducto(producto.title),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFEAF3E9)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = traducirProducto(producto.title),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreen
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = traducirCategoria(producto.category),
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "$ ${String.format(Locale.US, "%.2f", producto.price)}",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreen
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 15.dp)
                )

                Text(
                    text = "Descripción del producto",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = descripcionTraducida,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Calificación: ${producto.rating}",
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Disponibilidad: ${producto.stock} unidades",
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onFavoriteClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoGreen
                    )
                ) {
                    Text(
                        if (esFavorito) {
                            "Quitar de favoritos"
                        } else {
                            "♡ Agregar a favoritos"
                        }
                    )
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver")
                }
            }
        }
    }
}