package pt.isel.daw.battleship.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.domain.board.ShipInfo
import pt.isel.daw.battleship.repository.dto.toDTO
import pt.isel.daw.battleship.repository.testWithTransactionManagerAndRollback
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.entities.GameStateInfo
import pt.isel.daw.battleship.services.exception.*
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.services.validationEntities.UserValidation
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID
import java.lang.Thread.sleep

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

    private val timeoutTestGameRules = testGameRules.copy(
        playTimeout = 0,
        layoutDefinitionTimeout = 0
    )

    private val TIMEOUT_TIME = secondsToMillis(1)

    private val validRuleFleet = listOf(
        ShipInfo(Square(0, 0), 2, Orientation.Vertical),
        ShipInfo(Square(3, 1), 3, Orientation.Horizontal)
    )

    private fun TransactionFactory.createUser(name: String): AuthInformation {
        val userService = UserService(this)
        return userService.createUser(UserValidation(name, "12346"))
    }

    private fun TransactionFactory.createGameWith(rules: GameRules): TestGameInfo {
        val userService = UserService(this)
        val (uid1, _) = userService.createUser(UserValidation("user_test", "password1"))
        val (uid2, _) = userService.createUser(UserValidation("user_test2", "password1"))

        val gameID = this.execute {
            gamesRepository.persist(
                Game.new(uid1 to uid2, rules).toDTO()
            )
        }

        return TestGameInfo(gameID, uid1, uid2)
    }

    @Test
    fun `define a board layout in a game successfully`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val game = createGameWith(testGameRules)


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
            val myBoard = gameService.getFleetState(game.player1, game.id, "my")
            val opponentsBoard = gameService.getFleetState(game.player1, game.id, "opponent")

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
            assertEquals(emptyList<Square>(), myBoard.shots)
            assertEquals(emptyList<Square>(), myBoard.hits)
            assertEquals(game.player2, opponentsBoard.userID)
            assertEquals(emptyList<Square>(), opponentsBoard.shipParts)
        }
    }

    @Test
    fun `Defining a board layout after the timeout throws TimeoutExceededAppException`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val game = createGameWith(timeoutTestGameRules)

            sleep(TIMEOUT_TIME)

            assertThrows<TimeoutExceededAppException> {
                gameService.defineFleetLayout(game.player1, game.id, validRuleFleet)
            }
        }
    }

    @Test
    fun `define a board layout in a game that doesn't exist`() {
        assertThrows<GameNotFoundException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(this)
                val game = createGameWith(testGameRules)

                gameService.defineFleetLayout(game.player1, game.id + 1, emptyList())
            }
        }
    }

    @Test
    fun `define a board layout in a game that doesn't belong to the user`() {
        assertThrows<ForbiddenAccessAppException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(this)
                val game = createGameWith(testGameRules)

                gameService.defineFleetLayout(game.player2 + 1, game.id, emptyList())
            }
        }
    }

    @Test
    fun `Get the state of a game successfully after two players matched`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val game = createGameWith(testGameRules)


            val stateForPlayer1 = gameService.getGameState(game.id, game.player1)
            val stateForPlayer2 = gameService.getGameState(game.id, game.player2)

            assertEquals(stateForPlayer1, stateForPlayer2)
            val expectedState = GameStateInfo(Game.State.PLACING_SHIPS, game.player1,game.player1,game.player2)

            assertEquals(expectedState, stateForPlayer1)
            assertEquals(expectedState, stateForPlayer2)
        }
    }

    @Test
    fun `Get the state of a game successfully after two player define their layouts`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val game = createGameWith(testGameRules)

            gameService.defineFleetLayout(game.player1, game.id, validRuleFleet)
            gameService.defineFleetLayout(game.player2, game.id, validRuleFleet)

            val stateForPlayer1 = gameService.getGameState(game.id, game.player1)
            val stateForPlayer2 = gameService.getGameState(game.id, game.player2)

            assertEquals(stateForPlayer1, stateForPlayer2)
            assertEquals(GameStateInfo(Game.State.PLAYING, game.player1,game.player1,game.player2), stateForPlayer1)
        }
    }

    @Test
    fun `Get the state of a game that doesn't exist`() {
        assertThrows<GameNotFoundException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(this)
                val game = createGameWith(testGameRules)
                gameService.getGameState(game.id + 1, game.player1)
            }
        }
    }

    @Test
    fun `Get the state of a game that doesn't belong to the user`() {
        assertThrows<ForbiddenAccessAppException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(this)
                val game = createGameWith(testGameRules)

                gameService.getGameState(game.id, game.player2 + 1)
            }
        }
    }

    @Test
    fun `Get the state of a fleet that isn't either my or opponent`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val game = createGameWith(testGameRules)

            assertThrows<NotFoundAppException> {
                gameService.getFleetState(game.player1, game.id, "INVALID_PARAM")
            }
        }
    }

    @Test
    fun `Get into the queue successfully`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)

            val user = createUser("abasdasd")

            val res = gameService.enqueue(user.uid)

            val queue = gameService.getMyLobbyState(user.uid, res.lobbyID)

            assertEquals(null, queue.gameID)
        }
    }

    @Test
    fun `Join a game successfully with the queue`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)

            val user = createUser("asssss")
            val user2 = createUser("aaaaaa")

            val res = gameService.enqueue(user.uid)
            val res2 = gameService.enqueue(user2.uid)

            val queue = gameService.getMyLobbyState(user.uid, res.lobbyID)
            val queue2 = gameService.getMyLobbyState(user2.uid, res2.lobbyID)

            assert(queue.gameID != null)
            assert(queue2.gameID != null)

            assertEquals(queue.gameID, queue2.gameID)
        }
    }

    @Test
    fun `Same user joins the queue twice`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)

            val user = createUser("abcdde")

            val res1 = gameService.enqueue(user.uid)
            val res2 = gameService.enqueue(user.uid)
            assert(res1.lobbyID != res2.lobbyID)
            assert(res1.gameID == null)
            assert(res2.gameID == null)
        }
    }

    @Test
    fun `making a play with an invalid number of shots gives GameRuleViolationException`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val gameInfo = createGameWith(testGameRules)

            gameService.defineFleetLayout(gameInfo.player1, gameInfo.id, validRuleFleet)
            gameService.defineFleetLayout(gameInfo.player2, gameInfo.id, validRuleFleet)

            assertThrows<GameRuleViolationException> {
                gameService.makeShots(
                    userID = gameInfo.player1,
                    gameId = gameInfo.id,
                    shots = listOf(Square(0, 0), Square(1, 0))
                )
            }

        }
    }

    @Test
    fun `making a play in a game with a user that is not in the game gives ForbiddenAccessAppException`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val gameInfo = createGameWith(testGameRules)
            val (uid3, _) = createUser("player3")

            gameService.defineFleetLayout(gameInfo.player1, gameInfo.id, validRuleFleet)
            gameService.defineFleetLayout(gameInfo.player2, gameInfo.id, validRuleFleet)

            assertThrows<ForbiddenAccessAppException> {
                gameService.makeShots(
                    userID = uid3,
                    gameId = gameInfo.id,
                    shots = listOf(Square(0, 0))
                )
            }

        }
    }

    @Test
    fun `making a play after timeout gives TimeoutExceededAppException`(){
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val gameInfo = createGameWith(
                timeoutTestGameRules.copy(layoutDefinitionTimeout = 9999)
            )

            gameService.defineFleetLayout(gameInfo.player1, gameInfo.id, validRuleFleet)
            gameService.defineFleetLayout(gameInfo.player2, gameInfo.id, validRuleFleet)

            assertThrows<TimeoutExceededAppException> {
                gameService.makeShots(
                    userID = gameInfo.player1,
                    gameId = gameInfo.id,
                    shots = listOf(Square(0, 0))
                )
            }
        }
    }

    @Test
    fun `making a play when it is not the user's turn`(){
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val gameInfo = createGameWith(testGameRules)

            gameService.defineFleetLayout(gameInfo.player1, gameInfo.id, validRuleFleet)
            gameService.defineFleetLayout(gameInfo.player2, gameInfo.id, validRuleFleet)

            assertThrows<ForbiddenAccessAppException> {
                gameService.makeShots(
                    userID = gameInfo.player2,
                    gameId = gameInfo.id,
                    shots = listOf(Square(0, 0))
                )
            }
        }
    }

    @Test
    fun `whole game test`() {

        testWithTransactionManagerAndRollback {

            val gameService = GameService(this)
            val gameInfo = createGameWith(testGameRules)

            gameService.defineFleetLayout(gameInfo.player1, gameInfo.id, validRuleFleet)

            val inBetweenState = gameService.getGameState(gameInfo.id, gameInfo.player1)

            assertEquals(GameStateInfo(Game.State.PLACING_SHIPS, gameInfo.player1,gameInfo.player1,gameInfo.player2), inBetweenState)

            gameService.defineFleetLayout(gameInfo.player2, gameInfo.id, validRuleFleet)

            val stateForPlayer1 = gameService.getGameState(gameInfo.id, gameInfo.player1)
            val stateForPlayer2 = gameService.getGameState(gameInfo.id, gameInfo.player2)

            assertEquals(GameStateInfo(Game.State.PLAYING, gameInfo.player1,gameInfo.player1,gameInfo.player2), stateForPlayer1)
            assertEquals(stateForPlayer1, stateForPlayer2)

            val squaresToHit = validRuleFleet.flatMap { it.getShipSquares() }

            squaresToHit.forEachIndexed { index, square ->
                gameService.makeShots(gameInfo.player1, gameInfo.id, listOf(square))
                if (index != squaresToHit.indices.last) // Player1 ALready ended the game at this point
                    gameService.makeShots(gameInfo.player2, gameInfo.id, listOf(square))
            }

            val stateForPlayer1AfterGame = gameService.getGameState(gameInfo.id, gameInfo.player1)
            assertEquals(GameStateInfo(Game.State.FINISHED, gameInfo.player2 ,gameInfo.player1,gameInfo.player2), stateForPlayer1AfterGame)

            val board = gameService.getFleetState(
                gameInfo.player1,
                gameInfo.id,
                GameService.Fleet.OPPONENT.toString().lowercase()
            )

            assertEquals(board.userID, gameInfo.player2)
            assertEquals(board.shipParts, emptyList<Square>())
            assertEquals(squaresToHit.size, board.hits.size)
            assertEquals(squaresToHit, board.hits)
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

