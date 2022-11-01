package pt.isel.daw.battleship.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.repository.dto.toDTO
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


    private val testGameRules = GameRules(
        shotsPerTurn = 1,
        playTimeout = 999999,
        boardSide = 4,
        layoutDefinitionTimeout = 999999,
        shipRules = GameRules.ShipRules(
            name = "TestRules",
            fleetComposition = mapOf(
                2 to 1,
                3 to 1
            )
        )
    )

    private fun createUser(transaction: TransactionFactory, name: String): AuthInformation {
        val userService = UserService(transaction)
        return userService.createUser(UserValidation(name, "12346"))
    }

    private fun createGame(transaction: TransactionFactory): TestGameInfo? {
        val userService = UserService(transaction)
        val (uid1, _) = userService.createUser(UserValidation("user_test", "password1"))
        val (uid2, _) = userService.createUser(UserValidation("user_test2", "password1"))

        val gameID = transaction.execute {
            gamesRepository.persist(
                Game.new(uid1 to uid2, testGameRules).toDTO()
            )
        }

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
                ShipInfo(Square(0, 0), 2, Orientation.Vertical),
                ShipInfo(Square(3, 1), 3, Orientation.Horizontal)
            )

            /*
                B###
                B###
                ####
                ####
             */

            gameService.defineFleetLayout(game.player1, game.id, fleet)
            val myBoard = gameService.getFleetState(game.player1, game.id, GameService.Fleet.MY)
            val opponentsBoard = gameService.getFleetState(game.player1, game.id, GameService.Fleet.OPPONENT)

            val expectedSquares = listOf<Square>(
                Square(0, 0),
                Square(1, 0),
                Square(3, 1),
                Square(3, 2),
                Square(3, 3)
            )

            assertEquals(game.player1, myBoard.userID)
            expectedSquares.forEach { square ->
                assert(square in myBoard.shipParts)
            }
            assertEquals(game.player2, opponentsBoard.userID)
            assertEquals(emptyList<Square>(), opponentsBoard.shipParts)
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
            assertEquals(Game.State.PLACING_SHIPS,stateForPlayer1)
            assertEquals(Game.State.PLACING_SHIPS,stateForPlayer2)
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
            assertEquals(Game.State.PLAYING,stateForPlayer1)
            assertEquals(Game.State.PLAYING,stateForPlayer2 )
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
            val user = createUser(it,"test")

            val joinedGame = gameService.createOrJoinGame(user.uid)
            assertEquals(null,joinedGame)
            gameService.leaveLobby(user.uid)
        }
    }

    @Test
    fun

            `Cant leave the queue if user did not join`() {
        assertThrows<ForbiddenAccessAppException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(it)
                val user = createUser(it, "test")


                gameService.leaveLobby(user.uid)
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

            val inBetweenState = gameService.getGameState(gameInfo.id, gameInfo.player1)

            assertEquals(inBetweenState, Game.State.PLACING_SHIPS)

            gameService.defineFleetLayout(gameInfo.player2, gameInfo.id, fleet)

            val stateForPlayer1 = gameService.getGameState(gameInfo.id, gameInfo.player1)
            val stateForPlayer2 = gameService.getGameState(gameInfo.id, gameInfo.player2)

            assertEquals(stateForPlayer1, Game.State.PLAYING)
            assertEquals(stateForPlayer1, stateForPlayer2)


            val squaresToHit = fleet.flatMap { it.getShipSquares() }

            squaresToHit.forEachIndexed { index, square ->
                gameService.makeShots(gameInfo.player1, gameInfo.id, listOf(square))
                if (index != squaresToHit.indices.last) // Player1 ALready ended the game at this point
                    gameService.makeShots(gameInfo.player2, gameInfo.id, listOf(square))
            }

            val stateForPlayer1AfterGame = gameService.getGameState(gameInfo.id, gameInfo.player1)
            assertEquals(stateForPlayer1AfterGame, Game.State.FINISHED)

            val board = gameService.getFleetState(gameInfo.player1, gameInfo.id, GameService.Fleet.OPPONENT)

            assertEquals(board.userID, gameInfo.player2)
            assertEquals(board.shipParts, emptyList<Square>())
            assertEquals(squaresToHit.size, board.shots.size)
            assertEquals(squaresToHit, board.shots)
        }

    }


}

private fun ShipInfo.getShipSquares(): List<Square> {

    val dRow = if (orientation == Orientation.Vertical) 1 else 0
    val dCol = if (orientation == Orientation.Horizontal) 1 else 0

    return (0 until size).map { i ->
        Square(initialSquare.row.ordinal + (i * dRow), initialSquare.column.ordinal + (i * dCol))
    }
}
