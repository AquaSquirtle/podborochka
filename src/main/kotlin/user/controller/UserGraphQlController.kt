package org.podborochka.user.controller
import org.podborochka.common.security.AppUserPrincipal
import org.podborochka.graphql.dto.UpdateUserInputGql
import org.podborochka.user.application.service.UserService
import org.podborochka.user.controller.dto.UpdateUserRequest
import org.podborochka.user.controller.mapper.UserControllerMapper
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller

@Controller
class UserGraphQlController(
    private val userService: UserService,
    private val userControllerMapper: UserControllerMapper
) {

    @QueryMapping
    fun me(@AuthenticationPrincipal principal: AppUserPrincipal) =
        userControllerMapper.toResponse(userService.getById(principal.id))

    @MutationMapping
    fun updateMe(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Argument input: UpdateUserInputGql
    ) = userControllerMapper.toResponse(
        userService.update(
            principal.id,
            userControllerMapper.toUpdateCommand(
                UpdateUserRequest(
                    email = input.email,
                    password = input.password,
                    displayName = input.displayName
                )
            )
        )
    )
}