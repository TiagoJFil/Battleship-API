package pt.isel.daw.battleship.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.daw.battleship.controller.dto.BoardDTO
import pt.isel.daw.battleship.repository.testWithTransactionManagerAndRollback
import pt.isel.daw.battleship.services.exception.ForbiddenAccessAppException
import pt.isel.daw.battleship.services.exception.GameNotFoundException
import pt.isel.daw.battleship.services.model.*
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

    private fun createGame(transaction: TransactionFactory): TestGameInfo? {

        val userService = UserService(transaction)
        val gameService = GameService(transaction)
        val (uid1, token1) = userService.createUser(UserValidation("user_test", "password"))
        val (uid2, token2) = userService.createUser(UserValidation("user_test2", "password"))
        gameService.createOrJoinLobby(uid1)
        val gameID = gameService.createOrJoinLobby(uid2) ?: return null

        return TestGameInfo(gameID, uid1, uid2)
    }

    @Test
    fun `define a board layout in a game successfully`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(it)
            val testGameInfo = createGame(it)

            if (testGameInfo == null) {
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

            gameService.defineFleetLayout(testGameInfo.player1, testGameInfo.id, fleet)
            val board1 = gameService.getFleetState(testGameInfo.player1, testGameInfo.id, GameService.Fleet.MY)
            val board2 = gameService.getFleetState(testGameInfo.player1, testGameInfo.id, GameService.Fleet.OPPONENT)

            if (board1 == null || board2 == null) {
                assert(false)
                return@testWithTransactionManagerAndRollback
            }

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

            assertEquals(testGameInfo.player1, board1.userID)
            expectedSquares.forEach { square ->
                assert(square in board1.shipParts)
            }
            assertEquals(testGameInfo.player2, board2.userID)
            assertEquals(board2.shipParts, emptyList<Square>())
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
            assertEquals(stateForPlayer1, Game.State.PLACING_SHIPS)
            assertEquals(stateForPlayer2, Game.State.PLACING_SHIPS)
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
            assertEquals(stateForPlayer1, Game.State.PLAYING)
            assertEquals(stateForPlayer2, Game.State.PLAYING)
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
                    assert(false)
                    return@testWithTransactionManagerAndRollback
                }

                gameService.getGameState(game.id, game.player2 + 1)
            }
        }
    }

    @Test
    fun `whole game test`() {

        testWithTransactionManagerAndRollback {

            val gameService = GameService(it)
            val gameInfo = createGame(it)

            if (gameInfo == null) {
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

            gameService.defineFleetLayout(gameInfo.player1, gameInfo.id, fleet)
            val startingFleet = gameService.getFleetState(gameInfo.player1, gameInfo.id, GameService.Fleet.MY)

            val inBetweenState = gameService.getGameState(gameInfo.id, gameInfo.player1)

            assertEquals(inBetweenState, Game.State.PLACING_SHIPS)

            gameService.defineFleetLayout(gameInfo.player2, gameInfo.id, fleet)

            val stateForPlayer1 = gameService.getGameState(gameInfo.id, gameInfo.player1)
            val stateForPlayer2 = gameService.getGameState(gameInfo.id, gameInfo.player2)

            assertEquals(stateForPlayer1, stateForPlayer2)
            assertEquals(stateForPlayer1, Game.State.PLAYING)

            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(0, 0)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(0, 0)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(2, 1)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(2, 1)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(3, 1)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(3, 2)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(1, 3)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(1, 3)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(1, 4)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(1, 4)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(1, 5)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(1, 5)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(4, 3)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(4, 3)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(4, 4)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(4, 4)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(4, 5)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(4, 5)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(4, 6)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(4, 6)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(1, 8)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(1, 8)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(2, 8)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(2, 8)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(3, 8)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(3, 8)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(4, 8)))
            gameService.makeShots(gameInfo.id, gameInfo.player2, listOf(Square(4, 8)))
            gameService.makeShots(gameInfo.id, gameInfo.player1, listOf(Square(5, 8)))

            val stateForPlayer1AfterGame = gameService.getGameState(gameInfo.id, gameInfo.player1)
            assertEquals(stateForPlayer1AfterGame, Game.State.FINISHED)


            assertEquals(gameService.getFleetState(gameInfo.player2, gameInfo.id, GameService.Fleet.OPPONENT),
                BoardDTO(gameInfo.player1, emptyList(), fleet.flatMap { it.getShipSquares() }, 10)
            )

        }

    }


}