package pt.isel.daw.battleship.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
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
import pt.isel.daw.battleship.controller.dto.input.LayoutInfoInputModel
import pt.isel.daw.battleship.controller.dto.input.ShotsInfoInputModel
import pt.isel.daw.battleship.controller.dto.input.UserInfoInputModel
import pt.isel.daw.battleship.controller.hypermedia.Problem
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.domain.Game
import pt.isel.daw.battleship.domain.Orientation
import pt.isel.daw.battleship.domain.ShipInfo
import pt.isel.daw.battleship.domain.Square
import pt.isel.daw.battleship.repository.*
import pt.isel.daw.battleship.repository.dto.BoardDTO
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.entities.GameInformation
import pt.isel.daw.battleship.services.entities.GameStateInfo
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
        val usersCreation = createPlayers("player1", "player2") ?: return
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
        val usersCreation = createPlayers("player1", "player2") ?: return

        client.get().uri("/game/0/myFleet")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${usersCreation.player1.token}")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader()
            .assertContentTypeProblem()
            .expectBody<Problem>()
    }

    @Test
    fun `get user's and opponent fleet with invalid token fails with problem`() {
        val usersCreation = createPlayers("player1", "player2") ?: return
        val gameID = enterLobby(usersCreation) ?: return

        client.get().uri("/game/$gameID/myFleet")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 0invalid0?!xd«?")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader()
            .assertContentTypeProblem()
            .expectBody<Problem>()
    }

    @Test
    fun `A user tries to get the fleet of a game that isn't his and it fails with problem`() {
        val usersCreation = createPlayers("player1", "player2") ?: return
        val gameID = enterLobby(usersCreation) ?: return

        val usersCreation2 = createPlayers("player3", "player4") ?: return
        val gameID2 = enterLobby(usersCreation2) ?: return

        client.get().uri("/game/$gameID2/myFleet")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${usersCreation.player1.token}")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader()
            .assertContentTypeProblem()
            .expectBody<Problem>()
    }

    @Test
    fun `define a fleet succesfully`() {
        val usersCreation = createPlayers("player1", "player2") ?: return
        val gameID = enterLobby(usersCreation) ?: return

        val fleetJson = objectMapper.writeValueAsString(defaultFleet)

        client.post().uri("/game/$gameID/layoutDefinition")
            .assertAndDefineFleet(fleetJson, usersCreation.player1.token)


        val board = client.get().uri("/game/$gameID/myFleet")
            .assertAndGetBoard(usersCreation.player1.token)


        if(board == null) {
            assert(false)
            return
        }

        val expectedShipSquares = listOf<Square>(
            Square(0, 0),
            Square(2, 1),
            Square(3, 1),
            Square(1, 3),
            Square(1, 4),
            Square(1, 5),
            Square(4, 3),
            Square(4, 4),
            Square(4, 5),
            Square(4, 6),
            Square(1, 8),
            Square(2, 8),
            Square(3, 8),
            Square(4, 8),
            Square(5, 8)
        )

        assertEquals(usersCreation.player1.uid, board.userID)
        expectedShipSquares.forEach { square ->
            assert(square in board.shipParts)
        }
        assertEquals(emptyList<Square>(), board.shots)

    }

    @Test
    fun `define a fleet on an invalid game id fails with problem`() {
        val usersCreation = createPlayers("player1", "player2") ?: return

        val fleet = LayoutInfoInputModel(
            listOf(
                ShipInfo(Square(0, 0), 1, Orientation.Vertical),
            )
        )

        val fleetJson = objectMapper.writeValueAsString(fleet)

        client.post().uri("/game/0/layoutDefinition")
            .bodyValue(fleetJson)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${usersCreation.player1.token}")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader()
            .assertContentTypeProblem()
            .expectBody<Problem>()
    }

    @Test
    fun `define a fleet with invalid token fails with problem`() {
        val usersCreation = createPlayers("player1", "player2") ?: return
        val userCreation2 = createPlayers("player3", "player4") ?: return
        val gameID = enterLobby(usersCreation) ?: return
        val gameID2 = enterLobby(userCreation2) ?: return

        val fleet = LayoutInfoInputModel(
            listOf(
                ShipInfo(Square(0, 0), 1, Orientation.Vertical),
            )
        )

        val fleetJson = objectMapper.writeValueAsString(fleet)

        client.post().uri("/game/$gameID2/layoutDefinition")
            .bodyValue(fleetJson)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 0invalid0?!xd«?")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader()
            .assertContentTypeProblem()
            .expectBody<Problem>()
    }

    @Test
    fun `A user tries to define the fleet of a game that isn't his and it fails with problem`(){
        val usersCreation = createPlayers("player1", "player2") ?: return
        val userCreation2 = createPlayers("player3", "player4") ?: return
        val gameID = enterLobby(usersCreation) ?: return
        val gameID2 = enterLobby(userCreation2) ?: return

        val fleet = LayoutInfoInputModel(
            listOf(
                ShipInfo(Square(0, 0), 1, Orientation.Vertical),
            )
        )

        val fleetJson = objectMapper.writeValueAsString(fleet)

        client.post().uri("/game/$gameID2/layoutDefinition")
            .bodyValue(fleetJson)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${usersCreation.player1.token}")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader()
            .assertContentTypeProblem()
            .expectBody<Problem>()
    }

    @Test
    fun `define shots successfully`(){
        val usersCreation = createPlayers("player1", "player2") ?: return
        val gameID = enterLobby(usersCreation) ?: return

        val fleetJson = objectMapper.writeValueAsString(defaultFleet)

        client.post().uri("/game/$gameID/layoutDefinition")
            .assertAndDefineFleet(fleetJson, usersCreation.player1.token)

        client.post().uri("/game/$gameID/layoutDefinition")
            .assertAndDefineFleet(fleetJson, usersCreation.player2.token)

        val shots = ShotsInfoInputModel(
            listOf(Square(0, 0))
        )
        val shotsJson = objectMapper.writeValueAsString(shots)
        client.post().uri("/game/$gameID/shotsDefinition")
            .bodyValue(shotsJson)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${usersCreation.player1.token}")
            .exchange()
            .expectBody<SirenEntity<Nothing>>()

        val board = client.get().uri("/game/$gameID/opponentFleet")
            .assertAndGetBoard(usersCreation.player1.token)

        if(board == null) {
            assert(false)
            return
        }

        val expectedShipSquares = listOf<Square>(
            Square(2, 1),
            Square(3, 1),
            Square(1, 3),
            Square(1, 4),
            Square(1, 5),
            Square(4, 3),
            Square(4, 4),
            Square(4, 5),
            Square(4, 6),
            Square(1, 8),
            Square(2, 8),
            Square(3, 8),
            Square(4, 8),
            Square(5, 8)
        )

        val expectedShotsSquares = listOf<Square>(
            Square(0, 1),
            Square(1, 0),
            Square(1, 1),
        )

        //assertEquals(usersCreation.player2.uid, board.userID)
        expectedShipSquares.forEach { square ->
            assert(square in board.shipParts)
        }
        expectedShotsSquares.forEach { square ->
            assert(square in board.shots)
        }

    }

    @Test
    fun `get Game state successfully`(){
        val usersCreation = createPlayers("player1", "player2") ?: return
        val gameID = enterLobby(usersCreation) ?: return

        val gameInfo = client.get().uri("/game/$gameID/gameState")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${usersCreation.player1.token}")
            .exchange()
            .expectHeader()
            .assertContentTypeSiren()
            .expectBody<SirenEntity<GameStateInfo>>()
            .returnResult().responseBody?.properties

        if (gameInfo == null) {
            assert(false)
            return
        }

        assertEquals(Game.State.PLACING_SHIPS, gameInfo.state)
    }


    private data class UsersCreation(val player1: AuthInformation, val player2: AuthInformation)

    private fun createPlayers(player1: String, player2: String): UsersCreation? {
        val user1 = UserInfoInputModel(player1, "123456878")
        val user2 = UserInfoInputModel(player2, "123456878")

        val player1Info = client.post().uri("${Uris.User.REGISTER}/")
            .assertAndCreateUser(user1)

        val player2Info = client.post().uri("${Uris.User.REGISTER}/")
            .assertAndCreateUser(user2)

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

    private fun WebTestClient.RequestBodySpec.assertAndDefineFleet(fleetJson: String, userToken: String){
        bodyValue(fleetJson)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $userToken")
            .exchange()
            .expectBody<SirenEntity<Nothing>>()
    }

    val defaultFleet = LayoutInfoInputModel(
        listOf(
            ShipInfo(Square(0, 0), 1, Orientation.Vertical),
            ShipInfo(Square(2, 1), 2, Orientation.Vertical),
            ShipInfo(Square(1, 3), 3, Orientation.Horizontal),
            ShipInfo(Square(4, 3), 4, Orientation.Horizontal),
            ShipInfo(Square(1, 8), 5, Orientation.Vertical)
        )
    )
}
