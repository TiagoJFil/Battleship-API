package pt.isel.daw.battleship.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.dto.input.UserInfoInputModel
import pt.isel.daw.battleship.controller.hypermedia.Problem
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.domain.Board
import pt.isel.daw.battleship.repository.*
import pt.isel.daw.battleship.repository.dto.BoardDTO
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.entities.GameInformation
import pt.isel.daw.battleship.services.exception.GameNotFoundException
import pt.isel.daw.battleship.utils.ID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerTests {
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

    companion object {
        private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    }

    @Test
    fun `get user's and opponent fleet succesfully`() {
        val usersCreation = createPlayers() ?: return
        val gameID = enterLobby(usersCreation) ?: return

        val board = client.get().uri("/game/$gameID/myFleet")
            .assertAndGetBoard(usersCreation.player1.token)

        val boardOpponent = client.get().uri("/game/$gameID/opponentFleet")
            .assertAndGetBoard(usersCreation.player2.token)

        if (board == null || boardOpponent == null) {
            assert(false)
            return
        }


        assert(board.shipParts.isEmpty())
        assert(board.shots.isEmpty())

        assert(boardOpponent.shipParts.isEmpty())
        assert(boardOpponent.shots.isEmpty())
    }

    @Test
    fun `get user's and opponent fleet with invalid game id fails with problem`() {
        val usersCreation = createPlayers() ?: return

        client.get().uri("/game/0/myFleet")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${usersCreation.player1.token}")
            .exchange()
            .expectHeader()
            .assertContentTypeProblem()

    }
    data class UsersCreation(val player1: AuthInformation, val player2: AuthInformation)

    private fun createPlayers(): UsersCreation? {
        val player1 = UserInfoInputModel("player1", "123456878")
        val player2 = UserInfoInputModel("player2", "123456878")

        val player1Info = client.post().uri("${Uris.User.REGISTER}/")
            .assertAndCreateUser(player1)

        val player2Info = client.post().uri("${Uris.User.REGISTER}/")
            .assertAndCreateUser(player2)

        return if (player1Info != null && player2Info != null)
            UsersCreation(player1Info, player2Info)
        else {
            assert(false)
            null
        }
    }

    private fun enterLobby(usersCreation: UsersCreation): ID? {
        client.post().uri("${Uris.Lobby.QUEUE}/")
            .assertAndEnterLobby(usersCreation.player1.token)

        val gameInfo = client.post().uri("${Uris.Lobby.QUEUE}/")
            .assertAndEnterLobby(usersCreation.player2.token)

        return if (gameInfo == null) {
            assert(false)
            null
        } else {
            gameInfo.id
        }
    }

    private fun WebTestClient.RequestHeadersSpec<*>.assertAndGetBoard(userToken: String): BoardDTO? =
        header("Content-Type", "application/json")
            .header("Authorization", "Bearer $userToken")
            .exchange()
            .expectStatus().isOk
            .expectHeader()
            .assertContentTypeSiren()
            .expectBody<SirenEntity<BoardDTO>>()
            .returnResult().responseBody?.properties

    private fun WebTestClient.RequestHeadersSpec<*>.assertAndEnterLobby(userToken: String): GameInformation? {
        return header("Content-Type", "application/json")
            .header("Authorization", "Bearer $userToken")
            .exchange()
            .expectHeader()
            .assertContentTypeSiren()
            .expectBody<SirenEntity<GameInformation>>()
            .returnResult().responseBody?.properties
    }

    private fun WebTestClient.RequestBodySpec.assertAndCreateUser(userInputInfo: UserInfoInputModel): AuthInformation? {
        return bodyValue(
            """
                {
                    "username": "${userInputInfo.username}",
                    "password": "${userInputInfo.password}"
                }
            """
        )
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isCreated
            .expectBody<SirenEntity<AuthInformation>>()
            .returnResult().responseBody?.properties


    }
}
