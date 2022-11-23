package pt.isel.daw.battleship.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.repository.dto.toDTO
import pt.isel.daw.battleship.repository.testWithTransactionManagerAndRollback
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.entities.GameStateInfo
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

    private val validRuleFleet = listOf(
        ShipInfo(Square(0, 0), 2, Orientation.Vertical),
        ShipInfo(Square(3, 1), 3, Orientation.Horizontal)
    )

    private fun TransactionFactory.createUser(name: String): AuthInformation {
        val userService = UserService(this)
        return userService.createUser(UserValidation(name, "12346"))
    }

    private fun TransactionFactory.createGame(): TestGameInfo {
        val userService = UserService(this)
        val (uid1, _) = userService.createUser(UserValidation("user_test", "password1"))
        val (uid2, _) = userService.createUser(UserValidation("user_test2", "password1"))

        val gameID = this.execute {
            gamesRepository.persist(
                Game.new(uid1 to uid2, testGameRules).toDTO()
            )
        }

        return TestGameInfo(gameID, uid1, uid2)
    }

    @Test
    fun `define a board layout in a game successfully`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val game = createGame()


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
    fun `define a board layout in a game that doesn't exist`() {
        assertThrows<GameNotFoundException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(this)
                val game = createGame()

                gameService.defineFleetLayout(game.player1, game.id + 1, emptyList())
            }
        }
    }

    @Test
    fun `define a board layout in a game that doesn't belong to the user`() {
        assertThrows<ForbiddenAccessAppException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(this)
                val game = createGame()

                gameService.defineFleetLayout(game.player2 + 1, game.id, emptyList())
            }
        }
    }

    @Test
    fun `Get the state of a game successfully after two players matched`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val game = createGame()


            val stateForPlayer1 = gameService.getGameState(game.id, game.player1)
            val stateForPlayer2 = gameService.getGameState(game.id, game.player2)

            assertEquals(stateForPlayer1, stateForPlayer2)
            val expectedState = GameStateInfo(Game.State.PLACING_SHIPS, null)

            assertEquals(expectedState, stateForPlayer1)
            assertEquals(expectedState, stateForPlayer2)
        }
    }

    @Test
    fun `Get the state of a game sucessfully after two player define their layouts`(){
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val game = createGame()

            gameService.defineFleetLayout(game.player1, game.id, validRuleFleet)
            gameService.defineFleetLayout(game.player2, game.id, validRuleFleet)

            val stateForPlayer1 = gameService.getGameState(game.id, game.player1)
            val stateForPlayer2 = gameService.getGameState(game.id, game.player2)

            assertEquals(stateForPlayer1, stateForPlayer2)
            assertEquals(GameStateInfo(Game.State.PLAYING, null), stateForPlayer1)
        }
    }

    @Test
    fun `Get the state of a game that doesn't exist`() {
        assertThrows<GameNotFoundException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(this)
                val game = createGame()
                gameService.getGameState(game.id + 1, game.player1)
            }
        }
    }

    @Test
    fun `Get the state of a game that doesn't belong to the user`() {
        assertThrows<ForbiddenAccessAppException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(this)
                val game = createGame()

                gameService.getGameState(game.id, game.player2 + 1)
            }
        }
    }

    @Test
    fun `Cant leave the queue if user did not join`() {
        assertThrows<ForbiddenAccessAppException> {
            testWithTransactionManagerAndRollback {
                val gameService = GameService(this)
                val user = createUser("abasdd")

                gameService.leaveLobby(user.uid)
            }
        }
    }

    @Test
    fun `Get into the queue successfully`() {
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)

            val user = createUser("abasdasd")

            val res =gameService.enqueue(user.uid)

            val queue = gameService.getMyLobbyState(user.uid,res.id)

            assertEquals(null, queue.gameID)
        }
    }

    @Test
    fun `Join a game sucessfully with the queue`(){
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)

            val user = createUser("asssss")
            val user2 = createUser("aaaaaa")

            val res =gameService.enqueue(user.uid)
            val res2 =gameService.enqueue(user2.uid)

            val queue = gameService.getMyLobbyState(user.uid,res.id)
            val queue2 = gameService.getMyLobbyState(user2.uid,res2.id)

            assert(queue.gameID != null)
            assert(queue2.gameID != null)

            assertEquals(queue.gameID, queue2.gameID)
        }
    }

    @Test
    fun `Same user joins the queue twice`(){
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)

            val user = createUser("abcdde")

            val res1 =gameService.enqueue(user.uid)
            val res2 =gameService.enqueue(user.uid)
            assert(res1.id != res2.id)
            assert(res1.gameID == null)
            assert(res2.gameID == null)
        }
    }

    @Test
    fun `making a play with an invalid number of shots gives GameRuleViolationException`(){
        testWithTransactionManagerAndRollback {
            val gameService = GameService(this)
            val gameInfo = createGame()

            gameService.defineFleetLayout(gameInfo.player1, gameInfo.id, validRuleFleet)
            gameService.defineFleetLayout(gameInfo.player2, gameInfo.id, validRuleFleet)

            assertThrows<GameRuleViolationException> {
                gameService.makeShots(
                    userID=gameInfo.player1,
                    gameId=gameInfo.id,
                    shots=listOf(Square(0,0), Square(1,0))
                )
            }

        }
    }

    @Test
    fun `whole game test`() {

        testWithTransactionManagerAndRollback {

            val gameService = GameService(this)
            val gameInfo = createGame()

            gameService.defineFleetLayout(gameInfo.player1, gameInfo.id, validRuleFleet)

            val inBetweenState = gameService.getGameState(gameInfo.id, gameInfo.player1)

            assertEquals(GameStateInfo(Game.State.PLACING_SHIPS, null), inBetweenState)

            gameService.defineFleetLayout(gameInfo.player2, gameInfo.id, validRuleFleet)

            val stateForPlayer1 = gameService.getGameState(gameInfo.id, gameInfo.player1)
            val stateForPlayer2 = gameService.getGameState(gameInfo.id, gameInfo.player2)

            assertEquals(GameStateInfo(Game.State.PLAYING, null), stateForPlayer1)
            assertEquals(stateForPlayer1, stateForPlayer2)

            val squaresToHit = validRuleFleet.flatMap { it.getShipSquares() }

            squaresToHit.forEachIndexed { index, square ->
                gameService.makeShots(gameInfo.player1, gameInfo.id, listOf(square))
                if (index != squaresToHit.indices.last) // Player1 ALready ended the game at this point
                    gameService.makeShots(gameInfo.player2, gameInfo.id, listOf(square))
            }

            val stateForPlayer1AfterGame = gameService.getGameState(gameInfo.id, gameInfo.player1)
            assertEquals(GameStateInfo(Game.State.FINISHED, gameInfo.player1), stateForPlayer1AfterGame)

            val board = gameService.getFleetState(gameInfo.player1, gameInfo.id, GameService.Fleet.OPPONENT.toString().lowercase())

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
