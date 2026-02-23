package com.example.test.Model
import androidx.annotation.DrawableRes
data class Psikolog(
    val nama: String,
    val deskripsi: String,
    val harga: Int,
    @DrawableRes val imageRes: Int
)
