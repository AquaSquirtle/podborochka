package org.podborochka.auth.controller.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(

    @Email
    @NotBlank
    @Size(max = 255)
    val email: String,

    @NotBlank
    @Size(min = 6, max = 255)
    val password: String,

    @NotBlank
    @Size(max = 100)
    val displayName: String
)