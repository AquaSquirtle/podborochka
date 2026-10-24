package org.podborochka.auth.controller.dto

data class AuthResponse(
    val id: Long,
    val email: String,
    val displayName: String,
    val role: String
)