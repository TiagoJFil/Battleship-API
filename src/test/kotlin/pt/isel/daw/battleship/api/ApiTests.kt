package pt.isel.daw.battleship.api

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTests {

    @LocalServerPort
    var port: Int = 0

    @Test
    fun `a request for a path that does not exist returns 404`() {
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client.get().uri("/api/does-not-exist")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader()
            .value("content-type") {
                assertTrue(it.equals("application/problem+json"))
            }
    }

    @Test
    fun `Method not supported on a path with a different method other then the one supported returns 405`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client.post().uri("/api/statistics")
            .exchange()
            .expectStatus().isEqualTo(405)
            .expectHeader()
            .value("content-type") {
                assertTrue(it.equals("application/problem+json"))
            }
    }


}