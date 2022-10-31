package pt.isel.daw.battleship.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.Problem
import pt.isel.daw.battleship.controller.hypermedia.ProblemContentType
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.repository.JdbiTransactionFactoryTestDB
import pt.isel.daw.battleship.repository.clear
import pt.isel.daw.battleship.repository.executeWithHandle
import pt.isel.daw.battleship.services.entities.GameInformation

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LobbyControllerTests {

    @LocalServerPort
    var port: Int = 0

    @TestConfiguration
    class Config {
        @Bean
        @Primary
        fun getTransactionFactory() = JdbiTransactionFactoryTestDB()
    }

    @BeforeEach
    fun executeClean() {
        executeWithHandle { handle ->
            clear()
        }
    }

    private val client by lazy {
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port/api")
            .build()
    }


    @Test
    fun `join Queue sucessfully`() {
        val authInfo = client.createUser("user1", "pass1")
        client.joinQueue(authInfo.token)
    }

    @Test
    fun `create and join a game sucessfully`() {
        val authInfo1 = client.createUser("user1", "pass1")
        val authInfo2 = client.createUser("user2", "pass2")
        val res1 = client.joinQueue(authInfo1.token)
        val res2 = client.joinQueue(authInfo2.token)

        assert(res1 == null)
        assert(res2 != null)
    }

    @Test
    fun `Try to enqueue with an invalid token`() {
        client.post().uri(Uris.Lobby.QUEUE)
            .setAuthToken("authTokeninvalid")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader()
            .assertContentTypeProblem()
            .expectBody<Problem>()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `Trying to cancel queue with an invalid token`() {
        client.post().uri(Uris.Lobby.CANCEL_QUEUE)
            .setAuthToken("authTokeninvalid")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader()
            .assertContentTypeProblem()
            .expectBody<Problem>()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `Trying to cancel queue without joining the queue first`() {
        val authInfo = client.createUser("user1", "pass1")
        client.post().uri(Uris.Lobby.CANCEL_QUEUE)
            .setAuthToken(authInfo.token)
            .exchange()
            .expectStatus().isForbidden
            .expectHeader()
            .assertContentTypeProblem()
            .expectBody<Problem>()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `Trying to cancel queue sucessfully after joining`() {
        val authInfo = client.createUser("user1", "pass1")
        client.joinQueue(authInfo.token)
        client.post().uri(Uris.Lobby.CANCEL_QUEUE)
            .setAuthToken(authInfo.token)
            .exchange()
            .expectStatus().isOk
    }

}

