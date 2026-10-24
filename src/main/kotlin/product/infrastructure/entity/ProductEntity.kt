package org.podborochka.product.infrastructure.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
class ProductEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "marketplace", nullable = false, length = 100)
    var marketplace: String,

    @Column(name = "title", nullable = false, length = 255)
    var title: String,

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    var url: String,

    @Column(name = "image_url", columnDefinition = "TEXT")
    var imageUrl: String? = null,

    @Column(name = "price", precision = 10, scale = 2)
    var price: BigDecimal? = null,

    @Column(name = "currency", length = 10)
    var currency: String? = null,

    @Column(name = "rating", precision = 3, scale = 2)
    var rating: BigDecimal? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
) {
    @PrePersist
    fun prePersist() {
        val now = LocalDateTime.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}