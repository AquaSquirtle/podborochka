package org.podborochka.wishlist.application.service.impl

import org.podborochka.wishlist.application.service.WishlistNotificationService
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Service
class WishlistNotificationServiceImpl : WishlistNotificationService {

    private val emitters = ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>>()

    override fun subscribe(userId: Long): SseEmitter {
        val emitter = SseEmitter(0L)

        emitters.computeIfAbsent(userId) { CopyOnWriteArrayList() }.add(emitter)

        emitter.onCompletion {
            removeEmitter(userId, emitter)
        }

        emitter.onTimeout {
            emitter.complete()
            removeEmitter(userId, emitter)
        }

        emitter.onError {
            emitter.complete()
            removeEmitter(userId, emitter)
        }

        try {
            emitter.send(
                SseEmitter.event()
                    .name("connected")
                    .data("ok")
            )
        } catch (_: IOException) {
            emitter.complete()
            removeEmitter(userId, emitter)
        }

        return emitter
    }

    override fun notifyWishlistSaved(userId: Long, title: String) {
        val userEmitters = emitters[userId] ?: return
        val deadEmitters = mutableListOf<SseEmitter>()

        userEmitters.forEach { emitter ->
            try {
                emitter.send(
                    SseEmitter.event()
                        .name("wishlist-saved")
                        .data(title)
                )
            } catch (_: IOException) {
                emitter.complete()
                deadEmitters.add(emitter)
            }
        }

        if (deadEmitters.isNotEmpty()) {
            userEmitters.removeAll(deadEmitters)
        }
    }

    private fun removeEmitter(userId: Long, emitter: SseEmitter) {
        emitters[userId]?.remove(emitter)
        if (emitters[userId].isNullOrEmpty()) {
            emitters.remove(userId)
        }
    }
}