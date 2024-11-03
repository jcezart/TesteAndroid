package com.example.testeandroid.data.models

data class Book(
    val id: Int,
    val title: String,
    val summary: String?,
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