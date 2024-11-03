package com.example.testeandroid.data.models

import com.google.gson.annotations.SerializedName

data class UserRegistrationRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)