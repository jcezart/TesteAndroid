package com.example.testeandroid

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class UserRegistrationRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)

data class UserRegistrationResponse(
    val id: Int,
    val name: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String

)

data class ErrorResponse(
    val error: List<ApiError> = emptyList()
)

data class ApiError(
    val code: String,
    val expected: String,
    val received: String,
    val path: List<String>,
    val message: String
)

data class UserLoginRequest(
    @SerializedName("credential") val credential: String,
    @SerializedName("password") val password: String
)

data class UserLoginResponse(
    val token: String,
    val user: UserRegistrationResponse
)

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
}