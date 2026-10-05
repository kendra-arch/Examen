package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

// ==========================================
// 1. MODELO DE CLASES (Principios SOLID)
// ==========================================

// Principio Abierto/Cerrado (OCP): Clase base abstracta.
abstract class ProductoDeportivo(
    open val id: Int,
    open val nombre: String,
    open val precio: Double,
    open var stock: Int
)

data class Calzado(
    override val id: Int,
    override val nombre: String,
    override val precio: Double,
    override var stock: Int,
    val talla: Double,
    val tipo: String
) : ProductoDeportivo(id, nombre, precio, stock)

data class Ropa(
    override val id: Int,
    override val nombre: String,
    override val precio: Double,
    override var stock: Int,
    val talla: String,
    val material: String
) : ProductoDeportivo(id, nombre, precio, stock)

// Principio de Responsabilidad Única (SRP): Aislar la gestión de la lista.
class GestorInventario {
    private val productos = mutableListOf<ProductoDeportivo>()
    fun registrarProducto(producto: ProductoDeportivo) {
        productos.add(producto)
    }
    fun obtenerInventario(): List<ProductoDeportivo> {
        return productos.toList()
    }
}

// ==========================================
// 2. ACTIVIDAD PRINCIPAL
// ==========================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // setContent define el contenido de la pantalla usando funciones @Composable
        setContent {
            MyApplicationTheme {
                // Surface es el contenedor base que aplica el color de fondo del tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos a nuestro componente principal de navegación en lugar de Greeting
                    AppTiendaDeportiva()
                }
            }
        }
    }
}

// ==========================================
// 3. DISEÑO DE PANTALLAS (Jetpack Compose)
// ==========================================

@Composable
fun AppTiendaDeportiva() {
    // Inicializamos datos de prueba manteniendo el estado con remember
    val inventario = remember {
        GestorInventario().apply {
            registrarProducto(Calzado(1, "Tenis Running X", 450.0, 10, 42.0, "Running"))
            registrarProducto(Calzado(2, "Botines Fútbol Y", 550.0, 5, 40.5, "Fútbol"))
            registrarProducto(Ropa(3, "Camiseta Térmica", 120.0, 20, "M", "Poliéster"))
            registrarProducto(Ropa(4, "Pantalón Buzo", 180.0, 15, "L", "Algodón"))
        }
    }

    // Configuración de Navegación
    val navController = rememberNavController()

    // NavHost define las rutas y pantallas (destinos) de la aplicación
    NavHost(navController = navController, startDestination = "pantalla_calzado") {

        // composable define cada pantalla dentro del grafo de navegación
        composable(route = "pantalla_calzado") {
            val calzados = inventario.obtenerInventario().filterIsInstance<Calzado>()
            PantallaListado(
                titulo = "Catálogo de Calzados",
                productos = calzados,
                onNavigate = { navController.navigate("pantalla_ropa") },
                textoBotonNavegacion = "Ver Ropa"
            )
        }

        composable(route = "pantalla_ropa") {
            val ropa = inventario.obtenerInventario().filterIsInstance<Ropa>()
            PantallaListado(
                titulo = "Catálogo de Ropa",
                productos = ropa,
                onNavigate = { navController.navigate("pantalla_calzado") },
                textoBotonNavegacion = "Ver Calzados"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListado(
    titulo: String,
    productos: List<ProductoDeportivo>,
    onNavigate: () -> Unit,
    textoBotonNavegacion: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onNavigate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(textoBotonNavegacion)
            }
        }
    ) { paddingValues ->
        // LazyColumn es equivalente a un RecyclerView, renderiza solo lo visible
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(productos, key = { it.id }) { producto ->
                TarjetaProducto(producto)
            }
        }
    }
}

@Composable
fun TarjetaProducto(producto: ProductoDeportivo) {
    // Card proporciona un contenedor con elevación y bordes redondeados
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Column apila los textos verticalmente
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Precio: Bs. ${producto.precio}")
            Text(text = "Stock: ${producto.stock} unidades")

            // Renderizado condicional según el tipo de clase hija
            when (producto) {
                is Calzado -> {
                    Text(text = "Talla: ${producto.talla} | Tipo: ${producto.tipo}")
                }
                is Ropa -> {
                    Text(text = "Talla: ${producto.talla} | Material: ${producto.material}")
                }
            }
        }
    }
}
// 1. Clase Base Abstracta (Aplica OCP)
abstract class ProductoDeportivo(
    open val id: Int,
    open val nombre: String,
    open val precio: Double,
    open var stock: Int // Cambiado de 'cant' a 'stock' por semántica
)

// 2. Clases Hijas
data class Calzado(
    override val id: Int,
    override val nombre: String,
    override val precio: Double,
    override var stock: Int,
    val talla: Double,
    val tipo: String // Ej: "Running", "Fútbol"
) : ProductoDeportivo(id, nombre, precio, stock)

data class Ropa(
    override val id: Int,
    override val nombre: String,
    override val precio: Double,
    override var stock: Int,
    val talla: String, // String es mejor para ropa (S, M, L, XL)
    val material: String
) : ProductoDeportivo(id, nombre, precio, stock)

// 3. Entidades Adicionales
data class Cliente(
    val id: Int,
    val nombre: String
)

// 4. Gestor (Aplica SRP)
class GestorInventario {
    // Lista mutable para almacenar el inventario
    private val productos = mutableListOf<ProductoDeportivo>()

    fun registrarProducto(producto: ProductoDeportivo) {
        productos.add(producto)
    }

    fun obtenerInventario(): List<ProductoDeportivo> {
        return productos.toList() // Retorna lista inmutable por seguridad
    }
}