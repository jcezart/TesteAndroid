package com.example.testeandroid.core.domain

data class ErrorResponse(
    val error: List<ApiError> = emptyList()
)