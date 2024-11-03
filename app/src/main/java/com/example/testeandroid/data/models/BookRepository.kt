package com.example.testeandroid.data.models

import com.example.testeandroid.data.network.ApiService

class BookRepository(private val apiService: ApiService) {

    suspend fun deleteBook(bookId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteBook(bookId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Erro ao deletar o livro."
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}