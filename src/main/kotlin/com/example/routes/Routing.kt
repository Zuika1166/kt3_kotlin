package com.example.routes

import com.example.service.AuthService
import com.example.service.BookService
import io.ktor.server.application.Application
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing

fun Application.configureRouting(
    authService: AuthService,
    bookService: BookService
) {
    routing {
        swaggerUI(
            path = "swagger",
            swaggerFile = "openapi/documentation.yaml"
        )

        openAPI(
            path = "openapi",
            swaggerFile = "openapi/documentation.yaml"
        )

        authRoutes(authService)
        bookRoutes(bookService)
    }
}
