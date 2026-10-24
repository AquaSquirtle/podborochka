package org.podborochka.user.application.mapper

import org.podborochka.user.application.model.UpdateUserCommand
import org.podborochka.user.infrastructure.entity.UserEntity
import org.springframework.stereotype.Component

@Component
class UserApplicationMapper {

    fun applyUpdate(
        entity: UserEntity,
        command: UpdateUserCommand,
        encodedPassword: String?
    ) {
        command.email?.let { entity.email = it }
        command.displayName?.let { entity.displayName = it }
        encodedPassword?.let { entity.passwordHash = it }

    }
}