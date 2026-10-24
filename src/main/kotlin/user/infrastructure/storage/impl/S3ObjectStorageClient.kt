package org.podborochka.user.infrastructure.storage.impl

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import org.podborochka.common.configuration.ObjectStorageProperties
import org.podborochka.user.infrastructure.storage.ObjectStorageClient
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI
import java.util.UUID

@Component
class S3ObjectStorageClient(
    private val properties: ObjectStorageProperties
) : ObjectStorageClient {

    private val s3Client: S3Client = S3Client.builder()
        .endpointOverride(URI.create(properties.endpoint))
        .region(Region.US_EAST_1)
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.accessKey, properties.secretKey)
            )
        )
        .build()

    override fun uploadAvatar(userId: Long, file: MultipartFile): String {
        val extension = file.originalFilename
            ?.substringAfterLast('.', "")
            ?.takeIf { it.isNotBlank() }

        val objectKey = buildString {
            append("avatars/")
            append(userId)
            append("/")
            append(UUID.randomUUID())
            if (extension != null) {
                append(".")
                append(extension)
            }
        }

        val request = PutObjectRequest.builder()
            .bucket(properties.bucket)
            .key(objectKey)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(
            request,
            RequestBody.fromInputStream(file.inputStream, file.size)
        )

        return "${properties.publicBaseUrl}/$objectKey"
    }
}