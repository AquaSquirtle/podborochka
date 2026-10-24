package org.podborochka.common.web.mapper

import org.podborochka.common.web.dto.PageResponse
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class PageResponseMapper {

    fun <T, R> toPageResponse(page: Page<T>, mapper: (T) -> R): PageResponse<R> =
        PageResponse(
            content = page.content.map(mapper),
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            first = page.isFirst,
            last = page.isLast
        )
}