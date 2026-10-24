package org.podborochka.user.infrastructure.storage

import org.springframework.web.multipart.MultipartFile

interface ObjectStorageClient {
    fun uploadAvatar(userId: Long, file: MultipartFile): String
}