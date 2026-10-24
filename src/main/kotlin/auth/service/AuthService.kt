package org.podborochka.auth.service

import org.podborochka.auth.controller.dto.AuthResponse
import org.podborochka.auth.controller.dto.RegisterRequest
import org.podborochka.common.exceptions.DuplicateResourceException
import org.podborochka.common.exceptions.ResourceNotFoundException
import org.podborochka.user.domain.UserRole
import org.podborochka.user.infrastructure.entity.UserEntity
import org.podborochka.user.infrastructure.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateResourceException("User with email ${request.email} already exists")
        }

        val user = UserEntity(
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            displayName = request.displayName,
            role = UserRole.USER
        )

        val saved = userRepository.save(user)

        return AuthResponse(
            id = requireNotNull(saved.id),
            email = saved.email,
            displayName = saved.displayName,
            role = saved.role.name
        )
    }

    fun me(userId: Long): AuthResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User with id $userId not found") }

        return AuthResponse(
            id = requireNotNull(user.id),
            email = user.email,
            displayName = user.displayName,
            role = user.role.name
        )
    }
}