package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.test.Model.Psikolog
import com.example.test.Model.PsikologSource
import com.example.test.ui.theme.TestTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() } // Snackbar di level terluar

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) } // Host snackbar tunggal
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController as androidx.navigation.NavHostController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            ListPsikolog(navController = navController, snackbarHostState = snackbarHostState)
        }
        composable("detail/{psikologId}") { backStackEntry ->
            val psikologId = backStackEntry.arguments?.getString("psikologId")
            val psikolog = PsikologSource.dataDiri.find { it.nama == psikologId }
            if (psikolog != null) {
                // Di halaman detail, isDetailView = true
                DetailScreen(
                    psikolog = psikolog,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    isDetailView = true
                )
            }
        }
    }
}

@Composable
fun ListPsikolog(navController: NavController, snackbarHostState: SnackbarHostState) {
    val profil = PsikologSource.dataDiri
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Psikolog Terpopuler", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(profil) { psikolog ->
                    PsikologRowItem(psikolog = psikolog, navController = navController)
                }
            }
        }

        item {
            Text("Psikolog Lainnya", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        items(profil) { psikolog ->
            // isDetailView = false agar muncul tombol "Lihat Detail"
            DetailScreen(
                psikolog = psikolog,
                navController = navController,
                snackbarHostState = snackbarHostState,
                isDetailView = false
            )
        }
    }
}

@Composable
fun DetailScreen(    psikolog: Psikolog,
                     navController: NavController,
                     snackbarHostState: SnackbarHostState,
                     isDetailView: Boolean
) {
    var isFavorite by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    // TAMBAHKAN STATE INI:
    var isOrdered by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box {
                Image(
                    painter = painterResource(id = psikolog.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(psikolog.nama, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(psikolog.deskripsi, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(12.dp))

            if (isDetailView) {
                // Tombol di halaman Detail sesungguhnya
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    // MODIFIKASI: Button mati jika sedang loading ATAU sudah dipesan
                    enabled = !isLoading && !isOrdered,
                    colors = ButtonDefaults.buttonColors(
                        // MODIFIKASI: Jika sudah dipesan, warna berubah jadi Silver (LightGray)
                        containerColor = if (isOrdered) Color.LightGray else MaterialTheme.colorScheme.primary,
                        contentColor = if (isOrdered) Color.DarkGray else MaterialTheme.colorScheme.onPrimary,
                        // Menangani warna saat disabled agar tetap terlihat silver
                        disabledContainerColor = if (isOrdered) Color.LightGray else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        disabledContentColor = if (isOrdered) Color.DarkGray else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
                    onClick = {
                        scope.launch {
                            isLoading = true
                            delay(1500)
                            isLoading = false
                            isOrdered = true // SET TRUE SETELAH BERHASIL
                            snackbarHostState.showSnackbar("Berhasil memesan ${psikolog.nama}")
                        }
                    }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        // MODIFIKASI: Teks berubah jika sudah dipesan
                        Text(if (isOrdered) "Pesanan Berhasil" else "Konfirmasi Pesanan")
                    }
                }
            } else {
                // Tombol di halaman List (Home)
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate("detail/${psikolog.nama}") }
                ) {
                    Text("Lihat Detail")
                }
            }
        }
    }
}

@Composable
fun PsikologRowItem(psikolog: Psikolog, navController: NavController) {
    Card(
        modifier = Modifier.width(160.dp),
        onClick = { navController.navigate("detail/${psikolog.nama}") }
    ) {
        Column {
            Image(
                painter = painterResource(psikolog.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                psikolog.nama,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}