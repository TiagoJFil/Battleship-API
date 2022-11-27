package pt.isel.daw.battleship.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
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
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.repository.jdbiTransactionFactoryTestDB
import pt.isel.daw.battleship.repository.clear
import pt.isel.daw.battleship.repository.executeWithHandle
import pt.isel.daw.battleship.services.entities.LobbyInformation
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LobbyControllerTests {

    @LocalServerPort
    var port: Int = 0

    @TestConfiguration
    class Config {
        @Bean
        @Primary
        fun getTransactionFactory() = jdbiTransactionFactoryTestDB()
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
        val lobbyInfo = client.joinQueue(authInfo.token) ?: fail()

        assertEquals(lobbyInfo?.gameId, null)
    }

    @Test
    fun `create and join a game sucessfully`() {
        val authInfo1 = client.createUser("user1", "pass1")
        val authInfo2 = client.createUser("user2", "pass2")
        val res1 = client.joinQueue(authInfo1.token) ?: fail()
        val res2 = client.joinQueue(authInfo2.token) ?: fail()

        assert(res1.gameId == null)
        assert(res2.gameId != null)
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
        val authInfo = client.createUser("user1", "pass1")
        val lobbyInfo = client.joinQueue(authInfo.token)
        client.delete().uri("${Uris.Lobby.ROOT}/${lobbyInfo?.lobbyId}/cancel")
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
    fun `Trying to cancel queue successfully after joining`() {
        val authInfo = client.createUser("user1", "pass1")
        val lobbyInfo = client.joinQueue(authInfo.token)
        client.delete().uri("${Uris.Lobby.ROOT}/${lobbyInfo?.lobbyId}/cancel")
            .setAuthToken(authInfo.token)
            .exchange()
            .expectStatus().isOk
    }


    @Test
    fun `get lobby state sucessfully`(){
        val player1Info = client.createUser("user1", "pass1")
        val player2Info = client.createUser("user2", "pass2")

        val lobbyInfoPlayer1 = client.joinQueue(player1Info.token) ?: fail()

        assertEquals(null, lobbyInfoPlayer1.gameId)

        val lobbyInfoPlayer2 = client.joinQueue(player2Info.token)

        assert(lobbyInfoPlayer2 != null)

        val lobbyInfo = client.get().uri("/lobby/${lobbyInfoPlayer1.lobbyId}")
            .setAuthToken(player1Info.token)
            .exchange()
            .expectStatus().isOk
            .expectBody<SirenEntity<LobbyInformation>>()
            .returnResult()
            .responseBody?.properties

        assert(lobbyInfo != null)
        assertEquals(lobbyInfoPlayer2!!.gameId, lobbyInfo!!.gameId)

    }

    @Test
    fun `try to get the state of a lobby with a player that does not make part in it`(){
        val player1Info = client.createUser("user1", "pass1")
        val player2Info = client.createUser("user2", "pass2")

        val lobbyInfoPlayer1 = client.joinQueue(player1Info.token) ?: fail()

        client.get().uri(Uris.Lobby.STATE, lobbyInfoPlayer1.lobbyId)
            .setAuthToken(player2Info.token)
            .exchange()
            .expectStatus().isForbidden
            .expectHeader()
            .assertContentTypeProblem()
            .expectBody<Problem>()
            .returnResult()
            .responseBody!!
    }

}

