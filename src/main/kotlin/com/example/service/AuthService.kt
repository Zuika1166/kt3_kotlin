package com.example.service

import com.example.config.JwtConfig
import com.example.dto.LoginRequest
import com.example.dto.RegisterRequest
import com.example.dto.TokenResponse
import com.example.error.ConflictException
import com.example.error.UnauthorizedException
import com.example.error.ValidationException
import com.example.model.User
import com.example.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt

class AuthService(
    private val userRepository: UserRepository,
    private val jwtConfig: JwtConfig
) {
    fun register(request: RegisterRequest): User {
        val login = request.login.trim()

        if (login.length < 3) {
            throw ValidationException("Login must contain at least 3 characters")
        }

        if (request.password.length < 6) {
            throw ValidationException("Password must contain at least 6 characters")
        }

        val passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt())

        return userRepository.create(login, passwordHash)
            ?: throw ConflictException("Login already exists")
    }

    fun login(request: LoginRequest): TokenResponse {
        val user = userRepository.findByLogin(request.login.trim())
            ?: throw UnauthorizedException("Invalid login or password")

        if (!BCrypt.checkpw(request.password, user.passwordHash)) {
            throw UnauthorizedException("Invalid login or password")
        }

        return TokenResponse(
            token = jwtConfig.createToken(user),
            expiresInSeconds = jwtConfig.expiresInSeconds
        )
    }
}
