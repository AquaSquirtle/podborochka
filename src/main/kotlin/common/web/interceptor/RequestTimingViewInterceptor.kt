package org.podborochka.common.web.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import kotlin.math.roundToLong

@Component
class RequestTimingViewInterceptor : HandlerInterceptor {

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        if (modelAndView?.viewName == null) return

        val startedAt = request.getAttribute("requestStartTimeNs") as? Long ?: return
        val elapsedMs = ((System.nanoTime() - startedAt) / 1_000_000.0).roundToLong()

        modelAndView.addObject("serverElapsedTimeMs", elapsedMs)
    }
}