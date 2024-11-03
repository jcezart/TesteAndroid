// HomeViewModel.kt
package com.example.testeandroid

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import com.google.gson.Gson
import android.util.Log

class HomeViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService
    val createBookResult = MutableLiveData<Result<CreateBookResponse>>()

    // LiveData para observar o resultado das categorias
    val categoriesResult = MutableLiveData<Result<List<Category>>>()

    // LiveData para observar o resultado dos livros
    val booksResult = MutableLiveData<Result<List<Book>>>()

    // Função para buscar categorias
    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val categories = apiService.getCategories()
                categoriesResult.postValue(Result.success(categories))
            } catch (e: IOException) {
                Log.e("HomeViewModel", "Network Error: ${e.localizedMessage}")
                categoriesResult.postValue(Result.failure(Exception("Erro de rede: ${e.localizedMessage}")))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("HomeViewModel", "HTTP Error Body: $errorBody")
                val errorResponse = errorBody?.let {
                    try {
                        Gson().fromJson(it, ErrorResponse::class.java)
                    } catch (ex: Exception) {
                        null
                    }
                }
                val errorMessage = errorResponse?.error?.joinToString(", ") { it.message }
                    ?: "Erro HTTP ${e.code()}: ${e.message()}"
                Log.e("HomeViewModel", "HTTP Error: $errorMessage")
                categoriesResult.postValue(Result.failure(Exception(errorMessage)))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected Error: ${e.localizedMessage}")
                categoriesResult.postValue(Result.failure(Exception("Erro: ${e.localizedMessage}")))
            }
        }
    }

    // Função para buscar todos os livros
    fun fetchBooks() {
        viewModelScope.launch {
            try {
                val booksResponse = apiService.getAllBooks()
                booksResult.postValue(Result.success(booksResponse.data))
            } catch (e: IOException) {
                Log.e("HomeViewModel", "Network Error: ${e.localizedMessage}")
                booksResult.postValue(Result.failure(Exception("Erro de rede: ${e.localizedMessage}")))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("HomeViewModel", "HTTP Error Body: $errorBody")
                val errorResponse = errorBody?.let {
                    try {
                        Gson().fromJson(it, ErrorResponse::class.java)
                    } catch (ex: Exception) {
                        null
                    }
                }
                val errorMessage = errorResponse?.error?.joinToString(", ") { it.message }
                    ?: "Erro HTTP ${e.code()}: ${e.message()}"
                Log.e("HomeViewModel", "HTTP Error: $errorMessage")
                booksResult.postValue(Result.failure(Exception(errorMessage)))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected Error: ${e.localizedMessage}")
                booksResult.postValue(Result.failure(Exception("Erro: ${e.localizedMessage}")))
            }
        }
    }

    // Função para buscar um livro por ID (Opcional)
    fun fetchBookById(id: Int) {
        viewModelScope.launch {
            try {
                val book = apiService.getBookById(id)
                // Neste exemplo, vamos adicionar o livro em uma lista existente
                val currentBooks = booksResult.value?.getOrNull() ?: emptyList()
                booksResult.postValue(Result.success(currentBooks + book))
            } catch (e: IOException) {
                Log.e("HomeViewModel", "Network Error: ${e.localizedMessage}")
                booksResult.postValue(Result.failure(Exception("Erro de rede: ${e.localizedMessage}")))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("HomeViewModel", "HTTP Error Body: $errorBody")
                val errorResponse = errorBody?.let {
                    try {
                        Gson().fromJson(it, ErrorResponse::class.java)
                    } catch (ex: Exception) {
                        null
                    }
                }
                val errorMessage = errorResponse?.error?.joinToString(", ") { it.message }
                    ?: "Erro HTTP ${e.code()}: ${e.message()}"
                Log.e("HomeViewModel", "HTTP Error: $errorMessage")
                booksResult.postValue(Result.failure(Exception(errorMessage)))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected Error: ${e.localizedMessage}")
                booksResult.postValue(Result.failure(Exception("Erro: ${e.localizedMessage}")))
            }
        }
    }

    fun createBook(createBookRequest: CreateBookRequest) {
        viewModelScope.launch {
            try {
                val response = apiService.createBook(createBookRequest)
                Log.d("HomeViewModel", "Create Book Response: $response")
                createBookResult.postValue(Result.success(response))
            } catch (e: IOException) {
                Log.e("HomeViewModel", "Network Error: ${e.localizedMessage}")
                createBookResult.postValue(Result.failure(Exception("Erro de rede: ${e.localizedMessage}")))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("HomeViewModel", "HTTP Error Body: $errorBody")
                val errorResponse = errorBody?.let {
                    try {
                        Gson().fromJson(it, ErrorResponse::class.java)
                    } catch (ex: Exception) {
                        null
                    }
                }
                val errorMessage = errorResponse?.error?.joinToString(", ") { it.message }
                    ?: "Erro HTTP ${e.code()}: ${e.message()}"
                Log.e("HomeViewModel", "HTTP Error: $errorMessage")
                createBookResult.postValue(Result.failure(Exception(errorMessage)))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected Error: ${e.localizedMessage}")
                createBookResult.postValue(Result.failure(Exception("Erro: ${e.localizedMessage}")))
            }
        }
    }
}
