package org.podborochka.user.infrastructure.mapper

import org.podborochka.user.application.model.UserModel
import org.podborochka.user.infrastructure.entity.UserEntity
import org.springframework.stereotype.Component

@Component
class UserPersistenceMapper {

    fun toModel(entity: UserEntity): UserModel =
        UserModel(
            id = requireNotNull(entity.id),
            email = entity.email,
            passwordHash = entity.passwordHash,
            displayName = entity.displayName,
            avatarUrl = entity.avatarUrl,
            role = entity.role,
            createdAt = requireNotNull(entity.createdAt),
            updatedAt = requireNotNull(entity.updatedAt)
        )

    fun toNewEntity(
        email: String,
        passwordHash: String,
        displayName: String
    ): UserEntity =
        UserEntity(
            email = email,
            passwordHash = passwordHash,
            displayName = displayName
        )
}