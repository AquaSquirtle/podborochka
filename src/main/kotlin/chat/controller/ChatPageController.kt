package org.podborochka.web.chat

import org.podborochka.common.security.AppUserPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ChatPageController {

    @GetMapping("/chat")
    fun chatPage(
        @AuthenticationPrincipal principal: AppUserPrincipal,
        model: Model
    ): String {
        model.addAttribute("isAuthenticated", true)
            model.addAttribute("username", principal.displayName)
        return "chat"
    }
}