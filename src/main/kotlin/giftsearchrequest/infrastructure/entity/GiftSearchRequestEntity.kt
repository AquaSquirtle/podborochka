package org.podborochka.giftsearchrequest.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import org.podborochka.conversation.infrastructure.entity.ConversationEntity
import org.podborochka.giftsearchrequest.domain.GiftSearchRequestStatus
import org.podborochka.user.infrastructure.entity.UserEntity
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "gift_search_requests")
class GiftSearchRequestEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    var conversation: ConversationEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity,

    @Column(name = "raw_prompt", nullable = false, columnDefinition = "TEXT")
    var rawPrompt: String,

    @Column(name = "recipient_description", columnDefinition = "TEXT")
    var recipientDescription: String? = null,

    @Column(name = "occasion", length = 100)
    var occasion: String? = null,

    @Column(name = "budget_min", precision = 10, scale = 2)
    var budgetMin: BigDecimal? = null,

    @Column(name = "budget_max", precision = 10, scale = 2)
    var budgetMax: BigDecimal? = null,

    @Column(name = "interests", columnDefinition = "TEXT")
    var interests: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    var status: GiftSearchRequestStatus = GiftSearchRequestStatus.CREATED,

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