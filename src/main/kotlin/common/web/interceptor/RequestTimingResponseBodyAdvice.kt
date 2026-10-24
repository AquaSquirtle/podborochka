package org.podborochka.common.web.interceptor

import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import kotlin.math.roundToLong

@RestControllerAdvice
class RequestTimingResponseBodyAdvice : ResponseBodyAdvice<Any> {

    companion object {
        private const val HEADER_NAME = "X-Elapsed-Time"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {
        return true
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        val servletRequest = (request as? ServletServerHttpRequest)?.servletRequest
        val startedAt = servletRequest?.getAttribute(RequestTimingStartFilter.START_TIME_ATTR) as? Long

        if (startedAt != null) {
            val elapsedNs = System.nanoTime() - startedAt
            val elapsedMs = (elapsedNs / 1_000_000.0).roundToLong()
            response.headers.set(HEADER_NAME, "${elapsedMs}ms")
        } else {
            log.warn(
                "Start time attribute not found for {}.{}",
                returnType.containingClass.simpleName,
                returnType.method?.name
            )
        }

        return body
    }
}