package org.podborochka.user.controller.dto

import org.podborochka.user.domain.UserRole


data class UserResponse(
    val id: Long,
    val email: String,
    val displayName: String,
    val role: UserRole,
    val avatarUrl: String?
)