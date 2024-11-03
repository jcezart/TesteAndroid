package com.example.testeandroid.data.models
import com.google.gson.annotations.SerializedName

data class UserLoginRequest(
    @SerializedName("credential") val credential: String,
    @SerializedName("password") val password: String
)
