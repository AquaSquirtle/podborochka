package org.podborochka.common.web.utils

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

object PaginationUtils {
    private const val MAX_PAGE_SIZE = 100
    private const val DEFAULT_PAGE = 0
    private const val DEFAULT_SIZE = 20

    fun pageable(page: Int?, size: Int?): Pageable {
        val safePage = (page ?: DEFAULT_PAGE).coerceAtLeast(0)
        val safeSize = (size ?: DEFAULT_SIZE).coerceIn(1, MAX_PAGE_SIZE)
        return PageRequest.of(safePage, safeSize)
    }
}