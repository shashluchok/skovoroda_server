package ru.skovoroda.data.model.auth.requests

data class RegisterRequest (
        val name:String,
        val email:String,
        val password:String
)