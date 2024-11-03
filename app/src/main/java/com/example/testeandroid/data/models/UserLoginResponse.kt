package com.example.testeandroid.data.models

data class UserLoginResponse(
    val token: String,
    val user: UserRegistrationResponse
)
