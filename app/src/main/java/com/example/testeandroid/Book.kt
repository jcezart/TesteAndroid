package com.example.testeandroid

import com.google.gson.annotations.SerializedName

data class Book(
    val id: Int,
    val title: String,
    val summary: String?, // Campo opcional
    val author: String,
    val imageUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val category: Category
)

data class BooksResponse(
    val data: List<Book>,
    val totalItems: Int,
    val totalPages: Int,
    val itemsPerPage: Int,
    val page: Int
)