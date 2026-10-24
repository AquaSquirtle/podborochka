package org.podborochka.user.controller

import org.springframework.ui.Model
import org.podborochka.common.security.AppUserPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UserPageController {
    @GetMapping("/profile")
    fun profile(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        model: Model
    ): String {
        model.addAttribute("isAuthenticated", true)
        model.addAttribute("username", principal.displayName)
        return "profile"
    }
}