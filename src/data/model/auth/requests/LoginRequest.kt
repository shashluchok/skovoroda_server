package ru.skovoroda.data.model.auth.requests

data class LoginRequest(
    val email: String,
    val password: String
)