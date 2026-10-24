package org.podborochka.auth.presentation.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.podborochka.auth.controller.dto.RegisterRequest
import org.podborochka.auth.controller.dto.LoginRequest
import org.podborochka.auth.service.AuthService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class AuthFormController(
    private val authService: AuthService,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/register")
    fun register(
        @Valid @ModelAttribute request: RegisterRequest,
        httpRequest: HttpServletRequest,
        model: Model
    ): String {
        return try {
            authService.register(request)

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

            "redirect:/chat"
        } catch (ex: Exception) {
            model.addAttribute("error", "Пользователь с таким email уже существует")
            model.addAttribute("email", request.email)
            model.addAttribute("displayName", request.displayName)
            "register"
        }
    }

    @PostMapping("/login")
    fun login(
        @Valid @ModelAttribute request: LoginRequest,
        httpRequest: HttpServletRequest,
        model: Model
    ): String {
        return try {
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

            "redirect:/chat"
        } catch (ex: BadCredentialsException) {
            model.addAttribute("error", "Неверное имя пользователя или пароль")
            model.addAttribute("email", request.email)
            "login"
        } catch (ex: Exception) {
            model.addAttribute("error", "Не удалось выполнить вход")
            model.addAttribute("email", request.email)
            "login"
        }
    }
}