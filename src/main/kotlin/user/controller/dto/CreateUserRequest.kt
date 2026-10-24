package org.podborochka.user.controller.dto
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size


data class CreateUserRequest(
    @NotBlank
    @Email
    @Size(max = 255)
    val email: String,

    @NotBlank
    @Size(max = 255)
    val password: String,

    @NotBlank
    @Size(max = 100)
    val displayName: String
)