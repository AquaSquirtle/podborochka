package org.podborochka.graphql.dto


import org.podborochka.chat.controller.dto.ChatRecommendationResponse
import org.podborochka.conversation.controller.dto.ConversationResponse
import org.podborochka.message.controller.dto.MessageResponse
import org.podborochka.product.controller.dto.ProductResponse
import org.podborochka.wishlist.controller.dto.WishlistItemResponse

data class CreateProductInputGql(
    val marketplace: String,
    val title: String,
    val url: String,
    val imageUrl: String?,
    val price: Double?,
    val currency: String?,
    val rating: Double?
)

data class UpdateProductInputGql(
    val marketplace: String?,
    val title: String?,
    val url: String?,
    val imageUrl: String?,
    val price: Double?,
    val currency: String?,
    val rating: Double?
)

data class UpdateUserInputGql(
    val email: String?,
    val password: String?,
    val displayName: String?
)

data class SaveWishlistItemInputGql(
    val productId: Long?,
    val recommendationId: Long?,
    val customTitle: String?,
    val customUrl: String?,
    val notes: String?
)

data class CreateConversationInputGql(
    val title: String?
)

data class UpdateConversationInputGql(
    val title: String?
)

data class SendMessageInputGql(
    val prompt: String
)

data class ProductPageGql(
    val content: List<ProductResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int
)

data class WishlistItemPageGql(
    val content: List<WishlistItemResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int
)

data class ConversationPageGql(
    val content: List<ConversationResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int
)

data class MessagePageGql(
    val content: List<MessageResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int
)

data class SendChatMessagePayloadGql(
    val conversationId: Long,
    val prompt: String,
    val assistantMessage: String,
    val recipientSummary: String?,
    val recommendations: List<ChatRecommendationResponse>
)

data class ConversationMessagesCarrier(
    val id: Long,
    val title: String?,
    val createdAt: String,
    val updatedAt: String
)