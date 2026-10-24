package org.podborochka.common.configuration

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.ShallowEtagHeaderFilter

@Configuration
class WebCacheConfig {

    @Bean
    fun messagesEtagFilter(): FilterRegistrationBean<ShallowEtagHeaderFilter> {
        val filter: ShallowEtagHeaderFilter = object : ShallowEtagHeaderFilter() {
            override fun shouldNotFilter(request: HttpServletRequest): Boolean {
                return request.method !in setOf("GET", "HEAD") ||
                        !request.requestURI.matches(Regex("^/api/v1/conversations/\\d+/messages$"))
            }
        }

        return FilterRegistrationBean<ShallowEtagHeaderFilter>(filter).apply {
            addUrlPatterns("/*")
            setName("messagesEtagFilter")
            order = 10
        }
    }
}