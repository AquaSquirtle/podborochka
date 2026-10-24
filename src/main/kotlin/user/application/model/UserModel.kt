package org.podborochka.user.application.model
import org.podborochka.user.domain.UserRole
import java.time.LocalDateTime

data class UserModel(
    val id: Long,
    val email: String,
    val passwordHash: String,
    val displayName: String,
    val avatarUrl: String?,
    val role: UserRole,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)