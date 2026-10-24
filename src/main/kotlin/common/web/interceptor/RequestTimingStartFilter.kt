package org.podborochka.common.web.interceptor

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RequestTimingStartFilter : OncePerRequestFilter() {

    companion object {
        const val START_TIME_ATTR = "requestStartTimeNs"
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val uri = request.requestURI
        return uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/images/") ||
                uri.startsWith("/scripts/") ||
                uri.startsWith("/styles/") ||
                uri.startsWith("/webjars/") ||
                uri == "/favicon.ico"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        request.setAttribute(START_TIME_ATTR, System.nanoTime())
        filterChain.doFilter(request, response)
    }
}