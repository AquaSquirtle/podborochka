package org.podborochka.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.podborochka.auth.controller.dto.AuthResponse
import org.podborochka.auth.service.AuthService
import org.podborochka.common.security.AppUserPrincipal
import org.podborochka.common.web.dto.PageResponse
import org.podborochka.common.web.mapper.PageResponseMapper
import org.podborochka.common.web.utils.PaginationUtils
import org.podborochka.user.application.service.UserService
import org.podborochka.user.controller.dto.CreateUserRequest
import org.podborochka.user.controller.dto.UpdateUserRequest
import org.podborochka.user.controller.dto.UserResponse
import org.podborochka.user.controller.mapper.UserControllerMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/users")
@Tag(
    name = "Users",
    description = "CRUD операции над пользователями и доступ к текущему авторизованному пользователю"
)
class UserController(
    private val userService: UserService,
    private val userControllerMapper: UserControllerMapper,
    private val authService: AuthService,
    private val pageResponseMapper: PageResponseMapper
) {

    @Operation(
        summary = "Создать пользователя",
        description = """
            Создает нового пользователя.
            
            Обычно в пользовательском сценарии вместо этого endpoint'а используется /api/v1/auth/register,
            однако данный метод позволяет создать пользователя через общий CRUD API.
        """,
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Пользователь создан"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса"),
            ApiResponse(responseCode = "409", description = "Пользователь уже существует")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    fun create(
        @Valid @RequestBody request: CreateUserRequest
    ): UserResponse {
        val command = userControllerMapper.toCreateCommand(request)
        val model = userService.create(command)
        return userControllerMapper.toResponse(model)
    }

    @Operation(
        summary = "Получить список пользователей",
        description = """
            Возвращает страницу пользователей.
            
            Поддерживает пагинацию:
            - page — номер страницы, начиная с 0
            - size — количество элементов на странице
            
            Используется для административного просмотра пользователей или внутреннего API.
        """,
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            ApiResponse(responseCode = "400", description = "Некорректные параметры пагинации")
        ]
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAll(
        @Parameter(description = "Номер страницы, начиная с 0")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Размер страницы")
        @RequestParam(defaultValue = "20") size: Int
    ): PageResponse<UserResponse> {
        val pageable = PaginationUtils.pageable(page, size)
        val result = userService.getAll(pageable)
        return pageResponseMapper.toPageResponse(result, userControllerMapper::toResponse)
    }

    @Operation(
        summary = "Получить пользователя по id",
        description = "Возвращает пользователя по его идентификатору",
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Пользователь найден"),
            ApiResponse(responseCode = "404", description = "Пользователь не найден")
        ]
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getById(
        @Parameter(description = "Идентификатор пользователя")
        @PathVariable id: Long
    ): UserResponse {
        val model = userService.getById(id)
        return userControllerMapper.toResponse(model)
    }

    @Operation(
        summary = "Обновить пользователя",
        description = """
            Обновляет данные пользователя.
            
            Может использоваться для изменения email, displayName и других полей,
            если это разрешено бизнес-логикой приложения.
        """,
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса"),
            ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            ApiResponse(responseCode = "409", description = "Конфликт данных")
        ]
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(
        @Parameter(description = "Идентификатор пользователя")
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest
    ): UserResponse {
        val command = userControllerMapper.toUpdateCommand(request)
        val model = userService.update(id, command)
        return userControllerMapper.toResponse(model)
    }

    @Operation(
        summary = "Удалить пользователя",
        description = "Удаляет пользователя по его идентификатору",
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Пользователь удален"),
            ApiResponse(responseCode = "404", description = "Пользователь не найден")
        ]
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(
        @Parameter(description = "Идентификатор пользователя")
        @PathVariable id: Long
    ) {
        userService.delete(id)
    }

    @Operation(
        summary = "Получить текущего пользователя",
        description = """
            Возвращает информацию о текущем авторизованном пользователе.
            
            Используется фронтендом для определения состояния сессии,
            отображения профиля и настройки интерфейса.
        """,
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Информация о текущем пользователе возвращена"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        ]
    )
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun me(
        @AuthenticationPrincipal principal: AppUserPrincipal
    ): UserResponse {
        val model = userService.getById(principal.id)
        return userControllerMapper.toResponse(model)
    }


    @Operation(
        summary = "Обновить текущего пользователя",
        description = """
        Обновляет данные текущего авторизованного пользователя.
        
        Позволяет изменить параметры профиля, такие как имя, email или другие доступные поля.
        
        Идентификатор пользователя берётся из текущей сессии, поэтому изменить можно только свои данные.""",
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены"),
            ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        ]
    )
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun updateMe(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @Valid @RequestBody request: UpdateUserRequest
    ): UserResponse {
        val command = userControllerMapper.toUpdateCommand(request)
        val model = userService.update(principal.id, command)
        return userControllerMapper.toResponse(model)
    }
    @Operation(
        summary = "Загружаем аватар в профиль",
        description = """
            Загружает аватар в профиль
        """,
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @PostMapping("/me/avatar", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadAvatar(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        @RequestPart("file") file: MultipartFile
    ): UserResponse {
        val model = userService.uploadAvatar(principal.id, file)
        return userControllerMapper.toResponse(model)
    }
}