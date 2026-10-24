package org.podborochka.user.application.service.impl

import org.podborochka.common.exceptions.DuplicateResourceException
import org.podborochka.common.exceptions.ResourceNotFoundException
import org.podborochka.user.application.mapper.UserApplicationMapper
import org.podborochka.user.application.model.CreateUserCommand
import org.podborochka.user.application.model.UpdateUserCommand
import org.podborochka.user.application.model.UserModel
import org.podborochka.user.application.service.UserService
import org.podborochka.user.infrastructure.mapper.UserPersistenceMapper
import org.podborochka.user.infrastructure.repository.UserRepository
import org.podborochka.user.infrastructure.storage.ObjectStorageClient
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile


@Service
@Transactional(readOnly = true)
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userPersistenceMapper: UserPersistenceMapper,
    private val userApplicationMapper: UserApplicationMapper,
    private val passwordEncoder: PasswordEncoder,
    private val objectStorageClient: ObjectStorageClient,
) : UserService {

    @Transactional
    override fun create(command: CreateUserCommand): UserModel {
        if (userRepository.existsByEmail(command.email)) {
            throw DuplicateResourceException("User with email ${command.email} already exists")
        }

        val passwordHash = passwordEncoder.encode(command.rawPassword)

        val entity = userPersistenceMapper.toNewEntity(
            email = command.email,
            passwordHash = passwordHash,
            displayName = command.displayName
        )

        val saved = userRepository.save(entity)
        return userPersistenceMapper.toModel(saved)
    }

    override fun getById(id: Long): UserModel {
        val entity = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        return userPersistenceMapper.toModel(entity)
    }

    override fun getAll(pageable: Pageable): Page<UserModel> =
        userRepository.findAll(pageable)
            .map(userPersistenceMapper::toModel)

    @Transactional
    override fun update(id: Long, command: UpdateUserCommand): UserModel {
        val entity = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User with id $id not found") }

        if (command.email != null &&
            command.email != entity.email &&
            userRepository.existsByEmail(command.email)
        ) {
            throw DuplicateResourceException("User with email ${command.email} already exists")
        }

        val encodedPassword = command.rawPassword
            ?.takeIf { it.isNotBlank() }
            ?.let(passwordEncoder::encode)

        userApplicationMapper.applyUpdate(
            entity = entity,
            command = command,
            encodedPassword = encodedPassword
        )

        val updated = userRepository.save(entity)
        return userPersistenceMapper.toModel(updated)
    }

    @Transactional
    override fun delete(id: Long) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException("User with id $id not found")
        }

        userRepository.deleteById(id)
    }

    @Transactional
    override fun uploadAvatar(userId: Long, file: MultipartFile): UserModel {
        validateAvatar(file)

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Пользователь не найден") }

        val avatarUrl = objectStorageClient.uploadAvatar(userId, file)
        user.avatarUrl = avatarUrl

        val saved = userRepository.save(user)
        return userPersistenceMapper.toModel(saved)
    }

    private fun validateAvatar(file: MultipartFile) {
        require(!file.isEmpty) { "Файл пустой" }
        require(file.size <= maxSizeBytes) { "Файл слишком большой. Максимум 5 МБ" }
        require(file.contentType in allowedTypes) { "Допустимы только JPG, PNG, WEBP" }
    }
    companion object {
        private val allowedTypes = setOf("image/jpeg", "image/png", "image/webp")
        private val maxSizeBytes = 5 * 1024 * 1024L
    }
}