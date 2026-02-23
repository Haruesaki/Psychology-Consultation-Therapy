package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.test.Model.Psikolog
import com.example.test.Model.PsikologSource
import com.example.test.ui.theme.TestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        //name = "Android",
                        //modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestTheme {
        Greeting("Android")
    }
}

@Composable
fun Greeting() {

    val food = PsikologSource.dummyFood[0]   // ambil 1 data dari index ke-0

    Column(modifier = Modifier. fillMaxSize().padding(24.dp)){
        Text(text = "Nama: ${food.nama}")
        Text(text = "Deskripsi: ${food.deskripsi}")
        Text(text = "Harga: Rp ${food.harga}")
        Image(
            painter = painterResource(id = food.imageRes),
            contentDescription = "Gambar ${food.nama}",
            modifier = Modifier.padding(top = 8.dp) // Opsional: memberi jarak dengan teks di atasnya
        )
    }
}
