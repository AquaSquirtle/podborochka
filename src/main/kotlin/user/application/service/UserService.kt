package org.podborochka.user.application.service
import org.podborochka.user.application.model.CreateUserCommand
import org.podborochka.user.application.model.UpdateUserCommand
import org.podborochka.user.application.model.UserModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.multipart.MultipartFile


interface UserService {

    fun create(command: CreateUserCommand): UserModel

    fun getById(id: Long): UserModel

    fun getAll(pageable: Pageable): Page<UserModel>

    fun update(id: Long, command: UpdateUserCommand): UserModel

    fun delete(id: Long)

    fun uploadAvatar(userId: Long, file: MultipartFile): UserModel
}