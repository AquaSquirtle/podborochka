package org.podborochka.user.application.model
data class CreateUserCommand(
    val email: String,
    val rawPassword: String,
    val displayName: String
)