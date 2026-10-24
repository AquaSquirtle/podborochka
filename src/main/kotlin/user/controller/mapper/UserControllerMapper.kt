package org.podborochka.user.controller.mapper
import org.podborochka.user.application.model.CreateUserCommand
import org.podborochka.user.application.model.UpdateUserCommand
import org.podborochka.user.application.model.UserModel
import org.podborochka.user.controller.dto.CreateUserRequest
import org.podborochka.user.controller.dto.UpdateUserRequest
import org.podborochka.user.controller.dto.UserResponse
import org.springframework.stereotype.Component


@Component
class UserControllerMapper {

    fun toCreateCommand(request: CreateUserRequest): CreateUserCommand =
        CreateUserCommand(
            email = request.email,
            rawPassword = request.password,
            displayName = request.displayName
        )

    fun toUpdateCommand(request: UpdateUserRequest): UpdateUserCommand =
        UpdateUserCommand(
            email = request.email,
            rawPassword = request.password,
            displayName = request.displayName
        )

    fun toResponse(model: UserModel): UserResponse =
        UserResponse(
            id = model.id,
            email = model.email,
            displayName = model.displayName,
            role = model.role,
            avatarUrl = model.avatarUrl
        )
}