package pt.isel.daw.battleship.api

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.ProblemContentType
import pt.isel.daw.battleship.controller.hypermedia.SirenContentType
import pt.isel.daw.battleship.controller.hypermedia.SirenEntity
import pt.isel.daw.battleship.repository.JdbiTransactionFactoryTestDB
import pt.isel.daw.battleship.repository.assertContentTypeSiren
import pt.isel.daw.battleship.services.entities.SystemInfo
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTests {

    @LocalServerPort
    var port: Int = 0



    @TestConfiguration
    class Config{

        @Bean
        @Primary
        fun getTransactionFactory() = JdbiTransactionFactoryTestDB()
    }



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

        client.post().uri("/api/systemInfo")
            .exchange()
            .expectStatus().isEqualTo(405)
            .expectHeader()
            .value("content-type") {
                assertTrue(it.equals(ProblemContentType))
            }
    }

    @Test
    fun `get server stats ok`(){
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()

        client.get().uri("/api/${Uris.SYSTEM_INFO}")
            .exchange()
            .expectStatus().isOk
            .expectHeader()
            .assertContentTypeSiren()
            .expectBody()
            .jsonPath("\$.properties.version").isEqualTo("0.0.2")

    }




}