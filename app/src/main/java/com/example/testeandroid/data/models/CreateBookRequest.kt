package com.example.testeandroid.data.models

data class CreateBookRequest(
    val title: String,
    val summary : String?,
    val author: String,
    val imageUrl: String?,
    val categoryId: Int
)


data class CreateBookResponse(
    val id: Int,
    val title: String,
    val summary: String?,
    val author: String,
    val imageUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val category: Category
)