package pt.isel.daw.battleship.services

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.domain.board.ShipInfo
import pt.isel.daw.battleship.repository.dto.toDTO
import pt.isel.daw.battleship.repository.jdbi.configure
import pt.isel.daw.battleship.repository.testWithTransactionManagerAndRollback
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.exception.InternalErrorAppException
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.services.validationEntities.UserValidation
import pt.isel.daw.battleship.utils.ID

class GeneralServicesTest {

    private fun TransactionFactory.createUser(name: String): AuthInformation {
        val userService = UserService(this)
        return userService.createUser(UserValidation(name, "12346"))
    }

    private fun TransactionFactory.createGame(uid1: ID, uid2: ID, testRules: GameRules): ID =
        this.execute {
            gamesRepository.persist(
                Game.new(uid1 to uid2, testRules).toDTO()
            )
        }

    private fun TransactionFactory.createFinishedGame(){

        val authInfoPlayer1 = createUser("player1")
        val authInfoPlayer2 = createUser("player2")

        val testRules = GameRules.DEFAULT.copy(
            playTimeout =  9999,
            layoutDefinitionTimeout = 9999,
            shipRules = GameRules.ShipRules("Test", mapOf(1 to 1))
        )

        val gameID = createGame(authInfoPlayer1.uid, authInfoPlayer2.uid, testRules)

        val gameService = GameService(this)

        val shipInfo = ShipInfo(
            initialSquare = Square(0,0),
            size = 1,
            orientation = Orientation.Horizontal
        )

        gameService.defineFleetLayout(authInfoPlayer1.uid, gameID, listOf(shipInfo))
        gameService.defineFleetLayout(authInfoPlayer2.uid, gameID, listOf(shipInfo))

        gameService.makeShots(authInfoPlayer1.uid, gameID, listOf(Square(0,0)))
    }

    @Test
    fun `getSystemInfo returns the correct system info`() {
        testWithTransactionManagerAndRollback {
            val generalService = GeneralService(this)

            val systemInfo = generalService.getSystemInfo()
            assertEquals(systemInfo.authors.sortedBy { it.name }.map { it.name },
                listOf(
                    "Francisco Costa","Teodosie Pienescu","Tiago Filipe"
                )
            )
        }
    }

    @Test
    fun `get statistics returns the correct statistics`() {
        testWithTransactionManagerAndRollback {
            val generalService = GeneralService(this)

            val statistics = generalService.getStatistics()

            assertEquals(statistics.nGames, 0)
            assertEquals(statistics.ranking.size, 0)
        }
    }

    @Test
    fun `get statistics of an user`() {
        testWithTransactionManagerAndRollback {
            val generalService = GeneralService(this)

            createFinishedGame()

            val statistics = generalService.getStatistics()

            assertEquals(statistics.nGames, 1)
            assertEquals(statistics.ranking.size, 2)

            val player1Stats = statistics.ranking[0]
            val player2Stats = statistics.ranking[1]

            assertEquals(player1Stats.rank, 1)
            assertEquals(player2Stats.rank, 2)

            assertEquals(player1Stats.totalGames, 1)
            assertEquals(player2Stats.totalGames, 1)

            assertEquals(player1Stats.wins, 1)
            assertEquals(player2Stats.wins, 0)
        }
    }

    @Test
    fun `minutes to millis conversion is correct`() {
        val millis = minutesToMillis(1)
        assertEquals(millis, 60000)
    }

}