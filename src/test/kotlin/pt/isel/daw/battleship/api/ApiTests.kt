package pt.isel.daw.battleship.api

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTests {

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 0

    @Test
    fun `a request for a path that does not exist returns 404`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client.get().uri("/api/does-not-exist")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader()
            .value("content-type") {
                val a = it

                assertTrue(it.equals("application/problem+json"))
            }
    }


}