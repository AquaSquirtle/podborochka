package org.podborochka.auth.controller.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(

    @Email
    @NotBlank
    @Size(max = 255)
    val email: String,

    @NotBlank
    @Size(max = 255)
    val password: String
)