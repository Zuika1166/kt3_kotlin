package com.example

import com.example.dto.TokenResponse
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.contentType
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun documentationEndpointsAreAvailable() = testApplication {
        application { module() }

        val swaggerStatus = client.get("/swagger").status.value
        val openApiStatus = client.get("/openapi").status.value

        assertTrue(swaggerStatus in 200..399)
        assertTrue(openApiStatus in 200..399)
    }

    @Test
    fun statusPagesHandlesValidationAndNotFound() = testApplication {
        application { module() }

        val invalidId = client.get("/books/not-a-number")
        assertEquals(HttpStatusCode.BadRequest, invalidId.status)
        assertTrue(invalidId.bodyAsText().contains("Book id must be a positive number"))

        val missingBook = client.get("/books/999")
        assertEquals(HttpStatusCode.NotFound, missingBook.status)
        assertTrue(missingBook.bodyAsText().contains("Book not found"))
    }

    @Test
    fun malformedJsonReturnsBadRequest() = testApplication {
        application { module() }

        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("{")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Invalid request body"))
    }

    @Test
    fun authenticationAndCrudFlowWorks() = testApplication {
        application { module() }

        val registerResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"login":"john","password":"secret123"}""")
        }
        assertEquals(HttpStatusCode.Created, registerResponse.status)

        val duplicateResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"login":"john","password":"secret123"}""")
        }
        assertEquals(HttpStatusCode.Conflict, duplicateResponse.status)

        val wrongPasswordResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"login":"john","password":"wrong-password"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, wrongPasswordResponse.status)

        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"login":"john","password":"secret123"}""")
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)

        val token = Json.decodeFromString<TokenResponse>(
            loginResponse.bodyAsText()
        ).token

        val unauthorizedCreate = client.post("/books") {
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Clean Code","author":"Robert C. Martin","year":2008}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, unauthorizedCreate.status)

        val createResponse = client.post("/books") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Clean Code","author":"Robert C. Martin","year":2008}""")
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)

        assertEquals(HttpStatusCode.OK, client.get("/books").status)
        assertEquals(HttpStatusCode.OK, client.get("/books/1").status)

        val updateResponse = client.put("/books/1") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Clean Code","author":"Robert C. Martin","year":2009}""")
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)
        assertTrue(updateResponse.bodyAsText().contains("2009"))

        val deleteResponse = client.delete("/books/1") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        assertEquals(
            HttpStatusCode.NotFound,
            client.get("/books/1").status
        )
    }
}
