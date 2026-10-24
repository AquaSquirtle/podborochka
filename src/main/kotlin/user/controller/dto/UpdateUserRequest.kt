package org.podborochka.user.controller.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UpdateUserRequest(

    @Email
    @Size(max = 255)
    val email: String? = null,

    @Size(max = 255)
    val password: String? = null,

    @Size(max = 100)
    val displayName: String? = null
)