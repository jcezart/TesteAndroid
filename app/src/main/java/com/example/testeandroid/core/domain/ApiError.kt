package com.example.testeandroid.core.domain

data class ApiError(
    val code: String,
    val expected: String,
    val received: String,
    val path: List<String>,
    val message: String
)
