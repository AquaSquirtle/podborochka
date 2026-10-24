package org.podborochka.giftrecommendation.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import org.podborochka.giftsearchrequest.infrastructure.entity.GiftSearchRequestEntity
import org.podborochka.product.infrastructure.entity.ProductEntity
import java.time.LocalDateTime

@Entity
@Table(name = "gift_recommendations")
class GiftRecommendationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_request_id", nullable = false)
    var searchRequest: GiftSearchRequestEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: ProductEntity? = null,

    @Column(name = "title", nullable = false, length = 255)
    var title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "reasoning", columnDefinition = "TEXT")
    var reasoning: String? = null,

    @Column(name = "approximate_price")
    var approximatePrice: Int? = null,

    @Column(name = "search_query", columnDefinition = "TEXT")
    var searchQuery: String? = null,

    @Column(name = "ozon_search_url", columnDefinition = "TEXT")
    var ozonSearchUrl: String? = null,

    @Column(name = "wildberries_search_url", columnDefinition = "TEXT")
    var wildberriesSearchUrl: String? = null,

    @Column(name = "yandex_market_search_url", columnDefinition = "TEXT")
    var yandexMarketSearchUrl: String? = null,

    @Column(name = "rank_position", nullable = false)
    var rankPosition: Int,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null
) {
    @PrePersist
    fun prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now()
        }
    }
}
