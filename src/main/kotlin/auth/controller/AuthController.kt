package org.podborochka.auth.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.podborochka.auth.controller.dto.AuthResponse
import org.podborochka.auth.controller.dto.LoginRequest
import org.podborochka.auth.controller.dto.RegisterRequest
import org.podborochka.auth.service.AuthService
import org.podborochka.common.security.AppUserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@Tag(
    name = "Authentication",
    description = "Регистрация, вход в систему и завершение пользовательской сессии"
)
class AuthController(
    private val authService: AuthService,
    private val authenticationManager: AuthenticationManager
) {

    @Operation(
        summary = "Регистрация пользователя",
        description = """
            Создает нового пользователя в системе по email, паролю и displayName.
            
            После успешной регистрации пользователь создается в локальной базе данных приложения.
            Этот endpoint не создает сессию автоматически и используется отдельно от login.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных"),
            ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
        ]
    )
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): AuthResponse = authService.register(request)

    @Operation(
        summary = "Вход в систему",
        description = """
            Аутентифицирует пользователя по email и паролю и создает серверную сессию.
            
            После успешного входа Spring Security сохраняет аутентификацию в HTTP session,
            а клиент получает cookie JSESSIONID для последующих запросов к защищенным endpoint'ам.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Пользователь успешно вошел в систему"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных"),
            ApiResponse(responseCode = "401", description = "Неверные учетные данные")
        ]
    )
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        @Parameter(hidden = true) httpRequest: HttpServletRequest
    ): AuthResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )

        SecurityContextHolder.getContext().authentication = authentication
        httpRequest.session.setAttribute(
            "SPRING_SECURITY_CONTEXT",
            SecurityContextHolder.getContext()
        )

        val principal = authentication.principal as AppUserPrincipal

        return AuthResponse(
            id = principal.id,
            email = principal.username,
            displayName = principal.displayName,
            role = principal.role
        )
    }

    @Operation(
        summary = "Выход из системы",
        description = """
            Завершает текущую пользовательскую сессию.
            
            Используется для logout из приложения. После вызова этого метода защищенные endpoint'ы
            больше не будут доступны без повторного входа в систему.
        """,
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Сессия успешно завершена"),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        ]
    )
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    fun logout() {
    }
}