package org.podborochka.common.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "storage")

data class ObjectStorageProperties(
    val endpoint: String,
    val bucket: String,
    val accessKey: String,
    val secretKey: String,
    val publicBaseUrl: String
)