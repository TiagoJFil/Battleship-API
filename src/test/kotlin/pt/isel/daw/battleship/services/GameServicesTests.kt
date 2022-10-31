package pt.isel.daw.battleship.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import pt.isel.daw.battleship.domain.Game
import pt.isel.daw.battleship.domain.Orientation
import pt.isel.daw.battleship.domain.ShipInfo
import pt.isel.daw.battleship.domain.Square
import pt.isel.daw.battleship.repository.GameRepository
import pt.isel.daw.battleship.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.battleship.repository.testWithTransactionManagerAndRollback
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.exception.ForbiddenAccessAppException
import pt.isel.daw.battleship.services.exception.GameNotFoundException
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.services.validationEntities.UserValidation
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID

class GameServicesTests {

    data class TestGameInfo(
        val id: ID,
        val player1: UserID,
        val player2: UserID
    )

    private fun createUser(transaction: TransactionFactory, name: String): AuthInformation {
        val userService = UserService(transaction)
        return userService.createUser(UserValidation(name, "12346"))
    }

    private fun createGame(transaction: TransactionFactory): TestGameInfo? {

        val userService = UserService(transaction)
        val gameService = GameService(transaction)
        val (uid1, token1) = userService.createUser(UserValidation("user_test", "password1"))
        val (uid2, token2) = userService.createUser(UserValidation("user_test2", "password1"))
        gameService.createOrJoinGame(uid1)
        val gameID = gameService.createOrJoinGame(uid2) ?: return null

        return TestGameInfo(gameID, uid1, uid2)
    }

    @Test
    fun `define a board layout in a game successfully`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(it)
            val game = createGame(it)

            if (game == null) {
                assert(false)
                return@testWithTransactionManagerAndRollback
            }


            val fleet = listOf(
                ShipInfo(Square(0, 0), 1, Orientation.Vertical),
                ShipInfo(Square(2, 1), 2, Orientation.Vertical),
                ShipInfo(Square(1, 3), 3, Orientation.Horizontal),
                ShipInfo(Square(4, 3), 4, Orientation.Horizontal),
                ShipInfo(Square(1, 8), 5, Orientation.Vertical)
            )

            gameService.defineFleetLayout(game.player1, game.id, fleet)
            val board1 = gameService.getFleet(game.player1, game.id, false)
            val board2 = gameService.getFleet(game.player1, game.id, true)

            val expectedSquares = listOf<Square>(
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

            assertEquals(game.player1, board1.userID)
            expectedSquares.forEach { square ->
                assert(square in board1.shipParts)
            }
            assertEquals(game.player2, board2.userID)
            assertEquals(emptyList<Square>(),board2.shipParts)
        }
    }

    @Test
    fun `define a board layout in a game that doesn't exist`() {
        assertThrows<GameNotFoundException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(it)
                val game = createGame(it)

                if (game == null) {
                    assert(false)
                    return@testWithTransactionManagerAndRollback
                }

                gameService.defineFleetLayout(game.player1, game.id + 1, emptyList())
            }
        }
    }

    @Test
    fun `define a board layout in a game that doesn't belong to the user`() {
        assertThrows<ForbiddenAccessAppException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(it)
                val game = createGame(it)

                if (game == null) {
                    assert(false)
                    return@testWithTransactionManagerAndRollback
                }

                gameService.defineFleetLayout(game.player2 + 1, game.id, emptyList())
            }
        }
    }

    @Test
    fun `Get the state of a game successfully after two players matched`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(it)
            val game = createGame(it)

            if (game == null) {
                assert(false)
                return@testWithTransactionManagerAndRollback
            }

            val stateForPlayer1 = gameService.getGameState(game.id, game.player1)
            val stateForPlayer2 = gameService.getGameState(game.id, game.player2)

            assertEquals(stateForPlayer1, stateForPlayer2)
            assertEquals(Game.State.PLACING_SHIPS,stateForPlayer1.state)
            assertEquals(Game.State.PLACING_SHIPS,stateForPlayer2.state)
        }
    }

    @Test
    fun `Get the state of a game sucessfully after two player define their layouts`(){
        testWithTransactionManagerAndRollback {
            val gameService = GameService(it)
            val game = createGame(it)

            if (game == null) {
                assert(false)
                return@testWithTransactionManagerAndRollback
            }

            val fleet = listOf(
                ShipInfo(Square(0, 0), 1, Orientation.Vertical),
                ShipInfo(Square(2, 1), 2, Orientation.Vertical),
                ShipInfo(Square(1, 3), 3, Orientation.Horizontal),
                ShipInfo(Square(4, 3), 4, Orientation.Horizontal),
                ShipInfo(Square(1, 8), 5, Orientation.Vertical)
            )

            gameService.defineFleetLayout(game.player1, game.id, fleet)
            gameService.defineFleetLayout(game.player2, game.id, fleet)

            val stateForPlayer1 = gameService.getGameState(game.id, game.player1)
            val stateForPlayer2 = gameService.getGameState(game.id, game.player2)

            assertEquals(stateForPlayer1, stateForPlayer2)
            assertEquals(Game.State.PLAYING,stateForPlayer1.state)
            assertEquals(Game.State.PLAYING,stateForPlayer2.state )
        }
    }

    @Test
    fun `Get the state of a game that doesn't exist`() {
        assertThrows<GameNotFoundException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(it)
                val game = createGame(it)

                if (game == null) {
                    assert(false)
                    return@testWithTransactionManagerAndRollback
                }

                gameService.getGameState(game.id + 1, game.player1)
            }
        }
    }

    @Test
    fun `Get the state of a game that doesn't belong to the user`() {
        assertThrows<ForbiddenAccessAppException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(it)
                val game = createGame(it)

                if (game == null) {
                    assert(true)
                    return@testWithTransactionManagerAndRollback
                }

                gameService.getGameState(game.id, game.player2 + 1)
            }
        }
    }


    @Test
    fun ` Leave the queue sucessfully`(){
        testWithTransactionManagerAndRollback {
            val gameService = GameService(it)
            val user = createUser(it,"testuser")

            val joinedGame = gameService.createOrJoinGame(user.uid)
            assertEquals(null,joinedGame)
            gameService.leaveLobby(user.uid)
        }
    }

    @Test
    fun `Cant leave the queue if user did not join`(){
        assertThrows<ForbiddenAccessAppException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(it)
                val user = createUser(it,"testuser")


                gameService.leaveLobby(user.uid)
            }
        }
    }



}