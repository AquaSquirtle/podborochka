package org.podborochka.user.application.model
data class UpdateUserCommand(
    val email: String? = null,
    val rawPassword: String? = null,
    val displayName: String? = null
)