package com.example.testeandroid.presentation.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import com.google.gson.Gson
import android.util.Log
import com.example.testeandroid.data.models.Book
import com.example.testeandroid.data.models.Category
import com.example.testeandroid.data.models.CreateBookRequest
import com.example.testeandroid.data.models.CreateBookResponse
import com.example.testeandroid.core.domain.ErrorResponse
import com.example.testeandroid.core.data.networking.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class HomeViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService
    val createBookResult = MutableLiveData<Result<CreateBookResponse>>()

    // LiveData para observar o resultado das categorias
    val categoriesResult = MutableLiveData<Result<List<Category>>>()

    // LiveData para observar o resultado dos livros
    val booksResult = MutableLiveData<Result<List<Book>>>()

    // LiveData para observar o resultado da deleção
    val deleteBookResult = MutableLiveData<Result<Unit>>()

    // LiveData para observar o resultado dos detalhes do livro
    val bookDetailResult = MutableLiveData<Result<Book>>()

    // LiveData para observar o resultado do upload da imagem
    val uploadImageResult = MutableLiveData<Result<String>>()

    // Função para fazer o upload da imagem
    fun uploadImage(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageFile = getFileFromUri(context, imageUri)
                if (imageFile != null) {
                    val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val multipartBody = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

                    // Chamar o método uploadFile
                    val uploadResponse = apiService.uploadFile(multipartBody)
                    val uploadedImageUrl = uploadResponse.url

                    if (uploadedImageUrl != null) {
                        // Postar o resultado bem-sucedido
                        uploadImageResult.postValue(Result.success(uploadedImageUrl))
                    } else {
                        // Tratar o caso onde a URL é nula
                        uploadImageResult.postValue(Result.failure(Exception("URL da imagem é nula.")))
                    }
                } else {
                    uploadImageResult.postValue(Result.failure(Exception("Falha ao converter Uri para File.")))
                }
            } catch (e: IOException) {
                uploadImageResult.postValue(Result.failure(Exception("Erro de rede: ${e.localizedMessage}")))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = errorBody ?: "Erro HTTP ${e.code()}: ${e.message()}"
                uploadImageResult.postValue(Result.failure(Exception(errorMessage)))
            } catch (e: Exception) {
                uploadImageResult.postValue(Result.failure(Exception("Erro: ${e.localizedMessage}")))
            }
        }
    }

    // Função auxiliar para converter Uri em File
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".tmp", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Função para criar o livro após o upload da imagem
    fun createBookWithImage(createBookRequest: CreateBookRequest, context: Context, imageUri: Uri) {
        // Primeiro, fazer o upload da imagem
        uploadImage(context, imageUri)

        // Observar o resultado do upload
        uploadImageResult.observeForever { result ->
            result.fold(onSuccess = { imageUrl ->
                // Atualizar o request com a URL da imagem
                val updatedRequest = createBookRequest.copy(imageUrl = imageUrl)
                // Chamar a função para criar o livro
                createBook(updatedRequest)
                // Remover o observer para evitar chamadas múltiplas
                uploadImageResult.removeObserver {  }
            }, onFailure = { error ->
                // Tratar o erro do upload
                createBookResult.postValue(Result.failure(error))
                uploadImageResult.removeObserver {  }
            })
        }
    }

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
                bookDetailResult.postValue(Result.success(book))
            } catch (e: IOException) {
                Log.e("HomeViewModel", "Network Error: ${e.localizedMessage}")
                bookDetailResult.postValue(Result.failure(Exception("Erro de rede: ${e.localizedMessage}")))
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
                bookDetailResult.postValue(Result.failure(Exception(errorMessage)))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected Error: ${e.localizedMessage}")
                bookDetailResult.postValue(Result.failure(Exception("Erro: ${e.localizedMessage}")))
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

    fun deleteBook(bookId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteBook(bookId)
                if (response.isSuccessful) {
                    Log.d("HomeViewModel", "Livro deletado com sucesso")
                    deleteBookResult.postValue(Result.success(Unit))
                    // Atualize a lista de livros após a deleção
                    fetchBooks()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("HomeViewModel", "Erro na resposta HTTP: $errorBody")
                    val errorResponse = errorBody?.let {
                        try {
                            Gson().fromJson(it, ErrorResponse::class.java)
                        } catch (ex: Exception) {
                            null
                        }
                    }
                    val errorMessage = errorResponse?.error?.joinToString(", ") { it.message }
                        ?: "Erro HTTP ${response.code()}: ${response.message()}"
                    Log.e("HomeViewModel", "Erro HTTP: $errorMessage")
                    deleteBookResult.postValue(Result.failure(Exception(errorMessage)))
                }
            } catch (e: IOException) {
                Log.e("HomeViewModel", "Erro de rede: ${e.localizedMessage}")
                deleteBookResult.postValue(Result.failure(Exception("Erro de rede: ${e.localizedMessage}")))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("HomeViewModel", "Erro na resposta HTTP: $errorBody")
                val errorResponse = errorBody?.let {
                    try {
                        Gson().fromJson(it, ErrorResponse::class.java)
                    } catch (ex: Exception) {
                        null
                    }
                }
                val errorMessage = errorResponse?.error?.joinToString(", ") { it.message }
                    ?: "Erro HTTP ${e.code()}: ${e.message()}"
                Log.e("HomeViewModel", "Erro HTTP: $errorMessage")
                deleteBookResult.postValue(Result.failure(Exception(errorMessage)))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro inesperado: ${e.localizedMessage}")
                deleteBookResult.postValue(Result.failure(Exception("Erro: ${e.localizedMessage}")))
            }
        }
    }
}

