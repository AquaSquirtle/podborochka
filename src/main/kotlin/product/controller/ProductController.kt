package org.podborochka.product.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.podborochka.common.web.dto.PageResponse
import org.podborochka.common.web.mapper.PageResponseMapper
import org.podborochka.common.web.utils.PaginationUtils
import org.podborochka.product.application.service.ProductService
import org.podborochka.product.controller.dto.CreateProductRequest
import org.podborochka.product.controller.dto.ProductResponse
import org.podborochka.product.controller.dto.UpdateProductRequest
import org.podborochka.product.controller.mapper.ProductControllerMapper
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
@Tag(
    name = "Products",
    description = "Каталог товаров и карточек подарков"
)
class ProductController(
    private val service: ProductService,
    private val mapper: ProductControllerMapper,
    private val pageResponseMapper: PageResponseMapper
) {

    @Operation(
        summary = "Создать продукт",
        description = """
            Создает новый product в каталоге.
            
            Product используется как общая сущность товара или карточки подарка и может быть
            связан с рекомендациями, wishlist и внешними маркетплейсами.
        """,
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Продукт создан"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса")
        ]
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun create(
        @Valid @RequestBody request: CreateProductRequest
    ): ProductResponse {
        val command = mapper.toCreateCommand(request)
        val model = service.create(command)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Получить список продуктов",
        description = """
            Возвращает страницу продуктов.
            
            Поддерживает:
            - пагинацию
            - опциональный фильтр по marketplace
            
            Используется для отображения каталога товаров и выбора продукта при сохранении рекомендаций.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Список продуктов получен"),
            ApiResponse(responseCode = "400", description = "Некорректные параметры пагинации")
        ]
    )
    @GetMapping
    fun getAll(
        @Parameter(description = "Название marketplace для фильтрации")
        @RequestParam(required = false) marketplace: String?,
        @Parameter(description = "Номер страницы, начиная с 0")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Размер страницы")
        @RequestParam(defaultValue = "20") size: Int
    ): PageResponse<ProductResponse> {
        val pageable = PaginationUtils.pageable(page, size)

        val result = if (marketplace.isNullOrBlank()) {
            service.getAll(pageable)
        } else {
            service.getByMarketplace(marketplace, pageable)
        }

        return pageResponseMapper.toPageResponse(result, mapper::toResponse)
    }

    @Operation(
        summary = "Получить продукт по id",
        description = "Возвращает один product по идентификатору"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Продукт найден"),
            ApiResponse(responseCode = "404", description = "Продукт не найден")
        ]
    )
    @GetMapping("/{id}")
    fun getById(
        @Parameter(description = "Идентификатор продукта")
        @PathVariable id: Long
    ): ProductResponse {
        val model = service.getById(id)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Обновить продукт",
        description = """
            Обновляет существующий product.
            
            Может использоваться для изменения:
            - marketplace
            - title
            - url
            - imageUrl
            - price
            - currency
            - rating
        """,
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Продукт обновлен"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации запроса"),
            ApiResponse(responseCode = "404", description = "Продукт не найден")
        ]
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(
        @Parameter(description = "Идентификатор продукта")
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateProductRequest
    ): ProductResponse {
        val command = mapper.toUpdateCommand(request)
        val model = service.update(id, command)
        return mapper.toResponse(model)
    }

    @Operation(
        summary = "Удалить продукт",
        description = "Удаляет product из каталога",
        security = [SecurityRequirement(name = "sessionAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Продукт удален"),
            ApiResponse(responseCode = "404", description = "Продукт не найден")
        ]
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(
        @Parameter(description = "Идентификатор продукта")
        @PathVariable id: Long
    ) {
        service.delete(id)
    }
}