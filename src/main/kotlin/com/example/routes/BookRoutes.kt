package com.example.routes

import com.example.dto.CreateBookRequest
import com.example.dto.UpdateBookRequest
import com.example.error.ValidationException
import com.example.service.BookService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.bookRoutes(bookService: BookService) {
    route("/books") {
        get {
            call.respond(HttpStatusCode.OK, bookService.getAll())
        }

        get("/{id}") {
            val id = call.bookId()
            call.respond(HttpStatusCode.OK, bookService.getById(id))
        }

        authenticate("auth-jwt") {
            post {
                val request = call.receive<CreateBookRequest>()
                call.respond(HttpStatusCode.Created, bookService.create(request))
            }

            put("/{id}") {
                val id = call.bookId()
                val request = call.receive<UpdateBookRequest>()
                call.respond(HttpStatusCode.OK, bookService.update(id, request))
            }

            delete("/{id}") {
                val id = call.bookId()
                bookService.delete(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private fun io.ktor.server.application.ApplicationCall.bookId(): Long {
    val id = parameters["id"]?.toLongOrNull()

    if (id == null || id <= 0) {
        throw ValidationException("Book id must be a positive number")
    }

    return id
}
