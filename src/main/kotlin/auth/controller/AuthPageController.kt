package org.podborochka.auth.presentation.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class AuthPageController {

    @GetMapping("/login/form")
    fun loginPage(
        @RequestParam(required = false) error: String?,
        @RequestParam(required = false) email: String?,
        model: Model
    ): String {
        if (!error.isNullOrBlank()) {
            model.addAttribute("error", error)
        }
        if (!email.isNullOrBlank()) {
            model.addAttribute("email", email)
        }
        return "login"
    }

    @GetMapping("/register/form")
    fun registerPage(
        @RequestParam(required = false) error: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) displayName: String?,
        model: Model
    ): String {
        if (!error.isNullOrBlank()) {
            model.addAttribute("error", error)
        }
        if (!email.isNullOrBlank()) {
            model.addAttribute("email", email)
        }
        if (!displayName.isNullOrBlank()) {
            model.addAttribute("displayName", displayName)
        }
        return "register"
    }
}