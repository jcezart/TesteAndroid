package com.example.testeandroid.data.network

import com.example.testeandroid.data.models.Book
import com.example.testeandroid.data.models.BooksResponse
import com.example.testeandroid.data.models.Category
import com.example.testeandroid.data.models.CreateBookRequest
import com.example.testeandroid.data.models.CreateBookResponse
import com.example.testeandroid.data.models.UploadFileResponse
import com.example.testeandroid.data.models.UserLoginRequest
import com.example.testeandroid.data.models.UserLoginResponse
import com.example.testeandroid.data.models.UserRegistrationRequest
import com.example.testeandroid.data.models.UserRegistrationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiService {
    @POST("users")
    suspend fun registerUser(@Body user: UserRegistrationRequest): UserRegistrationResponse

    @POST("auth/login")
    suspend fun loginUser(@Body loginRequest: UserLoginRequest): UserLoginResponse

    @GET("categories")
    suspend fun getCategories(
    ): List<Category>

    @GET("books/{id}")
    suspend fun getBookById(
        @Path("id") id: Int
    ): Book

    @GET("books")
    suspend fun getAllBooks(): BooksResponse

    @POST("books")
    suspend fun createBook(
        @Body createBookRequest: CreateBookRequest
    ): CreateBookResponse

    @DELETE("books/{id}")
    suspend fun deleteBook(@Path("id") bookId: Int): Response<Unit>

    @Multipart
    @POST("upload-file")
    suspend fun uploadFile(@Part file: MultipartBody.Part): UploadFileResponse
}