package com.example.routes

import com.example.dto.LoginRequest
import com.example.dto.RegisterRequest
import com.example.dto.UserResponse
import com.example.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val user = authService.register(request)

            call.respond(
                HttpStatusCode.Created,
                UserResponse(
                    id = user.id,
                    login = user.login
                )
            )
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            call.respond(HttpStatusCode.OK, authService.login(request))
        }
    }
}
